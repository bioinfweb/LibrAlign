/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.model.io;


import info.bioinfweb.commons.IntegerIDManager;
import info.bioinfweb.commons.bio.CharacterSymbolType;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.semanticweb.owlapi.io.XMLUtils;



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

	
	private boolean checkTokenSetID(String id) throws IllegalArgumentException {
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
		//TODO Add constituents for ambiguity codes (Possibly model in token set? Should work in the same way, as the set of background colors is determined.)
		//     Note that will JPhyloIO add them anyway for NeXML.
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
		
		// The order is important. Ambiguity codes must be defined after atomic states, since they may reference them. (This is also relevant, if no constituents are defined, since JPhyloIO may add these for some formats.)
		for (T token : tokenSet) {  // Write atomic states first.
			if (!token.equals(defaultGapToken) && !token.equals(defaultMissingToken) && 
					CharacterSymbolType.ATOMIC_STATE.equals(tokenSet.getSymbolType(token))) {
				
				writeSingleTokenDefinitionEvent(receiver, idManager, token);
			}
		}
		
		// The missing token may reference atomic tokens and must therefore be written after them.
		if (defaultMissingToken != null) {
			writeSingleTokenDefinitionEvent(receiver, idManager, defaultMissingToken);  // Make sure the default missing data token in the first in the sequence.
		}
		
		for (T token : tokenSet) {  // Write uncertain states that may reference atomic states later. (Uncertain states should not reference other uncertain states.)
			if (!token.equals(defaultGapToken) && !token.equals(defaultMissingToken) && 
					CharacterSymbolType.UNCERTAIN.equals(tokenSet.getSymbolType(token))) {
				
				writeSingleTokenDefinitionEvent(receiver, idManager, token);
			}
		}
		
		if (start < end) {  // Interval events must not be created for alignments with the length 0.
			receiver.add(new CharacterSetIntervalEvent(start, end));
		}
		
		//TODO JPhyloIO sollte auch mehrere Gap- und Missing-Tokens im Modell akzeptieren (falls später Formate auftauchen, die
		//     das auch unterstützen). Bei Formaten, die nur eins zulassen, soll immer das erste (aus writeData()) verwendet werden
    	//     und die anderen als zusätzliche Zustände geschrieben werden.
		//     In NeXML gäbe es weiterhin die Möglichkeit bei den alternativen Tokens entsprechende Metadaten zu schreiben. Dies 
		//     könnte in JPhyloIO direkt geschehen oder aus LibrAlign oder der Anwendung. (Im Fall von AC würde man z.B. zu den 
		//     supergaps weitere Informationen als nur "alternative Lücke" speichern wollen. (Außer bei einem evtl. Export wird
		//     in AC allerdings ohnehin das Superalignment nicht direkt gespeichert, weshalb dort diese Tokens nicht vorkommen.)
	}
}
