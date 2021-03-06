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
package info.bioinfweb.libralign.dataarea.implementations.sequenceindex;


import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.SystemColor;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.EnumSet;
import java.util.Set;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.collections.observable.ListRemoveEvent;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentPaintEvent;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreasAdapter;
import info.bioinfweb.libralign.dataarea.DataAreasListener;
import info.bioinfweb.libralign.dataelement.DataListType;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsEventForwarder;



/**
 * A data area displaying the indices of aligned sequences.
 * <p>
 * You would usually use this component in the head area of an {@link AlignmentArea} but it is possible to
 * insert it at any position.
 * <p>
 * The labeling interval if this area is calculated from the space needed to label the highest index in the
 * associated alignment model. Note that due to rounding effects the label interval may vary by one depending
 * on the zoom factor.
 *
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class SequenceIndexArea extends DataArea {
	/** The default value returned by {@link #getFirstIndex()} if it has not been changed. */
	public static final int DEFAULT_FIRST_INDEX = 1;

	/** The stroke used to paint the dashes */
	public static final Stroke DASH_STROKE = new BasicStroke(0, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);

	/** The length of a dash */
	public static final double DASH_LENGTH_FACTOR = 0.3f;

	/** The length of a dash with a label */
	public static final double LABELED_DASH_LENGTH_FACTOR = 1f;

	/** The distance of the labels to the border of the component */
	public static final double LABEL_TOP_DISTANCE_FACTOR = 0.55f;  // With 0.5 digits get cut off at the top end for some zoom factors.

	/** The distance of the labels to their dash  */
	public static final double LABEL_LEFT_DISTANCE_FACTOR = 0.2f;


	private int firstIndex = DEFAULT_FIRST_INDEX;

	private final SequenceIndexArea thisSequenceIndexArea;
	private final DataAreasListener dataAreasListener;
	

	/**
	 * Creates a new instance of this class using the parent alignment area of {@code owner} as the labeled area.
	 *
	 * @param owner the alignment area that is going to contain this data area
	 */
	public SequenceIndexArea(AlignmentArea owner) {
		super(owner);
		
		thisSequenceIndexArea = this;
		dataAreasListener = new DataAreasAdapter() {
			@Override
			public void afterElementsRemoved(ListRemoveEvent<DataArea, DataArea> event) {
				if (event.getAffectedElements().contains(thisSequenceIndexArea)) {
					if (getOwner().hasContainer()) {
						//TODO Also remove alignment model listener (and possibly PropertyChangeListener)
						MultipleAlignmentsEventForwarder.getInstance().removeDataAreasListener(getOwner().getContainer(), this);  //TODO This could lead to an ConcurrentModificationException. Adjust implementation of MultipleAlignmentsEventForwarder if necessary.
					}
					else {
						//TODO Also remove alignment model listener (and possibly PropertyChangeListener)
						getOwner().removeDataAreasListener(this);  //TODO This could lead to an ConcurrentModificationException. Adjust implementation of AlignmentArea if necessary.
					}
				}
			}
		};
		
		if (owner.hasContainer()) {
			MultipleAlignmentsEventForwarder.getInstance().addDataAreasListener(owner.getContainer(), dataAreasListener);
		}
		else {
			owner.addDataAreasListener(dataAreasListener);
		}
		
		//TODO Add listeners for AlignmentModel and possible ProperChangeListener for model.
		//     - MultipleAlignmentsEventForwarder should possibly refactored to also work with a single AlignmentArea and track model changes or respective functionality should be extracted and exposed.
		
//		labeledArea.getAlignmentModel().addModelListener(new AlignmentModelAdapter() {
//			@Override
//			public <T> void afterTokenChange(TokenChangeEvent<T> e) {
//				if (!e.getType().equals(ListChangeType.REPLACEMENT)) {
//					assignSize();  // Length of the longest sequence could have changed.
//					repaint();  // In case the size did not change, but the space before the alignment did.
//				}
//			}
//		});
		//TODO Listener needs to be removed again, when this data area is removed.
	}


	private void paintSelection(Graphics2D g) {
		//TODO implement
//		g.setColor(SystemColor.textHighlight);
//		AlignmentComparisonPanelSelection selection = getAlignmentComparisonPanel().getSelection();
//		int x = getAlignmentComparisonPanel().paintXBycolumn(selection.getFirstPos());
//		g.fill(new Rectangle2D.Double(x, 0,
//				getAlignmentComparisonPanel().paintXBycolumn(selection.getLastPos() + 1) - 1 - x, HEIGHT));
	}


	/**
	 * Calculates the number of columns that will at most be used to display a column index label.
	 * The minimal token width is assumed for calculation so that the interval fits for all token types
	 * in a {@link ConcatenatedAlignmentModel}. Areas with wider tokens would have more space more left 
	 * space in between the labels. 
	 * 
	 * @param fontMetrics the font metrics object to calculate the width
	 * @return the number of columns that will at most be used to display a sequence index label
	 */
	private int calculateLabelInterval(FontMetrics fontMetrics) {
		double compoundWidth = getOwner().getPaintSettings().minTokenWidth();
		return (int)Math2.roundUp((fontMetrics.stringWidth("0") *
				Integer.toString(getOwner().getSizeManager().getGlobalMaxSequenceLength()).length() + 
				2 * LABEL_LEFT_DISTANCE_FACTOR * compoundWidth)	/ compoundWidth);
	}


	@Override
	public void paintPart(AlignmentPaintEvent e) {
		Graphics2D g = e.getGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    		RenderingHints.VALUE_ANTIALIAS_ON);

    // Paint background:
    Rectangle2D visibleRect = e.getRectangle();
		g.setColor(SystemColor.menu);
		g.fill(visibleRect);
		paintSelection(g);

    // Paint base line
		//TODO The following check was removed, since there is no single labeled model anymore. If ConcatenatedAlignmentModels will exist one day, it would have to be discussed if they can be combines with models with other columns in the same container and how this data area would handle such a case.
