import java.io.File;
import java.io.IOException;
  
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
  
//class to convert HTML to String
public class HTMLParser{
  
    public static void main(String args[]) {
  //HTML from local path
        Document htmlFile = null;
        try {
            htmlFile = Jsoup.parse(new File("test.html"), "ISO-8859-1");
        } catch (IOException e) {
            e.printStackTrace();
        } // right
        String title = htmlFile.body().text();

        System.out.println("Body of HTML looks like:");
        System.out.println( title);
    }
}