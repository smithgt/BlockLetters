
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import dk.dren.hunspell.Hunspell;
import dk.dren.hunspell.HunspellLibrary;
import dk.dren.hunspell.HunspellMain;


public class BlockLettersParser {
	public enum BlockType{
		UP('b',ups),
		DOWN('g',downs),
		SMALL('a',smalls),
		SPACE(' ',spaces);
		private char[] values;
		private char encodedValue;
		private BlockType(char encodedValue,char[] values){
			this.encodedValue=encodedValue;
			this.values=values;
		}

		public static BlockType getType(char c){
			if(c==UP.encodedValue)return UP;
			if(c==DOWN.encodedValue)return DOWN;
			if(c==SMALL.encodedValue)return SMALL;
			return BlockType.SPACE;
		}
	}

	private static final char[] ups=new char[]{'b','d','f','h','k','l','t'};
	private static final char[] downs=new char[]{'g','j','p','q','y'};
	private static final char[] smalls=new char[]{'a','c','e','i','m','n','o','r','s','u','v','w','x','z'};
	private static final char[] spaces=new char[]{' '};

	private static Hunspell.Dictionary dictionary;

//		public static final String encodedInput="bbba ba a baab";// this is a test
//	public static final String encodedInput="gaaa abbb bba abab";//gone with the wind
//	public static final String encodedInput="aba aaaba bbaa";//who wrote this
//	public static final String encodedInput="bbaabba";//69928 (5)
	public static String encodedInput="bab";//66 (30)
//	public static final String encodedInput="baagaaaa";
	
	public static void main(String[] args) {
		try{
			dictionary=Hunspell.getInstance().getDictionary("lib/hunspell-en_US-7.1-0/en_US");
		}catch(Exception ex){
			ex.printStackTrace();
			return;
		}
		
		encodedInput="bab";//66 (30)
		encodedInput="baabbaa";//91 (52066)
		encodedInput="baabbaab";//40 (378587) fuckhead!!!
		
		long start=Calendar.getInstance().getTimeInMillis();
		
		String[] encodedWords=encodedInput.split(String.valueOf(BlockType.SPACE.encodedValue));

		List<List<String>> possibleWords=new ArrayList<List<String>>();
		for(String encodedWord:encodedWords){
			possibleWords.add(parseEncodedWord(encodedWord));
		}

		List<String> possiblePhrases=new ArrayList<String>();
		createPossiblePhrases(possiblePhrases,possibleWords);

		System.err.println("possiblePhrases["+possiblePhrases.size()+"]=");
				for(String phrase:possiblePhrases){
					System.err.println("\t"+phrase);
				}
				
				System.err.println("time="+(Calendar.getInstance().getTimeInMillis()-start));
//		System.err.println(dictionary.analyze("thin he a iced"));
//		System.err.println(dictionary.analyze("this is a test"));
	}

	private static void createPossiblePhrases(List<String> possiblePhrases, List<List<String>> possibleWords) {
		if(possibleWords.size()==0)return;
		List<String> words=possibleWords.get(0);
		List<String> combos=new ArrayList<String>();

		for(String word:words){
			if(possiblePhrases.size()>0){
				for(String w:possiblePhrases){
					combos.add(w+word+" ");
				}
			}else{
				combos.add(word+" ");
			}
		}
		possiblePhrases.clear();
		possiblePhrases.addAll(combos);

		possibleWords.remove(0);
		createPossiblePhrases(possiblePhrases,possibleWords);
	}

	private static List<BlockType> getEncodedWordAsBlockType(String encodedWord){
		List<BlockType> list=new ArrayList<BlockType>();
		char encodedChar;
		for(int i=0;i<encodedWord.length();i++){
			encodedChar=encodedWord.charAt(i);
			list.add(BlockType.getType(encodedChar));
		}
		return list;
	}

	private static boolean encodingMatches(List<BlockType> blockTypeList, BlockType ... params){
		if(blockTypeList.size()!=params.length)return false;
		for(int i=0;i<params.length;i++){
			if(blockTypeList.get(i)!=params[i])return false;
		}

		return true;
	}

	private static List<String> parseEncodedWord(String encodedWord) {
		List<String> possibleWords=new ArrayList<String>();

		List<BlockType> blockTypes = getEncodedWordAsBlockType(encodedWord);

		//do some smart filtering
		if(encodingMatches(blockTypes,BlockType.SMALL)){
			possibleWords.add("a");	
		}else if(encodingMatches(blockTypes,BlockType.UP,BlockType.SMALL)){
			possibleWords.add("be");
			possibleWords.add("he");
			possibleWords.add("in");
			possibleWords.add("do");
			possibleWords.add("to");
			possibleWords.add("is");	
		}

		//otherwise brute force
		if(possibleWords.size()==0){
			List<char[]> possibleChars=new ArrayList<char[]>();
			for(BlockType type:blockTypes){
				switch(type){
				case UP:
					possibleChars.add(ups);
					break;
				case DOWN:
					possibleChars.add(downs);
					break;
				case SMALL:
					possibleChars.add(smalls);
					break;
				case SPACE:
					possibleChars.add(spaces);
					break;
				}
			}
			iteration=0;
			addPossibleLetters(possibleWords,possibleChars);
//			filterMisSpelledWords(possibleWords);
		}

		System.err.println("possibleWords ["+possibleWords.size()+"]="+possibleWords);

		return possibleWords;
	}
private static long iteration;
//	private static void filterMisSpelledWords(List<String> possibleWords) {
//		for(int i=possibleWords.size()-1;i>=0;i--){
//			if(dictionary.misspelled(possibleWords.get(i))){
//				possibleWords.remove(i);
//			}
//		}
//	}

	private static void addPossibleLetters(List<String> possibleWords, List<char[]> possibleChars) {
		iteration++;
		System.err.println(iteration);
		
		if(possibleChars.size()==0)return;
		char[] chars=possibleChars.get(0);
		List<String> combos=new ArrayList<String>();
		
//		String[] combos=new String[chars.length*(possibleWords.size()>0?possibleWords.size():1)];
		
		try{
			int i=0;
			String s;
		for(char c:chars){
			if(possibleWords.size()>0){
				for(String w:possibleWords){
//					combos[i++]=w.concat(String.valueOf(c));
					s=w+c;
					if(iteration==encodedInput.length()&&dictionary.misspelled(s)){
						continue;
					}else{
						combos.add(s);
					}
				}
			}else{
				i++;
//				combos[i++]=String.valueOf(c);
				combos.add(String.valueOf(c));
			}
		}
		}catch(Throwable e){
			e.printStackTrace();
			System.err.println("iteration="+iteration);
		}
		possibleWords.clear();
		possibleWords.addAll(combos);
//		possibleWords.addAll(Arrays.asList(combos));

		possibleChars.remove(0);
		addPossibleLetters(possibleWords, possibleChars);
	}
}