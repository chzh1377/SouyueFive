package com.speex.encode;


public class AudioLoaderConfigrition {

    public int connectionTimeOut = 5000;
    public int readTimeOut = 10000;
    public int threadNums = 3;
    
    public AudioLoaderConfigrition setConnectTimeOut(int t){
        this.connectionTimeOut = t;
        return this;
    }
    
    public AudioLoaderConfigrition setReadTimeOut(int t){
        this.readTimeOut = t;
        return this;
    }
}
