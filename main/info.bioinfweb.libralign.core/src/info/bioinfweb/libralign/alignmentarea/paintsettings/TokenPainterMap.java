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
package info.bioinfweb.libralign.alignmentarea.paintsettings;



import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.AminoAcidTokenPainter;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.NucleotideTokenPainter;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.SingleColorTokenPainter;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.TokenPainter;

import java.util.EnumMap;



/**
 * Provides best matching token painters depending on the specified token set type.
 * <p>
 * For each {@link CharacterStateSetType} a separate token painter can be provided. If the map
 * contains no matching painter, an instance of {@link SingleColorTokenPainter} without any 
 * entries in its color map is provided. By default {@link NucleotideTokenPainter} and
 * {@link AminoAcidTokenPainter} are contained in the map, but this can be adjusted using
 * {@link #putTokenPainter(CharacterStateSetType, TokenPainter)}.
 * <p>
 * Instances of this class are used to provide default painters by {@link TokenPainterList}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.6.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 */
public class TokenPainterMap {
	private EnumMap<CharacterStateSetType, TokenPainter> painterMap;
	private TokenPainter defaultPainter;

	
	/**
	 * Creates a new instance of this class.
	 */
	public TokenPainterMap() {
		super();
		painterMap = new EnumMap<CharacterStateSetType, TokenPainter>(CharacterStateSetType.class);
		painterMap.put(CharacterStateSetType.NUCLEOTIDE, new NucleotideTokenPainter());
		painterMap.put(CharacterStateSetType.AMINO_ACID, new AminoAcidTokenPainter());
		defaultPainter = new SingleColorTokenPainter();  // Default painter of no other matches.
	}


	/**
	 * Returns the best matching painter to specified token type. The mapping for 
	 * {@link CharacterStateSetType#NUCLEOTIDE} are returned is no matching painter for 
	 * {@link CharacterStateSetType#DNA} or {@link CharacterStateSetType#RNA} is found.
	 * The mapping for {@link CharacterStateSetType#DISCRETE} is returned for all 
	 * discrete types for which no direct mapping is present.
	 * 
	 * @param type the token type for which a painter is requested
	 * @return the best matching painter (never {@code null})
	 */
	public TokenPainter getPainter(CharacterStateSetType type) {
		if (type == null) {
			return defaultPainter;
		}
		else {
			TokenPainter result = painterMap.get(type);
			if (result == null) {
				if (type.isNucleotide()) {
					result = painterMap.get(CharacterStateSetType.NUCLEOTIDE);
				}
				if (result == null) {
					if (type.isDiscrete()) {
						result = painterMap.get(CharacterStateSetType.DISCRETE);
					}
					if (result == null) {
						return defaultPainter;
					}
				}
			}
			return result;
		}
	}
	
	
	/**
	 * Specifies a new painter for a certain token type.
	 * 
	 * @param type the token type to map the painter to
	 * @param painter the new painter
	 * @return the painter previously mapped to {@code type} or {@code null}
	 * @throws NullPointerException if {@code type} or {@code painter} are {@code null}
	 */
	public TokenPainter putTokenPainter(CharacterStateSetType type, TokenPainter painter) {
		if (painter == null) {  // painterMap.put will throw an NPE if type is null
			throw new NullPointerException("painter must not be null.");
		}
		else {
			return painterMap.put(type, painter);
		}
	}
	
	
	/**
	 * Removes the painter mapping for the specified token type.
	 * 
	 * @param type the token type to remove the mapping for
	 * @return the painter previously mapped to {@code type} or {@code null}
	 */
	public TokenPainter removeTokenPainter(CharacterStateSetType type) {
		return painterMap.remove(type);
	}
}
