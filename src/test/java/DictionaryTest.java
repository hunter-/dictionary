import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import dictionary.controller.Dictionary;
import dictionary.model.Definition;
import dictionary.model.DictionaryEntry;
import dictionary.model.PartOfSpeech;
import dictionary.model.WordForm;
import dictionary.model.WordSense;
import dictionary.utils.DatabaseUtil;

public class DictionaryTest {

	private static final String WF_STR = "test";
	private static final String DEF_STR = "test";
	private static final String POS_STR = "Noun";
	
	private static <T> T getFirst(Collection<T> list) {
		for (T item : list) return item;
		return null;
	}
	
	private static DictionaryEntry getTestEntry() {
		
		// test word form
		WordForm wf = new WordForm(WF_STR);
		
		// test word sense
		WordSense ws = new WordSense(new Definition(DEF_STR), new PartOfSpeech(POS_STR));
		ws.addWordForm(wf);
		
		// test entry
		DictionaryEntry e = new DictionaryEntry(wf);
		e.addSense(ws);
		return e;
	}
	
	private static void assertTestEntry(DictionaryEntry e, DictionaryEntry re) {
		
		// Check id
		assertEquals(e.getId(), re.getId());
		
		// Check root word
		assertEquals(re.getWordRoot().getWordForm(), WF_STR);
		
		// Check word sense
		for (WordSense ws : re.getWordSenses()) {
			for (WordForm wf : ws.getWorldForms()) {
				assertEquals(wf.getWordForm(), WF_STR);
			}
		}
	}

	@Test
	public void addRemoveLookupTest() {
		
		/*
		 * Case 0: add entry.
		 */
		
		// Get a test entry and try to add it
		DictionaryEntry e = getTestEntry();
		Dictionary.getInstance().addEntry(e);
		
		// Make sure it was added correctly
		Session session = DatabaseUtil.getSessionFactory().openSession();
		session.beginTransaction();
		DictionaryEntry re = (DictionaryEntry) session.get(DictionaryEntry.class, e.getId());
		assertTestEntry(e, re);
		session.getTransaction().commit();
		session.close();
		
		/*
		 * Case 1: lookup entry
		 */
		re = Dictionary.getInstance().lookupByEntry("test");
		assertNotNull(re);
		
		/*
		 * Case 2: remove entry
		 */
		Dictionary.getInstance().remove(e);
	}
	
	@Test
	public void addSenseTest() {
		
		/*
		 * Case 0: get an entry then add to it. Make sure it made it to DB.
		 */
		DictionaryEntry e = getTestEntry();
		WordSense newWs = new WordSense(new Definition("supported by one's feet"), new PartOfSpeech("Verb"));
		newWs.addWordForm(new WordForm("Stand"));
		Dictionary.getInstance().addSense(newWs, e);
		
		// Remove from DB
		Dictionary.getInstance().remove(e);
		assertEquals(Dictionary.getInstance().getAllEntries().size(), 0);
		
	}
	
	@Test
	public void addFromFileTest() {
		
		/*
		 * Case 0: load from file and check
		 */
		Dictionary.getInstance().addFromFile("entry_list.csv");
		List<DictionaryEntry> es = Dictionary.getInstance().getAllEntries();
		
		// check count
		assertEquals(es.size(), 5);
		
		// get entries
		DictionaryEntry run = es.get(0);
		DictionaryEntry smile = es.get(1);
		DictionaryEntry dance = es.get(2);
		DictionaryEntry cheese = es.get(3);
		DictionaryEntry gargle = es.get(4);
		
		// check root words
		assertEquals(run.getWordRoot().getWordForm(), "run");
		assertEquals(smile.getWordRoot().getWordForm(), "smile");
		assertEquals(dance.getWordRoot().getWordForm(), "dance");
		assertEquals(cheese.getWordRoot().getWordForm(), "cheese");
		assertEquals(gargle.getWordRoot().getWordForm(), "gargle");
		
		// get word senses
		WordSense runSense = getFirst(run.getWordSenses());
		assertNotNull(runSense);
		WordSense smileSense = getFirst(smile.getWordSenses());
		assertNotNull(smileSense);
		WordSense danceSense = getFirst(dance.getWordSenses());
		assertNotNull(danceSense);
		WordSense cheeseSense = getFirst(cheese.getWordSenses());
		assertNotNull(cheeseSense);
		WordSense gargleSense = getFirst(gargle.getWordSenses());
		assertNotNull(gargleSense);
		
		// check that the word sense also has the word forms
		assertEquals(getFirst(runSense.getWorldForms()).getWordForm(), "run");
		assertEquals(getFirst(smileSense.getWorldForms()).getWordForm(), "smile");
		assertEquals(getFirst(danceSense.getWorldForms()).getWordForm(), "dance");
		assertEquals(getFirst(cheeseSense.getWorldForms()).getWordForm(), "cheese");
		assertEquals(getFirst(gargleSense.getWorldForms()).getWordForm(), "gargle");
		
		// check defs
		Definition runDef = new Definition("move at a speed faster than a walk and never have both or all the feet on the ground at the same time.");
		assertEquals(runSense.getDefinition().getDefinition(), runDef.getDefinition());
		Definition smileDef = new Definition("form one's features into a pleased or kind or amused expression");
		assertEquals(smileSense.getDefinition().getDefinition(), smileDef.getDefinition());
		Definition danceDef = new Definition("move rhythmically to music");
		assertEquals(danceSense.getDefinition().getDefinition(), danceDef.getDefinition());
		Definition cheeseDef = new Definition("a food made from the pressed curds of milk");
		assertEquals(cheeseSense.getDefinition().getDefinition(), cheeseDef.getDefinition());
		Definition gargleDef = new Definition("wash one's mouth and throat with a liquid kept in motion by exhaling through it.");
		assertEquals(gargleSense.getDefinition().getDefinition(), gargleDef.getDefinition());
		
		// Remove all from DB
		for (DictionaryEntry e : es)
			Dictionary.getInstance().remove(e);
		assertEquals(Dictionary.getInstance().getAllEntries().size(), 0);
	}

}
