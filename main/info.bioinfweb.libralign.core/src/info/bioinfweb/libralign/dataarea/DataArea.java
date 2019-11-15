/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.dataarea;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Set;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.SizeManager;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentSubArea;
import info.bioinfweb.libralign.dataelement.DataList;
import info.bioinfweb.libralign.dataelement.DataListType;



/**
 * All classes representing a data area in an {@link AlignmentArea} must be inherited from this class.
 * <p>
 * For performance reasons the space possibly required by data areas left and right of the aligned sequences is stored and not recalculated in every access.
 * Data area implementations must make sure to inform the {@link SizeManager} instance of their owning {@link AlignmentArea} (accessible via 
 * {@link AlignmentArea#getSizeManager()}) when recalculation becomes necessary by calling {@link SizeManager#setLocalMaxLengthBeforeAfterRecalculate()}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 */
public abstract class DataArea extends AlignmentSubArea {
	private DataList<AlignmentArea, DataArea> list = null;
	private boolean visible = true;
	protected PropertyChangeSupport propertyChangeListeners = new PropertyChangeSupport(this);
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner the alignment area that will contain this instance
	 * @param labeledArea the alignment area displays the sequence which is labeled by the new instance
	 *        (If {@code null} is specified here, the parent alignment area of {@code owner} will be assumed.)  
	 */
	public DataArea(AlignmentArea owner) {
		super(owner);
	}
	

	/**
	 * Returns the list this data area is contained in. This list defines whether this data area is located above 
	 * or underneath the alignment or attached to a sequence.
	 */
	public DataList<AlignmentArea, DataArea> getList() {
		return list;
	}


	/**
	 * Updates the list this element is contained in.
	 * 
	 * @param list - the list that contains this element
	 */
	public void setList(DataList<AlignmentArea, DataArea> list) {
		if (this.list != list) {  // Comparing with equals() is not required here since a reference change is sufficient, even of the contents of both lists would be identical.
			DataList<AlignmentArea, DataArea> formerList = this.list;
			this.list = list;
			propertyChangeListeners.firePropertyChange("list", formerList, list);
		}
	}


	/**
	 * Indicates whether this data area is currently faded in or out in the containing alignment area.
	 * <p>
	 * It does not state whether this data area is contained a area of the alignment area that is 
	 * currently visible on the screen. 
	 * 
	 * @return {@code true} if this data area is faded in, {@code false} if it is faded out
	 */
	public boolean isVisible() {
		return visible;
	}


	/**
	 * Fades this data area in or out and lets the containing alignment area repaint, if necessary.
	 * 
	 * @param visible
	 * @return {@code true} if the state of this element was changed, {@code false} otherwise
	 */
	public boolean setVisible(boolean visible) {
		boolean result = this.visible != visible; 
		if (result) {
			this.visible = visible;
			propertyChangeListeners.firePropertyChange("visible", !visible, visible);
			getList().getOwner().getOwner().fireDataAreaVisibilitChanged(this, visible);  //TODO This method should be removed from AlignmentArea and it should register a PropertyChangeListener instead.
		}
		return result;
	}
	
	
	/**
	 * The result should enumerate all valid locations where the implementing data area is allowed to be located.
	 * <p>
	 * An implementation for a data area could be located everywhere could look like this:
	 * <pre>return EnumSet.of(DataAreaListType.TOP, DataAreaListType.BOTTOM, DataAreaListType.SEQUENCE);</pre>
	 * 
	 * @return a non-empty set of locations
	 */
	public abstract Set<DataListType> validLocations();
	
	
	/**
	 * Returns the length in pixels considering the current zoom factor this component needs to display
	 * data left of the first alignment column.
	 * <p>
	 * Overwrite this method if your component displays additional information left of the alignment.
	 * 
	 * @return This default implementation always returns 0. 
	 */
	public double getLengthBeforeStart() {
		return 0;
	}
	
	
	/**
	 * Returns the length in pixels considering the current zoom factor this component needs to display
	 * data right of the last alignment column.
	 * <p>
	 * Overwrite this method if your component displays additional information right of the alignment.
	 * 
	 * @return This default implementation always returns 0. 
	 */
	public double getLengthAfterEnd() {
		return 0;
	}


	/**
	 * Registers a property change listener for all properties.
	 * <p>
	 * This class fires events for the following properties:
	 * <ul>
	 *   <li>{@code list} if {@link #setList(DataList)} is called with a new list</li>
	 *   <li>{@code visible} if {@link #setVisible(boolean)} is called with a new value</li>
	 * </ul>
	 * <p>
	 * Note that inherited classes may add more properties.
	 * 
	 * @param listener the new listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeListeners.addPropertyChangeListener(listener);
	}


	/**
	 * Registers a property change listener for the specified property.
	 * <p>
	 * This class fires events for the following properties:
	 * <ul>
	 *   <li>{@code list} if {@link #setList(DataList)} is called with a new list</li>
	 *   <li>{@code visible} if {@link #setVisible(boolean)} is called with a new value</li>
	 * </ul>
	 * <p>
	 * Note that inherited classes may add more properties.
	 * 
	 * @param propertyName the name of the property to be informed on (See list above.)
	 * @param listener the new listener
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeListeners.addPropertyChangeListener(propertyName, listener);
	}


	/**
	 * Remove a {@link PropertyChangeListener} from the listener list.
	 * <p>
	 * This removes a {@link PropertyChangeListener} that was registered for all properties. If {@code listener} was added more than once to the same event source, 
	 * it will be notified one less time after being removed. If {@code listener} is {@code null}, or was never added, no exception is thrown and no action is taken. 
	 * 
	 * @param listener the listener to be removed
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeListeners.removePropertyChangeListener(listener);
	}


	/**
	 * Remove a {@link PropertyChangeListener} for a specific property.
	 * <p>
	 * This removes a {@link PropertyChangeListener} that was registered for all properties. If {@code listener} was added more than once to the same event source, 
	 * it will be notified one less time after being removed. If {@code listener} is {@code null}, or was never added, no exception is thrown and no action is taken. 
	 * 
	 * @param propertyName the name of the property that was listened on
	 * @param listener the listener to be removed
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeListeners.removePropertyChangeListener(propertyName, listener);
	}
}
