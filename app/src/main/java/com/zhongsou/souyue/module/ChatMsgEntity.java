
package com.zhongsou.souyue.module;

import android.text.TextUtils;
//import com.alibaba.fastjson.JSONException;
//import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.GroupMembers;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.tuita.sdk.im.db.module.MessageHistory;
import com.tuita.sdk.im.db.module.ServiceMessage;
import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.circle.model.Posts;
import com.zhongsou.souyue.im.module.GroupMember;
import com.zhongsou.souyue.im.module.ImAskInterest;
import com.zhongsou.souyue.im.module.ImShareInterest;
import com.zhongsou.souyue.im.module.ImShareNews;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.utils.SYUserManager;
import org.json.JSONObject;

import java.util.Calendar;




public class ChatMsgEntity extends ResponseObject implements DontObfuscateInterface {
    private int position;//新增本条消息在消息列表中的位置
    private static final String TAG = ChatMsgEntity.class.getSimpleName();
    public static final String FORWARD = "FORWARD";
    public static final int TOTALLENGTH = 10;
    public static final int RECEIVEMAXLENGTH = 300;
    private String name;
    public long date;
    private String text;
    private String url;
    private String iconUrl;
    private String voiceUrl;
    private int voiceLength;
    private int type; // 0文本 1语音 2图片 3名片 4求中搜币
    public long userId;
    public long chatId;
    public Contact contact;
    private ServiceMessage serviceMessage;
    public int status;
    private long sessionOrd;
    private boolean isComMeg = true;
    public String UUId;
    private int retry;
    private long sendId;
    private int chatType;
    private Contact card = new Contact();
    private boolean isShowTime;
    private boolean isFromTiger;
    private ImShareNews imsharenews;
    private ImShareInterest imshareinterest;
    private Posts mPosts;
    private ImAskInterest imaskinterest;
    private boolean versiontip=false; //是否要出更新提示
    private long whisperTimestamp=0;//结束时间
    private long timerLength=0;
    private boolean isWhisperDelete;
    private int isReceiveDetailOpen;
    private boolean isVertical;
    public static final  int OPEN=1;
    public static final  int BACK=2;
    public static final  int INIT=0;
    public static final  int BIGIMAGE = 3;//跳转到大图聊天
    private String nickname;
    private String userImage;
    private String tempText;//临时消息，用于记录表情和url转换成html代码
    private float minHeight;
    private float minWidth;
    private String contentForAt;//用于保存发送失败的@消息
    private boolean isShowUnreadLine;
    private long fileMsgId;//发送文件数据库的ID

    public long getFileMsgId() {
        return fileMsgId;
    }

    public void setFileMsgId(long fileMsgId) {
        this.fileMsgId = fileMsgId;
    }

    public String getContentForAt() {
        return contentForAt;
    }

    public void setContentForAt(String contentForAt) {
        this.contentForAt = contentForAt;
    }

    public boolean isShowUnreadLine() {
        return isShowUnreadLine;
    }

    public void setShowUnreadLine(boolean isShowUnreadLine) {
        this.isShowUnreadLine = isShowUnreadLine;
    }

    public float getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(float minHeight) {
        this.minHeight = minHeight;
    }
    public float getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(float minWidth) {
        this.minWidth = minWidth;
    }
    public String getTempText() {
        return tempText;
    }

