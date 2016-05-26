package com.zhongsou.souyue.countUtils;

import android.content.Context;
import android.util.Log;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.zhongsou.souyue.utils.DeviceInfoUtils;
import com.zhongsou.souyue.utils.SYUserManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UpEventAgent {

    /**
     * 启动，激活用户，后台自己算是不是第一次
     * <p/>
     * {"event":"launch" // 启动，激活用户，后台自己算是不是第一次}
     */
    public static void onLaunch(Context context) {
        SendToServer(new NewsHead(context).getObj(),
                reJSONObject("event", "launch"));
    }

    /**
     * 新增进程统计
     *
     * @param context
     * @param daemon
     */
    public static void onDaemon(Context context, boolean daemon) {
        JSONObject json = new JSONObject();

        try {
            json.put("event", "daemon");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("daemon", daemon);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SendToServer(new NewsHead(context).getObj(), json);
    }

    /**
     * 点击注册按钮
     * <p/>
     * {"event":"reg.click" // 注册点击}
     */
    public static void onRegClick(Context context) {
        SendToServer(new NewsHead(context).getObj(),
                reJSONObject("event", "reg.click"));

        //ZSSDK 统计
        onZSRegClick(context);
    }

    /**
     * 注册成功
     *
     * @param type {"event":"reg","type":"手机|邮箱|短信" //
     *             注册成功，type为注册类型，1：手机，2：邮箱：3：短信...}
     */
    public static void onReg(Context context, String type) {
        JSONObject json = new JSONObject();

        try {
            json.put("event", "reg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SendToServer(new NewsHead(context).getObj(), json);
        onZSreg(context, type);
    }

    /**
     * 登录
     * <p/>
     * "body":{"event":"login//登录}
     */
    public static void onLogin(Context context) {
        SendToServer(new NewsHead(context).getObj(),
                reJSONObject("event", "login"));
        onZSLogin(context);
    }

    /**
     * 付费
     *
     * @param type
     *
     *            {"event":"pay"//付费,"type":"老虎|邮宝|中搜币商城 // 付费成功}
     */
//  public static void onPay(Context context, String type) {
//      JSONObject json = new JSONObject();
//      try {
//          json.put("event", "pay");
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("type", type);
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      SendToServer(new NewsHead(context).getObj(), json);
//  }

    /**
     * 查看
     *
     * @param browser
     * @param channel
     * @param srp
     * @param title
     * @param url     {"event":"news.view","browser":"souyue| ","channel":"头条",
     *                "srp:"暴力事件","title":"标题","url":"新闻地址"}//查看
     */
    public static void onNewsView(Context context, String browser,
                                  String channel, String srp, String srpId, String title, String url) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "news.view");
        } catch (JSONException e) {

            e.printStackTrace();
        }
        try {
            json.put("browser", browser);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        try {
            json.put("channel", channel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("srp", srp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("srpid", srpId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("title", title);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if (url != null) {
                json.put("url", url.replaceAll("#.*$", ""));
            } else {
                json.put("url", " ");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);
    }

    /**
     * 分享
     *
     * @param channel
     * @param srp
     * @param title
     * @param url     {"event":"news.share","channel":"","srp":"暴力事件","title":"标题",
     *                "url":"新闻地址"}//分享
     */
    public static void onNewsShare(Context context, String channel, String srp,
                                   String srpId, String title, String url, String dest) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "news.share");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("channel", channel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("srp", srp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("srpid", srpId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("title", title);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        try {
            if (url != null) {
                json.put("url", url.replaceAll("#.*$", ""));
            } else {
                json.put("url", " ");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("dest", dest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);
        //ZSSDK 统计
        onZSNewsShare(context, srpId, url, dest);
    }

    /**
     * 顶
     *
     * @param channel
     * @param srp
     * @param title
     * @param url     {"event":"news.up","channel":"","srp":"暴力事件","title":"标题",
     *                "url":"新闻地址"}//顶
     */
    public static void onNewsUp(Context context, String channel, String srp,
                                String srpId, String title, String url) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "news.up");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("channel", channel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("srp", srp);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        try {
            json.put("srpid", srpId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("title", title);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if (url != null) {
                json.put("url", url.replaceAll("#.*$", ""));
            } else {
                json.put("url", " ");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);
        onZSNewsUp(context, srpId, url);    //ZSSDK
    }

    /**
     * 评论
     *
     * @param channel
     * @param srp
     * @param title
     * @param url     {"event":"news.comment","channel":"","srp":"暴力事件","title":"标题"
     *                ,"url":"新闻地址"}//评论
     *                ####################################################################评论无channel
     */
    public static void onNewsComment(Context context, String channel,
                                     String srp, String srpId, String title, String url) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "news.comment");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("channel", channel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("srp", srp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("srpid", srpId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("title", title);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if (url != null) {
//                String str = "";
//                try {//由于服务器给返回来的URL是不一样的，所以这里根据服务器规则拼接评论的URL使其与查看新闻时相同
//                str = "http://m.zhongsou.com/share/index?"+"keyword="+URLEncoder.encode(srp,"UTF-8")+"&"+"srpId="+srpId+"&"+"userid="+AppInfoUtils.getUid()+"&"+"url="+URLEncoder.encode(url,"UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
                json.put("url", url.replaceAll("#.*$", ""));
            } else {
                json.put("url", " ");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);
        onZSNewsComment(context, srpId, url);   //ZSSDK
    }

    /**
     * 订阅大全
     */
    public static void onSuberAll(Context context) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "click");
            JSONObject info = new JSONObject();
            info.put("object_id", "subscribepage.daquan");
            json.put("info", info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);

        //ZSSDK 统计
        onZSSuberAll(context);
    }


    /**
     * 推送
     *
     * @param channel
     * @param srp
     * @param title
     * @param url
     *
     *            {"event":"news.push","channel":"","srp":"暴力事件","title":"标题",
     *            "url":"新闻地址"}//推送
     */
//  public static void onNewsPush(Context context, String channel, String srp,
//          String srpId, String title, String url) {
//      JSONObject json = new JSONObject();
//      try {
//          json.put("event", "news.push");
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("channel", channel);
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("srp", srp);
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("srpid", srpId);
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("title", title);
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("url", url.replaceAll("#.*$",""));
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      SendToServer(new NewsHead(context).getObj(), json);
//  }

    /**
     * 推送到达
     *
     * @param channel
     * @param srp
     * @param title
     * @param url
     *
     *            {"event":"news.arrive","channel":"","srp":"暴力事件","title":"标题",
     *            "url":"新闻地址"}//推送到达
     *
     */
//  public static void onNewsArrive(Context context, String channel,
//          String srp, String srpId, String title, String url) {
//      JSONObject json = new JSONObject();
//      try {
//          json.put("event", "news.arrive");
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("channel", channel);
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("srp", srp);
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("srpid", srpId);
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("title", title);
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("url", url.replaceAll("#.*$",""));
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      SendToServer(new NewsHead(context).getObj(), json);
//  }

    /**
     * 在推送中打开
     * <p/>
     * <p/>
     * {"event":"news.push.view","channel":"","srp":"暴力事件","title":
     * "标题","url":"新闻地址"}//在推送中打开
     * 在4.2.3中修改
     * "body":{"event":"news.push.view","newspushid":"id"}//在推送中打开
     */
    public static void onNewsPushView(Context context, String newspushid, String position) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "news.push.view");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("newspushid", newspushid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("position", position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);
        onZSNewsPushView(context, newspushid, position);
    }

    /**
     * 收藏
     *
     * @param channel
     * @param srp
     * @param title
     * @param url     {"event":"news.favorite","channel":"","srp":"暴力事件","title":
     *                "标题","url":"新闻地址"}//收藏 分类未知???????
     */

    public static void onNewsFavorite(Context context, String channel,
                                      String srp, String srpId, String title, String url) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "news.favorite");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("channel", channel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("srp", srp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("srpid", srpId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("title", title);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if (url != null) {
                json.put("url", url.replaceAll("#.*$", ""));
            } else {
                json.put("url", " ");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);
        onZSNewsFavorite(context, srpId, url);
    }

    /**
     * 查看
     *
     * @param browser
     * @param srp
     *
     *            {"event":"srp.view","browser":"souyue| ","srp:"暴力事件"}//查看
     */

//  public static void onSrpView(Context context, String browser, String srp,
//          String srpId) {
//      JSONObject json = new JSONObject();
//      try {
//          json.put("event", "srp.view");
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("browser", browser);
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("srp", srp);
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("srpid", srpId);
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      SendToServer(new NewsHead(context).getObj(), json);
//  }

    /**
     * 搜索
     *
     * @param srp {"event":"srp.search","srp":"暴力事件"}//搜索
     */
    public static void onSrpSearch(Context context, String srp, String srpId) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "srp.search");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("srp", srp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("srpid", srpId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);
        onZSSrpSearch(context, srpId);
    }

    /**
     * 分享
     *
     * @param srp {"event":"srp.share","srp":"暴力事件"}//分享
     */

    public static void onSrpShare(Context context, String srp, String srpId, String dest) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "srp.share");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("srp", srp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("srpid", srpId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("dest", dest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);

        //ZSSDK 统计
        onZSSrpShare(context, srpId, dest);
    }

    /**
     * 点赞
     *
     * @param srp
     *
     *            {"event":"srp.up","srp":"暴力事件"}//点赞
     */
//  public static void onSrpUp(Context context, String srp, String srpId) {
//      JSONObject json = new JSONObject();
//      try {
//          json.put("event", "srp.up");
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("srp", srp);
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("srpid", srpId);
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      SendToServer(new NewsHead(context).getObj(), json);
//  }

    /**
     * 评论
     *
     * @param srp
     *
     *            {"event":"srp.comment","srp":"暴力事件"}//评论
     */
//  public static void onSrpComment(Context context, String srp, String srpId) {
//      JSONObject json = new JSONObject();
//      try {
//          json.put("event", "srp.comment");
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("srp", srp);
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("srpid", srpId);
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      SendToServer(new NewsHead(context).getObj(), json);
//  }

    /**
     * 推送
     *
     * @param srp
     *
     *            {"event":"srp.push","srp":"暴力事件"}//推送
     */
//  public static void onSrpPush(Context context, String srp, String srpId) {
//      JSONObject json = new JSONObject();
//      try {
//          json.put("event", "srp.push");
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("srp", srp);
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("srpid", srpId);
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      SendToServer(new NewsHead(context).getObj(), json);
//  }

    /**
     * 收藏
     *
     * @param srp
     *
     *            {"event":"srp.favorite","srp":"暴力事件"}//收藏
     */
//  public static void onSrpFavorite(Context context, String srp, String srpId) {
//      JSONObject json = new JSONObject();
//      try {
//          json.put("event", "srp.favorite");
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      try {
//          json.put("srp", srp);
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//
//      try {
//          json.put("srpid", srpId);
//      } catch (JSONException e) {
//          e.printStackTrace();
//      }
//      SendToServer(new NewsHead(context).getObj(), json);
//  }

    /**
     * 订阅SRP
     *
     * @param srp {"event":"srp.subscribe","srp":"暴力事件" //订阅SRP
     */
    public static void onSrpSubscribe(Context context, String srp, String srpId) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "srp.subscribe");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("srp", srp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("srpid", srpId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);
    }

    /**
     * 取消订阅
     *
     * @param srp {"event":"srp.unsubscribe","srp":"暴力事件"}//取消订阅
     */
    public static void onSrpUnsubscribe(Context context, String srp,
                                        String srpId) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "srp.unsubscribe");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("srp", srp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("srpid", srpId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);
//        onZSSrpUnsubscribe(context, srpId);
    }

    /**
     * 圈吧访问事件
     *
     * @param context
     * @param group
     * @param groupAD "body":{"event":"group.access","group":"id.圈吧名","groupAD":"圈主名"//圈吧访问}
     */
    public static void onGroupAccess(Context context, String group, String groupAD) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "group.access");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            json.put("group", group);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            json.put("groupAD", groupAD);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SendToServer(new NewsHead(context).getObj(), json);

        //ZSSDK 统计
        onZSGroupAccess(context, group);
    }

    /**
     * 圈贴访问
     *
     * @param browser
     * @param group
     * @param groupAD
     * @param title
     * @param docid   {"event":"group.view","browser":"souyue|","group":"id.圈吧名",
     *                "groupAD":"圈主名","title":"标题",docid":"帖子id" //圈贴访问}
     */
    public static void onGroupView(Context context, String browser,
                                   String group, String groupAD, String title, String docid) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "group.view");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("browser", browser);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("group", group);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("groupAD", groupAD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("title", title);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("docid", docid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);
//        onZSGroupView(context, group, title, docid, channel);   //未在此处调用，直接在代码中添加
    }

    /**
     * 圈贴原创
     *
     * @param group
     * @param groupAD
     * @param title
     * @param docid   {"event":"group.publish","group":"id.圈吧名","groupAD":"圈主名",
     *                "title":"标题",docid":"帖子id" //圈贴原创}
     */
    public static void onGroupPublish(Context context, String group,
                                      String groupAD, String title, String docid) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "group.publish");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("group", group);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("groupAD", groupAD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("title", title);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("docid", docid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);
        onZSGroupPublish(context, group, groupAD, title, docid);
    }

    /**
     * 圈贴跟帖
     *
     * @param group
     * @param groupAD
     * @param docid   {"event":"group.comment","group":"id.圈吧名","groupAD":"圈主名",
     *                "docid":"帖子id" //圈贴跟帖}
     */
    public static void onGroupComment(Context context, String group,
                                      String groupAD, String docid) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "group.comment");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("group", group);
        } catch (JSONException e) {

            e.printStackTrace();
        }
        try {
            json.put("groupAD", groupAD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("docid", docid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);
        onZSGroupComment(context, group, docid);
    }

    /**
     * 圈贴回复
     *
     * @param group
     * @param groupAD
     * @param docid   {"event":"group.reply","group":"id.圈吧名","groupAD":"圈主名",
     *                "docid":"帖子id" //圈贴回复}
     */
    public static void onGroupReply(Context context, String group,
                                    String groupAD, String docid) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "group.reply");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("group", group);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("groupAD", groupAD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("docid", docid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);
        onZSGroupReply(context, group, docid);
    }

    /**
     * 圈贴点赞
     *
     * @param group
     * @param groupAD
     * @param docid   {"event":"group.up","group":"id.圈吧名","groupAD":"圈主名","docid":
     *                "帖子id" //圈贴点赞}
     */

    public static void onGroupUp(Context context, String group, String groupAD,
                                 String docid) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "group.up");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("group", group);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("groupAD", groupAD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("docid", docid);
        } catch (JSONException e) {

            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);
        onZSGroupUp(context, group, docid);
    }

    /**
     * 圈贴收藏
     *
     * @param group
     * @param groupAD
     * @param docid   {"event":"group.favorite","group":"id.圈吧名","groupAD":"圈主名",
     *                "docid":"帖子id" //圈贴收藏}
     */
    public static void onGroupFavorite(Context context, String group,
                                       String groupAD, String docid) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "group.favorite");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("group", group);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("groupAD", groupAD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("docid", docid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);
        onZSGroupFavorite(context, group, docid);
    }

    /**
     * 圈贴分享
     *
     * @param group
     * @param groupAD
     * @param docid
     * @param dest    {"event":"group.share","group":"id.圈吧名","groupAD":"圈主名",
     *                "docid":"帖子id" //圈贴分享}
     */
    public static void onGroupShare(Context context, String group,
                                    String groupAD, String docid, String dest) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "group.share");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("group", group);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("groupAD", groupAD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("docid", docid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("dest", dest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);

        //ZSSDK 统计
        onZSGroupShare(context, group, docid, dest);
    }

    /**
     * 加入圈子
     *
     * @param group
     * @param groupAD {"event":"group.join","group":"id.圈吧名","groupAD":"圈主名" //加入圈子}
     */

    public static void onGroupJoin(Context context, String group, String groupAD) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "group.join");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("group", group);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("groupAD", groupAD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);
    }

    /**
     * 退出圈子
     *
     * @param group
     * @param groupAD {"event":"group.quit","group":"id.圈吧名","groupAD":"圈主名" //退出圈子}
     */
    public static void onGroupQuit(Context context, String group, String groupAD) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "group.quit");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("group", group);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("groupAD", groupAD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);
