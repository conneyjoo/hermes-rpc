package com.xhtech.hermes.rpc.dto;

import com.xhtech.hermes.core.netty.annotation.ProtoDefine;
import com.xhtech.hermes.core.netty.proto.StringEx;
import com.xhtech.hermes.rpc.net.proto.CMD;
import com.xhtech.hermes.rpc.net.proto.RPCProto;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

@ProtoDefine
public class PrepareData extends RPCProto {

    private List<AppSecurity> appSecurities = new ArrayList<>();

    @Override
    public byte getCmd() {
        return CMD.PREPARE_DATA_CMD;
    }

    @Override
    public void writeBody(ByteBuf buf) {
        buf.writeInt(appSecurities.size());
        appSecurities.stream().forEach((e) -> e.write(buf));
    }

    @Override
    public void readBody(ByteBuf buf) {
        for (int i = 0, len = buf.readInt(); i < len; i++) {
            appSecurities.add(new AppSecurity(buf));
        }
    }

    @Override
    public int bodyLength() {
        return 4 + appSecurities.stream().mapToInt(AppSecurity::length).sum();
    }

    public void add(String accessKey, String secretKey) {
        appSecurities.add(new AppSecurity(accessKey, secretKey));
    }

    public List<AppSecurity> appSecurities() {
        return appSecurities;
    }

    public class AppSecurity {

        StringEx accessKey;

        StringEx secretKey;

        public AppSecurity(String accessKey, String secretKey) {
            this.accessKey = new StringEx(accessKey);
            this.secretKey = new StringEx(secretKey);
        }

        AppSecurity(ByteBuf buf) {
            read(buf);
        }

        void write(ByteBuf buf) {
            writeString(buf, accessKey);
            writeString(buf, secretKey);
        }

        void read(ByteBuf buf) {
            accessKey = readString(buf);
            secretKey = readString(buf);
        }

        int length() {
            return accessKey.length() + secretKey.length();
        }

        public String getAccessKey() {
            return accessKey.getValue();
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = new StringEx(accessKey);
        }

        public String getSecretKey() {
            return secretKey.getValue();
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = new StringEx(secretKey);
        }
    }
}
