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
	  //Initialize the indexer with the English language as the base
	  Directory indexDirectory= FSDirectory.open(Paths.get(indexDirectoryPath));
	  
	  config = new IndexWriterConfig(new EnglishAnalyzer());
	  config.setOpenMode(OpenMode.CREATE);
	  //check if Vector space or Okapi should be used
	  if (bm25) config.setSimilarity(new BM25Similarity());
	  else config.setSimilarity(new ClassicSimilarity());
	  System.out.println(config.getSimilarity().toString());
      //create the indexer
      writer = new IndexWriter(indexDirectory, 
    		  config);
   }

   public void close() throws CorruptIndexException, IOException {
	  //close the index writer 
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
	  //index the given file 
      System.out.println("Indexing "+file.getCanonicalPath());
      Document document = getDocument(file);
      writer.addDocument(document);
   }

   public int createIndex(String dataDirPath, FileFilter filter) 
      throws IOException {
      //get all files in the data directory
      File[] files = new File(dataDirPath).listFiles();
      
      int num_docs = 0;
      //check if the given file is valid and if it's a directory check inside for more files
      for (File file : files) {
         if(!file.isDirectory()
            && !file.isHidden()
            && file.exists()
            && file.canRead()
            && filter.accept(file)
         ){
        	 
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