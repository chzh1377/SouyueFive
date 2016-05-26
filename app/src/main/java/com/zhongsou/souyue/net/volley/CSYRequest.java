package com.zhongsou.souyue.net.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lvqiang on 15/5/12.
 */
public class CSYRequest<T>
        extends Request<T>
{

    /** Default charset for JSON request. */
    protected static final String PROTOCOL_CHARSET = "utf-8";

    /** Content type for request. */
    //这里需要改成搜悦约定的
    private static final String PROTOCOL_CONTENT_TYPE = "application/x-www-form-urlencoded";
    //            String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    private static final String USER_AGENT = "Android";
    private static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
    private static final String POST_ENTITY = "%entity";


    private final Listener<T> mListener;
    private Map<String,String> mRequestBody;
    private Map<String,String> mHeader;
    private boolean mBodyNull;
    private String mCacheKey;
    private IParser<T> mDoParser;
    private NetworkResponse mResponse;
    private CVolleyRequest mRequest;
    private String mUrl;
    private String mTime;


    /**
     * Creates a new request.
     * @param method the HTTP method to use
     * @param url URL to fetch the JSON from
     * @param requestBody A  to post with the request. Null is allowed and
     *   indicates no parameters will be posted along with request.
     * @param listener Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public CSYRequest(int method, String url,Map<String,String> requestBody,
                      Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        mUrl = url;
        mListener = listener;
        if(requestBody==null){
            mBodyNull=true;
            mRequestBody = new HashMap<String, String>();
        }else {
            mBodyNull=false;
            mRequestBody = requestBody;
        }
    }

    /**
     * Constructor which defaults to <code>GET</code> if <code>jsonRequest</code> is
     * <code>null</code>, <code>POST</code> otherwise.
     *
     */
    public CSYRequest(String url, Map<String,String> requestParams, Listener<T> listener,
                      ErrorListener errorListener) {
        this(requestParams == null ? Method.GET : Method.POST, url, requestParams,
                listener, errorListener);
    }

    public void addExtraParams(String key,String value){
        if (!mRequestBody.containsKey(key)){
            mRequestBody.put(key, value);
        }
    }
    public Map<String,String> getRequestParams(){
        return mRequestBody;
    }
    @Override
    public String getUrl(){
        return mUrl;
    }

    public void setGetUrl(String url){
        mUrl = url;
    }

    public void setPostParams(Map<String,String> params){
        mRequestBody = params;
    }

    public void clearParams(){
        mRequestBody.clear();
    }



    private byte[] getPostContent() throws IOException {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        Object value;
        for (Map.Entry<String, String> e : mRequestBody.entrySet()) {
            value = e.getValue();
            if (value != null) {
                pairs.add(new BasicNameValuePair(e.getKey(), value.toString()));
            }
        }
//        Map<String,Object> params = convertStringToObject(mRequestBody);
//        List<BasicNameValuePair> pairs = Utils.encryptToPostEntity(super.getUrl(), params, "");
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(pairs, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] bytes;
        InputStream in = entity.getContent();
        bytes = new byte[in.available()];
        in.read(bytes);
        return bytes;
    }

    private byte[] getEntity() throws IOException {
        HttpEntity entity = null;
        String value;
        String key;
        for (Map.Entry<String, String> e : mRequestBody.entrySet()) {
            key  = e.getKey();
            value = e.getValue();
            if (key.equals(POST_ENTITY)){
                entity = new StringEntity(value, "UTF-8");
            }
        }
        if (entity == null){
            return null;
        }
        byte[] bytes;
        InputStream in = entity.getContent();
        bytes = new byte[in.available()];
        in.read(bytes);
        return bytes;
    }


    public void setCacheKey(String key){
        mCacheKey=key;
    }

    @Override
    public String getCacheKey() {
        if(mCacheKey==null){
            return getUrl();
        }
        return mCacheKey;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            mResponse = response;
            if(response.headers!=null) {
                mTime = response.headers.get("Date");
            }
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

            Object obj = mDoParser.doParse(mRequest, jsonString);
            return (Response<T>) Response.success(obj, HttpHeaderParser.parseCacheHeaders(response));
        } catch (VolleyError e){
            return Response.error(e);
        } catch (Exception je) {
            je.printStackTrace();
            return Response.error(new ParseError(je));
        } catch (OutOfMemoryError e){
            return Response.error(new ParseError(e));
        }
    }

    public void setDoParse(IParser<T> parser){
        mDoParser = parser;
    }

    public CVolleyRequest getmRequest() {
        return mRequest;
    }

    public void setmRequest(CVolleyRequest mRequest) {
        this.mRequest = mRequest;
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    public void setHeaders(Map<String,String> headers){
        mHeader = headers;
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        if (mHeader == null) {
            mHeader = new HashMap<String, String>();
        }
        if (!mHeader.containsKey("User-Agent")) {
            mHeader.put("User-Agent", USER_AGENT);
        }
//        map.put("Accept", ACCEPT);
        return mHeader;
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        try {
            BaseUrlRequest request1 = mRequest.getBaseUrlRequest();
            if (request1!=null&&request1.getProcessStragegy(BaseUrlRequest.REQUEST_ALREADY_ENTITY)) {
                return getEntity();
            }
            return mBodyNull? null : getPostContent();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
