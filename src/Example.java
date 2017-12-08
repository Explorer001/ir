import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Example {
	
   String indexDir = "Index";
   String dataDir = "Data";
   Indexer indexer;
   Searcher searcher;
   boolean bm25 = false;

   public static void main(String[] args) {
      Example tester;
      try {
         tester = new Example();
         tester.createIndex();
         tester.search("LuceneFirstApplication");
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ParseException e) {
         e.printStackTrace();
      }
   }

   private void createIndex() throws IOException {
      indexer = new Indexer(indexDir);
      int numIndexed;
      long startTime = System.currentTimeMillis();	
      numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
      long endTime = System.currentTimeMillis();
      indexer.close();
      System.out.println(numIndexed+" File indexed, time taken: "
         +(endTime-startTime)+" ms");		
   }

   private void search(String searchQuery) throws IOException, ParseException {
      searcher = new Searcher(indexDir, bm25);
      long startTime = System.currentTimeMillis();
      TopDocs hits = searcher.search(searchQuery);
      long endTime = System.currentTimeMillis();
      float[] scoring= new float[10];
      String[] ranking= new String[10];
      System.out.println(hits.totalHits +
         " documents found. Time :" + (endTime - startTime));
      for(ScoreDoc scoreDoc : hits.scoreDocs) {
    	 float score = scoreDoc.score;
         for (int i=0;i<10;i++){
        	 if(score>scoring[i]){
        		 for (int j=i;j<9;j++){
        			 scoring[j+1]=scoring[j];
            		 ranking[i+1]=ranking[j];
        		 }
        		 scoring[i]=score;
        		 Document doc=searcher.getDocument(scoreDoc);
        		 ranking[i]=doc.get(LuceneConstants.FILE_NAME);
        		 break;
        	 }
         }
         Document doc = searcher.getDocument(scoreDoc);
            System.out.println("File: "
            + doc.get(LuceneConstants.FILE_PATH) + " " + score );   
      }
      for (int i = 0; i < ranking.length; i++) {
    	  System.out.println("|" + Integer.toString(i+1) + " " + ranking[i] + " | Score: " + Float.toString(scoring[i]));
    	  System.out.println("+ Place summary here");
      }
      //System.out.println("Best 10 Documents:" + Arrays.toString(ranking));
      searcher.close(indexDir);
      //for(ScoreDoc scoreDoc : hits.scoreDocs) 
   
      //close associate index files and save deletions to disk
      //indexReader.close();
      //Directory index = FSDirectory.open(Paths.get(indexDir));
      //IndexReader rdr = DirectoryReader.open(index);
      
      // TODO: An input from the console
      // TODO: Output the content
   }
}