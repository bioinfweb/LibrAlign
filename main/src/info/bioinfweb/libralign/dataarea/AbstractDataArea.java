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


import info.bioinfweb.libralign.AlignmentArea;



/**
 * Provides basic functionality for an implementation of {@link DataArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public abstract class AbstractDataArea implements DataArea {
	private AlignmentArea owner = null;
	private DataAreaList list = null;
	private boolean visible = true;
	
	
	@Override
	public AlignmentArea getOwner() {
		return owner;
	}
	
	
	@Override
	public DataAreaList getList() {
		return list;
	}


	@Override
	public void setList(DataAreaList list) {
		this.list = list;
	}


	@Override
	public boolean isVisible() {
		return visible;
	}


	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
