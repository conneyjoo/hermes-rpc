package com.xhtech.hermes.core.gossip.net;

import com.google.common.collect.Lists;
import com.xhtech.hermes.core.gossip.gms.GossipDigestAck2VerbHandler;
import com.xhtech.hermes.core.gossip.gms.GossipDigestAckVerbHandler;
import com.xhtech.hermes.core.gossip.gms.GossipDigestSynVerbHandler;
import com.xhtech.hermes.core.gossip.gms.GossipShutdownVerbHandler;
import com.xhtech.hermes.core.gossip.net.MessageVerb.Verb;
import com.xhtech.hermes.core.gossip.utils.FBUtilities;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOError;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.AsynchronousCloseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/***
 * 代替cassandra中的类
 * @author jydong
 * 2012-6-25
 *
 */
public class MessagingService {

    private static final Logger logger_ = LoggerFactory.getLogger(MessagingService.class);

    /**
     * we preface every mesg with this number so the recipient can validate the sender is sane
     */
    static final int PROTOCOL_MAGIC = 0xCA552DFA;

    private static final Map<MessageVerb.Verb, IVerbHandler> verbHandlers_ = new EnumMap<MessageVerb.Verb, IVerbHandler>(MessageVerb.Verb.class);
    ;

    private final NonBlockingHashMap<InetSocketAddress, OutboundTcpConnectionPool> connectionManagers_ = new NonBlockingHashMap<InetSocketAddress, OutboundTcpConnectionPool>();

    private List<SocketThread> socketThreads = Lists.newArrayList();

    static {
        verbHandlers_.put(Verb.GOSSIP_DIGEST_SYN, new GossipDigestSynVerbHandler());
        verbHandlers_.put(Verb.GOSSIP_DIGEST_ACK, new GossipDigestAckVerbHandler());
        verbHandlers_.put(Verb.GOSSIP_DIGEST_ACK2, new GossipDigestAck2VerbHandler());
        verbHandlers_.put(Verb.GOSSIP_SHUTDOWN, new GossipShutdownVerbHandler());
    }


    public void sendOneWay(Message message, InetSocketAddress to) {
        sendOneWay(message, nextId(), to);
    }

    private static AtomicInteger idGen = new AtomicInteger(0);

    // TODO make these integers to avoid unnecessary int -> string -> int conversions
    private static String nextId() {
        return Integer.toString(idGen.incrementAndGet());
    }


    /**
     * Send a mesg to a given endpoint. This method adheres to the fire and forget
     * style messaging.
     *
     * @param message mesg to be sent.
     * @param to      endpoint to which the mesg needs to be sent
     */
    public void sendOneWay(Message message, String id, InetSocketAddress to) {
        OutboundTcpConnection connection = getConnection(to, message);
        connection.enqueue(message, id);
    }

    public OutboundTcpConnection getConnection(InetSocketAddress to, Message msg) {
        return getConnectionPool(to).getConnection(msg);
    }

    public OutboundTcpConnectionPool getConnectionPool(InetSocketAddress to) {
        OutboundTcpConnectionPool cp = connectionManagers_.get(to);
        if (cp == null) {
            connectionManagers_.putIfAbsent(to, new OutboundTcpConnectionPool(to));
            cp = connectionManagers_.get(to);
        }
        return cp;
    }

    /**
     * called from gossiper when it notices a node is not responding.
     */
    public void convict(InetSocketAddress ep) {
        logger_.debug("Resetting pool for " + ep);
        getConnectionPool(ep).reset();
    }


    public static int messageLength(Header header, String id, byte[] bytes) {
        return 2 + FBUtilities.encodedUTF8Length(id) + header.serializedSize() + 4 + bytes.length;
    }

    public static void validateMagic(int magic) throws IOException {
        if (magic != PROTOCOL_MAGIC) {
            throw new IOException("invalid protocol header");
        }
    }


    public void waitUntilListening() {
    }

    /**
     * This method returns the verb handler associated with the registered
     * verb. If no handler has been registered then null is returned.
     *
     * @param type for which the verb handler is sought
     * @return a reference to IVerbHandler which is the handler for the specified verb
     */
    public static IVerbHandler getVerbHandler(MessageVerb.Verb type) {
        return verbHandlers_.get(type);
    }


    public static void addVerbHandler(MessageVerb.Verb type, IVerbHandler handler) {
        verbHandlers_.put(type, handler);
    }

    /**
     * Listen on the specified port.
     *
     * @param localEp InetAddress whose port to listen on.
     */
    public void listen(InetSocketAddress localEp) throws IOException {
        ServerSocket socket = new ServerSocket(localEp.getPort(), 0, localEp.getAddress()); // 在9001端口监听
        SocketThread th = new SocketThread(socket, "ACCEPT-" + localEp);
        th.start();
        socketThreads.add(th);
    }

    public void shutdownAllConnections() {
        try {
            for (SocketThread th : socketThreads) {
                th.close();   //服务监听端口。立刻执行socket.close(),然后服务线程跳出循环终止.同时把accept的所有socket也关闭，使这些线程也终止。
            }
            for (Iterator<OutboundTcpConnectionPool> iterator = connectionManagers_.values().iterator(); iterator.hasNext(); ) {
                iterator.next().shutdown();   //发送端口。只要线程还存活，则会执行socket.close(),并设法使线程跳出循环终止。
            }
        } catch (IOException e) {
            throw new IOError(e);
        }
    }


    private static class MSHandle {
        public static final MessagingService instance = new MessagingService();
    }

    public static MessagingService instance() {
        return MSHandle.instance;
    }


    private static class SocketThread extends Thread {
        private final ServerSocket server;

        private final List<IncomingTcpConnection> socketList = new ArrayList<IncomingTcpConnection>();

        SocketThread(ServerSocket server, String name) {
            super(name);
            this.server = server;
        }

        public void run() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                logger_.error(e.getMessage(), e);
            }

            while (true) {
                try {
                    Socket socket = server.accept();
                    IncomingTcpConnection incomingTcpConnection = new IncomingTcpConnection(socket);
                    socketList.add(incomingTcpConnection);
                    incomingTcpConnection.start();
                } catch (AsynchronousCloseException e) {
                    // this happens when another thread calls close().
                    logger_.info("MessagingService shutting down server thread.");
                    break;
                } catch (IOException e) {
                    // this happens when another thread calls close().   added by jydong
                    if (server.isClosed()) {
                        logger_.info("server socket has been closed.MessagingService shutting down server thread.");
                        break;
                    } else {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        void close() throws IOException {

            server.close();
            for (IncomingTcpConnection socket : socketList) {
                socket.close();
            }
        }
    }
}
