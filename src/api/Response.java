package api;

import java.util.ArrayList;

public class Response
{
    public String language;
    public ArrayList<Tweet> data; //"data" is key, hashmap is value

    public Response(int tweetnum, String language)
    {
        this.language = language;
        data = new ArrayList<>(tweetnum);
    }

    public Response()
    {
        data = new ArrayList<>();
        language = "auto";
    }

    void addTweet(String tweet)
    {
        data.add(new Tweet(tweet));
    }
}