/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
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
package info.bioinfweb.libralign.dataarea;


import java.util.Iterator;

import info.bioinfweb.libralign.sequenceprovider.SequenceDataChangeListener;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.events.SequenceChangeEvent;
import info.bioinfweb.libralign.sequenceprovider.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.sequenceprovider.events.TokenChangeEvent;



/**
 * Helper class used by {@link DataAreaModel} to inform the data areas it contains about changes in the
 * associated {@link SequenceDataProvider}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public class DataAreaSequenceChangeListener implements SequenceDataChangeListener {
	private DataAreaModel owner;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the data area model which will be using this instance
	 */
	public DataAreaSequenceChangeListener(DataAreaModel owner) {
		super();
		this.owner = owner;
	}


	/**
	 * Returns the the data area model using this instance.
	 * 
	 * @return the data area model that was specified in the constructor
	 */
	public DataAreaModel getOwner() {
		return owner;
	}


	private <T> void fireAfterSequenceChange(DataAreaList list, SequenceChangeEvent<T> e) {
		Iterator<DataArea> iterator = list.iterator();
		while (iterator.hasNext()) {
			iterator.next().afterSequenceChange(e);
		}
	}
	
	
	@Override
	public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {
		fireAfterSequenceChange(getOwner().getTopAreas(), e);
		
		Iterator<Integer> iterator = e.getSource().sequenceIDIterator();
		while (iterator.hasNext()) {
			fireAfterSequenceChange(getOwner().getSequenceAreas(iterator.next()), e);
		}

		fireAfterSequenceChange(getOwner().getBottomAreas(), e);
	}
	

	private <T> void fireAfterSequenceRenamed(DataAreaList list, SequenceRenamedEvent<T> e) {
		Iterator<DataArea> iterator = list.iterator();
		while (iterator.hasNext()) {
			iterator.next().afterSequenceRenamed(e);
		}
	}
	
	
	@Override
	public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e) {
		fireAfterSequenceRenamed(getOwner().getTopAreas(), e);
		
		Iterator<Integer> iterator = e.getSource().sequenceIDIterator();
		while (iterator.hasNext()) {
			fireAfterSequenceRenamed(getOwner().getSequenceAreas(iterator.next()), e);
		}

		fireAfterSequenceRenamed(getOwner().getBottomAreas(), e);
	}
	

	private <T> void fireAfterTokenChange(DataAreaList list, TokenChangeEvent<T> e) {
		Iterator<DataArea> iterator = list.iterator();
		while (iterator.hasNext()) {
			iterator.next().afterTokenChange(e);
		}
	}
	
	
	@Override
	public <T> void afterTokenChange(TokenChangeEvent<T> e) {
		fireAfterTokenChange(getOwner().getTopAreas(), e);
		
		Iterator<Integer> iterator = e.getSource().sequenceIDIterator();
		while (iterator.hasNext()) {
			fireAfterTokenChange(getOwner().getSequenceAreas(iterator.next()), e);
		}

		fireAfterTokenChange(getOwner().getBottomAreas(), e);
	}
	

	private <T, U> void fireAfterProviderChanged(DataAreaList list, SequenceDataProvider<T> previous,
			SequenceDataProvider<U> current) {
		
		Iterator<DataArea> iterator = list.iterator();
		while (iterator.hasNext()) {
			iterator.next().afterProviderChanged(previous, current);
		}
	}
	
	
	@Override
	public <T, U> void afterProviderChanged(SequenceDataProvider<T> previous,
			SequenceDataProvider<U> current) {

		fireAfterProviderChanged(getOwner().getTopAreas(), previous, current);

		//TODO The event afterProviderChanged would only occur for data areas attached to the new provider, but
		//     data areas previously attached to sequences might anyway be removed. It is not clear yet, whether
		//     forwarding this event to sequence data areas makes sense.
//		Iterator<Integer> iterator = previous.sequenceIDIterator();
//		while (iterator.hasNext()) {
//			fireAfterProviderChanged(getOwner().getSequenceAreas(iterator.next()), previous, current);
//		}

		fireAfterProviderChanged(getOwner().getBottomAreas(), previous, current);
	}
}
