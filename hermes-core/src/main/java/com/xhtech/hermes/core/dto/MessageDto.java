package com.xhtech.hermes.core.dto;

import com.alibaba.fastjson.JSON;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;

/**
 * @author: zjy
 * @date 2019/06/05
 */
public class MessageDto {

    /**
     * 消息唯一id
     */
    private String msgId;

    /**
     * 应用id(教师端:app_tea, 学生端:app_stu)
     */
    private String appId;

    /**
     * 子应用id(应用包名)
     */
    private String subAppId;

    /**
     * 消息已到达的hermes-msg-server
     */
    private String pmsId;

    /**
     * 消息已到达的pn-box-server
     */
    private String pbsId;

    /**
     * 应用消息类型(填:TT)
     */
    private String msgType;

    /**
     * 子应用消息类型(填:app-notification)
     */
    private String subMsgType;

    /**
     * 消息内容
     */
    private String payload;

    /**
     * 消息有效期(ms)(非必填,默认为24小时)
     */
    private Integer expireTime;

    /**
     * 应用id(发起方)
     */
    private String srcAppId;

    /**
     * 应用实例id(发起方)
     */
    private String srcInstanceId;

    /**
     * 消息状态
     */
    private String state;

    /**
     * 是否为离线消息(N.否, Y.是, 默认:N)
     */
    private String offlineMsgFlag = "N";

    /**
     * 0:显示角标计数, 1:不显示角标计数
     */
    private Integer style = 0;

    /**
     * 消息的目的地集合
     */
    private List<Destination> destinations = new LinkedList<>();

    public void validateBasic() {
        Assert.hasText(getSrcAppId(), "srcAppId cannot be empty.");
        Assert.hasText(getSrcAppId(), "srcAppId cannot be empty.");
        Assert.hasText(getAppId(), "appId cannot be empty.");
        Assert.notEmpty(getDestinations(), "destinations cannot be empty.");
        Assert.hasText(getPayload(), "payload cannot be empty.");
    }

    @Override
    public String toString() {
        return super.toString() + "; " + JSON.toJSONString(this);
    }

    public String getSrcAppId() {
        return srcAppId;
    }

    public void setSrcAppId(String srcAppId) {
        this.srcAppId = srcAppId;
    }

    public String getSrcInstanceId() {
        return srcInstanceId;
    }

    public void setSrcInstanceId(String srcInstanceId) {
        this.srcInstanceId = srcInstanceId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSubAppId() {
        return subAppId;
    }

    public void setSubAppId(String subAppId) {
        this.subAppId = subAppId;
    }

    public String getPmsId() {
        return pmsId;
    }

    public void setPmsId(String pmsId) {
        this.pmsId = pmsId;
    }

    public String getPbsId() {
        return pbsId;
    }

    public void setPbsId(String pbsId) {
        this.pbsId = pbsId;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getSubMsgType() {
        return subMsgType;
    }

    public void setSubMsgType(String subMsgType) {
        this.subMsgType = subMsgType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Integer getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Integer expireTime) {
        this.expireTime = expireTime;
    }

    public String getOfflineMsgFlag() {
        return offlineMsgFlag;
    }

    public void setOfflineMsgFlag(String offlineMsgFlag) {
        this.offlineMsgFlag = offlineMsgFlag;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Integer getStyle() {
        return style;
    }

    public void setStyle(Integer style) {
        this.style = style;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<Destination> destinations) {
        this.destinations = destinations;
    }

    public static class Destination {
        private String userId;

        private String clientId;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }
    }
}
