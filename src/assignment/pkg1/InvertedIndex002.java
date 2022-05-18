/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment.pkg1;

/**
 *
 * @author hp
 */
//package com.company;
import java.io.*;
import java.util.*;

//=====================================================================

class DictEntry2 {

    public int doc_freq = 0; // number of documents that contain the term
    public int term_freq = 0; //number of times the term is mentioned in the collection
    public HashSet<Integer> postingList;

    DictEntry2() {
        postingList = new HashSet<Integer>();
    }
}

//=====================================================================
class Index2 {

    //--------------------------------------------
    Map<Integer, String> sources;  // store the doc_id and the file name
    HashMap<String, DictEntry2> index; // THe inverted index
    //--------------------------------------------

    Index2() {
        sources = new HashMap<Integer, String>();
        index = new HashMap<String, DictEntry2>();
    }

    //---------------------------------------------
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry2 dd = (DictEntry2) pair.getValue();
            HashSet<Integer> hset = dd.postingList;// (HashSet<Integer>) pair.getValue();
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
            Iterator<Integer> it2 = hset.iterator();
            while (it2.hasNext()) {
                System.out.print(it2.next() + ", ");
            }
            System.out.println("");
            //it.remove(); // avoids a ConcurrentModificationException
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size());
    }

    //-----------------------------------------------
    public void buildIndex(String[] files) {
        int i = 0;
        for (String fileName : files) {
            try ( BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                sources.put(i, fileName);
                String ln;
                while ((ln = file.readLine()) != null) {
                    String[] words = ln.split("\\W+");
                    for (String word : words) {
                        word = word.toLowerCase();
                        // check to see if the word is not in the dictionary
                        if (!index.containsKey(word)) {
                            index.put(word, new DictEntry2());
                        }
                        // add document id to the posting list
                        if (!index.get(word).postingList.contains(i)) {
                            index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term
                            index.get(word).postingList.add(i); // add the posting to the posting:ist
                        }
                        //set the term_fteq in the collection
                        index.get(word).term_freq += 1;
                    }
                }
                printDictionary();
            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            i++;
        }
    }

    //--------------------------------------------------------------------------
    // query inverted index
    // takes a string of terms as an argument
    public String find(String phrase) {
        String[] words = phrase.split("\\W+");
        HashSet<Integer> res = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        for (String word : words) {
            res.retainAll(index.get(word).postingList);
        }
        if (res.size() == 0) {
            System.out.println("Not found");
           return "";
        }
        String result = "Found in: \n";
        for (int num : res) {
            result += "\t" + sources.get(num) + "\n";
        }
        return result;
    }

    //----------------------------------------------------------------------------
    HashSet<Integer> intersect(HashSet<Integer> pL1, HashSet<Integer> pL2) {
        HashSet<Integer> answer = new HashSet<>();
        Iterator it1 = pL1.iterator();
        int value1 = (int) it1.next();
        while(it1 != null){
            Iterator it2 = pL2.iterator();
            int value2 = (int) it2.next();
            while(it2 != null){
                if(value1 == value2){
                    answer.add(value1);
                    //break;
                }
                if(it2.hasNext()){
                    value2 = (int) it2.next();
                } else {
                    it2 = null;
                }
            }
            if(it1.hasNext()){
                value1 = (int) it1.next();
            } else {
                it1 = null;
            }
        }
        return answer;
    }
    //-----------------------------------------------------------------------

    public String find_01(String phrase) { // 2 term phrase  2 postingsLists
        String result = "";
        String[] words = phrase.split("\\W+");
        // 1- get first posting list
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        // 2- get second posting list
        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
        // 3- apply the algorithm
        HashSet<Integer> answer = intersect(pL1, pL2);
        System.out.println("Found in: ");
        for (int num : answer) {
            result += "\t" + sources.get(num) + "\n";
        }
        return result;
    }
//-----------------------------------------------------------------------

    public String find_02(String phrase) { // 3 lists
        String result = "";
        // write you code here
        String[] words = phrase.split("\\W+");
        // 1- get first posting list
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        // 2- get second posting list
        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
        HashSet<Integer> pL3 = new HashSet<Integer>(index.get(words[2].toLowerCase()).postingList);
        // 3- apply the algorithm
        HashSet<Integer> answer = intersect(pL1, pL2);
        answer = intersect(answer, pL3);
        System.out.println("Found in: ");
        for (int num : answer) {
            //System.out.println("\t" + sources.get(num));
            result += "\t" + sources.get(num) + "\n";
        }
        return result;

    }
    //-----------------------------------------------------------------------

    public String find_03(String phrase) { // any mumber of terms non-optimized search
        String result = "";
        String[] words = phrase.split("\\W+");
        int size = words.length;
        HashSet <Integer> answer = new HashSet <Integer>();
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        if(size==1)
        {
            answer=pL1;
        }
        else if(size == 2)
        {
            HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
            answer = intersect(pL1,pL2);
        }
        else if(size > 2)
        {
            HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
            answer = intersect(pL1,pL2);
            for(int i=0 ; i<words.length ; i++)
            {
                HashSet<Integer> x = new HashSet<Integer>(index.get(words[i].toLowerCase()).postingList);
                answer = intersect(x,answer);
            }
        }

        for (int num : answer) {
            result += "\t" + sources.get(num) + "\n";
        }
        return result;

    }
    //-----------------------------------------------------------------------

    public String find_04(String phrase) { // any mumber of terms optimized search
        String result = "";
        String[] words = phrase.split("\\W+");
        int size = words.length;
        HashSet <Integer> answer = new HashSet <Integer>();
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        //HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
        if(size==1)
        {
            answer=pL1;
        }
        else if(size == 2)
        {
            HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
            answer = intersect(pL1,pL2);
        }
        else if(size > 2)
        {
            int arr[];
            arr = new int[words.length];
            String sorted[] = new String[words.length] ;
            for (int i=0 ; i<words.length ;i++)
            {
                arr[i] = index.get(words[i].toLowerCase()).doc_freq;

                sorted[i] = words[i];
            }
            int n = words.length;

            // One by one move boundary of unsorted subarray
            for (int i = 0; i < n-1; i++)
            {
                // Find the minimum element in unsorted array
                int min_idx = i;
                for (int j = i+1; j < n; j++)
                    if (arr[j] < arr[min_idx])
                        min_idx = j;

                // Swap the found minimum element with the first
                // element
                int temp = arr[min_idx];
                arr[min_idx] = arr[i];
                arr[i] = temp;
                if(index.get(words[i].toLowerCase()).doc_freq==temp)
                {
                    sorted[i] = words[i];
                }
            }
            HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
            answer = intersect(pL1,pL2);
            HashSet<Integer> x = null;
            for(int i=3 ;i<words.length;i++ )
            {
                x = new HashSet<Integer>(index.get(sorted[i].toLowerCase()).postingList);
                answer = intersect(x,answer);
            }
        }
        for (int num : answer) {
            result += "\t" + sources.get(num) + "\n";
        }
        return result;
    }
    //-----------------------------------------------------------------------

    // This function compares the estimated time in each find function and we will notice that the elapsed time
    //in the optimized search is less than the non optimized search because in the optimized search we start with the least 
    //document frequency
    public void compare(String phrase) { // optimized search
        long iterations=10000000;
        String result = "";
        long startTime = System.currentTimeMillis();
        for (long i = 1; i < iterations; i++) {
            result = find(phrase);

        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(" (*) elapsed = " + estimatedTime+" ms.");

        System.out.println(" result = " + result);
        startTime = System.currentTimeMillis();
        for (long i = 1; i < iterations; i++) {
            result = find_03(phrase);
        }
        estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(" (*) Find_03 non-optimized intersect  elapsed = " + estimatedTime +" ms.");

        System.out.println(" result = " + result);

        startTime = System.currentTimeMillis();
        for (long i = 1; i < iterations; i++) {
            result = find_04(phrase);
        }
        estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(" (*) Find_04 optimized intersect elapsed = " + estimatedTime+" ms.");
        System.out.println(" result = " + result);
    }
}


//=====================================================================
public class InvertedIndex002 {

    public static void main(String args[]) throws IOException {
        Index2 index = new Index2();
      
        index.buildIndex(new String[]{
            "C:\\Users\\hp\\Documents\\3.2\\DR\\hana.txt",
            "C:\\Users\\hp\\Documents\\3.2\\DR\\hala.txt",
        });
       
        index.compare("oh la");
        
    }
}
