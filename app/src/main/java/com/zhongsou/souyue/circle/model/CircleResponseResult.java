package com.zhongsou.souyue.circle.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.module.ResponseObject;
import com.zhongsou.souyue.net.HttpJsonResponse;

import java.util.List;

public class CircleResponseResult extends ResponseObject {

	public static int POSTS_TYPE_NOPIC = 0; // 帖子布局类型，无图
	public static int POSTS_TYPE_IMAGE_ONE = 1; // 帖子布局类型，一张
	public static int POSTS_TYPE_IMAGE_TWO = 2; // 帖子布局类型，两张
	public static int POSTS_TYPE_IMAGE_THREE = 3; // 帖子布局类型，三张
	public static int POSTS_TYPE_IMAGE_TOP = 4; // 帖子布局类型，置顶贴

	private List<CircleResponseResultItem> items;// 返回结果集

	private JsonArray item;// 返回单个对象
	
	private boolean existsTop = false; //是否存在置顶区布局
	

	public CircleResponseResult(HttpJsonResponse response) {
		transData(response,existsTop);
	}
	
	public CircleResponseResult(HttpJsonResponse response,int from) {
		transDataForPerson(response,existsTop);
	}

	public CircleResponseResult(HttpJsonResponse response ,boolean existsTop) {
//		this.existsTop = existsTop;
		transData(response,existsTop);
	}
	
	public void transDataForPerson(HttpJsonResponse response,boolean existsTop) {
		if (response.isJsonArray()) {
			items = new Gson().fromJson(response.getBodyArray(),
					new TypeToken<List<CircleResponseResultItem>>() {}.getType());
		} else {
			item = response.getBody().get("personalMblogList").getAsJsonArray();
			if (null != item) {
				items = new Gson().fromJson(item,
						new TypeToken<List<CircleResponseResultItem>>() {}.getType());
			}
		}

		if (items != null) {
			for (int index = 0; index < items.size(); index++) {
				CircleResponseResultItem item = items.get(index);
				
				if(existsTop) {
					if(item.getTop_status() == 1) {
						item.setPostLayoutType(CircleResponseResult.POSTS_TYPE_IMAGE_TOP);
						continue;
					}
				}
				// 判断图片数量
				if (item.getImages() != null && item.getImages().size() != 0) {
					if (item.getImages().size()==1){
						item.setPostLayoutType(CircleResponseResult.POSTS_TYPE_IMAGE_ONE);
					}else if(item.getImages().size()==2){
						item.setPostLayoutType(CircleResponseResult.POSTS_TYPE_IMAGE_TWO);
					}else if(item.getImages().size()==3){
						item.setPostLayoutType(CircleResponseResult.POSTS_TYPE_IMAGE_THREE);
					}
					
				} else {
						item.setPostLayoutType(CircleResponseResult.POSTS_TYPE_NOPIC);
				}
			}
			
		}
	}
	
	

	public void transData(HttpJsonResponse response,boolean existsTop) {
		if (response.isJsonArray()) {
			items = new Gson().fromJson(response.getBodyArray(),
					new TypeToken<List<CircleResponseResultItem>>() {}.getType());
		} else {
			item = response.getBody().getAsJsonArray("body");
			if (null != item) {
				items = new Gson().fromJson(item,
						new TypeToken<List<CircleResponseResultItem>>() {}.getType());
			}
		}

		if (items != null) {
			for (int index = 0; index < items.size(); index++) {
				CircleResponseResultItem item = items.get(index);
				
				if(existsTop) {
					if(item.getTop_status() == 1) {
						item.setPostLayoutType(CircleResponseResult.POSTS_TYPE_IMAGE_TOP);
						continue;
					}
				}
				// 判断图片数量
				if (item.getImages() != null) {
                    item.setPostLayoutType(getLayoutType(item.getImages().size()));
				}
			}
			
		}
	}
	
	
	public List<CircleResponseResultItem> getItems() {
		return items;
	}

	public void setItems(List<CircleResponseResultItem> items) {
		this.items = items;
	}

	public JsonArray getItem() {
		return item;
	}

	public void setItem(JsonArray item) {
		this.item = item;
	}
	
	public boolean isExistsTop() {
		return existsTop;
	}


	public void setExistsTop(boolean existsTop) {
		this.existsTop = existsTop;
	}

    public static int getLayoutType(int size){
        return size > POSTS_TYPE_IMAGE_THREE ? POSTS_TYPE_IMAGE_THREE : size;
    }

	
}
