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
	//TODO This class is currently refactored to fully determine the gap borders every time a gap is reached. The previous 
	//     implementation (until r993) did loose count if the direction was changes within a gap. Since the code was not 
	//     easy to understand anymore, a major refactoring was planned. Since AlignmentComparator currently uses 
	//     RandomAccessIndexTranslator, refactoring was postponed.
	
	
	protected static final class IndexInfo {
		/** The aligned index related to the current token or the first position in the current gap. */
		public int alignedIndex = 0;
		
		/** The last position in the current gap or -1 if there is currently no gap. */
		public int lastAlignedIndexInGap = -1;
		
		/** 
		 * The unaligned index of token at the current aligned position or of the first token before the current gap 
		 * (or {@link IndexRelation#OUT_OF_RANGE}).
		 */
		public int unalignedIndex = 0;
		
		/** 
		 * The unaligned index of the first token after the current gap or {@link IndexRelation#OUT_OF_RANGE}. (Identical to 
		 * {{@link #unalignedIndex} if there is currently no gap.)
		 */
		public int unalignedIndexAfterGap = 0;
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
		IndexInfo result = new IndexInfo();
		if ((getModel().getSequenceLength(sequenceID) > 0) && getGapTokens().contains(getModel().getTokenAt(sequenceID, result.alignedIndex))) {
			result.unalignedIndex = IndexRelation.OUT_OF_RANGE;  // There is no position left of leading gaps.
			result.lastAlignedIndexInGap = 0;
			while ((getModel().getSequenceLength(sequenceID) > result.lastAlignedIndexInGap + 1) && 
					getGapTokens().contains(getModel().getTokenAt(sequenceID, result.lastAlignedIndexInGap + 1))) {
				
				result.lastAlignedIndexInGap++;
			}
		}
		
		if ((getModel().getSequenceLength(sequenceID) == result.lastAlignedIndexInGap + 1)) {  // The whole sequence only contains gaps.
			result.unalignedIndexAfterGap = IndexRelation.OUT_OF_RANGE;  // There is no position right of a gap spanning the whole sequence.
		}
		return result;
	}
	
	
