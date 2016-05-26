package com.zhongsou.souyue.im.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.ac.ContactsListActivity;
import com.zhongsou.souyue.im.ac.IMChatActivity;
import com.zhongsou.souyue.im.search.ByteUtils;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.Utility;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;

public class ImUtils {

    public static boolean longIsNotNull(Long s) {
        if (s != null) {
            return true;
        }
        return false;
    }

    /**
     * 验证修改备注名
     */
    public static boolean validateNoteName(TextView nickName, Context c) {
        int nickname_length = nickName.getText().toString().trim().length();
        if (nickname_length != 0) {
            if (Utility.getStrLength(nickName.getText().toString().trim()) < 4
                    || Utility.getStrLength(nickName.getText().toString().trim()) > 20) {
                SouYueToast.makeText(c,
                        c.getResources().getString(R.string.notename_length_error), 0).show();
                return false;
            } else {
                if (Utility.isChAndEnAndNum(nickName.getText().toString().trim())) {
                    return true;
                } else {
                    SouYueToast.makeText(c,
                            c.getResources().getString(R.string.notename_format_error), 0).show();
                    return false;
                }
            }
        } else {
            SouYueToast.makeText(c,
                    c.getResources().getString(R.string.notename_no_empty), 0).show();
            return false;
        }
    }


    /**
     * 验证修改备注名——im
     */
    public static boolean validateNoteNameforIm(TextView nickName, Context c) {
        int nickname_length = nickName.getText().toString().trim().length();
        if (nickname_length != 0) {
            if (Utility.getStrLength(nickName.getText().toString().trim()) < 4
                    || Utility.getStrLength(nickName.getText().toString().trim()) > 20) {
                SouYueToast.makeText(c,
                        c.getResources().getString(R.string.notename_length_error), 0).show();
                return false;
            } else {
                if (Utility.isImName(nickName.getText().toString().trim().replace(" ", ""))) {
                    return true;
                } else {
                    SouYueToast.makeText(c,
                            c.getResources().getString(R.string.notename_format_error), 0).show();
                    return false;
                }
            }
        } else {
            SouYueToast.makeText(c,
                    c.getResources().getString(R.string.notename_no_empty), 0).show();
            return false;
        }
    }

    /**
     * 验证修改我的群昵称
     */
    public static boolean validateMyGroupName(TextView nickName, Context c) {
        int nickname_length = nickName.getText().toString().trim().length();
        if (nickname_length != 0) {
            if (Utility.getStrLength(nickName.getText().toString().trim()) < 4
                    || Utility.getStrLength(nickName.getText().toString().trim()) > 20) {
                SouYueToast.makeText(c,
                        c.getResources().getString(R.string.mygroupnickname_length_error), 0).show();
                return false;
            } else {
                if (Utility.isChAndEnAndNum(nickName.getText().toString().trim())) {
                    return true;
                } else {
                    SouYueToast.makeText(c,
                            c.getResources().getString(R.string.mygroupnickname_format_error), 0).show();
                    return false;
                }
            }
        } else {
            SouYueToast.makeText(c,
                    c.getResources().getString(R.string.mygroupnickname_no_empty), 0).show();
            return false;
        }
    }

    /**
     * 验证修改群名称
     */
    public static boolean validateGroupName(TextView nickName, Context c) {
        int nickname_length = nickName.getText().toString().trim().length();
        if (nickname_length != 0) {
            if (Utility.getStrLength(nickName.getText().toString().trim()) < 4
                    || Utility.getStrLength(nickName.getText().toString().trim()) > 20) {
                SouYueToast.makeText(c,
                        c.getResources().getString(R.string.groupname_length_error), 0).show();
                return false;
            } else {
                if (Utility.isChAndEnAndNum(nickName.getText().toString().trim())) {
                    return true;
                } else {
                    SouYueToast.makeText(c,
                            c.getResources().getString(R.string.groupname_format_error), 0).show();
                    return false;
                }
            }
        } else {
            SouYueToast.makeText(c,
                    c.getResources().getString(R.string.groupname_no_empty), 0).show();
            return false;
        }
    }

    /**
     * 修正冒泡数
     */
    public static String getBubleText(String bubleNum) {
        if (bubleNum != null) {
            if (bubleNum.trim().length() >= 3) {
                bubleNum = "99+";
            }
        }
        return bubleNum;
    }

