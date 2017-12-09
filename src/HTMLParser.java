import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
  
//class to convert HTML to String
public class HTMLParser{
  
	public HTMLParser() {};
	
    public File parse(File file, String dataDir) {
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
                    System.out.println(htmlFile.title());
                    System.out.println(htmlFile.body().text());
                    PrintWriter out = new PrintWriter(file.getPath().replaceFirst(".html$", "") + ".txt");
                    out.println( htmlFile.title() + "\n");
                    out.println( htmlFile.body().text() );
                    out.close();                    
                    file=new File(dataDir.replaceFirst(".html$", "") + ".txt");
                    return file;
                    
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                	System.out.println("parsing failed");
                    e.printStackTrace();
                    return file;
                }
            }
        /*}
    }*/
}