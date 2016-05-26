package com.zhongsou.souyue.im.render;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.souyue.image.helper.ImageHelper;
import com.tuita.sdk.im.db.module.AtFriend;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.GroupMembers;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.tuita.sdk.im.db.module.UserBean;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.adapter.ImDialogAdapter;
import com.zhongsou.souyue.im.emoji.IMEmojiPattern;
import com.zhongsou.souyue.im.module.MsgContent;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.ImHtmlTagHandler;
import com.zhongsou.souyue.im.util.ImUtils;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 消息工具类
 *
 * @author zhm
 *
 */
public class MsgUtils {
	public static final long MAX_SHOW_TIME = 60 * 10 * 1000;// 显示间隔最大时间
	private static final int RENDER_COUNT = 34;//render的数量如果有增加需要更改此数量

	/**
	 * 根据消息的类型返回对应的消息View
	 *
	 * @param context
	 * @param itemType
	 *            消息类开
	 * @param adapter
	 *            列表的适配类
	 * @return
	 */
	public static ItemTypeRender getItemTypeRender(Context context,
			final int itemType, BaseTypeAdapter<ChatMsgEntity> adapter) {
		ItemTypeRender typeRender = null;
		switch (itemType) {             //消息的显示类型
		case MsgItemRender.MSG_TEXT_LEFT:
		case MsgItemRender.MSG_TEXT_RIGHT:
			typeRender = new MsgTextRender(context, adapter, itemType);     //文本
			break;
		case MsgItemRender.MSG_IMAGE_LEFT:
		case MsgItemRender.MSG_IMAGE_RIGHT:
			typeRender = new MsgImageRender(context, adapter, itemType);    //图片
			break;
		case MsgItemRender.MSG_AUDIO_LEFT:
		case MsgItemRender.MSG_AUDIO_RIGHT:
			typeRender = new MsgAudioRender(context, adapter, itemType);    //语音
			break;
		case MsgItemRender.MSG_GIF_LEFT:
		case MsgItemRender.MSG_GIF_RIGHT:
			typeRender = new MsgGifRender(context, adapter, itemType);      //gif动画
			break;
        case MsgItemRender.MSG_CARD_LEFT:
        case MsgItemRender.MSG_CARD_RIGHT:
            typeRender = new MsgCardRender(context, adapter, itemType);     //名片
            break;
        case MsgItemRender.MSG_SHARE_LEFT:
        case MsgItemRender.MSG_SHARE_RIGHT:
            typeRender = new MsgShareRender(context, adapter, itemType);    //分享
            break;
        case MsgItemRender.MSG_WHISPER_LEFT:
        case MsgItemRender.MSG_WHISPER_RIGHT:
            typeRender = new MsgWhisperRender(context, adapter, itemType);  //密信
            break;

		case MsgItemRender.MSG_NOT_FRIEND:
		case MsgItemRender.MSG_FRIEND:
			typeRender = new MsgNoFriendRender(context,adapter,itemType);//非好友聊天
			break;

        case MsgItemRender.MSG_ASKCOIN_LEFT:
        case MsgItemRender.MSG_ASKCOIN_RIGHT:
            typeRender = new MsgAskCoinRender(context, adapter, itemType);  //求中搜币
            break;

        case MsgItemRender.MSG_GIFTCOIN_LEFT:
        case MsgItemRender.MSG_GIFTCOIN_RIGHT:
            typeRender = new MsgGiftCoinRender(context, adapter, itemType);  //赠中搜币
            break;

        case MsgItemRender.MSG_SERMSGFIRST_LEFT:
        case MsgItemRender.MSG_SERMSGFIRST_RIGHT:
             typeRender = new MsgSerMsgFirstRender(context, adapter, itemType);  //服务号第一种类型
             break;
        case MsgItemRender.MSG_SERMSGSECOND_LEFT:
        case MsgItemRender.MSG_SERMSGSECOND_RIGHT:
             typeRender = new MsgSerMsgSecondRender(context, adapter, itemType);  //服务号第二种类型
             break;
		case MsgItemRender.MSG_FILE_LEFT:
		case MsgItemRender.MSG_FILE_RIGHT:
			 typeRender = new MsgFileRender(context, adapter, itemType);  //文件类型消息
			 break;
		case MsgItemRender.MSG_RED_PACKET_LEFT:
		case MsgItemRender.MSG_RED_PACKET_RIGHT:
			typeRender = new MsgRedPacketRender(context, adapter, itemType);  //红包类型类型消息
			break;
		case MsgItemRender.MSG_NEW_SYSTEM_MSG_LEFT:
		case MsgItemRender.MSG_NEW_SYSTEM_MSG_RIGHT:
			typeRender = new MsgSysMsgRender(context, adapter, itemType);  //新系统消息
			break;
		default:
			typeRender = new MsgUpdateRender(context, adapter, MsgItemRender.MSG_DEFAULT_LEFT);
			break;
		}
		return typeRender;
	}

