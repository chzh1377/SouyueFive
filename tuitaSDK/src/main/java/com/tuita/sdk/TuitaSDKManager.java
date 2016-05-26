package com.tuita.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import com.tuita.sdk.im.db.helper.ConfigDaoHelper;
import com.tuita.sdk.im.db.helper.MessageMidDaoHelper;
import com.tuita.sdk.im.db.module.Config;
import com.zhongsou.souyue.log.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author fangxm@zhongsou.com
 */
public class TuitaSDKManager {
    //protected static final String TUITA_SDK_VERSION = "test_1.0";
    protected static final String TUITA_SDK_VERSION = "souyue_4_0";
    protected static final String TAG = "TuitaSDK";
    protected static final String SOUYUE_TAG = "souyue";
    protected static final int TUITA_STATE_DISCONNECT = 0;
    protected static final int TUITA_STATE_CONNECT = 1;
    protected static final int CONN_STATE_NOTCONNECT = 0;
    protected static final int CONN_STATE_CONNECTING = 1;
    protected static final int PING_NOACK_LIMIT = 5;
    public static final String TUITA_HOST = "tuita_host";
    private static final String TUITA_SHARE_CLIENTID = "tuita_clientID";
    private static final String TUITA_SHARE_DEVICEID = "tuita_deviceID";
    private static final String TUITA_SHARE_LASTMSGTYPE = "tuita_lastMSGType";
    private static final String TUITA_SHARE_LASTMSGTIME = "tuita_lastMSGTime";
    private static final String TUITA_SHARE_TOKEN = "tuita_token";
    //    public static String TUITA_CENTER_HOST = "http://newpush.souyue.mobi/api/get.mid2";
    public static String TUITA_CENTER_HOST = "http://103.29.134.26/api/get.mid3," +
            "http://202.108.1.46/api/get.mid3," +
            "http://newpush.souyue.mobi/api/get.mid3";

    //开发环境
    public static final String TUITA_CENTER_HOST_TEST_DEVLOPER = "http://103.7.221.128:8080/api/get.jsp";
    //测试
    public static final String TUITA_CENTER_HOST_TEST_INSIDE = "http://103.29.134.173/api/get.mid2";
    //预上线
    public static final String TUITA_CENTER_HOST_TEST = "http://test.push.zhongsou.com/api/get.mid2";
    //线上
//    public static final String TUITA_CENTER_HOST_ONLINE = "http://newpush.souyue.mobi/api/get.mid2";
    public static final String TUITA_CENTER_HOST_ONLINE = "http://103.29.134.26/api/get.mid3," +
            "http://202.108.1.46/api/get.mid3," +
            "http://newpush.souyue.mobi/api/get.mid3";

    private static final int TUITA_CENTER_RETRY = 1;
    private static final int TUITA_CENTER_TIMEOUT = TuitaConnection.TUITA_CONNECT_TIMEOUT;
    private static TuitaSDKManager manager;
    private TuitaConnection connection;
    protected Context context;
    public SharedPreferences preferences;
    private List<ConnectListener> connectListeners = new ArrayList<ConnectListener>();
    private List<ErrorListener> errorListeners = new ArrayList<ErrorListener>();
    private List<ReadListener> readListeners = new ArrayList<ReadListener>();
    private List<WriteListener> writeListeners = new ArrayList<WriteListener>();
    private String clientID;
    private String nodeHost = "";
    private String token;
    private final long mGapTime = 60 * 60 * 24 * 30; //30天间隔
    private volatile int connState; // 0 not connect 1 connecting
    private volatile int tuitaState; // 0 disconnect 1 connect
    private AtomicInteger pingNoAckCount = new AtomicInteger(0);
    //    private AtomicInteger reConnectCount = new AtomicInteger(0);
    private TuitaIMManager immanager;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private TuitaSDKManager(Context context) {
        init(context);//初始化clientID 和 用户token
        connectListeners.add(new ConnectListener() {//添加监听
            @Override
            public boolean connect(TuitaConnection conn) {
                if (immanager == null) {
                    loadIM();
                }
                immanager.setOwner(ContextUtil.getOwner(getContext()));
                if (immanager.getOwner().getUid() != 0 && immanager.getOwner().getPass() != null) {
                    deleteMid();    //删除mid
                    immanager.checkVersion(getContext(), immanager.getOwner().getUid(), getAppVersionName(manager));
                    Config config = ConfigDaoHelper.getInstance(getContext()).find(immanager.getOwner().getUid());
                    ImCommand cmd = ImCommand.newConnectIMCmd(TuitaSDKManager.this, immanager, config != null ? config.getContact_last_update() : 0, getAppVersionName(manager));
                    conn.write(cmd.getPacket());
                    Logger.i("tuita", "TuitaSDKManager.TuitaSDKManager", "t = " + cmd.getType());
                    immanager.getRunningCmd().put(cmd.getTid(), cmd);
                    immanager.requestTimeOut(manager, cmd.getTid());
                }
                return true;
            }
        });
        readListeners.add(new DefaultReadListener(this));
        errorListeners.add(new DefaultErrorListener(this));
        connection = new TuitaConnection(this);
    }