//		if (getLabeledAlignmentModel() instanceof ConcatenatedAlignmentModel) {
//			throw new InternalError("Support for concatenated models not yet implemented.");
//		}
		double compoundWidth = getOwner().getPaintSettings().getTokenWidth(0);  //TODO Implement support for concatenated models.
		g.setColor(SystemColor.menuText);
    g.draw(new Line2D.Double(visibleRect.getMinX(), getHeight() - 1, visibleRect.getMinX() + visibleRect.getWidth(), getHeight() - 1));  // base line

    // Paint text data and dashes:
    final double maxLengthBeforeStart = getOwner().getSizeManager().getGlobalMaxLengthBeforeStart();
    double labelLeftDistance = LABEL_LEFT_DISTANCE_FACTOR * compoundWidth;
    g.setFont(getOwner().getPaintSettings().getTokenHeightFont());
    int labelInterval = calculateLabelInterval(g.getFontMetrics());
    double x = Math.max(compoundWidth / 2f,
    		visibleRect.getMinX() - visibleRect.getMinX() % compoundWidth - labelInterval * compoundWidth - compoundWidth / 2f);  // labelInterval is subtracted because partly visible text should also be painted
    Stroke stroke = g.getStroke();
    try {
      while (x <= visibleRect.getMinX() + visibleRect.getWidth()) {
    		// Text output
    		double dashLength = DASH_LENGTH_FACTOR * getHeight();
    		long compoundIndex = Math2.roundUp((x - maxLengthBeforeStart) / compoundWidth);  // If Math.round() is used, intervals are not constant, if values like 2.4999999999 occur.
    		//TODO Rename compoundIndex to column and get index from model.
    		if ((compoundIndex - 1) % labelInterval == 0) {  // BioJava indices start with 1
    			g.drawString("" + (compoundIndex + getFirstIndex() - 1), (int)(x + labelLeftDistance),
    					(int)Math2.roundUp(LABEL_TOP_DISTANCE_FACTOR * getHeight()));
    			dashLength = LABELED_DASH_LENGTH_FACTOR * getHeight();
    		}

      	// Dash output
    		g.setStroke(DASH_STROKE);
    		Path2D path = new  Path2D.Double();
    		path.moveTo(x - 0.5, getHeight());
    		path.lineTo(x + 0.5, getHeight());
    		path.lineTo(x + 0.5, getHeight() - dashLength);
    		path.lineTo(x - 0.5, getHeight() - dashLength);
    		path.closePath();
    		g.fill(path);

    		x += compoundWidth;
      }
    }
    finally {
    	g.setStroke(stroke);
    }
	}


	@Override
	public Set<DataListType> validLocations() {
		return EnumSet.of(DataListType.TOP, DataListType.SEQUENCE, DataListType.BOTTOM);
  }


	/**
	 * Returns the index that will be displayed for the first column of the alignment area.
	 * <p>
	 * The default value is {@value #DEFAULT_FIRST_INDEX}. Note that the methods of {@link AlignmentArea} and associated classes will always
	 * use 0 as the index of the first column no matter which value is set here. It is used for the GUI output
	 * of this data area only.
	 *
	 * @return the displayed index of the first column
	 */
	public int getFirstIndex() {
		return firstIndex;
	}


	/**
	 * Sets a new index that will be displayed for the first column of the alignment area.
	 * <p>
	 * The methods of {@link AlignmentArea} and associated classes will always use 0 as the index of the first
	 * column no matter which value is specified here. It is used for the GUI output of this data area only.
	 *
	 * @param firstIndex - the new index display for the first column
	 */
	public void setFirstIndex(int firstIndex) {
		this.firstIndex = firstIndex;
	}


	@Override
	public double getHeight() {
		return getOwner().getPaintSettings().getTokenHeight();
	}
}
