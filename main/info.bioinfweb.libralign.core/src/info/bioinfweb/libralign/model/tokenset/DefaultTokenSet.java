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
package info.bioinfweb.libralign.model.tokenset;


import info.bioinfweb.commons.bio.CharacterStateSetType;



/**
 * Basic implementation of a discrete token set. It can contain any set of objects and returns their string
 * representations for {@link #representationByToken(Object)} and {@link #descriptionByToken(Object)}.
 * <p>
 * In many cases a more specific implementation of {@link TokenSet} may be preferable. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 *
 * @param <T> the type of token in this token set
 */
public class DefaultTokenSet<T> extends AbstractTokenSet<T> {
	/**
	 * Returns a new instance of this class.
	 * 
	 * @param type the token type of the new instance
	 */
	public DefaultTokenSet(CharacterStateSetType type) {
		super(type);
	}


	@Override
	public String representationByToken(T token) {
		if (contains(token)) {
			if (token == null) {
				return "null";
			}
			else {
				return token.toString();
			}
		}
		else {
			return "";
		}
	}

	
	/**
	 * Returns the first token in this set returned by {@link #iterator()} with a string representation equal 
	 * to {@code representation}.
	 * <p>
	 * Inherited classes might want to overwrite this method to achieve a better performance for large token sets.
	 * 
	 * @param representation the string representation of the token
	 * @return the according token or {@code null} if none is defined
	 */
	@Override
	public T tokenByRepresentation(String representation) {
		for (T token : this) {
			if (token.toString().equals(representation)) {
				return token;
			}
		}
		return null;
	}

	
	@Override
	public int maxRepresentationLength() {
		return TokenSetTools.maxRepresentationLength(this);
	}

	@Override
	public boolean representationLengthEqual() {
		return TokenSetTools.representationLengthEqual(this);
	}

	@Override
	public String descriptionByToken(T token) {
		return token.toString();
	}

	
	@Override
	public DefaultTokenSet<T> clone() {
		DefaultTokenSet<T> result = new DefaultTokenSet<T>(getType());
		result.addAll(this);
		result.getKeyMap().putAll(getKeyMap());
		return result;
	}
}
