/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
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
package info.bioinfweb.libralign.sequenceprovider.adapters;



/**
 * Exception that indicates that {@link SingleCharSequenceAdapter} tried to convert a token to a character
 * which has a string representation that is not exactly one character long and 
 * {@link SingleCharSequenceAdapter#isCutLongRepresentations()} was set to {@code false}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
public class InvalidStringRepresentationException extends RuntimeException {
	private SingleCharSequenceAdapter<?> adapter;
	private Object token;
	private String representation;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param adapter - the adapter that tries to convert the token to a character
	 * @param token - the token that could not be converted
	 * @param representation - the invalid representation of that token
	 */
	public InvalidStringRepresentationException(SingleCharSequenceAdapter<?> adapter, Object token, 
			String representation) {
		
		super("The string representation \"" + representation + 
				"\" is not exectly one character long which is not allowd by the used SingleCharSequenceAdapter.");
		this.adapter = adapter;
		this.token = token;
		this.representation = representation;
	}


	public SingleCharSequenceAdapter<?> getAdapter() {
		return adapter;
	}


	public Object getToken() {
		return token;
	}


	public String getRepresentation() {
		return representation;
	}
}