//        onZSGroupQuit(context, group);
    }


    /**
     * 搜悦统计移动统计系统  新闻微件访问日志
     *
     * @param widget_type
     * @param widget
     * @param group_srp   {"event":"widget.view","widget_type":"微件类型id.微件类型名称","widget":"微件id.微件名称","group_srp":"id.名称" //微件访问}
     */
    public static void onWidgetView(Context context, String widget_type, String widget, String group_srp) {
        JSONObject json = new JSONObject();
        //处理widget  后台只想要标准32位的wedgit  故处理
//      widget = "nihao-abcdefghijklmnopqrstuvwxyz-abcde";
//      if(widget!=null&&!widget.equals("")&&widget.getBytes().length>32){
//          int length = widget.length();
//          widget = widget.substring(length-32,length);
//      }
        try {
            json.put("event", "widget.view");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("widget_type", widget_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("widget", widget);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("group_srp", group_srp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);

        //ZSSDK 统计
        onZSWidgetView(context, widget_type, widget, group_srp);
    }

    /**
     * 首页点击订阅
     * "body":{"event":"click","info":{"object_id":"id"}}
     * id=homepage.subscribe
     */
    public static void onHomeSubscribe(Context context) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "click");
            JSONObject info = new JSONObject();
            info.put("object_id", "homepage.subscribe");
            json.put("info", info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);
        onZSHomeSubscribe(context);
    }

    /**
     * 首页点击网址导航
     * "body":{"event":"click","info":{"object_id":"id"}}
     * id=homepage.website
     */
    public static void onHomeWebsite(Context context) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "click");
            JSONObject info = new JSONObject();
            info.put("object_id", "homepage.website");
            json.put("info", info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);

        //ZSSDK 统计
        onZSHomeWebsite(context);
    }

    /**
     * 首页点击搜索
     * "body":{"event":"click","info":{"object_id":"id"}}
     * id=homepage.search
     */
    public static void onHomeSearch(Context context) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "click");
            JSONObject info = new JSONObject();
            info.put("object_id", "homepage.search");
            json.put("info", info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);

        //ZSSDK 统计
        onZSHomeSearch(context);
    }

    /**
     * 详情页点击二级导航
     * "body":{"event":"click","info":{"object_id":"id"}}
     * id=detailpage.widget
     */
    public static void onDetailWidget(Context context) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "click");
            JSONObject info = new JSONObject();
            info.put("object_id", "detailpage.widget");
            json.put("info", info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);

        //ZSSDK 统计
        onZSDetailWidget(context);
    }

    /**
     * 首页中心导航
     *
     * @param context
     */
    public static void onHomePageMiddleBall(Context context) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "click");
            JSONObject info = new JSONObject();
            info.put("object_id", "homepage.midleball");
            json.put("info", info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);

        //ZSSDK 统计
        onZSHomePageMiddleBall(context);
    }

    /**
     * 首页非中心导航
     *
     * @param context
     */
    public static void onHomePageInMiddleBall(Context context) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "click");
            JSONObject info = new JSONObject();
            info.put("object_id", "homepage.inmidleball");
            json.put("info", info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);

        //ZSSDK 统计
        onZSHomePageInMiddleBall(context);
    }

    /**
     * 进入详情按钮
     *
     * @param context
     */
    public static void onHomePageEnterButton(Context context) {
        JSONObject json = new JSONObject();
        try {
            json.put("event", "click");
            JSONObject info = new JSONObject();
            info.put("object_id", "enterbutton");
            json.put("info", info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendToServer(new NewsHead(context).getObj(), json);

        //ZSSDK 统计
        onZSHomePageEnterButton(context);
    }


    /**
     * 根据头部和body的json对象拼接最终json对象并上传服务器
     *
     * @param jsonHead 头json
     * @param jsonBody body json
     */
    public static void SendToServer(JSONObject jsonHead, JSONObject jsonBody) {
        JSONObject json = new JSONObject();
        try {
            json.put("head", jsonHead);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("body", jsonBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 上传数据到服务器
        String str = json.toString();
        new UpDataThread().send(new MyCallBack(), json.toString());
    }

    /**
     * 根据传入的key和Value来返回一个JSONObject对象
     *
     * @param key   json的键
     * @param value json的值
     * @return 返回的json对象
     */
    private static JSONObject reJSONObject(String key, String value) {
        JSONObject json = new JSONObject();
        try {
            json.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    // /**
    // * 根据传入的map组合json对象并返回
    // * @param map map来源
    // * @return 返回json对象
    // */
    // private static JSONObject reJSONObject(HashMap<jsoning,String> map){
    // JSONObject json=new JSONObject();
    // Iterator<Entry<String, String>> iter = map.entrySet().iterator();
    // while (iter.hasNext()) {
    // Entry<String,String> entry=iter.next();
    // try {
    // json.put(entry.getKey(), entry.getValue());
    // } catch (JSONException e) {
    //
    // e.printStackTrace();
    // }
    // }
    // return json;
    // }

    public static class MyCallBack implements HttpCallBack {

        @Override
        public void onSuccess(String str) {
            Log.i("--->onSuccess", str);
        }

        @Override
        public void onTaskError(String str) {
            System.out.println("任务异常");
            Log.i("--->TaskError", str);
        }

        @Override
        public void onNetError() {
            System.out.println("网络异常");
            Log.i("--->onNetError", "2222");
        }

        @Override
        public void onTimeOut() {
            System.out.println("请求超时");
            Log.i("--->onTimeOut", "2222");
        }

    }

    /**
     * url转换方法
     *
     * @param url url
     * @return 返回新的url
     */
    private static String parseUrl(String url) {
        String ret = null;
        if (url != null) {
            ret = url.replaceAll("#.*$", "");
        } else {
            ret = " ";
        }
        return ret;
    }

    //-------------- 统计 ZSSDK 相关方法 -----

    /**
     * ZSSDK 注册点击 统计
     *
     * @param context 上下文
     */
    public static void onZSRegClick(Context context) {
        String eventId = "reg.click";
        ZSSdkUtil.onEvent(context, eventId, null);
    }

    /**
     * ZSSDK 新闻分享 统计
     *
     * @param context 上下文
     * @param srpId
     * @param url
     * @param dest
     */
    public static void onZSNewsShare(Context context,
                                     String srpId,
                                     String url,
                                     String dest) {
        String eventId = "news.share";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("srpId", srpId);
        map.put("url", parseUrl(url));
        map.put("dest", dest);

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK SRP分享 统计
     *
     * @param context 上下文
     * @param srpId
     * @param dest
     */
    public static void onZSSrpShare(Context context,
                                    String srpId,
                                    String dest) {
        String eventId = "srp.share";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("srpId", srpId);
        map.put("dest", dest);

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 圈吧访问 统计
     *
     * @param context 上下文
     * @param group
     */
    public static void onZSGroupAccess(Context context, String group) {
        String eventId = "group.access";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("group", group);

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 圈帖分享 统计
     *
     * @param context 上下文
     * @param group
     * @param docid
     * @param dest
     */
    public static void onZSGroupShare(Context context,
                                      String group,
                                      String docid,
                                      String dest) {
        String eventId = "group.share";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("group", group);
        map.put("docid", docid);
        map.put("dest", dest);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 微件访问 统计
     *
     * @param context     上下文
     * @param widget_type
     * @param widget
     * @param group_srp
     */
    public static void onZSWidgetView(Context context, String widget_type, String widget, String group_srp) {
        String eventId = "widget.view";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("widget_type", widget_type);
        map.put("widget", widget);
        map.put("group_srp", group_srp);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 首页网址按钮 统计
     *
     * @param context 上下文
     */
    public static void onZSHomeWebsite(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "homepage.website");

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 首页搜索按钮 统计
     *
     * @param context 上下文
     */
    public static void onZSHomeSearch(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "homepage.search");

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 首页订阅按钮 统计
     *
     * @param context 上下文
     */
    public static void onZSHomeSubscribe(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "homepage.subscribe");

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 详情页二级导航 统计
     *
     * @param context 上下文
     */
    public static void onZSDetailWidget(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "detailpage.widget");

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 首页中心导航
     *
     * @param context 上下文
     */
    public static void onZSHomePageMiddleBall(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "homepage.midleball");

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 首页非中心导航
     *
     * @param context 上下文
     */
    public static void onZSHomePageInMiddleBall(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "homepage.inmidleball");

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 进入详情按钮
     *
     * @param context 上下文
     */
    public static void onZSHomePageEnterButton(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "enterbutton");

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 订阅大全按钮点击 统计
     *
     * @param context
     */
    public static void onZSSuberAll(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "subscribepage.daquan");

        ZSSdkUtil.onEvent(context, eventId, map);
    }


    /**
     * srp页内srp点击次数
     *
     * @param context
     */
    public static void onZSSrpClick(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "srpdetail.srp.click");

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * srp页内搜索按钮点击次数
     *
     * @param context
     */
    public static void onZSSearchClick(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "srpdetail.search.click");

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * srp页内菜单点击次数
     *
     * @param context
     */
    public static void onZSMenuClick(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "srpdetail.menu.click");

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * srp页内滑动页面次数
     *
     * @param context
     */
    public static void onZSNavigationbarSlide(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "srpdetail.navigationbar.slide");

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * srp页内点击导航栏次数
     *
     * @param context
     */
    public static void onZSNavigationbarClick(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "srpdetail.navigationbar.click");

        ZSSdkUtil.onEvent(context, eventId, map);
    }


    /**
     * srp页内菜单内编辑按钮点击
     *
     * @param context
     */
    public static void onZSMenuEdit(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "srpdetail.menu.edit");

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * srp页内菜单内报错按钮点击
     *
     * @param context
     */
    public static void onZSMenuReporterror(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "srpdetail.menu.reporterror");

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * srp页内菜单内二维码按钮点击
     *
     * @param context
     */
    public static void onZSMenuQrcode(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "srpdetail.menu.qrcode");

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * srp页内菜单内分享按钮点击
     *
     * @param context
     */
    public static void onZSMenuShare(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "srpdetail.menu.share");

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * srp页内菜单内合作经营按钮点击
     *
     * @param context
     */
    public static void onZSMenuCooperation(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "srpdetail.menu.cooperation");

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * srp页内菜单内快捷方式按钮点击
     *
     * @param context
     */
    public static void onZSMenuShortcut(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "srpdetail.menu.shortcut");

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * srp页内菜单内订阅按钮点击
     *
     * @param context
     */
    public static void onZSMenuSubscribe(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "srpdetail.menu.subscribe");

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 专题分享 各种方式统计 统计
     *
     * @param context 上下文
     * @param dest    分享目标
     * @param srpId   srp词的id
     * @param url     路径
     */
    public static void onZSTopicShare(Context context, String dest, String srpId, String url) {
        String eventId = "topic.share";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("dest", dest);
        map.put("srpid", srpId);
        map.put("url", url);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 专题分享按钮点击事件 统计
     *
     * @param context 上下文
     */
    public static void onZSZhuantiShare(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "zhuanti.share");
        ZSSdkUtil.onEvent(context, eventId, map);
    }


    /**
     * ZSSDK 首页球长按 统计
     *
     * @param context 上下文
     */
    public static void onZSHomePagerLongClick(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "homepage.pressball");

        ZSSdkUtil.onEvent(context, eventId, map);
    }


    /**
     * ZSSDK 要闻轮播图滑动响应次数 统计
     *
     * @param context 上下文
     * @param channel 频道
     */
    public static void onZSYaoWenImageSlide(Context context, String channel) {
        String eventId = "yaowenchanel.image.slide";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("channel", channel);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 要闻轮播分页点击次数 统计
     *
     * @param context 上下文
     * @param image   图片索引
     * @param channel 频道
     */
    public static void onZSYaoWenImageClick(Context context, String image, String channel) {
        String eventId = "yaowenchanel.image.click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("image", image);
        map.put("channel", channel);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

//    /**
//     * ZSSDK 新闻图集内图片pv 统计
//     * @param context 上下文
//     * @param url 路径
//     * @param channel 频道
//     * @param image 图片索引
//     */
//    public static void onZSYaoWenTujiClick(Context context, String url, String channel,String image) {
//        String eventId = "yaowen.tuji.click";
//        HashMap<String,String> map = new HashMap<String,String>();
//        map.put("url",url);
//        if (StringUtils.isEmpty(channel)) {
//            channel = "其它";
//        }
//        map.put("channel",channel);
//        map.put("image", image);
//        ZSSdkUtil.onEvent(context, eventId, map);
//    }

    /**
     * ZSSDK 推荐图集浏览次数 统计
     *
     * @param context 上下文
     * @param url     图集url
     */
    public static void onZSRecommendImageView(Context context, String srpid, String url) {
        String eventId = "recommendimage.view";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("srpid", srpid);
        map.put("url", url);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 详情页面红包分享到微信好友 统计
     *
     * @param context 上下文
     */
    public static void onZSDetailWXShare(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "detailpage.wxshare");
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 详情页面红包分享到微信朋友圈 统计
     *
     * @param context 上下文
     */
    public static void onZSDetailFriendShare(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "detailpage.friendshare");
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 详情页微信红包被领取按钮被点击
     *
     * @param context 上下文
     */
    public static void onZSDetailMoney(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "detailpage.prizeget");
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    //--------- 5.1.0 start ---------

    /**
     * ZSSDK 新闻查看 统计
     *
     * @param context 上下文
     * @param channel
     * @param srpid
     * @param title
     * @param url
     * @param type    type：图集、段子、gif、空；
     * @param image   image：1、2、3、4、5、6…；如果type不是图集，则默认传0
     */
    public static void onZSNewsView(Context context, String channel,
                                    String srpid, String title,
                                    String url, String type, String image) {
        String eventId = "news.view";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("channel", channel);
        map.put("srpid", srpid);
        map.put("title", title);
        map.put("url", parseUrl(url));
        map.put("type", type);
        map.put("image", image);

        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 新闻顶 统计
     *
     * @param context 上下文
     * @param srpid
     * @param url
     */
    public static void onZSNewsUp(Context context, String srpid,
                                  String url) {
        String eventId = "news.up";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("srpid", srpid);
        map.put("url", parseUrl(url));
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 新闻评论 统计
     *
     * @param context 上下文
     * @param srpid
     * @param url
     */
    public static void onZSNewsComment(Context context, String srpid,
                                       String url) {
        String eventId = "news.comment";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("srpid", srpid);
        map.put("url", parseUrl(url));
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 新闻收藏 统计
     *
     * @param context 上下文
     * @param srpid
     * @param url
     */
    public static void onZSNewsFavorite(Context context, String srpid,
                                        String url) {
        String eventId = "news.favorite";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("srpid", srpid);
        map.put("url", parseUrl(url));
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 推送中打开 统计
     *
     * @param context    上下文
     * @param newspushid
     * @param position   notificationbar|msglist
     */
    public static void onZSNewsPushView(Context context, String newspushid,
                                        String position) {
        String eventId = "news.push.view";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("newspushid", newspushid);
        map.put("position", position);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK Srp搜索 统计
     *
     * @param context
     * @param srpid
     */
    public static void onZSSrpSearch(Context context, String srpid) {
        String eventId = "srp.search";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("srpid", srpid);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 取消订阅 统计
     *
     * @param context
     * @param srpid
     */
    public static void onZSSrpUnsubscribe(Context context, String srpid) {
        String eventId = "srp.unsubscribe";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("srpid", srpid);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK  圈贴访问  统计
     *
     * @param context
     * @param group
     * @param title
     * @param docid
     * @param channel Channel：头条、历史、空"
     */
    public static void onZSGroupView(Context context, String group, String title, String docid, String channel) {
        String eventId = "group.view";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("group", group);
        map.put("title", title);
        map.put("docid", docid);
        map.put("channel", channel);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 圈贴跟帖 统计
     *
     * @param context
     * @param group
     * @param docid
     */
    public static void onZSGroupPublish(Context context, String group, String groupAD, String title, String docid) {
        String eventId = "group.publish";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("group", group);
        map.put("groupAD", groupAD);
        map.put("title", title);
        map.put("docid", docid);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 圈贴跟帖 统计
     *
     * @param context
     * @param group
     * @param docid
     */
    public static void onZSGroupComment(Context context, String group, String docid) {
        String eventId = "group.comment";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("group", group);
        map.put("docid", docid);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 圈贴回复 统计
     *
     * @param context
     * @param group
     * @param docid
     */
    public static void onZSGroupReply(Context context, String group, String docid) {
        String eventId = "group.reply";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("group", group);
        map.put("docid", docid);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 圈贴点赞 统计
     *
     * @param context
     * @param group
     * @param docid
     */
    public static void onZSGroupUp(Context context, String group, String docid) {
        String eventId = "group.up";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("group", group);
        map.put("docid", docid);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 圈贴收藏 统计
     *
     * @param context
     * @param group
     * @param docid
     */
    public static void onZSGroupFavorite(Context context, String group, String docid) {
        String eventId = "group.favorite";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("group", group);
        map.put("docid", docid);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 加入圈子 统计
     *
     * @param context
     * @param group
     * @param type    type：精选订阅推荐、详情页顶部订阅、详情页正文底部订阅、订阅列表外部订阅、订阅菜下拉、热门订阅、订阅大全、其它
     */
//    public static void onZSGroupJoin(Context context, String group, String type) {
//        String eventId = "group.join";
//        HashMap<String, String> map = new HashMap<String, String>();
//        map.put("group", group);
//        map.put("type", type);
//        ZSSdkUtil.onEvent(context, eventId, map);
//    }

    /**
     * ZSSDK 退出圈子 统计
     *
     * @param context
     * @param group
     */
    public static void onZSGroupQuit(Context context, String group) {
        String eventId = "group.quit";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("group", group);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * 登录
     *
     * @param context
     */
    public static void onZSLogin(Context context) {
        String eventId = "login";
        ZSSdkUtil.onEvent(context, eventId, null);
    }

    /**
     * ZSSDK 注册成功
     *
     * @param context
     */
    public static void onZSreg(Context context, String type) {
        String eventId = "reg";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("type", type);
        ZSSdkUtil.onEvent(context, eventId, null);
    }

    /**
     * ZSSDK 引导页订阅行为 统计
     *
     * @param context 上下文
     */
    public static void onZSBootpageSub(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "bootpage.subscribe");
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK  我的订阅页面搜索行为 统计
     *
     * @param context 上下文
     */
    public static void onZSSubSearch(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "subscribepage.search");
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK srp页内切换网页结果 统计
     *
     * @param context 上下文
     */
    public static void onZSSrpdetailChange(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "srpdetail.changepage.click");
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 搜索框搜索点击次数 统计
     *
     * @param context 上下文
     */
    public static void onZSAllSearchClick(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "allsearch.click");
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 搜索首页搜索框点击 统计
     *
     * @param context 上下文
     */
    public static void onZSSearchHomepageClick(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "searchhomepage.click");
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 我的订阅 保存按钮 统计
     *
     * @param context 上下文
     */
    public static void onZSSubscribepageSave(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "subscribepage.save.click");
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 精选订阅推荐取消按钮 统计
     *
     * @param context 上下文
     */
    public static void onZSJxCancel(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "jx.cancel");
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 精选订阅推荐订阅点击 统计
     *
     * @param context 上下文
     */
    public static void onZSJxSubscribe(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "jx.subscribe");
        ZSSdkUtil.onEvent(context, eventId, map);
    }


    /**
     * 点击开机广告跳转
     *
     * @param context 上下文
     *                url广告url
     */
    public static void onZSAdstart(Context context, String adid) {
        String eventId = "start.page.ad";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("adid", adid);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * 点击开机广告跳过按钮
     *
     * @param context 上下文
     *                url广告url
     */
    public static void onZSAdskip(Context context, String adid) {
        String eventId = "start.page.skip";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("adid", adid);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * 点击开机广告没有跳转
     *
     * @param context 上下文
     *                url广告url
     */
    public static void onZSAdclick(Context context, String adid) {
        String eventId = "start.page.click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("adid", adid);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 新闻不喜欢点击行为 统计
     *
     * @param context 上下文
     */
    public static void onZSNewsDislike(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "news.dislike.click");
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 我的订阅管理行为 统计
     *
     * @param context 上下文
     */
    public static void onZSSubscribeManage(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "subscribepage.manage");
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 兴趣圈不喜欢行为点击 统计
     *
     * @param context 上下文
     */
    public static void onZSGroupDislike(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "group.dislike.click");
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 段子不喜欢行为点击 统计
     *
     * @param context 上下文
     */
    public static void onZSDuanziDislike(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "duanzi.dislike.click");
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 图集不喜欢行为点击 统计
     *
     * @param context 上下文
     */
    public static void onZSPicturelDislike(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "tuji.dislike.click");
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK GIF不喜欢行为点击 统计
     *
     * @param context 上下文
     */
    public static void onZSGIFDislike(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "gif.dislike.click");
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 专题不喜欢行为点击 统计
     *
     * @param context 上下文
     */
    public static void onZSTopicDislike(Context context) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "topic.dislike.click");
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 底部导航点击
     *
     * @param context 上下文
     */
    public static void onZSDevTabItem(Context context, String name) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "dev.tab.item." + name);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 我的界面列表条目点击
     *
     * @param context 上下文
     */
    public static void onZSDevMyItem(Context context, String name) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "dev.my.item." + name);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 发现界面条目点击
     *
     * @param context 上下文
     */
    public static void onZSDevDiscoverItem(Context context, String name) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "dev.discover.item." + name);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 钱包界面条目点击
     *
     * @param context 上下文
     */
    public static void onZSDevMoneyItem(Context context, String name) {
        String eventId = "click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", "dev.money.item." + name);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 首页球球列表、要闻列表条目点击
     *
     * @param context 上下文
     */
    public static void onZSDevListItemClick(Context context, String title, String srpId, String keyword, String url, String source, String category, String channel, String blogId) {
        String eventId = "dev.list.item.click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("title", title);
        map.put("srpId", srpId);
        map.put("keyword", keyword);
        map.put("url", url);
        map.put("source", source);
        map.put("category", category);
        map.put("channel", channel);
        map.put("blogId", blogId);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 消息列表(list)界面服务号点击 统计
     *
     * @param context
     * @param serviceName
     * @param serviceID
     */
    public static void onZSIMServiceListClick(Context context, String serviceName, long serviceID) {
        String eventId = "dev.im.service.list.click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("user_id", SYUserManager.getInstance().getUserId());
        map.put("service_name", serviceName);
        map.put("service_id", String.valueOf(serviceID));
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 滚动至左侧(订阅)球球的次数
     *
     * @param context
     */
    public static void onZSDevBallLeftSelected(Context context, String title, String srpId, String keyword) {
        String eventId = "dev.ball.left.selected";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("title", title);
        map.put("srpId", srpId);
        map.put("keyword", keyword);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 滚动至右侧(固定)球球的次数
     *
     * @param context
     */
    public static void onZSDevBallRightSelected(Context context, String title, String srpId, String keyword) {
        String eventId = "dev.ball.right.selected";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("title", title);
        map.put("srpId", srpId);
        map.put("keyword", keyword);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 点击中间球球的次数
     *
     * @param context
     */
    public static void onZSDevBallMiddleClick(Context context, String title, String srpId, String keyword) {
        String eventId = "dev.ball.middle.click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("title", title);
        map.put("srpId", srpId);
        map.put("keyword", keyword);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 点击左侧(订阅)球球的次数
     *
     * @param context
     */
    public static void onZSDevBallLeftClick(Context context, String title, String srpId, String keyword) {
        String eventId = "dev.ball.left.click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("title", title);
        map.put("srpId", srpId);
        map.put("keyword", keyword);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 点击右侧(固定)球球的次数
     *
     * @param context
     */
    public static void onZSDevBallRightClick(Context context, String title, String srpId, String keyword) {
        String eventId = "dev.ball.right.click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("title", title);
        map.put("srpId", srpId);
        map.put("keyword", keyword);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 首页访问次数
     *
     * @param context
     */
    public static void onZSDevMainView(Context context, String source, String title) {
        String eventId = "dev.main.view";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("source", source);
        map.put("title", title);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * ZSSDK 服务号消息列表中消息(item)的点击次数 统计
     *
     * @param context
     * @param messageID
     * @param serviceID 当前服务号ID
     * @param itemTitle
     * @param ct        用于区分type
     */
    public static void onZSIMServiceItemClick(Context context, String messageID,
                                              long serviceID, String itemTitle, int ct) {
        String eventId = "dev.im.service.item.click";
        String type = null;
        switch (ct) {
            case IMessageConst.CONTENT_TYPE_SOUYUE_NEWS_SHARE:             //搜悦新闻分享 9 - souxiaoyue
                type = "新闻";
                break;
            case IMessageConst.CONTENT_TYPE_SRP_SHARE:                     // SRP词分享类型 20 - souxiaoyue
                type = "SRP";
                break;
            case IMessageConst.CONTENT_TYPE_INTEREST_CIRCLE_CARD:          //圈名片分享 13 - souxiaoyue
                type = "圈名片";
                break;
            case IMessageConst.CONTENT_TYPE_WEB:                           // WEB跳转类型（贺卡等）23 - souxiaoyue
                type = "WEB跳转";
                break;
            case IMessageConst.CONTENT_TYPE_VCARD:                         // 个人名片 3
                type = "个人名片";
                break;
            case IMessageConst.CONTENT_TYPE_SERVICE_MESSAGE_FIRST:         // 16
            case IMessageConst.CONTENT_TYPE_SERVICE_MESSAGE_SECOND:        // 17
                type = "服务号消息";
                break;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("user_id", SYUserManager.getInstance().getUserId());
        map.put("message_id", messageID);
        map.put("service_id", String.valueOf(serviceID));
        map.put("message_title", itemTitle);
        map.put("type", type);
        ZSSdkUtil.onEvent(context, eventId, map);
    }
    //--------- 5.1.0 end ---------

    //--------- 5.2.0 start --------------------

    /**
     * 引导页点击男女
     * @param context
     * @param gender 性别
     */
    public static void onZSGuideGenderClick(Context context, String gender) {
        String eventId = "guide.gender.click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("title", gender);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * 引导页人群分类点击
     * @param context
     * @param characterType 角色
     */
    public static void onZSGuideCharacterClick(Context context, String characterType) {
        String eventId = "guide.people.click";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("title", characterType);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * 引导页兴趣分类点击
     * @param context
     * @param interestType
     */
    public static void onZSGuideInterestClick(Context context, String interestType) {
        String eventId = "guide.people.interest";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("title", interestType);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * 引导页 默认选中的取消点击
     * @param context
     * @param selectedType
     */
    public static void onZSGuideSelectedCancel(Context context, String selectedType) {
        String eventId = "guide.selected.cancel";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("title", selectedType);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    public static final  String video_list_play="video.list.play";
    public static final  String video_detail_play ="video.detail.play";

    public static final  String list_share="video.list.share";
    public static final  String list_up="video.list.up";
    public static final  String list_down="video.list.down";
    public static final  String list_comment="video.list.comment";
    public static final  String list_favorate="video.list.favorate";
    public static final  String list_favorate_cancle="video.list.favorate.cancle";

    public static final  String video_detail_share="video.detail.share";
    public static final  String video_detail_up="video.detail.up";
    public static final  String video_detail_down="video.detail.down";
    public static final  String video_detail_favorate="video.detail.favorate";
    public static final  String video_detail_favorate_cancle="video.detail.favorate.cancle";
    public static final  String video_detail_comment="video.detail.comment";
    public static final  String video_relate_item_click="video.relate.item.click";
    public static final  String video_play_fullscreen="video.play.fullscreen";

    /**
     * 视频的相关事件
     * @param context
     * @param id
     */
    public static void onZSVideoEvent(Context context,String eventId, String id) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * 创建分组点击
     * @param context
     */
    public static void onZSGroupCreate(Context context)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        ZSSdkUtil.onEvent(context, "sub.group.create",map);
    }

    public static void onZSListItemDoSomeThingClick(Context context,String eventId, String srpId, String keyword,String category) {

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("srpId", srpId);
        map.put("keyword", keyword);
        map.put("category", category);
        ZSSdkUtil.onEvent(context, eventId, map);
    }

    /**
     * 新增书签
     * 新增历史记录
    *
    * */

    public static void onFirstEnterSouyueAccount(Context context) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("data", DeviceInfoUtils.getAccountDataJson(context));
        ZSSdkUtil.onEvent(context, "dev.account", map);
    }
    public static void onFirstEnterSouyueBookMark(Context context){
        HashMap<String, String> map2 = new HashMap<String, String>();
        map2.put("data", DeviceInfoUtils.getBookMarkJson(context));
        ZSSdkUtil.onEvent(context, "dev.bookmark", map2);
    }


    //--------- 5.2.0 end --------------------
}