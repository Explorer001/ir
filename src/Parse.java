import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
  
//class to convert HTML to String
public class Parse{
  
    public static void main(String args[]) {
  //HTML from local path
        Document htmlFile = null;
        File folder = new File("//Lucene"); //it should be a local folder(path)
        File[] listOfFiles = folder.listFiles();
        

        for (File file : listOfFiles) {
        	file.getName().endsWith(".html");

            if (file.getName().endsWith(".html")) {
            	try {
                    htmlFile = Jsoup.parse(new File(file.getName()), "ISO-8859-1");
                    PrintWriter out = new PrintWriter( file.getName() + ".txt ");
                    out.println( htmlFile.body().text() );
                    
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}