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
package info.bioinfweb.libralign.model.factory;


import info.bioinfweb.commons.bio.CharacterStateSetType;



/**
 * Models the definition of a token. Token definitions are enumerated by {@link NewAlignmentModelParameterMap#get}
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class TokenDefinition {
	private String representation;
	private CharacterStateSetType meaning;
	
	
	public TokenDefinition(String representation, CharacterStateSetType meaning) {
		super();
		this.representation = representation;
		this.meaning = meaning;
	}
	
	
	public String getRepresentation() {
		return representation;
	}
	
	
	public CharacterStateSetType getMeaning() {
		return meaning;
	}
	
}
