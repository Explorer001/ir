import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RobotHandler {
	
	String HOST_URL;
	List<String> DISALLOWED;
	
	public RobotHandler() {}
	
	public boolean allowed(String seed) {
		//iterate over robots.txt to see if url is allowed
		this.get_robot_txt(seed);
		for (String s : DISALLOWED) {
			if (seed.matches(HOST_URL + s)) {
				System.out.println();
				System.out.println(HOST_URL + s);
				return false;
			}
		}
		return true;
	}
	
	private void get_robot_txt(String seed) {
		System.out.println("Getting Robots.txt");
		//regex to match host part of url
		Pattern p = Pattern.compile("^.*://[^/]*");
		Matcher m = p.matcher(seed);
		String host_url = null;
		if (m.find()) {
			//get host part of url
			host_url = m.group();
		}
		this.HOST_URL = host_url;
		try {
			//variable idicates if user agent is *
			boolean relevant = false;
			
			//get robot.txt from server
			BufferedReader in = new BufferedReader(new InputStreamReader(new URL(host_url + "/robots.txt").openStream()));
			String line = null;
			//stores disallowed pages
			List<String> disallow = new LinkedList<>();
			//read robots.txt line by line
			while ((line = in.readLine()) != null) {
				if (relevant && line.contains("Disallow")) {
					//add in some regex symbols for better matching
					disallow.add(line.replaceAll(".*: ", "").replace("*", ".*") + ".*");
				}
				//check if user agent is *
				if (line.matches("User-[Aa]gent.*")) {
					if (line.contains("*")) {
						relevant = true;
					} else {
						relevant = false;
					}
				}
			}
			this.DISALLOWED = disallow;
		} catch (Exception e) {
	  }
	}
	

}
