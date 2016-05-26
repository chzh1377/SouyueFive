package com.zhongsou.souyue.module;

import com.tuita.sdk.im.db.module.Contact;
import com.zhongsou.souyue.DontObfuscateInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobiContactEntity implements DontObfuscateInterface{
    private List<Contact> contasts = new ArrayList<Contact>();
    private Map<String, Integer> alphaIndex = new HashMap<String, Integer>();
    public List<Contact> getContasts() {
        return contasts;
    }
    public void setContasts(List<Contact> contasts) {
        this.contasts = contasts;
    }
    public Map<String, Integer> getAlphaIndex() {
        return alphaIndex;
    }
    public void setAlphaIndex(Map<String, Integer> alphaIndex) {
        this.alphaIndex = alphaIndex;
    }
    
}
