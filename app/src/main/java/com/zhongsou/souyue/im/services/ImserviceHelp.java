package com.zhongsou.souyue.im.services;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuita.sdk.PushService;
import com.tuita.sdk.TuitaIMManager;
import com.tuita.sdk.im.db.helper.MessageFileDaoHelper;
import com.tuita.sdk.im.db.helper.MessageHistoryDaoHelper;
import com.tuita.sdk.im.db.helper.MessageRecentDaoHelper;
import com.tuita.sdk.im.db.module.Cate;
import com.tuita.sdk.im.db.module.Config;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.GroupMembers;
import com.tuita.sdk.im.db.module.MessageFile;
import com.tuita.sdk.im.db.module.MessageHistory;
import com.tuita.sdk.im.db.module.MessageRecent;
import com.tuita.sdk.im.db.module.NewFriend;
import com.tuita.sdk.im.db.module.SearchMsgResult;
import com.tuita.sdk.im.db.module.ServiceMessage;
import com.tuita.sdk.im.db.module.ServiceMessageRecent;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.im.aidl.ImAidlService;
import com.zhongsou.souyue.im.services.Imservice.ImserviceInterface;
import com.zhongsou.souyue.log.Logger;
import com.zhongsou.souyue.utils.SYUserManager;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImserviceHelp implements ImserviceInterface {
	private static ImserviceHelp instance;
	private MainApplication mInstance = MainApplication.getInstance();
	private static final String TAG = "im";

	private void outLog(String method, String str) {
		Log.w(TAG, "method [" + method + "]" + "---> " + str);
	}

	public static ImserviceHelp getInstance() {
		if (instance == null) {
            instance = new ImserviceHelp();
        }
		return instance;
	}

	private ImserviceHelp() {
	}

    /**
     * 获取用户id（每次都要获取拿到最新）
     * @return
     */
    private long getMyid(){
        return Long.parseLong(SYUserManager.getInstance().getUserId());
    }
	// /**
	// * 连接上线
	// */
	// @Override
	// public void im_connect() {
	// try {
	// if (mInstance != null) {
	// mInstance.imService.im_connect();
	// }
	// } catch (Exception e) {
	// outLog("im_connect", e.getMessage());
	// }
	// };
	@Override
	public void im_connect(String version) {
		try {
			if (mInstance != null && mInstance.imService != null) {
				TuitaIMManager.checkVersion(mInstance,getMyid(),version);
				mInstance.imService.im_connect(version);
			}
		} catch (Exception ex) {
			Logger.e("souyue", "Imservicehelp.im_connect", "im_connect Exception", ex);
			outLog("im_connect", ex.getMessage());
		}
	}

	// @Override
	// public boolean appOnline() {
	// try {
	// if (mInstance != null && mInstance.imService != null){
	// return mInstance.imService.appOnline();
	// }
	// } catch (Exception ex){
	// outLog("isAppOnForeground", ex.getMessage());
	// }
	// return false;
	// };

	// @Override
	// public void setAppOnline(boolean status) {
	// try {
	// if (mInstance != null && mInstance.imService != null){
	// mInstance.imService.setAppOnline(status);
	// }
	// } catch (Exception ex){
	// outLog("setAppRunStatus", ex.getMessage());
	// }
	// }
	/**
	 * 私聊好友的操作
	 * 
	 * @param op
	 *            1加好友并发验证内容 2确认成为好友 3拒绝成为好友 4删除好友关系
	 * @param uid
	 *            对应的好友id
	 * @param text
	 *            附加信息
	 */
	public boolean im_userOp(int op, long uid, String nick, String avatar,
			String text) {
		try {
			if (mInstance != null && mInstance.imService != null) {
				return mInstance.imService.im_userOp(op, uid, nick, avatar,
						text,0);
			}
		} catch (Exception re) {
			outLog("im_userOp", re.getMessage());
		}
		return false;
	}


    /**
     * 私聊好友的操作
     *
     * @param op
     *            1加好友并发验证内容 2确认成为好友 3拒绝成为好友 4删除好友关系
     * @param uid
     *            对应的好友id
     * @param text
     *            附加信息
     * @param originType
     *            来源信息  0. 未知，1. 条件查找， 2. 扫描二维码， 3. 通过个人中心添加
     */
    public boolean im_userOp(int op, long uid, String nick, String avatar,
                             String text,int originType) {
        try {
            if (mInstance != null && mInstance.imService != null) {
                return mInstance.imService.im_userOp(op, uid, nick, avatar,
						text, originType);
            }
        } catch (Exception re) {
            outLog("im_userOp", re.getMessage());
        }
        return false;
    }
	/**
	 * 根据条件取网络信息
	 * 
	 * @param op
	 *            1所有群和所有好友 2所有群 3单个群 4所有好友 5指定好友
	 * @param text
	 *            当取单个群或好友时，为对应的群id或好友id
	 */
	@Override
	public boolean im_info(int op, String text) {
		try {
			if (mInstance != null && mInstance.imService != null) {
				return mInstance.imService.im_info(op, text);
			}
		} catch (Exception e) {
			outLog("im_info", e.getMessage());
		}
		return false;
	}

	/**
	 * 搜索用户
	 */
	@Override
	public boolean im_search(String keyword) {
		try {
			if (mInstance != null && mInstance.imService != null) {
				return mInstance.imService.im_search(keyword);
			}
		} catch (Exception e) {
			outLog("im_search", e.getMessage());
		}
		return false;
	}

//    private ImAidlService getIMAldlService(){
//        if (mInstance.imService != null){
//            return mInstance.imService;
//        }else {
//            initImServices();
//        }
//    }
    private void initImServices() {
        Intent intent = new Intent(mInstance, Imservice.class);
        mInstance.bindService(intent, conn, mInstance.BIND_AUTO_CREATE);
    }

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("Tuita", "service conn onServiceDisconnected " + name);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mInstance.imService = ImAidlService.Stub.asInterface(service);
            Log.i("Tuita", "tuita_service conn onServiceConnected"
                    + mInstance.imService);
        }
    };
	/**
	 * 发送消息
	 * 
	 * @param type
	 * @param uidorgid
	 * @param contentType
	 * @param content
	 * @param retry
	 * @return
	 * @throws Exception
	 */
