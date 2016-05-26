package com.zhongsou.souyue.module;

import com.google.gson.JsonObject;

import java.util.ArrayList;

public class PlazaHome extends ResponseObject{
    public PlazaHome(JsonObject json) {
        
    }
    
    public PlazaHomeHead head;
    public ArrayList<PlazaCate> body;
    
    public class PlazaHomeHead{
        int status;
        public ArrayList<AdItem> adList;
        public Score score;
    }
    
    public class Score{
        public String title;
        public String url;
    }
    public class AdItem{
        public String category;
        public String title;
        public String image;
        public String url;
        public String keyword;
        public String srpId;
        public String jumpTo;
    }
    
    public class PlazaCate{
        public String category;
        public String title;
        public String description;
        public String date;
        public String count;
        public String image;
        public String keyword;
        public String srpId;
    }
}
