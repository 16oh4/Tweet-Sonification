package api;

import java.util.HashMap;

public class Tweet
{

    public String text;
    public int polarity;
    public HashMap<String, String> meta;


    Tweet()
    {
        initMaps();
    }
    Tweet(String text)
    {
        initMaps();
        this.text = text;

    }
    void initMaps()
    {
        meta = new HashMap<>();
    }
}
