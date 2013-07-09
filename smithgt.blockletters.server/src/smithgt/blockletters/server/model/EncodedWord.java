package smithgt.blockletters.server.model;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Join;
import javax.jdo.annotations.Persistent;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class EncodedWord implements IdentifiedObject {
	@Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String fromDictionary;

	@Persistent
	private String encodedString;

	@Join
	private List<String> actualWords;

	public EncodedWord(String encodedString, String fromDictionary){
		this.encodedString=encodedString;
		this.fromDictionary=fromDictionary;
		actualWords=new ArrayList<String>();
	}
	public boolean contains(String actualWord) {
		return actualWords.contains(actualWord);
	}
	public void add(String actualWord) {
		if(!actualWords.contains(actualWord)){
			actualWords.add(actualWord);
		}
	}
	
	@Override
	public Long getId() {return id;}
	public String getEncodedString() {return encodedString;}
	public String getFromDictionary() {return fromDictionary;}
	public List<String> getActualWords() {return actualWords;}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder("EncodedWord[encodedString="+encodedString+", actualWords="+actualWords+"]");
		return sb.toString();
	}
}
