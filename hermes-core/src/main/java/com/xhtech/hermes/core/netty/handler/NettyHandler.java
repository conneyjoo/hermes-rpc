package com.xhtech.hermes.core.netty.handler;

import com.xhtech.hermes.core.netty.channel.InterlocutionPipeline;
import com.xhtech.hermes.core.netty.proto.Proto;
import com.xhtech.hermes.core.netty.session.ClientSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.unix.Errors;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@ChannelHandler.Sharable
public class NettyHandler extends SimpleChannelInboundHandler<Proto> {

    private static final Logger logger = LoggerFactory.getLogger(NettyHandler.class);

    public static final AttributeKey<ClientSession> KEY_SESSION = AttributeKey.valueOf("SESSION");

    private Dispatcher dispatcher;

    private SocketEventHandler socketEventHandler;

    public NettyHandler(Dispatcher dispatcher, SocketEventHandler socketEventHandler) {
        this.dispatcher = dispatcher;
        this.socketEventHandler = socketEventHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("{} channelActive", ctx.toString());

        socketEventHandler.accept(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Proto proto) throws Exception {
        try {
            IHandler<Proto> handler = dispatcher.dispatch(proto.getCmd());

            if (handler != null) {

                InterlocutionPipeline pipeline = ctx.channel().attr(InterlocutionPipeline.SERIAL_CHANNEL_PIPELINE_KEY).get();
                if (pipeline != null) {
                    pipeline.answers(proto, ctx.channel());
                }

                handler.process(ctx, proto);
            } else {
                logger.debug("Unkown command cause cannot find handler.");
            }
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            throw t;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();

            if (state == IdleState.READER_IDLE) {
                if (logger.isInfoEnabled()) {
                    ClientSession session = ctx.channel().attr(KEY_SESSION).get();

                    if (session != null) {
                        logger.info("IdleStateEvent triggered: No message received from {} for a long time.", session.getId());
                    } else {
                        logger.info("IdleStateEvent triggered: No message received from communication server for a long time.");
                    }
                }

                close(ctx);
            } else if (state == IdleState.WRITER_IDLE) {
                socketEventHandler.heartbeat(ctx);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info("Inactive: {} - socket: {}", ctx.toString(), " inactive");
        }

        close(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof Errors.NativeIoException) {
            Errors.NativeIoException nativeIoException = (Errors.NativeIoException) cause;

            if (nativeIoException.expectedErr() == Errors.ERRNO_ECONNRESET_NEGATIVE) {
                logger.info("{}: {}", nativeIoException.getMessage(), ctx.toString());
            } else {
                logger.error("Exception: {} - socket: {}", nativeIoException.getMessage(), ctx.toString(), nativeIoException);
            }
        } else {
            logger.error("Exception: {} - socket: {}", cause.getMessage(), ctx.toString(), cause);
        }

        if (cause instanceof IOException) {
            close(ctx);
        }
    }

    public void close(ChannelHandlerContext ctx) {
        ClientSession session = ctx.channel().attr(KEY_SESSION).getAndSet(null);

        socketEventHandler.close(ctx);

        if (session != null) {
            logger.info("Close client connection: {}", session.getId());
            session.close(ctx);
        } else {
            logger.info("Close client connection");
            ctx.close();
        }
    }
}