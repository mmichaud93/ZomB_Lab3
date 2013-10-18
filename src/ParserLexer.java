import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class ParserLexer {
	
	int charClass;
	int lexLen;
	int LETTER = 0;
	int DIGIT = 1;
	int UNKNOWN = -1;
	
	int input_iterator = 0;
	
	String input = "";
	String word = "";
	String action = "";
	char nextChar = ' ';
	int current_code = -1;
	int last_code=-1;
	int[] next_codes = {1};
	int line = 1;
	boolean usedAdverb = false;
	final String filename = "complex.zom";
	ArrayList<String> actions = new ArrayList<String>();

	public static void main(String[] args) {
		LookUp.LoadLexeme();
		new ParserLexer();
	}
	
	public ParserLexer() {
		
		// go through the input and pull out words
		// match each word to a list of accepted words
		
		// open the source file
		BufferedReader inputStream = null;
		try {
			inputStream = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// check null conditions
		if(inputStream==null) {
			System.out.println("File not found");
			System.exit(0);
		}
		System.out.println("Parsing "+filename+"...");
		try {
			// while there are characters to be read
			while(inputStream.ready()) {
				// get the next character
				nextChar = (char) inputStream.read();
				// if we have pulled a space or a newline
				if((int)nextChar == 10 || (int)nextChar == 13) {
					if((int)nextChar == 10)
						line++;
					nextChar=' ';
				}
				// if we have a space  or a period then we have a word
				if(nextChar == ' ' || nextChar=='.') {
					//check if there is a cast code with the word
					if(word.length() > 3 && word.contains("(") && word.contains(")")) {
						// get the case identifier
						String identifier = word.substring( word.indexOf('(')+1, word.indexOf(')'));
						int id_code = -1;
						// figure out the code
						if(identifier.length() > 1) { // identifier must be a word ex: Matt(entity)
							if(identifier.equals("verb")) {
								id_code = 4;
							} else if(identifier.equals("adverb")) {
								id_code = 3;
							} else if(identifier.equals("entity")) {
								id_code = 1;
							}
							// add it and make sure we havent duplicated it
							if(LookUp.AddIdentifier(word.substring(0, word.indexOf('(')), id_code)==-1) {
								System.out.println("ERROR 0006, Identifier "+word.substring(0, word.indexOf('('))+" already in lexeme (line "+line+")");
								System.exit(0);
							}
							// remove the identifier
							word = word.substring(0, word.indexOf('('));
						} else // the identifier wasnt a word, so it was a number so just use that ex: Matt(1)
							current_code = Integer.parseInt(identifier);
					}
					// check the word with the lexer lookup
					current_code = LookUp.LookUpWord(word);
					
					// output the word and code // commented for build
					// System.out.println("word = "+word+", code = "+current_code);
					
					// check if the code is valid, if it isnt then we have an error
					if(validCode()) {
						// set the next set of valid codes based on the current code
						switch(current_code) {
						case 0:
							break;
						case 1: // entity
							// can be followed by a verb or an adverb
							next_codes = new int[] {4, 3};
							break;
						case 2: // second entity
							// can be followed by an adverb or a period if you havent used an adverb already
							if(usedAdverb)
								next_codes = new int[] {5};
							else
								next_codes = new int[] {5, 3};
							break;
						case 3: // adverb
							// can be followed by a period if the previous word was a verb
							usedAdverb = true;
							if(last_code==4) 
								next_codes = new int[] {5};
							else // or a verb if the previous word was not one.
								next_codes = new int[] {4};
							break;
						case 4: // verb
							// can be followed by an adverb or a second entity
							next_codes = new int[] {2, 3};
							break;
						case 5: // period
							// can only be followed by an entity to start a new action
							next_codes = new int[] {1};
							break;
						}
					} else if (current_code!=-1){
						switch(current_code) {
						case -1: // will probably never be reached
							System.out.println("error -1");
							break;
						case 0: // will probably never be reached
							System.out.println("error 0");
							break;
						case 1:
							// first entity
							// you probably added a third entity after a second one which doesnt work
							System.out.println("error 1");
							System.out.println("ERROR 0001, Expected Period. Got Second entity (line "+line+")");
							break;
						case 2:
							// second entity
							// got a second entity when you should have gotten something else
							System.out.println("ERROR 0002, Expected Entity Verb, Adverb, or Period. Got Second entity (line "+line+")");
							break;
						case 3:
							// adverb
							// should only get an adverb after a verb
							System.out.println("ERROR 0003, Expected Entity Verb or Period. Got Adverb (line "+line+")");
							break;
						case 4:
							// verb 
							// should get an entity or adverb after a verb
							System.out.println("ERROR 0004, Expected Entity or Adverb. Got Verb (line "+line+")");
							break;
						case 5: // will probably never be reached
							// period
							System.out.println("error 5");
							break;
						}
						// crash out
						System.exit(0);
					}
					
					// add the word to the action
					action+=word;
					if(nextChar=='.') {
						// terminated action
						// add action to a list of actions
						actions.add(action);
						// reset the parserlexer
						usedAdverb = false;
						next_codes = new int[] {1};
						LookUp.reset();
						action="";
					} else {
						// add a space to the action to separate words
						action+=" ";
					}
					
					// reset the word
					word="";
				} else {
					// add the char to the word
					word+=nextChar;
				}
				// store the last code
				last_code = current_code;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Successfully parsed "+filename);
		System.out.println("Parsed Actions:");
		// print the actions
		for(String action: actions) {
			System.out.println("     "+action+".");
		}
	}
	boolean validCode() {
		// go through the list of valid codes. if the current code is in there, then return true
		for(int i = 0; i < next_codes.length; i++) {
			if(next_codes[i]==current_code) {
				return true;
			}
		}
		return false;
	}
	void getNextChar() {
		nextChar = input.charAt(input_iterator);
		input_iterator++;
	}
}