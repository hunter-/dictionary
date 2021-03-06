package dictionary.view;

import dictionary.controller.Dictionary;

/**
 * 
 * @author Hunter Wapman
 *
 */
public class RemoveCommand extends DictionaryCommand {
	private int idx;
	private String wordForm;
	
	public RemoveCommand(String query, String queryType) {
		this.query = query;
		this.queryType = queryType;
	}

	public RemoveCommand(String query, String queryType, int idx) {
		this(query, queryType);
		this.idx = idx;
	}

	public RemoveCommand(String query, String queryType, int idx, String wordForm) {
		this(query, queryType, idx);
		this.wordForm = wordForm;
	}
	
	@Override
	void run() {		
		switch (queryType) {
			case DICTIONARY_ENTRY_QUERY_TYPE: 
				Dictionary.getInstance().removeDictionaryEntry(query);
				status = getIdentifierString(query) + ": removed";
				break;
			case WORD_SENSE_QUERY_TYPE: 
				Dictionary.getInstance().removeWordSense(query, idx);
				status = getIdentifierString(query) + ": Word Sense " + idx + "removed";
				break;
			case DEFINITION_QUERY_TYPE: 
				Dictionary.getInstance().removeDefinition(query, idx);
				status = getIdentifierString(query, idx) + ": Definition removed";
				break;
			case PART_OF_SPEECH_QUERY_TYPE: 
				Dictionary.getInstance().removePartOfSpeech(query, idx);
				status = getIdentifierString(query, idx) + ": Part of Speech removed";
				break;
			case WORD_FORM_QUERY_TYPE:
				Dictionary.getInstance().removeWordForm(query, idx, wordForm);
				status = getIdentifierString(query, idx) + ": Word Form " + wordForm + "removed";
				break;
			default: 
				setNotSupportedStatus(queryType);
				break;
		}
	}
}
