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
	private int MAX_DEPTH = 0;
	private String INDEXDIR;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		WebCrawler crawl = new WebCrawler("index");
		List<String> urls = crawl.getURLS("http://www.dke-research.de", 1);
		for(String url : urls) {
			System.out.println(url);
		}
	}
	
	public WebCrawler(String indexdir) {
		this.URL_LIST = new LinkedList<>();
		this.INDEXDIR = indexdir;
	}
	
	public List<String> getURLS(String seed, int max_depth) throws IOException {
		this.MAX_DEPTH = max_depth;
		_getURLS(seed, 0);
		FileWriter writer = new FileWriter(this.INDEXDIR + "/pages.txt");
		for (String url : this.URL_LIST) {
			writer.write(url + "\n");
		}
		writer.close();
		return URL_LIST;
	}
	
	public void _getURLS(String seed, int depth) {
		if (depth > this.MAX_DEPTH) return;
		try {
			Connection con = Jsoup.connect(seed);
			Document doc = con.get();
			Elements links = doc.select("a[href]");
			
			String url;
			for (Element e : links) {
				url = e.absUrl("href").replaceFirst("#.*", "");
				if (!url.equals("") && !this.URL_LIST.contains(url + "\t" + Integer.toString(depth))) {
					this.URL_LIST.add(url.toLowerCase() + "\t" + Integer.toString(depth));
					_getURLS(url, depth + 1);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
