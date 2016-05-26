package com.zhongsou.souyue.im.interfaceclass;

/**
 * Created by zhangwenbin on 15/7/2.
 *
 * 私聊，群聊，服务号详情页变化影响会话页的接口
 *
 */
public interface DetailChangeInterface {

    /**
     * 消息免打扰开关变化
     */
    void msgNotifyChange(boolean isOpen);
}
