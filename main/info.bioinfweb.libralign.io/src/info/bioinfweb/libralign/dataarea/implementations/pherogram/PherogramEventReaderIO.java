/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
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
package info.bioinfweb.libralign.dataarea.implementations.pherogram;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.libralign.model.io.AbstractDataElementEventReader;
import info.bioinfweb.libralign.model.io.AlignmentDataReader;
import info.bioinfweb.libralign.model.io.DataElementKey;
import info.bioinfweb.libralign.pherogram.model.PherogramAreaModel;
import info.bioinfweb.libralign.pherogram.model.ShiftChange;
import info.bioinfweb.libralign.pherogram.provider.PherogramProviderByURL;
import info.bioinfweb.libralign.pherogram.provider.PherogramReference;



public class PherogramEventReaderIO extends AbstractDataElementEventReader<PherogramReference> {
	//TODO Use LibrAlign namespaces and move this class there.
	
	private URIOrStringIdentifier predicate;
	private String currentSequenceID = null;
	private String currentAlignmentID = null;
	private int leftCutPosition;
	private int rightCutPosition;
	private int firstSeqPosition;
	private PherogramProviderByURL provider;
	private List<ShiftChange> shiftChangeList = new ArrayList<ShiftChange>();
	private URL pherogramURL;

	
	
	public PherogramEventReaderIO(AlignmentDataReader mainReader, PherogramProviderByURL provider) {
		super(mainReader, null);
		this.provider = provider;
	}
	
	
	private void getPherogramAreaModel() {
		if (pherogramURL != null) {
			DataElementKey key = new DataElementKey(currentAlignmentID, currentSequenceID);
			try {
				PherogramReference reference = new PherogramReference(getMainReader().getAlignmentModelReader().getModelByJPhyloIOID(key.getAlignmentID()), 
						provider.getPherogramProvider(pherogramURL), pherogramURL, currentSequenceID, shiftChangeList);
				if (leftCutPosition != -1) {
					reference.setLeftCutPosition(leftCutPosition);
				}
				if (rightCutPosition != -1) {
					reference.setRightCutPosition(rightCutPosition);				
				}
				if (firstSeqPosition != -1) {
					reference.setFirstSeqPos(firstSeqPosition);
				}
				
				getCompletedElements().put(key, reference);
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	@Override
	public void processEvent(JPhyloIOEventReader source, JPhyloIOEvent event) throws IOException {
		switch (event.getType().getContentType()) {
		case ALIGNMENT:
			if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
				currentAlignmentID = event.asLabeledIDEvent().getID();
			}
			break;
		case SEQUENCE:
			if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
				currentSequenceID = event.asLabeledIDEvent().getID();
			}
			else if (event.getType().getTopologyType().equals(EventTopologyType.END)) {
				getPherogramAreaModel();
				pherogramURL = null;
				leftCutPosition = -1;
				rightCutPosition = -1;
			}
			break;
		case RESOURCE_META:
			if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
				if (PherogramIOConstants.PREDICATE_HAS_PHEROGRAM.equals(event.asResourceMetadataEvent().getRel().getURI())) {
					pherogramURL = new URL((String) event.asResourceMetadataEvent().getHRef().toString());
				}
			}
			break;
		case LITERAL_META:
			if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
				predicate = event.asLiteralMetadataEvent().getPredicate();
			}
			break;
		case LITERAL_META_CONTENT:
			Object value = event.asLiteralMetadataContentEvent().getObjectValue();
			if (PherogramIOConstants.PREDICATE_IS_REVERSE_COMPLEMENTED.equals(predicate.getURI())) {

			}
			else if (PherogramIOConstants.PREDICATE_HAS_LEFT_CUT_POSITION.equals(predicate.getURI())) {
				leftCutPosition = (int) value;
			}
			else if (PherogramIOConstants.PREDICATE_HAS_RIGHT_CUT_POSITION.equals(predicate.getURI())) {
				rightCutPosition = (int) value;
			}
			else if (PherogramIOConstants.PREDICATE_HAS_FIRST_SEQ_POSITION.equals(predicate.getURI())) {
				firstSeqPosition = (int) value;
			}
			else if (PherogramIOConstants.PREDICATE_HAS_PHEROGRAM_ALIGNMENT.equals(predicate.getURI())) {
				shiftChangeList = (List<ShiftChange>) value;
			}

		default:
			break;
		}
	}
}