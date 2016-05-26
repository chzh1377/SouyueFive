/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhongsou.souyue.zxing.decoding;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.ScaningActivity;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.qrdecoding.BitmapLuminanceSource;
import com.zhongsou.souyue.utils.ImageCacheUtil;
import com.zhongsou.souyue.zxing.camera.CameraManager;
import com.zhongsou.souyue.zxing.camera.PlanarYUVLuminanceSource;

import java.util.Hashtable;

final class DecodeHandler extends Handler {

	private static final String TAG = DecodeHandler.class.getSimpleName();

	private final ScaningActivity activity;
	private final MultiFormatReader multiFormatReader;
    /**
     * 图片的target大小.
     */
    private static final int target = 400;

	DecodeHandler(ScaningActivity activity,
			Hashtable<DecodeHintType, Object> hints) {
		multiFormatReader = new MultiFormatReader();
		multiFormatReader.setHints(hints);
		this.activity = activity;
	}

	@Override
	public void handleMessage(Message message) {
	    switch (message.what) {
	      case R.id.decode:
                        //Log.d(TAG, "Got decode message");
	        decode((byte[]) message.obj, message.arg1, message.arg2);
	        break;
                  case R.id.get_decoding_result:
                      decodeBitmap((Uri) message.obj);
	      case R.id.quit:
	        Looper.myLooper().quit();
	        break;
	    }
	  }

    /**
     * 处理本地相册选择一张二维码图片解析并跳转逻辑。
     */
    private void decodeBitmap(Uri uri) {
        long start = System.currentTimeMillis();
        Result rawResult = null;
//        Bitmap myBitmap = BitmapFactory.decodeFile(path);
        Bitmap myBitmap = ImageCacheUtil.getResizedBitmap(null, null,
                activity, uri, target, false);
        if(myBitmap==null){
            activity.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    Log.d("callback","获取照片失败");
                    activity.decodeImgFailed();
                }
            });
            return;
        }
        LuminanceSource source = new BitmapLuminanceSource(myBitmap);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));//生成一個圖片矩陣
        try {
            rawResult = multiFormatReader.decodeWithState(bitmap);
        } catch (ReaderException re) {
            re.printStackTrace();
        } finally {
            multiFormatReader.reset();
        }

        if (rawResult != null) {
            long end = System.currentTimeMillis();
            Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());
            Message message = Message.obtain(activity.getHandler(), R.id.decode_succeeded, rawResult);//R.id.decode_succeeded = 1
            message.sendToTarget();
        } else {
            activity.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    Log.d("callback","获取照片失败");
                    activity.decodeImgFailed();
                }
            });
//            Message message = Message.obtain(activity.getHandler(), R.id.decodeImg_failed);//R.id.decode_failed = 2
//
//            message.sendToTarget();
        }
    }


    /**
	 * Decode the data within the viewfinder rectangle, and time how long it
	 * took. For efficiency, reuse the same reader objects from one decode to
	 * the next.
	 * 
	 * @param data
	 *            The YUV preview frame.
	 * @param width
	 *            The width of the preview frame.
	 * @param height
	 *            The height of the preview frame.
	 */
	private void decode(byte[] data, int width, int height) {
		long start = System.currentTimeMillis();
		Result rawResult = null;
		
		byte[] rotatedData = new byte[data.length]; 
		 for (int y = 0; y < height; y++) { 
		 for (int x = 0; x < width; x++) 
		 rotatedData[x * height + height - y - 1] = data[x + y * width]; 
		 } 
		 int tmp = width; // Here we are swapping, that's the difference to #11 
		 width = height; 
		 height = tmp; 
		 data = rotatedData; 
		
		PlanarYUVLuminanceSource source = CameraManager.get()
				.buildLuminanceSource(data, width, height);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		try {
			rawResult = multiFormatReader.decodeWithState(bitmap);
		} catch (ReaderException re) {
			// continue
		} finally {
			multiFormatReader.reset();
		}

		if (rawResult != null && CMainHttp.getInstance().isNetworkAvailable(activity)) {

			long end = System.currentTimeMillis();
			Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n"
					+ rawResult.toString());
			Message message = Message.obtain(activity.getHandler(),
					R.id.decode_succeeded, rawResult);
			Bundle bundle = new Bundle();
			bundle.putParcelable(DecodeThread.BARCODE_BITMAP,
					source.renderCroppedGreyscaleBitmap());
			message.setData(bundle);
			// Log.d(TAG, "Sending decode succeeded message...");
			message.sendToTarget();
		} else {

			Message message = Message.obtain(activity.getHandler(),
					R.id.decode_failed);
                              Log.d("callback","解析失败.......");
			message.sendToTarget();
		}
	}

}