//	@Override
	public String im_sendMessage(int type, long uidorgid, int contentType,
			String content, String retry) {
		String uuid = null;
		try {
            //入库返回uuid
            uuid=TuitaIMManager.sendInsertMsg(mInstance, getMyid(), type, uidorgid, contentType, content, retry);

            //发送广播
            Intent intent = new Intent();
            intent.putExtra("type",type);
            intent.putExtra("uidorgid",uidorgid);
            intent.putExtra("contentType",contentType);
            intent.putExtra("content",content);
            intent.putExtra("uuid",uuid);
            intent.putExtra("retry",retry != null && retry.length() > 0 ? 1 : 0);
            intent.setAction(PushService.mAction);
            intent.setPackage(mInstance.getPackageName());
            mInstance.sendBroadcast(intent);
		} catch (Exception ex) {
			Logger.e("souyue","Imservicehelp.im_sendMessage","未知异常",ex);
			outLog("im_sendMessage", ex.getMessage());
		}
		return uuid;
	}


	/**
	 * 分享事发送消息  5.0.8新增
	 *
	 * @param type
	 * @param uidorgid
	 * @param contentType
	 * @param content
	 * @param retry
	 * @return
	 * @throws Exception
	 */
//	@Override
	public String im_sendShareMessage(int type, long uidorgid, int contentType,
								 String content, String retry,long id,long fileMsgId) {
		String uuid = null;
		try {
			//入库返回uuid
			uuid=TuitaIMManager.sendInsertMsg(mInstance, getMyid(), type, uidorgid, contentType, content, retry);
			MessageHistoryDaoHelper.getInstance(mInstance).updateMsgFileId(id,fileMsgId);
			//发送广播
			Intent intent = new Intent();
			intent.putExtra("type",type);
			intent.putExtra("uidorgid",uidorgid);
			intent.putExtra("contentType",contentType);
			intent.putExtra("content",content);
			intent.putExtra("uuid",uuid);
			intent.putExtra("retry",retry != null && retry.length() > 0 ? 1 : 0);
			intent.setAction(PushService.mAction);
			intent.setPackage(mInstance.getPackageName());
			mInstance.sendBroadcast(intent);
		} catch (Exception ex) {
			Logger.e("souyue","Imservicehelp.im_sendMessage","未知异常",ex);
			outLog("im_sendMessage", ex.getMessage());
		}
		return uuid;
	}

	/**
	 * 手机联系人状态
	 */
	@Override
	public boolean im_contacts_status(String contactJson) {
		try {
			if (mInstance != null && mInstance.imService != null) {
				return mInstance.imService.im_contacts_status(contactJson);
			}
		} catch (Exception ex) {
			outLog("im_contacts_status", ex.getMessage());
		}
		return false;
	}

	/**
	 * IM下线
	 */
	@Override
	public void im_logout() {
		try {
			if (mInstance != null && mInstance.imService != null) {
				mInstance.imService.im_logout();
			}
		} catch (Exception ex) {
			outLog("im_logout", ex.getMessage());
		}
	}

	/**
	 * 赠送中搜币
	 */
	@Override
	public boolean im_giftzsb(long uid, int num) {
		try {
			if (mInstance != null && mInstance.imService != null) {
				return mInstance.imService.im_giftzsb(uid, num);
			}
		} catch (Exception ex) {
			outLog("im_giftzsb", ex.getMessage());
		}
		return false;
	}

	/**
	 * 更新各种信息
	 * 
	 * @param op
	 *            1更新昵称 2更新群里的昵称 3更新好友名称
	 * @param text
	 */
	@Override
	public boolean im_update(int op, long uid, String text) {
		try {
			if (mInstance != null && mInstance.imService != null) {
				return mInstance.imService.im_update(op, uid, text);
			}
		} catch (Exception ex) {
			outLog("im_giftzsb", ex.getMessage());
		}
		return false;
	}

	/**
	 * 新朋友
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<NewFriend> db_getNewFriend() {
		List<NewFriend> result = null;
//		if (mInstance != null && mInstance.imService != null) {
			try {
				result = new ArrayList<NewFriend>();
				result = new Gson().fromJson(
                        TuitaIMManager.db_getNewFriend(mInstance,getMyid()),
						new TypeToken<List<NewFriend>>() {
						}.getType());
			} catch (Exception e) {
				outLog("db_getNewFriend", e.getMessage());
			}
//		}
		return (List<NewFriend>) (result == null ? Collections.emptyList()
				: result);
	}

	/**
	 * 删除全部新朋友
	 */
	@Override
	public void db_clearNewFriend() {
		try {
//			if (mInstance != null && mInstance.imService != null) {
				TuitaIMManager.db_clearNewFriend(mInstance,getMyid());
//			}
		} catch (Exception e) {
			outLog("db_clearNewFriend", e.getMessage());
		}
	}

	/**
	 * 逐条删除新朋友
	 */
	@Override
	public void db_delNewFriend(long chat_id) {
		try {
//			if (mInstance != null && mInstance.imService != null) {
				TuitaIMManager.db_delNewFriend(mInstance, getMyid(), chat_id);
//			}
		} catch (Exception e) {
			outLog("db_delNewFriend", e.getMessage());
		}
	}

	/**
	 * 获取消息历史记录
	 */
	@Override
	public List<MessageHistory> db_getMessage(long chat_id, long session_order,
			int queryType) {
		List<MessageHistory> result = new ArrayList<MessageHistory>();
            result = TuitaIMManager.db_getMessage(mInstance,getMyid(),chat_id,
						session_order, queryType);

		return result;
	}

    /**
     * 根据 bubblenumber获取消息
     * @param chat_id
     * @param limitCount
     * @return
     */
    public List<MessageHistory> getMessageByLimitCount(long chat_id, int limitCount) {
        List<MessageHistory> result = new ArrayList<MessageHistory>();
        try {
            result = TuitaIMManager.getMessageByLimitCount(mInstance,getMyid(),chat_id, limitCount);
        } catch (Exception ex) {
            outLog("getMessageByLimitCount", ex.getMessage());
        }
        return result;
    }

	/**
	 * 单个会话删除全部记录
	 */
	@Override
	public void db_clearMessageHistory(long chat_id,int chat_type) {
		try {
//			if (mInstance != null && mInstance.imService != null) {
            TuitaIMManager.db_clearMessageHistory(mInstance,getMyid(),chat_id,chat_type);
//			}
		} catch (Exception ex) {
			outLog("db_clearMessageHistory", ex.getMessage());
		}
	}

	/**
	 * 删除私聊选中记录
	 */
	@Override
	public void db_deleteSelectedMessageHistory(long chat_id, String uuid) {
		try {
//			if (mInstance != null && mInstance.imService != null) {
            TuitaIMManager.db_deleteSelectedMessageHistory(mInstance, getMyid(), chat_id, uuid);
//			}
		} catch (Exception ex) {
			outLog("db_deleteSelectedMessageHistory", ex.getMessage());
		}
	}


    /**
     * 新增删除  私聊 群聊 搜小悦。。。  等所有类型选中记录 zcz
     */
    public void db_deleteSelectedAllTypeMessageHistory(long chat_id, String uuid,int chat_type) {
        try {
//			if (mInstance != null && mInstance.imService != null) {
            TuitaIMManager.db_deleteSelectedAllTypeMessageHistory(mInstance, getMyid(), chat_id, uuid, chat_type);
//			}
        } catch (Exception ex) {
            outLog("db_deleteSelectedMessageHistory", ex.getMessage());
        }
    }


	/**
	 * 最近聊天
	 * 
	 * @return
	 */
	@Override
	public List<MessageRecent> db_getMessageRecent() {
		List<MessageRecent> result = new ArrayList<MessageRecent>();
		try {
            result = TuitaIMManager.db_getMessageRecent(mInstance,getMyid());
		} catch (Exception ex) {
			outLog("db_getMessageRecent", ex.getMessage());
		}
		return result;
	}

    /**
     * 最近聊天(除去联系人)
     *
     * @return
     */
    public List<MessageRecent> db_getMsgRecent() {
        List<MessageRecent> result = new ArrayList<MessageRecent>();
        try {
            String json = TuitaIMManager.db_getMsgRecent(mInstance,getMyid());
            if (json != null) {
                result = new Gson().fromJson(json,
                        new TypeToken<List<MessageRecent>>() {
                        }.getType());
            }
        } catch (Exception ex) {
            outLog("db_getMessageRecent", ex.getMessage());
        }
        return result;
    }

	/**
	 * 最近聊天列表(除去联系人)
	 *
	 * @return
	 */
	public JSONArray db_getMsgRecentList() {
		List<MessageRecent> result = new ArrayList<MessageRecent>();
		JSONArray jsonArray=null;
		try {
			jsonArray = TuitaIMManager.db_getMsgRecentList(mInstance,getMyid());
		} catch (Exception ex) {
			outLog("db_getMessageRecent", ex.getMessage());
		}
		return jsonArray;
	}

    /**
     *
     * @param uuid
     * @return
     * by zhangwb
     */
    @Override
    public ServiceMessage db_getServiceMessageMessage(String uuid) {
        ServiceMessage service = null;
                service = TuitaIMManager.db_getServiceMessageMessage(mInstance,uuid);
        return service;
    }

    /**
	 * 根据id删除最近会话
	 */
	@Override
	public void db_delMessageRecent(long chat_id) {
		try {
				TuitaIMManager.db_delMessageRecent(mInstance,getMyid(),chat_id);
		} catch (Exception e) {
			outLog("db_delMessageRecent", e.getMessage());
		}
	}

    /**
     *置顶相关操作
     */

    public void db_ToTopMessageRecent(long chat_id,String by3) {
        try {
            TuitaIMManager.db_ToTopMessageRecent(mInstance,getMyid(),chat_id,by3);
        } catch (Exception e) {
            outLog("db_ToTopMessageRecent", e.getMessage());
        }
    }

	/**
	 * 获取联系人
	 * 
	 * @return
	 */
	@Override
	public List<Contact> db_getContact() {
		List<Contact> result = null;
//		if (mInstance != null && mInstance.imService != null) {
			try {
//				result = new ArrayList<Contact>();
//				result = new Gson().fromJson(
//						TuitaIMManager.db_getContact(mInstance,getMyid()),
//						new TypeToken<List<Contact>>() {
//						}.getType());

                //直接转list,不再来回倒数据
                result = TuitaIMManager.db_getContacts(mInstance, getMyid());
			} catch (Exception e) {
				outLog("db_getContact", e.getMessage());
			}
//		}
		return result;
	}

	/**
	 * 清除好友气泡
	 */
	@Override
	public void db_clearFriendBubble() {
		try {
//			if (mInstance != null && mInstance.imService != null) {
            TuitaIMManager.db_clearFriendBubble(mInstance,getMyid());
//			}
		} catch (Exception ex) {
			outLog("db_clearFriendBubble", ex.getMessage());
		}
	}

    /**
     * 清除Cate气泡
     */
    public void clearCateBubble(long cateId) {
            TuitaIMManager.dbClearCateBubble(mInstance,getMyid(),cateId);
    }

	/**
	 * 清除最近联系人气泡
	 */
	@Override
	public void db_clearMessageRecentBubble(long chat_id) {
            TuitaIMManager.db_clearMessageRecentBubble(mInstance,getMyid(),chat_id);
	}

	/**
	 * 获取气泡，取不同记录
	 */
	@Override
	public Config db_getConfig() {
		Config config = null;
//		if (mInstance != null && mInstance.imService != null) {
			try {
				config = new Gson().fromJson(
						TuitaIMManager.db_getConfig(mInstance,getMyid()), Config.class);
			} catch (Exception e) {
				outLog("db_getConfig", e.getMessage());
			}
//		}
		return config;
	}

	/**
	 * 获取某个联系人
	 */
	@Override
	public Contact db_getContactById(long chat_id) {
		Contact contact = null;
				contact = TuitaIMManager.db_getContactByid(mInstance,getMyid(),chat_id);
		return contact;
	}

	@Override
	public boolean im_contacts_upload(String contactJson) {

		try {
			if (mInstance != null && mInstance.imService != null) {
				return mInstance.imService.im_contacts_upload(contactJson);
			}
		} catch (Exception ex) {
			outLog("im_contacts_upload", ex.getMessage());
		}
		return false;
	}

	@Override
	public void db_updateMessageHistoryTime(String uuid, int content_type,
			long chat_id, long currentTime) {
		try {
//			if (mInstance != null && mInstance.imService != null) {
				TuitaIMManager.db_updateMessageHistoryTime(mInstance,getMyid(),uuid,
						content_type, chat_id, currentTime);
//			}
		} catch (Exception ex) {
			outLog("db_clearMessageRecentBubble", ex.getMessage());
		}

	}

	@Override
	public void updateStatus(String uuid,
							 int content_type, long chat_id, int isRead) {
		try {
				TuitaIMManager.updateStatus(mInstance,getMyid(),uuid,
						content_type, chat_id, isRead);
		} catch (Exception ex) {
			outLog("updateStatus", ex.getMessage());
		}
	}

    @Override
    public GroupMembers db_findMemberListByGroupidandUid(long groupid, long userid) {
		GroupMembers members = null;
        try {
//            if (mInstance != null && mInstance.imService != null) {
                members = TuitaIMManager.db_findMemberListByGroupidandUid(mInstance,getMyid(),groupid,userid);
//            }
        } catch (Exception ex) {
            outLog("db_findMemberListByGroupidandUid", ex.getMessage());
        }
        return  members;
    }

    @Override
    public List<GroupMembers> db_findMemberListByGroupid(long group_id) {
		List<GroupMembers> members = null;
        try {
//            if (mInstance != null && mInstance.imService != null) {
            if (mInstance != null) {
//                members = mInstance.imService.db_findMemberListByGroupid(group_id);
                members = TuitaIMManager.db_findMemberListByGroupid(mInstance,getMyid(),group_id);
            }
        } catch (Exception ex) {
            outLog("de_findMemberListByGroupid", ex.getMessage());
        }
        return  members;
    }

    @Override
    public String db_findGroupListByGroupidAndIsSaved(long group_id, int is_saved) {
        String group = null;
        try {
//            if (mInstance != null && mInstance.imService != null) {
                if (mInstance != null ) {
                group = TuitaIMManager.db_findGroupListByGroupidAndIsSaved(mInstance, group_id, is_saved);
            }
        } catch (Exception ex) {
            outLog("db_findGroupListByGroupidAndIsSaved", ex.getMessage());
        }
        return  group;
    }

    @Override
    public String db_findGroupListByUserid(long group_id) {
        String group = null;
        try {
            if (mInstance != null ) {
                group = TuitaIMManager.db_findGroupListByUserid(mInstance, group_id);
            }
        } catch (Exception ex) {
            outLog("db_findGroupListByUserid", ex.getMessage());
        }
        return  group;
    }


    @Override
	public String im_saveMessage(int type, long uidorgid, int contentType,
			String content, String retry) {
		String uuid = null;
		try {
			if (mInstance != null && mInstance.imService != null) {
				uuid = mInstance.imService.im_saveMessage(type, uidorgid,
						contentType, content, retry);
			}
		} catch (Exception ex) {
			outLog("im_saveMessage", ex.getMessage());
		}
		return uuid;
	}

	/**
	 * 建群  op 1  系统消息101
	 */
	@Override
	public boolean newGroupOp(int op, List uids) {
		try {
			if (mInstance != null && mInstance.imService != null) {
				return mInstance.imService.newGroupOp(op, uids);
			}
		} catch (Exception ex) {
			outLog("newGroupOp", ex.getMessage());
		}
		return false;
	}

	/**
	 * 加好友入群  op 2   系统消息101
	 * 删除群好友  op 3    系统消息102
	 */
	@Override
	public boolean addOrDeleteGroupMembersOp(int op, String gid, List uids) {
		try {
			if (mInstance != null && mInstance.imService != null) {
				return mInstance.imService.addOrDeleteGroupMembersOp(op, gid, uids);
			}
		} catch (Exception ex) {
			outLog("addGroupMembersOp", ex.getMessage());
		}
		return false;
	}

	/**
	 * 退群  op 4  系统消息103
	 */
	@Override
	public boolean retreatGroupMembersOp(int op, String gid, String nextOwnerId) {
		try {
			if (mInstance != null && mInstance.imService != null) {
				return mInstance.imService.retreatGroupMembersOp(op, gid, nextOwnerId);
			}
		} catch (Exception ex) {
			outLog("retreatGroupMembersOp", ex.getMessage());
		}
		return false;
	}

	/**
	 * 保存设置信息  op 5  
	 */
	@Override
	public boolean saveGroupConfigOp(int op, String gid, boolean isGroupSaved,
			boolean isNewsNotifyShielded) {
		try {
			if (mInstance != null && mInstance.imService != null) {
				return mInstance.imService.saveGroupConfigOp(op, gid,isGroupSaved,isNewsNotifyShielded);
			}
		} catch (Exception ex) {
			outLog("saveGroupConfigOp", ex.getMessage());
		}
		return false;
	}
	/**
	 * 修改群昵称  op 6  系统消息104
	 * 
	 * 修改群成员昵称 op 7 系统消息105
	 */
	@Override
	public boolean updateGroupNickNameOp(int op, String gid, String nick) {
		try {
			if (mInstance != null && mInstance.imService != null) {
				return mInstance.imService.updateGroupNickNameOp(op, gid, nick);
			}
		} catch (Exception ex) {
			outLog("updateGroupNickNameOp", ex.getMessage());
		}
		return false;
	}

	/**
	 * 获取群详细信息  op 9
	 * 
	 */
	@Override
	public boolean getGroupDetailsOp(int op, String gid) {
		try {
			if (mInstance != null && mInstance.imService != null) {
				return mInstance.imService.getGroupDetailsOp(op, gid);
			}
		} catch (Exception ex) {
			outLog("getGroupDetailsOp", ex.getMessage());
		}
		return false;
	}

    /**
     * 根据群id和userid查询群成员（member）
     */
    @Override
    public Group db_updateGroup(long group_id) {
		Group groups = null;
        try {
            if (mInstance != null ) {
                groups = TuitaIMManager.db_updateGroup(mInstance,getMyid(),group_id);
            }
        } catch (Exception ex) {
            outLog("db_updateGroup", ex.getMessage());
        }
        return  groups;
    }

	@Override
	public List<ServiceMessage> db_getServiceMsg(long cateId) {
		List<ServiceMessage> result = null;
//		if (mInstance != null && mInstance.imService != null) {
			try {
				result = new ArrayList<ServiceMessage>();
				result = new Gson().fromJson(
						TuitaIMManager.db_getServiceMsg(mInstance,getMyid(),cateId),
						new TypeToken<List<ServiceMessage>>() {
						}.getType());
			} catch (Exception e) {
				outLog("db_getServiceMsg", e.getMessage());
			}
//		}
		return (List<ServiceMessage>) (result == null ? Collections.emptyList()
				: result);
	}
