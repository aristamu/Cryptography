package cryptography;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Arista Mueller
 * !! Currently has some bugs
 */
public class CryptogramSolve 
{

    Dictionary dict;
    ArrayList <ArrayList> possibleSolutions = new ArrayList ();
    
    /**
     * Begins the solution
     * @param cipheredMessage message to decipher
     * @throws IOException if dictionary text file not found
     */
    public CryptogramSolve (String cipheredMessage) throws IOException
    {
        //!! Add string cleaning
        startSolve (cipheredMessage);
    }
    
    
    /**
     * 
     * @param input
     * @throws IOException 
     */
    public void startSolve (String input) throws IOException
    {
        dict = new Dictionary ();;
        String [] cipheredWords;
        
        cipheredWords = input.split(" ");
        cipheredWords = removeDuplicates (cipheredWords);
        
        for (int i = 0; i < cipheredWords.length; i ++)
        {
            solutionsFromMatches (matchWordPattern (cipheredWords [i], matchWordLength (cipheredWords [i])), i, cipheredWords [i]);
        }
        
        HashMap smallest;
        
        solve (possibleSolutions, new HashMap (), cipheredWords);
        
    }
    
    
    /**
     * Recursively solves by trying possible ciphers
     * @param possibleMatches all possible words that could match
     * @param currentSolution solution cipher
     * @param cipheredWords original coded message
     */
    public void solve (ArrayList <ArrayList> possibleMatches, HashMap currentSolution, String [] cipheredWords)
    {
        ArrayList shortestWordSet = getSmallestNumberSolutions (possibleMatches);
        
        possibleMatches = removeInvalidSolutions (currentSolution, possibleMatches);
                
        for (int i = 0; i < shortestWordSet.size (); i ++)
        {
            HashMap nextPossibility = (HashMap) shortestWordSet.get (i);
            
            HashMap merged = new HashMap ();
            merged.putAll (nextPossibility);
            merged.putAll (currentSolution);
            
            if (possibleMatches.isEmpty ())
            {
                break;
            }
            else if (hasDuplicateValues (merged))
            {
                
            }
            else
            {
                ArrayList <ArrayList> newMatches = new ArrayList ();
                for (int j = 0; j < possibleMatches.size (); j ++)
                {
                    newMatches.add (possibleMatches.get (j));
                }
                newMatches.remove (shortestWordSet);
                solve (newMatches, merged, cipheredWords);
            }
        }
        
        if (possibleMatches.isEmpty ())
        {
            if (checkResult (constructNaturalMessage (cipheredWords, currentSolution)))
            {
                System.out.println (constructNaturalMessage (cipheredWords, currentSolution));
            }
            
        }
    }
    
    
    /**
     * Determines if solution has more than one ciphered letter representing a natural letter
     * @param currentSolution solution to test
     * @return true if currentSolution has repeat values
     */
    public boolean hasDuplicateValues (HashMap currentSolution)
    {
        Collection values = currentSolution.values ();
        Iterator iter = values.iterator ();
                
        char next;
        while (iter.hasNext ())
        {
            next = (char) iter.next ();
            if (Collections.frequency (values, next) > 1)
            {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * Does a final check to ensure all words are real English words
     * @param naturalMessage decrypted message
     * @return false if natural message contains non-existent word
     */
    public boolean checkResult (String naturalMessage)
    {
            String [] naturalWords = naturalMessage.split (" ");
            for (int i = 0 ; i < naturalWords.length; i ++)
            {
                if (!dict.contains (naturalWords [i]))
                {
                    return false;
                }
            }
            return true;
    }
    
    /**
     * Constructs decrypted message to display to user
     * @param cipheredWords decrypted message
     * @param cipher solution cipher used to decrypt the message
     * @return decrypted message
     */
    public String constructNaturalMessage (String [] cipheredWords, HashMap cipher)
    {
        String naturalMessage = "";
        for (int i = cipheredWords.length - 1; i >= 0 ; i --)
        {
            for (int j = 0; j < cipheredWords [i].length (); j ++)
            {
                naturalMessage += cipher.get (cipheredWords [i].charAt (j));
            }
            naturalMessage += " ";
        }
        return naturalMessage;
    }
    
    
    /**
     * Finds word with least number of possible matches
     * @param solutions all possible matches for all words
     * @return smallest ArrayList from solutions
     */
    public ArrayList getSmallestNumberSolutions (ArrayList <ArrayList> solutions)
    {
        ArrayList currentMin = new ArrayList ();
        Iterator iter = solutions.iterator ();
        ArrayList next;
        while (iter.hasNext ())
        {
            next = (ArrayList) iter.next ();
            if (next.isEmpty ())
            {
                iter.remove ();
            }
        }
        
        for (int i = 0; i < solutions.size (); i ++)
        {
            if (currentMin.isEmpty () || (currentMin.size () > solutions.get (i).size ()))
            {
                currentMin = solutions.get (i);
                
            }
        }
        return currentMin;
    }
    
    
    /**
     * Gets pattern of characters from word
     * @param word word to get pattern for
     * @return pattern with a unique number assigned to each unique letter
     */
    public Integer [] getWordPattern (String word)
    {
        HashMap usedLetters = new HashMap ();
        Integer [] wordPattern = new Integer [word.length ()];
        char nextChar;
        int counter = 0;
        
        for (int i = 0; i < word.length(); i ++)
        {
            nextChar = word.charAt (i);
            if (usedLetters.containsKey(nextChar))
            {
                wordPattern [i] = (int) usedLetters.get (nextChar);
            }
            else
            {
                wordPattern [i] = counter;
                usedLetters.put (nextChar, counter);
                counter ++;
            }
        }
        
        return wordPattern;
    }
    
    /**
     * gets all words in the dictionary that are same length as word
     * @param word word to compare to
     * @return all words with same length as word
     */
    public HashSet matchWordLength (String word)
    {
        HashSet <String> allWords = dict.getWordsSet();
        int wordLength = word.length();
        Iterator iter = allWords.iterator();
        HashSet <String> wordsLengthMatched = new HashSet ();
        String next;
        
        while (iter.hasNext ())
        {
            next = (String) iter.next ();
            if (next.length () == wordLength)
            {
                wordsLengthMatched.add(next);
            }
        }
        return wordsLengthMatched;
    }
    
    /**
     * Creates a set of all matches with the same word pattern
     * @param word word with desired pattern
     * @param possibleMatches words that could match (i.e. same length words)
     * @return all words containing the same pattern as word
     */
    public HashSet matchWordPattern (String word, HashSet possibleMatches)
    {
        Iterator iter = possibleMatches.iterator();
        String next;
        HashSet <String> newMatches = new HashSet ();
        
        while (iter.hasNext())
        {
            next = (String) iter.next ();
            if (Arrays.equals(getWordPattern (word), getWordPattern (next)))
            {
                newMatches.add (next);
            }
        }
        
        return newMatches;
    }
    
    
    /**
     * Generate possible matches of the ciphered and natural alphabet
     * @param matches all words to generate solutions from
     * @param index 
     * @param cipheredWord 
     */
    public void solutionsFromMatches (HashSet <String> matches, int index, String cipheredWord)
    {
        possibleSolutions.add (index, new ArrayList ());
        Iterator iter = matches.iterator ();
        String next = "";
        
        while (iter.hasNext ())
        {
            next = (String) iter.next ();
            HashMap matchedLetters = new HashMap ();
            
            for (int i = 0; i < next.length (); i ++)
            {
                if (!matchedLetters.containsValue (next.charAt(i)))
                {
                    matchedLetters.put (cipheredWord.charAt (i), next.charAt (i));
                }
            }
            
            possibleSolutions.get (index).add (matchedLetters);
        }
    }
    
    
    /**
     * Removes all possible solutions that have different key value pairs in knownLetters
     * @param knownLetters all letter associations we are currently presuming to be correct
     * @param solutions possible solutions from dictionary words
     * @return all possible solutions after invalid ones are removed
     */
    public ArrayList <ArrayList> removeInvalidSolutions (HashMap knownLetters, ArrayList <ArrayList> solutions)
    {
        Set confirmedCiphered = knownLetters.keySet ();
        Iterator iter = confirmedCiphered.iterator ();
        char nextKey;
        char nextValue;
        HashMap nextSolutionSet;     
        
        while (iter.hasNext ())
        {
            nextKey = (char) iter.next ();
            nextValue = (char) knownLetters.get (nextKey);
            
            for (int i = 0; i < solutions.size (); i ++)
            {
                for (int j = 0; j < solutions.get (i).size (); j ++)
                {
                    nextSolutionSet = (HashMap) solutions.get (i).get (j);
                    if (nextSolutionSet.containsKey (nextKey)
                            && (char) nextSolutionSet.get (nextKey) != nextValue)
                    {
                        solutions.get (i).remove (nextSolutionSet);
                    }
                }
            }
        }
        
        return solutions;
    }
    
    
    /**
     * Removes any duplicate words to reduce run time
     * @param cipheredWords words in the coded message
     * @return coded message with all duplicate words removed
     */
    public String [] removeDuplicates (String [] cipheredWords)
    {
        Set wordSet = new HashSet (Arrays.asList (cipheredWords));
        Iterator iter = wordSet.iterator ();
        String [] noDups = new String [wordSet.size ()];
        
        for (int i = 0; i < noDups.length; i ++)
        {
            noDups [i] = iter.next ().toString ();
        }
        
        return noDups;
    }
}
