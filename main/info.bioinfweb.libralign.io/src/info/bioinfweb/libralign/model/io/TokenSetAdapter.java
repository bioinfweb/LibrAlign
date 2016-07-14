package info.bioinfweb.libralign.model.io;


import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.semanticweb.owlapi.io.XMLUtils;

import info.bioinfweb.commons.IntegerIDManager;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.dataadapters.ObjectListDataAdapter;
import info.bioinfweb.jphyloio.events.CharacterSetIntervalEvent;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.SingleTokenDefinitionEvent;
import info.bioinfweb.jphyloio.events.TokenSetDefinitionEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.libralign.model.tokenset.TokenSet;



public class TokenSetAdapter<T> implements ObjectListDataAdapter<TokenSetDefinitionEvent> {
	private String idPrefix; 
	private TokenSet<T> tokenSet;
	private String tokenSetID;
	private long start;
	private long end;
	
	
	public TokenSetAdapter(String idPrefix, TokenSet<T> tokenSet, long startIndex, long endIndex) {
		super();
		if (!XMLUtils.isNCName(idPrefix)) {
			throw new IllegalArgumentException("The ID prefix  (\"" + idPrefix + "\") is not a valid NCName.");
		}
		else if (tokenSet == null) {
			throw new NullPointerException("tokenSet must not be null.");
		}
		else {
			this.idPrefix = idPrefix;
			this.tokenSet = tokenSet;
			tokenSetID = idPrefix + ReadWriteConstants.DEFAULT_TOKEN_SET_ID_PREFIX;
			this.start = startIndex;
			this.end = endIndex;
		}
	}


	public String getIDPrefix() {
		return idPrefix;
	}


	public TokenSet<T> getTokenSet() {
		return tokenSet;
	}

	
	private boolean checkTokenSetID(String id) {
		if (tokenSetID.equals(id)) {
			return true;
		}
		else {
			throw new IllegalArgumentException("A token set with the ID \"" + id + "\" is not present.");
		}
	}
	

	@Override
	public TokenSetDefinitionEvent getObjectStartEvent(ReadWriteParameterMap parameters, String id) throws IllegalArgumentException {
		checkTokenSetID(id);  // May throw an exception.
		return new TokenSetDefinitionEvent(tokenSet.getType(), id, null);
	}
	

	@Override
	public long getCount(ReadWriteParameterMap parameters) {
		return 1;
	}

	
	@Override
	public Iterator<String> getIDIterator(ReadWriteParameterMap parameters) {
		return Arrays.asList(new String[]{tokenSetID}).iterator();
	}

	
	private void writeSingleTokenDefinitionEvent(JPhyloIOEventReceiver receiver, IntegerIDManager idManager, T token) throws IOException {
		receiver.add(new SingleTokenDefinitionEvent(idPrefix + idManager.createNewID(), tokenSet.descriptionByToken(token), 
					tokenSet.representationByToken(token), tokenSet.getMeaning(token), tokenSet.getSymbolType(token)));
		//TODO Possibly add metadata (e.g. for alternative gap symbols) if that will not anyway be done in JPhyloIO in the future.
		receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.SINGLE_TOKEN_DEFINITION));
	}
	
	
	@Override
	public void writeContentData(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver, String id) throws IOException, IllegalArgumentException {
		checkTokenSetID(id);  // May throw an exception.
		IntegerIDManager idManager = new IntegerIDManager();
		
		T defaultGapToken = tokenSet.getGapToken();
		if (defaultGapToken != null) {
			writeSingleTokenDefinitionEvent(receiver, idManager, defaultGapToken);  // Make sure the default gap token in the first in the sequence.
		}
		
		T defaultMissingToken = tokenSet.getMissingInformationToken();
		if (defaultMissingToken != null) {
			writeSingleTokenDefinitionEvent(receiver, idManager, defaultMissingToken);  // Make sure the default missing data token in the first in the sequence.
		}
		
		for (T token : tokenSet) {
			if (!token.equals(defaultGapToken) && !token.equals(defaultMissingToken)) {
				writeSingleTokenDefinitionEvent(receiver, idManager, token);
			}
		}
		
		receiver.add(new CharacterSetIntervalEvent(start, end));
		
		//TODO JPhyloIO sollte auch mehrere Gap- und Missing-Tokens im Modell akzeptieren (falls später Formate auftauchen, die
		//     das auch unterstützen). Bei Formaten, die nur eins zulassen, soll immer das erste (aus writeData()) verwendet werden
    //     und die anderen als zusätzliche Zustände geschrieben werden.
		//     In NeXML gäbe es weiterhin die Möglichkeit bei den alternativen Tokens entsprechende Metadaten zu schreiben. Dies 
		//     könnte in JPhyloIO direkt geschehen oder aus LibrAlign oder der Anwendung. (Im Fall von AC würde man z.B. zu den 
		//     supergaps weitere Informationen als nur "alternative Lücke" speichern wollen. (Außer bei einem evtl. Export wird
		//     in AC allerdings ohnehin das Superalignment nicht direkt gespeichert, weshalb dort diese Tokens nicht vorkommen.)
	}
}
