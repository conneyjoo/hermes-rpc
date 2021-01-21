package com.xhtech.hermes.core.dto;

import com.alibaba.fastjson.JSON;
import com.xhtech.hermes.core.validator.Validator;
import org.springframework.util.StringUtils;

/**
 * Created by Jack on 2017/1/18 0018.
 */
public class MessageRawDto {

    public static final String SEPARATOR = ",";

    private String appId;
    private String subAppId;
    private String clientId;
    private String userId;
    private String pmsId;
    //todo:remove it later
    private String pbsId;
    private String msgType;
    private String subMsgType;
    private String payload;
    private String expireTime;
    private String srcAppId;
    private String srcInstanceId;
    private String state;

    private String offlineMsgFlag = "N";
    private String msgId;
    /**
     * 表示发送消息时， clientId对应设备连上communication server的连接的连接版本号。
     * 主要用来做离线主动拉取(pullMessage)时避免消息队列内的离线消息还未处理，又继续执行pullMessage离线拉取的问题，
     * 对于外部app server的消息发送请求(非pullMessage), 不需要设置该值，只在内部发起pullMessage时设置
     */
    private long pullVersion = 0;

    /** 0:显示角标计数, 1:不显示角标计数 */
    private Integer style = 0;

    public MessageRawDto() {
    }

    public void validateBasic() {
        Validator.assertNotEmptyStr(getSrcAppId(), "srcAppId cannot be empty.");
        Validator.assertNotEmptyStr(getAppId(), "appId cannot be empty.");
        Validator.assertNotEmptyStr(getClientId(), "clientId cannot be empty.");
        Validator.assertNotEmptyStr(getPayload(), "payload cannot be empty.");

        int clientIdNum = StringUtils.countOccurrencesOf(clientId, ",");
        int userIdNum = StringUtils.countOccurrencesOf(userId, ",");

        if (clientIdNum != userIdNum) {
            throw new IllegalArgumentException("clientId and userId of number inconsistency.");
        }

        if (expireTime != null) {
            Validator.assertDateTimeType(expireTime, "invalid expireTime: " + expireTime);
        }
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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
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

    public long getPullVersion() {
        return pullVersion;
    }

    public void setPullVersion(long pullVersion) {
        this.pullVersion = pullVersion;
    }
}
