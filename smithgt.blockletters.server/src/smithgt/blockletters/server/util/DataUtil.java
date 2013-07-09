package smithgt.blockletters.server.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import smithgt.blockletters.server.model.EncodedWord;
import smithgt.blockletters.server.model.IdentifiedObject;


public enum DataUtil {
	INSTANCE;

	//	public static blocklettersdictionary getdictionarybyname(string dictionaryname){
	//		persistencemanager pm = pmf.get().getpersistencemanager();
	//		try{
	//			query query = pm.newquery(blocklettersdictionary.class);
	//			query.declareparameters("string nameparam");
	//			query.setfilter("name==nameparam");
	//
	//			@suppresswarnings("unchecked")
	//			list<blocklettersdictionary> list=(list<blocklettersdictionary>) query.execute(dictionaryname);
	//			if(list!=null&&list.size()>0){
	//				blocklettersdictionary dictionary=list.get(0);
	//				//				return pm.detachcopy(dictionary);
	//				return dictionary;
	//			}
	//			else{
	//				return null;
	//			}
	//		}finally{
	//			pm.close();
	//		}
	//
	//	}

	public <T extends IdentifiedObject> void deleteAll(Class<T> clazz, List<T> list){
		EntityManager em = EMFService.get().createEntityManager();


		for(T object:list){
			T toRemove=em.find(clazz, object.getId());

			try{
				em.getTransaction().begin();
				em.remove(toRemove);
				em.getTransaction().commit();
			}finally{
				if(em.getTransaction().isActive()){
					em.getTransaction().rollback();
				}
			}
		}
		em.close();
	}

	public <T> void persist(T object) {
		synchronized (this) {
			EntityManager em = EMFService.get().createEntityManager();
			em.persist(object);
			em.close();
		}
	}

	public void persistEncodedWords(Map<String, EncodedWord> encodedWordMap) {
		synchronized (this) {
			for(EncodedWord encodedWord:encodedWordMap.values()){
				Collections.sort(encodedWord.getActualWords());
				persist(encodedWord);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<EncodedWord> getEncodedWordsByDictionary(String dictionaryName) {
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select w from EncodedWord w");
		return (List<EncodedWord>)q.getResultList();
	}

	public static List<String> getActualWords(String encodedWordString) {
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select w from EncodedWord w where w.encodedString = :encodedStringParam");
		q.setParameter("encodedStringParam", encodedWordString);
		try{
			EncodedWord encodedWord=(EncodedWord) q.getSingleResult();
			return encodedWord.getActualWords();
		}catch(NoResultException e){
			return new ArrayList<String>();
		}
	}

	public static EncodedWord getEncodedWord(String encodedWordString) {
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select w from EncodedWord w where w.encodedString = :encodedStringParam");
		q.setParameter("encodedStringParam", encodedWordString);
		try{
			return (EncodedWord) q.getSingleResult();
		}catch(NoResultException e){
			return null;
		}
	}
}
