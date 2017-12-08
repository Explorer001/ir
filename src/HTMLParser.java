import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
  
//class to convert HTML to String
public class HTMLParser{
  
	public HTMLParser() {};
	
    public void parse(File file, String dataDir) {
  //HTML from local path
    	System.out.println("parsing: " + file.getName());
        Document htmlFile = null;
        //File folder = new File("Data/"); //it should be a local folder(path)
        //File[] listOfFiles = folder.listFiles();
        

        /*for (File file : listOfFiles) {
        	file.getName().endsWith(".html");

            if (file.getName().endsWith(".html")) {*/
            	try {
                    htmlFile = Jsoup.parse(file, "ISO-8859-1");
                    //System.out.println(htmlFile.body().text());
                    PrintWriter out = new PrintWriter(dataDir + "/" + file.getName().replaceFirst(".html$", "") + ".txt");
                    out.println( htmlFile.title() + "\n");
                    out.println( htmlFile.body().text() );
                    out.close();
                    
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                	System.out.println("parsing failed");
                    e.printStackTrace();
                }
            }
        /*}
    }*/
}