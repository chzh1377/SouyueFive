package com.zhongsou.souyue.module;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.List;

@SuppressWarnings("serial")
public class FavoriteList extends ResponseObject {

    private boolean hasMore = false;
    private List<Favorite> items;
    
    @SuppressWarnings("unchecked")
	public FavoriteList(HttpJsonResponse response) {
    	hasMore = response.getHead().get("hasMore").getAsBoolean();
    	items = (List<Favorite>) new Gson().fromJson(response.getBodyArray(), new TypeToken<List<Favorite>>() {}.getType());
    	//设置布局类型
    	if (null != items && items.size() > 0) {
			for (int i = 0; i < items.size(); i++) {
				Favorite favorite = items.get(i);
				if(favorite != null) {
					String images = favorite.image();
					if(StringUtils.isNotEmpty(images)) {
						favorite.setFavoriteLayoutType(Favorite.LAYOUT_TYPE_ONE_PIC);
					}
				}
			}
		}
    }
    
    public boolean hasMore() {
        return hasMore;
    }
    public void hasMore_$eq(boolean hasMore) {
        this.hasMore = hasMore;
    }
    public List<Favorite> items() {
        return items;
    }
	public void items_$eq(List<Favorite> items) {
		this.items = items;
	}

}
