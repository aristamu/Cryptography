
package cryptography;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.logging.*;
//import javafx.scene.shape.Path;

/**
 * @author Arista
 */
public class Dictionary 
{
    private HashSet <String> wordsSet = new HashSet <> ();

    /**
     * Dictionary constructor, creates a set of word in EnglishWords.txt
     * @throws IOException if text file can't be found (shouldn't happen)
     */
    public Dictionary() throws IOException
    {
        
        URL input = this.getClass().getResource ("EnglishWords.txt");
        try 
        {
            File file = new File (input.toURI());
            BufferedReader reader = new BufferedReader (new FileReader (file));
            
            String line;
            while ((line = reader.readLine()) != null)
            {
                wordsSet.add (line);
            }
        } 
        catch (URISyntaxException ex) 
        {
            Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * returns true if the dictionary contains word
     * @param word word to check in dictionary
     * @return true if word is in dictionary
     */
    public boolean contains(String word)
    {
        return wordsSet.contains(word);
    }

    /**
     * gets all words in the dictionary
     * @return HashSet containing dictionary words
     */
    public HashSet<String> getWordsSet () 
    {
        return wordsSet;
    }
}
