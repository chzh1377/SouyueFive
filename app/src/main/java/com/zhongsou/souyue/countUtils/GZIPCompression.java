package com.zhongsou.souyue.countUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPCompression {
	public static byte[] compress(byte[] source){
		if(source ==null || source.length ==0){
			throw new IllegalArgumentException();
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip =null;
		try {
			 gzip = new GZIPOutputStream(out);
			 gzip.write(source, 0, source.length);
			 gzip.flush();
			 gzip.finish();
			 return out.toByteArray();
		} catch (IOException e) {
			if(gzip!=null){
				try {
					gzip.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			if(out!=null){
				try {
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		throw new IllegalArgumentException();
		
	}

	// 解压缩
	public static byte[] uncompress(byte[] source) throws IOException {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(source);
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		return out.toByteArray();
	}
}
