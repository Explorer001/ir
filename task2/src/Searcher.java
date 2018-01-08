import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {
	
   IndexSearcher indexSearcher;
   QueryParser queryParser;
   Query query;
   
   private int MAX_SEARCH = 10;
   
   public Searcher(String indexDirectoryPath) 
      //initialize the searcher with the indexed files and the corresponding model
      throws IOException {
	  Directory index = FSDirectory.open(Paths.get(indexDirectoryPath));
      IndexReader indexDirectory = DirectoryReader.open(index);
      indexSearcher = new IndexSearcher(indexDirectory);
      indexSearcher.setSimilarity(new ClassicSimilarity());
      queryParser = new QueryParser("contents",
         new EnglishAnalyzer());
   }
   
   public TopDocs search( String searchQuery) 
      //search the files with the given query 
      throws IOException, ParseException {
      query = queryParser.parse(searchQuery);
      return indexSearcher.search(query, MAX_SEARCH);
   }

   public Document getDocument(ScoreDoc scoreDoc) 
      //get the document score
      throws CorruptIndexException, IOException {
      return indexSearcher.doc(scoreDoc.doc);	
   }

    public void close(String path) throws IOException {
    	//end the search
    	Directory directory = null;
    	DirectoryReader ireader = null;
    	try {
    	    directory = FSDirectory.open(Paths.get(path));
    	    ireader = DirectoryReader.open(directory);

    	} catch (Exception e) {
    	//
    	} finally {
    	try {
    	    if(directory != null) {
    	        directory.close();
    	    }
    	    if(ireader != null) {
    	        ireader.close();
    	    }
    	    }catch(IOException e) {
    	        //
    	    }
    	}
   }
}