/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Set;

import org.biojava3.core.sequence.compound.NucleotideCompound;

import info.bioinfweb.commons.graphics.DoubleDimension;
import info.bioinfweb.commons.graphics.GraphicsUtils;
import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentprovider.AlignmentDataProvider;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaList;
import info.bioinfweb.libralign.dataarea.DataAreaModel;
import info.bioinfweb.libralign.selection.AlignmentCursor;
import info.bioinfweb.libralign.selection.SelectionModel;
import info.webinsel.util.Math2;



/**
 * GUI element of LibrAlign that displays an alignment including attached data views.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public class AlignmentArea extends TICComponent {
	public static final float COMPOUND_WIDTH = 10f;
	public static final float COMPOUND_HEIGHT = 14f;
	public static final String FONT_NAME = Font.SANS_SERIF;
	public static final int FONT_STYLE = Font.PLAIN;
	public static final float FONT_SIZE_FACTOR = 0.7f;
	public static final int MIN_FONT_SIZE = 4;

	
	private AlignmentDataProvider dataProvider = null;
	private SequenceOrder sequenceOrder = new SequenceOrder(this);
	private SequenceColorSchema colorSchema = new SequenceColorSchema();
	private WorkingMode workingMode = WorkingMode.VIEW;  //TODO Should this better be part of the controller (key and mouse listener)?
	private AlignmentDataViewMode viewMode = AlignmentDataViewMode.NUCLEOTIDE;  //TODO Initial value should be adjusted when the data type of the specified provider is known.
	private AlignmentCursor cursor = new AlignmentCursor();
	private SelectionModel selection = new SelectionModel(this);
	private DataAreaModel dataAreas = new DataAreaModel();
	private float zoomX = 1f;
	private float zoomY = 1f;
	private float compoundWidth = COMPOUND_WIDTH;
	private float compoundHeight = COMPOUND_HEIGHT;	
	private Font font = new Font(Font.SANS_SERIF, Font.PLAIN, Math.round(COMPOUND_HEIGHT * 0.7f));
	
	
	public boolean hasDataProvider() {
		return getDataProvider() != null;
	}
	
	
	public AlignmentDataProvider getDataProvider() {
		return dataProvider;
	}
	
	
	public void setDataProvider(AlignmentDataProvider dataProvider) {
		this.dataProvider = dataProvider;
		getSequenceOrder().setSourceSequenceOrder();  // Update sequence names
		//TODO repaint
		//TODO Send message to all and/or remove some data areas? (Some might be data specific (e.g. pherograms), some not (e.g. consensus sequence).) 
	}
	
	
	public SequenceOrder getSequenceOrder() {
		return sequenceOrder;
	}


	public SequenceColorSchema getColorSchema() {
		return colorSchema;
	}


	public WorkingMode getWorkingMode() {
		return workingMode;
	}
	
	
	public void setWorkingMode(WorkingMode workingMode) {
		this.workingMode = workingMode;
	}
	
	
	public AlignmentDataViewMode getViewMode() {
		return viewMode;
	}
	
	
	public void setViewMode(AlignmentDataViewMode viewMode) {
		this.viewMode = viewMode;
		//TODO repaint
	}


	public AlignmentCursor getCursor() {
		return cursor;
	}


	/**
	 * Returns the selection model used by this object.
	 */
	public SelectionModel getSelection() {
		return selection;
	}


	/**
	 * Returns the data area model used by this object containing all data areas attached 
	 * to this alignment. 
	 */
	public DataAreaModel getDataAreas() {
		return dataAreas;
	}


	public float getZoomX() {
		return zoomX;
	}


	public float getZoomY() {
		return zoomY;
	}


	public void setZoomX(float zoomX) {
		setZoom(zoomX, getZoomY());
	}
	
	
	public void setZoomY(float zoomY) {
		setZoom(getZoomX(), zoomY);
	}
	
	
	public void setZoom(float zoomX, float zoomY) {
		this.zoomX = zoomX;
		this.zoomY = zoomY;
		compoundWidth = COMPOUND_WIDTH * zoomX;
		compoundHeight = COMPOUND_HEIGHT * zoomY;
		calculateFont();
		
		//assignPaintSize();
		//fireZoomChanged();
	}
	
	
	private void calculateFont() {
		int fontSize = Math.round(Math.min(compoundHeight * (COMPOUND_WIDTH / COMPOUND_HEIGHT), compoundWidth) * 
				FONT_SIZE_FACTOR);  //TODO Equation correct?
		if (fontSize >= MIN_FONT_SIZE) {
			font = new Font(FONT_NAME, FONT_STYLE, fontSize);
		}
		else {
			font = null;
		}
	}

	
	/**
	 * Returns the current width of a compound depending on the current zoom factor.
	 * 
	 * @return a float value greater than zero
	 */
	public float getCompoundWidth() {
		return compoundWidth;
	}


	public void setCompoundWidth(float compoundWidth) {
		this.compoundWidth = compoundWidth;
		zoomX = compoundWidth / COMPOUND_WIDTH;
		calculateFont();
		
		//assignPaintSize();
		//fireZoomChanged();
	}


	/**
	 * Returns the current height of a compound depending on the current zoom factor.
	 * 
	 * @return a float value greater than zero
	 */
	public float getCompoundHeight() {
		return compoundHeight;
	}
	
	
	public void setCompoundHeight(float compoundHeight) {
		this.compoundHeight = compoundHeight;
		zoomY = compoundHeight / COMPOUND_HEIGHT;
		calculateFont();
		
		//assignPaintSize();
		//fireZoomChanged();
	}


	/**
	 * Returns the size this component will use depending on the current zoom factor.
	 */
	@Override
	public Dimension2D getSize() {
		return new DoubleDimension(getDataProvider().getMaxSequenceLength() * getCompoundWidth(),
				getDataProvider().getSequenceCount() * getCompoundHeight() + getDataAreas().getVisibleAreaHeight());
		//TODO May data areas be wider than the alignment?
	}


	/**
	 * Indicates whether compounds should are printed as text.
	 * <p>
	 * If any zoom factor is to low, each compound is only painted as a rectangle without any text in it.
	 * Text output will not be done, if the font size would be below {@link #MIN_FONT_SIZE}.
	 * </p>
	 * 
	 * @return {@code true}, if any compound text will be painted, {@code false} otherwise
	 */
	public boolean isPaintCompoundText() {
		return getCompoundFont() != null;
	}
	
	
	public Font getCompoundFont() {
		return font;
	}
	
	
	/**
	 * Returns the column containing the specified x coordinate.
	 *  
	 * @param x the paint coordinate
	 * @return the alignment column or <code>-1</code> id the coordinate is outside the alignment area.
	 */
	public int columnByPaintX(int x) {
		int result = (int)(x / getCompoundWidth());
		if (Math2.isBetween(result, 0, getDataProvider().getMaxSequenceLength() - 1)) {
			return result;
		}
		else {
			return -1;
		}
	}


	public int paintXByColumn(int column) {
		return (int)((column - 1) * getCompoundWidth());
	}

	
	/**
	 * Paints all the data ares contained in the specified list which are set visible.
	 * It is  not checked if these areas are contained in the visible rectangle.
	 * 
	 * @param list - the list of elements to be painted
	 * @param e - the the initial paint event
	 * @param x - the x coordinate of the data areas
	 * @param y - the y coordinate of the top most data area
	 * @return the new value for y below all the painted areas
	 */
	private float paintDataAreaList(DataAreaList list, TICPaintEvent e, float x, float y) {
		Iterator<DataArea> iterator = list.visibleIterator();
		while (iterator.hasNext()) {
			DataArea dataArea = iterator.next();
			
			// Create Graphics2D:
			Dimension2D size = dataArea.getSize();
			int intX = (int)x;
			int intY = (int)y;
			int rectWidth = (int)Math2.roundUp(Math.min(size.getWidth(), -x + e.getRectangle().width));
			int rectHeight = (int)Math2.roundUp(Math.min(size.getHeight(), -y + e.getRectangle().height));
			Graphics2D dataAreaGraphics = (Graphics2D)e.getGraphics().create(intX, intY, rectWidth, rectHeight); 
			dataAreaGraphics.translate(x - intX, y - intY);  // Do the rest of the translation
			
			dataArea.paint(new TICPaintEvent(e.getSource(), dataAreaGraphics, 
					new Rectangle(rectWidth, rectHeight)));
			y += size.getHeight();
		}
		return y;
	}
	
	
	@Override
	public void paint(TICPaintEvent e) {
		if (hasDataProvider()) {
			float x = 0f;
			float y = 0f;
			
			// Top areas:
			if (e.getRectangle().getMinY() <= y + getDataAreas().getTopAreas().getVisibleHeight()) {
				y = paintDataAreaList(getDataAreas().getTopAreas(), e, x, y);
			}
			
			// Sequences and attached areas:
			for (int i = 0; i < getDataProvider().getSequenceCount(); i++) {
				if (y >= e.getRectangle().getMinY()) {
					if (y > e.getRectangle().getMaxY()) {  // End output, if the end of the visible rectangle on y has been reached
						break;
					}
					else {
						paintSequence(e.getGraphics(), i, x, y, e.getRectangle());
						y += getCompoundHeight();
						y = paintDataAreaList(getDataAreas().getSequenceAreas(getSequenceOrder().nameByIndex(i)), e, x, y);
					}
				}
			}
			
			// Bottom areas:
			if (e.getRectangle().getMaxY() >= y) {
				y = paintDataAreaList(getDataAreas().getBottomAreas(), e, x, y);
			}
		}
	}
	
	
	private void paintSequence(Graphics2D g, int sequenceIndex, float x, float y, Rectangle visibleRect) {
		int firstIndex = Math.max(0, columnByPaintX((int)visibleRect.getMinX()));
		int lastIndex = columnByPaintX((int)visibleRect.getMaxX());
		if (lastIndex == -1) {
			lastIndex = getDataProvider().getMaxSequenceLength() - 1;
		}
		
  	x += firstIndex * getCompoundWidth();
		for (int i = firstIndex; i <= lastIndex; i++) {			
    	paintCompound(g, getDataProvider().getTokenAt(getSequenceOrder().nameByIndex(sequenceIndex), i), 
    			x, y,	getSelection().isSelected(i, sequenceIndex));
	    x += getCompoundWidth();
    }
	}
	
	
	private Color getBGColor(Color color, boolean selected) {
		if (color == null) {
			color = getColorSchema().getDefaultBgColor();
		}
		if (selected) {
			color = GraphicsUtils.blend(color, getColorSchema().getSelectionColor());
		}
		return color;
	}


	private String getNucleotideBaseString(NucleotideCompound compound) {
		String result = compound.getUpperedBase();
		if (result.equals("U") && getViewMode().equals(AlignmentDataViewMode.DNA)) {
			return "T";
		}
		else if (result.equals("T") && getViewMode().equals(AlignmentDataViewMode.RNA)) {
			return "U";
		}
		else {
			return result;
		}
	}
	
	
  private void paintNucleotideCompound(Graphics2D g, NucleotideCompound compound, float x, float y, 
  		boolean selected) {
  	
  	Set<NucleotideCompound> consituents = compound.getConstituents();
  	final float height = getCompoundHeight() / (float)consituents.size();
  	Iterator<NucleotideCompound> iterator = consituents.iterator();
  	float bgY = y;
  	while (iterator.hasNext()) {  // Fill the compound rectangle with differently colored zones, if ambiguity codes are used.
  		g.setColor(getBGColor(
  				getColorSchema().getNucleotideColorMap().get(getNucleotideBaseString(iterator.next())),	selected));
    	g.fill(new Rectangle2D.Float(x, bgY, getCompoundWidth(), height));
    	bgY += height;
  	}
  	
  	if (isPaintCompoundText()) {  // Text output only if font size is not too low
	  	g.setColor(getColorSchema().getFontColor());
	  	g.setFont(getCompoundFont());
			FontMetrics fm = g.getFontMetrics();
	  	g.drawString(compound.getBase(), x + 0.5f * (getCompoundWidth() - fm.charWidth(compound.getBase().charAt(0))), y + fm.getAscent());
  	}
  }
  
  
  private void paintCompound(Graphics2D g, Object compound, float x, float y, boolean selected) {
  	g.setColor(getColorSchema().getTokenBorderColor());
  	g.draw(new Rectangle2D.Float(x, y, getCompoundWidth(), getCompoundHeight()));
  	
  	switch (getViewMode()) {
  		case NUCLEOTIDE:
			case DNA:
			case RNA:
				paintNucleotideCompound(g, (NucleotideCompound)compound, x, y, selected);
				//TODO Type cast funktioniert so nicht, wenn Quelldaten nicht diesen Datentyp haben! => Konvertierung mit GeneticCode hinzufügen.
				break;
			case CODON:
				break;
			case MIXED_AMINO_ACID:
				break;
			case ALL_AMINO_ACID:
				break;
			case NONE:
				break;
  	}
  }
}
