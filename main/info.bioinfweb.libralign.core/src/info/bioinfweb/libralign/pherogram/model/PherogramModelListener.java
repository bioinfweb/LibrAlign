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
package info.bioinfweb.libralign.pherogram.model;



/**
 * Interface to be implemented by all classes that need to be informed on changes in instances of
 * {@link PherogramComponentModel}.
 * <p>
 * Note that setting a new pherogram provider in the respective model often also leads to changing the cut
 * positions at the same time. In such cases the order of events is always 
 * {@link #pherogramProviderChange(PherogramProviderChangeEvent)}, than 
 * {@link #leftCutPositionChange(PherogramCutPositionChangeEvent)} and finally 
 * {@link #rightCutPositionChange(PherogramCutPositionChangeEvent)}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public interface PherogramModelListener {
	/**
	 * This method is called, if the pherogram provider of a pherogram component model was replaced
	 * or its contents changed. (Pherogram providers provide the base call sequence and the trace curves.)
	 * 
	 * @param event object the describing this event
	 */
	public void pherogramProviderChange(PherogramProviderChangeEvent event);
	
	/**
	 * This method is called, if the left cut position of the pherogram was changed.
	 * 
	 * @param event object the describing this event
	 */
	public void leftCutPositionChange(PherogramCutPositionChangeEvent event);
	
	/**
	 * This method is called, if the right cut position of the pherogram was changed.
	 * 
	 * @param event object the describing this event
	 */
	public void rightCutPositionChange(PherogramCutPositionChangeEvent event);
	
	/**
	 * This method is called, if the {@link PherogramAreaModel#getFirstSeqPos()} was changed.
	 * <p>
	 * Note that this event will only be fired by instances of {@link PherogramAreaModel} and never
	 * by an instance of {@link PherogramComponentModel}.
	 * 
	 * @param event object the describing this event
	 */
	public void firstSequencePositionChange(PherogramFirstSeqPosChangeEvent event);
	
	/**
	 * This method is called, if the shift changes stored by {@link PherogramAreaModel} were updated.
	 * <p>
	 * Note that this event will only be fired by instances of {@link PherogramAreaModel} and never
	 * by an instance of {@link PherogramComponentModel}.
	 * 
	 * @param event object the describing this event
	 */
	public void shiftChangeEdited(PherogramShiftChangeUpdateEvent event);
}
