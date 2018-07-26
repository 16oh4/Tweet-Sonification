package api;

import java.util.ArrayList;
import java.util.HashMap;

public class Model
{

    //String language;
    public ArrayList<HashMap<String, String>> data; //"data" is key, hashmap is value
    //HashMap<String, String> temp;

    //final String TCONST = "text";

    public Model(int tweetnum, String language)
    {
        //this.language = language;
        data = new ArrayList<>(tweetnum);
        //temp = new HashMap<>();
    }

    public Model()
    {
        data = new ArrayList<>();
    }

    void addTweet(String tweet)
    {
        HashMap<String, String> temp = new HashMap<>();
        temp.put("text", tweet);
        data.add(temp);
    }
}

/*

{

    "data": [{"string": "string" }, {"string, "string"} ] ,
        "language": "auto"
        }
        */