//	private IndexRelation determineGapBorders(String sequenceID, IndexInfo info) {
//		if (info.unalignedIndex == IndexRelation.OUT_OF_RANGE) {  // Cursor has never left the leading gap.
//			int after = 0;
//			if (info.unalignedLength == 0) {
//				after = IndexRelation.OUT_OF_RANGE;
//			}
//			return new IndexRelation(IndexRelation.OUT_OF_RANGE, IndexRelation.GAP, after);
//		}
//		else {
//			int before;
//			int after;
//			if (info.lastMoveRight) {
//				before = info.unalignedIndex;
//				after = info.unalignedIndex + 1;
//				if (after >= info.unalignedLength) {
//					after = IndexRelation.OUT_OF_RANGE;
//				}
//			}
//			else {
//				if (info.unalignedIndex == 0) {
//					before = IndexRelation.OUT_OF_RANGE;
//				}
//				else {
//					before = info.unalignedIndex - 1;
//				}
//				after = info.unalignedIndex;
//			}
//			return new IndexRelation(before, IndexRelation.GAP, after);
//		}
//	}


	/**
	 * Calculates the ungapped index associated with a position in a sequence with gaps.
	 * <p>
	 * The runtime of this method scales linearly with the distance between {@code originalIndex}
	 * in this call of the method and {@code originalIndex} in the previous call with the same 
	 * sequence ID. If this is the first call with the specified sequence ID ever or since the 
	 * last modification of the respective sequence, the runtime is proportional to 
	 * {@code originalIndex}.
	 * 
	 * @param sequenceID the ID of the sequence where the ungapped index shall be determined
	 * @param alignedIndex the index in the original sequence including gaps
	 * @return an object describing the index the token at the specified position would have, if 
	 *         there were no gaps in the sequence
	 */
	@Override
	public IndexRelation getUnalignedIndex(String sequenceID, int alignedIndex) {
		if (Math2.isBetween(alignedIndex, 0, getModel().getSequenceLength(sequenceID) - 1)) {
			IndexInfo info = getSequenceData(sequenceID);
			
			if (Math2.isBetween(alignedIndex, info.alignedIndex, info.lastAlignedIndexInGap)) {
				return new IndexRelation(info.unalignedIndex, IndexRelation.GAP, info.unalignedIndexAfterGap);
			}
			else {
				if (alignedIndex < info.alignedIndex) {
					// info.unalignedIndex cannot be OUT_OF_RANGE here, since info.alignedIndex would be 0 then and the range check above would have applied.
					if (info.lastAlignedIndexInGap != -1) {  // Currently positioned in gap.
						info.alignedIndex--;  // Position the unaligned index on the first token before the current gap, which matches the current info.unalignedIndex.
						info.lastAlignedIndexInGap = -1;  // Indicate that the gap was left.
					}
					while (alignedIndex < info.alignedIndex) {
						info.alignedIndex--;
						if (!getGapTokens().contains(getModel().getTokenAt(sequenceID, info.alignedIndex))) {
							info.unalignedIndex--;
						}
						else {  // New gap was reached.
							
							//TODO Determine and save gap borders.
						}
					}
				}
			}
			//TODO Finish implementation.
			
			return null;
			
//			// Move left: (Only one of both loops will be used.)
//			while (alignedIndex < info.alignedIndex) {
//				info.alignedIndex--;
//				if (!getGapTokens().contains(getModel().getTokenAt(sequenceID, info.alignedIndex))) {
//					if (info.unalignedIndex == IndexRelation.OUT_OF_RANGE) {
//						info.unalignedIndex = 0;
//					}
//					else {
//						info.unalignedIndex--;
//					}
//				}
//				info.lastMoveRight = false;
//			}
//
//			// Move right: (Only one of both loops will be used.)
//			while ((alignedIndex > info.alignedIndex) && (info.alignedIndex < info.lastAlignedPosition)) {  // If going into the trailing gap would be allowed, alignedIndex would be set to a value not matching the unaligned index.
//				info.alignedIndex++;
//				if (!getGapTokens().contains(getModel().getTokenAt(sequenceID, info.alignedIndex))) {
//					if (info.unalignedIndex == IndexRelation.OUT_OF_RANGE) {
//						info.unalignedIndex = 0;
//					}
//					else {
//						info.unalignedIndex++;
//					}
//				}
//				info.lastMoveRight = true;
//			}
//
//			if (getGapTokens().contains(getModel().getTokenAt(sequenceID, alignedIndex))) {
//				return determineGapBorders(sequenceID, info);
//			}
//			else {
//				return new IndexRelation(info.unalignedIndex, info.unalignedIndex, info.unalignedIndex);
//			}
		}
		else {  // Throw exception:
			String message;
			int length = getModel().getSequenceLength(sequenceID);
			if (length == 0) {
				message = "The sequence with the ID " + sequenceID + " is empty.";
			}
			else {
				message = "The index " + alignedIndex + " in the sequence with the ID " + sequenceID + 
						" is not between 0 and " +	(length - 1) + ".";
			}
			throw new IndexOutOfBoundsException(message);
		}
	}


	@Override
	public int getAlignedIndex(String sequenceID, int unalignedIndex) {
		//TODO Refactor/reimplement according to new strategy.
		
		IndexInfo info = getSequenceData(sequenceID);

		// Move left: (Only one of both loops will be used.)
		while ((unalignedIndex < info.unalignedIndex) && (info.alignedIndex >= 0)) {
			info.alignedIndex--;
			if (!getGapTokens().contains(getModel().getTokenAt(sequenceID, info.alignedIndex))) {
				if (info.unalignedIndex == IndexRelation.OUT_OF_RANGE) {
					info.unalignedIndex = 0;
				}
				else {
					info.unalignedIndex--;
				}
			}
		}

		// Move right: (Only one of both loops will be used.)
		while ((unalignedIndex > info.unalignedIndex) && (info.alignedIndex < getModel().getSequenceLength(sequenceID))) {
			info.alignedIndex++;
			if (!getGapTokens().contains(getModel().getTokenAt(sequenceID, info.alignedIndex))) {
				if (info.unalignedIndex == IndexRelation.OUT_OF_RANGE) {
					info.unalignedIndex = 0;
				}
				else {
					info.unalignedIndex++;
				}
			}
		}
		
		return info.alignedIndex;
	}
}
