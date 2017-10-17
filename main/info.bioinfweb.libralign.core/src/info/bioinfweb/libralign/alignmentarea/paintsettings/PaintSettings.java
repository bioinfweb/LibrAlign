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


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.SingleColorTokenPainter;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.TokenPainter;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import java.util.Set;



/**
 * Object that manages properties related to painting the contents of an {@link AlignmentArea}.
 *
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class PaintSettings {
	/** The initial cursor line width. */
	public static final double DEFAULT_CURSOR_LINE_WIDTH = 2;


	private AlignmentArea owner;
	private TokenPainterList tokenPainterList = new TokenPainterList(this);
	private Color cursorColor = Color.BLACK;
	private double cursorLineWidth = DEFAULT_CURSOR_LINE_WIDTH;
	private Color selectionColor = SystemColor.textHighlight;
	private double zoomX = 1;
	private double zoomY = 1;
	private boolean changeZoomXOnMouseWheel = true;
	private boolean changeZoomYOnMouseWheel = true;
	// If new properties are added here, they also need to be added in PaintSettingsSynchronizer.addAllProperties() and possibly AlignmentArea.PAINT_SETTINGS_LISTERNER.propertyChange().
	private Set<PaintSettingsListener> listeners = new HashSet<PaintSettingsListener>();


	/**
	 * Creates a new instance of this class using default values for the properties.
	 *
	 * @param owner the alignment area using this instance
	 */
	public PaintSettings(AlignmentArea owner) {
		super();
		this.owner = owner;
	}


	/**
	 * Returns the alignment area using this instance.
	 *
	 * @return the associated alignment area
	 */
	public AlignmentArea getOwner() {
		return owner;
	}


	/**
	 * Returns the list of token painters to be used for the output of the data from the alignment model.
	 * If an concatenated alignment model is used, this list holds one painter for each part (column range),
	 * otherwise it will only contain one.
	 * <p>
	 * The size and the order of the list are updated automatically depending on alignment model changes (events).
	 *
	 * @return the token painter list
	 */
	public TokenPainterList getTokenPainterList() {
		return tokenPainterList;
	}


	public Color getCursorColor() {
		return cursorColor;
	}


	/**
	 * Sets a new color to paint the alignment cursor.
	 *
	 * @param cursorColor the new cursor color
	 * @throws NullPointerException if {@code null} is specified
	 */
	public void setCursorColor(Color cursorColor) {
		if (cursorColor == null) {
			throw new NullPointerException("The cursor color connot be null.");
		}
		else if (!this.cursorColor.equals(cursorColor)) {
			Color oldValue = this.cursorColor;
			this.cursorColor = cursorColor;
			firePropertyChanged("cursorColor", oldValue, cursorColor);
		}
	}


	public Color getSelectionColor() {
		return selectionColor;
	}


	/**
	 * Sets a new color of the selection in the owning alignment area.
	 *
	 * @param selectionColor the new selection color
	 * @throws NullPointerException if {@code null} is specified
	 */
	public void setSelectionColor(Color selectionColor) {
		if (selectionColor == null) {
			throw new NullPointerException("The selection color connot be null.");
		}
		else if (!this.selectionColor.equals(selectionColor)) {
			Color oldValue = this.selectionColor;
			this.selectionColor = selectionColor;
			firePropertyChanged("selectionColor", oldValue, selectionColor);
		}
	}


	public double getCursorLineWidth() {
		return cursorLineWidth;
	}


	public void setCursorLineWidth(double cursorLineWidth) {
		if (this.cursorLineWidth != cursorLineWidth) {
			double oldValue = this.cursorLineWidth;
			this.cursorLineWidth = cursorLineWidth;
			firePropertyChanged("cursorLineWidth", oldValue, cursorLineWidth);
		}
	}


	/**
	 * Returns the vertical zoom factor in an alignment area, that e.g. determines the token width.
	 *
	 * @return the zoom factor where {@code 1.0} indicates 100 % zoom (original size)
	 */
	public double getZoomX() {
		return zoomX;
	}


	/**
	 * Returns the horizontal zoom factor in an alignment area, that e.g. determines the token width.
	 *
	 * @return the zoom factor where {@code 1.0} indicates 100 % zoom (original size)
	 */
	public double getZoomY() {
		return zoomY;
	}


	/**
	 * Allows to specify a new horizontal zoom factor.
	 * <p>
	 * Note that {@link #setZoom(double, double)} should always be preferred (when both properties
	 * are to be set) over calling both single parameter setters in sequence to avoid unnecessary
	 * repaint and resize operations.
	 *
	 * @param zoomX the new horizontal zoom factor
	 * @throws IllegalArgumentException if a zoom factor <= 0 is specified
	 * @see #setZoom(double, double)
	 * @see #setChangeZoomXOnMouseWheel(boolean)
	 */
	public void setZoomX(double zoomX) {
		setZoom(zoomX, getZoomY());
	}


	/**
	 * Allows to specify a new vertical zoom factor.
	 * <p>
	 * Note that {@link #setZoom(double, double)} should always be preferred (when both properties
	 * are to be set) over calling both single parameter setters in sequence to avoid unnecessary
	 * repaint and resize operations.
	 *
	 * @param zoomY the new vertical zoom factor
	 * @throws IllegalArgumentException if a zoom factor <= 0 is specified
	 * @see #setZoom(double, double)
	 * @see #setChangeZoomYOnMouseWheel(boolean)
	 */
	public void setZoomY(double zoomY) {
		setZoom(getZoomX(), zoomY);
	}


	/**
	 * Allows to specify a new horizontal and vertical zoom factor in a single operation. Only one event
	 * indicating both changes will be fired.
	 *
	 * @param zoomX the new horizontal zoom factor
	 * @param zoomY the new vertical zoom factor
	 * @throws IllegalArgumentException if a zoom factor <= 0 is specified
	 * @see #setChangeZoomXOnMouseWheel(boolean)
	 * @see #setChangeZoomYOnMouseWheel(boolean)
	 */
	public void setZoom(double zoomX, double zoomY) {
		if ((this.zoomX != zoomX) || (this.zoomY != zoomY)) {
			if ((zoomX <= 0) || (zoomY <= 0)) {
				throw new IllegalArgumentException("Zoom factors must always be greater than zero.");
			}

			double oldZoomX = zoomX;
			double oldZoomY = zoomY;

			if (this.zoomX != zoomX) {
				this.zoomX = zoomX;
			}
			if (this.zoomY != zoomY) {
				this.zoomY = zoomY;
			}

			fireZoomChange(oldZoomX, oldZoomY);
		}
	}


	/**
	 * Indicates whether the horizontal zoom factor shall be changed, if the user moves the mouse wheel
	 * while pressing the control button (or meta button on Macs).
	 *
	 * @return {@code true} if the zoom will be changed, {@code false} otherwise
	 */
	public boolean isChangeZoomXOnMouseWheel() {
		return changeZoomXOnMouseWheel;
	}


	public void setChangeZoomXOnMouseWheel(boolean zoomXOnMouseWheel) {
		if (this.changeZoomXOnMouseWheel != zoomXOnMouseWheel) {
			this.changeZoomXOnMouseWheel = zoomXOnMouseWheel;
			firePropertyChanged("zoomXOnMouseWheel", !zoomXOnMouseWheel, zoomXOnMouseWheel);
		}
	}


	/**
	 * Indicates whether the vertical zoom factor shall be changed, if the user moves the mouse wheel
	 * while pressing the control button (or meta button on Macs).
	 *
	 * @return {@code true} if the zoom will be changed, {@code false} otherwise
	 */
	public boolean isChangeZoomYOnMouseWheel() {
		return changeZoomYOnMouseWheel;
	}


	public void setChangeZoomYOnMouseWheel(boolean zoomYOnMouseWheel) {
		if (this.changeZoomYOnMouseWheel != zoomYOnMouseWheel) {
			this.changeZoomYOnMouseWheel = zoomYOnMouseWheel;
			firePropertyChanged("zoomYOnMouseWheel", !zoomYOnMouseWheel, zoomYOnMouseWheel);
		}
	}


	/**
	 * Returns the width of the column with the specified index. If the index is out of bounds (the specified column
	 * does not exist in the alignment model) or no alignment model is defined in the owning alignment area, the token
	 * width of the default token painter is returned.
	 *
	 * @param columnIndex the index of the column to determine the width from
	 * @return the width of column in pixels
	 */
	public double getTokenWidth(int columnIndex) {
		return getTokenPainterList().painterByColumn(columnIndex).getPreferredWidth() * getZoomX();
	}


	/**
	 * Returns the maximal token width in the alignment according to the current zoom factor.
	 * <p>
	 * The preferred with of a respective default painter is considered if a painter for at least one 
	 * alignment model (if model models are present using {@link ConcatenatedAlignmentModel}) is missing 
	 * in the list.
	 *
	 * @return the maximal token width in pixels
	 */
	public double maxTokenWidth() {
		int modelCount = 1;
		if (getOwner().getAlignmentModel() instanceof ConcatenatedAlignmentModel) {
			modelCount = ((ConcatenatedAlignmentModel)getOwner().getAlignmentModel()).getPartModelCount();
		}
		double result = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < modelCount; i++) {
			result = Math.max(result, getTokenPainterList().get(i).getPreferredWidth());
		}
		return result * getZoomX();
	}


	/**
	 * Returns the minimal token width in the alignment according to the current zoom factor.
	 * <p>
	 * The preferred with of a respective default painter is considered if a painter for at least one 
	 * alignment model (if model models are present using {@link ConcatenatedAlignmentModel}) is missing 
	 * in the list.
	 *
	 * @return the minimal token width in pixels
	 */
	public double minTokenWidth() {
		int modelCount = 1;
		if (getOwner().getAlignmentModel() instanceof ConcatenatedAlignmentModel) {
			modelCount = ((ConcatenatedAlignmentModel)getOwner().getAlignmentModel()).getPartModelCount();
		}
		double result = Double.POSITIVE_INFINITY;
		for (int i = 0; i < modelCount; i++) {
			result = Math.min(result, getTokenPainterList().get(i).getPreferredWidth());
		}
		return result * getZoomX();
	}


	/**
	 * Returns the height of tokens displayed in this alignment recognizing the current value of {@link #getZoomY()}.
	 *
	 * @return the height in pixels
	 */
	public double getTokenHeight() {
		int modelCount = 1;
		if (getOwner().getAlignmentModel() instanceof ConcatenatedAlignmentModel) {
			modelCount = ((ConcatenatedAlignmentModel)getOwner().getAlignmentModel()).getPartModelCount();
		}
		double result = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < modelCount; i++) {
			result = Math.max(result, getTokenPainterList().get(i).getPreferredHeight());
		}
		return result * getZoomY();
	}


	/**
	 * Returns a sans serif plain font object with the size according to the token height in the current zoom factor.
	 *
	 * @return the font object
	 */
	public Font getTokenHeightFont() {
		return new Font(Font.SANS_SERIF, Font.PLAIN,
				(int)Math.round(SingleColorTokenPainter.FONT_SIZE_FACTOR * getTokenHeight()));
	}


	/**
	 * Attaches a paint settings listener to this instance.
	 *
	 * @param listener the listener to be attached
	 * @return {@code true} if the new listener was added, {@code false} if that listener was already attached
	 */
	public boolean addListener(PaintSettingsListener listener) {
		return listeners.add(listener);
	}


	/**
	 * Removes a paint settings listener from this instance.
	 *
	 * @param listener the listener to be removed
	 * @return {@code true} if the listener was removed, {@code false} if it was not attached before calling this method
	 */
	public boolean removeListener(PaintSettingsListener listener) {
		return listeners.remove(listener);
	}


	protected void firePropertyChanged(String propertyName, Object oldValue, Object newValue) {
		PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
		for (PaintSettingsListener listener : listeners.toArray(new PaintSettingsListener[listeners.size()])) {  // Copying the list is necessary to allow listeners to remove themselves from the list without a ConcurrentModificationException being thrown.
			listener.propertyChange(event);
		}
	}


	protected void fireZoomChange(double oldZoomX, double oldZoomY) {
		ZoomChangeEvent event = new ZoomChangeEvent(this, oldZoomX, getZoomX(), oldZoomY, getZoomY());
		for (PaintSettingsListener listener : listeners.toArray(new PaintSettingsListener[listeners.size()])) {  // Copying the list is necessary to allow listeners to remove themselves from the list without a ConcurrentModificationException being thrown.
			listener.zoomChange(event);
		}
	}


	protected void fireTokenPainterReplaced(TokenPainter previousPainter, TokenPainter newPainter, int index) {
		TokenPainterReplacedEvent event = new TokenPainterReplacedEvent(this, previousPainter, newPainter, index);
		for (PaintSettingsListener listener : listeners.toArray(new PaintSettingsListener[listeners.size()])) {  // Copying the list is necessary to allow listeners to remove themselves from the list without a ConcurrentModificationException being thrown.
			listener.tokenPainterReplaced(event);
		}
	}


	protected void fireTokenPainterListChange() {
		PaintSettingsEvent event = new PaintSettingsEvent(this);
		for (PaintSettingsListener listener : listeners.toArray(new PaintSettingsListener[listeners.size()])) {  // Copying the list is necessary to allow listeners to remove themselves from the list without a ConcurrentModificationException being thrown.
			listener.tokenPainterListChange(event);
		}
	}
}
