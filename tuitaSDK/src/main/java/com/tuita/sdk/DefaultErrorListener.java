package com.tuita.sdk;



import com.tuita.sdk.TuitaSDKManager.ErrorListener;
import com.tuita.sdk.TuitaSDKManager.Operations;
import com.zhongsou.souyue.log.Logger;

class DefaultErrorListener implements ErrorListener {

	private TuitaSDKManager manager;

	protected DefaultErrorListener(TuitaSDKManager manager) {
		this.manager = manager;
	}
	
	@Override
	public boolean error(TuitaConnection connection, Throwable e, Operations op, String message) {
		switch (op) {
			case PACK_READ:
				if (e instanceof java.net.SocketException) {
                    //重连
                    manager.setTuitaState(TuitaSDKManager.CONN_STATE_NOTCONNECT);

                    manager.reConnectIM();
					if (e.getMessage().equals("Socket closed")) {
						e.printStackTrace();
						Log.i(TuitaSDKManager.TAG, "read socket closed");
						Logger.e("tuita","DefaultErrorListener.error", "read socket closed", e);
					} else if (e.getMessage().equals("Connection time out")) {
						Log.i(TuitaSDKManager.TAG, "connect time out");
						Logger.e("tuita","DefaultErrorListener.error", "connect time out", e);
					} else {
						Log.i(TuitaSDKManager.TAG, "other socket exception", e);
						Logger.e("tuita","DefaultErrorListener.error", "other socket exception", e);
					}
				} else {
					Log.i(TuitaSDKManager.TAG, message + ",op:" + op, e);
					Logger.e("tuita","DefaultErrorListener.error",message + ",op:" + op, e);
				}
				break;
			default:
				Log.i(TuitaSDKManager.TAG, message + ",op:" + op, e);
				Logger.e("tuita","DefaultErrorListener.error",message + ",op:" + op, e);
				break;
		}
		return true;
	}

}
