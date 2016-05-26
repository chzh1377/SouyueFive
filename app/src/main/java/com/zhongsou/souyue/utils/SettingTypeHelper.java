package com.zhongsou.souyue.utils;

import java.util.ArrayList;
import java.util.List;




public class SettingTypeHelper {
    public static final List<String> mSettingNames = new ArrayList<String>();
    public static final List<Integer> mSettingTypes=new ArrayList<Integer>();
    
//    <item>""</item>
//    <item>声音</item>
//    <item>震动</item>
//    <item>""</item>
//    <item>帐号绑定</item>
//    <item>消息推送设置</item>
//    <item>消息推送列表</item>
//    <item>""</item>
//    <item>2G/3G网络加载图片</item>
//    <item>正文文字大小</item>
//    <item>清除缓存</item>
//    <item>""</item>
//    <item>评分</item>
//    <item>推荐给好友</item>
//    <item>新版本检测</item>
//    <item>关于我们</item>
//    <item>""</item>
//    <item>退出登录</item>
//    <item>""</item>
    
    
    
//    <integer-array name="setting_types">
//    <item>2</item>
//    <item>0</item>
//    <item>1</item>
//    <item>2</item>
//    <item>3</item>
//    <item>4</item>
//    <item>5</item>
//    <item>2</item>
//    <item>6</item>
//    <item>7</item>
//    <item>8</item>
//    <item>2</item>
//    <item>9</item>
//    <item>10</item>
//    <item>11</item>
//    <item>12</item>
//    <item>2</item>
//    <item>13</item>
//    <item>2</item>
//</integer-array>
    
    static{
        mSettingNames.add("");
        mSettingTypes.add(2);
        mSettingNames.add("弹窗推荐");
        mSettingTypes.add(3);
        mSettingNames.add("");
        mSettingTypes.add(2);
        mSettingNames.add("消息免打扰");
        mSettingTypes.add(4);
        mSettingNames.add("声音");
        mSettingTypes.add(0);
        mSettingNames.add("震动");
        mSettingTypes.add(1);
        mSettingNames.add("");
        mSettingTypes.add(2);
        mSettingNames.add("仅WiFi下加载图片");
        mSettingTypes.add(6);
        mSettingNames.add("字体大小");
        mSettingTypes.add(7);
        mSettingNames.add("清除缓存");
        mSettingTypes.add(8);
        mSettingNames.add("");
        mSettingTypes.add(2);
        
        mSettingNames.add("意见反馈");
        mSettingTypes.add(14);
        mSettingNames.add("评分");
        mSettingTypes.add(9);
//        mSettingNames.add("推荐给好友");
//        mSettingTypes.add(10);
        mSettingNames.add("新版本检测");
        mSettingTypes.add(11);
        mSettingNames.add("新手引导");
        mSettingTypes.add(15);
        mSettingNames.add("关于我们");
        mSettingTypes.add(12);
        mSettingNames.add("");
        mSettingTypes.add(2);
        mSettingNames.add("退出登录");
        mSettingTypes.add(13);
        mSettingNames.add("");
        mSettingTypes.add(2);
    }
    public static List<String> getNamesWithoutLogout() {
        List<String> result =mSettingNames.subList(0, mSettingNames.size()-2);
        return result;
    }
    public static List<Integer> getTypesWithoutLogout() {
        List<Integer> result =mSettingTypes.subList(0, mSettingTypes.size()-2);
        return result;
    }
}