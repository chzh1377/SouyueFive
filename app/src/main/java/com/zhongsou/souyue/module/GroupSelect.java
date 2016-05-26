package com.zhongsou.souyue.module;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @auther: qubian
 * @data: 2016/4/1.
 */
public class GroupSelect {
    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }


    public static List<GroupSelect> createList(int size)
    {
        List<GroupSelect> list = new ArrayList<>();
        for(int i =0 ;i<size ;i++)
        {
            GroupSelect s= new GroupSelect();
            s.setIsSelect(false);
            list.add(s);
        }
        return list;
    }
}
