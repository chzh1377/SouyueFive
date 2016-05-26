package com.speex.encode;

import java.util.UUID;

public class FileNameGenerate {
    
    public static String getName(String name){
        return MD5.Md5(name);
    }

    public static boolean isHttpUrl(String name) {
        return name.startsWith("http://");
    }

    public static String generateId() {
        return MD5.Md5(UUID.randomUUID().toString())+".spx";
    }

    public static String generateId(String string) {
        return MD5.Md5(UUID.randomUUID().toString())+"."+string;
    }
}
