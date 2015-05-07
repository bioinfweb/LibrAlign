/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben StÃ¶ver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.alignmentmodel.io;


import java.util.ArrayList;
import java.util.Collection;

import info.bioinfweb.jphyloio.JPhyloIOEventListener;
import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.SequenceTokensEvent;
import info.bioinfweb.libralign.alignmentmodel.AlignmentModel;
import info.bioinfweb.libralign.alignmentmodel.tokenset.TokenSet;



/**
 * Reader that writes a stream of JPhyloIO events into an instance of {@link AlignmentModel}.
 * 
 * @author Ben St&ouml;ver
 *
 * @param <T> - the type of sequence elements (tokens) the target model works with
 */
public class JPhyloIOSequenceDataListener<T> implements JPhyloIOEventListener {
	private AlignmentModel<T> sequenceDataProvider;
	private TokenSet<T> tokenSet;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param sequenceDataProvider the model to write the sequence data to
	 */
	public JPhyloIOSequenceDataListener(AlignmentModel<T> sequenceDataProvider) {
		super();
		this.sequenceDataProvider = sequenceDataProvider;
	}


	/**
	 * Returns the model this reader writes the data to.
	 * 
	 * @return the sequence model used
	 */
	public AlignmentModel<?> getSequenceDataProvider() {
		return sequenceDataProvider;
	}


	@Override
	public void processEvent(JPhyloIOEventReader source, JPhyloIOEvent event) {
		switch (event.getEventType()) {
			case ALIGNMENT_START:
				//TODO Find way to determine which alignment to load and to load multiple alignments.
				break;
			case SEQUENCE_CHARACTERS:
				SequenceTokensEvent tokensEvent = event.asSequenceTokensEvent();
				int id = sequenceDataProvider.sequenceIDByName(tokensEvent.getSequenceName());
				if (id == -1) {
					sequenceDataProvider.addSequence(tokensEvent.getSequenceName());
					id = sequenceDataProvider.sequenceIDByName(tokensEvent.getSequenceName());
				}
				Collection<T> tokens = new ArrayList<T>(tokensEvent.getCharacterValues().size());
				for (String representation : tokensEvent.getCharacterValues()) {
					tokens.add(tokenSet.tokenByRepresentation(representation));
				}
				sequenceDataProvider.appendTokensAt(id, tokens);
				break;
			case ALIGNMENT_END:
				break;
		}
	}
}
