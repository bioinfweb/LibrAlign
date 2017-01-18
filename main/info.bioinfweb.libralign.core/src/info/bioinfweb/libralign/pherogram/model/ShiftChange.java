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
package info.bioinfweb.libralign.pherogram.model;



/**
 * Used by {@link PherogramAlignmentModel} to model which position in the base call sequence corresponds to which
 * position in the editable alignment sequence.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class ShiftChange {
	protected int baseCallIndex = 0;
	protected int shiftChange = 0;


	public ShiftChange(int baseCallIndex, int shiftChange) {
		super();
		this.baseCallIndex = baseCallIndex;
		this.shiftChange = shiftChange;
	}


	public int getBaseCallIndex() {
		return baseCallIndex;
	}


	public int getShiftChange() {
		return shiftChange;
	}
}