	/**
	 * 消息的总视图个数 具体个数在MsgItemRender类中定义<br>
	 * 添加类型时需要修改返回值
	 *
	 * @return
	 */
	public static int getItemTypeCount() {
		return RENDER_COUNT;
	}

	/**
	 * 返回每条消息对应的视图类型
	 *
	 * @param chatMsg
	 * @return
	 */
	public static int getItemViewType(ChatMsgEntity chatMsg) {
		switch (chatMsg.getType()) {
		case IMessageConst.CONTENT_TYPE_TEXT:           //文本
			return chatMsg.isComMsg() ? MsgItemRender.MSG_TEXT_LEFT
					: MsgItemRender.MSG_TEXT_RIGHT;
        case IMessageConst.CONTENT_TYPE_AT_FRIEND:      //@好友
            return chatMsg.isComMsg() ? MsgItemRender.MSG_TEXT_LEFT
                    : MsgItemRender.MSG_TEXT_RIGHT;
        case IMessageConst.CONTENT_TYPE_SENDCOIN:       //11
            return chatMsg.isComMsg() ? MsgItemRender.MSG_TEXT_LEFT
                    : MsgItemRender.MSG_TEXT_RIGHT;

        case IMessageConst.CONTENT_TYPE_NEW_IMAGE:      //图片
            return chatMsg.isComMsg() ? MsgItemRender.MSG_IMAGE_LEFT
                    : MsgItemRender.MSG_IMAGE_RIGHT;


		case IMessageConst.CONTENT_TYPE_NEW_VOICE:      //语音
			return chatMsg.isComMsg() ? MsgItemRender.MSG_AUDIO_LEFT
					: MsgItemRender.MSG_AUDIO_RIGHT;


		case IMessageConst.CONTENT_TYPE_GIF:            //gif动画
			return chatMsg.isComMsg() ? MsgItemRender.MSG_GIF_LEFT
					: MsgItemRender.MSG_GIF_RIGHT;


        case IMessageConst.CONTENT_TYPE_VCARD:      //名片
            return chatMsg.isComMsg() ? MsgItemRender.MSG_CARD_LEFT
                    : MsgItemRender.MSG_CARD_RIGHT;
        case IMessageConst.CONTENT_TYPE_GROUP_CARD:      //群名片
            return chatMsg.isComMsg() ? MsgItemRender.MSG_CARD_LEFT
                    : MsgItemRender.MSG_CARD_RIGHT;


        case IMessageConst.CONTENT_TYPE_INTEREST_ADD_FRIEND:      //添加好友入公开圈吧
            return chatMsg.isComMsg() ? MsgItemRender.MSG_SHARE_LEFT
                    : MsgItemRender.MSG_SHARE_RIGHT;
        case IMessageConst.CONTENT_TYPE_INTEREST_CIRCLE_CARD:      //圈名片分享
            return chatMsg.isComMsg() ? MsgItemRender.MSG_SHARE_LEFT
                    : MsgItemRender.MSG_SHARE_RIGHT;
        case IMessageConst.CONTENT_TYPE_INTEREST_ADD_FRIEND_PRIVATE:      //添加好友入私密圈吧 14
            return chatMsg.isComMsg() ? MsgItemRender.MSG_SHARE_LEFT
                    : MsgItemRender.MSG_SHARE_RIGHT;
        case IMessageConst.CONTENT_TYPE_SRP_SHARE:      //srp词分享
            return chatMsg.isComMsg() ? MsgItemRender.MSG_SHARE_LEFT
                    : MsgItemRender.MSG_SHARE_RIGHT;
        case IMessageConst.CONTENT_TYPE_INTEREST_SHARE:      //圈吧分享帖 7
            return chatMsg.isComMsg() ? MsgItemRender.MSG_SHARE_LEFT
                    : MsgItemRender.MSG_SHARE_RIGHT;
        case IMessageConst.CONTENT_TYPE_SOUYUE_NEWS_SHARE:      //搜悦新闻分享 9
        return chatMsg.isComMsg() ? MsgItemRender.MSG_SHARE_LEFT
                    : MsgItemRender.MSG_SHARE_RIGHT;
        case IMessageConst.CONTENT_TYPE_WEB:      //WEB跳转类型（贺卡等)
            return chatMsg.isComMsg() ? MsgItemRender.MSG_SHARE_LEFT
                    : MsgItemRender.MSG_SHARE_RIGHT;


        case IMessageConst.CONTENT_TYPE_SECRET_MSG:      //密信
            return chatMsg.isComMsg() ? MsgItemRender.MSG_WHISPER_LEFT
                    : MsgItemRender.MSG_WHISPER_RIGHT;


        case IMessageConst.CONTENT_TYPE_SYS_NOTFRIEND://不是好友
			return chatMsg.isComMsg() ? MsgItemRender.MSG_NOT_FRIEND
                    : MsgItemRender.MSG_FRIEND;

//        case IMessageConst.CONTENT_TYPE_SYSMSG:     //系统消息
//            return MsgItemRender.MSG_NOT_FRIEND;
        case IMessageConst.CONTENT_TYPE_SYSMSG:     //系统消息
            return MsgItemRender.MSG_NOT_FRIEND;


        case IMessageConst.CONTENT_TYPE_SHARE_TIGER:      //分享老虎机
            return chatMsg.isComMsg() ? MsgItemRender.MSG_TIGER_LEFT
                    : MsgItemRender.MSG_TIGER_RIGHT;

        case IMessageConst.CONTENT_TYPE_TIGER:      //求中搜币
            return chatMsg.isComMsg() ? MsgItemRender.MSG_ASKCOIN_LEFT
                    : MsgItemRender.MSG_ASKCOIN_RIGHT;

        case IMessageConst.CONTENT_TYPE_GIFT_COIN:       //赠币
            return chatMsg.isComMsg() ? MsgItemRender.MSG_GIFTCOIN_LEFT
                    : MsgItemRender.MSG_GIFTCOIN_RIGHT;

        case IMessageConst.CONTENT_TYPE_SERVICE_MESSAGE_FIRST:      //服务号第一种类型
            return chatMsg.isComMsg() ? MsgItemRender.MSG_SERMSGFIRST_LEFT
                    : MsgItemRender.MSG_SERMSGFIRST_RIGHT;

        case IMessageConst.CONTENT_TYPE_SERVICE_MESSAGE_SECOND:      //服务号第二种类型
            return chatMsg.isComMsg() ? MsgItemRender.MSG_SERMSGSECOND_LEFT
                    : MsgItemRender.MSG_SERMSGSECOND_RIGHT;

		case IMessageConst.CONTENT_TYPE_FILE:			//文件类型
			return chatMsg.isComMsg() ? MsgItemRender.MSG_FILE_LEFT
					: MsgItemRender.MSG_FILE_RIGHT;

		case IMessageConst.CONTENT_TYPE_RED_PAKETS:   //红包类型
			return chatMsg.isComMsg() ? MsgItemRender.MSG_RED_PACKET_LEFT
					: MsgItemRender.MSG_RED_PACKET_RIGHT;

		case IMessageConst.CONTENT_TYPE_NEW_SYSTEM_MSG:   //新系统消息类型
			return chatMsg.isComMsg() ? MsgItemRender.MSG_NEW_SYSTEM_MSG_LEFT
					: MsgItemRender.MSG_NEW_SYSTEM_MSG_RIGHT;

		default:                                    //默认类型
			return chatMsg.isComMsg() ? MsgItemRender.MSG_DEFAULT_LEFT
					: MsgItemRender.MSG_DEFAULT_RIGHT;
		}
	}

