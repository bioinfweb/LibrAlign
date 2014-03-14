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


import java.awt.Graphics2D;

import info.bioinfweb.libralign.alignmentprovider.AlignmentDataProvider;



/**
 * GUI element of LibrAlign that displays an alignment including attached data views.
 * 
 * @author Ben St&ouml;ver
 */
public class AlignmentArea implements PaintableArea {
	private AlignmentDataProvider dataProvider;
	private WorkingMode workingMode = WorkingMode.VIEW;
	private AlignmentDataViewMode viewMode = AlignmentDataViewMode.NUCLEOTIDE;  //TODO Initial value should be adjusted when the data type of the specified provider is known.
	
	
	
	public AlignmentDataProvider getDataProvider() {
		return dataProvider;
	}
	
	
	public void setDataProvider(AlignmentDataProvider dataProvider) {
		this.dataProvider = dataProvider;
		//TODO repaint
	}
	
	
	public WorkingMode getWorkingMode() {
		return workingMode;
	}
	
	
	public void setWorkingMode(WorkingMode workingMode) {
		this.workingMode = workingMode;
	}
	
	
	public AlignmentDataViewMode getViewMode() {
		return viewMode;
	}
	
	
	public void setViewMode(AlignmentDataViewMode viewMode) {
		this.viewMode = viewMode;
		//TODO repaint
	}


	@Override
	public void paint(Graphics2D graphics) {
		//TODO Paint sequences
		//TODO Paint data views
	}
}
