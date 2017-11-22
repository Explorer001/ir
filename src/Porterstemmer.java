package ir.stemmer;

public class Porterstemmer {

	public static void main(String[] args) {
		Porterstemmer ps = new Porterstemmer();
		System.out.println(ps.stemm("controlling"));
	}
	
	public Porterstemmer() {}
	
	public String stemm(String term) {
		int m;
		String[][] replacements_s1a = {{"sses", "ss"},{"ies", "i"},{"ss", "ss"},{"s",""}};
		String[][] replacements_s2 = {{"ational", "ate"}, {"tional", "tion"}, {"enci", "ence"}, {"anci", "ance"}, {"izer", "ize"}, {"abli", "able"}, {"alli", "al"}, {"entli", "ent"}, {"eli", "e"}, {"ousli", "ous"}, {"ization", "ize"}, {"ation", "ate"}, {"ator", "ate"}, {"alism", "al"}, {"iveness", "ive"}, {"fulness", "ful"}, {"ousness", "ous"}, {"aliti", "al"}, {"iviti", "ive"}, {"biliti", "ble"}};
		String[][] replacements_s3 = {{"icate", "ic"}, {"ative", ""}, {"alize", "al"}, {"iciti", "ic"}, {"ical", "ic"}, {"ful", ""}, {"ness", ""}};
		
		term = replace(replacements_s1a, term);
		term = step1b(term);
		term = step1c(term);
		m = getVCCount(term);
		if (m > 0) term = replace(replacements_s2, term);
		m = getVCCount(term);
		if (m > 0) term = replace(replacements_s3, term);
		term = step4(term);
		term = step5a(term);
		term = step5b(term);
		return term;
	}
	
	//determine VC for stemming purposes
	private static int getVCCount(String term) {
		term = term.toLowerCase();
		int vccount = 0; //how many VS's in term
		boolean got_vowel = false;
		char c, c1; //char and predecessor
		boolean is_vowel;
		for (int i = 0; i < term.length(); i++) {
			c = term.charAt(i);
			c1 = (i > 0)? term.charAt(i-1): 'b'; //b because it would not break y
			is_vowel = isVowel(c, c1);
			if (is_vowel && got_vowel == false) { //char is vowel
				got_vowel = true;
				continue;
			}
			if (got_vowel) {
				if (is_vowel == false) { //detect vc
					got_vowel = false;
					vccount += 1;
					continue;
				}
			}
		}
		return vccount;
	}
	
	//Char and predecessor
	private static boolean isVowel(char c, char cm1) {
		switch(c) {
		case 'a':
		case 'e':
		case 'i': 
		case 'o':
		case 'u':
			return true;
		case 'y':
			if (isVowel(cm1, cm1)) return false;
		default: 
			return false;
		}
	}
	
	private static boolean containsVowel(String term) {
		char c, c1;
		for (int i = 0; i < term.length(); i++) {
			c = term.charAt(i);
			c1 = (i > 0)? term.charAt(i-1): 'b'; //b because it would not break y
			if (isVowel(c,c1)) return true;
		}
		return false;
	}
	
	private static boolean endsOnDouble(String term) {
		term = term.toLowerCase();
		return (term.charAt(term.length() - 1) == term.charAt(term.length() - 2));
	}
	
	private static boolean endsOnCVC(String term) {
		int m = getVCCount(term);
		if ((m > 0) && !(isVowel(term.charAt(0), 'b')) && !(isVowel(term.charAt(term.length()-1), term.charAt(term.length()-2)))) {
			char ch = term.charAt(term.length()-1);
			if (ch == 'w' || (ch == 'x') || (ch == 'y')) return false;
			return true;
		}
		return false;
	}
	
	private String replace(String[][] repl, String term) {
		term = term.toLowerCase();
		for (int i = 0; i < repl.length; i++) {
			if (term.endsWith(repl[i][0])) {
				//regex match end of line
				return term.replaceFirst(repl[i][0] + "$", repl[i][1]);
			}
		}
		return term;
	}
	
	private String step1b(String term) {
		term = term.toLowerCase();
		int m = getVCCount(term);
		if (m > 0) {
			if (term.endsWith("eed")) {
				term = term.replaceFirst("eed$", "ee");
				m = getVCCount(term);
			}
		}
		if (containsVowel(term)) {
			if (term.endsWith("ed")) {
				term = term.replaceFirst("ed$", "");
				m = getVCCount(term);
			} else if (term.endsWith("ing")) {
				term = term.replaceFirst("ing$", "");
				m = getVCCount(term);
			}
		}
		if (term.endsWith("at")) {
			term = term.replaceFirst("at$", "ate");
			m = getVCCount(term);
		} else if (term.endsWith("bl")) {
			term = term.replaceFirst("bl$", "ble");
			m = getVCCount(term);
		} else if (term.endsWith("iz")) {
			term = term.replaceFirst("iz$", "ize");
			m = getVCCount(term);
		}
		if (endsOnDouble(term) && !(term.endsWith("l") || term.endsWith("s") || term.endsWith("z"))) {
			term = term.substring(0, term.length() - 1); //cut last element
			m = getVCCount(term);
		}
		if ((m == 1) && endsOnCVC(term)) {
			term += "e";
			m = getVCCount(term);
		}
		return term;
	}
	
	private String step1c(String term) {
		if (containsVowel(term)) term = term.replaceFirst("y$", "i");
		return term;
	}
	
	private String step4(String term) {
		String[][] repl = {{"al", "0"}, {"ance", "0"}, {"ence", "0"}, {"er", "0"}, {"ic", "0"}, {"able", "0"}, {"ible", "0"}, {"ant", "0"}, {"ement", "0"}, {"ment", "0"}, {"ent", "0"}, {"ion", "1"}, {"ou", "0"}, {"ism", "0"}, {"ate", "0"}, {"iti", "0"}, {"ous", "0"}, {"ive", "0"}, {"ize", "0"}};
		int m = getVCCount(term);
		for (int i = 0; i < repl.length; i++) {
			if (term.endsWith(repl[i][0])) {
				if (m > 1 && repl[i][1] == "0") return term.replaceFirst(repl[i][0] + "$", "");
				else {
					if (m > 1 && (term.endsWith("s") || term.endsWith("t"))) return term.replaceFirst(repl[i][0] + "$", "");
				}
			}
		}
		return term;
	}
	
	private String step5a(String term) {
		int m = getVCCount(term);
		if (m > 1) return term.replaceFirst("e$", "");
		else if (m == 1 && !term.endsWith("o")) return term.replaceFirst("e$", "");
		return term;
	}
	
	private String step5b(String term) {
		int m = getVCCount(term);
		if (m > 1 && endsOnDouble(term) && term.endsWith("l")) term = term.substring(0, term.length()-1);
		return term;
	}

}