    public static TuitaSDKManager getInstance(Context context) {
        if (manager == null) {
            synchronized (TuitaSDKManager.class) {
                if (manager == null) {
                    manager = new TuitaSDKManager(context);
                }
            }
        }
        return manager;
    }

    int mGetMidRetryindex = 0;

    public void saveInfo(String key, String value) {
        Log.i(TAG, "saveInfo(" + key + "," + value + ")");
        preferences.edit().putString(key, value).commit();
    }
    public void saveLong(String key, long value) {
        Log.i(TAG, "saveLong(" + key + "," + value + ")");
        preferences.edit().putLong(key, value).commit();
    }
    public void loadIM() {
        this.setImmanager(TuitaIMManager.getInstance(this));
    }

    private long getLong(String key, long defaultValue) {
        long value = preferences.getLong(key, defaultValue);
        Log.i(TAG, "getLong(" + key + "," + value + ")");
        return value;
    }

    public void saveInt(String key, int value) {
        Log.i(TAG, "saveInt(" + key + "," + value + ")");
        preferences.edit().putInt(key, value).commit();
    }

    public int getInt(String key, int defaultValue) {
        int value = preferences.getInt(key, defaultValue);
        Log.i(TAG, "getInt(" + key + "," + value + ")");
        return value;
    }

    private String getInfo(String key, String defaultValue) {
        String value = preferences.getString(key, defaultValue);
        Log.i(TAG, "getInfo(" + key + "," + value + ")");
        return value;
    }

    private SYUserBean user;
    private TuitaIMManager.Owner owner;

    private void init(Context ctx) {
        Log.i(TAG, "Tuita SDK init...");
        this.context = ctx;
        preferences = ctx.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        clientID = getInfo(TUITA_SHARE_CLIENTID, "");
        token = getInfo(TUITA_SHARE_TOKEN, "");
    }

    protected boolean ping() {
        boolean result = false;
        if (ContextUtil.isNetworkAvailable(context)) {
            if (pingNoAckCount.get() >= PING_NOACK_LIMIT) {   //这个应该是重新链接的次数超过5
                this.setTuitaState(TuitaSDKManager.TUITA_STATE_DISCONNECT);
//                connection.disconnect();
                pingNoAckCount.set(0);
            }
            //超过5次走else否则if
            if (tuitaState == TUITA_STATE_CONNECT) {
                result = connection.keepAlive();
            } else {
                result = start();
            }
        }
        return result;
    }

    protected void ack(int type, String mid, int gm) {
        connection.write(TuitaPacket.createAckPacket(type, mid, gm));
    }

    protected void stop() {
        connection.disconnect();
    }

    protected boolean start() {
    	Log.i(TAG, "start...");
        Logger.i("tuita", "TuitaSDKManager.start", "TuitaSDKManager start()");
        if (tuitaState == TUITA_STATE_CONNECT) {
            Logger.i("tuita", "TuitaSDKManager.start", "Tuita tuitaState = " + tuitaState);
            return true;
        }
//        connection.disconnect();
        boolean result = false;
        if (ContextUtil.isNetworkAvailable(context)) {
            if (checkNodeHost()) {
                result = connection.connect();
            } else {
            	Log.i(TAG, "Tuita Center unavailable...");
                Logger.i("tuita", "TuitaSDKManager.start", "Tuita Center unavailable...");
            }
        } else {
        	Log.i(TAG, "Network unavailable...");
            Logger.i("tuita", "TuitaSDKManager.start", "网络不可用");
        }
        return result;
    }

