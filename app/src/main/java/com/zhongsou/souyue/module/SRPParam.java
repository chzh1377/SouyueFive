package com.zhongsou.souyue.module;


import java.util.List;



/**
 * User: zhangliang01@zhongsou.com
 * Date: 11/1/13
 * Time: 5:30 PM
 */
public class SRPParam extends ResponseObject {
    public String groupName;
    public String groupId;
    public List<SRP> srp;
    public SRPParam(){}
    public SRPParam(String groupName, String groupId, List<SRP> srp) {
        this.groupName = groupName;
        this.groupId = groupId;
        this.srp = srp;
    }
}
