package com.zhongsou.souyue.platform;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.zhongsou.souyue.MainApplication;

/** 
 * Description: 平台(搜悦和超级app)通用配置项实现类<br/> 
 * Company: ZhongSou.com<br/> 
 * Copyright: 2003-2014 ZhongSou All right reserved<br/> 
 * @date     2014-7-30 下午6:21:36
 * @author   liudl
 */  
public class ConfigPreferences {
	public static final String SP_NAME = "platform";
	private static ConfigPreferences sInstance = null;

	public static synchronized ConfigPreferences getInstance() {
		if (sInstance == null) {
		    sInstance = new ConfigPreferences();
		}
		return sInstance;
	}

	/**  
	 * getSp:平台配置项专用Preference. <br/>  
	 *  
	 * @author liudl
	 * @date   2014-7-31 上午9:08:20
	 * @return  
	 */
	private SharedPreferences getSp() {
		return MainApplication.getInstance().getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
	}

	public int getInt(String key, int def) {
		try {
		    SharedPreferences sp = getSp();
			if (sp != null)
				def = sp.getInt(key, def);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

//	public void setInt(String key, int val) {
//		try {
//		    SharedPreferences sp = getSp();
//			if (sp != null) {
//				Editor e = sp.edit();
//				e.putInt(key, val);
//				e.commit();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

    public long getLong(String key, long def) {
        try {
            SharedPreferences sp = getSp();
            if (sp != null)
                def = sp.getLong(key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

//    public void setLong(String key, long val) {
//        try {
//            SharedPreferences sp = getSp();
//            if (sp != null) {
//                Editor e = sp.edit();
//                e.putLong(key, val);
//                e.commit();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

	public String getString(String key, String def) {
		try {
		    SharedPreferences sp = getSp();
			if (sp != null)
				def = sp.getString(key, def);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

	public void setString(String key, String val) {
		try {
		    SharedPreferences sp = getSp();
			if (sp != null) {
				Editor e = sp.edit();
				e.putString(key, val);
				e.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean getBoolean(String key, boolean def) {
		try {
		    SharedPreferences sp = getSp();
			if (sp != null)
				def = sp.getBoolean(key, def);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

	public void setBoolean(String key, boolean val) {
		try {
		    SharedPreferences sp = getSp();
			if (sp != null) {
				Editor e = sp.edit();
				e.putBoolean(key, val);
				e.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void remove(String key) {
		try {
		    SharedPreferences sp = getSp();
			if (sp != null) {
				Editor e = sp.edit();
				e.remove(key);
				e.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
