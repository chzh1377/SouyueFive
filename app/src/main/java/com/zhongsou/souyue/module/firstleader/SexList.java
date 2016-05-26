package com.zhongsou.souyue.module.firstleader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.DontObfuscateInterface;

import java.util.List;
import java.util.Random;

/**
 * Created by zyw on 2016/3/24.
 */
public class SexList implements DontObfuscateInterface {
    public static final String TAG = SexList.class.getSimpleName();
    private List<SexListEntity> sexList;
    private int lastSelectedItem = -1;

    public int getLastSelectedItem() {
        return lastSelectedItem;
    }

    public void setLastSelectedItem(int lastSelectedItem) {
        this.lastSelectedItem = lastSelectedItem;
    }

    public void setSexList(List<SexListEntity> sexList) {
        this.sexList = sexList;
    }

    public List<SexListEntity> getSexList() {
        return sexList;
    }

    public static class SexListEntity implements DontObfuscateInterface{
        private String              name;
        private String              key;
        private String              image;
        private List<CharacterList> characterList;

        public List<CharacterList> getCharacterList() {
            return characterList;
        }

        public void setCharacterList(List<CharacterList> characterList) {
            this.characterList = characterList;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public String getKey() {
            return key;
        }

        public String getImage() {
            return image;
        }
    }

    public static SexList parseSexList(JsonObject datas) {
        Gson    gson    = new Gson();
        SexList sexList = gson.fromJson(datas.get("love"), SexList.class);
        for (SexList.SexListEntity entity : sexList.getSexList()) {
            String key = entity.getKey();
            List<CharacterList> characterList = gson.fromJson(datas.getAsJsonObject("love").get(key), new TypeToken<List<CharacterList>>() {
            }.getType());
            for (CharacterList c : characterList) {
                c.setSex(entity.getName());
                List<ChildGroupItem> items = gson.fromJson(datas.getAsJsonObject("childGroupMap").get(c.getSex().concat("_").concat(c.getName())), new TypeToken<List<ChildGroupItem>>() {
                }.getType());
                c.setChilds(items);
            }
            entity.setCharacterList(characterList);
        }
        return sexList;
    }
}
