package com.tuita.sdk.im.db.helper;

import android.content.Intent;
import android.util.Log;
import com.tuita.sdk.BroadcastUtil;
import com.tuita.sdk.TuitaIMManager;
import com.zhongsou.souyue.log.Logger;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.WhereCondition;
import org.json.JSONObject;

/**
 * Created by zhangwenbin on 15/1/19.
 */
public class HelperUtils extends BaseDaoHelper {


    /**
     * 仿greendao 增强like查询
     *
     * @param property
     * @param value
     * @return
     */
    public static WhereCondition like(Property property, String value) {
        return new WhereCondition.PropertyCondition(property, " LIKE ? ESCAPE '\\'", value);
    }

    /**
     * 更新所有的信息  用于100 109
     *
     * @param manager
     * @throws Exception
     */
    public static void updateInitMsg(final JSONObject data, final TuitaIMManager manager) {
        getDaoSession(manager.getManager().getContext()).runInTx(new Runnable() {
            @Override
            public void run() {
                long timeStart = System.currentTimeMillis();
                try {
                    //群入库或者更新
                    GroupDaoHelper.getInstance(manager.getManager().getContext()).updateGroup(data, manager);

                    //群成员入库或者更新
                    GroupMembersDaoHelper.getInstance(manager.getManager().getContext()).updateGroupMembers(data, manager);

                    //联系人信息入库或者更新
                    ContactDaoHelper.getInstance(manager.getManager().getContext()).updateContact(data, manager);
                    Log.i("saveTime1", "----->" + (System.currentTimeMillis() - timeStart));

                    //服务号相关信息入库或者更新
                    ServiceMessageRecentDaoHelper.getInstance(manager.getManager().getContext()).updateServiceMsg(data, manager);

                    //表情是否更新的标志
                    if (data.has("hasNewExpressionPackage")) {
                        Intent expressionIntent = new Intent(BroadcastUtil.ACTION_EXPRESSION_NEW);
                        expressionIntent.putExtra("newExpression", data.getBoolean("hasNewExpressionPackage"));
                        manager.getManager().getContext().sendBroadcast(expressionIntent);
                    }


                } catch (Exception e) {
                    Logger.e("tuita", "HelperUtils.updateInitMsg", "init parse is error", e);
                    e.printStackTrace();
                }
                Log.i("saveTime", "----->" + (System.currentTimeMillis() - timeStart));
                Logger.i("tuita", "HelperUtils.updateInitMsg", "离线消息入库时长" + (System.currentTimeMillis() - timeStart) + "毫秒");
            }
        });

    }

    /**
     * 获取极光推送过来的nc
     *
     * @param _jumpData
     * @return  数组  array[0] 界面跳转数据，array[1] 通知栏跳转数据
     */
    public static String[] getJumpData(String _jumpData,String _title,String _digst) throws Exception{
        JSONObject jumpObj = new JSONObject();
        String itemJump = "";
        String ncJump = "";
        String[] dataArray = _jumpData.split(",");

        if ("1".equals(dataArray[0])) {      //新闻二级导航词
            jumpObj.put("category", "pasePage");
            jumpObj.put("type", "news");
            jumpObj.put("keyword", dataArray[2]);
            jumpObj.put("url", dataArray[5]);
            jumpObj.put("images", dataArray[6]);
            jumpObj.put("srpId", dataArray[7]);
            jumpObj.put("title", _title);
            jumpObj.put("description", _digst);

            itemJump = jumpObj.toString();
            ncJump = _jumpData;

        } else if ("3".equals(dataArray[0])) {      //跳转图集 拼成搜悦格式
            jumpObj.put("category", "atlas");
            jumpObj.put("keyword", dataArray[1]);
            jumpObj.put("url", dataArray[2]);
            jumpObj.put("images", dataArray[3]);
            jumpObj.put("srpId", dataArray[4]);
            jumpObj.put("pubTime", dataArray[5]);
            jumpObj.put("source", dataArray[6]);
            jumpObj.put("title", _title);
            jumpObj.put("description", _digst);

            itemJump = jumpObj.toString();
            ncJump = _jumpData + "," + _title + "," + _digst;

        }
        return new String[]{itemJump,ncJump};
    }


}
