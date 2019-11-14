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
package info.bioinfweb.libralign.dataarea.implementations.consensus;



/**
 * Models a fraction for one token at one position of a consensus sequence. This class is used by {@link ConsensusSequenceModel}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.10.0
 */
public class FractionInfo {
	private String representation;
	private double fraction;
	
	
	public FractionInfo(String representation, double fraction) {
		super();
		this.representation = representation;
		this.fraction = fraction;
	}


	public String getRepresentation() {
		return representation;
	}


	public double getFraction() {
		return fraction;
	}
}