    /*private void errorNodeHost() {
        Log.i(TAG, "error Node Host..." + this.nodeHost);
        String host = fetchNodeHost();
        if (host.length() > 0) {
            nodeHost = host;
        }
        
    }*/
    private boolean checkNodeHost() {
        boolean result = true;
//        String host = "202.108.1.203:80";
        String host = getInfo(TUITA_HOST,"");
        if (host == null || host.equals("")){
                host = fetchNodeHost();
        }

        if (host.length() > 0) {
            mGetMidRetryindex = 0;
            nodeHost = host;
        } else {
            if (nodeHost.length() <= 0) {
                mGetMidRetryindex++;
                result = false;
            }
        }
        Logger.i("tuita", "TuitaSDKManager.checkNodeHost", "Node Host check " + result + "..." + nodeHost);

        return result;
    }

    private String fetchNodeHost() {
        HttpURLConnection conn = null;
        int attempt = 0;
        String mid = "";
        String uid = "";
        String cid = "";

        Log.i(TAG, "begin fetch Node Host...(" + this.nodeHost + ")");
        do {
            attempt++;
            long getMidStartTime = System.currentTimeMillis();
            try {
                String[] hosts = TUITA_CENTER_HOST.split(",");
                mid = this.nodeHost.length() > 0 ? this.nodeHost : "0";
                uid = this.getImmanager() != null && this.getImmanager().getOwner() != null ? this.getImmanager().getOwner().getUid()+"" : "0";
                cid = this.getClientID() != null && !"".equals(this.getClientID()) ? this.getClientID() : "0";
                if (hosts.length <= mGetMidRetryindex){
                    mGetMidRetryindex = 0;
                }
                Log.i(TAG, "url--------->" + hosts[mGetMidRetryindex]+"?mid="+mid+"&uid="+uid+"&cid="+cid+"&sv="+getAppVersionName(manager));
                URL url = new URL(hosts[mGetMidRetryindex]+"?mid="+mid+"&uid="+uid+"&cid="+cid+"&sv="+getAppVersionName(manager));
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(TUITA_CENTER_TIMEOUT);
                conn.connect();
                int status = conn.getResponseCode();
                if (status == 200) {
                    Logger.i("tuita", "TuitaSDKManager.fetchNodeHost", "http status == 200 getmid time is---->" + (System.currentTimeMillis() - getMidStartTime));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder content = new StringBuilder();
                    String newLine = null;
                    do {
                        newLine = reader.readLine();
                        if (newLine != null)
                            content.append(newLine).append("\r\n");
                    } while (newLine != null);
                    if (content.length() > 0) {
                        content.setLength(content.length() - 2);
                    }
                    Log.i(TAG, "get Node Host..." + content.toString());
                    String str = content.toString();
                    if (str.length() <= 20 && str.indexOf(":") != -1) {
                        return str;
                    }
                    return "";
                }
            } catch (Exception e) {
                Log.i(TAG, "get Node Host Exception..." + TUITA_CENTER_HOST);
                Log.i(TAG, "get Node Host Exception..." + e);
                Logger.i("tuita", "TuitaSDKManager.fetchNodeHost", "http catch getmid time is---->" + (System.currentTimeMillis() - getMidStartTime));
                e.printStackTrace();
            } finally {
                Logger.i("tuita", "TuitaSDKManager.fetchNodeHost", "http finally getmid time is---->" + (System.currentTimeMillis() - getMidStartTime));
                if (conn != null) {
                    conn.disconnect();
                }
            }
        } while (attempt < TUITA_CENTER_RETRY);
        return "";
    }

    protected String getNodeHost() {
        return nodeHost;
    }

    protected static interface ConnectListener {
        public boolean connect(TuitaConnection connection);
    }

    protected static interface ErrorListener {
        public boolean error(TuitaConnection connection, Throwable e, Operations op, String message);
    }

    protected static interface ReadListener {
        public boolean read(TuitaConnection connection, TuitaPacket packet);
    }

    protected static interface WriteListener {
        public boolean write(TuitaConnection connection, TuitaPacket packet);
    }

    static enum Operations {
        CONNECT, DISCONNECT, PACK_WRITE, PACK_READ
    }

    protected List<ConnectListener> getConnectListeners() {
        return connectListeners;
    }

    //  private void setConnectListeners(List<ConnectListener> connectListeners) {
    //      this.connectListeners = connectListeners;
    //  }
    protected List<ReadListener> getReadListeners() {
        return readListeners;
    }

    //  private void setReadListeners(List<ReadListener> readListeners) {
    //      this.readListeners = readListeners;
    //  }
    protected List<WriteListener> getWriteListeners() {
        return writeListeners;
    }

