/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2015  Ben Stöver
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
package info.bioinfweb.libralign.model.tokenset.continuous;


import info.bioinfweb.commons.bio.CharacterSymbolMeaning;



/**
 * A token class to represent continuous values. It acts as a wrapper for numeric values but additionally allows to act
 * as a gap or missing data token. In such cases the according value will be returned by {@link #getMeaning()} and 
 * {@code null} will be returned by {@link #getValue()}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 *
 * @param <V> the numeric type to be wrapped
 */
public class ContinuousToken<V extends Number> {
	private V value;
	private CharacterSymbolMeaning meaning;
	
	
	public ContinuousToken(V value) {
		this(value, CharacterSymbolMeaning.CHARACTER_STATE);
	}


	protected ContinuousToken(V value, CharacterSymbolMeaning meaning) {
		super();
		this.value = value;
		this.meaning = meaning;
	}
	
	
	public static <V extends Number> ContinuousToken<V> newGapInstance() {
		return new ContinuousToken<V>(null, CharacterSymbolMeaning.GAP);
	}


	public static <V extends Number> ContinuousToken<V> newMissingInformationInstance() {
		return new ContinuousToken<V>(null, CharacterSymbolMeaning.MISSING);
	}


	public V getValue() {
		return value;
	}
	
	
	public boolean hasValue() {
		return getValue() != null;
	}


	public CharacterSymbolMeaning getMeaning() {
		return meaning;
	}
}
