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


import java.util.HashSet;
import java.util.Set;

import info.bioinfweb.commons.collections.PackedIntegerArrayList;
import info.bioinfweb.libralign.model.AlignmentModel;



/**
 * Instance of this tool class allow fast translation between aligned and unaligned indices of tokens in an {@link AlignmentModel}. 
 * A set of tokens to be considered as gaps can be defined using the {@code gapTokens} constructor parameter.
 * <p>
 * In contrast to {@link SequentialAccessIndexTranslator} this class stores the mapping for all positions of all sequences to 
 * allow fast random access, while {@link SequentialAccessIndexTranslator} calculates the mapping in each call with a runtime 
 * proportional to the distance between the indices used in two subsequent calls. Therefore this implementation is faster for 
 * random access but uses more memory. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.5.0
 */
public class RandomAccessIndexTranslator<T> extends AbstractIndexTranslator<T, RandomAccessIndexTranslator.IndexTranslations> {
	protected static final class IndexTranslations {
		private PackedIntegerArrayList unalignedIndices;
		private PackedIntegerArrayList alignedIndices;
		private int unalignedLength = 0;
	}
	
	
	public RandomAccessIndexTranslator(AlignmentModel<T> model, Set<T> gapTokens) {
		super(model, gapTokens);
	}


	/**
	 * Creates a new instance of this class using a {@link HashSet} containing the gap token specified by the 
	 * token set of {@code model}. Additional gap tokens can be added using {@link #getGapTokens()} later on. 
	 * 
	 * @param model the alignment model to calculate ungapped indices in
	 */
	public RandomAccessIndexTranslator(AlignmentModel<T> model) {
		this(model, new HashSet<T>());
		getGapTokens().add(model.getTokenSet().getGapToken());
	}


	@Override
	protected IndexTranslations createSequenceData(String sequenceID) {
		IndexTranslations result = new IndexTranslations();
		int length = getModel().getSequenceLength(sequenceID);
		int bitsPerValue = PackedIntegerArrayList.calculateBitsPerValue(length + 2);  // -2..(length - 1)
		result.alignedIndices = new PackedIntegerArrayList(bitsPerValue, -2, length);
		result.unalignedIndices = new PackedIntegerArrayList(bitsPerValue, -2, length);
		int alignedIndex = 0;
		int unalignedIndex = IndexRelation.OUT_OF_RANGE;
		
		while (alignedIndex < length) {
			if (!getGapTokens().contains(getModel().getTokenAt(sequenceID, alignedIndex))) {
				if (unalignedIndex == IndexRelation.OUT_OF_RANGE) {
					unalignedIndex = 0;
				}
				else {
					unalignedIndex++;
				}
				result.alignedIndices.add(alignedIndex);
			}
			result.unalignedIndices.add(unalignedIndex);
			alignedIndex++;
		}
		if (unalignedIndex >= 0) {  // The default value 0 will be left unchanged, if the unaligned index is still OUT_OF_RANGE (the sequence contained only gaps).
			result.unalignedLength = unalignedIndex + 1;
		}
		return result;
	}


	@Override
	public IndexRelation getUnalignedIndex(String sequenceID, int alignedIndex) {
		IndexTranslations translations = getSequenceData(sequenceID);
		
		if (alignedIndex < 0) {  // before alignment
			int unalignedIndexAfter = 0;
			if (translations.alignedIndices.size() == 0) {
				unalignedIndexAfter = IndexRelation.OUT_OF_RANGE;
			}
			return new IndexRelation(IndexRelation.OUT_OF_RANGE, IndexRelation.OUT_OF_RANGE, unalignedIndexAfter);
		}
		else if (alignedIndex >= getSequenceData(sequenceID).unalignedIndices.size()) {  // behind alignment
			int unalignedIndexBefore = (int)translations.alignedIndices.size() - 1;
			if (translations.alignedIndices.size() == 0) {
				unalignedIndexBefore = IndexRelation.OUT_OF_RANGE;
			}
			return new IndexRelation(unalignedIndexBefore, IndexRelation.OUT_OF_RANGE, IndexRelation.OUT_OF_RANGE);
		}
		else {  // in alignment
			int unalignedIndex = (int)translations.unalignedIndices.get(alignedIndex);
			if (getGapTokens().contains(getModel().getTokenAt(sequenceID, alignedIndex))) {
				int unalignedIndexAfter = unalignedIndex + 1;
				if (unalignedIndex == IndexRelation.OUT_OF_RANGE) {  // Possible if requested position is part of a leading gap.
					unalignedIndexAfter = 0;
				}
				
				if (unalignedIndexAfter >= translations.alignedIndices.size()) {
					unalignedIndexAfter = IndexRelation.OUT_OF_RANGE;
				}
				return new IndexRelation(unalignedIndex, IndexRelation.GAP, unalignedIndexAfter);
			}
			else {
				return new IndexRelation(unalignedIndex, unalignedIndex, unalignedIndex);
			}
		}
	}


	@Override
	public int getAlignedIndex(String sequenceID, int unalignedIndex) {
		if ((unalignedIndex < 0) || (unalignedIndex >= getSequenceData(sequenceID).alignedIndices.size())) {
			return IndexRelation.OUT_OF_RANGE;
		}
		else {
			return (int)getSequenceData(sequenceID).alignedIndices.get(unalignedIndex);
		}
	}


	@Override
	public int getUnalignedLength(String sequenceID) {
		return getSequenceData(sequenceID).unalignedLength;
	}
}
