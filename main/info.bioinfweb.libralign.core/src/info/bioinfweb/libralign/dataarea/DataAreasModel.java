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


import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelArea;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



/**
 * Manages the data areas attached to an {@link AlignmentArea}.
 *
 * @author Ben St&ouml;ver
 */
public class DataAreasModel {
	private final AlignmentArea owner;
  private final DataAreaList topAreas = new DataAreaList(this, DataAreaListType.TOP);
  private final DataAreaList bottomAreas = new DataAreaList(this, DataAreaListType.BOTTOM);
  private final Map<String, DataAreaList> sequenceAreaLists = new HashMap<String, DataAreaList>();
  private final List<DataAreasModelListener> listeners = new ArrayList<DataAreasModelListener>(8);
  private boolean visibilityUpdateInProgress = false;
  private final DataAreaSequenceChangeListener sequenceChangeListener = new DataAreaSequenceChangeListener(this);
  private double localMaxLengthBeforeStart = AlignmentLabelArea.RECALCULATE_VALUE;
  private double localMaxLengthAfterEnd = AlignmentLabelArea.RECALCULATE_VALUE;


	/**
	 * Creates a new instance of this class.
	 *
	 * @param owner - the alignment content area that will be using this instance
	 */
  public DataAreasModel(AlignmentArea owner) {
		super();
		this.owner = owner;
	}


	/**
	 * Returns the alignment content area that uses this instance.
	 */
	public AlignmentArea getOwner() {
		return owner;
	}


	/**
	 * Returns a list of data areas to be displayed on the top of the alignment.
	 *
	 * @return a modifiable list
	 */
	public DataAreaList getTopAreas() {
		return topAreas;
	}


	/**
	 * Returns a list of data areas to be displayed underneath the alignment.
	 *
	 * @return a modifiable list
	 */
	public DataAreaList getBottomAreas() {
		return bottomAreas;
	}


	/**
	 * Returns a list of data areas to be displayed underneath the specified sequence.
	 *
	 * @param sequenceName - the unique identifier of the sequence carrying the data areas
	 *        in the returned list
	 * @return a modifiable list
	 */
	public DataAreaList getSequenceAreas(String sequenceID) {
		DataAreaList result = sequenceAreaLists.get(sequenceID);
		if (result == null) {
			result = new DataAreaList(this, sequenceID);
			sequenceAreaLists.put(sequenceID, result);
		}
		return result;
	}
	
	
	/**
	 * Removes the list of data areas attached to the specified sequence from this model.
	 *
	 * @param sequenceName - the name of the sequence the data areas to be removed are attached to
	 */
	public void removeSequence(String sequenceName) {
		sequenceAreaLists.remove(sequenceName);
	}


	/**
	 * Fades all data areas associated with any sequence in or out.
	 *
	 * @param visible - Specify {@code true} here, if you want the elements to be displayed, {@code false} otherwise.
	 * @see DataAreaList#setAllVisible(boolean)
	 */
	public void setSequenceDataAreasVisible(boolean visible) {
		boolean informListeners = !isVisibilityUpdateInProgress();
		if (informListeners) {
			startVisibilityUpdate();
		}

		List<DataArea> affectedAreas = new ArrayList<DataArea>();
		Iterator<String> idIterator = sequenceAreaLists.keySet().iterator();
		while (idIterator.hasNext()) {
			affectedAreas.addAll(getSequenceAreas(idIterator.next()).setAllVisible(visible));
		}

		if (informListeners) {
			finishVisibilityUpdate(false, affectedAreas);
		}
	}


	/**
	 * Calculates the sum of the heights of all visible data areas contained in this model, that are
	 * attached to any sequence.
	 *
	 * @return a double value greater of equal to zero
	 */
	public int getVisibleSequenceAreaHeight() {
		int result = 0;
		Iterator<String> iterator = sequenceAreaLists.keySet().iterator();
		while (iterator.hasNext()) {
			result += getSequenceAreas(iterator.next()).getVisibleHeight();
		}
		return result;
	}


	/**
	 * Calculates the sum of the heights of all visible data areas contained in this model (above,
	 * underneath and attached to any sequence).
	 *
	 * @return a double value greater of equal to zero
	 */
	public int getVisibleAreaHeight() {
		return getTopAreas().getVisibleHeight() + getVisibleSequenceAreaHeight() + getBottomAreas().getVisibleHeight();
	}


