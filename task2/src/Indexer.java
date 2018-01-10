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
    //close index files and commit changes
		System.out.println("Indexed " + writer.numDocs() + " Docs!");
		writer.commit();
		writer.close();
	}
	
	public Document createDocument(org.jsoup.nodes.Document jdoc, String url) {
		Document newDoc =  new Document();
		//transform jsoup doc to lucene doc
		try {
		    //at website title
		    Field fileNameField = new StringField("filename",
		         jdoc.title(),Field.Store.YES);
		    
		    //add body of website
		    Field contentField = new TextField("contents",
			         jdoc.body().text(), Field.Store.YES);
		    
		    //add url of website
		    Field linkField = new StringField("filepath", url, Field.Store.YES);
		    
		    newDoc.add(linkField);
		    newDoc.add(fileNameField);
		    newDoc.add(contentField);
		   
		} catch (Exception e) {
	      //if no document exists
		}
	    
	    return newDoc;
	}
	
	public void indexFile(Document doc) throws IOException {
		//simple lucene indexing
    writer.addDocument(doc);
	}
	
}
