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


import java.util.Set;

import info.bioinfweb.commons.collections.PackedIntegerArrayList;
import info.bioinfweb.libralign.model.AlignmentModel;



/**
 * Instance of this tool class allow fast translation between aligned and unaligned indices of tokens in an {@link AlignmentModel}. 
 * A set of tokens to be considered as gaps can be defined using .
 * <p>
 * In contrast to {@link SequentialAccessIndexTranslator} this class stores the mapping for all positions of all sequences to allow fast
 * random access, while {@link SequentialAccessIndexTranslator} calculates the mapping in each call with a runtime proportional to the 
 * distance between the indices used in two subsequent calls. Therefore this implementation is faster for random access but uses
 * more memory. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.5.0
 */
public class RandomAccessIndexTranslator<T> extends AbstractIndexTranslator<T, RandomAccessIndexTranslator.IndexTranslations> {
	protected static final class IndexTranslations {
		private PackedIntegerArrayList unalignedIndices;
		private PackedIntegerArrayList alignedIndices;
	}
	
	
	public RandomAccessIndexTranslator(AlignmentModel<T> model, Set<T> gapTokens) {
		super(model, gapTokens);
	}


	@Override
	protected IndexTranslations createSequenceData(String sequenceID) {
		IndexTranslations result = new IndexTranslations();
		int length = getModel().getSequenceLength(sequenceID);
		int alignedIndex = 0;
		int unalignedIndex = 0;
		int bitsPerValue = PackedIntegerArrayList.calculateBitsPerValue(length);
		result.alignedIndices = new PackedIntegerArrayList(bitsPerValue, 0, length);
		result.unalignedIndices = new PackedIntegerArrayList(bitsPerValue, 0, length);
		
		while (alignedIndex < length) {
			result.alignedIndices.add(alignedIndex);
			result.unalignedIndices.add(unalignedIndex);
			if (!getGapTokens().contains(getModel().getTokenAt(sequenceID, alignedIndex))) {
				unalignedIndex++;
			}
			alignedIndex++;
		}
		return result;
	}


	@Override
	public IndexRelation getUnalignedIndex(String sequenceID, int alignedIndex) {
		//TODO Range check? Return OUT_OF_RANGE or exception?
		
		IndexTranslations translations = getSequenceData(sequenceID);
		int unalignedIndex = (int)translations.unalignedIndices.get(alignedIndex);
		if (getGapTokens().contains(getModel().getTokenAt(sequenceID, alignedIndex))) {
			int unalignedIndexAfter = IndexRelation.OUT_OF_RANGE;
			if (alignedIndex + 1 < translations.unalignedIndices.size()) {
				unalignedIndexAfter = (int)translations.unalignedIndices.get(alignedIndex + 1);
			}
			return new IndexRelation(unalignedIndex, IndexRelation.GAP, unalignedIndexAfter);
			//TODO Before value must possibly be set to OUT_OF_RANGE if sequence starts with a gap.
		}
		else {
			return new IndexRelation(unalignedIndex, unalignedIndex, unalignedIndex);
		}
	}


	@Override
	public int getAlignedIndex(String sequenceID, int unalignedIndex) {
		//TODO Range check? Return OUT_OF_RANGE or exception?
		return (int)getSequenceData(sequenceID).alignedIndices.get(unalignedIndex);
	}
}
