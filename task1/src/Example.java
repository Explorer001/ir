import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class Example {
	
   static String indexDir = "Index";
   static String dataDir = "Data";
   static String in_query;
   Indexer indexer;
   Searcher searcher;
   static boolean bm25;

   public static void main(String[] args) {
      Example tester;
      //check the incoming String for the correct input and initialize the values
      if (args.length < 4 || args.length > 4) {
    	  System.out.println("Usage: java -jar IR_P01.jar [Path/to/document/folder] [Path/to/index/folder] [VS/OK] <query>");
    	  return;
      } else {
    	  dataDir = args[0];
    	  indexDir = args[1];
    	  if (args[2].toLowerCase().equals("ok")) {
    		  System.out.println("Using OK");
    		  bm25 = true;
    	  } else {
    		  System.out.println("Using VS");
    		  bm25 = false;
    	  }
    	  in_query = args[3];
      }
      //index the given path and search with the given query
      try {
    	 System.out.println("----------------Indexing-----------------");
         tester = new Example();
         tester.createIndex();
         System.out.println("----------------Searching----------------");
         tester.search(in_query);
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ParseException e) {
         e.printStackTrace();
      }
   }

   private void createIndex() throws IOException {
	  //call the indexer with the given values 
      indexer = new Indexer(indexDir, bm25);
      int numIndexed;
      long startTime = System.currentTimeMillis();	
      numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
      long endTime = System.currentTimeMillis();
      indexer.close();
      System.out.println(numIndexed+" File indexed, time taken: "
         +(endTime-startTime)+" ms");		
   }

   private void search(String searchQuery) throws IOException, ParseException {
	  //call the searcher with the given values 
	  System.out.println(bm25);
      searcher = new Searcher(indexDir, bm25);
      long startTime = System.currentTimeMillis();
      TopDocs hits = searcher.search(searchQuery);
      long endTime = System.currentTimeMillis();
      float[] scoring= new float[10];
      String[] ranking= new String[10];
      String[] paths= new String[10];
      
      System.out.println(hits.totalHits +
         " documents found. Time :" + (endTime - startTime));
      //check the scores for each file and create a top 10 of all documents
      for(ScoreDoc scoreDoc : hits.scoreDocs) {
    	 float score = scoreDoc.score;
         for (int i=0;i<10;i++){
        	 if(score>scoring[i]){
        		 for (int j=i;j<9;j++){
        			 scoring[j+1]=scoring[j];
            		 ranking[j+1]=ranking[j];
            		 paths[j+1]=paths[j];
        		 }
        		 scoring[i]=score;
        		 Document doc=searcher.getDocument(scoreDoc);
        		 ranking[i]=doc.get(LuceneConstants.FILE_NAME);
        		 paths[i]=doc.get(LuceneConstants.FILE_PATH);
        		 break;
        	 }
         }
         Document doc = searcher.getDocument(scoreDoc);
            System.out.println("File: "
            + doc.get(LuceneConstants.FILE_PATH) + " " + score );   
      }
      //print out the top 10 with enough information
      for (int i = 0; i < ranking.length; i++) {
    	  //printing up to 10 documents
    	  if (ranking[i] != null) {
    		  System.out.println("|" + Integer.toString(i+1) + " " + ranking[i] + " | Score: " + Float.toString(scoring[i]) + " | Path: " + paths[i] + "|");
    	  }
      }
      //end the process
      searcher.close(indexDir);

   }
}