package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class WendaUpDown extends ResponseObject {

    private String answerId = "";
    private String questionId = "";
    private int upCount = 0;
    private int downCount = 0;
    private String md5 = "";

    public String answerId() {
        return answerId;
    }

    public void answerId_$eq(String answerId) {
        this.answerId = answerId;
    }

    public String questionId() {
        return questionId;
    }

    public void questionId_$eq(String questionId) {
        this.questionId = questionId;
    }

    public int upCount() {
        return upCount;
    }

    public void upCount_$eq(int upCount) {
        this.upCount = upCount;
    }

    public int downCount() {
        return downCount;
    }

    public void downCount_$eq(int downCount) {
        this.downCount = downCount;
    }

    public String md5() {
        return md5;
    }

    public void md5_$eq(String md5) {
        this.md5 = md5;
    }

}
