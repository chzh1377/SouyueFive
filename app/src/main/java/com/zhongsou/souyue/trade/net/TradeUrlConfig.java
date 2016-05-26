package com.zhongsou.souyue.trade.net;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
/**
 * 超级app接口配置页
 * @author Administrator
 *
 */
public class TradeUrlConfig {
    public static String IGID;
    public static String MEGAGAME_SEARCH_KEYWORD = "中华之梦创业大赛"; // 大赛搜索词 正式环境
    public static String MEGAGAME_SEARCH_URL = "http://xmwj.test.zae.zhongsou.com/mobile/search";
    public static String YZD_WEINXIN_SHARE_URL = "http://m.zhongsou.com/Wxshare/guidereg?keyword=%E4%B8%AD%E5%8D%8E%E4%B9%8B%E6%A2%A6%E5%88%9B%E4%B8%9A%E5%A4%A7%E8%B5%9B&srpId=d5ee8a7a0c7bc87f908f8fe731ebf3ff";
    public static String MATCH_SHAREURL_IGID="zgyzd";
    /* end */
    static {

        IGID = MainApplication.getInstance().getResources()
                .getString(R.string.IGID);
    }}
