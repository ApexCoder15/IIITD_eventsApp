package com.example.buzzup;

import java.util.List;

public class HomeRvModel
{
    List<HomeRvModelInner> tagEventLists;
    String tagName;

    public HomeRvModel(List<HomeRvModelInner> e1, String e2)
    {
        this.tagEventLists = e1;
        this.tagName = e2;
    }

    public List<HomeRvModelInner> getTagEventLists()
    {
        return this.tagEventLists;
    }
    public void setTagEventLists(List<HomeRvModelInner> e)
    {
        this.tagEventLists = e;
    }

    public String getTagName()
    {
        return this.tagName;
    }
    public void setTagName(String e)
    {
        this.tagName = e;
    }
}
