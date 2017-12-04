import java.io.IOException;
import java.nio.file.Paths;

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
	
   String indexDir = "/data/code/ir_workspace/Informationretrieval/Index";
   String dataDir = "/data/code/ir_workspace/Informationretrieval/Data";
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
   
      System.out.println(hits.totalHits +
         " documents found. Time :" + (endTime - startTime));
      for(ScoreDoc scoreDoc : hits.scoreDocs) {
    	 float score = scoreDoc.score;
         Document doc = searcher.getDocument(scoreDoc);
            System.out.println("File: "
            + doc.get(LuceneConstants.FILE_PATH) + " " + score);   
      }
      searcher.close(indexDir);
      //for(ScoreDoc scoreDoc : hits.scoreDocs) 
   
      //close associate index files and save deletions to disk
      //indexReader.close();
      //Directory index = FSDirectory.open(Paths.get(indexDir));
      //IndexReader rdr = DirectoryReader.open(index);
      
      // TODO: An input from the console
      // TODO: Output the content!
      // TODO: Does it parse the title?
      // TODO: Separate the body and title? 
      // TODO: search all subfolders
   }
}