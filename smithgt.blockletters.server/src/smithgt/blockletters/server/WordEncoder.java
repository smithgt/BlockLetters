package smithgt.blockletters.server;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import smithgt.blockletters.server.model.EncodedWord;

public class WordEncoder {

	public enum BlockType{
		UP('b',new String("bdfhklt")),
		DOWN('g',new String("gjpqy")),
		SMALL('a',new String("aceimnorsuvwxz")),
		SPACE(' ',new String(" "));

		private String values;
		private char encodedValue;
		private BlockType(char encodedValue,String values){
			this.encodedValue=encodedValue;
			this.values=values;
		}

		public static BlockType getTypeForEncodedChar(char c){
			if(c==UP.encodedValue)return UP;
			if(c==DOWN.encodedValue)return DOWN;
			if(c==SMALL.encodedValue)return SMALL;
			return BlockType.SPACE;
		}

		public static BlockType getTypeForChar(char c){
			if(UP.values.indexOf(c)>=0)return UP;
			if(DOWN.values.indexOf(c)>=0)return DOWN;
			if(SMALL.values.indexOf(c)>=0)return SMALL;
			return BlockType.SPACE;
		}
	}

	public Map<String, EncodedWord> parseWords(String wordListFilePath){

		List<String> words=null;
		try {
			words=readWords(wordListFilePath);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		words=filterWords(words);

		String encoded;
		Map<String, EncodedWord> encodedWordMap=new HashMap<String, EncodedWord>();
		for(String word:words){
			encoded=encodeWord(word);

			if(!encodedWordMap.containsKey(encoded)){
				encodedWordMap.put(encoded, new EncodedWord(encoded, wordListFilePath));
			}
			EncodedWord encodedWord=encodedWordMap.get(encoded);
			if(!encodedWord.contains(word)){
				encodedWord.add(word);
			}
		}
		
		return encodedWordMap;
	}

	private static String encodeWord(String word){
		char[] encodedWord=new char[word.length()];
		BlockType blockType;
		for(int i=0;i<word.length();i++){
			blockType=BlockType.getTypeForChar(word.charAt(i));
			encodedWord[i]=blockType.encodedValue;
		}
		return new String(encodedWord);
	}

	private static List<String> filterWords(List<String> words) {
		List<String> acceptableWords=new ArrayList<String>();
		String word;
		for(int i=words.size()-1;i>=0;i--){
			word=words.get(i);
			//filter out proper names (begins with capital)
			if(Character.isUpperCase(word.charAt(0))){
				continue;
			}
			//filter out possessive (contains apostrophe)
			if(word.contains("'")){
				continue;
			}
			//other filtering...
			if(word.length()==1&&(!word.equals("i")&&!word.equals("a"))){
				continue;
			}
			
			acceptableWords.add(word);
		}
		return acceptableWords;
	}

	private static List<String> readWords(String wordListFilePath) throws IOException {
		FileReader reader=new FileReader(wordListFilePath);
		List<String> words = IOUtils.readLines(reader);
		return words;
	}

}
