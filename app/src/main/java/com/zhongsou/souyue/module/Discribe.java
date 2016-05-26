package com.zhongsou.souyue.module;

import com.zhongsou.souyue.DontObfuscateInterface;

/**
 * 内部类，描述更新的版本，及更新内容
	  {
	    "version": "4.0.2",
	    "changes": [
	      "没有说明111",
	      "没有说明222",
	      "没有说明333",
	      "没有说明444"
	    ]
	  }
 * 
 * 
 * @author zcz
 *
 */
public class Discribe implements DontObfuscateInterface{
	public String version;
	public String changes[];
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String[] getChanges() {
		return changes;
	}
	public void setChanges(String[] changes) {
		this.changes = changes;
	}
}
