package com.xhtech.hermes.core.netty.session;

import io.netty.channel.ChannelHandlerContext;

import static com.xhtech.hermes.core.netty.handler.NettyHandler.KEY_SESSION;

public class ClientSessionManager extends SessionManager {

    public ClientSession create(ChannelHandlerContext ctx) {
        ClientSession session = new ClientSession(ctx, this);
        session.setMaxInactiveInterval(getMaxInactiveInterval());
        return session;
    }

    public ClientSession createSession(ChannelHandlerContext ctx, String id) {
        return createSession(ctx, id, null);
    }

    public ClientSession createSession(ChannelHandlerContext ctx, String id, SessionListener sessionListener) {
        ClientSession session;

        if ((session = get(id)) == null) {
            session = create(ctx);
            session.addListener(sessionListener);
            session.setId(id);
        } else {
            session.addContext(ctx);
        }

        ctx.channel().attr(KEY_SESSION).setIfAbsent(session);
        return session;
    }

    @Override
    public ClientSession get(String clientId) {
        return (ClientSession) super.get(clientId);
    }

    public int getActiveCount() {
        return getSessions().stream().mapToInt(session -> ((ClientSession) session).getContexts().size()).sum();
    }
}
