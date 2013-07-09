package smithgt.blockletters.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.datanucleus.util.StringUtils;

import com.google.gson.Gson;

import smithgt.blockletters.server.model.EncodedWord;
import smithgt.blockletters.server.util.DataUtil;

@SuppressWarnings("serial")
public class BlockLettersServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(BlockLettersServlet.class.getName());
	
	//TODO: separate out into 2 servlets
	private static final String REGENERATE_ID="REGENERATE";
	private static final String ENCODED_WORD_ID="encodedWord";
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)	throws IOException {
		resp.setContentType("text/plain");
		resp.addHeader("Access-Control-Allow-Origin", "*");//required by d3
		
		if(StringUtils.notEmpty(req.getParameter(REGENERATE_ID))){
			String dictionaryName=req.getParameter(REGENERATE_ID);
			log.info("Regenerating dictionary: "+dictionaryName);
			
			List<EncodedWord> encodedWords=DataUtil.INSTANCE.getEncodedWordsByDictionary(dictionaryName);
			if(encodedWords.size()>0){
				log.info("\tDeleting old dictionary... ");
				DataUtil.INSTANCE.deleteAll(EncodedWord.class, encodedWords);
			}
			
			log.info("\tCreating new dictionary...");
			WordEncoder wordEncoder=new WordEncoder();
			Map<String, EncodedWord> encodedWordMap = wordEncoder.parseWords(dictionaryName);
			log.info("\tPersisting new dictionary...");
			DataUtil.INSTANCE.persistEncodedWords(encodedWordMap);
			
			resp.getWriter().println("Successfully created dictionary "+dictionaryName+" containing "+encodedWordMap.size()+" encoded word patterns.");
		}else if(StringUtils.notEmpty(req.getParameter(ENCODED_WORD_ID))){
			String encodedWordString=req.getParameter(ENCODED_WORD_ID);
			log.info("Received request for encoded word "+encodedWordString);
			
			List<EncodedWord> encodedWords=new ArrayList<EncodedWord>();
			EncodedWord encodedWord;
			String[] split=encodedWordString.split(" ");
			for(String encodedWordToken:split){
				//add a space
				if(encodedWords.size()>0){
					encodedWords.add(new EncodedWord(" ", "*"));
				}
				encodedWord=DataUtil.getEncodedWord(encodedWordToken);
				if(encodedWord==null){
					encodedWord=new EncodedWord(encodedWordToken, "*");
					String unknownWord="";
					for(int i=0;i<encodedWordToken.length();i++){
						unknownWord+="?";
					}
					encodedWord.getActualWords().add(unknownWord);
				}
				encodedWords.add(encodedWord);
			}
			log.info("\tReturning "+encodedWords);
			
			Gson gson=new Gson();
			resp.getWriter().println(gson.toJson(encodedWords));
		}
	}
}
