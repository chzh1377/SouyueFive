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

package com.android.volley;

import com.android.volley.Response.Listener;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Base class for all network requests.
 *
 */
public class DownOrUpRequest extends Request<String>{

    public interface ILoadProcess{
    	public void process(long max, long cur);
    }
    /** URL of this request. */
    private String mUrl;

    private long mMaxLength;

    private long mCurLength;

    /** Sequence number of this request, used to enforce FIFO ordering. */
    private Integer mSequence;

    /** Whether or not this request has been canceløøed. */
    private boolean mCanceled = false;

    private String mDownloadPath;
    private String mDownloadName;

    private File mDownloaded;

    /** The network after post*/
    private IProcess mPostProcess;

    /** The network after pre*/
    private IProcess mPreProcess;

    /** The network after post*/
    private ILoadProcess mLoadingProcess;

    /** An opaque token tagging this request; used for bulk cancellation. */
    private Object mTag;

    private int mId;

	private Listener<String> mSucess;

    /** all same download request list*/
    private List<DownOrUpRequest> mUniqDownload;

    /**
     * Creates a new request with the given method (one of the values from {@link Method}),
     * URL, and error listener.  Note that the normal response listener is not provided here as
     * delivery of responses is provided by subclasses, who have a better idea of how to deliver
     * an already-parsed response.
     */
    public DownOrUpRequest(int method, String url,Listener<String> sucess, Response.ErrorListener listener) {
    	super(method, url, listener);
    	mUrl = url;
    	mSucess = sucess;
        mUniqDownload = new ArrayList<DownOrUpRequest>();
    }

    public IProcess getPostProcess() {
		return mPostProcess;
	}

	public void setPostProcess(IProcess mPostProcess) {
		this.mPostProcess = mPostProcess;
	}

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public boolean isForceCache() {
		return false;
	}

	public void setForceCache(boolean mForceCache) {
		throw new IllegalArgumentException("setForceCache not compat down or up load!");
	}

	public IProcess getPreProcess() {
		return mPreProcess;
	}

	public void setPreProcess(IProcess mPreProcess) {
		this.mPreProcess = mPreProcess;
	}

    /**
     * Set a tag on this request. Can be used to cancel all requests with this
     * tag by {@link RequestQueue#cancelAll(Object)}.
     *
     * @return This Request object to allow for chaining.
     */
    public DownOrUpRequest setTag(Object tag) {
        mTag = tag;
        return this;
    }

    /**
     * Returns this request's tag.
     * @see DownOrUpRequest#setTag(Object)
     */
    public Object getTag() {
        return mTag;
    }


    public String getDownloadPath() {
		return mDownloadPath;
	}

	public void setDownloadPath(String mDownloadPath) {
		this.mDownloadPath = mDownloadPath;
	}

    public ILoadProcess getmLoadingProcess() {
		return mLoadingProcess;
	}

	public void setmLoadingProcess(ILoadProcess mLoadingProcess) {
		this.mLoadingProcess = mLoadingProcess;
	}

    /**
     * Returns the URL of this request.
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * Returns the cache key for this request.  By default, this is the URL.
     */
    public String getCacheKey() {
        return getUrl();
    }

	public long getmMaxLength() {
		return mMaxLength;
	}

	public void setmMaxLength(long mMaxLength) {
		this.mMaxLength = mMaxLength;
	}

    public long getmCurLength() {
        return mCurLength;
    }

    public void setmCurLength(long mCurLength) {
        this.mCurLength = mCurLength;
    }

    /**
     * Mark this request as canceled.  No callback will be delivered.
     */
    public void cancel() {
        mCanceled = true;
    }

    /**
     * Returns true if this request has been canceled.
     */
    public boolean isCanceled() {
        return mCanceled;
    }

    /**
     * Returns a list of extra HTTP headers to go along with this request. Can
     * throw {@link AuthFailureError} as authentication may be required to
     * provide these values.
     * @throws AuthFailureError In the event of auth failure
     */
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String ,String> map = new HashMap<String ,String>();
        map.put("Accept-Encoding", "identity");//防止返回-1
        return map;
    }

    /**
     * Returns a Map of parameters to be used for a POST or PUT request.  Can throw
     * {@link AuthFailureError} as authentication may be required to provide these values.
     *
     * <p>Note that you can directly override {@link #getBody()} for custom data.</p>
     *
     * @throws AuthFailureError in the event of auth failure
     */
    protected Map<String, String> getParams() throws AuthFailureError {
        return null;
    }

    /**
     * Returns the content type of the POST or PUT body.
     */
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    /**
     * Returns the raw POST or PUT body to be sent.
     *
     * <p>By default, the body consists of the request parameters in
     * application/x-www-form-urlencoded format. When overriding this method, consider overriding
     * {@link #getBodyContentType()} as well to match the new body format.
     *
     * @throws AuthFailureError in the event of auth failure
     */
    public byte[] getBody() throws AuthFailureError {
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }

    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
     */
    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    public void doProcess(long cur){
        mCurLength = cur;
    	if(mLoadingProcess!=null){
    		mLoadingProcess.process(mMaxLength, cur);
    	}

        for (DownOrUpRequest request:mUniqDownload){
            request.doProcess(cur);
        }
    }


    @Override
    public String toString() {
        String trafficStatsTag = "0x" + Integer.toHexString(getTrafficStatsTag());
        return (mCanceled ? "[X] " : "[ ] ") + getUrl() + " " + trafficStatsTag + " "
                + getPriority() + " " + mSequence;
    }

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String str = new String(response.data);
		return Response.success(str,null);
	}

	@Override
	public void deliverResponse(String response) {
		mSucess.onResponse(response);
        for (DownOrUpRequest request:mUniqDownload){
            request.deliverResponse(response);
        }
	}

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
        int count = mUniqDownload.size();
        for(int i=0 ; i< count ; i++)
        {
            DownOrUpRequest request = mUniqDownload.get(i);
            request.deliverError(error);
        }
    }

    public void setDownloadFile(File rename) {
		// TODO Auto-generated method stub
		mDownloaded = rename;
	}

    public File getDownloadFile(){
        return mDownloaded;
    }

    public String getmDownloadName() {
        return mDownloadName;
    }

    public void setmDownloadName(String mDownloadName) {
        this.mDownloadName = mDownloadName;
    }

    public void addDownloadRequest(DownOrUpRequest request){
        mUniqDownload.add(request);
    }

    public void cancelDownload(int id){
        Iterator<DownOrUpRequest> iterator = mUniqDownload.iterator();
        while(iterator.hasNext()){
            DownOrUpRequest req = iterator.next();
            if(req.getId() == id){
                mUniqDownload.remove(req);
            }
        }
    }
    /**
     * 判断下载任务是否在队列中
     * @param url
     * @return
     */
    public boolean isInDownloadQueue(String url){
        if  (url == null){
            return false;
        }
        if (mUrl.equals(url)){
            return true;
        }

        return false;
    }
}
