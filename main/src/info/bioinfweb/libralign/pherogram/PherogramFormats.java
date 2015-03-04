/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bioinfweb.libralign.pherogram;


import info.bioinfweb.commons.graphics.GraphicsUtils;
import info.bioinfweb.libralign.alignmentarea.content.SequenceColorSchema;
import info.bioinfweb.libralign.pherogram.view.PherogramHeadingView;

import java.awt.Color;
import java.awt.Font;



/**
 * Class that stores colors and fonts and settings used to display a pherogram from Sanger sequencing.
 * It is used by implementations of {@link PherogramComponent} and {@link PherogramHeadingView} to format
 * their output.
 * <p>
 * Note that the associated pherogram components are not automatically repainted when a property of
 * an instance of this class is changed. Changes will only be visible if the according components are
 * repainted by the application after a property has been changed.
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
		/** Displays quality values for all four nucleotides. */
		ALL, 
		
		/** Displays only the quality value for the nucleotide that has been called. */
		MAXIMUM, 
		
		/** Does not display any quality values. */
		NONE;
	}
	
	
	/** Used to calculate the default background color for the cut off areas. */
	public static final float CUT_COLOR_FACTOR = 1.5f;

	/** Used to calculate the default base call line color. */
	public static final float BASE_CALL_LINE_COLOR_FACTOR = 1.1f;
	
	/** Used to determine the line spacing around base calls, indices or probabilities. */
	public static final double FONT_HEIGHT_FACTOR = 1.2;
	
	
	private SequenceColorSchema  nucleotideColorSchema;
	private Color backgroundColor;
	private Color cutBackgroundColor;
	private Color headingBackgroundColor;
	private Color baseCallLineColor;
	private Color cutBaseCallLineColor;
	private boolean showBaseCallLines = true;
	private Font baseCallFont = new Font(Font.DIALOG, Font.BOLD, 12);
	private Font indexFont = new Font(Font.DIALOG, Font.PLAIN, 8);
	private Font annotationFont = new Font(Font.DIALOG, Font.PLAIN, 8);
	private QualityOutputType qualityOutputType = QualityOutputType.MAXIMUM;
	private boolean showProbabilityValues = false;
	private Color probabilityColor;
	
	
	/**
	 * Creates a new instance of this class with default values.
	 */
	public PherogramFormats() {
		super();
		
		nucleotideColorSchema = new SequenceColorSchema();
		backgroundColor = getNucleotideColorSchema().getDefaultBgColor();
		headingBackgroundColor = backgroundColor;
		cutBackgroundColor = GraphicsUtils.moveColorToCenter(backgroundColor, CUT_COLOR_FACTOR);
		baseCallLineColor =  GraphicsUtils.moveColorToCenter(backgroundColor, BASE_CALL_LINE_COLOR_FACTOR);
		cutBaseCallLineColor =  GraphicsUtils.moveColorToCenter(cutBackgroundColor, BASE_CALL_LINE_COLOR_FACTOR);
		probabilityColor = GraphicsUtils.getContrastColor(Color.BLACK, headingBackgroundColor, 30);
	}


	/**
	 * Returns the color schema that is used to paint the trace curves and base call sequence of the displayed
	 * pherogram.
	 * 
	 * @return the color schema object
	 */
	public SequenceColorSchema getNucleotideColorSchema() {
		return nucleotideColorSchema;
	}


	/**
	 * Sets a new color schema that is used to paint the trace curves and base call sequence of the displayed
	 * pherogram.
	 * <p>
	 * Note that this schema does not include pherogram specific color properties which can be set as separate
	 * properties.
	 * 
	 * @param colorSchema - the new color schema to be used
	 */
	public void setNucleotideColorSchema(SequenceColorSchema nucleotideColorSchema) {
		this.nucleotideColorSchema = nucleotideColorSchema;
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
		this.backgroundColor = backgroundColor;
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
		this.cutBackgroundColor = cutBackgroundColor;
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
		this.headingBackgroundColor = headingBackgroundColor;
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
		this.baseCallLineColor = baseCallLineColor;
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
		this.cutBaseCallLineColor = cutBaseCallLineColor;
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
	 */
	public void setShowBaseCallLines(boolean showBaseCallLines) {
		this.showBaseCallLines = showBaseCallLines;
	}


	/**
	 * Returns the font used to print the characters of the base call sequence.
	 * 
	 * @return the font object that is used
	 */
	public Font getBaseCallFont() {
		return baseCallFont;
	}


	/**
	 * Sets a new font used to print the characters of the base call sequence.
	 * 
	 * @param baseCallFont - the new font
	 */
	public void setBaseCallFont(Font baseCallFont) {
		this.baseCallFont = baseCallFont;
	}


	/**
	 * Returns the font used to print the nucleotide indices.
	 * 
	 * @return the font object that is used
	 */
	public Font getIndexFont() {
		return indexFont;
	}


	/**
	 * Sets a new font used to print the the nucleotide indices.
	 * 
	 * @param baseCallFont - the new font
	 */
	public void setIndexFont(Font indexFont) {
		this.indexFont = indexFont;
	}


	/**
	 * Returns the font used to print the base call quality scores and probability values.
	 * 
	 * @return the font object that is used
	 */
	public Font getAnnotationFont() {
		return annotationFont;
	}


	/**
	 * Sets a new font used to print the base call quality scores and probability values.
	 * 
	 * @param qualityFont - the new font
	 */
	public void setAnnotationFont(Font qualityFont) {
		this.annotationFont = qualityFont;
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
		this.qualityOutputType = qualityOutputType;
	}

	
	public double qualityOutputHeight() {
		switch (getQualityOutputType()) {
			case ALL:
				return 4 * getAnnotationFont().getSize() * FONT_HEIGHT_FACTOR;
			case MAXIMUM:
				return getAnnotationFont().getSize() * FONT_HEIGHT_FACTOR;
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
	 */
	public void setShowProbabilityValues(boolean showProbabilityValues) {
		this.showProbabilityValues = showProbabilityValues;
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
		this.probabilityColor = probabilityColor;
	}
}
