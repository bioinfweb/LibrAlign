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
package info.bioinfweb.libralign.dataarea;


import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.SequenceColorSchema;
import info.bioinfweb.libralign.pherogram.PherogramComponent;
import info.bioinfweb.libralign.pherogram.PherogramProvider;

import java.awt.Dimension;
import java.util.EnumSet;
import java.util.Set;



/**
 * A data area displaying a trace file resulting from Sanger sequencing.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class PherogramArea extends DataArea implements PherogramComponent {
	private PherogramProvider pherogram; 
	
	
	public PherogramArea(AlignmentArea owner, PherogramProvider pherogram) {
		super(owner);
		this.pherogram = pherogram;
	}


	@Override
	public void paint(TICPaintEvent event) {
	}


	@Override
	public Set<DataAreaListType> validLocations() {
		return EnumSet.of(DataAreaListType.SEQUENCE);
	}


	@Override
	public Dimension getSize() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public PherogramProvider getProvider() {
		return pherogram;
	}


	@Override
	public double getVerticalScale() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void setVerticalScale(double value) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public SequenceColorSchema getColorSchema() {
		// TODO Auto-generated method stub
		return null;
	}
}
