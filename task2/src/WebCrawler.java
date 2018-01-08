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
	
	public WebCrawler(String indexdir) throws IOException {
		// initialize the values
		this.URL_LIST = new LinkedList<>();
		this.URL_DEPTH_LIST = new LinkedList<>();
		this.INDEXDIR = indexdir;
		this.indexer = new Indexer(indexdir);
	}
	
	public void getURLSandIndex(String seed, int max_depth) throws IOException {
		// get all the URL's with _getURLS and index them
		this.MAX_DEPTH = max_depth;
		System.out.println("--------------Indexing---------------");
		_getURLS(seed, 0);
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
		this.URL_DEPTH_LIST.add(seed.toLowerCase() + "\t" + Integer.toString(depth));
		this.URL_LIST.add(seed);
		try {
			System.out.println(seed + " at depth: " + depth);
			Connection con = Jsoup.connect(seed);
			Document doc = con.get();
			Elements links = doc.select("a[href]");
			
			indexer.indexFile(indexer.createDocument(Jsoup.parse(doc.toString()), seed));
			
			String url;
			for (Element e : links) {
				url = e.absUrl("href").replaceFirst("#.*", "");
				if (!url.equals("") && !this.URL_LIST.contains(url)) {
					_getURLS(url, depth + 1);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
