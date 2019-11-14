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
package info.bioinfweb.libralign.pherogram;


import info.bioinfweb.commons.beans.PropertyChangeEventForwarder;
import info.bioinfweb.commons.graphics.GraphicsUtils;
import info.bioinfweb.commons.graphics.ZoomableFont;
import info.bioinfweb.libralign.alignmentarea.paintsettings.PaintSettings;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.NucleotideTokenPainter;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;
import info.bioinfweb.libralign.pherogram.view.PherogramHeadingView;
import info.bioinfweb.libralign.pherogram.view.PherogramTraceCurveView;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.TreeMap;



/**
 * Class that stores colors and fonts and settings used to display a pherogram from Sanger sequencing.
 * It is used by implementations of {@link PherogramComponent} and {@link PherogramHeadingView} to format
 * their output.
 * <p>
 * Changes to all properties can be tracked using {@link PropertyChangeListener}s during runtime. Note that
 * separate listeners could be added to  the nested {@link ZoomableFont} objects, but their events are also
 * forwarded to listeners registered with this objects. The forwarded events then have the font property name
 * as a prefix (e.g. {@code "baseCallFont.name"} if the {@code "name"} property of {@link #getBaseCallFont()}
 * was changed). 
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
public class PherogramFormats {
	/**
	 * Enumeration that determines the way quality scores are displays in an 
	 * 
	 * @author Ben St&ouml;ver
	 */
	public static enum QualityOutputType {
		/** Does not display any quality values. */
		NONE,
		
		/** Displays only the quality value for the nucleotide that has been called. */
		MAXIMUM, 
		
		/** Displays quality values for all four nucleotides. */
		ALL; 
	}
	
	
	/** Used to calculate the default background color for the cut off areas. */
	public static final float CUT_COLOR_FACTOR = 1.5f;

	/** Used to calculate the default base call line color. */
	public static final float BASE_CALL_LINE_COLOR_FACTOR = 1.1f;
	
	/** Used to determine the line spacing around base calls, indices or probabilities. */
	public static final double FONT_HEIGHT_FACTOR = 1.2;
	
	private static final double VIEW_FONT_HEIGHT_FACTOR = 0.8;
	
	private Color backgroundColor;
	private Color cutBackgroundColor;
	private Color headingBackgroundColor;
	private Color baseCallLineColor;
	private Color cutBaseCallLineColor;
	private Map<String, Color> nucleotideColorMap;
	private boolean showBaseCallLines = true;
	private ZoomableFont baseCallFont;
	private ZoomableFont indexFont;
	private ZoomableFont annotationFont;
	private QualityOutputType qualityOutputType = QualityOutputType.MAXIMUM;
	private boolean showProbabilityValues = false;
	private Color probabilityColor;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private PropertyChangeListener PROPERTY_CHANGE_FORWARDER = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			propertyChangeSupport.firePropertyChange(event);
		}
	};
	
	
	private ZoomableFont createFont(String prefix, int style, double size) {
		ZoomableFont result = new ZoomableFont(Font.DIALOG, style, size);
		result.addPropertyChangeListener(new PropertyChangeEventForwarder(prefix, PROPERTY_CHANGE_FORWARDER));
		return result;
	}
	
	
	/**
	 * Creates a new instance of this class with default values.
	 */
	public PherogramFormats() {
		super();
		
		backgroundColor = Color.LIGHT_GRAY.brighter();
		headingBackgroundColor = backgroundColor;
		cutBackgroundColor = GraphicsUtils.moveColorToCenter(backgroundColor, CUT_COLOR_FACTOR);
		baseCallLineColor =  GraphicsUtils.moveColorToCenter(backgroundColor, BASE_CALL_LINE_COLOR_FACTOR);
		cutBaseCallLineColor =  GraphicsUtils.moveColorToCenter(cutBackgroundColor, BASE_CALL_LINE_COLOR_FACTOR);
		probabilityColor = GraphicsUtils.getContrastColor(Color.BLACK, headingBackgroundColor, 30);
		
		nucleotideColorMap = new TreeMap<String, Color>();
		NucleotideTokenPainter.putNucleotideColors(nucleotideColorMap);

		baseCallFont = createFont("baseCallFont.", Font.BOLD, 15);
		indexFont = createFont("indexFont.", Font.PLAIN, 10);
		annotationFont = createFont("annotationFont.", Font.PLAIN, 10);
	}


	/**
	 * Returns the background color for displaying the central part of the pherogram (the positions that have not 
	 * been cut off).
	 * 
	 * @return the current background color for the center positions
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}


	/**
	 * Sets a new background color for displaying the central part of the pherogram (the positions that have not 
	 * been cut off). 
	 * 
	 * @param backgroundColor - the new background color for the center positions
	 */
	public void setBackgroundColor(Color backgroundColor) {
		final Color oldColor = this.backgroundColor;
		this.backgroundColor = backgroundColor;
		propertyChangeSupport.firePropertyChange("backgroundColor", oldColor, backgroundColor);
	}


	/**
	 * Returns the background color for displaying the parts of the pherogram on the left and right end that have
	 * been cut off by the user.
	 * 
	 * @return the current background color for the cut off positions
	 */
	public Color getCutBackgroundColor() {
		return cutBackgroundColor;
	}


	/**
	 * Sets a new background color for displaying the parts of the pherogram on the left and right end that have
	 * been cut off by the user. 
	 * 
	 * @param backgroundColor - the new background color for the cut off positions
	 */
	public void setCutBackgroundColor(Color cutBackgroundColor) {
		final Color oldColor = this.cutBackgroundColor;
		this.cutBackgroundColor = cutBackgroundColor;
		propertyChangeSupport.firePropertyChange("cutBackgroundColor", oldColor, cutBackgroundColor);
	}


	/**
	 * Returns the background color for the heading of the pherogram (displaying the base call sequence and nucleotide
	 * indices).
	 * 
	 * @return the current background color for the header information area
	 */
	public Color getHeadingBackgroundColor() {
		return headingBackgroundColor;
	}


	/**
	 * Sets a new background color for the heading of the pherogram (displaying the base call sequence and nucleotide
	 * indices).
	 * 
	 * @param headingBackgroundColor - the new background color for the heading information area
	 */
	public void setHeadingBackgroundColor(Color headingBackgroundColor) {
		final Color oldColor = this.headingBackgroundColor;
		this.headingBackgroundColor = headingBackgroundColor;
		propertyChangeSupport.firePropertyChange("headingBackgroundColor", oldColor, headingBackgroundColor);
	}


	/**
	 * Returns the color used to paint the vertical lines at each base call position in the background of the trace 
	 * curve output in the center area that has not been cut off by the user.
	 * 
	 * @return the current base call line color
	 */
	public Color getBaseCallLineColor() {
		return baseCallLineColor;
	}


	/**
	 * Sets a new color used to paint the vertical lines at each base call position in the background of the trace 
	 * curve output in the center area that has not been cut off by the user.
	 * 
	 * @param baseCallLineColor - the new base call line color for the center
	 */
	public void setBaseCallLineColor(Color baseCallLineColor) {
		final Color oldColor = this.baseCallLineColor;
		this.baseCallLineColor = baseCallLineColor;
		propertyChangeSupport.firePropertyChange("baseCallLineColor", oldColor, baseCallLineColor);
	}


	/**
	 * Returns the color used to paint the vertical lines at each base call position in the background of the trace 
	 * curve output at the left and right areas that have been cut off by the user.
	 * 
	 * @return the current base call line color for the cut off areas
	 */
	public Color getCutBaseCallLineColor() {
		return cutBaseCallLineColor;
	}


	/**
	 * Sets a new color used to paint the vertical lines at each base call position in the background of the trace 
	 * curve output at the left and right areas that have been cut off by the user.
	 * 
	 * @param baseCallLineColor - the new base call line color for the cut off areas
	 */
	public void setCutBaseCallLineColor(Color cutBaseCallLineColor) {
		final Color oldColor = this.cutBaseCallLineColor;
		this.cutBaseCallLineColor = cutBaseCallLineColor;
		propertyChangeSupport.firePropertyChange("cutBaseCallLineColor", oldColor, cutBaseCallLineColor);
	}


	/**
	 * Returns the color associated with the specified token representation.
	 * <p>
	 * If this instance is owned by a {@link PherogramArea}, this method will first try to determine the color
	 * from the associated token painter. If that produces no result, it will return a default color from its
	 * own nucleotide color map. If that map has no entry for the specified representation as well, 
	 * {@link #getProbabilityColor()} will be returned as the default color.
	 * 
	 * @param nucleotide the token representation (nucleotide characters or the gap or unknown character are valid here)
	 * @return the color determined by the algorithm described above.
	 */
	public Color getNucleotideColor(PherogramComponent pherogramComponent, char nucleotide) {
		Color result = null;
		if (pherogramComponent instanceof PherogramArea) {
			result = ((PherogramArea)pherogramComponent).getRelatedTokenPainter().getColor(Character.toString(nucleotide));
		}
		if (result == null) {
			result = nucleotideColorMap.get(Character.toString(nucleotide));
		}
		if (result == null) {
			result = getProbabilityColor();
		}
		return result;
	}
	
	
	/**
	 * Sets a new color for a nucleotide representation in the internal color map of this instance.
	 * <p>
	 * Note that that color is only used if this instance is owned by a {@link PherogramTraceCurveView}
	 * or if the token painter associated with the owning {@link PherogramArea} does not specify an
	 * according color.
	 * 
	 * @param nucleotide the nucleotide (or gap, ...) representation
	 * @param color the color to put in the map (Specify {@code null} here to remove the association for
	 *        {@code nucleotide} from the map.)
	 */
	public void setNucleotideColor(char nucleotide, Color color) {
		Color oldValue;
		if (color == null) {
			oldValue = nucleotideColorMap.remove(Character.toString(nucleotide));
		}
		else {
			oldValue = nucleotideColorMap.put(Character.toString(nucleotide), color);
		}
		propertyChangeSupport.firePropertyChange("nucleotideColor." + nucleotide, oldValue, color);
	}
	
	
	/**
	 * Determines if base call lines (vertical lines at each base call position in the background of the trace 
	 * curve output) shall be painted.
	 * 
	 * @return {@code true} if the lines are painted, {@code false} otherwise
	 */
	public boolean isShowBaseCallLines() {
		return showBaseCallLines;
	}


	/**
	 * Allows to define if base call lines (vertical lines at each base call position in the background of the 
	 * trace curve output) shall be painted. The pherogram will be repainted after setting the property.
	 *
	 * @param showBaseCallLines - Specify {@code true} here if lines shall be painted or {@code false} if not.
	 * @see #toggleShowBaseCallLines()
	 */
	public void setShowBaseCallLines(boolean showBaseCallLines) {
		final boolean oldValue = this.showBaseCallLines;
		this.showBaseCallLines = showBaseCallLines;
		propertyChangeSupport.firePropertyChange("showBaseCallLines", oldValue, showBaseCallLines);
	}


	/**
	 * Toggles whether base call lines (vertical lines at each base call position in the background of the 
	 * trace curve output) shall be painted.
	 * 
	 * @see #setShowBaseCallLines(boolean)
	 */
	public void toggleShowBaseCallLines() {
		setShowBaseCallLines(!isShowBaseCallLines());
	}


	/**
	 * Returns the font used to print the characters of the base call sequence.
	 * 
	 * @return the font object that is used
	 */
	public ZoomableFont getBaseCallFont() {
		return baseCallFont;
	}


	/**
	 * Returns the font used to print the nucleotide indices.
	 * 
	 * @return the font object that is used
	 */
	public ZoomableFont getIndexFont() {
		return indexFont;
	}


	/**
	 * Returns the font used to print the base call quality scores and probability values.
	 * 
	 * @return the font object that is used
	 */
	public ZoomableFont getAnnotationFont() {
		return annotationFont;
	}


	/**
	 * Determines the way quality scores shall be displayed.
	 * 
	 * @return the output type for quality scores
	 */
	public QualityOutputType getQualityOutputType() {
		return qualityOutputType;
	}


	public void setQualityOutputType(QualityOutputType qualityOutputType) {
		final QualityOutputType oldValue = this.qualityOutputType;
		this.qualityOutputType = qualityOutputType;
		propertyChangeSupport.firePropertyChange("qualityOutputType", oldValue, qualityOutputType);
	}
	
	
	/**
	 * Changes the current quality output type to the next value in the order defined by {@link QualityOutputType}. 
	 * If the end of the {@code enum} has been reached this method will start with the first value again.
	 * 
	 * @see #setQualityOutputType(QualityOutputType)
	 */
	public void changeQualityOutputType() {
		int index = getQualityOutputType().ordinal() + 1;
		QualityOutputType[] values = QualityOutputType.values();
		if (index >= values.length) {
			index = 0;
		}
		setQualityOutputType(values[index]);
	}

	
	/**
	 * Returns the height of the quality score output depending on the current output type for the current 
	 * zoom factor.
	 * 
	 * @return the height needed to display the quality output
	 */
	public double qualityOutputHeight(PherogramComponent pherogramComponent) {
		switch (getQualityOutputType()) {
			case ALL:
				return 4 * getAnnotationFont().createFont(calculateFontZoomFactor(pherogramComponent)).getSize2D() * FONT_HEIGHT_FACTOR;
			case MAXIMUM:
				return getAnnotationFont().createFont(calculateFontZoomFactor(pherogramComponent)).getSize2D() * FONT_HEIGHT_FACTOR;
			default:
				return 0;
		}
	}
	

	/**
	 * Determines whether probability values (substitution, overcall and undercall) should be displayed
	 * in the pherogram heading.
	 * 
	 * @return {@code true} the values shall be displayed, {@code false} otherwise
	 */
	public boolean isShowProbabilityValues() {
		return showProbabilityValues;
	}


	/**
	 * Allows to specify whether probability values (substitution, overcall and undercall) should be displayed
	 * in the pherogram heading.
	 * 
	 * @param showProbabilityValues - Specify {@code true} here if the values shall be displayed from now on or 
	 *        {@code false} otherwise.
	 * @see #toggleShowProbabilityValues()
	 */
	public void setShowProbabilityValues(boolean showProbabilityValues) {
		final boolean oldValue = this.showProbabilityValues;
		this.showProbabilityValues = showProbabilityValues;
		propertyChangeSupport.firePropertyChange("showProbabilityValues", oldValue, showProbabilityValues);
	}
	
	
	/**
	 * Toggles whether probability values (substitution, overcall and undercall) should be displayed
	 * in the pherogram heading.
	 * 
	 * @see #setShowProbabilityValues(boolean)
	 */
	public void toggleShowProbabilityValues() {
		setShowProbabilityValues(!isShowProbabilityValues());
	}


	/**
	 * Returns the font color for displaying probability values.
	 * 
	 * @return the current color for probability values
	 */
	public Color getProbabilityColor() {
		return probabilityColor;
	}


	/**
	 * Sets a new font font color for displaying probability values.
	 * 
	 * @param probabilityColor - the new color for probability values
	 */
	public void setProbabilityColor(Color probabilityColor) {
		final Color oldColor = this.probabilityColor;
		this.probabilityColor = probabilityColor;
		propertyChangeSupport.firePropertyChange("probabilityColor", oldColor, probabilityColor);
	}
	
	
	/**
	 * Calculates the factor to multiply font heights with to fit to the current size of the owning component.
	 * 
	 * @throws IllegalStateException if the owner is neither an instance of {@link PherogramArea} or
	 *         {@link PherogramTraceCurveView}
	 */
	public double calculateFontZoomFactor(PherogramComponent pherogramComponent) {
		if (pherogramComponent instanceof PherogramArea) {
			PherogramArea area = (PherogramArea)pherogramComponent;
			PaintSettings paintSettings = area.getOwner().getPaintSettings();
			return Math.min(paintSettings.getZoomY(),
					(area.getEditableTokenWidth() / area.getOwner().getPaintSettings().getTokenHeight()) * 
					paintSettings.getZoomY());
		}
		else if (pherogramComponent instanceof PherogramTraceCurveView) {
			return ((PherogramTraceCurveView)pherogramComponent).getHorizontalScale() * VIEW_FONT_HEIGHT_FACTOR;
		}
		else {
			throw new IllegalStateException("Reading the zoom from an owner of type " + pherogramComponent.getClass() + 
					" is not supported by this implementation.");
		}
	}


	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}


	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}


	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}


	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}
}
