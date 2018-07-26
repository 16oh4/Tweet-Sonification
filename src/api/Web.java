package api;
import com.google.gson.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import sound.Sonify;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Web
{
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';
    static CloseableHttpClient client;
    static HttpPost request;
    static HttpResponse response;
    static BufferedReader rd;

    static Response server;
    static Model send, send2, drake;
    static Gson gson;

    static Sonify sound;
    public static void main(String[] args) throws IOException
    {
        gson = new GsonBuilder().create();

        send = new Model();
        drake = new Model();

//        send.addTweet("Thank you! https://t.co/TD0rYcWN8C");
//
//        send.addTweet("I hate the weather");
//
//
//        send.addTweet("Love is great");
//        send.addTweet("God is love");
//        send.addTweet("Thank you! https://t.co/TD0rYcWN8C");
//
//        send.addTweet("I hate the weather");
//        send.addTweet("Thank you! https://t.co/TD0rYcWN8C");
//
//        send.addTweet("I hate the weather");
//
//        send.addTweet("I am very happy.");
//        send.addTweet("ICongratulations to the Houston @Astros, 2017 #WorldSeries Championsâš¾ï¸\u008F#HoustonStrong #EarnHistory https://t.co/BP7OEzdhFi");

        outData();//read drake tweets

        connectAPI();

        getServerData();

        sonifyData();

        writeFile();

        System.exit(0);
    }

    static void connectAPI() throws IOException
    {
        String toServer = gson.toJson(drake);
        System.out.println("TO SERVER: >\n" + toServer);
        // create client
        client = HttpClients.createDefault();

        // create the request
//        request = new HttpPost("http://www.sentiment140.com/api/bulkClassifyJson?appid=ohf614@my.utsa.edu");
        request = new HttpPost("http://www.sentiment140.com/api/bulkClassifyJson");
        //convert string to server understandable
        StringEntity entity = new StringEntity(toServer);

        //Set request format with json data
        request.setEntity(entity);

        //NOT NEEDED but sets input for server
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        // execute the request and capture the response object
        response = client.execute(request);

        // print status code
        System.err.println("Status: " + response.getStatusLine().getStatusCode());

        // Get the response payload/data
        rd = new BufferedReader  (new InputStreamReader( response.getEntity().getContent()));
    }

    static void getServerData() throws IOException
    {
        String line, sd;
        StringBuilder serverData = new StringBuilder();
        while ((line = rd.readLine()) != null)
        {
            serverData.append(line);
        }
        sd = serverData.toString();
        System.out.println("FROM SERVER: >\n" + sd);

        //convert server data to JSON
        JsonObject jo;
        JsonParser p = new JsonParser();
        jo = p.parse(sd).getAsJsonObject();
        //System.out.println("Jo:\n"+ jo);

        //store JSON data into response
        server = gson.fromJson(jo.toString(), Response.class); //store response from json

        System.out.println(server.data.get(0).polarity);
        //System.out.println(server);
    }

    static void sonifyData() throws FileNotFoundException {
        sound = new Sonify(server);
        sound.turnUp();

    }

    public static void writeFile() throws IOException
    {

        String outcsv = "C:/Users/admin/Documents/Java/Tweet Sonification/trumpout.csv";
        String poltemp = "";
        String notetemp = "";

        FileWriter writer = new FileWriter(outcsv);

        for(int i=0; i < sound.inotes.size(); i++)
        {
            poltemp = Integer.toString(sound.pol.get(i));
            notetemp = Integer.toString(sound.inotes.get(i));
            System.out.println(poltemp);
            writeLine(writer, Arrays.asList(poltemp, notetemp));
        }


        writer.flush();
        writer.close();
    }
    public static void outData() throws FileNotFoundException, IOException
    {
//        String csvFile = "C:/Users/admin/Desktop/cudi.csv";
        String csvFile = "C:/Users/admin/Documents/Java/Tweet Sonification/trump.csv";
        Scanner scanner = new Scanner(new File(csvFile));
        int g =0;
        while (scanner.hasNext())
        {
            List<String> line = parseLine(scanner.nextLine());
            System.out.println("LINE READ: > " + line.get(6));
            g++;
            drake.addTweet(line.get(6));
        }
        System.out.println(g);
        scanner.close();


    }

    
    /*BEGIN SHARED CODE:
    https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
    */
    
    public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators) {
        return parseLine(cvsLine, separators, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList<>();

        //if empty, return!
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {

                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        result.add(curVal.toString());

        return result;
    }

    public static void writeLine(Writer w, List<String> values) throws IOException {
        writeLine(w, values, DEFAULT_SEPARATOR, ' ');
    }

    public static void writeLine(Writer w, List<String> values, char separators) throws IOException {
        writeLine(w, values, separators, ' ');
    }

    //https://tools.ietf.org/html/rfc4180
    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    public static void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

        boolean first = true;

        //default customQuote is empty

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());
    

    }
    
    /*END SHARED CODE:
    https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
    */
}
