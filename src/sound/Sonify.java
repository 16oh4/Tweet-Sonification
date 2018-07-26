package sound;
import api.Response;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.theory.Note;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Sonify
{
    Response spout;

    Random rand;
    public ArrayList<Integer> pol;
    ArrayList<String> notes;
    public ArrayList<Integer> inotes;
    ArrayList<Pattern> pat;

    ArrayList<Integer> octaveNotes; //57-69 A4 - A5
    ArrayList<String> scale;
    HashMap<Integer, String> scaleMap;

    int baseNote = -1; //silence
    int numTweets;
    int index = 0; //analyze how many good emotions or bad emotions theres been

    public Sonify(Response r)
    {
        spout = r;
        pol = new ArrayList<>();
        notes = new ArrayList<>();
        inotes = new ArrayList<>();
        pat = new ArrayList<>();
        rand = new Random(System.currentTimeMillis()); //generate a new seed every time
        octaveNotes = new ArrayList<>(Arrays.asList(57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69));
//        octaveNotes = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5 , 6, 7, 8, 9, 10, 11));
        scale = new ArrayList<>(Arrays.asList("A5","B5","C5","D5","E5","F5","G5"));
        numTweets = spout.data.size();

        //init methods

        getPols(); //read polarities from data and store in arraylist
        setRange();
//        buildNotes();
    }

    void getPols()
    {
        for(int i=0; i<numTweets; i++)
        {
            pol.add(spout.data.get(i).polarity);
        }
    }
    void buildNotes()
    {
        index = 0;
        String n="";
        StringBuilder b = new StringBuilder();

        n=scale.get(3); //first note
        notes.add(scale.get(3));

        b.append(n);
        b.append(" ");
        n="";

        for(int i=1; i<numTweets; i++)
        {
            n=getPat(pol.get(i), notes.get(i-1));
            notes.add(n);

            b.append(n);
            b.append(" ");
        }
        pat.add(new Pattern(b.toString()));
    }
    String getPat(int polar, String pnote)
    {
        switch(polar)
        {
            case 0:

//                return "I[Piano] Cmin Bbmaj Amin Gmin ";
//                return "C3";
            case 2:
//                return "I[Piano] C C# ";
//                return "E4";
            case 4:
//                return "I[Piano] Cmaj Dmin Emaj Fmaj ";
//                return "A5";
            default:
                return "";
        }
    }
    void setRange() //set notes to be played in song
    {
        int r = rand.nextInt(12); //Give number between 0 and 12 exclusive
        int tempnote = 0; //holds next note played
        StringBuilder sb = new StringBuilder();
        String temp = "";
        index = 0;

        baseNote = octaveNotes.get(r); //get random note from array

        inotes.add(baseNote); //basenote is element 0 in array

        for(int i=1; i<numTweets; i++)
        {
//            System.out.println("pol " + pol.get(i) + " pnote " + inotes.get(i-1));
            tempnote = nextNote(pol.get(i), inotes.get(i-1));
//            System.out.println("Tempnote " + tempnote);

            temp = Integer.toString(tempnote);
//            System.out.println(temp);



            //update class arrays
            inotes.add(tempnote);
            sb.append(temp);
            sb.append(" ");

        }
        pat.add(new Pattern(sb.toString()));
        System.out.println(pat.get(0)); //add pattern to pattern array

//        System.out.println(Note.getFrequencyForNote(baseNote));
    }
    int nextNote(int polar, int pnote)
    {
        switch(polar)
        {
            case 0:
                index--;
                if(index == -4)
                {
                    index = 0;
                    if(pnote-12>=0) return pnote-12; //reduce an octave if we receive 3 neg. in a row
                    else return 0;
                }
                if(pnote!=0) return pnote-1;
                return pnote;
//                return "C3";
            case 2:
                return pnote;
//                return "E4";
            case 4:
                index++;
                if(index == 4)
                {
                    index = 0;
                    if(pnote+12<=127) return pnote+12;
                    else return 127;
                }
                if(pnote!= 127) return pnote+1;
                return pnote;
//                return "A5";
            default:
                return -1;
        }
    }
    public void turnUp()
    {
        Player play1 = new Player();
//        for(int i=0; i<pat.size(); i++)
//        {
//            play1.play("I[Piano] " + pat.get(i));
//        }
        for(int j=0; j<inotes.size(); j++)
        {
            System.out.println(spout.data.get(j).polarity + " > "+spout.data.get(j).text);
            play1.play(inotes.get(j) + "qqq");

        }
    }



}