//    @Override
//    public String db_findGroupMemberByMemberid(long memberid) {
//        String groupmember = null;
//        try {
//              groupmember = TuitaIMManager.db_findGroupMemberByMemberid(mInstance,getMyid(),memberid);
//        } catch (Exception ex) {
//            outLog("db_updateGroup", ex.getMessage());
//        }
//        return  groupmember;
//    }

    @Override
    public boolean addGroupMemberOp(int op, String gid, String inviterId, int mode, List uids,String source) {
        try {
            if (mInstance != null && mInstance.imService != null) {
                return mInstance.imService.addGroupMemberOp(op, gid, inviterId,mode,uids,source);
            }
        } catch (Exception ex) {
            outLog("addGroupMemberOp", ex.getMessage());
        }
        return false;
    }

    /**
     * //删除群聊历史
     * @param chat_id
     * @param uuid
     */
    @Override
    public void db_deleteGroupSelectedMessageHistory(long chat_id, String uuid) {
        try {
               TuitaIMManager.db_deleteGroupSelectedMessageHistory(mInstance,getMyid(),chat_id,uuid);
        } catch (Exception ex) {
            outLog("db_deleteGroupSelectedMessageHistory", ex.getMessage());
        }
    }

    @Override
    public void db_deleteMemberById(long member_id,long gid) {
        try {
               TuitaIMManager.db_deleteMemberById(mInstance, getMyid(), member_id, gid);
        } catch (Exception ex) {
            outLog("db_deleteMemberById", ex.getMessage());
        }
    }

    @Override
    public void db_updateCommentName(long chat_id, String commentName) {
        try {
                TuitaIMManager.db_updateCommentName(mInstance, getMyid(), chat_id, commentName);
        } catch (Exception ex) {
            outLog("db_updateCommentName", ex.getMessage());
        }
    }

    @Override
    public Group db_findGourp(long group_id) {
        Group group = null;
               group = TuitaIMManager.db_findGourp(mInstance,getMyid(),group_id);
        return  group;
    }

    public void db_updateRecent(MessageRecent msg) {
        try {
                TuitaIMManager.db_updateRecent(mInstance,msg);
        } catch (Exception ex) {
            outLog("db_updateRecent", ex.getMessage());
        }
    }

    @Override
    public void db_updateRecentBy1(long chatid, long myid, String num) {
        try {
             TuitaIMManager.db_updateRecentBy1(mInstance, chatid, myid, num);
        } catch (Exception ex) {
            outLog("db_updateRecentBy1", ex.getMessage());
        }
    }

    public void db_updateRecentTime(long chatid,long myid,long time){
        try {
            TuitaIMManager.db_updateRecentTime(mInstance, chatid, myid, time);
        } catch (Exception ex) {
            outLog("db_updateRecentTime", ex.getMessage());
        }
    }

    @Override
    public List<ServiceMessageRecent> db_getServiceMessageByMyid() {
        List<ServiceMessageRecent> result = null;
//        if (mInstance != null && mInstance.imService != null) {
            try {
                result = new ArrayList<ServiceMessageRecent>();
                result = new Gson().fromJson(
                       TuitaIMManager.db_getServiceMessageByMyid(mInstance,getMyid()),
                        new TypeToken<List<ServiceMessageRecent>>() {
                        }.getType());
            } catch (Exception e) {
                outLog("db_getServiceMessageByMyid", e.getMessage());
            }
//        }
        return (List<ServiceMessageRecent>) (result == null ? Collections.emptyList()
                : result);
    }



    @Override
	public List<ServiceMessage> db_getServiceMsgByServiceid(long cateId,
			long serviceMsgId,int queryType,long session_order) {
		List<ServiceMessage> result = null;
//		if (mInstance != null && mInstance.imService != null) {
			try {
				result = new ArrayList<ServiceMessage>();
				result = new Gson().fromJson(
						TuitaIMManager.db_getServiceMsgByServiceid(mInstance, getMyid(), cateId, serviceMsgId),
						new TypeToken<List<ServiceMessage>>() {
						}.getType());
			} catch (Exception e) {
				outLog("db_getServiceMsgByServiceid", e.getMessage());
			}
//		}
		return (List<ServiceMessage>) (result == null ? Collections.emptyList()
				: result);
	}

	@Override
	public List<ServiceMessageRecent> db_getServiceMsgRe(long cateId) {
		List<ServiceMessageRecent> result = null;
			try {
                result = TuitaIMManager.db_getServiceMsgRe(mInstance, getMyid(), cateId);
			} catch (Exception e) {
				outLog("db_getServiceMsgRe", e.getMessage());
			}
		return (List<ServiceMessageRecent>) (result == null ? Collections.emptyList()
				: result);
	}

	@Override
	public void db_clearSouyueMessageRecentBubble(long serviceId,long cateId) {
		try {
//			if (mInstance != null && mInstance.imService != null) {
				TuitaIMManager.db_clearSouyueMessageRecentBubble(mInstance,getMyid(),serviceId,cateId);
//			}
		} catch (Exception ex) {
			outLog("db_clearSouyueMessageRecentBubble", ex.getMessage());
		}
	}

	@Override
	public boolean getUserOp(int op, long uid) {
		try {
			if (mInstance != null && mInstance.imService != null) {
				return mInstance.imService.getUserOp(op, uid);
			}
		} catch (Exception ex) {
			outLog("getUserOp", ex.getMessage());
		}
		return false;
	}

	@Override
	public void db_updateSouyueMessageRecentBubble(long chat_id, long service_id) {
		try {
//			if (mInstance != null && mInstance.imService != null) {
				TuitaIMManager.db_updateSouyueMessageRecentBubble(mInstance,getMyid(),chat_id,service_id);
//			}
		} catch (Exception ex) {
			outLog("db_updateSouyueMessageRecentBubble", ex.getMessage());
		}
	}



    /**
     * 系统赠送中搜币
     */
    // @Override
    // public boolean im_chargezsb() {
    // try {
    // if (mInstance != null && mInstance.imService != null) {
    // return mInstance.imService.im_chargezsb();
    // }
    // } catch (Exception e) {
    // outLog("im_chargezsb", e.getMessage());
    // }
    // return false;
    // }

    @Override
    public long db_findMemberCountByGroupid(long group_id) {
        try {
              return TuitaIMManager.db_findMemberCountByGroupid(mInstance, group_id, getMyid());
        } catch (Exception ex) {
            outLog("db_findMemberCountByGroupid", ex.getMessage());
        }
        return 0l;
    }

    /**
     * 获取某个群的所有信息
     * @param op
     * @param group_id
     * @return
     */
    @Override
    public boolean findGroupInfo(int op, long group_id, List memberid) {
        try {
            if (mInstance != null && mInstance.imService != null) {
                return mInstance.imService.findGroupInfo(op, group_id, memberid);
            }
        } catch (Exception ex) {
            outLog("findGroupInfo", ex.getMessage());
        }
        return false;
    }

	/**
	 * 获取群成员详细信息
	 * @param op  9
	 * @param group_id
	 * @param memberId
	 * @return
	 */
	@Override
	public boolean getMemberDetail(int op, long group_id, long memberId) {
		try {
			if (mInstance != null && mInstance.imService != null) {
				return mInstance.imService.getMemberDetail(op, group_id, memberId);
			}
		} catch (Exception ex) {
			outLog("getMemberDetail", ex.getMessage());
		}
		return false;
	}


    public String[] db_getFinalName(long gid,long memberId){
		try {
			return TuitaIMManager.db_getFinalName(mInstance, getMyid(),gid,memberId);
		} catch (Exception ex) {
			outLog("db_getFinalName", ex.getMessage());
		}
		return null;
	}

    @Override
    public void db_insertDraft(long chatid, String draftConent) {
        try {
            if (mInstance != null && mInstance.imService != null) {
                TuitaIMManager.db_insertDraftContent(mInstance, getMyid(), chatid,draftConent);
            }
        } catch (Exception ex) {
            outLog("db_insertDraft", ex.getMessage());
        }
    }

    @Override
    public void db_insertDraftForAt(long chatid, String draftContent, String draftForAtContent) {
        try {
            if (mInstance != null && mInstance.imService != null) {
                TuitaIMManager.db_insertDraftForAtContent(mInstance, getMyid(), chatid, draftContent, draftForAtContent);
            }
        } catch (Exception ex) {
            outLog("db_insertDraftForAt", ex.getMessage());
        }
    }

    @Override
    public MessageRecent db_findMessageRecent(long chatid) {
        MessageRecent messageRecent = null;
                messageRecent = TuitaIMManager.db_getMessageRecentByMyid(mInstance,getMyid(),chatid);
        return messageRecent;
    }

    @Override
    public boolean updateNewsNotify(int op, long uid, boolean is_news_notify) {
        try {
            if (mInstance != null && mInstance.imService != null) {
                return mInstance.imService.updateNewsNotify(op, uid,is_news_notify);
            }
        } catch (Exception ex) {
            outLog("updateNewsNotify", ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean saveServiceMsgNotify(long srvId, boolean isNewsNotifyShielded) {
        try {
            if (mInstance != null && mInstance.imService != null) {
                return mInstance.imService.saveServiceMsgNotify(srvId, isNewsNotifyShielded);
            }
        } catch (Exception ex) {
            outLog("saveServiceMsgNotify", ex.getMessage());
        }
        return false;
    }

	/**
	 * 调用AIDL清除通知栏的方法
	 * @param id
	 */
	@Override
	public void cancelNotify(int id) {
		try {
			if(mInstance.imService!=null){//解决当搜悦被系统杀死时，imService为空的情况
				mInstance.imService.cancelNotify(id);
			}else{//解决当程序杀死时，  通知栏计数不能清空的问题
				Log.i("notifyCation" ,"//程序杀死，  通知栏计数不能清空");
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到当前通知栏两个或两个联系人以上的数目一区分清不清通知栏
	 * @param id
	 */
	@Override
	public int getNotifyNum(int id) {
		try {
			if(mInstance.imService!=null){//解决当搜悦被系统杀死时，imService为空的情况
				return mInstance.imService.getNotifyNum(id);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return 0;
	}

	/**
     * 初始化imservice中的TuitaIMManager
     */
    @Override
    public void initImService() {
        if (mInstance != null && mInstance.imService != null) {
            try {
                mInstance.imService.initImService();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Contact> db_findLike(String localOrder){
		List<Contact> contactList = null;
			try {
				contactList = TuitaIMManager.db_findLike(mInstance,getMyid(),localOrder);
			} catch (Exception e) {
				outLog("db_findLike", e.getMessage());
			}
		return contactList;
	}

    public List<SearchMsgResult> db_find_search_all(String localOrder){
        List<SearchMsgResult> list = null;
        try {
            list = TuitaIMManager.db_find_search_all(mInstance, getMyid(), localOrder);
        } catch (Exception e) {
            outLog("db_findLike", e.getMessage());
        }
        return list;
    }

    public List<SearchMsgResult> db_find_search_contact_detail(String localOrder){
        List<SearchMsgResult> list = null;
        try {
            list = TuitaIMManager.db_find_search_contact_detail(mInstance, getMyid(), localOrder);
        } catch (Exception e) {
            outLog("db_findLike", e.getMessage());
        }
        return list;
    }

    public List<SearchMsgResult> db_find_search_group_detail(String localOrder){
        List<SearchMsgResult> list = null;
        try {
            list = TuitaIMManager.db_find_search_group_detail(mInstance, getMyid(), localOrder);
        } catch (Exception e) {
            outLog("db_findLike", e.getMessage());
        }
        return list;
    }


    public List<Contact> db_findLikeOnlyContact(String localOrder){
        List<Contact> contactList = null;
        try {
            contactList = TuitaIMManager.db_findLikeOnlyContact(mInstance, getMyid(), localOrder);
        } catch (Exception e) {
            outLog("db_findLike", e.getMessage());
        }
        return contactList;
    }

	public ServiceMessageRecent db_getServiceMessageRecent(long serviceId,long cateId){
		ServiceMessageRecent serviceMessageRecent = null;
		try {
			serviceMessageRecent = TuitaIMManager.db_getServiceMessageRecent(mInstance, getMyid(), serviceId, cateId);
		} catch (Exception e) {
			outLog("db_getServiceMessageRecent", e.getMessage());
		}
		return serviceMessageRecent;
	}

    /**
     * 查找目标用户的服务号
     * @param serviceId
     * @return
     */
    public ServiceMessageRecent db_getTargetServiceMsgRe(long serviceId){
        ServiceMessageRecent serviceMessageRecent = null;
            serviceMessageRecent = TuitaIMManager.db_getTargetServiceMsgRe(mInstance, getMyid(), serviceId);
        return serviceMessageRecent;
    }

    /**
     *
     * @param cateId
     * @return
     */
    public Cate getCate(long cateId){
       return TuitaIMManager.findCate(mInstance,getMyid(),cateId);
    }


    /**
     * 删除列表页数据
     * @param chatType
     * @param chatId
     */
    public void deleteMessageRecent(int chatType,long chatId){
        MessageRecentDaoHelper.getInstance(mInstance).delete(getMyid(),chatId,chatType);
    }

    /**
     * 查找所有文件
     * @return
     */
    public List<MessageFile> getAllFiles(){
        return MessageFileDaoHelper.getInstance(mInstance).selectAll();
    }

	/**
	 * 查找所有已经下载完成的文件
	 * @return
	 */
	public List<MessageFile> getAllDownLoadFiles(){
		return MessageFileDaoHelper.getInstance(mInstance).selectAllDownLoadFile();
	}

    /**
     * 删除MessagefiLe中数据
     * @param _fileId
     */
    public void deleteFile(long _fileId){
        MessageFileDaoHelper.getInstance(mInstance).delete(_fileId);
    }
	/**
	 * 更新基本信息
	 * 消息免打扰的三个数据
	 * op 6
	 * by qubian
	 */
	public boolean setUserMessageNotify(int op, long uid, boolean isNewsNotifyShielded ,boolean hasSound,boolean isVibrate) {
		try {
			if (mInstance != null && mInstance.imService != null) {
				return mInstance.imService.setUserMessageNotify(op,uid, isNewsNotifyShielded, hasSound, isVibrate);
			}
		} catch (Exception ex) {
			outLog("getGroupDetailsOp", ex.getMessage());
		}
		return false;
	}
}
