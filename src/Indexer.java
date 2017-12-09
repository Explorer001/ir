import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BasicModelIn;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.jsoup.Jsoup;

public class Indexer {

   private IndexWriterConfig config;
   private IndexWriter writer;

   public Indexer(String indexDirectoryPath, boolean bm25) throws IOException {
	  Directory indexDirectory= FSDirectory.open(Paths.get(indexDirectoryPath));
	  
	  config = new IndexWriterConfig(new EnglishAnalyzer());
	  config.setOpenMode(OpenMode.CREATE);
	  if (bm25) config.setSimilarity(new BM25Similarity());
	  else config.setSimilarity(new ClassicSimilarity());
	  System.out.println(config.getSimilarity().toString());
      //create the indexer
      writer = new IndexWriter(indexDirectory, 
    		  config);
   }

   public void close() throws CorruptIndexException, IOException {
      writer.close();
   }

   private Document getDocument(File file) throws IOException {
      Document document = new Document();

      //index file contents
      Field contentField = new TextField(LuceneConstants.CONTENTS, new FileReader(file));
      //index file name
      org.jsoup.nodes.Document htmlFile=null;
      htmlFile=Jsoup.parse(file, "ISO-8859-1");      
      Field fileNameField = new StringField(LuceneConstants.FILE_NAME,
         htmlFile.title(),Field.Store.YES);
      //index file path
      Field filePathField = new StringField(LuceneConstants.FILE_PATH,
         file.getCanonicalPath(),Field.Store.YES);

      document.add(contentField);
      document.add(fileNameField);
      document.add(filePathField);

      return document;
   }   

   private void indexFile(File file) throws IOException {
      System.out.println("Indexing "+file.getCanonicalPath());
      Document document = getDocument(file);
      writer.addDocument(document);
   }

   public int createIndex(String dataDirPath, FileFilter filter) 
      throws IOException {
      //get all files in the data directory
	  //System.out.println(dataDirPath);
      File[] files = new File(dataDirPath).listFiles();
      
      int num_docs = 0;

      for (File file : files) {
         if(!file.isDirectory()
            && !file.isHidden()
            && file.exists()
            && file.canRead()
            && filter.accept(file)
         ){
        	//System.out.println(file.toString());
        	//HTMLParser parser= new HTMLParser();
            //indexFile(parser.parse(file, file.getCanonicalPath()));
        	 indexFile(file);
         }
         if(file.isDirectory())
         {
        	 num_docs += createIndex(file.getAbsolutePath(), filter);
         }
       }
      
      return num_docs + writer.numDocs();
   }
}