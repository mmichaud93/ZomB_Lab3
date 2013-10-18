import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;


public class LookUp {
	
	// 1 = entity
	// 2 = second entity
	// 3 = adverb
	// 4 = verb entity
	// 5 = period
	static boolean second_entity = false;
	
	// define the lists of reserved words and codes
	static ArrayList<String> reserved = new ArrayList<String>();
	static ArrayList<Integer> reserved_code = new ArrayList<Integer>();

	// literal filename of the lexeme
	private final static String FILENAME = "lexeme.txt";
	public static void LoadLexeme() {
		ArrayList<String> reservedL = new ArrayList<String>();
		ArrayList<Integer> reservedCodeL = new ArrayList<Integer>();
		// open the file
		BufferedReader inputStream = null;
		try {
			inputStream = new BufferedReader(new FileReader(FILENAME));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// check null conditions
		if(inputStream==null) {
			System.out.println("Lexeme file not found");
			System.exit(0);
		}
		
		try {
			String input;
			String tag = null;
			int lexemeCode = -1;
			// while there are characters to be read
			while(inputStream.ready()) {
				// consume line
				input = inputStream.readLine();
				//System.out.println(input+", "+tag);
				if(input.charAt(0)=='<' && input.charAt(1)!='/' && input.charAt(input.length()-1)=='>') {
					// we got a thing
					tag = input.substring(1, input.length()-1);
					
					if(tag.equals("verbs")) {
						lexemeCode = 4;
					} else if(tag.equals("entities")) {
						lexemeCode = 1;
					} else if(tag.equals("adverbs")) {
						lexemeCode = 3;
					}
				} else if(input.charAt(0)=='<' && input.charAt(1)=='/' && input.charAt(input.length()-1)=='>') {
					// we got an end thing
					String newtag = input.substring(2, input.length()-1);
					//System.out.println(newtag);
					if(tag.equals(newtag)) {
						// end thing
						tag = null;
					}
				} else if(tag!=null) {
					// add whatever it is we have pulled to the array with the code we have designated
					reservedL.add(input);
					reservedCodeL.add(lexemeCode);
				}
			}
		} catch (Exception e) {
			
		}
		// put the loaded lists into the actual lists
		reserved = reservedL;
		reserved_code = reservedCodeL;
		//System.out.println("Successfully loaded lexeme");
	}
	public static int AddIdentifier(String id, int code) {
		// we found a cast in the ParserLexer, so we need to add it to the lexeme
		id = id.toLowerCase();
		for(int i = 0; i < reserved.size(); i++) {
			if(id.equals(reserved.get(i))) {
				return -1;
			}
		}
		reserved.add(id);
		reserved_code.add(code);
		return 0;
	}
	public static int LookUpWord(String word) {
		//  check the word against the list
		word=word.toLowerCase();
		int code = -1;
		for(int i = 0; i < reserved.size(); i++) {
			if(word.equals(reserved.get(i))) {
				code = reserved_code.get(i);
			}
		}
		// calculate if we have reached the second entity
		if(code==1) {
			if(!second_entity) {
				second_entity = true;
			} else {
				code = 2;
				second_entity = false;
			}
		}
		return code;
		
	}
	public static void reset() {
		second_entity = false;
	}
}
