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


import info.bioinfweb.commons.Math2;
import info.bioinfweb.libralign.pherogram.provider.PherogramProvider;

import java.util.HashSet;
import java.util.Set;



/**
 * The model used by all views displaying pherograms.
 *
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class PherogramComponentModel {
	private PherogramProvider pherogramProvider;
	private int leftCutPosition = 0;
	private int rightCutPosition = 0;
	protected Set<PherogramModelListener> listeners = new HashSet<PherogramModelListener>();


	/**
	 * Creates a new instance of this class without any cut off pherogram ends.
	 *
	 * @param pherogramProvider the pherogram provider
	 * @throws NullPointerException if {@code null} is specified for {@code pherogramProvider}
	 */
	public PherogramComponentModel(PherogramProvider pherogramProvider) {
		this(pherogramProvider, 0, pherogramProvider.getSequenceLength());
	}


	/**
	 * Creates a copy from the specified instance. (The underlying pherogram provider instance is shared
	 * and not deep copied.)
	 *
	 * @param model the instance to be copied
	 * @throws NullPointerException if {@code null} is specified for {@code model}
	 */
	public PherogramComponentModel(PherogramComponentModel model) {
		this(model.getPherogramProvider(), model.getLeftCutPosition(), model.getRightCutPosition());
	}


	/**
	 * Creates a new instance of this class.
	 *
	 * @param pherogramProvider the pherogram provider
	 * @param leftCutPosition the index in the base call sequence where the pherogram shall be cut off at the left end
	 * @param rightCutPosition the index in the base call sequence where the pherogram shall be cut off at the right end
	 * @throws NullPointerException if {@code null} is specified for {@code pherogramProvider}
	 * @throws IndexOutOfBoundsException if {@code leftCutPosition} is not between 0 and and {@code rightCutPosition}
	 *         or {@code rightCutPosition} if not between {@code leftCutPosition} and the length of the base call sequence
	 *         defined by {@code pherogramProvider}
	 */
	public PherogramComponentModel(PherogramProvider pherogramProvider,	int leftCutPosition, int rightCutPosition) {
		super();

		if (pherogramProvider != null) {
			this.pherogramProvider = pherogramProvider;
		}
		else {
			throw new NullPointerException("The pherogram provider must not be null.");
		}

		if (Math2.isBetween(leftCutPosition, 0, rightCutPosition)) {
			this.leftCutPosition = leftCutPosition;
		}
		else {
			throw new IndexOutOfBoundsException("The left cut position (" + leftCutPosition +
					") must lie between 0 and the specified right cut position (" + rightCutPosition + ").");
		}

		if (Math2.isBetween(rightCutPosition, leftCutPosition, pherogramProvider.getSequenceLength())) {
			this.rightCutPosition = rightCutPosition;
		}
		else {
			throw new IndexOutOfBoundsException("The right cut position (" + rightCutPosition +
					") must lie between the specified left cut position (" + leftCutPosition + ") and the length of the base call sequence (" +
					pherogramProvider.getSequenceLength() + ").");
		}
	}


	public PherogramProvider getPherogramProvider() {
		return pherogramProvider;
	}


	/**
	 * Reverse complements the pherogram and swaps the left and right cut positions accordingly.
	 */
	public void reverseComplement() {
		PherogramProvider oldProvider = pherogramProvider;
		pherogramProvider = pherogramProvider.reverseComplement();

		int oldLeftCutPosition = leftCutPosition;
		PherogramAlignmentRelation oldLeftEditableIndex = getAlignmentRelation(oldLeftCutPosition);  // Must be called before the position is changed.
		int oldRightCutPosition = rightCutPosition;
		PherogramAlignmentRelation oldRightEditableIndex = getAlignmentRelation(oldRightCutPosition);  // Must be called before the position is changed.

		leftCutPosition = pherogramProvider.getSequenceLength() - rightCutPosition;
		rightCutPosition = pherogramProvider.getSequenceLength() - oldLeftCutPosition;

		firePherogramProviderChanged(oldProvider, true,
				(leftCutPosition != oldLeftCutPosition) || (rightCutPosition != oldRightCutPosition));
		if (leftCutPosition != oldLeftCutPosition) {
			fireLeftCutPositionChanged(oldLeftCutPosition, oldLeftEditableIndex, rightCutPosition != oldRightCutPosition);
		}
		if (rightCutPosition != oldRightCutPosition) {
			fireRightCutPositionChanged(oldRightCutPosition, oldRightEditableIndex, false);
		}
	}


	/**
	 * Sets a new pherogram provider and fires according events. The left and right cut positions
	 * are set to the ends of the base call sequences of the new provider.
	 *
	 * @param pherogramProvider the new provider for trace curves and the base call sequence
	 * @throws NullPointerException if {@code null} is specified for {@code pherogramProvider}
	 */
	public void setProvider(PherogramProvider pherogramProvider) {
		if (pherogramProvider != null) {
			PherogramProvider oldProvider = this.pherogramProvider;
			this.pherogramProvider = pherogramProvider;

			firePherogramProviderChanged(oldProvider, false,
					(getLeftCutPosition() != 0) || (getRightCutPosition() != pherogramProvider.getSequenceLength()));
			setLeftCutPosition(0);
			setRightCutPosition(pherogramProvider.getSequenceLength());
		}
		else {
			throw new NullPointerException("The pherogram provider must not be null.");
		}
	}


	/**
	 * Returns the first base call index of the pherogram which has not been cut off.
	 *
	 * @return a base call index > 0
	 */
	public int getLeftCutPosition() {
		return leftCutPosition;
	}


	/**
	 * Hook method to be implemented in inherited classes and called from within {@link #setLeftCutPosition(int)}.
	 *
	 * @param oldBaseCallIndex
	 * @param newBaseCallIndex
	 */
	protected void onSetLeftCutPosition(int oldBaseCallIndex, int newBaseCallIndex) {}


	/**
	 * Sets a new cut position for the left border of the visible part of the pherogram.
	 * <p>
	 * If this method is called on an instance of {@link PherogramAreaModel} the property
	 * {@link PherogramAreaModel#getFirstSeqPos()} is moved accordingly, so that the pherogram does not move
	 * relative to the sequence. If this shall be avoided {@link PherogramAreaModel#setFirstSeqLeftCutPos(int, int)}
	 * should be used instead.
	 *
	 * @param baseCallIndex - the index of the first nucleotide in the base call sequence that shall be visible
	 * @throws IndexOutOfBoundsException if {@code baseCallIndex} is not between 0 and and {@link #getRightCutPosition()}
	 * @see PherogramAreaModel#setFirstSeqLeftCutPos(int, int)
	 */
	public void setLeftCutPosition(int baseCallIndex) {
		if (leftCutPosition != baseCallIndex) {
			if (Math2.isBetween(baseCallIndex, 0, getRightCutPosition())) {
				int oldValue = leftCutPosition;
				PherogramAlignmentRelation oldEditableIndex = getAlignmentRelation(oldValue);  // Must be called before the position is changed.
				leftCutPosition = baseCallIndex;

				onSetLeftCutPosition(oldValue, leftCutPosition);
				if (leftCutPosition > getRightCutPosition()) {
					setRightCutPosition(leftCutPosition);  // Calls PherogramArea.deleteCutOffDistortions() if this instance is of type PherogramArea.
				}

				fireLeftCutPositionChanged(oldValue, oldEditableIndex, false);
			}
			else {
				throw new IndexOutOfBoundsException("The left cut position (" + baseCallIndex +
						") must lie between 0 and the specified right cut position (" + getRightCutPosition() + ").");
			}
		}
	}


	/**
	 * Returns the first base call index of the pherogram that has been cut off (so that the length of the visible
	 * area of the pherogram can be calculated as {@code getRightCutPosition() - }{@link #getLeftCutPosition()}).
	 *
	 * @return a base call index >= {@link #getLeftCutPosition()}
	 */
	public int getRightCutPosition() {
		return rightCutPosition;
	}


	/**
	 * Hook method to be implemented in inherited classes and called from within {@link #setRightCutPosition(int)}.
	 *
	 * @param oldBaseCallIndex
	 * @param newBaseCallIndex
	 */
	protected void onSetRightCutPosition(int oldBaseCallIndex, int newBaseCallIndex) {}


	/**
	 * Sets a new cut position for the right border of the visible part of the pherogram.
	 *
	 * @param baseCallIndex - the index of the first nucleotide after the visible part in the base call sequence
	 * @throws IndexOutOfBoundsException if {@code baseCallIndex} if not between {@link #getLeftCutPosition()} and the length
	 *         of the base call sequence defined by {@link #getPherogramProvider()}
	 */
	public void setRightCutPosition(int baseCallIndex) {
		if (rightCutPosition != baseCallIndex) {
			if (Math2.isBetween(baseCallIndex, leftCutPosition, pherogramProvider.getSequenceLength())) {
				int oldValue = rightCutPosition;
				PherogramAlignmentRelation oldEditableIndex = getAlignmentRelation(oldValue);  // Must be called before the position is changed.
				rightCutPosition = baseCallIndex;

				onSetRightCutPosition(oldValue, rightCutPosition);
				if (getLeftCutPosition() > rightCutPosition) {
					setLeftCutPosition(rightCutPosition);  // Calls PherogramArea.deleteCutOffDistortions() if this instance is of type PherogramArea.
				}

				fireRightCutPositionChanged(oldValue, oldEditableIndex, false);
			}
			else {
				throw new IndexOutOfBoundsException("The right cut position (" + baseCallIndex +
						") must lie between the specified left cut position (" + getLeftCutPosition() +
						") and the length of the base call sequence (" + pherogramProvider.getSequenceLength() + ").");
			}
		}
	}


	public void addListener(PherogramModelListener listener) {
		listeners.add(listener);
	}


	public void removeListener(PherogramModelListener listener) {
		listeners.remove(listener);
	}


	protected PherogramAlignmentRelation getAlignmentRelation(int baseCallIndex) {
		return null;
	}


	protected void fireLeftCutPositionChanged(int oldValue, PherogramAlignmentRelation oldEditableIndex,
			boolean moreEventsUpcoming) {

		PherogramCutPositionChangeEvent event = new PherogramCutPositionChangeEvent(this, moreEventsUpcoming,
				oldValue, leftCutPosition, oldEditableIndex, getAlignmentRelation(leftCutPosition));
		for (PherogramModelListener listener : listeners.toArray(new PherogramModelListener[listeners.size()])) {  // Copying the list is necessary to allow listeners to remove themselves from the list without a ConcurrentModificationException being thrown.
			listener.leftCutPositionChange(event);
		}
	}


	protected void fireRightCutPositionChanged(int oldValue, PherogramAlignmentRelation oldEditableIndex,
			boolean moreEventsUpcoming) {

		PherogramCutPositionChangeEvent event = new PherogramCutPositionChangeEvent(this, moreEventsUpcoming,
				oldValue, rightCutPosition,	oldEditableIndex, getAlignmentRelation(rightCutPosition));
        for (PherogramModelListener listener : listeners.toArray(new PherogramModelListener[listeners.size()])) {  // Copying the list is necessary to allow listeners to remove themselves from the list without a ConcurrentModificationException being thrown.
			listener.rightCutPositionChange(event);
		}
	}


	protected void firePherogramProviderChanged(PherogramProvider oldProvider, boolean reverseComplemented,
			boolean moreEventsUpcoming) {

		PherogramProviderChangeEvent event = new PherogramProviderChangeEvent(this, moreEventsUpcoming,
				oldProvider, pherogramProvider, reverseComplemented);
        for (PherogramModelListener listener : listeners.toArray(new PherogramModelListener[listeners.size()])) {  // Copying the list is necessary to allow listeners to remove themselves from the list without a ConcurrentModificationException being thrown.
			listener.pherogramProviderChange(event);
		}
	}
}
