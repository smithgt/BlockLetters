import java.io.File;
import java.io.FileOutputStream;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.IOUtils;

import dk.dren.hunspell.Hunspell;

public class BlockLettersParser {
	public enum BlockType{
		UP('b',new char[]{'b','d','f','h','k','l','t'}),
		DOWN('g',new char[]{'g','j','p','q','y'}),
		SMALL('a',new char[]{'a','c','e','i','m','n','o','r','s','u','v','w','x','z'}),
		SPACE(' ',new char[]{' '});
		
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

	private static Hunspell.Dictionary dictionary;

//		public static final String encodedInput="bbba ba a baab";// this is a test
//	public static final String encodedInput="gaaa abbb bba abab";//gone with the wind
//	public static final String encodedInput="aba aaaba bbaa";//who wrote this
	public static String encodedInput="bab";//66 (117)
//	public static final String encodedInput="baagaaaa";
private static int MAX_WORD_LENGTH=5;
	
//4 letter=       2s
//5 letter=      44s
//6 letter= 1165427s

	public static void main(String[] args) throws Exception {
		try{
			dictionary=Hunspell.getInstance().getDictionary("lib/hunspell-en_US-7.1-0/en_US");
		}catch(Exception ex){
			ex.printStackTrace();
			return;
		}
		
//		String encodedInput="bab";//66 (117)
//		encodedInput="baabbaa";//91 (51477) 
//		encodedInput="baabbaab";//40 (378587) fuckhead!!!
		
		List<char[]> encodedChars=new ArrayList<char[]>();
		for(int i=1;i<=MAX_WORD_LENGTH;i++){
			System.err.println("word length="+i+" ...");
			
			encodedChars.add(new char[]{'b','a','g'});
			int totalWords=0;
			long start=Calendar.getInstance().getTimeInMillis();
			CharIncrementer incrementer=new CharIncrementer(encodedChars);
			boolean canIncrement=true;
			String encodedString;
			List<String> possibleWords;
			while(canIncrement){
				encodedString=incrementer.getCurrentString();
				long wordStartTime=Calendar.getInstance().getTimeInMillis();
				possibleWords=parseEncodedWord(encodedString);
				totalWords+=possibleWords.size();
				if(possibleWords.size()>0){
					File f=null;
						
					 f=new File("output/"+i+"/"+encodedString+".txt");
					f.getParentFile().mkdirs();
					FileOutputStream fos=new FileOutputStream(f);
					IOUtils.write(possibleWords.toString(), fos);
					
				}
//				System.err.println("\t"+encodedString+" ("+(Calendar.getInstance().getTimeInMillis()-wordStartTime)+") :"+possibleWords);
//				for(String s:possibleWords){
//					System.err.println("\t"+s);
//				}
				canIncrement=incrementer.increment();
			}
			System.err.println("\ttotal time="+(Calendar.getInstance().getTimeInMillis()-start)+" words="+totalWords);

		}

		
//		long start=Calendar.getInstance().getTimeInMillis();
//		List<String> possibleWords=parseEncodedWord(encodedInput);
//		for(String s:possibleWords){
//			System.err.println(s);
//		}
//		System.err.println("time="+(Calendar.getInstance().getTimeInMillis()-start));
	}
	
	private static List<String> computePossiblePhrases(String encodedPhrase){
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
		
		return possiblePhrases;
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
		}else if(encodingMatches(blockTypes,BlockType.UP)){
			return possibleWords;
		}else if(encodingMatches(blockTypes,BlockType.DOWN)){
			return possibleWords;
		}else if(encodingMatches(blockTypes,BlockType.UP,BlockType.SMALL)){
			possibleWords.add("be");
			possibleWords.add("he");
			possibleWords.add("in");
			possibleWords.add("do");
			possibleWords.add("to");
			possibleWords.add("is");	
		}else if(encodingMatches(blockTypes,BlockType.UP,BlockType.UP,BlockType.SMALL)){
			possibleWords.add("the");
			possibleWords.add("flu");
		}

		//otherwise brute force
		if(possibleWords.size()==0){
			List<char[]> possibleChars=new ArrayList<char[]>();
			for(BlockType type:blockTypes){
				possibleChars.add(type.values);
//				switch(type){
//				case UP:
//					possibleChars.add(ups);
//					break;
//				case DOWN:
//					possibleChars.add(downs);
//					break;
//				case SMALL:
//					possibleChars.add(smalls);
//					break;
//				case SPACE:
//					possibleChars.add(spaces);
//					break;
//				}
			}
			
			CharIncrementer incrementer=new CharIncrementer(possibleChars);
			boolean canIncrement=true;
			int i=0;

			String possibleWord;
			while(canIncrement){
				possibleWord=incrementer.getCurrentString();
				if(!dictionary.misspelled(possibleWord)){
					possibleWords.add(possibleWord);
				}
//				System.err.println("i="+i+" "+incrementer+" "+incrementer.getCurrentString());				
				i++;
				canIncrement=incrementer.increment();
			}
//			iteration=0;
//			addPossibleLetters(possibleWords,possibleChars);
//			filterMisSpelledWords(possibleWords);
		}

//		System.err.println("possibleWords ["+possibleWords.size()+"]="+possibleWords);

		return possibleWords;
	}
	
	
	
private static long iteration=0;
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
