/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.model.implementations.decorate;


import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.tokenset.TokenSet;
import info.bioinfweb.libralign.model.utils.AlignmentModelUtils;



/**
 * Shows the underlying nucleotide data source as a RNA sequence, i.e. replaces all thymine tokens 
 * by uracil tokens.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 * 
 * @param <T> the type of sequence elements (tokens) the implementing decorator works with
 * @param <U> the type of sequence elements (tokens) the underlying model works with
 */
public class RNAAlignmentModelDecorator<T, U> extends AbstractTokenReplacementAlignmentModelDecorator<T, U> {
	public RNAAlignmentModelDecorator(TokenSet<T> tokenSet,	AlignmentModel<U> underlyingModel) {
		super(tokenSet, underlyingModel);
	}


	public static RNAAlignmentModelDecorator<Character, Character> newCharacterInstance(AlignmentModel<Character> underlyingModel) {
		TokenSet<Character> tokenSet = underlyingModel.getTokenSet().clone();
		tokenSet.add(AlignmentModelUtils.URACILE.charAt(0));
		return new RNAAlignmentModelDecorator<Character, Character>(tokenSet, underlyingModel);
	}
	
	
	@Override
	protected T convertUnderlyingToken(String underlyingSequenceID, int underlyingIndex, U underlyingToken) {
		if (underlyingToken != null) {
			String representation = getUnderlyingModel().getTokenSet().representationByToken(underlyingToken);
			if (representation.equals(AlignmentModelUtils.THYMINE)) {
				return getTokenSet().tokenByRepresentation(AlignmentModelUtils.URACILE);
			}
			else {
				return getTokenSet().tokenByRepresentation(representation);
			}
		}
		else {
			return null;
		}
	}

	
	@Override
	protected U convertDecoratedToken(String viewedSequenceID, int viewedIndex, T decoratedToken) {
		if (decoratedToken != null) {
			String representation = getTokenSet().representationByToken(decoratedToken);
			if (representation.equals(AlignmentModelUtils.URACILE)) {
				return getUnderlyingModel().getTokenSet().tokenByRepresentation(AlignmentModelUtils.THYMINE);
			}
			else {
				return getUnderlyingModel().getTokenSet().tokenByRepresentation(representation);
			}
		}
		else {
			return null;
		}
	}
}
