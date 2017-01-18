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
package info.bioinfweb.libralign.alignmentarea.paintsettings;


import java.beans.PropertyChangeListener;



/**
 * Listener interface to be implemented by classes that want to be informed on changes happening in an 
 * instance of {@link PaintSettings}.
 * <p>
 * Changes of the simple properties are indicated by calls of {@link #propertyChange(java.beans.PropertyChangeEvent)}.
 * Additionally two methods indicating modifications of {@link PaintSettings#getTokenPainterList()} are defined.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public interface PaintSettingsListener extends PropertyChangeListener {
	/**
	 * Called if the {@link PaintSettings#getZoomX()} or {@link PaintSettings#getZoomY()} or both changed.
	 * <p>
	 * Zoom changes are indicated by a separate event type instead of 
	 * {@link #propertyChange(java.beans.PropertyChangeEvent)}, to avoid separate events (and e.g. multiple
	 * repaint operations) if both zoom factors are changed in one operation.  
	 * 
	 * @param event the object describing the event
	 */
	public void zoomChange(ZoomChangeEvent event);
	
	/**
	 * Indicating that a token painter at a certain position in the token painter list was replaced.
	 * <p>
	 * Note that this event is only fired if a direct replace happened and not if a more complex list change
	 * (e.g. modifying the list length and adding new painters at the) took place.
	 * 
	 * @param event the object describing the event
	 */
	public void tokenPainterReplaced(TokenPainterReplacedEvent event);
	
	/**
	 * Called if any change happens to the token painter list which is not a direct replacement of a token painter
	 * as indicated by {@link #tokenPainterReplaced(TokenPainterReplacedEvent)}. This event will happen if the alignment
	 * model of the associated alignment area was changed (or a concatenated model was modified) and according changes 
	 * e.g. to the length of the token painter list were made.
	 * 
	 * @param event the object describing the event
	 */
	public void tokenPainterListChange(PaintSettingsEvent event);
}
