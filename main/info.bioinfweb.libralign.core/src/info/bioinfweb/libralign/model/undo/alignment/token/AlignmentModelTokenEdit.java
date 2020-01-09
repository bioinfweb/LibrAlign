/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
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
package info.bioinfweb.libralign.model.undo.alignment.token;


import java.util.Collection;
import java.util.Collections;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.undo.EditRecorder;
import info.bioinfweb.libralign.model.undo.alignment.sequence.AlignmentModelSequenceEdit;



/**
 * Offers basic functionality used by edits that manipulate sequence elements (tokens). 
 * 
 * @author Ben St&ouml;ver
 * @since 0.10.0
 * @see EditRecorder
 */
public abstract class AlignmentModelTokenEdit<M extends AlignmentModel<T>, T> extends AlignmentModelSequenceEdit<M, T> {
	protected int beginIndex;
	protected Collection<? extends T> tokens;
	
	
	public AlignmentModelTokenEdit(M alignmentModel, String sequenceID, int beginIndex, Collection<? extends T> tokens) {
		
		super(alignmentModel, sequenceID);
		if (tokens == null) {
			throw new IllegalArgumentException("tokens must not be null.");
		}
		else {
			this.beginIndex = beginIndex;
			this.tokens = tokens;
		}
	}


	/**
	 * Returns the index in the sequence where this token edit is taking place.
	 * 
	 * @return an index >= 0 (The first position has the index 0.)
	 */
	public int getBeginIndex() {
		return beginIndex;
	}
	
	
	/**
	 * Returns the list of tokens this edit deals with. These are the tokens to be inserted or deleted or the 
	 * tokens that will replace the current tokens in case of {@link AlignmentModelSetTokensEdit}.  
	 * 
	 * @return an unmodifiable collection of tokens
	 */
	public Collection<? extends T> getTokens() {
		return Collections.unmodifiableCollection(tokens);
	}


	protected abstract String getOperationName();
	
	
	@Override
	public String getPresentationName() {  //TODO This will have to be refactored, since presentation names need to be provided by CombinedEdit.
		return getOperationName() + " " + tokens.size() + " sequence element(s) in \"" + 
	      getAlignmentModel().sequenceNameByID(getSequenceID()) + "\" beginning at column " + beginIndex;
	}
}
