package com.xhtech.hermes.rpc.net.proto;

/**
 * C和M的通信指令集
 * C表示Communication, M表示MessgaeServer
 */
public class CMD {

    /** C到M的连接指令 */
    public static byte CONNECT_CMD = (byte) 0x1;

    /** C到M的存活指令 */
    public static byte KEEPALIVE_CMD = (byte) 0x2;

    /** C转发客户端状态到M的指令 */
    public static byte CLIENT_STATUS_CMD = (byte) 0x10;

    /** C转发设备心跳到M的指令 */
    public static byte DEVICE_HEARTBEAT_FORWARD_CMD = (byte) 0x11;

    /** C转发客户端ECHO到M的指令 */
    public static byte CLIENT_ECHO_CMD = (byte) 0x18;

    /** M到C的推送消息指令 */
    public static byte PUSH_MESSAGE_CMD = (byte) 0x21;

    /** C到M的拉取消息指令 */
    public static byte PULL_MESSAGE_CMD = (byte) 0x22;

    /** C转发客户端消息反馈到M的指令 */
    public static byte FEEDBACK_MESSAGE_CMD = (byte) 0x23;

    /** M到C的预备数据 */
    public static byte PREPARE_DATA_CMD = (byte) 0x25;

    /** 清理数据数据 */
    public static byte CLEAR_DATA_CMD = (byte) 0x26;

    /** C诊断报告指令 */
    public static byte DIAGNOSE_CMD = (byte) 0x30;
}
