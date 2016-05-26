package com.tuita.sdk;

import java.util.LinkedHashMap;
import java.util.Map;

public class SmallMap<K, V> extends LinkedHashMap<K, V> {
	private int size;
	
	public SmallMap(int size) {
		super(size, (float)0.75, true);
		this.size = size;
	}
	
	public boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > size;
	}

}
