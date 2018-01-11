import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {
	
	private List<String> URL_LIST;
	private List<String> URL_DEPTH_LIST;
	private int MAX_DEPTH = 0;
	private String INDEXDIR;
	private Indexer indexer;
	private RobotHandler R_HANDLER;
	
	public WebCrawler(String indexdir) throws IOException {
		// initialize the values
		// list to write in pages txt and list to check if url was seen at some place
		this.URL_LIST = new LinkedList<>();
		this.URL_DEPTH_LIST = new LinkedList<>();
		//directory where index should be stored
		this.INDEXDIR = indexdir;
		this.indexer = new Indexer(indexdir);
		this.R_HANDLER = new RobotHandler();
	}
	
	public void getURLSandIndex(String seed, int max_depth) throws IOException {
		// get all the URL's with _getURLS and index them
		this.MAX_DEPTH = max_depth;
		System.out.println("--------------Indexing---------------");
		//crawling method
		_getURLS(seed, 0);
		//write results in inn pages txt
		FileWriter writer = new FileWriter(this.INDEXDIR + "/pages.txt");
		for (String url : this.URL_DEPTH_LIST) {
			writer.write(url + "\n");
		}
		writer.close();
		indexer.close();
	}
	
	public void _getURLS(String seed, int depth) {
		// is going through all URL's until the max depth has been accomplished and adds them to the URL list as well as index them
		if (depth > this.MAX_DEPTH) return;
		try {
			//add url to list of seen
			this.URL_LIST.add(seed);
			if (!R_HANDLER.allowed(seed)) {
				System.out.println("Acces to: " + seed + " disallowed!");
				//add normalized url to list for pages.txt with keyword disallowed
				this.URL_DEPTH_LIST.add("Disallowed: " + seed.toLowerCase() + "\t" + Integer.toString(depth));
				return;
			}
			System.out.println(seed + " at depth: " + depth);
			//add normalized url to list for pages.txt
			this.URL_DEPTH_LIST.add(seed.toLowerCase() + "\t" + Integer.toString(depth));
			//connect to seed and filter for links (a[href])
			Connection con = Jsoup.connect(seed);
			Document doc = con.get();
			Elements links = doc.select("a[href]");
			
			//index page
			indexer.indexFile(indexer.createDocument(Jsoup.parse(doc.toString()), seed));
			
			String url;
			//recursively call all linked websites if not seen or empty
			for (Element e : links) {
				url = e.absUrl("href").replaceFirst("#.*", "");
				if (!url.equals("") && !this.URL_LIST.contains(url)) {
					_getURLS(url, depth + 1);
				}
			}
		} catch (Exception e) {
		  //if things go wrong
		}
	}

}
