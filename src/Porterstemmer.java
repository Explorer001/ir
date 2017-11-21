package ir.stemmer;

public class Porterstemmer {

	public static void main(String[] args) {
		Porterstemmer ps = new Porterstemmer();
		System.out.println(getVCCount("fil"));
		System.out.println(endsOnCVC("fil"));
		System.out.println(ps.step1a("troubles"));
		System.out.println(ps.step1b("plastered"));
		System.out.println(ps.step1c("happy"));
		System.out.println(ps.step1c("sky"));
		System.out.println(ps.step2("hopefulness"));
		System.out.println(ps.step3("electrical"));
	}
	
	public Porterstemmer() {}
	
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
	
	private static int lastVowel(String term) {
		int index = 0;
		char c, c1;
		for (int i = 0; i < term.length(); i++) {
			c = term.charAt(i);
			c1 = (i > 0)? term.charAt(i-1): 'b'; //b because it would not break y
			if (isVowel(c,c1)) index = i;
		}
		return index;
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
	
	private String step1a(String term) {
		term = term.toLowerCase();
		String[][] replacements = {{"sses", "ss"},{"ies", "i"},{"ss", "ss"},{"s",""}};
		return replace(replacements, term);
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
	
	private String step2(String term) {
		int m = getVCCount(term);
		String[][] replacements = {{"ational", "ate"}, {"tional", "tion"}, {"enci", "ence"}, {"anci", "ance"}, {"izer", "ize"}, {"abli", "able"}, {"alli", "al"}, {"entli", "ent"}, {"eli", "e"}, {"ousli", "ous"}, {"ization", "ize"}, {"ation", "ate"}, {"ator", "ate"}, {"alism", "al"}, {"iveness", "ive"}, {"fulness", "ful"}, {"ousness", "ous"}, {"aliti", "al"}, {"iviti", "ive"}, {"biliti", "ble"},};
		if (m > 0) return replace(replacements, term);
		return term;
	}
	
	private String step3(String term) {
		int m = getVCCount(term);
		String[][] replacements = {{"icate", "ic"}, {"ative", ""}, {"alize", "al"}, {"iciti", "ic"}, {"ical", "ic"}, {"ful", ""}, {"ness", ""}, };
		if (m > 0) return replace(replacements, term);
		return term;
	}

}