	/**
	 * 是否显示时间
	 *
	 * @param prevMsg
	 * @param currentMsg
	 * @return
	 */
	public static boolean isShowTime(ChatMsgEntity prevMsg,
			ChatMsgEntity currentMsg) {
		if (prevMsg != null) {
			// 上一条信息时间
			long lastTime = Long.valueOf(prevMsg.getDate());
			// 当前信息时间
			long time = Long.valueOf(currentMsg.getDate());
			if (time - lastTime < MAX_SHOW_TIME || currentMsg.getType() == IMessageConst.CONTENT_TYPE_SYS_NOTFRIEND||time==0||lastTime==0) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * text 显示规则
	 *
	 * @param context
	 * @param entity
	 * @return
	 */
	public static Spanned showText(final Context context, ChatMsgEntity entity) {
		Spanned showText;
		Html.ImageGetter imageGetter = new Html.ImageGetter() {
			@Override
			public Drawable getDrawable(String source) {
				int id = Integer.parseInt(source);
				Drawable d = context.getResources().getDrawable(id);
				d.setBounds(0, 0, d.getIntrinsicWidth() * 5 / 7,
						d.getIntrinsicWidth() * 5 / 7);
				return d;
			}
		};
		setEmoji(context, entity);

		// 文本消息
		String content = entity.getTempText();
		content = content.replaceAll("\n", "<br/>");

		// 识别url正则表达式
		String regex = ImUtils.WEB_URL_PATTERN;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		StringBuffer result = new StringBuffer();
		// 循环找到匹配的url,然后给添加上标签
		while (matcher.find()) {
			String urlStr = matcher.group();
			StringBuffer replace = new StringBuffer();
			replace.append("<html><body><im_html><![CDATA[" + urlStr
					+ "]]></im_html></body></html>");
			matcher.appendReplacement(result, replace.toString());
		}
		matcher.appendTail(result);
		String[] sArray = result.toString().split("<", 2);
		String showContent = "";
		if (sArray.length == 2) {
			showContent = sArray[0].replaceAll(" ", "&nbsp;") + "<" + sArray[1];
		}
		showText = Html.fromHtml(TextUtils.isEmpty(showContent) ? result.toString().replaceAll(" ", "&nbsp;") : showContent,
				imageGetter, new ImHtmlTagHandler(context));

		return showText;
	}

	/**
	 * 设置表情
	 *
	 * @param context
	 * @param entity
	 */
	private static void setEmoji(Context context, ChatMsgEntity entity) {
		// 显示表情
		if (entity.getText() != null) {
			entity.setTempText(entity.getText().replaceAll("<", "&lt;")
					.replaceAll(">", "&gt;"));
            String spannableString = null;
			try {
				spannableString = IMEmojiPattern.getInstace()
						.getExpressionString(context, entity.getTempText());
            } catch (Exception e1) {
				e1.printStackTrace();
			}

			entity.setTempText(spannableString);
		}
	}

	/**
	 * @好友 解析
	 * @param entity
	 * @param context
	 * @return
	 */
	public static String changeContentToName(Context context,
			ChatMsgEntity entity) {
		setEmoji(context, entity);
		AtFriend atFriend = new Gson().fromJson(entity.getContentForAt(),
				AtFriend.class);
		String c = atFriend.getC();
		String newc = "";// @者名字
		List<UserBean> userBean = atFriend.getUsers();
		for (int i = 0; i < userBean.size(); i++) {
			long uid = userBean.get(i).getUid();
			String nickname = userBean.get(i).getNick();
			Contact contact = ImserviceHelp.getInstance()
					.db_getContactById(uid);
            GroupMembers groupMembers = ImserviceHelp.getInstance()
					.db_findMemberListByGroupidandUid((Long.valueOf(entity.chatId)), uid);
			String newname = "";
			if (contact != null
					&& !TextUtils.isEmpty(contact.getComment_name())) {
				newname = contact.getComment_name();
			} else if (groupMembers != null) {
				newname = TextUtils.isEmpty(groupMembers.getMember_name()) ? groupMembers
						.getNick_name() : groupMembers.getMember_name();
			} else {
				newname = nickname;
			}
			c = c.replace("@"+nickname+" ", "@"+newname+" ");
//			c = c.replace(nickname,newname);
		}
		return c;
	}

    /**
     * 获取分享的iocn options
     * @return
     */
    public static DisplayImageOptions getShareIconOptions(){
        return new DisplayImageOptions.Builder().cacheOnDisk(true)
                .cacheInMemory(true).displayer(new RoundedBitmapDisplayer(10))
                .postProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        return ImageUtil.getSquareBitmap(bitmap);
                    }
                })
                .build();
    }

    /**
     * 获取头像options
     * @return
     */
	private static DisplayImageOptions headOptions= null;
    public static DisplayImageOptions getHeadOptions(){
		if(headOptions==null){
			headOptions = new DisplayImageOptions.Builder().cacheOnDisk(true)
					.cacheInMemory(true)
					.displayer(new RoundedBitmapDisplayer(10))
					.showImageOnLoading(R.drawable.default_head).build();
		}
       return headOptions;
    }

	private static DisplayImageOptions imgOptions= null;
	public static DisplayImageOptions getImgOptions(final Context context){
//		final int mBigSize = DeviceUtil.dip2px(context, 120);// 竖图大于两倍图片的高度
//		final int mLowSize = DeviceUtil.dip2px(context, 60);// 横图大于两倍图片的宽度
		if(imgOptions==null){
			imgOptions = new DisplayImageOptions.Builder().cacheOnDisk(true)
					.cacheInMemory(true)
					.showImageOnLoading(R.drawable.im_chat_default)
							// resource or drawable
					.showImageForEmptyUri(R.drawable.im_chat_default)
							// resource or drawable
					.showImageOnFail(R.drawable.im_chat_default)
							// resource or drawable
					.displayer(new RoundedBitmapDisplayer(10))
//					.postProcessor(new BitmapProcessor() {
//						public Bitmap process(Bitmap bitmap) {
//							Bitmap zoomImg = null;
//							if (bitmap.getHeight() < bitmap.getWidth()) {
//								zoomImg = ImageUtil.newZoomImg(context,bitmap);
//							} else {
//								zoomImg = ImageUtil.newZoomImg(context,bitmap);
//							}
//							return zoomImg;
//						}
//					})
					.build();
		}
		return imgOptions;
	}

	/**
	 * 自定义 IM聊天dialog 长按聊天信息显示  其中dialog大小已在方法内部经过计算
	 * @param context
	 * @param items  要显示的条目
	 * @param listener  OnItemClickListener 条目点击时的监听器
	 * @return 返回创建的dialog
	 */
	public static AlertDialog showDialog(Context context,int items[],AdapterView.OnItemClickListener listener){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		View view = LayoutInflater.from(context).inflate(R.layout.im_new_dialog, null);
		ListView listView = (ListView) view.findViewById(R.id.dialog_items_list);
		ImDialogAdapter adapter = new ImDialogAdapter(context,items);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(listener);
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		if(!dialog.isShowing()){
			dialog.show();
		}
		dialog.getWindow().setLayout(DeviceUtil.dip2px(context, 236), ViewGroup.LayoutParams.WRAP_CONTENT);
		dialog.getWindow().setContentView(view);
		return dialog;
	}

	/**
	 * 根据文件名和处理过的bitmap 执行保存图片操作
	 * @param fileName 两种缩略图的文件名称
	 * @param bm 经过压缩的缩略图
	 */
	public static String saveImage(Context context,String fileName,Bitmap bm){
		Bitmap thumb = ImageUtil.newZoomImg(context, bm);
		File filePath = new File(getCachePathFile(context),fileName+".jpg");//构建缓存缩略图文件
//        File thumbPath = new File(diskThumbDir,fileName+".jpg");//构建指定格式的缩略图文件
		ImageHelper.writeToFile(bm, filePath, 60);
		ImageUtil.saveBitmap(context, thumb, getThumbPathFile(context), fileName + ".jpg");
		return filePath.getAbsolutePath();
	}

	/**
	 * 缓存缩略图目录
	 * @param context
	 * @return
	 */
	public static File getCachePathFile(Context context){
		return ImageUtil.getDiskCacheDir(context, "temporary");
	}

	/**
	 * 缓存IM列表显示的指定格式的缩略图目录
	 * @param context
	 * @return
	 */
	public static File getThumbPathFile(Context context){
		return ImageUtil.getDiskCacheDir(context,"thumb");
	}
