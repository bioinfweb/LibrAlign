/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bioinfweb.libralign.model.implementations;


import info.bioinfweb.libralign.model.SequenceAccessAlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.exception.SequenceNotFoundException;
import info.bioinfweb.libralign.model.tokenset.TokenSet;



/**
 * An implementation of {@link AlignmentModel} backed by a set of {@link CharSequence} implementations
 * (e.g. {@link String} objects).
 * <p>
 * This class does not allow to write tokens (characters) since this is not supported by the 
 * {@link CharSequence} interface. If you use a character sequence implementation that supports writing
 * you can subclass this class and overwrite the methods that edit tokens.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
public class CharSequenceAlignmentModel extends AbstractUnmodifyableAlignmentModel<CharSequence, Character>
    implements SequenceAccessAlignmentModel<CharSequence, Character> {
	
	/**
	 * Creates a new instance of this class using a possibly shared ID manager.
	 * 
	 * @param tokenSet - the token set which is supported by the implementation
	 * @param idManager the ID manager to be used by the new instance (maybe shared among multiple instances) 
	 */
	public CharSequenceAlignmentModel(TokenSet<Character> tokenSet, SequenceIDManager idManager) {
		super(tokenSet, idManager);
	}


	/**
	 * Creates a new instance of this class using its own ID manager.
	 * 
	 * @param tokenSet - the token set which is supported by the implementation
	 */
	public CharSequenceAlignmentModel(TokenSet<Character> tokenSet) {
		this(tokenSet, new SequenceIDManager());
	}


	/**
	 * Returns an empty string as the new character sequence.
	 * 
	 * @return ""
	 */
	@Override
	protected CharSequence createNewSequence(int sequenceID, String sequenceName) {
		return "";
	}


	@Override
	public int getSequenceLength(int sequenceID) {
		CharSequence sequence = getSequence(sequenceID);
		if (sequence != null) {
			return sequence.length();
		}
		else {
			return -1;
		}
	}


	@Override
	public Character getTokenAt(int sequenceID, int index) {
		CharSequence sequence = getSequence(sequenceID);
		if (sequence != null) {
			return sequence.charAt(index);
		}
		else {
			throw new SequenceNotFoundException(this, sequenceID);
		}
	}
}
