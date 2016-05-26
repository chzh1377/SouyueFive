package com.zhongsou.souyue.module;


import android.os.Parcel;
import android.os.Parcelable;
import com.zhongsou.souyue.DontObfuscateInterface;

public class ChatItem implements Parcelable,DontObfuscateInterface{

	private int id;
	private String contactId;

	private String content;
	
	private String textContent;

	private String mimeType;

	private int boxType;
	
	private int status;

	private boolean isSendSucceed;

	private String timeStamp;
	
	private String msgId;
	private int isAck;
	private int msgSource;
	private long audioLength;
	private int duration=0;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public long getAudioLength() {
		return audioLength;
	}

	public void setAudioLength(long audioLength) {
		this.audioLength = audioLength;
	}

	public int getBoxType() {
		return boxType;
	}

	public void setBoxType(int boxType) {
		this.boxType = boxType;
	}

	public int getMsgSource() {
		return msgSource;
	}

	public void setMsgSource(int msgSource) {
		this.msgSource = msgSource;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public int getIsAck() {
		return isAck;
	}

	public void setIsAck(int isAck) {
		this.isAck = isAck;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isSendSucceed() {
		return isSendSucceed;
	}

	public void setSendSucceed(boolean isSendSucceed) {
		this.isSendSucceed = isSendSucceed;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	

	
	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(id);
		dest.writeString(contactId);
		dest.writeString(content);
		dest.writeString(textContent);
		dest.writeString(mimeType);
		dest.writeInt(boxType);
		dest.writeInt(status);
		dest.writeByte((byte) (isSendSucceed ? 1 : 0));
		dest.writeString(timeStamp);
		dest.writeString(msgId);
		dest.writeInt(isAck);
		dest.writeInt(msgSource);
		dest.writeLong(audioLength);
		dest.writeInt(duration);
	}
	public static final  Parcelable.Creator<ChatItem> CREATOR = new Parcelable.Creator<ChatItem>(){

		@Override
		public ChatItem createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			ChatItem item = new ChatItem();
			item.id = source.readInt();
			item.contactId=source.readString();
			item.content = source.readString();
			item.textContent = source.readString();
			item.mimeType = source.readString();
			item.boxType = source.readInt();
			item.status = source.readInt();
			item.isSendSucceed=source.readByte()==1;
			item.timeStamp=source.readString();
			item.msgId=source.readString();
			item.isAck=source.readInt();
			item.msgSource= source.readInt();
			item.audioLength= source.readLong();
			item.duration=source.readInt();
			return item;
		}

		@Override
		public ChatItem[] newArray(int size) {
			// TODO Auto-generated method stub
			return new ChatItem[size];
		}
		
	};
	public boolean equals(Object obj){
		if(obj==this){
			return true;
		}
		if(obj instanceof ChatItem){
			ChatItem item = (ChatItem) obj;
			if(item.contactId.equals(contactId)&&item.timeStamp.equals(timeStamp)){
				return true;
			}
		}
		return false;
	}
}
