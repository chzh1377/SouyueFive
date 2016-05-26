package com.zhongsou.souyue.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class SaveBitmap {

	public static void saveMyBitmap(String bitName, Bitmap mBitmap) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File f = new File(Environment.getExternalStorageDirectory()+ "/souyue/share/temp/"+ bitName +".png");
			File folder = new File(Environment.getExternalStorageDirectory()+ "/souyue/share/temp/");
			try {
				if(!folder.exists())
					folder.mkdirs();
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FileOutputStream fOut = null;
			try {
				fOut = new FileOutputStream(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			mBitmap.compress(Bitmap.CompressFormat.PNG, 75, fOut);
			try {
				fOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	public static Bitmap getImage(String Url) throws Exception {
		try {
			HttpURLConnection conn;
			URL url = new URL(Url);
			conn = (HttpURLConnection) url.openConnection();
			if (conn.getResponseCode() != 200)
				throw new Exception("status=" + conn.getResponseCode());
			return BitmapFactory.decodeStream(url.openStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new Exception(e.getMessage());
		}
	}

}
