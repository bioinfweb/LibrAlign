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
package info.bioinfweb.libralign.model.utils.indextranslation;


import info.bioinfweb.commons.Math2;
import info.bioinfweb.libralign.model.AlignmentModel;

import java.util.HashSet;
import java.util.Set;



/**
 * Utility class that can calculate ungapped indices of sequences in an alignment model. {@link #getUnalignedIndex(int, int)} 
 * is the key method which calculates the index, a token in a sequence would have, if that sequence contained no gaps. 
 * It allows to specify a set of tokens to be considered as gaps using {@link #getGapTokens()}.
 * <p>
 * Instances of this class register as a change listener for their associated alignment model to handle edits that change the
 * mapping between aligned and unaligned indices.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 *
 * @param <T> the token type used in the associated alignment model
 */
public class SequentialAccessIndexTranslator<T> extends AbstractIndexTranslator<T, SequentialAccessIndexTranslator.IndexInfo> {
	protected static final class IndexInfo {
		public int alignedIndex = 0;
		public int unalignedIndex = 0;
	}
	
	
	/**
	 * Creates a new instance of this class. 
	 * 
	 * @param model the alignment model to calculate ungapped indices from
	 * @param gapTokens a set of tokens to be considered as gaps
	 */
	public SequentialAccessIndexTranslator(AlignmentModel<T> model, Set<T> gapTokens) {
		super(model, gapTokens);
	}


	/**
	 * Creates a new instance of this class using a {@link HashSet} containing the gap token specified by the 
	 * token set of {@code model}. Additional gap tokens can be added using {@link #getGapTokens()} later on. 
	 * 
	 * @param model the alignment model to calculate ungapped indices in
	 */
	public SequentialAccessIndexTranslator(AlignmentModel<T> model) {
		this(model, new HashSet<T>());
		getGapTokens().add(model.getTokenSet().getGapToken());
	}


	@Override
	protected IndexInfo createSequenceData(String sequenceID) {
		return new IndexInfo();
	}


	/**
	 * Calculates the ungapped index associated with a position in a sequence with gaps.
	 * <p>
	 * If {@code originalIndex} refers to a position in the original sequence of a gap,
	 * the ungapped index of the next non-gap token on the left is returned. If no non-gap
	 * token exists left of the specified position, 0 is returned.
	 * <p>
	 * The runtime of this method scales linearly with the distance between {@code originalIndex}
	 * in this call of the method and {@code originalIndex} in the previous call with the same 
	 * sequence ID. If this is the first call with the specified sequence ID ever or since the 
	 * last modification of the respective sequence, the runtime is proportional to 
	 * {@code originalIndex}.
	 * 
	 * @param sequenceID the ID of the sequence where the ungapped index shall be determined
	 * @param alignedIndex the index in the original sequence including gaps
	 * @return the index the token at the specified position would have, if there were no gaps
	 *         in the sequence
	 */
	@Override
	public IndexRelation getUnalignedIndex(String sequenceID, int alignedIndex) {
		//TODO Finish implementation
		
		if (Math2.isBetween(alignedIndex, 0, getModel().getSequenceLength(sequenceID) - 1)) {
			IndexInfo info = getSequenceData(sequenceID);
			
			// Move left: (Only one of both loops will be used.)
			while (alignedIndex < info.alignedIndex) {  //TODO Make sure to move to the left of a gap?
				if (!getGapTokens().contains(getModel().getTokenAt(sequenceID, info.alignedIndex))) {
					info.unalignedIndex--;
				}
				info.alignedIndex--;
			}
	
			// Move right: (Only one of both loops will be used.)
			while (alignedIndex > info.alignedIndex) {
				if (!getGapTokens().contains(getModel().getTokenAt(sequenceID, info.alignedIndex))) {
					info.unalignedIndex++;
				}
				info.alignedIndex++;
			}
			
			//return info.unalignedIndex;
			return null;
		}
		else {
			throw new IndexOutOfBoundsException("The index " + alignedIndex + " in the sequence with the ID " + sequenceID + 
					" is not between 0 and " +	(getModel().getSequenceLength(sequenceID) - 1) + ".");
		}
	}


	@Override
	public int getAlignedIndex(String sequenceID, int unalignedIndex) {
		//TODO Test implementation
		
		IndexInfo info = getSequenceData(sequenceID);
		
		// Move left: (Only one of both loops will be used.)
		while (unalignedIndex < info.unalignedIndex) {
			if (!getGapTokens().contains(getModel().getTokenAt(sequenceID, info.alignedIndex))) {
				info.unalignedIndex--;
			}
			info.alignedIndex--;
		}

		// Move right: (Only one of both loops will be used.)
		while (unalignedIndex > info.unalignedIndex) {
			if (!getGapTokens().contains(getModel().getTokenAt(sequenceID, info.alignedIndex))) {
				info.unalignedIndex++;
			}
			info.alignedIndex++;
		}
		
		return info.unalignedIndex;
	}
}
