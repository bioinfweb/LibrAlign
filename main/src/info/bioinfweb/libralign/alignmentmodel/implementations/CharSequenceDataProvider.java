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
package info.bioinfweb.libralign.alignmentmodel.implementations;


import info.bioinfweb.libralign.alignmentmodel.SequenceAccessDataProvider;
import info.bioinfweb.libralign.alignmentmodel.AlignmentModel;
import info.bioinfweb.libralign.alignmentmodel.tokenset.TokenSet;



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
public class CharSequenceDataProvider extends AbstractUnmodifyableSequenceDataProvider<CharSequence, Character>
    implements SequenceAccessDataProvider<CharSequence, Character> {
	
	public CharSequenceDataProvider(TokenSet<Character> tokenSet) {
		super(tokenSet);
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
		return getSequence(sequenceID).length();
	}


	@Override
	public Character getTokenAt(int sequenceID, int index) {
		return getSequence(sequenceID).charAt(index);
	}
}