    /**
     * 错误提示
     */
    public static void showImError(String json, Context context) {
        try {
            JSONObject object = new JSONObject(json);
            int code = object.getInt("code");
            switch (code) {
                case 403:
                    SouYueToast.makeText(context, R.string.Kicked_out_group, 1).show();
                    break;
                case 601:
                    SouYueToast.makeText(context, R.string.not_friend_for_two, 1).show();
                    break;
                case 603:
                    SouYueToast.makeText(context, R.string.Permission_to_verify, 1).show();
                    break;
                case 605:
                    SouYueToast.makeText(context, R.string.group_member_transfinite, 1).show();
                    break;
                case 606:
                    SouYueToast.makeText(context, R.string.group_member_little_ban, 1).show();
                    break;
                case 607:
                    SouYueToast.makeText(context, R.string.group_not_exist, 1).show();
                    break;
                case 8000:
                    SouYueToast.makeText(context, R.string.resquestfailed, 1).show();
                    break;
                case 8001:
                    SouYueToast.makeText(context, R.string.resquestfailed, 1).show();
                    break;
                default:
                    SouYueToast.makeText(context, R.string.appear_problem, 1).show();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //("http://(([a-zA-z0-9]|-){1,}\\.){1,}[a-zA-z0-9]{1,}-*");
//    public static final Pattern WEB_URL_PATTERN = Pattern.compile("(http://|https://){1}[\\w\\.\\-/:]+");
//    public static final Pattern WEB_URL_PATTERN = Pattern.compile("http://(([a-zA-z0-9]|-){1,}\\.){1,}[a-zA-z0-9]{1,}-*");
    public static final String WEB_URL_PATTERN = "((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\|\\-~!@#$%^&*+?:_/=]*)?)|((www\\.)[a-zA-Z0-9\\.\\|\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=]*)?)";


    public static String getTextIndex(String sourceText, String serchText,String keyWord) {
        int index = 0;
        if (!keyWord.equals("")) {
            if (sourceText.contains(keyWord)) {
                index = sourceText.indexOf(keyWord);
            } else {
                index = serchText.indexOf(keyWord);
            }
        }

        if (index>18){
            //sourceText.length()
            if(sourceText.length()-index>5){
                sourceText = sourceText.substring(index-3, index+keyWord.length()+3);
                sourceText = sourceText.replace(sourceText,"..."+sourceText+"...");
            }else {
                sourceText = sourceText.substring(index-3, sourceText.length());
                sourceText = sourceText.replace(sourceText,"..."+sourceText);
            }
            return sourceText;
        }else{
            return sourceText;
        }

    }


    public static String cutText(String sourceText) {
        if(sourceText.length()>15){
            sourceText = sourceText.substring(0, 10);
            sourceText = sourceText.replace(sourceText,sourceText+"...");
            return sourceText;
        }else{
            return sourceText;
        }

    }

    public static Spanned getHighlightText(String sourceText, String serchText, String keyWord) {
        int index = 0;
        keyWord = keyWord.toUpperCase();
        if (!keyWord.equals("")) {
            if (sourceText.toUpperCase().contains(keyWord)) {
                index = sourceText.toUpperCase().indexOf(keyWord);
            } else {
                index = serchText.toUpperCase().indexOf(keyWord);
            }

            Spanned temp;

            int len = keyWord.replace("", "").length();
            if (index >= 0) {
                temp = Html.fromHtml(sourceText.substring(0, index)
                        + "<font color=#da4644>"
                        + sourceText.substring(index, index + len) + "</font>"
                        + sourceText.substring(index + len, sourceText.length()));
            } else {
                temp = Html.fromHtml(sourceText);
            }
            return temp;
        } else {
            Spanned noTemp = Html.fromHtml(sourceText);
            return noTemp;
        }

    }

    /**
     * 完全匹配大小写
     * @param sourceText
     * @param serchText
     * @param keyWord
     * @return
     */

    public static Spanned getHighlightTextIgnore(String sourceText, String serchText, String keyWord) {
        int index = 0;
        if (!keyWord.equals("")) {
            if (sourceText.contains(keyWord)) {
                index = sourceText.indexOf(keyWord);
            } else {
                index = serchText.indexOf(keyWord);
            }

            Spanned temp;

            int len = keyWord.replace("", "").length();
            if (index >= 0) {
                temp = Html.fromHtml(sourceText.substring(0, index)
                        + "<font color=#418ec9>"
                        + sourceText.substring(index, index + len) + "</font>"
                        + sourceText.substring(index + len, sourceText.length()));
            } else {
                temp = Html.fromHtml(sourceText);
            }
            return temp;
        } else {
            Spanned noTemp = Html.fromHtml(sourceText);
            return noTemp;
        }

    }

    public static Spanned getSimpleHighlightText(String sourceText, String keyWord) {
        int index = 0;
        keyWord = keyWord.toUpperCase();
        if (!keyWord.equals("")) {
            if (sourceText.toUpperCase().contains(keyWord)) {
                index = sourceText.toUpperCase().indexOf(keyWord);
            } else {
                index = 0;
            }
            Spanned temp;
            int len = keyWord.replace(" ", "").length();
            if (index >= 0) {
                temp = Html.fromHtml(sourceText.substring(0, index)
                        + "<font color=#418ec9>"
                        + sourceText.substring(index, index + len) + "</font>"
                        + sourceText.substring(index + len, sourceText.length()));
            } else {
                temp = Html.fromHtml(sourceText);
            }
            return temp;
        } else {
            Spanned noTemp = Html.fromHtml(sourceText);
            return noTemp;
        }


    }

    public static Spanned getHighlightText(String start, String sourceText, String serchText, String keyWord) {
        int index = 0;
        keyWord = keyWord.toUpperCase();
        if (!keyWord.equals("")) {
            if (sourceText.toUpperCase().contains(keyWord)) {
                index = sourceText.toUpperCase().indexOf(keyWord);
            } else {
                index = serchText.toUpperCase().indexOf(keyWord);
            }

            int len = keyWord.replace(" ", "").length();
            Spanned temp;
            if (index >= 0) {
                temp = Html.fromHtml(start + sourceText.substring(0, index)
                        + "<font color=#da4644>"
                        + sourceText.substring(index, index + len) + "</font>"
                        + sourceText.substring(index + len, sourceText.length()));
            } else {
                temp = Html.fromHtml(sourceText);
            }
            return temp;
        } else {
            Spanned noTemp = Html.fromHtml(sourceText);
            return noTemp;
        }

    }


    /**
     * 高亮区分大小写
     */
    public static Spanned getHighlightTextIgnore(String start, String sourceText, String serchText, String keyWord) {
        int index = 0;
        if (!keyWord.equals("")) {
            if (sourceText.contains(keyWord)) {
                index = sourceText.indexOf(keyWord);
            } else {
                index = serchText.indexOf(keyWord);
            }

            int len = keyWord.replace(" ", "").length();
            Spanned temp;
            if (index >= 0) {
                temp = Html.fromHtml(start + sourceText.substring(0, index)
                        + "<font color=#418ec9>"
                        + sourceText.substring(index, index + len) + "</font>"
                        + sourceText.substring(index + len, sourceText.length()));
            } else {
                temp = Html.fromHtml(sourceText);
            }
            return temp;
        } else {
            Spanned noTemp = Html.fromHtml(sourceText);
            return noTemp;
        }

    }

    /**
     * 快速查找数组中是否包含某个String
     */

    public static boolean useArraysBinarySearch(String[] arr, String targetValue) {
        int a = Arrays.binarySearch(arr, targetValue);
        if (a >= 0) {
            return true;
        } else {
            return false;
        }
    }

//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    public static int[] getDrawableWidthAndHeight(Context ct, String id) {
//        int[] wandh = new int[2];
//        id = id.split("asset/")[1];
//        File file = new File(ct.getResources().getDrawable());
//        try {
//            RandomAccessFile randomAccessFile = new RandomAccessFile(file,"r");
//            byte[] a = new byte[2];
//            try {
//                if(randomAccessFile!=null&&randomAccessFile.length()>0){
//                    randomAccessFile.seek(6);
//                    randomAccessFile.read(a, 0, 2);
//                    wandh[0]=ByteUtils.byte2int(a);
////                    System.out.println("------------>abc=" + ByteUtils.byte2int(a));
//                    randomAccessFile.seek(8);
//                    randomAccessFile.read(a, 0, 2);
//                    wandh[1]=ByteUtils.byte2int(a);
////                    System.out.println("------------>abc1=" + ByteUtils.byte2int(a));
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        return wandh;
//    }
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static int[] getDrawableWidthAndHeightByPath(Context ct,String gifPath){
    	Slog.d("callback", "gifPath:------------------" + gifPath);
    	int[] wandh = new int[2];
        File file = new File(gifPath);
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file,"r");
            byte[] a = new byte[2];
            try {
                if(randomAccessFile!=null&&randomAccessFile.length()>0){
                    randomAccessFile.seek(6);
                    randomAccessFile.read(a, 0, 2);
                    wandh[0]=ByteUtils.byte2int(a);
//                    System.out.println("------------>abc=" + ByteUtils.byte2int(a));
                    randomAccessFile.seek(8);
                    randomAccessFile.read(a, 0, 2);
                    wandh[1]=ByteUtils.byte2int(a);
//                    System.out.println("------------>abc1=" + ByteUtils.byte2int(a));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    	return wandh;
    }

    public static int byteToInt2(byte[] b){

        return ((int)b[0]) << 8 + b[1];

    }


    //发送群名片到群聊
    public static void sendGroupCard(Activity activity,Group itemContact,Group group){
//        Intent intent = new Intent(activity, TestChatActivity.class);
//        intent.putExtra(GroupChatActivity.KEY_ACTION,
//                GroupChatActivity.ACTION_SEND_GROUPCARD);
//        intent.putExtra("group", itemContact);
//        intent.putExtra(GroupChatActivity.KEY_GET_GROUPCARD_ID, group);
//        activity.startActivity(intent);
//        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);

        ChatMsgEntity e =  getEntity(itemContact);
        e.setType(IMessageConst.CONTENT_TYPE_GROUP_CARD);
        e.status = IMessageConst.STATUS_SENTING;
        e.setGroup(group);
        ImserviceHelp.getInstance().im_sendMessage(IConst.CHAT_TYPE_GROUP,
                itemContact.getGroup_id(), e.getType(), e.getText(), e.getRetry());
        IMChatActivity.invoke(activity, IConst.CHAT_TYPE_GROUP, itemContact.getGroup_id());
//        finish();
    }

    // 从用户信息进入--更多--发送名片
    public static void sendCardFromInfo(Activity activity,Group itemContact,Contact contactItem) {
//        Intent intent = new Intent(activity, TestChatActivity.class);
//        intent.putExtra(TestChatActivity.KEY_ACTION,
//                TestChatActivity.ACTION_SEND_VCARD);
//        intent.putExtra(TestChatActivity.KEY_CONTACT, itemContact);
//        intent.putExtra(TestChatActivity.KEY_GET_CARD_ID, contactItem);
//        activity.startActivity(intent);
//        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);


        ChatMsgEntity e =  getEntity(itemContact);
        e.setType(IMessageConst.CONTENT_TYPE_VCARD);
        e.status = IMessageConst.STATUS_SENTING;
        e.setCard(contactItem);
        ImserviceHelp.getInstance().im_sendMessage(IConst.CHAT_TYPE_GROUP,
                itemContact.getGroup_id(), e.getType(), e.getText(), e.getRetry());
        IMChatActivity.invoke(activity, IConst.CHAT_TYPE_GROUP, itemContact.getGroup_id());
        activity.finish();
    }


    /**
     * 从老虎机页面进入
     *
     * @param itemContact 传递的联系人信息
     * @param sendType    1 ACTION_ASK_COIN 索要中搜币 2 ACTION_ASK_SHARE分享
     */
    public static void sendCardFromTigerGame(Activity activity, Group itemContact, String sendType, boolean isFromTiger, int tigernum) {
        Intent intent = new Intent(activity, IMChatActivity.class);
        intent.putExtra(IMChatActivity.KEY_ACTION, sendType);
        intent.putExtra(IMChatActivity.KEY_CONTACT, itemContact);
        intent.putExtra(ContactsListActivity.START_FROM, isFromTiger);
        intent.putExtra(ConstantsUtils.SHARE_COUNT, tigernum);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
        activity.finish();
    }


    // 从聊天页面进入--发送名片
    public static void sendCardFromImChat(Activity activity, Group itemContact) {
        Intent backIntent = new Intent();
        backIntent.putExtra(IMChatActivity.KEY_GET_CARD_ID, itemContact);
        activity.setResult(Activity.RESULT_OK, backIntent);
        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
        activity.finish();
    }

    //从contactListFragment进入--发送群名片
    public static void sendCardToChat(Activity activity, Group group) {
        Intent backIntent = new Intent();
        backIntent.putExtra(IMChatActivity.KEY_GET_CARD_ID, group);
        activity.setResult(IMIntentUtil.SCARD, backIntent);
        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
        activity.finish();
    }

    /**
     * 获取消息对像
     * @param group
     * @return
     */
    private static ChatMsgEntity getEntity(Group group) {
        ChatMsgEntity entity = new ChatMsgEntity();
        entity.userId = Long.parseLong(SYUserManager.getInstance().getUserId());
        entity.chatId = group.getGroup_id();
        entity.setSendId(Long.parseLong(SYUserManager.getInstance().getUserId()));
        entity.setChatType(IConst.CHAT_TYPE_GROUP);
        entity.setIconUrl(SYUserManager.getInstance().getUser().image());
        return entity;
    }
}
