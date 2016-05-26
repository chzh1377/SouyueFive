package com.zhongsou.souyue.im.search;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

public class ParseUtils {
	/**
	 * 解析搜索消息的结果
	 * 
	 * @param result
	 * @return
	 * @throws Exception
	 */
	public static MsgResult parseMessage(byte[] result) throws ParserException,
			Exception {
		MsgResult msgResult = null;
		if (result != null && result.length > 0 && result.length % 4 == 0) {
			byte[] buffer = new byte[4];
			ByteArrayInputStream byteBuffer = new ByteArrayInputStream(result);
			int readLen = byteBuffer.read(buffer);
			if (readLen == 4) {
				msgResult = new MsgResult();
				msgResult.count = ByteUtils.byteToInt(buffer);
				int count = msgResult.count;
				msgResult.msgIds = new ArrayList<Integer>();
				readLen = byteBuffer.read(buffer);
				count--;
				while (readLen == 4 && count >= 0) {
					msgResult.msgIds.add(ByteUtils.byteToInt(buffer));
					readLen = byteBuffer.read(buffer);
					count--;
				}
			} else {
				throw new ParserException();
			}

		} else {
			throw new ParserException();
		}
		return msgResult;
	}

	/**
	 * 搜索列表解析
	 * 
	 * @param result
	 * @return
	 * @throws Exception
	 */
	public static ListResult parseList(byte[] result) throws ParserException,
			Exception {
		ListResult listResult = null;
		if (result != null && result.length > 0) {
			int count = 0;
			byte[] buffer = new byte[4];
			ByteArrayInputStream byteArrayBuffer = new ByteArrayInputStream(
					result);
			int readLen = byteArrayBuffer.read(buffer);
			if (readLen == 4) {
				listResult = new ListResult();
				listResult.count = ByteUtils.byteToInt(buffer);
				count = listResult.count;
				listResult.setSessionList(new ArrayList<Session>());
			} else {
				throw new ParserException();
			}
			

			buffer = new byte[20];
			readLen = byteArrayBuffer.read(buffer);
			

			ByteArrayInputStream itemArrayBuffer = null;
			byte[] itemBuffer = null;
			Session session = null;

			while (readLen == 20 && count >0) {
				count--;
				session = new Session();
				itemArrayBuffer = new ByteArrayInputStream(buffer);

				itemBuffer = new byte[4];
				itemArrayBuffer.read(itemBuffer);
				session.sessionType = ByteUtils.byte2int(itemBuffer);

				itemBuffer = new byte[8];
				itemArrayBuffer.read(itemBuffer);
				session.sessionId = ByteUtils.byteToLong(itemBuffer);

				itemBuffer = new byte[4];
				itemArrayBuffer.read(itemBuffer);
				session.msgId = ByteUtils.byteToInt(itemBuffer);

				itemBuffer = new byte[4];
				itemArrayBuffer.read(itemBuffer);
				session.count = ByteUtils.byteToInt(itemBuffer);

				listResult.getSessionList().add(session);
				readLen = byteArrayBuffer.read(buffer);
				
				Log.v("log", "read len:" + readLen);
			}

			if (byteArrayBuffer != null) {
				byteArrayBuffer.close();
			}
			if (itemArrayBuffer != null) {
				itemArrayBuffer.close();
			}
		} else {
			throw new ParserException();
		}

		return listResult;
	}

	public static void testParseList(Context context) {
		try {
			InputStream fileStream = context.getAssets().open("output.dat");
			byte[] buffer = new byte[fileStream.available()];
			fileStream.read(buffer);
			ListResult listResult = parseList(buffer);
			if (listResult != null) {
				Log.v("log", "list count:" + listResult.count);
				for (Session session : listResult.getSessionList()) {
					Log.v("log", "item:" + session.sessionType + ":"
							+ session.sessionId + ":" + session.msgId + ":"
							+ session.count);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void testParseMessage(Context context) {
		try {
			InputStream fileStream = context.getAssets().open(
					"query_message.dat");
			byte[] buffer = new byte[fileStream.available()];
			fileStream.read(buffer);
			MsgResult msgResult = parseMessage(buffer);
			if (msgResult != null) {
				Log.v("log", "list count:" + msgResult.count);
				for (int msgId : msgResult.msgIds) {
					Log.v("log", "msg id:" + msgId);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void testMoreMessage(Context context, int start, int offset) {
		try {
			InputStream fileStream = context.getAssets().open("msgs.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fileStream));
			String line = br.readLine();
			int num = 0;
			Gson gson = new Gson();
			MsgInfo msgInfo = null;
			Log.v("log", "start add");
			while (line != null && num < start + offset) {
				if (num >= start) {
					msgInfo = gson.fromJson(line, MsgInfo.class);
					num++;
					int code = SearchUtils.addMessage(msgInfo.msg,
							(short) msgInfo.sessionType, msgInfo.sessionId,
							msgInfo.msgId);
					if(num%100==0){
						Log.v("log", "code:"+code);
					}
					line = br.readLine();
				}

			}
			Log.v("log", "end add");
//			SearchUtils.saveIndex();
//			SearchUtils.loadIndex();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
