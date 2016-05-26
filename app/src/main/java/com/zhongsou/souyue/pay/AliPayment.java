package com.zhongsou.souyue.pay;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import com.alipay.android.app.IAlixPay;
import com.alipay.android.app.IRemoteServiceCallback;
import com.alipay.sdk.app.PayTask;

public class AliPayment {

//	Integer lock = 0;
//
//	IAlixPay mAlixPay = null;
//
//	boolean paying = false;
//
//	Activity payActivity = null;
//
	
	
//	// 和安全支付服务建立连接
//	private ServiceConnection mAlixPayConnection = instantConnection();
//
//    private ServiceConnection instantConnection() {
//        return new ServiceConnection() {
//
//            public void onServiceConnected(ComponentName className, IBinder service) {
//                //
//                // wake up the binder to continue.
//                // 获得通信通道
//                synchronized (lock) {
//                    mAlixPay = IAlixPay.Stub.asInterface(service);
//                    lock.notify();
//                }
//            }
//
//            public void onServiceDisconnected(ComponentName className) {
//                mAlixPay = null;
//            }
//        };
//    }

	 
//	public boolean pay(final String  payInfo, final Handler callback, Activity activity) {
//		if (paying)
//			return false;
//		paying = true;
//
//
//		payActivity = activity;
//		MobileSecurePayHelper payHelper = new MobileSecurePayHelper(payActivity);
//		if(!payHelper.detectMobile_sp()){
//			return false;
//		}
//
//		if (mAlixPay == null) {
//			// 绑定安全支付服务需要获取上下文环境，
//			// 如果绑定不成功使用mActivity.getApplicationContext().bindService
//			// 解绑时同理
//            mAlixPayConnection = instantConnection();
//			activity.getApplicationContext().bindService(
//                    new Intent(IAlixPay.class.getName()), mAlixPayConnection,
//                    Context.BIND_AUTO_CREATE);
//		}
//
//		// 实例一个线程来进行支付
//				new Thread(new Runnable() {
//					public void run() {
//						try {
//							// wait for the service bind operation to completely
//							// finished.
//							// Note: this is important,otherwise the next mAlixPay.Pay()
//							// will fail.
//							// 等待安全支付服务绑定操作结束
//							// 注意：这里很重要，否则mAlixPay.Pay()方法会失败
//							synchronized (lock) {
//								if (mAlixPay == null)
//									lock.wait();
//							}
//
//							// register a Callback for the service.
//							// 为安全支付服务注册一个回调
//							mAlixPay.registerCallback(mCallback);
//
//							// call the MobileSecurePay service.
//							// 调用安全支付服务的pay方法
//							String strRet = mAlixPay.Pay(payInfo);
//
//							// set the flag to indicate that we have finished.
//							// unregister the Callback, and unbind the service.
//							// 将mbPaying置为false，表示支付结束
//							// 移除回调的注册，解绑安全支付服务
//							paying = false;
//							mAlixPay.unregisterCallback(mCallback);
//							payActivity.getApplicationContext().unbindService(
//									mAlixPayConnection);
//
//							// send the result back to caller.
//							// 发送交易结果
//							Message msg = new Message();
//							msg.obj = strRet;
//							callback.sendMessage(msg);
//						} catch (Exception e) {
//							e.printStackTrace();
//
//							// send the result back to caller.
//							// 发送交易结果
//							Message msg = new Message();
//							callback.sendMessage(msg);
//						} finally {
//                            paying = false;
//                            try {
//                                mAlixPay.unregisterCallback(mCallback);
//                                mAlixPay = null;
//                            } catch (RemoteException e) {
//                                e.printStackTrace();
//                            }
//                            try {
//                                payActivity.getApplicationContext().unbindService(
//                                        mAlixPayConnection);
//                            } catch (Exception e){}
//
//                        }
//                    }
//				}).start();
//		return true;
//	}

	/**
	 * 调用支付宝进行支付，使用最新jar包 - YanBin - 20160118
	 *
	 * @param activity 操作activity
	 * @param payInfo 订单信息
	 * @param handler 回调的Handler
	 */
	public void payByAli(final Activity activity, final String  payInfo, final Handler handler) {

		Runnable payRunnable = new Runnable() {
			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(activity);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo, true);

				Log.d(activity.getClass().getSimpleName(), "payInfo = " + payInfo);

				Message msg = new Message();
				msg.what = Constant.SDK_PAY_FLAG;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}


	/**
	 * 查询终端设备是否存在支付宝认证账户 - YanBin
	 * @param activity 操作activity
	 * @param handler 回调的Handler
	 */
	public void check(final Activity activity, final Handler handler) {
		Runnable checkRunnable = new Runnable() {
			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(activity);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = Constant.SDK_CHECK_FLAG;
				msg.obj = isExist;
				handler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();
	}

//
//	/**
//	 * This implementation is used to receive callbacks from the remote service.
//	 * 实现安全支付的回调
//	 */
//	private IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub() {
//		/**
//		 * This is called by the remote service regularly to tell us about new
//		 * values. Note that IPC calls are dispatched through a thread pool
//		 * running in each process, so the code executing here will NOT be
//		 * running in our main thread like most other things -- so, to update
//		 * the UI, we need to use a Handler to hop over there. 通过IPC机制启动安全支付服务
//		 */
//		public void startActivity(String packageName, String className,
//				int iCallingPid, Bundle bundle) throws RemoteException {
//			Intent intent = new Intent(Intent.ACTION_MAIN, null);
//
//			if (bundle == null)
//				bundle = new Bundle();
//			// else ok.
//
//			try {
//				bundle.putInt("CallingPid", iCallingPid);
//				intent.putExtras(bundle);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			intent.setClassName(packageName, className);
//			payActivity.startActivity(intent);
//		}
//
//		/**
//		 * when the msp loading dialog gone, call back this method.
//		 */
//		@Override
//		public boolean isHideLoadingScreen() throws RemoteException {
//			return false;
//		}
//
//		/**
//		 * when the current trade is finished or cancelled, call back this method.
//		 */
//		@Override
//		public void payEnd(boolean arg0, String arg1) throws RemoteException {
//			System.out.println(arg0);
//		}
//
//
//	};

}
