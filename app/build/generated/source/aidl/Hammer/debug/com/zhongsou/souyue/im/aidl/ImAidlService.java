/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: F:\\workspace\\SouyueFive\\app\\src\\main\\aidl\\com\\zhongsou\\souyue\\im\\aidl\\ImAidlService.aidl
 */
package com.zhongsou.souyue.im.aidl;
public interface ImAidlService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.zhongsou.souyue.im.aidl.ImAidlService
{
private static final java.lang.String DESCRIPTOR = "com.zhongsou.souyue.im.aidl.ImAidlService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.zhongsou.souyue.im.aidl.ImAidlService interface,
 * generating a proxy if needed.
 */
public static com.zhongsou.souyue.im.aidl.ImAidlService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.zhongsou.souyue.im.aidl.ImAidlService))) {
return ((com.zhongsou.souyue.im.aidl.ImAidlService)iin);
}
return new com.zhongsou.souyue.im.aidl.ImAidlService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_im_connect:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.im_connect(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_im_logout:
{
data.enforceInterface(DESCRIPTOR);
this.im_logout();
reply.writeNoException();
return true;
}
case TRANSACTION_im_search:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.im_search(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_newGroupOp:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.util.List _arg1;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readArrayList(cl);
boolean _result = this.newGroupOp(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_addOrDeleteGroupMembersOp:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
java.util.List _arg2;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg2 = data.readArrayList(cl);
boolean _result = this.addOrDeleteGroupMembersOp(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_retreatGroupMembersOp:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
boolean _result = this.retreatGroupMembersOp(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_saveGroupConfigOp:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _arg2;
_arg2 = (0!=data.readInt());
boolean _arg3;
_arg3 = (0!=data.readInt());
boolean _result = this.saveGroupConfigOp(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_updateGroupNickNameOp:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
boolean _result = this.updateGroupNickNameOp(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getGroupDetailsOp:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _result = this.getGroupDetailsOp(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getUserOp:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
long _arg1;
_arg1 = data.readLong();
boolean _result = this.getUserOp(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_im_userOp:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
long _arg1;
_arg1 = data.readLong();
java.lang.String _arg2;
_arg2 = data.readString();
java.lang.String _arg3;
_arg3 = data.readString();
java.lang.String _arg4;
_arg4 = data.readString();
int _arg5;
_arg5 = data.readInt();
boolean _result = this.im_userOp(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_im_info:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _result = this.im_info(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_im_contacts_status:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.im_contacts_status(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_im_contacts_upload:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.im_contacts_upload(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_im_saveMessage:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
long _arg1;
_arg1 = data.readLong();
int _arg2;
_arg2 = data.readInt();
java.lang.String _arg3;
_arg3 = data.readString();
java.lang.String _arg4;
_arg4 = data.readString();
java.lang.String _result = this.im_saveMessage(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_im_update:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
long _arg1;
_arg1 = data.readLong();
java.lang.String _arg2;
_arg2 = data.readString();
boolean _result = this.im_update(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_im_giftzsb:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
int _arg1;
_arg1 = data.readInt();
boolean _result = this.im_giftzsb(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_addGroupMemberOp:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
int _arg3;
_arg3 = data.readInt();
java.util.List _arg4;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg4 = data.readArrayList(cl);
java.lang.String _arg5;
_arg5 = data.readString();
boolean _result = this.addGroupMemberOp(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_findGroupInfo:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
long _arg1;
_arg1 = data.readLong();
java.util.List _arg2;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg2 = data.readArrayList(cl);
boolean _result = this.findGroupInfo(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getMemberDetail:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
long _arg1;
_arg1 = data.readLong();
long _arg2;
_arg2 = data.readLong();
boolean _result = this.getMemberDetail(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_updateNewsNotify:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
long _arg1;
_arg1 = data.readLong();
boolean _arg2;
_arg2 = (0!=data.readInt());
boolean _result = this.updateNewsNotify(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_saveServiceMsgNotify:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
boolean _arg1;
_arg1 = (0!=data.readInt());
boolean _result = this.saveServiceMsgNotify(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_initImService:
{
data.enforceInterface(DESCRIPTOR);
this.initImService();
reply.writeNoException();
return true;
}
case TRANSACTION_cancelNotify:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.cancelNotify(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getNotifyNum:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getNotifyNum(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setUserMessageNotify:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
long _arg1;
_arg1 = data.readLong();
boolean _arg2;
_arg2 = (0!=data.readInt());
boolean _arg3;
_arg3 = (0!=data.readInt());
boolean _arg4;
_arg4 = (0!=data.readInt());
boolean _result = this.setUserMessageNotify(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.zhongsou.souyue.im.aidl.ImAidlService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void im_connect(java.lang.String version) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(version);
mRemote.transact(Stub.TRANSACTION_im_connect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//上线 通
//   boolean appOnline();//程序前台运行
//    void setAppOnline(in boolean status);//程序运行状态。前台，后台

@Override public void im_logout() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_im_logout, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//IM下线

@Override public boolean im_search(java.lang.String keyword) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(keyword);
mRemote.transact(Stub.TRANSACTION_im_search, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//查找好友 通

@Override public boolean newGroupOp(int op, java.util.List uids) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(op);
_data.writeList(uids);
mRemote.transact(Stub.TRANSACTION_newGroupOp, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//创建群

@Override public boolean addOrDeleteGroupMembersOp(int op, java.lang.String gid, java.util.List uids) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(op);
_data.writeString(gid);
_data.writeList(uids);
mRemote.transact(Stub.TRANSACTION_addOrDeleteGroupMembersOp, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//加好友入群,删除群好友

@Override public boolean retreatGroupMembersOp(int op, java.lang.String gid, java.lang.String nextOwnerId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(op);
_data.writeString(gid);
_data.writeString(nextOwnerId);
mRemote.transact(Stub.TRANSACTION_retreatGroupMembersOp, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//退出群

@Override public boolean saveGroupConfigOp(int op, java.lang.String gid, boolean isGroupSaved, boolean isNewsNotifyShielded) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(op);
_data.writeString(gid);
_data.writeInt(((isGroupSaved)?(1):(0)));
_data.writeInt(((isNewsNotifyShielded)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_saveGroupConfigOp, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//保存配置信息

@Override public boolean updateGroupNickNameOp(int op, java.lang.String gid, java.lang.String nick) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(op);
_data.writeString(gid);
_data.writeString(nick);
mRemote.transact(Stub.TRANSACTION_updateGroupNickNameOp, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//修改群昵称或者群成员昵称

@Override public boolean getGroupDetailsOp(int op, java.lang.String gid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(op);
_data.writeString(gid);
mRemote.transact(Stub.TRANSACTION_getGroupDetailsOp, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//获取群详细信息

@Override public boolean getUserOp(int op, long uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(op);
_data.writeLong(uid);
mRemote.transact(Stub.TRANSACTION_getUserOp, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//获取用户并查看是否是好友

@Override public boolean im_userOp(int o, long uid, java.lang.String nick, java.lang.String avatar, java.lang.String text, int originType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(o);
_data.writeLong(uid);
_data.writeString(nick);
_data.writeString(avatar);
_data.writeString(text);
_data.writeInt(originType);
mRemote.transact(Stub.TRANSACTION_im_userOp, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//加好友 通

@Override public boolean im_info(int op, java.lang.String text) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(op);
_data.writeString(text);
mRemote.transact(Stub.TRANSACTION_im_info, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//获得分组，好友列表 调试中

@Override public boolean im_contacts_status(java.lang.String contactJson) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(contactJson);
mRemote.transact(Stub.TRANSACTION_im_contacts_status, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//手机联系人状态匹配

@Override public boolean im_contacts_upload(java.lang.String contactJson) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(contactJson);
mRemote.transact(Stub.TRANSACTION_im_contacts_upload, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//手机联系人上传
//String im_sendMessage(int type, in long uidorgid, in int contentType, in String content, in String retry);//发送消息

@Override public java.lang.String im_saveMessage(int type, long uidorgid, int contentType, java.lang.String content, java.lang.String retry) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
_data.writeLong(uidorgid);
_data.writeInt(contentType);
_data.writeString(content);
_data.writeString(retry);
mRemote.transact(Stub.TRANSACTION_im_saveMessage, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//发送消息(针对语音，图片等，只存不发)

@Override public boolean im_update(int op, long uid, java.lang.String text) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(op);
_data.writeLong(uid);
_data.writeString(text);
mRemote.transact(Stub.TRANSACTION_im_update, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//更新各种信息 op 1更新昵称 2更新群里的昵称 3更新好友名称

@Override public boolean im_giftzsb(long uid, int num) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(uid);
_data.writeInt(num);
mRemote.transact(Stub.TRANSACTION_im_giftzsb, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//赠送中搜币

@Override public boolean addGroupMemberOp(int op, java.lang.String gid, java.lang.String inviterId, int mode, java.util.List uids, java.lang.String source) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(op);
_data.writeString(gid);
_data.writeString(inviterId);
_data.writeInt(mode);
_data.writeList(uids);
_data.writeString(source);
mRemote.transact(Stub.TRANSACTION_addGroupMemberOp, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//加人进群   5.2 添加进群时候的来源

@Override public boolean findGroupInfo(int op, long group_id, java.util.List memberIds) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(op);
_data.writeLong(group_id);
_data.writeList(memberIds);
mRemote.transact(Stub.TRANSACTION_findGroupInfo, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//查询群信息

@Override public boolean getMemberDetail(int op, long group_id, long memberId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(op);
_data.writeLong(group_id);
_data.writeLong(memberId);
mRemote.transact(Stub.TRANSACTION_getMemberDetail, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//获取群成员详细信息 op 9

@Override public boolean updateNewsNotify(int op, long uid, boolean is_news_notify) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(op);
_data.writeLong(uid);
_data.writeInt(((is_news_notify)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_updateNewsNotify, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//私聊消息提醒

@Override public boolean saveServiceMsgNotify(long srvId, boolean isNewsNotifyShielded) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(srvId);
_data.writeInt(((isNewsNotifyShielded)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_saveServiceMsgNotify, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//服务号消息免打扰

@Override public void initImService() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_initImService, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//初始化tuita用户，以后可能去掉

@Override public void cancelNotify(int id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(id);
mRemote.transact(Stub.TRANSACTION_cancelNotify, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//取消通知栏的AIDL方法

@Override public int getNotifyNum(int id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(id);
mRemote.transact(Stub.TRANSACTION_getNotifyNum, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//获得某个ID的通知栏计数的AIDL方法

@Override public boolean setUserMessageNotify(int op, long uid, boolean isNewsNotifyShielded, boolean hasSound, boolean isVibrate) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(op);
_data.writeLong(uid);
_data.writeInt(((isNewsNotifyShielded)?(1):(0)));
_data.writeInt(((hasSound)?(1):(0)));
_data.writeInt(((isVibrate)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setUserMessageNotify, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_im_connect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_im_logout = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_im_search = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_newGroupOp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_addOrDeleteGroupMembersOp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_retreatGroupMembersOp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_saveGroupConfigOp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_updateGroupNickNameOp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getGroupDetailsOp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_getUserOp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_im_userOp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_im_info = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_im_contacts_status = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_im_contacts_upload = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_im_saveMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_im_update = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_im_giftzsb = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_addGroupMemberOp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_findGroupInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_getMemberDetail = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_updateNewsNotify = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_saveServiceMsgNotify = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_initImService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
static final int TRANSACTION_cancelNotify = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
static final int TRANSACTION_getNotifyNum = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
static final int TRANSACTION_setUserMessageNotify = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
}
public void im_connect(java.lang.String version) throws android.os.RemoteException;
//上线 通
//   boolean appOnline();//程序前台运行
//    void setAppOnline(in boolean status);//程序运行状态。前台，后台

public void im_logout() throws android.os.RemoteException;
//IM下线

public boolean im_search(java.lang.String keyword) throws android.os.RemoteException;
//查找好友 通

public boolean newGroupOp(int op, java.util.List uids) throws android.os.RemoteException;
//创建群

public boolean addOrDeleteGroupMembersOp(int op, java.lang.String gid, java.util.List uids) throws android.os.RemoteException;
//加好友入群,删除群好友

public boolean retreatGroupMembersOp(int op, java.lang.String gid, java.lang.String nextOwnerId) throws android.os.RemoteException;
//退出群

public boolean saveGroupConfigOp(int op, java.lang.String gid, boolean isGroupSaved, boolean isNewsNotifyShielded) throws android.os.RemoteException;
//保存配置信息

public boolean updateGroupNickNameOp(int op, java.lang.String gid, java.lang.String nick) throws android.os.RemoteException;
//修改群昵称或者群成员昵称

public boolean getGroupDetailsOp(int op, java.lang.String gid) throws android.os.RemoteException;
//获取群详细信息

public boolean getUserOp(int op, long uid) throws android.os.RemoteException;
//获取用户并查看是否是好友

public boolean im_userOp(int o, long uid, java.lang.String nick, java.lang.String avatar, java.lang.String text, int originType) throws android.os.RemoteException;
//加好友 通

public boolean im_info(int op, java.lang.String text) throws android.os.RemoteException;
//获得分组，好友列表 调试中

public boolean im_contacts_status(java.lang.String contactJson) throws android.os.RemoteException;
//手机联系人状态匹配

public boolean im_contacts_upload(java.lang.String contactJson) throws android.os.RemoteException;
//手机联系人上传
//String im_sendMessage(int type, in long uidorgid, in int contentType, in String content, in String retry);//发送消息

public java.lang.String im_saveMessage(int type, long uidorgid, int contentType, java.lang.String content, java.lang.String retry) throws android.os.RemoteException;
//发送消息(针对语音，图片等，只存不发)

public boolean im_update(int op, long uid, java.lang.String text) throws android.os.RemoteException;
//更新各种信息 op 1更新昵称 2更新群里的昵称 3更新好友名称

public boolean im_giftzsb(long uid, int num) throws android.os.RemoteException;
//赠送中搜币

public boolean addGroupMemberOp(int op, java.lang.String gid, java.lang.String inviterId, int mode, java.util.List uids, java.lang.String source) throws android.os.RemoteException;
//加人进群   5.2 添加进群时候的来源

public boolean findGroupInfo(int op, long group_id, java.util.List memberIds) throws android.os.RemoteException;
//查询群信息

public boolean getMemberDetail(int op, long group_id, long memberId) throws android.os.RemoteException;
//获取群成员详细信息 op 9

public boolean updateNewsNotify(int op, long uid, boolean is_news_notify) throws android.os.RemoteException;
//私聊消息提醒

public boolean saveServiceMsgNotify(long srvId, boolean isNewsNotifyShielded) throws android.os.RemoteException;
//服务号消息免打扰

public void initImService() throws android.os.RemoteException;
//初始化tuita用户，以后可能去掉

public void cancelNotify(int id) throws android.os.RemoteException;
//取消通知栏的AIDL方法

public int getNotifyNum(int id) throws android.os.RemoteException;
//获得某个ID的通知栏计数的AIDL方法

public boolean setUserMessageNotify(int op, long uid, boolean isNewsNotifyShielded, boolean hasSound, boolean isVibrate) throws android.os.RemoteException;
}