	/**
	 * Returns maximum space left of the alignment start that is needed by any currently visible data area in
	 * any list contained in this model. Note the space that is actually present will be determined using
	 * {@link #getGlobalMaxLengthBeforeStart()}.
	 *
	 * @return an integer >= 0
	 */
	public double getLocalMaxLengthBeforeStart() {
		if (localMaxLengthBeforeStart == AlignmentLabelArea.RECALCULATE_VALUE) {
			localMaxLengthBeforeStart = Math.max((int)Math2.roundUp(getOwner().getPaintSettings().getCursorLineWidth() / 2),
					Math.max(getTopAreas().getMaxLengthBeforeStart(), getBottomAreas().getMaxLengthBeforeStart()));
			Iterator<String> iterator = sequenceAreaLists.keySet().iterator();
			while (iterator.hasNext()) {
				localMaxLengthBeforeStart = Math.max(localMaxLengthBeforeStart, getSequenceAreas(iterator.next()).getMaxLengthBeforeStart());
			}
		}
		return localMaxLengthBeforeStart;
	}


	/**
	 * Flags the properties {@link #getLocalMaxLengthBeforeStart()} and {@link #getLocalMaxLengthAfterEnd()} to
	 * be recalculated when they are accessed the next time.
	 * <p>
	 * LibrAlign does not recalculate these values on every call for performance reasons. Usually application code
	 * will not have to call this method directly.
	 */
	public void setLocalMaxLengthBeforeAfterRecalculate() {
		localMaxLengthBeforeStart = AlignmentLabelArea.RECALCULATE_VALUE;
    localMaxLengthAfterEnd = AlignmentLabelArea.RECALCULATE_VALUE;
	}


	/**
	 * Returns maximum space left of the alignment calculated over all alignment areas in the parent
	 * {@link MultipleAlignmentsContainer} of the alignment area using this model. If that alignment area
	 * is not contained in such a container the return value is equivalent to {@link #getLocalMaxLengthBeforeStart()}.
	 *
	 * @return an integer >= 0
	 */
	public double getGlobalMaxLengthBeforeStart() {
		AlignmentArea alignmentArea = getOwner();
		double result = 0;
		if (alignmentArea.hasContainer()) {
			for (AlignmentArea containerAlignmentArea : alignmentArea.getContainer().getAlignmentAreas()) {
				result = Math.max(result, containerAlignmentArea.getDataAreas().getLocalMaxLengthBeforeStart());
			}
		}
		else {
			result = getLocalMaxLengthBeforeStart();
		}
		return result;
	}


  /**
   * Returns maximum space right of the alignment end that is needed by any currently visible data area in
   * any list contained in this model.
   *
   * @return an integer >= 0
   */
  public double getLocalMaxLengthAfterEnd() {
    if (localMaxLengthAfterEnd == AlignmentLabelArea.RECALCULATE_VALUE) {
      localMaxLengthAfterEnd = Math.max((int)Math2.roundUp(getOwner().getPaintSettings().getCursorLineWidth() / 2),
      		Math.max(getTopAreas().getMaxLengthAfterEnd(), getBottomAreas().getMaxLengthAfterEnd()));
      Iterator<String> iterator = sequenceAreaLists.keySet().iterator();
      while (iterator.hasNext()) {
        localMaxLengthAfterEnd = Math.max(localMaxLengthAfterEnd, getSequenceAreas(iterator.next()).getMaxLengthAfterEnd());
      }
    }
    return localMaxLengthAfterEnd;
  }


  /**
	 * Adds a lister to this object that will be informed about future insertions or deletions
	 * of data areas.
	 *
	 * @param listener - the listener object to be notified in the future
	 * @return {@code true} (as specified by {@link Collection#add(Object)})
	 */
	public boolean addListener(DataAreasModelListener listener) {
		return listeners.add(listener);
	}


	/**
	 * Removes the specified listener from this objects list.
	 *
	 * @param listener - the listener to be removed
	 * @return {@code true} if this list contained the specified element
	 */
	public boolean removeListener(DataAreasModelListener listener) {
		return listeners.remove(listener);
	}


	/**
	 * Returns the change listener that forwards events from the associated {@link AlignmentModel} to
	 * all data areas contained in this model.
	 */
	public DataAreaSequenceChangeListener getSequenceDataChangeListener() {
		return sequenceChangeListener;
	}


