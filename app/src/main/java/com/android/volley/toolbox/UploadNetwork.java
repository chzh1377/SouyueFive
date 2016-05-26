/*
 * Copyright (C) 2011 The Android Open Source Project
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

import android.os.SystemClock;
import com.android.volley.DownOrUpRequest;
import com.android.volley.Network;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * A network performing Volley requests over an {@link HttpStack}.
 */
public class UploadNetwork implements Network {
    protected static final boolean DEBUG = VolleyLog.DEBUG;

    private static int SLOW_REQUEST_THRESHOLD_MS = 3000;

    protected final HttpStack mHttpStack;

//    protected final ByteArrayPool mPool;

    /**
     * @param httpStack HTTP stack to be used
     */
    public UploadNetwork(HttpStack httpStack) {
        // If a pool isn't passed in, then build a small default pool that will give us a lot of
        // benefit and not use too much memory.
    	mHttpStack = httpStack;
    }

    @Override
    public NetworkResponse performRequest(Request<?> request) throws VolleyError {
        long requestStart = SystemClock.elapsedRealtime();
        while (true) {
            HttpResponse httpResponse = null;
            byte[] responseContents = null;
            Map<String, String> responseHeaders = Collections.emptyMap();
            RandomAccessFile acessfile = null;
            File file = null;
            try {
            	if(!(request instanceof DownOrUpRequest)){
            		throw new IllegalArgumentException("request object mast be DownOrUpRequest！！！");
            	}
            	DownOrUpRequest requestDown = (DownOrUpRequest) request;
                // Gather headers.
                Map<String, String> headers = new HashMap<String, String>();
                // Download have no cache
                String url = requestDown.getUrl();
                String name = url.substring(url.lastIndexOf('/'), url.length());
            	String path = requestDown.getDownloadPath();
            	String filePath = "";
            	if	(path.endsWith("/")){
            		filePath = path+name;
            	}else{
            		path=path+"/";
            		filePath = path+"/"+name;
            	}
            	File dir = new File(path);
            	dir.mkdirs();
            	file =  File.createTempFile(path, null,dir);
            	acessfile = new RandomAccessFile(file, "rws");
            	
            	long length = acessfile.length();
            	acessfile.seek(length);
//            	Range: bytes=5275648- 
            	headers.put("Range", "bytes="+length+"-");//断点续传
                httpResponse = mHttpStack.performRequest(requestDown, headers);
                StatusLine statusLine = httpResponse.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                responseHeaders = convertHeaders(httpResponse.getAllHeaders());
                // if the request is slow, log it.
                long requestLifetime = SystemClock.elapsedRealtime() - requestStart;
                logSlowRequests(requestLifetime, requestDown, responseContents, statusLine);

                if (statusCode < 200 || statusCode > 299) {
                	acessfile.close();
                    throw new IOException();
                }
                // Some responses such as 204s do not have content.  We must check.
                if (httpResponse.getEntity() != null) {
                  responseContents = entityToBytes(httpResponse.getEntity(),requestDown,acessfile);
                } else {
                  // Add 0 byte response as a way of honestly representing a
                  // no-content request.
                  responseContents = new byte[0];
                }
                acessfile.close();
                file.renameTo(new File(filePath));
                
                return new NetworkResponse(statusCode, responseContents, responseHeaders, false,
                        SystemClock.elapsedRealtime() - requestStart);
            } catch (SocketTimeoutException e) {
                attemptRetryOnException("socket", request, new TimeoutError());
            } catch (ConnectTimeoutException e) {
                attemptRetryOnException("connection", request, new TimeoutError());
            } catch (MalformedURLException e) {
                throw new RuntimeException("Bad URL " + request.getUrl(), e);
            } catch (IOException e) {
            	if(acessfile!=null){
            		try {
						acessfile.close();
						file.delete();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
            	}
                int statusCode = 0;
                NetworkResponse networkResponse = null;
                if (httpResponse != null) {
                    statusCode = httpResponse.getStatusLine().getStatusCode();
                } else {
                    throw new NoConnectionError(e);
                }
                VolleyLog.e("Unexpected response code %d for %s", statusCode, request.getUrl());
                throw new NetworkError(networkResponse);
            }
        }
    }

    /**
     * Logs requests that took over SLOW_REQUEST_THRESHOLD_MS to complete.
     */
    private void logSlowRequests(long requestLifetime, Request<?> request,
            byte[] responseContents, StatusLine statusLine) {
        if (DEBUG || requestLifetime > SLOW_REQUEST_THRESHOLD_MS) {
            VolleyLog.d("HTTP response for request=<%s> [lifetime=%d], [size=%s], " +
                    "[rc=%d], [retryCount=%s]", request, requestLifetime,
                    responseContents != null ? responseContents.length : "null",
                    statusLine.getStatusCode(), request.getRetryPolicy().getCurrentRetryCount());
        }
    }

    /**
     * Attempts to prepare the request for a retry. If there are no more attempts remaining in the
     * request's retry policy, a timeout exception is thrown.
     * @param request The request to use.
     */
    private static void attemptRetryOnException(String logPrefix, Request<?> request,
            VolleyError exception) throws VolleyError {
        RetryPolicy retryPolicy = request.getRetryPolicy();
        int oldTimeout = request.getTimeoutMs();

        try {
            retryPolicy.retry(exception);
        } catch (VolleyError e) {
            request.addMarker(
                    String.format("%s-timeout-giveup [timeout=%s]", logPrefix, oldTimeout));
            throw e;
        }
        request.addMarker(String.format("%s-retry [timeout=%s]", logPrefix, oldTimeout));
    }

    protected void logError(String what, String url, long start) {
        long now = SystemClock.elapsedRealtime();
        VolleyLog.v("HTTP ERROR(%s) %d ms to fetch %s", what, (now - start), url);
    }

    /** Reads the contents of HttpEntity into a byte[]. 
     * @param request 
     * @param acessfile */
    private byte[] entityToBytes(HttpEntity entity, DownOrUpRequest request, RandomAccessFile acessfile) throws IOException, ServerError {
//        PoolingByteArrayOutputStream bytes =
//                new PoolingByteArrayOutputStream(mPool, (int) entity.getContentLength());
    	long length = acessfile.length();
    	request.setmMaxLength(entity.getContentLength());
        byte[] buffer = new byte[1024];
        try {
            InputStream in = entity.getContent();
            if (in == null) {
                throw new ServerError();
            }
            int count;
            while ((count = in.read(buffer)) != -1) {
            	if(request.isCanceled()){
            		break;
            	}
            	acessfile.write(buffer, 0, count);
            	length+=count;
            	request.doProcess(length);
            	
            }
            return null;
        } finally {
            try {
                // Close the InputStream and release the resources by "consuming the content".
                entity.consumeContent();
            } catch (IOException e) {
                // This can happen if there was an exception above that left the entity in
                // an invalid state.
                VolleyLog.v("Error occured when calling consumingContent");
            }
//            bytes.close();
            
        }
    }

    /**
     * Converts Headers[] to Map<String, String>.
     */
    protected static Map<String, String> convertHeaders(Header[] headers) {
        Map<String, String> result = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < headers.length; i++) {
            result.put(headers[i].getName(), headers[i].getValue());
        }
        return result;
    }
}
