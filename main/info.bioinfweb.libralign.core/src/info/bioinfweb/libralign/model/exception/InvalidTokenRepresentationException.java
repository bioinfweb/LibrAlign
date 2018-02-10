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
package info.bioinfweb.libralign.model.exception;


import info.bioinfweb.libralign.model.tokenset.TokenSet;



/**
 * Indicates that no token associated with a certain string representation was found in a token set.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class InvalidTokenRepresentationException extends RuntimeException {
	private TokenSet<?> tokenSet;
	private String invalidRepresentation;

	
	public InvalidTokenRepresentationException(TokenSet<?> tokenSet, String invalidRepresentation) {
		super("The token representation \"" + invalidRepresentation + "\" was not found in this token set.");
		this.tokenSet = tokenSet;
		this.invalidRepresentation = invalidRepresentation;
	}
}
