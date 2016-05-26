package com.zhongsou.souyue.common.utils;

import org.json.JSONException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonUtils {

	public static int getJsonValue(JsonObject _obj, String _key, int def)
			throws JSONException {
		if (!_obj.has(_key)) {
			return def;
		} else {
			return _obj.get(_key).getAsInt();
		}

	}

	public static long getJsonValue(JsonObject _obj, String _key, long def)
			throws JSONException {
		if (!_obj.has(_key)) {
			return def;
		} else {
			return _obj.get(_key).getAsLong();
		}

	}

	public static String getJsonValue(JsonObject _obj, String _key, String def)
			throws JSONException {
		if (!_obj.has(_key)) {
			return def;
		} else {
			return _obj.get(_key).getAsString();
		}

	}

	public static double getJsonValue(JsonObject _obj, String _key, double def)
			throws JSONException {
		if (!_obj.has(_key)) {
			return def;
		} else {
			return _obj.get(_key).getAsDouble();
		}

	}

	public static JsonObject getJsonValue(JsonObject _obj, String _key)
			throws JSONException {
		if (!_obj.has(_key)) {
			return null;
		} else {
			return _obj.get(_key).getAsJsonObject();
		}

	}

	public static boolean getJsonValue(JsonObject _obj, String _key, boolean def)
			throws JSONException {
		if (!_obj.has(_key)) {
			return def;
		} else {
			return _obj.get(_key).getAsBoolean();
		}

	}

	public static JsonArray getJsonArrayValue(JsonObject _obj, String _key)
			throws JSONException {
		if (!_obj.has(_key)) {
			return new JsonArray();
		} else {
			return _obj.get(_key).getAsJsonArray();
		}

	}
}
