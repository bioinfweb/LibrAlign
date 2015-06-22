/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2015  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.pherogram.model;


import javax.swing.event.ChangeEvent;



/**
 * All events indicating changes in an instance of {@link PherogramComponentModel} should be inherited
 * from this class.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class PherogramComponentModelChangeEvent extends ChangeEvent{
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param source the model where the change occurred
	 */ 
	public PherogramComponentModelChangeEvent(PherogramComponentModel source) {
		super(source);
	}
	
	
	@Override
	public PherogramComponentModel getSource() {
		return (PherogramComponentModel)super.getSource();
	}
}