    public void setTempText(String tempText) {
        this.tempText = tempText;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private long id;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public void setWhisperTimestamp(long whisperTimestamp) {
        this.whisperTimestamp = whisperTimestamp;
    }
    public int getIsReceiveDetailOpen() {
        return isReceiveDetailOpen;
    }
    public void setIsReceiveDetailOpen(int isReceiveDetailOpen) {
        this.isReceiveDetailOpen = isReceiveDetailOpen;
    }
    public boolean isWhisperDelete() {
        return isWhisperDelete;
    }
    public void setWhisperDelete(boolean isWhisperDelete) {
        this.isWhisperDelete = isWhisperDelete;
    }
    public long getWhisperTimestamp() {
        return whisperTimestamp;
    }
    public long getTimerLength() {
        return timerLength;
    }
    public void initTimerLength() {
        long currentTime=Calendar.getInstance().getTimeInMillis();
        if(isComMsg()){
            if(getText().length()>TOTALLENGTH&&getText().length()<=RECEIVEMAXLENGTH){
                initwhisperTime(currentTime,getText().length());
            }else if(getText().length()>RECEIVEMAXLENGTH){
                initwhisperTime(currentTime,RECEIVEMAXLENGTH);
            }else{
                initwhisperTime(currentTime,TOTALLENGTH);
            }
        }else{
            initwhisperTime(currentTime,TOTALLENGTH);
        }
    }

    private void initwhisperTime(long currentTime,int totalLenght){
        if((Math.rint(currentTime-whisperTimestamp)/1000)>totalLenght){
            this.timerLength = 0;
        }else{
            this.timerLength=totalLenght-(currentTime-whisperTimestamp)/1000;
            if(timerLength<totalLenght){
                isReceiveDetailOpen=BACK;
            }else{
                isReceiveDetailOpen=INIT;
            }
        }
    }

    public void setTimerLength(long timerLength) {
        this.timerLength = timerLength;
    }
    public boolean isVersiontip() {
        return versiontip;
    }
    public void setVersiontip(int type) {
        if(type==IMessageConst.CONTENT_TYPE_TEXT){
            this.versiontip=false;
        }else if(type==IMessageConst.CONTENT_TYPE_VOICE){
            this.versiontip=false;
        }else if(type==IMessageConst.CONTENT_TYPE_IMAGE){
            this.versiontip=false;
        }else if(type==IMessageConst.CONTENT_TYPE_VCARD){
            this.versiontip=false;
        }else if(type==IMessageConst.CONTENT_TYPE_TIGER){
            this.versiontip=false;
        }else if(type==IMessageConst.CONTENT_TYPE_SHARE_TIGER){
            this.versiontip=false;
        }else if(type==IMessageConst.CONTENT_TYPE_SYS_NOTFRIEND){
            this.versiontip=false;
        }else if(type==IMessageConst.CONTENT_TYPE_INTEREST_SHARE){
            this.versiontip=false;
        }else if(type==IMessageConst.CONTENT_TYPE_INTEREST_ADD_FRIEND){
            this.versiontip=false;
        }else if(type==IMessageConst.CONTENT_TYPE_SOUYUE_NEWS_SHARE){
            this.versiontip=false;
        }else if(type==IMessageConst.CONTENT_TYPE_SECRET_MSG){
            this.versiontip=false;
        }else if(type==IMessageConst.CONTENT_TYPE_SENDCOIN){
            this.versiontip=false;
        }else if(type==IMessageConst.CONTENT_TYPE_NEW_VOICE){
            this.versiontip=false;
        }else if(type==IMessageConst.CONTENT_TYPE_INTEREST_ADD_FRIEND_PRIVATE){
            this.versiontip=false;
        }else if(type==IMessageConst.CONTENT_TYPE_INTEREST_CIRCLE_CARD){
            this.versiontip=false;
        }else if(type==IMessageConst.CONTENT_TYPE_NEW_IMAGE){
            this.versiontip=false;
        }else if(type == IMessageConst.CONTENT_TYPE_SYSMSG){
            this.versiontip = false;
        }else if (type == IMessageConst.CONTENT_TYPE_SERVICE_MESSAGE_FIRST){
            this.versiontip = false;
        }else if (type == IMessageConst.CONTENT_TYPE_SERVICE_MESSAGE_SECOND){
            this.versiontip = false;
        }else if (type == IMessageConst.CONTENT_TYPE_GROUP_CARD){
            this.versiontip = false;
        }else if (type == IMessageConst.CONTENT_TYPE_SRP_SHARE){
            this.versiontip = false;
        }else if(type == IMessageConst.CONTENT_TYPE_AT_FRIEND){
            this.versiontip = false;
        }else if(type == IMessageConst.CONTENT_TYPE_GIF){
            this.versiontip = false;
        }else if(type == IMessageConst.CONTENT_TYPE_WEB){
            this.versiontip = false;
        }else if(type == IMessageConst.CONTENT_TYPE_GIFT_COIN){
            this.versiontip = false;
        } else if(type == IMessageConst.CONTENT_TYPE_FILE){
            this.versiontip = false;
        }else{
            this.versiontip=true;
        }

    }

    private boolean isEdit;

    private String mid;   //消息id

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public boolean isEdit() {
        return isEdit;
    }
    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }
    public ImShareNews getImsharenews() {
        return imsharenews;
    }
    public ImShareInterest getImshareinterest() {
        return imshareinterest;
    }