//---------------------------------  以下是download文件相关方法
	/**
	 * 根据扩展名来判断是那种文件 目前分四类
	 * 1.图片
	 * 2.pdf
	 * 3.MP3
	 * 4.文件类型
	 */
	public static final int FILE_TYPE_IMG = 0;
	public static final int FILE_TYPE_PDF = 1;
	public static final int FILE_TYPE_MP3 = 2;
	public static final int FILE_TYPE_FILE = 3;
	public enum Image{//Image 文件类型的扩展名
		jpg,jpeg,bmp,gif,png,tiff,psd,swf
	}
	public enum Mp3{//mp3 文件类型的扩展名
		mp3,wma,rm,wav,
	}
	public static final String PDF_SUFFIX = "pdf";
	public static int getFileType(String fileName){
		String tempSuffix = getFileSuffix(fileName);
		if(TextUtils.isEmpty(tempSuffix)){
			return FILE_TYPE_FILE;
		}
		if(PDF_SUFFIX.equals(tempSuffix)){
			return FILE_TYPE_PDF;
		}
		Image[] images = Image.values();
		int imagesLength = images.length;
		for(int i=0;i<imagesLength;i++){
			if(tempSuffix.equals(images[i].name())){
				return FILE_TYPE_IMG;
			}
		}

		Mp3 mp3s[] = Mp3.values();
		int mp3sLength = mp3s.length;
		for(int i=0;i<mp3sLength;i++){
			if(tempSuffix.equals(mp3s[i].name())){
				return FILE_TYPE_MP3;
			}
		}
		return FILE_TYPE_FILE;
	}

	/**
	 * 根据文件名来获取文件的扩展名
	 * @param fileName
	 * @return
	 */
	public static String getFileSuffix(String fileName){
		if(!TextUtils.isEmpty(fileName)){
//			String str[]=fileName.split(".");
//			return str[str.length-1];
			return fileName.substring(fileName.lastIndexOf(".")+1);
		}
		return "";
	}

	/**
	 * 根据给定的URL地址来获得其中的文件名
	 * @param url
	 * @return
	 */
	public static String getFileName(String url){
		return url.substring(url.lastIndexOf('/')+1);
	}

	/**
	 * 根据指定文件名的类型把不同的图片放到给定的ImageView中
	 * @param image 给定的ImageView
	 * @param name　文件的url
	 */
	public static int setImagePic(ImageView image,String name){
		int fileType = MsgUtils.getFileType(name);
		if(MsgUtils.FILE_TYPE_PDF == fileType){
			image.setImageResource(R.drawable.im_type_pdf);
			return MsgUtils.FILE_TYPE_PDF;
		}else if(MsgUtils.FILE_TYPE_IMG == fileType){
			image.setImageResource(R.drawable.im_type_pic);
			return MsgUtils.FILE_TYPE_IMG;
		}else if(MsgUtils.FILE_TYPE_MP3 ==fileType){
			image.setImageResource(R.drawable.im_type_mp3);
			return MsgUtils.FILE_TYPE_MP3;
		}else{
			image.setImageResource(R.drawable.im_type_file);
			return MsgUtils.FILE_TYPE_FILE;
		}
	}

    /**
     * 按照指定的规则 计算文件应该显示的大小
     * @return
     */
    public static String getFileSize(long _fileSize,DecimalFormat _dFormat){
        String fileSizeStr;
        if(_fileSize>500){
            if(_fileSize/1024>1024){
                fileSizeStr = _dFormat.format(_fileSize/1024.0/1024)+"MB";
            }else{
                fileSizeStr = _dFormat.format(_fileSize/1024.0)+"KB";
            }
        }else{
            fileSizeStr = _fileSize+"B";
        }
        return fileSizeStr;
    }

	/**
	 * 根据给出的文件名字在某个文件夹中查找，如果有重复的则重命名
	 * @return 重命名后的文件名
	 */
	public static String createOnlyFileName(String filePath,String fileName){
		if(!isFileExists(filePath,fileName)){
			return fileName;
		}
		String name = fileName.substring(0,fileName.lastIndexOf("."));
		String ext = getFileSuffix(fileName);
		for(int i=1;i<Integer.MAX_VALUE;i++){
			String newFileName = name+"("+i+")."+ext;
			if(!isFileExists(filePath , newFileName)){
				return newFileName;
			}
		}
		return fileName;
	}

	public static boolean isFileExists(String filePath,String fileName){
		File file = new File(filePath+fileName);
		if(file.exists()){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * msg跳转处理方法
	 * @param msgContent
     */
	public static void msgJump(Context context,MsgContent msgContent){
		if (msgContent != null && msgContent.getJumpType() != null){
			if ("".equals(msgContent.getJumpType())){
				//不跳转
			}else if ("redPacketUrl".equals(msgContent.getJumpType())){
				IntentUtil.gotoWeb(context,createOpenRedUrl(msgContent.getUrl()),"interactWeb");
			}else {
				MainApplication.getInstance().checkVersion(1);
			}
		}
	}

	/**
	 *拼拆红包地址
	 * @param url
	 * @return
	 */
	public static String createOpenRedUrl(String url) {
		StringBuilder sb = new StringBuilder(url);
		sb.append("&receivename=").append(SYUserManager.getInstance().getUser().userName()).append("&receiveid=")
				.append(SYUserManager.getInstance().getUserId());
		return sb.toString();
	}
}
