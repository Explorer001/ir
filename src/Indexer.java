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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {

   private IndexWriter writer;
   private HTMLParser html_parser;

   public Indexer(String indexDirectoryPath) throws IOException {
	  Directory indexDirectory= FSDirectory.open(Paths.get(indexDirectoryPath));
    

      //create the indexer
      writer = new IndexWriter(indexDirectory, 
    		  new IndexWriterConfig(new EnglishAnalyzer()).setOpenMode(OpenMode.CREATE));//StandardAnalyzer()) );
   }

   public void close() throws CorruptIndexException, IOException {
      writer.close();
   }

   private Document getDocument(File file) throws IOException {
      Document document = new Document();

      //index file contents
      Field contentField = new TextField(LuceneConstants.CONTENTS, new FileReader(file));
      //index file name
      Field fileNameField = new StringField(LuceneConstants.FILE_NAME,
         file.getName(),Field.Store.YES);
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
	   html_parser = new HTMLParser();
      File[] files = new File(dataDirPath).listFiles();
      
      int num_docs = 0;

      for (File file : files) {
         if(!file.isDirectory()
            && !file.isHidden()
            && file.exists()
            && file.canRead()
            && filter.accept(file)
         ){
        	System.out.println(file.toString());
        	html_parser.parse(file, dataDirPath);
            indexFile(new File(file.getPath().replaceFirst(".html$", ".txt")));
         }
         if(file.isDirectory())
         {
        	 num_docs = createIndex(file.getAbsolutePath(), new TextFileFilter());
         }
       }
      
      return num_docs + writer.numDocs();
   }
}