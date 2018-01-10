import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class CommandLineHandler {

	private static WebCrawler crawl;
	private static Searcher searcher;
	
	public static void main(String[] args) throws IOException, ParseException {
		//check the incoming String for the correct input and initialize the values
		if (args.length != 4) {
			System.out.println("Usage: java -jar IRP02.jar [seed URL] [crawl depth] [path to index folder] [query]");
			return;
		} else {
			//select arguments from string
			String indexdir = args[2];
			int searchdepth = Integer.parseInt(args[1]);
			String seed = args[0];
			String query = args[3];
			
			if (!seed.matches("^.*://[^/]*/.*")) {
				System.out.println("Malformed URL");
				System.out.println("Use URL of format: http://www.example.com/");
				System.out.println("Aborting");
				return;
			}
					
			//crawl the website to specific depth and index while crawling
			crawl = new WebCrawler(indexdir);
			crawl.getURLSandIndex(seed, searchdepth);
			
			search(query, indexdir);
		}
	}
	
	   private static void search(String searchQuery, String indexDir) throws IOException, ParseException {
			  //call the searcher with the given values 
		      searcher = new Searcher(indexDir);
		      long startTime = System.currentTimeMillis();
		      TopDocs hits = searcher.search(searchQuery);
		      long endTime = System.currentTimeMillis();
		      float[] scoring= new float[10];
		      String[] ranking= new String[10];
		      String[] paths= new String[10];
		      
		      System.out.println("--------------Searching---------------");
		      
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
		        		 ranking[i]=doc.get("filename");
		        		 paths[i]=doc.get("filepath");
		        		 break;
		        	 }
		         }
		         Document doc = searcher.getDocument(scoreDoc);
		      }
		      //print out the top 10 with enough information
		      for (int i = 0; i < ranking.length; i++) {
		    	  //printing up to 10 documents
		    	  if (ranking[i] != null) {
		    		  System.out.println("|" + Integer.toString(i+1) + " " + ranking[i] + " | Score: " + Float.toString(scoring[i]) + " | URL: " + paths[i] + "|");
		    	  }
		      }
		      //end the process
		      searcher.close(indexDir);

		   }
	
}