	/**
	 * Informs all listeners that a data area has been added or removed.
	 */
	protected void fireInsertedRemoved(ListChangeType type, Collection<? extends DataArea> affectedElement) {
		DataAreaChangeEvent e = new DataAreaChangeEvent(this, true, type, affectedElement);
        for (DataAreasModelListener listener : listeners.toArray(new DataAreasModelListener[listeners.size()])) {  // Copying the list is necessary to allow listeners to remove themselves from the list without a ConcurrentModificationException being thrown.
			listener.dataAreaInsertedRemoved(e);
		}
	}


	/**
	 * Informs all listeners that a data area has been added or removed.
	 */
	protected void fireInsertedRemoved(ListChangeType type, DataArea affectedElements) {
		DataAreaChangeEvent e = new DataAreaChangeEvent(this, true, type, affectedElements);
        for (DataAreasModelListener listener : listeners.toArray(new DataAreasModelListener[listeners.size()])) {  // Copying the list is necessary to allow listeners to remove themselves from the list without a ConcurrentModificationException being thrown.
			listener.dataAreaInsertedRemoved(e);
		}
	}


	/**
	 * After calling this method {@link #fireVisibilityChanged(DataAreaList, Collection)} will have no effect
	 * until {@link #finishVisibilityUpdate(DataAreaList, Collection)} is called.
	 * <p>
	 * This method can be called before changing the visibility of many data areas in one step.
	 * {@link #finishVisibilityUpdate(DataAreaList, Collection)} can than be used to inform the listeners
	 * about the changed with one single event.
	 */
	protected void startVisibilityUpdate() {
		visibilityUpdateInProgress = true;
	}


	/**
	 * Returns {@code true} if informing listeners about visibility changes of data areas currently
	 * disabled.
	 *
	 * @return {@code true} if {@link #fireVisibilityChanged(DataAreaList, Collection)} would currently have
	 *          no effect, {@code false} otherwise
	 */
	protected boolean isVisibilityUpdateInProgress() {
		return visibilityUpdateInProgress;
	}


	/**
	 * Enables {@link #fireVisibilityChanged(DataAreaList, Collection)} again and makes a call of
	 * this method to inform the listeners about previous changes.
	 *
	 * @param e - the event describing the changes since the previous call of
	 *            {@link #startVisibilityUpdate()} that will be send to all listeners
	 */
	protected void finishVisibilityUpdate(boolean eventsFromSingleList, Collection<? extends DataArea> affectedElements) {
		visibilityUpdateInProgress = false;
		fireVisibilityChanged(eventsFromSingleList, affectedElements);
	}


	/**
	 * Informs all listeners that the visibility of a set of data areas has changed.
	 * <p>
	 * This method will have no effect if {@link #isVisibilityUpdateInProgress()} return {@code true}.
	 *
	 * @param eventsFromSingleList - Specify {@code true} here if all elements in {@code affectedElements}
	 *        are contained in the same {@link DataAreaList}.
	 * @param affectedElements - a list of elements that have been changed
	 */
	protected void fireVisibilityChanged(boolean eventsFromSingleList, Collection<? extends DataArea> affectedElements) {
		DataAreaChangeEvent e = new DataAreaChangeEvent(this, eventsFromSingleList, null, affectedElements);
        for (DataAreasModelListener listener : listeners.toArray(new DataAreasModelListener[listeners.size()])) {  // Copying the list is necessary to allow listeners to remove themselves from the list without a ConcurrentModificationException being thrown.
			listener.dataAreaVisibilityChanged(e);
		}
	}


	/**
	 * Informs all listeners that the visibility of a data area has changed. (Convenience method that
	 * calls {@link #fireVisibilityChanged(DataAreaList, Collection)} internally.)
	 * <p>
	 * This method will have no effect if {@link #isVisibilityUpdateInProgress()} return {@code true}.
	 *
	 * @param eventsFromSingleList - Specify {@code true} here if all elements in {@code affectedElements}
	 *        are contained in the same {@link DataAreaList}.
	 * @param affectedElements - a list of elements that have been changed
	 */
	protected void fireVisibilityChanged(boolean eventsFromSingleList, DataArea affectedElement) {
		Collection<DataArea> affectedElements = new ArrayList<DataArea>(1);
		affectedElements.add(affectedElement);
		fireVisibilityChanged(eventsFromSingleList, affectedElements);
	}
}
