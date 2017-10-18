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
package info.bioinfweb.libralign.model.implementations.swingundo.edits.token;


import info.bioinfweb.libralign.model.implementations.swingundo.SwingUndoAlignmentModel;
import info.bioinfweb.libralign.model.implementations.swingundo.edits.LibrAlignSwingAlignmentEdit;

import java.util.Collection;
import java.util.Collections;



/**
 * Offers basic functionality used by edits that manipulate sequence elements (tokens). 
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 * @see SwingUndoAlignmentModel
 */
public abstract class SwingTokenEdit<T> extends LibrAlignSwingAlignmentEdit<T> {
	protected String sequenceID;
	protected int beginIndex;
	protected Collection<? extends T> tokens;
	
	
	public SwingTokenEdit(SwingUndoAlignmentModel<T> provider, String sequenceID, int beginIndex, 
			Collection<? extends T> tokens) {
		
		super(provider);
		this.sequenceID = sequenceID;
		this.beginIndex = beginIndex;
		this.tokens = tokens;
	}


	/**
	 * Returns the sequence ID this edit deals with.
	 * 
	 * @return the sequence ID
	 */
	public String getSequenceID() {
		return sequenceID;
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
	 * tokens that will replace the current tokens in case of {@link SwingSetTokensEdit}.  
	 * 
	 * @return an unmodifiable collection of tokens
	 */
	public Collection<? extends T> getTokens() {
		return Collections.unmodifiableCollection(tokens);
	}


	protected abstract String getOperationName();
	
	
	@Override
	public String getPresentationName() {
		return getOperationName() + " " + tokens.size() + " sequence element(s) in \"" + 
	      getModel().sequenceNameByID(sequenceID) + "\" beginning at column " + beginIndex;
	}
}
