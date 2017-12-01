package dictionary.model;

/**
 * 
 * @author Adam Wiemerslage
 *
 * This class represents a word form in a dictionary entry.
 */
public class WordForm implements ICollide{
	
	/**
	 * The text for the word form token
	 */
	String wordForm;
	
	public String getWordForm() {
		return wordForm;
	}
	
	public boolean collides(WordForm wf) {
		return wf.getWordForm().equals(getWordForm());
	}
}