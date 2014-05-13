/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.sequenceprovider.events;


import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;

import java.util.Collection;



/**
 * Event object that indicates that tokens in a sequence provided by an instance of 
 * {@link SequenceDataProvider} were inserted, removed or replaced.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * 
 * @param <T> - the type of sequence elements (tokens) the implementing provider object works with
 */
public class TokenChangeEvent<T> extends SequenceChangeEvent<T> {
	private int startIndex;
	private Collection<Object> affectedTokens;
	
	
	public TokenChangeEvent(SequenceDataProvider<T> source, int sequenceID, ListChangeType type,
			int startIndex, Collection<Object> affectedTokens) {
		
		super(source, sequenceID, type);
		this.startIndex = startIndex;
		this.affectedTokens = affectedTokens;
	}


	/**
	 * Returns the (former) index of the first affected token.
	 * 
	 * @return a value greater or equal to zero (The first token of sequence has the index 0.)
	 */
	public int getStartIndex() {
		return startIndex;
	}


	/**
	 * Returns a collection of tokens that have been affected by this operation.
	 * 
	 * @return a non empty collection
	 */
	public Collection<Object> getAffectedTokens() {
		return affectedTokens;
	}
	
	
	/**
	 * Returns the first affected token. Convenience method if only one element is affected.
	 * 
	 * @return the fist affected element or {@code null} if {@link #getAffectedElements()} return an empty list
	 */
	public Object getAffectedToken() {
		if (!getAffectedTokens().isEmpty()) {
			return getAffectedTokens().iterator().next();
		}
		else {
			return null;
		}
	}
}
