package com.example.buzzup;

public class HomeRvModelInner
{
    String eventImageUrl = "";
    String eventName = "";

    public HomeRvModelInner(String e1, String e2)
    {
        this.eventImageUrl = e1;
        this.eventName = e2;
    }

    public String getEventImageUrl()
    {
        return this.eventImageUrl;
    }
    public void setEventImageUrl(String e)
    {
        this.eventImageUrl = e;
    }
    public String getEventName()
    {
        return this.eventName;
    }
    public void setEventName(String e)
    {
        this.eventName = e;
    }
}
