package ir.stemmer;

public class Porterstemmer {
	
	static char NOT_VOWEL = 'b';

	public static void main(String[] args) {
		String test = "Do you really think it is weakness that yields to temptation I tell you that there are terrible temptations which it requires strength strength and courage to yield to Oscar Wilde";
		String wanted = "Do you really think it is weak that yield to temptat I tell you that there ar terribl temptat which it requir strength strength and courag to yield to Oscar Wild";
		wanted = wanted.toLowerCase();
		String[] ar = test.split(" ");
		String[] war = wanted.split(" ");
		Porterstemmer ps = new Porterstemmer();
		String re;
		for (int i = 0; i < ar.length; i++) {
			re = ps.stemm(ar[i]);
			System.out.println(re);
		}
	}
	
	public Porterstemmer() {}
	
	public String stemm(String term) {
		String[][] replacements_s1a = {{"sses", "ss"},{"ies", "i"},{"ss", "ss"},{"s",""}};
		String[][] replacements_s2 = {{"ational", "ate"}, {"tional", "tion"}, {"enci", "ence"}, {"anci", "ance"}, {"izer", "ize"}, {"abli", "able"}, {"alli", "al"}, {"entli", "ent"}, {"eli", "e"}, {"ousli", "ous"}, {"ization", "ize"}, {"ation", "ate"}, {"ator", "ate"}, {"alism", "al"}, {"iveness", "ive"}, {"fulness", "ful"}, {"ousness", "ous"}, {"aliti", "al"}, {"iviti", "ive"}, {"biliti", "ble"}};
		String[][] replacements_s3 = {{"icate", "ic"}, {"ative", ""}, {"alize", "al"}, {"iciti", "ic"}, {"ical", "ic"}, {"ful", ""}, {"ness", ""}};
		
		term = term.toLowerCase();
		term = replace(replacements_s1a, term, -1);
		term = step1b(term);
		term = step1c(term);
		term = replace(replacements_s2, term, 0);
		term = replace(replacements_s3, term, 0);
		term = step4(term);
		term = step5a(term);
		term = step5b(term);
		return term;
	}
	
	//determine VC for stemming purposes
	private static int getVCCount(String term) {
		int vccount = 0; //how many VS's in term
		boolean got_vowel = false;
		char c, c1; //char and predecessor
		boolean is_vowel;
		for (int i = 0; i < term.length(); i++) {
			c = term.charAt(i);
			c1 = (i > 0)? term.charAt(i-1): NOT_VOWEL; //b because it would not break y
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
			c1 = (i > 0)? term.charAt(i-1): NOT_VOWEL; //b because it would not break y
			if (isVowel(c,c1)) return true;
		}
		return false;
	}
	
	private static boolean endsOnDouble(String term) {
		return (term.charAt(term.length() - 1) == term.charAt(term.length() - 2));
	}
	
	private static boolean endsOnCVC(String term) {
		if (term.length() <= 2) return false;
		else {
			char lastm0 = term.charAt(term.length()-1);
			char lastm1 = term.charAt(term.length()-2);
			char lastm2 = term.charAt(term.length()-3);
			char lastm3 = (term.length() > 3)? term.charAt(term.length()-4) : NOT_VOWEL;
			return !isVowel(lastm2, lastm3) && isVowel(lastm1, lastm2) && !isVowel(lastm0, lastm1);
		}
		/*int m = getVCCount(term);
		char start = term.charAt(0);
		char last = term.charAt(term.length()-1);
		char lastm1 = term.charAt(term.length()-2);
		if (((m > 0) && !isVowel(start, NOT_VOWEL) && !isVowel(last, lastm1)) || ((m > 1) && !isVowel(last, lastm1))) {
			if (last == 'w' || (last == 'x') || (last == 'y')) return false;
			return true;
		}
		return false;*/
	}
	
	private String replace(String[][] repl, String term, int condition) {
		String stem;
		for (int i = 0; i < repl.length; i++) {
			if (term.endsWith(repl[i][0])) {
				//regex match end of line
				stem = term.replaceFirst(repl[i][0] + "$", "");
				if(getVCCount(stem) > condition) return term.replaceFirst(repl[i][0], repl[i][1]);
			}
		}
		return term;
	}
	
	private String step1b(String term) {
		boolean twoOrThree = false;
		int m = getVCCount(term);
		String stem;
		if (term.endsWith("eed")) {
			stem = term.replaceFirst("eed$", "");
			if (getVCCount(stem) > 0) {
				term = stem;
				m = getVCCount(term);
			}
		}
		if (containsVowel(term)) {
			if (term.endsWith("ed")) {
				term = term.replaceFirst("ed$", "");
				m = getVCCount(term);
				twoOrThree = true;
			} else if (term.endsWith("ing")) {
				term = term.replaceFirst("ing$", "");
				m = getVCCount(term);
				twoOrThree = true;
			}
		}
		if (twoOrThree) {
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
			}
		}
		return term;
	}
	
	private String step1c(String term) {
		if (containsVowel(term)) term = term.replaceFirst("y$", "i");
		return term;
	}
	
	private String step4(String term) {
		String[][] repl = {{"al", "0"}, {"ance", "0"}, {"ence", "0"}, {"er", "0"}, {"ic", "0"}, {"able", "0"}, {"ible", "0"}, {"ant", "0"}, {"ement", "0"}, {"ment", "0"}, {"ent", "0"}, {"ion", "1"}, {"ou", "0"}, {"ism", "0"}, {"ate", "0"}, {"iti", "0"}, {"ous", "0"}, {"ive", "0"}, {"ize", "0"}};
		int m;
		String stem;
		for (int i = 0; i < repl.length; i++) {
			if (term.endsWith(repl[i][0])) {
				stem = term.replaceFirst(repl[i][0] + "$", "");
				m = getVCCount(stem);
				if (m > 1 && repl[i][1] == "0") return stem;
				else {
					if (m > 1 && (stem.endsWith("s") || stem.endsWith("t"))) return stem;
				}
			}
		}
		return term;
	}
	
	private String step5a(String term) {
		String stem = term.replaceFirst("e$", "");
		int m = getVCCount(stem);
		if (m > 1) return stem;
		else if (m == 1 && !endsOnCVC(stem)) return stem;
		return term;
	}
	
	private String step5b(String term) {
		int m = getVCCount(term);
		if (m > 1 && endsOnDouble(term) && term.endsWith("l")) return term.substring(0, term.length()-1);
		return term;
	}

}
