/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.android.volley.toolbox;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.os.Environment;
import com.android.volley.Network;
import com.android.volley.RequestDownOrUpQueue;
import com.android.volley.RequestQueue;

import java.io.File;

public class Volley {

	/** Default on-disk cache directory. */
	private static final String DEFAULT_CACHE_DIR = "souyue";

	private static final String DEFAULT_USER_AGENT = "Android";

	/**
	 * Creates a default instance of the worker pool and calls
	 * {@link RequestQueue#start()} on it.
	 *
	 * @param context
	 *            A {@link Context} to use for creating the cache dir.
	 * @param stack
	 *            An {@link HttpStack} to use for the network, or null for
	 *            default.
	 * @return A started {@link RequestQueue} instance.
	 */
	public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
		File cacheDir = new File(getDiskCacheDir(context, DEFAULT_CACHE_DIR));

		String userAgent = DEFAULT_USER_AGENT;
		// try {
		// String packageName = context.getPackageName();
		// PackageInfo info =
		// context.getPackageManager().getPackageInfo(packageName, 0);
		// userAgent = packageName + "/" + info.versionCode;
		// } catch (NameNotFoundException e) {
		// }

		if (stack == null) {
			if (Build.VERSION.SDK_INT >= 9) {
				stack = new HurlStack();
			} else {
				// Prior to Gingerbread, HttpUrlConnection was unreliable.
				// See:
				// http://android-developers.blogspot.com/2011/09/androids-http-clients.html
				stack = new HttpClientStack(
						AndroidHttpClient.newInstance(userAgent));
			}
		}

		Network network = new BasicNetwork(stack);

		RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir),
				network);
		queue.start();

		return queue;
	}

	/**
	 * Creates a default instance of the worker pool and calls
	 * {@link RequestQueue#start()} on it.
	 *
	 * @param context
	 *            A {@link Context} to use for creating the cache dir.
	 * @return A started {@link RequestQueue} instance.
	 */
	public static RequestQueue newRequestQueue(Context context) {
		return newRequestQueue(context, null);
	}

	/**
	 * Creates a default instance of the worker pool and calls
	 * {@link RequestDownOrUpQueue#start()} on it.
	 *
	 * @param context
	 *            A {@link Context} to use for creating the cache dir.
	 * @param stack
	 *            An {@link HttpStack} to use for the network, or null for
	 *            default.
	 * @return A started {@link RequestQueue} instance.
	 */
	public static RequestDownOrUpQueue newRequestDownloadQueue(Context context,
			HttpStack stack) {
		String userAgent = DEFAULT_USER_AGENT;
		// try {
		// String packageName = context.getPackageName();
		// PackageInfo info =
		// context.getPackageManager().getPackageInfo(packageName, 0);
		// userAgent = packageName + "/" + info.versionCode;
		// } catch (NameNotFoundException e) {
		// }

		if (stack == null) {
			if (Build.VERSION.SDK_INT >= 9) {
				stack = new HurlStack();
			} else {
				// Prior to Gingerbread, HttpUrlConnection was unreliable.
				// See:
				// http://android-developers.blogspot.com/2011/09/androids-http-clients.html
				stack = new HttpClientStack(
						AndroidHttpClient.newInstance(userAgent));
			}
		}

		Network network = new DownloadNetwork(stack);

		RequestDownOrUpQueue queue = new RequestDownOrUpQueue(network);
		queue.start();

		return queue;
	}

	public static RequestDownOrUpQueue newDownloadQueue(Context context) {
		return newRequestDownloadQueue(context, null);
	}

	public static String getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		File file = context.getExternalCacheDir();
		if ((Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) || !Environment
				.isExternalStorageRemovable())
				&& file != null) {
			cachePath = file.getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return cachePath + File.separator + uniqueName;
	}
}
