package com.xhtech.hermes.core.monitor;

import com.xhtech.hermes.commons.util.ChargeExecutor;
import com.xhtech.hermes.core.gossip.gms.ApplicationState;
import com.xhtech.hermes.core.gossip.gms.EndpointState;
import com.xhtech.hermes.core.gossip.locator.EndpointSnitch;
import com.xhtech.hermes.core.gossip.net.ServerAddress;
import com.xhtech.hermes.core.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xhtech.hermes.core.cluster.NodeBase.nodeID;

public class Dashboard extends EndpointSnitch {

    private static final Logger logger = LoggerFactory.getLogger(Dashboard.class);

    public static final int ONLINE_TYPE = 0x0;

    public static final int SENDED_TYPE = 0x1;

    public static final int FEEDBACK_TYPE = 0x2;

    private int intBit = 28;

    private int maxInt = (1 << intBit) -1;

    private final ChargeExecutor chargeExecutor;

    private volatile String monitorEndpoint;

    private AtomicInteger onlineCount = new AtomicInteger();

    private AtomicInteger sendReqCount = new AtomicInteger();

    private AtomicInteger sendingCount = new AtomicInteger();

    private AtomicInteger sendedCount = new AtomicInteger();

    private AtomicInteger feedbackCount = new AtomicInteger();

    public Dashboard() {
        this.chargeExecutor = new ChargeExecutor(15000, 128);
    }

    public void online(int i) {
        onlineCount.set(i);
        chargeExecutor.execute(new MetricsTask(value(i, ONLINE_TYPE)));
    }

    public void sendReq(int i) {
        sendReqCount.accumulateAndGet(i, (l, r) -> l + r);
        chargeExecutor.execute(new MetricsTask(value(i, SENDED_TYPE)));
    }

    public void sending(int i) {
        sendingCount.accumulateAndGet(i, (l, r) -> l + r);
        chargeExecutor.execute(new MetricsTask(value(i, SENDED_TYPE)));
    }

    public void sended(int i) {
        sendedCount.accumulateAndGet(i, (l, r) -> l + r);
        chargeExecutor.execute(new MetricsTask(value(i, SENDED_TYPE)));
    }

    public void feedback(int i) {
        feedbackCount.accumulateAndGet(i, (l, r) -> l + r);
        chargeExecutor.execute(new MetricsTask(value(i, FEEDBACK_TYPE)));
    }

    public void clear() {
        onlineCount.set(0);
        sendReqCount.set(0);
        sendingCount.set(0);
        sendedCount.set(0);
        feedbackCount.set(0);
    }

    public int value(int i, int type) {
        return (type << intBit) | (maxInt & i);
    }

    public int getValue(int i) {
        return maxInt & i;
    }

    public int getType(int value) {
        return (value >> intBit);
    }

    @Override
    public void onAlive(ServerAddress endpoint, EndpointState state) {
        super.onAlive(endpoint, state);

        if (state.getApplicationState(ApplicationState.TYPE).isMonitor()) {
            monitorEndpoint = String.format("http://%s:%s", endpoint.getAddress().getHostAddress(), endpoint.getServerPort());
        }
    }

    @Override
    public void onDead(ServerAddress endpoint, EndpointState state) {
        super.onDead(endpoint, state);

        if (state.getApplicationState(ApplicationState.TYPE).isMonitor()) {
            monitorEndpoint = null;
        }
    }

    class MetricsTask extends ChargeExecutor.ChargeTask<Integer> {

        public static final String METRICS_API = "/api/v1/metrics";

        private Integer value;

        public MetricsTask(Integer value) {
            this.value = value;
        }

        @Override
        public void execute(List<Integer> list, boolean filled) {
            reportMetrics(onlineCount.get(), sendReqCount.get(), sendingCount.get(), sendedCount.get(), feedbackCount.get());
        }

        private void reportMetrics(int online, int sendReq, int sending, int sended, int feedback) {
            if (monitorEndpoint != null) {
                StringBuilder uri = new StringBuilder(monitorEndpoint + METRICS_API);
                uri.append("?nodeID=").append(nodeID());
                uri.append("&online=").append(online);
                uri.append("&sendReq=").append(sendReq);
                uri.append("&sending=").append(sending);
                uri.append("&sended=").append(sended);
                uri.append("&feedback=").append(feedback);

                logger.debug(uri.toString());

                try {
                    HttpUtils.get(uri.toString());
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        @Override
        public Integer getValue() {
            return value;
        }
    }
}
