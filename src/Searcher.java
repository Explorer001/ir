import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {
	
	boolean bm25 = false;
	
   IndexSearcher indexSearcher;
   QueryParser queryParser;
   Query query;
   
   public Searcher(String indexDirectoryPath, boolean bm25) 
      throws IOException {
	  Directory index = FSDirectory.open(Paths.get(indexDirectoryPath));
      IndexReader indexDirectory = DirectoryReader.open(index);
      indexSearcher = new IndexSearcher(indexDirectory);
      if (bm25) indexSearcher.setSimilarity(new BM25Similarity());
      queryParser = new QueryParser(LuceneConstants.CONTENTS,
         new EnglishAnalyzer());
   }
   
   public TopDocs search( String searchQuery) 
      throws IOException, ParseException {
      query = queryParser.parse(searchQuery);
      return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
   }

   public Document getDocument(ScoreDoc scoreDoc) 
      throws CorruptIndexException, IOException {
      return indexSearcher.doc(scoreDoc.doc);	
   }

    public void close(String path) throws IOException {
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