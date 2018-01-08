import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {
	
	private IndexWriter writer;

	public Indexer(String indexpath) throws IOException {
		//Initialize the indexer with the English language as the base
		  Directory indexDirectory= FSDirectory.open(Paths.get(indexpath));
		  
		  IndexWriterConfig config = new IndexWriterConfig(new EnglishAnalyzer());
		  config.setOpenMode(OpenMode.CREATE);
		  config.setSimilarity(new ClassicSimilarity());
	      //create the indexer
	      writer = new IndexWriter(indexDirectory, config);
	}
	
	public void close() throws IOException{
		System.out.println(writer.numDocs());
		writer.commit();
		writer.close();
	}
	
	public Document createDocument(org.jsoup.nodes.Document jdoc, String url) {
		Document newDoc =  new Document();
		
		try {
		 
		    Field fileNameField = new StringField("filename",
		         jdoc.title(),Field.Store.YES);
		    
		    Field contentField = new TextField("contents",
			         jdoc.body().toString(), Field.Store.YES);
		    
		    Field linkField = new StringField("filepath", url, Field.Store.YES);
		    
		    newDoc.add(linkField);
		    newDoc.add(fileNameField);
		    newDoc.add(contentField);
		   
		} catch (Exception e) {
			e.printStackTrace();
		}
	     
	    //System.out.println(newDoc.getField("link"));
	    
	    return newDoc;
	}
	
	public void indexFile(Document doc) throws IOException {
		writer.addDocument(doc);
	}
	
}
