/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2016  Ben Stöver
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
package info.bioinfweb.libralign.model.utils;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelChangeListener;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;



/**
 * Utility class that can calculate ungapped indices of sequences in an alignment model.
 * <p>
 * It allows to specify a set of tokens to be considered as gaps. This set may contain any token,
 * not necessarily actual gap tokens like '-'.
 * <p>
 * {@link #degapedIndex(int, int)} is the key method which calculates the index a token in a 
 * sequence would have, if that sequence contained no gaps (no tokens contained in {@link #getGapTokens()}).
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 *
 * @param <T> the token type used in the associated alignment model
 */
public class DegapedIndexCalculator<T> {
	private static final class IndexInfo {
		public int originalIndex = 0;
		public int degapedIndex = 0;
	}
	
	
	private final AlignmentModelChangeListener MODEL_LISTENER = new AlignmentModelChangeListener() {
		@Override
		public <T> void afterTokenChange(TokenChangeEvent<T> e) {
			indexInfos.remove(e.getSequenceID());
		}
		
		@Override
		public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e) {}
		
		@Override
		public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {
			if (e.getType().equals(ListChangeType.DELETION) || e.getType().equals(ListChangeType.REPLACEMENT)) {
				indexInfos.remove(e.getSequenceID());
			}
		}
		
		@Override
		public <T, U> void afterProviderChanged(AlignmentModel<T> previous,	AlignmentModel<U> current) {}
	};
	
	
	private AlignmentModel<T> model;
	private Set<T> gapTokens;
	private Map<String, IndexInfo> indexInfos = new HashMap<String, IndexInfo>();
	
	
	/**
	 * Creates a new instance of this class. 
	 * 
	 * @param model the alignment model to calculate ungapped indices from
	 * @param gapTokens a set of tokens to be considered as gaps
	 */
	public DegapedIndexCalculator(AlignmentModel<T> model, Set<T> gapTokens) {
		super();
		this.model = model;
		this.gapTokens = gapTokens;
		
		model.getChangeListeners().add(MODEL_LISTENER);
	}


	/**
	 * Creates a new instance of this class using a {@link HashSet} containing the gap token specified by the 
	 * token set of {@code model}. Additional gap tokens can be added using {@link #getGapTokens()} later on. 
	 * 
	 * @param model the alignment model to calculate ungapped indices in
	 */
	public DegapedIndexCalculator(AlignmentModel<T> model) {
		this(model, new HashSet<T>());
		getGapTokens().add(model.getTokenSet().getGapToken());
	}


	/**
	 * Returns the alignment model this instance works on.
	 * 
	 * @return the associated alignment model
	 */
	public AlignmentModel<T> getModel() {
		return model;
	}


	/**
	 * Returns the set of gap tokens used by this instance.  
	 * 
	 * @return a set of tokens to be considered as gaps
	 */
	public Set<T> getGapTokens() {
		return gapTokens;
	}
	
	
	private IndexInfo getIndexInfo(String sequenceID) {
		IndexInfo result = indexInfos.get(sequenceID);
		if (result == null) {
			result = new IndexInfo();
			indexInfos.put(sequenceID, result);
		}
		return result;
	}
	
	
	/**
	 * Calculates the ungapped index according to a position in the sequence containing gaps.
	 * <p>
	 * If {@code originalIndex} refers to position of the original sequence containing a gap,
	 * the ungapped index of the next non-gap token on the left is returned. If no non-gap
	 * token exists left of the specified position, 0 is returned.
	 * <p>
	 * The runtime of this method scales linearly to the distance between {@code originalIndex}
	 * in this call of this method and {@code originalIndex} in the previous call with the same 
	 * sequence ID. If this is the first call with the specified sequence ID ever or since the 
	 * last modification of the according sequence, the runtime is proportional to 
	 * {@code originalIndex}.
	 * 
	 * @param sequenceID the ID of the sequence where the ungapped index shall be determined
	 * @param originalIndex the index in the original sequence including gaps
	 * @return the index the token at the specified position would have, if there were no gaps
	 *         in the sequence
	 */
	public int degapedIndex(String sequenceID, int originalIndex) {
		if (Math2.isBetween(originalIndex, 0, model.getSequenceLength(sequenceID) - 1)) {
			IndexInfo info = getIndexInfo(sequenceID);
			
			// Move left: (Only one of both loops will be used.)
			while (originalIndex < info.originalIndex) {
				if (!gapTokens.contains(model.getTokenAt(sequenceID, info.originalIndex))) {
					info.degapedIndex--;
				}
				info.originalIndex--;
			}
	
			// Move right: (Only one of both loops will be used.)
			while (originalIndex > info.originalIndex) {
				if (!gapTokens.contains(model.getTokenAt(sequenceID, info.originalIndex))) {
					info.degapedIndex++;
				}
				info.originalIndex++;
			}
			
			return info.degapedIndex;
		}
		else {
			throw new IndexOutOfBoundsException("The index " + originalIndex + " in the sequence with the ID " + sequenceID + 
					"is not between 0 and " +	(model.getSequenceLength(sequenceID) - 1) + ".");
		}
	}


	@Override
	protected void finalize() throws Throwable {
		model.getChangeListeners().remove(MODEL_LISTENER);  //TODO Allow doing this manually with another method too?
		super.finalize();
	}
}
