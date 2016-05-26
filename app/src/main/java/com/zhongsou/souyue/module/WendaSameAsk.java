package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class WendaSameAsk extends ResponseObject {

    private String questionId = "";
    private String md5 = "";
    private int sameAskCount = 0;

    public String questionId() {
        return questionId;
    }

    public void questionId_$eq(String questionId) {
        this.questionId = questionId;
    }

    public String md5() {
        return md5;
    }

    public void md5_$eq(String md5) {
        this.md5 = md5;
    }

    public int sameAskCount() {
        return sameAskCount;
    }

    public void sameAskCount_$eq(int sameAskCount) {
        this.sameAskCount = sameAskCount;
    }


}