    public Posts getmPosts() {
        return mPosts;
    }

    public ImAskInterest getImaskinterest() {
        return imaskinterest;
    }

    public boolean isFromTiger() {
        return isFromTiger;
    }

    public void setFromTiger(boolean isFromTiger) {
        this.isFromTiger = isFromTiger;
    }

    public ChatMsgEntity(MessageHistory msg){
        this.chatId = msg.getChat_id();
        this.chatType = msg.getChat_type();
        type = msg.getContent_type();
        this.text = msg.getContent();
        this.date = msg.getDate();
        this.sessionOrd = msg.getSession_order();
        this.status = msg.getStatus();
        this.userId = msg.getMyid();
        this.mid = msg.getBy4();
        if(!TextUtils.isEmpty(msg.getContentforat()))
            this.contentForAt = msg.getContentforat();
        if(msg.getId() != null)
            this.id = msg.getId();
        if(!TextUtils.isEmpty(msg.getBy2())){
            GroupMembers groupMembers = ImserviceHelp.getInstance().db_findMemberListByGroupidandUid(msg.getChat_id(),msg.getSender());
            Contact contacts = ImserviceHelp.getInstance().db_getContactById(msg.getSender());
            String comment_name = "";
            String member_name = "";
            String nick_name = "";
            if (contacts != null) {
                comment_name = contacts.getComment_name();//备注名
            }
            if (groupMembers != null) {
                member_name = groupMembers.getMember_name();//群昵称
                nick_name = groupMembers.getNick_name();//搜悦昵称
                this.userImage = groupMembers.getMember_avatar();
                this.iconUrl = groupMembers.getMember_avatar();
            }else{
                GroupMember groupMember = new Gson().fromJson(msg.getBy2(),new TypeToken<GroupMember>() {}.getType());
                this.userImage = groupMember.getUserImage();
                this.iconUrl = groupMember.getUserImage();
            }
            String name = TextUtils.isEmpty(comment_name) ? TextUtils.isEmpty(member_name) ? nick_name : member_name : comment_name;
            if(TextUtils.isEmpty(name)) {
                GroupMember groupMember = new Gson().fromJson(msg.getBy2(),new TypeToken<GroupMember>() {}.getType());
                this.nickname = groupMember.getNickname();
            }else {
                this.nickname = name;
            }
//            this.userImage = groupMembers.getMember_avatar();
        }
//        if(!TextUtils.isEmpty(msg.getBy2())){
//            GroupMember groupMember = new Gson().fromJson(msg.getBy2(),new TypeToken<GroupMember>() {}.getType());
//            this.nickname = groupMember.getNickname();
//            this.userImage = groupMember.getUserImage();
//        }
        setSendId(msg.getSender());
        this.UUId = msg.getUuid();
        try {
            this.whisperTimestamp=Long.parseLong(msg.getBy1());
        } catch (Exception e) {
            this.whisperTimestamp=0;
        }
        if(isSendWhisperType()){
            initTimerLength();
        }
        if(isCard()){
            initCard();
        }else if(isShareNewsType()){
            initShareNews();
        }else if(isAskInterestType()){
            initAskInterest();
        }else if(isShareInterestType()){
            initShareInterest();
        } else if(isSendWhisperType()) {
            initWhisper();
        }else if(isSendInvitePrivateType()){
            initAskInterest();
        }else if(isSendCircleCardType()){
            initShareInterest();
        }else if(isShareSRPType()){
            initShareNews();
        }else if (isWebType()){
            initWeb();
        }else if(isFileMsgType()){
            initFileMsg(msg);
        }

        setVersiontip(type);
    }

    private boolean isFileMsgType() {
        return type == MessageHistory.CONTENT_TYPE_FILE;
    }


    private void initWhisper() {
        if(!TextUtils.isEmpty(text)){
//              imshareinterest = new Gson().fromJson(text, ImShareInterest.class);
        }
    }

    private void initShareInterest() {
        if(!TextUtils.isEmpty(text)){
            imshareinterest = new Gson().fromJson(text, ImShareInterest.class);
        }
    }

