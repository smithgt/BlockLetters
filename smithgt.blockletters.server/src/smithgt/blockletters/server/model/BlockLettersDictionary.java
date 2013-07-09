//package smithgt.blockletters.server.model;
//
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.jdo.annotations.Element;
//import javax.jdo.annotations.Embedded;
//import javax.jdo.annotations.IdGeneratorStrategy;
//import javax.jdo.annotations.IdentityType;
//import javax.jdo.annotations.Join;
//import javax.jdo.annotations.Key;
//import javax.jdo.annotations.PersistenceCapable;
//import javax.jdo.annotations.Persistent;
//import javax.jdo.annotations.PrimaryKey;
//
//@PersistenceCapable(identityType=IdentityType.APPLICATION)
//public class BlockLettersDictionary {
//	@PrimaryKey
//    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
//    private Long id;
//
//	@Persistent
////	@Element(dependent = "true")
//	private List<EncodedWord> encodedWords;
//
//	private Map<String, EncodedWord> encodedWordMap123;
//
//	@Persistent
//	private String name; 
//
//	@Persistent
//	private URI wordListFileURI;
//
//	public BlockLettersDictionary(String name, URI wordListFileURI){
//		this.name=name;
//		this.wordListFileURI=wordListFileURI;
//		encodedWords=new ArrayList<EncodedWord>();
//		encodedWordMap123=new HashMap<String, EncodedWord>();
//	}
//
//	public String getName() {return name;}
//	public URI getWordListFileURI() {return wordListFileURI;}
//
//	public EncodedWord addEncodedWord(EncodedWord encodedWord){
//		encodedWordMap123.put(encodedWord.getEncodedString(), encodedWord);
//		encodedWords.add(encodedWord);
//		return encodedWord;
//	}
//	public boolean containsEncodedWord(String encoded) {
//		return encodedWordMap123.containsKey(encoded);
//	}
//
//	public EncodedWord getEncodedWord(String encoded) {
//		return encodedWordMap123.get(encoded);
//	}
//	public int getEncodedWordSize(){
//		return encodedWordMap123.size();
//	}
//
//	@Override
//	public String toString() {
//		return "EncodedDictionary [id=" + id + ", name=" + name
//				+ ", wordListFileURI=" + wordListFileURI + ", encodedWords.size()="+encodedWords.size()+"]";
//	}
//
//}
