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
package info.bioinfweb.libralign;


import java.awt.geom.Dimension2D;

import info.bioinfweb.commons.tic.TICPaintEvent;



/**
 * The area inside an {@link AlignmentArea} that displays one sequence of the alignment.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public class SequenceArea extends AlignmentSubArea {
	private int seqenceID;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the alignment area that will contain this instance
	 * @param seqenceID - the unique identifier of the sequence that will be displayed in this area
	 */
	public SequenceArea(AlignmentArea owner, int seqenceID) {
		super(owner);
		this.seqenceID = seqenceID;
	}


	public int getSeqenceID() {
		return seqenceID;
	}


	@Override
	public void paint(TICPaintEvent event) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public Dimension2D getSize() {
		// TODO Auto-generated method stub
		return null;
	}
}