    private void initWeb() {
        if(!TextUtils.isEmpty(text)){
            mPosts = new Gson().fromJson(text, Posts.class);
        }
    }
    private void initFileMsg(MessageHistory msg){
        if(msg.getFileMsgId()==null){
            fileMsgId = -1;
        }else{
            fileMsgId = msg.getFileMsgId();
        }
    }

    private void initAskInterest() {
        if(!TextUtils.isEmpty(text)){
            imaskinterest = new Gson().fromJson(text, ImAskInterest.class);
        }
    }

    private void initShareNews() {
        if(!TextUtils.isEmpty(text)){
            imsharenews = new Gson().fromJson(text, ImShareNews.class);
        }
    }
    private void initCard() {
        try{
            if(!TextUtils.isEmpty(text)){
                card = new Gson().fromJson(text, new TypeToken<Contact>() {}.getType());
                card.setComment_name("");
                card.setChat_id(card.getMyid());
                card.setMyid(SYUserManager.getInstance().getUser().userId());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isComMsg() {
        return SYUserManager.getInstance().getUser().userId() != getSendId();
    }

    public void setIsComMeg(boolean isComMsg) {
        isComMeg = isComMsg;
    }

    public ChatMsgEntity() {
    }


    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public boolean isNotYourFriend(){
        return type == IMessageConst.CONTENT_TYPE_SYS_NOTFRIEND;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getVoiceLength() {
        return voiceLength;
    }

    public void setVoiceLength(int voiceLength) {
        this.voiceLength = voiceLength;
    }

    public boolean isTextType() {
        return type == MessageHistory.CONTENT_TYPE_TEXT;
    }

    public boolean isImageType() {
        return type == MessageHistory.CONTENT_TYPE_NEW_IMAGE;
    }

    public boolean isVoice() {
        return type == MessageHistory.CONTENT_TYPE_NEW_VOICE;
    }
    public boolean isShareNewsType() {
        return type == MessageHistory.CONTENT_TYPE_SOUYUE_NEWS_SHARE;
    }
    public boolean isAskInterestType() {
        return type == MessageHistory.CONTENT_TYPE_INTEREST_ADD_FRIEND;
    }
    public boolean isShareInterestType() {
        return type == MessageHistory.CONTENT_TYPE_INTEREST_SHARE;
    }
    //密信类型
    public boolean isSendWhisperType() {
        return type == MessageHistory.CONTENT_TYPE_SECRET_MSG;
    }
    //是否为服务号ct=1
    public boolean isServiceFirst(){
        return type == MessageHistory.CONTENT_TYPE_SERVICE_MESSAGE_FIRST;
    }
    //是否为服务号ct=2
    public boolean isServiceSecond(){
        return type == MessageHistory.CONTENT_TYPE_SERVICE_MESSAGE_SECOND;
    }

    //赠币类型
    public boolean isSendCoinType() {
        return type == MessageHistory.CONTENT_TYPE_SENDCOIN;
    }

    //私密圈邀请好友类型
    public boolean isSendInvitePrivateType() {
        return type == MessageHistory.CONTENT_TYPE_INTEREST_ADD_FRIEND_PRIVATE;
    }

    //圈名片类型
    public boolean isSendCircleCardType() {
        return type == MessageHistory.CONTENT_TYPE_INTEREST_CIRCLE_CARD;
    }
    //群消息系统提示
    public boolean isGroupSysMessage(){
        return type == MessageHistory.CONTENT_TYPE_SYSMSG;
    }
    //群名片类型
    public boolean isGroupCardType(){
        return type == MessageHistory.CONTENT_TYPE_GROUP_CARD;
    }
    //SRP类型
    public boolean isShareSRPType() {
        return type == MessageHistory.CONTENT_TYPE_SRP_SHARE;
    }
    //at类型
    public boolean isAtFriendType(){
        return type == MessageHistory.CONTENT_TYPE_AT_FRIEND;
    }
    //GIF类型
    public boolean isGifType(){
        return type == MessageHistory.CONTENT_TYPE_GIF;
    }
    //WEB类型
    public boolean isWebType(){
        return type == MessageHistory.CONTENT_TYPE_WEB;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public boolean isCard() {
        return type==MessageHistory.CONTENT_TYPE_VCARD;
    }

    public boolean isCion() {
        return type == MessageHistory.CONTENT_TYPE_TIGER;
    }


    /**
     *
     * @return 将json转换成展示的字符串
     */
    public String getCoinString(){
        if(!TextUtils.isEmpty(text)){
            Coin c = new Gson().fromJson(text, new TypeToken<Coin>() {}.getType());
            return c.count;
        }
        return "10";
    }


    public String getCardUrl() {
        return card.getAvatar();
    }

    public MessageHistory getMsgHistory() {
        MessageHistory msg = new MessageHistory();
        msg.setChat_id(chatId);
        msg.setChat_type(type);
        msg.setDate(date);
        msg.setMyid(userId);
        msg.setSession_order(sessionOrd);
        msg.setStatus(status);
        msg.setUuid(UUId);
        msg.setContent_type(msg.getContent_type());
        msg.setContent(text);
        return msg;
    }

    public boolean isSending() {
        return status == 0;
    }

    public boolean isSendFailed() {
        return status == MessageHistory.STATUS_SENT_FAIL;
    }

    public void setFailed() {
        status = MessageHistory.STATUS_SENT_FAIL;
    }

    public void setSuccess() {
        status = MessageHistory.STATUS_HAS_SENT;
    }

    public long getDate() {
        return this.date;
    }

    public void setDate(long date2) {
        this.date = date2;
    }

    public String getRetry() {
        return UUId;
    }

    public long getSessionOrd() {
        return sessionOrd;
    }

    public void setSessionOrd(long sessionOrd) {
        this.sessionOrd = sessionOrd;
    }

    public long getSendId() {
        return sendId;
    }

    public void setSendId(long sendId) {
        this.sendId = sendId;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public boolean isShareTiger() {
        return type == IMessageConst.CONTENT_TYPE_SHARE_TIGER;
    }


    public class Coin implements DontObfuscateInterface {
        public String count;
    }

    public CharSequence getCardName() {
        if(!TextUtils.isEmpty(card.getComment_name())){
            return card.getComment_name();
        }
        return card.getNick_name();
    }

    public CharSequence getCardNickName(){
        return card.getNick_name();
    }

    public void setCard(Contact card) {
        try {
            JSONObject j =new JSONObject();
            j.put("avatar", card.getAvatar());
            j.put("nick_name", card.getNick_name());
            j.put("myid", String.valueOf(card.getChat_id()));
            if(card.getComment_name()!=null){
                j.put("comment_name", card.getComment_name());
            }
            this.text = j.toString();
        } catch (Exception e) {
        }
        this.card = card;
    }

    public void setGroup(com.tuita.sdk.im.db.module.Group group){
        try {
            JSONObject j =new JSONObject();
            j.put("group_avatar", group.getGroup_avatar());
            j.put("group_name", group.getGroup_nick_name());
            j.put("groupid", String.valueOf(group.getGroup_id()));
            j.put("groupmember",group.getMemberCount());
            this.text = j.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.card = groupToContact(group);
    }

    private Contact groupToContact(com.tuita.sdk.im.db.module.Group group){
        Contact contact1 = new Contact();
        contact1.setAvatar(group.getGroup_avatar());
        contact1.setNick_name(group.getGroup_nick_name());
        contact1.setChat_id(group.getGroup_id());
        return contact1;
    }

    public Contact getCard() {
        return card;
    }

    public boolean isShowTime() {
        return isShowTime;
    }

    public void setShowTime(boolean isShowTime) {
        this.isShowTime = isShowTime;
    }
    public boolean isComMeg() {
        return isComMeg;
    }
    public void setComMeg(boolean isComMeg) {
        this.isComMeg = isComMeg;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((UUId == null) ? 0 : UUId.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ChatMsgEntity other = (ChatMsgEntity) obj;
        if (UUId == null) {
            if (other.UUId != null) return false;
        } else if (!UUId.equals(other.UUId)) return false;
        return true;
    }
    public boolean isVertical() {
        return isVertical;
    }
    public void setVertical(boolean isVertical) {
        this.isVertical = isVertical;
    }

    public ServiceMessage getServiceMessage() {
        return serviceMessage;
    }

    public void setServiceMessage(ServiceMessage serviceMessage) {
        this.serviceMessage = serviceMessage;
    }
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