    //  private void setWriteListeners(List<WriteListener> writeListeners) {
    //      this.writeListeners = writeListeners;
    //  }
    protected List<ErrorListener> getErrorListeners() {
        return errorListeners;
    }

    //  private void setErrorListeners(List<ErrorListener> errorListeners) {
    //      this.errorListeners = errorListeners;
    //  }
    protected String getClientID() {
        return clientID;
    }

    protected String getDeviceId() {
        String deviceId = getInfo(TUITA_SHARE_DEVICEID, null);
        if (deviceId == null) {
            deviceId = DeviceUtil.getDeviceId(this.getContext());
            saveInfo(TUITA_SHARE_DEVICEID, deviceId);
            return deviceId;
        } else {
            return deviceId;
        }
    }

    protected void setClientID(String clientID) {
        this.clientID = clientID;
        saveInfo(TUITA_SHARE_CLIENTID, clientID);
    }

    protected String getLastMSGType() {
        return getInfo(TUITA_SHARE_LASTMSGTYPE, "");
    }

    protected void setLastMSGType(String lastMSGType) {
        saveInfo(TUITA_SHARE_LASTMSGTYPE, lastMSGType);
    }

    protected String getLastMSGTime() {
        return getInfo(TUITA_SHARE_LASTMSGTIME, "0");
    }

    protected void setLastMSGTime(String lastGMTime) {
        saveInfo(TUITA_SHARE_LASTMSGTIME, lastGMTime);
    }

    protected String getToken() {
        return token;
    }

    protected void setToken(String token) {
        this.token = token;
        saveInfo(TUITA_SHARE_TOKEN, token);
    }

    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public void setScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

    protected int getConnState() {
        return connState;
    }

    protected void setConnState(int connState) {
        this.connState = connState;
    }

    protected int getTuitaState() {
        return tuitaState;
    }

    protected void setTuitaState(int tuitaState) {
        this.tuitaState = tuitaState;
        if (tuitaState == TUITA_STATE_DISCONNECT) {
            if (this.getImmanager() != null) {
                this.getImmanager().setTuitaIMState(TuitaIMManager.TUITA_IM_STATE_DISCONNECT);
            }
        }
    }

    public AtomicInteger getPingNoAckCount() {
        return pingNoAckCount;
    }

    public void setPingNoAckCount(AtomicInteger pingNoAckCount) {
        this.pingNoAckCount = pingNoAckCount;
    }

    public TuitaIMManager getImmanager() {
        return immanager;
    }

    public void setImmanager(TuitaIMManager immanager) {
        this.immanager = immanager;
    }

    public Context getContext() {
        return context;
    }

    public TuitaConnection getConnection() {
        return connection;
    }

    public void setConnection(TuitaConnection connection) {
        this.connection = connection;
    }

    /**
     * im在业务中重连方法
     */
    public void reConnectIM() {
        Log.i("Tutia", "----------reConnect---------");
        new Thread(new Runnable() {
            @Override
            public void run() {
//                connection.disconnect();
                reConnectStart();
            }
        }).start();
    }

    /**
     * 仿start，不判断状态直接重连
     *
     * @return
     */
    protected boolean reConnectStart() {
        Log.i(TAG, "start...");
        boolean reConnect = false;
        saveInfo(TuitaSDKManager.TUITA_HOST,"");
        if (ContextUtil.isNetworkAvailable(context)) {
            if (checkNodeHost()) {
                reConnect = connection.connect();
            } else {
                Log.i(TAG, "re----Tuita Center unavailable...");
            }
        } else {
            Log.i(TAG, "re----Network unavailable...");
        }
        return reConnect;
    }

    public static String getAppVersionName(TuitaSDKManager manager) {
        String versionName = null;
        try {
            versionName = manager.getContext().getPackageManager().getPackageInfo(manager.getContext().getPackageName(), 0).versionName;
            manager.saveInfo("version", versionName);
            return versionName;
        } catch (Exception e) {
            return versionName == null ? "" : versionName;
        }
    }

    /**
     * 删除排重表数据逻辑
     *
     * 大于30天的消息mid要删除
     * @return
     */
    private void deleteMid(){
        long currentTime = System.currentTimeMillis();      //当前时间戳
        long saveTime = getLong("delete_mid_time",0);       //上次记录的时间戳
        long gapTime = (currentTime - saveTime) / 1000;     //时间间隔
        if (gapTime > mGapTime){
            saveLong("delete_mid_time",currentTime);
            MessageMidDaoHelper.getInstance(context).deleteByTimeStamp(currentTime);
        }
    }
}
