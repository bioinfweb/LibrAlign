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
package info.bioinfweb.libralign.alignmentarea.content;


import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.SystemUtils;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.ToolkitSpecificAlignmentArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelArea;
import info.bioinfweb.libralign.alignmentarea.paintsettings.PaintSettings;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionModel;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaList;
import info.bioinfweb.libralign.dataarea.DataAreaListType;
import info.bioinfweb.libralign.dataarea.DataAreaLocation;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;
import info.bioinfweb.tic.TICComponent;
import info.bioinfweb.tic.TICPaintEvent;
import info.bioinfweb.tic.TargetToolkit;
import info.bioinfweb.tic.input.TICMouseWheelEvent;
import info.bioinfweb.tic.input.TICMouseWheelListener;
import info.bioinfweb.tic.toolkit.ToolkitComponent;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;



/**
 * Toolkit independent component displaying the contents (not the labels) of a single alignment and its attached
 * data areas.
 * <p>
 * This component is used a child component of {@link AlignmentArea} and in contrast to {@link AlignmentArea}
 * does not contain a scroll container, but acts as the scrolled component. Application developers will not
 * need to create instances of this class directly but should use {@link AlignmentArea}.
 *
 * @author Ben St&ouml;ver
 * @since 0.3.0
 *
 * @see AlignmentArea
 * @see AlignmentLabelArea
 */
public class AlignmentContentArea extends TICComponent {
	private static final double ZOOM_PER_CLICK = 0.1;


	private final AlignmentArea owner;
	private final boolean useSubcomponents;
	private SequenceAreaMap sequenceAreaMap;
	private Map<KeyStroke, Action> actionMap = new HashMap<KeyStroke, Action>();


	/**
	 * Creates a new instance of this class.
	 * <p>
	 * If {@code code} is part of a {@link MultipleAlignmentsContainer} the shared edit settings of this container
	 * will automatically be used by the returned instance.
	 *
	 * @param owner the alignment area component that will be containing the return instance
	 * @param useSubcomponents Determines whether single subcomponents shall be used for each sequence or data area 
	 *        or if all their contents shall be painted on a single shared component.
	 */
	public AlignmentContentArea(AlignmentArea owner, boolean useSubcomponents) {
		super();
		this.useSubcomponents = useSubcomponents;
		this.owner = owner;
		sequenceAreaMap = new SequenceAreaMap(this);
		
		fillActionMap();
		addMouseWheelListener(new TICMouseWheelListener() {
			@Override
			public boolean mouseWheelMoved(TICMouseWheelEvent event) {
				if ((event.isMetaDown() && SystemUtils.IS_OS_MAC) || (event.isControlDown() && !SystemUtils.IS_OS_MAC)) {
					double change = event.getPreciseWheelRotation() * ZOOM_PER_CLICK;
					PaintSettings settings = getOwner().getPaintSettings();

					double zoomX = settings.getZoomX();
					if (settings.isChangeZoomXOnMouseWheel() && (zoomX - change >= 0)) {
						zoomX -= change;
					}
					double zoomY = settings.getZoomY();
					if (settings.isChangeZoomYOnMouseWheel() && (zoomY - change >= 0)) {
						zoomY -= change;
					}
					settings.setZoom(zoomX, zoomY);
					return true;
				}
				else {
					return false;  // Forward event to parent JScrollPane to scroll.
				}
			}
		});
  }


	private void fillActionMap() {
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.SHIFT_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SelectionModel selection = getOwner().getSelection();
				if (selection.getCursorRow() < selection.getStartRow()) {  // Above selection start
					selection.setSelectionEnd(selection.getCursorColumn() - 1, selection.getCursorRow());
				}
				else {  // Below selection start
					selection.setSelectionEnd(selection.getCursorColumn() - 1, 
							selection.getCursorRow() + selection.getCursorHeight() - 1);
				}
			}
		});
		
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SelectionModel selection = getOwner().getSelection();
				selection.setNewCursorColumn(selection.getCursorColumn() - 1);
			}
		});
		
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.SHIFT_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SelectionModel selection = getOwner().getSelection();
				if (selection.getCursorRow() < selection.getStartRow()) {  // Above selection start
					selection.setSelectionEnd(selection.getCursorColumn() + 1, selection.getCursorRow());
				}
				else {  // Below selection start
					selection.setSelectionEnd(selection.getCursorColumn() + 1, 
							selection.getCursorRow() + selection.getCursorHeight() - 1);
				}
			}
		});

		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SelectionModel selection = getOwner().getSelection();
				selection.setNewCursorColumn(selection.getCursorColumn() + 1);
			}
		});
		
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.SHIFT_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SelectionModel selection = getOwner().getSelection();
				if (selection.getCursorRow() < selection.getStartRow()) {  // Above selection start
					selection.setSelectionEnd(selection.getCursorColumn(), selection.getCursorRow() - 1);
				}
				else {  // Below selection start
					selection.setSelectionEnd(selection.getCursorColumn(), selection.getCursorRow() + selection.getCursorHeight() - 2);
				}
			}
		});

		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SelectionModel selection = getOwner().getSelection();
				selection.setNewCursorRow(selection.getCursorRow() - 1);
			}
		});
		
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.SHIFT_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SelectionModel selection = getOwner().getSelection();
				if (selection.getCursorRow() < selection.getStartRow()) {  // Above selection start
					selection.setSelectionEnd(selection.getCursorColumn(), selection.getCursorRow() + 1);
				}
				else {  // Below selection start
					selection.setSelectionEnd(selection.getCursorColumn(), selection.getCursorRow() + selection.getCursorHeight());  // - 1 + 1
				}
			}
		});

		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SelectionModel selection = getOwner().getSelection();
				selection.setNewCursorRow(selection.getCursorRow() + 1);
			}
		});

		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, KeyEvent.SHIFT_MASK + 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						SelectionModel selection = getOwner().getSelection();
						selection.setSelectionEnd(0, 0);
					}
				});

		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, KeyEvent.SHIFT_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SelectionModel selection = getOwner().getSelection();
				if (selection.getCursorRow() < selection.getStartRow()) {  // Above selection start
					selection.setSelectionEnd(0, selection.getCursorRow());
				}
				else { // Below selection start
					selection.setSelectionEnd(0, 
							selection.getCursorRow() + selection.getCursorHeight() - 1);
				}
			}
		});

		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), 
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getOwner().getSelection().setNewCursorPosition(0, 0);
					}
				});

		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getOwner().getSelection().setNewCursorColumn(0);
			}
		});

		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_END, KeyEvent.SHIFT_MASK + 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						AlignmentModel<?> model = getOwner().getAlignmentModel();
						getOwner().getSelection().setSelectionEnd(model.getMaxSequenceLength(), model.getSequenceCount() - 1);
					}
				});

		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_END, KeyEvent.SHIFT_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SelectionModel selection = getOwner().getSelection();
				AlignmentModel<?> model = getOwner().getAlignmentModel();
				if (selection.getCursorRow() < selection.getStartRow()) {  // Above selection start
					selection.setSelectionEnd(model.getMaxSequenceLength(), selection.getCursorRow());
				}
				else { // Below selection start
					selection.setSelectionEnd(model.getMaxSequenceLength(), 
							selection.getCursorRow() + selection.getCursorHeight() - 1);
				}
			}
		});

		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_END, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), 
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						AlignmentModel<?> model = getOwner().getAlignmentModel();
						getOwner().getSelection().setNewCursorPosition(model.getMaxSequenceLength(), model.getSequenceCount() - 1);
					}
				});

		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getOwner().getSelection().setNewCursorColumn(getOwner().getAlignmentModel().getMaxSequenceLength());
			}
		});

		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getOwner().getEditSettings().toggleInsert();
			}
		});

		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!getOwner().getActionProvider().deleteForward()) {
					Toolkit.getDefaultToolkit().beep();
				}
			}
		});

		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!getOwner().getActionProvider().deleteBackwards()) {
					Toolkit.getDefaultToolkit().beep();
				}
			}
		});
		
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), 
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getOwner().getSelection().selectAll();
					}
				});
	}
	

	public AlignmentArea getOwner() {
		return owner;
	}


	/**
	 * Determines whether single subcomponents shall be used for each sequence or data area 
	 * or if all their contents shall be painted on a single shared component.
	 * 
	 * @return {@code true} if subcomponents are used, {@code false} otherwise
	 * @since 0.5.0
	 */
	public boolean isUseSubcomponents() {
		//TODO Does this work this way? If so, update documentation of constructors.
		return (hasToolkitComponent() && getToolkitComponent().getTargetToolkit().equals(TargetToolkit.SWING)) || useSubcomponents;
	}
	
	
	public AlignmentSubAreaIterator subAreaIterator() {
		return new AlignmentSubAreaIterator(getOwner());
	}


  protected SequenceAreaMap getSequenceAreaMap() {
		return sequenceAreaMap;
	}


	/**
   * Returns the {@link SequenceArea} inside this area that displays the sequence with the specified ID.
   * 
   * @param sequenceID the ID of the sequence displayed in the returned area
   * @return the sequence area or {@code null} if no sequence with the specified ID is displayed in this area
   */
	public SequenceArea getSequenceAreaByID(String sequenceID) {
		return sequenceAreaMap.get(sequenceID);
	}


	public Map<KeyStroke, Action> getActionMap() {
		return actionMap;
	}
	
	
	public void updateSubelements() {
		getSequenceAreaMap().updateElements();
		if (isUseSubcomponents() && hasToolkitComponent()) {
			((ToolkitSpecificAlignmentContentArea)getToolkitComponent()).reinsertSubelements();
		}
	}


	/**
	 * Returns the rectangle in the paint coordinate system of scrolled area displaying the sequences, that contains
	 * all cells currently occupied by the alignment cursor.
	 * <p>
	 * If the last row has associated data areas, the height of these areas is also included in the rectangle.
	 *
	 * @return a rectangle with paint coordinates
	 */
	public Rectangle2D getCursorRectangle() {
		SelectionModel selection = getOwner().getSelection();
		double y = paintYByRow(selection.getCursorRow());
		double width = 0;
		if (selection.getCursorColumn() < getOwner().getAlignmentModel().getMaxSequenceLength()) {  // There is no more token behind the cursor, if it is located at the right end of the alignment.
			width = getOwner().getPaintSettings().getTokenWidth(selection.getCursorColumn());
		}
		Rectangle2D.Double result = new Rectangle2D.Double(paintXByColumn(selection.getCursorColumn()), y, width,
				paintYByRow(selection.getCursorRow() + selection.getCursorHeight()) - y);
		if (selection.getCursorRow() + selection.getCursorHeight() - 1 ==
				getOwner().getAlignmentModel().getSequenceCount() - 1) {

			result.height += getOwner().getPaintSettings().getTokenHeight();  // Add height of the last row, because the return value of paintYByRow(maxIndex + 1) is equal to paintYByRow(maxIndex).
		}
		return result;
	}


	@Override
	public Dimension getSize() {
		Dimension result = new Dimension((int)Math2.roundUp(getOwner().getGlobalMaxNeededWidth()), getOwner().getDataAreas().getVisibleAreaHeight());
		if (getOwner().hasAlignmentModel()) {
			result.height += getOwner().getAlignmentModel().getSequenceCount() * getOwner().getPaintSettings().getTokenHeight();
		}
		return result;
	}


	private double paintSubArea(AlignmentSubArea area, double y, Graphics2D g, Rectangle2D r, int firstIndex, int lastIndex) {
		if (Math2.overlaps(y, y + area.getHeight(), r.getMinY(), r.getMaxY())) {
			double yDif = Math.max(0, r.getMinY() - y);
			g.translate(0, y);
			area.paintPart(new AlignmentPaintEvent(this, getOwner(), 
					firstIndex, lastIndex, g, 
					new Rectangle2D.Double(r.getMinX(), yDif, r.getWidth(), Math.min(r.getHeight(), area.getHeight()) - yDif)));
		}
		return y + area.getHeight();
	}
	
	
	private double paintDataAreaList(DataAreaList list, double y, Graphics2D g, Rectangle2D r, int firstIndex, int lastIndex, 
			AffineTransform saveAT) {
		
		for (DataArea area : list) {
			paintSubArea(area, y, g, r, firstIndex, lastIndex);
			g.setTransform(saveAT);
			y += area.getHeight();
		}
		return y;
	}
	
	
	/**
	 * Paints the contents (sequences and data areas) of an alignment, if this instance is set to use direct painting.
	 * This method is not used, if the instance is set to use subcomponents for each sequence and data area.
	 * 
	 * @see #isUseSubcomponents()
	 * @see info.bioinfweb.tic.TICComponent#paint(info.bioinfweb.tic.TICPaintEvent)
	 */
	@Override
	public void paint(TICPaintEvent event) {
		double y = 0;
		Rectangle2D r = event.getRectangle();
		Graphics2D g = event.getGraphics();
		AffineTransform saveAT = g.getTransform();
		
		//TODO Move this code to a tool class and reuse it in DefaultAlignmentSubAreaComponent.paint().
		int firstIndex = Math.max(0, columnByPaintX(r.getMinX()));
		int lastIndex = columnByPaintX(r.getMaxX());
		if ((lastIndex == -1)) {
			lastIndex = getOwner().getAlignmentModel().getMaxSequenceLength();
		}
		
		y = paintDataAreaList(getOwner().getDataAreas().getTopAreas(), y, g, r, firstIndex, lastIndex, saveAT);
		Iterator<String> idIterator = getOwner().getSequenceOrder().idIterator();
		while (idIterator.hasNext()) {
			String id = idIterator.next();
			SequenceArea sequenceArea = getSequenceAreaByID(id);
			y = paintSubArea(sequenceArea, y, g, r, firstIndex, lastIndex);
			g.setTransform(saveAT);
			y = paintDataAreaList(getOwner().getDataAreas().getSequenceAreas(id), y, g, r, firstIndex, lastIndex, saveAT);
		}
		y = paintDataAreaList(getOwner().getDataAreas().getBottomAreas(), y, g, r, firstIndex, lastIndex, saveAT);
		
		g.setColor(SystemColor.control);  //TODO Which color should be used?
		g.fill(new Rectangle2D.Double(r.getMinX(), y, r.getWidth(), r.getHeight() - Math.max(0, r.getMinY() - y)));  // The remaining space needs to be filled. Otherwise scrolling artifacts are drawn there.
	}


	@Override
	protected String getSwingComponentClassName() {
		return "info.bioinfweb.libralign.alignmentarea.content.ScrollContainerSwingAlignmentContentArea";
//		if (isUseSubcomponents()) {
//			return "info.bioinfweb.libralign.alignmentarea.content.ScrollContainerSwingAlignmentContentArea";
//		}
//		else {
//			return super.getSwingComponentClassName();
//		}
	}


	@Override
	protected String getSWTComponentClassName() {
		if (isUseSubcomponents()) {
			return "info.bioinfweb.libralign.alignmentarea.content.ScrollContainerSWTAlignmentContentArea";
		}
		else {
			return "info.bioinfweb.libralign.alignmentarea.content.DirectSWTAlignmentContentArea";
		}
	}


	/**
	 * Returns the column containing the specified x coordinate. If the coordinate lies behind the last column,
	 * the number of columns + 1 is returned.
	 *
	 * @param x the paint coordinate
	 * @return the alignment column
	 */
	public int columnByPaintX(double x) {
		if (getOwner().getAlignmentModel() instanceof ConcatenatedAlignmentModel) {
			throw new InternalError("not implemented");  //TODO Implement and consider that different alignment parts may have different token widths here.
		}
		else {
			return Math.max(0, Math.min(getOwner().getGlobalMaxSequenceLength(),
					(int)((x - getOwner().getDataAreas().getGlobalMaxLengthBeforeStart()) / getOwner().getPaintSettings().getTokenWidth(0))));  //TODO Catch IllegalStateException?
		}
	}


	/**
	 * Returns the left most x-coordinate of the area the specified column is painted in relative to the
	 * component on which the sequences are painted. Use this method to convert between cell indices and
	 * paint coordinates. This method takes the current horizontal zoom factor into account.
	 *
	 * @param column the column painted at the returned x-position
	 * @return a value >= 0
	 */
	public double paintXByColumn(int column) {
	  if (getOwner().hasAlignmentModel()) {
  		if (getOwner().getAlignmentModel() instanceof ConcatenatedAlignmentModel) {
  			throw new InternalError("not implemented");  //TODO Implement and consider that different alignment parts may have different token widths here.
  		}
  		else {
  			return column *
  					getOwner().getPaintSettings().getTokenPainterList().painterByColumn(0).getPreferredWidth() *
  					getOwner().getPaintSettings().getZoomX() + getOwner().getDataAreas().getGlobalMaxLengthBeforeStart();  //TODO Catch IllegalStateException?
  		}
	  }
	  else {
	    throw new IllegalStateException("Column dependent paint positions can only be calculated if an alignment model is defined.");
	  }
	}


	/**
	 * Returns the child component containing the specified y-coordinate.
	 * 
	 * @param y the y-coordinate relative to this alignment content area
	 * @return the sequence or data area at the specified position or {@code null} if {@code y} is 
	 *         below 0 or higher than this instance
	 */
	public AlignmentSubArea getAreaByY(double y) {
		double currentY = 0;
		Iterator<AlignmentSubArea> iterator = subAreaIterator();
		while (iterator.hasNext()) {
			AlignmentSubArea area = iterator.next();
			double height = area.getHeight();
			if (Math2.isBetween(y, currentY, currentY + height)) {
				return area;
			}
			currentY += height;
		}
		return null;
	}
	

	/**
	 * Returns the row index of the sequence displayed at the specified y coordinate considering the current order
	 * of sequences. If a data area is displayed at the specified position, the row of the associated sequence is returned
	 * anyway.
	 *
	 * @param y the y coordinate relative to the alignment part area containing the sequence areas
	 * @return a valid sequence position.
	 */
	public int rowByPaintY(double y) {
		AlignmentSubArea subArea = getAreaByY(y);
		if (subArea instanceof DataArea) {
			DataAreaLocation location = ((DataArea)subArea).getList().getLocation();
			if (location.getListType().equals(DataAreaListType.SEQUENCE)) {
				subArea = getSequenceAreaByID(location.getSequenceID());
			}
		}
		if (subArea instanceof SequenceArea) {
			return getOwner().getSequenceOrder().indexByID(((SequenceArea)subArea).getSequenceID());
		}
		else if (y < 0) {
			return 0;
		}
		else if (getOwner().hasAlignmentModel()) {
			return Math.max(0, getOwner().getAlignmentModel().getSequenceCount() - 1);
		}
		else {
			return 0;
		}
	}


	/**
	 * Returns the top most y-coordinate of the area the specified row is painted in relative to the
	 * component on which the sequences are painted. Use this method to convert between cell indices and
	 * paint coordinates.
	 * <p>
	 * If an index lower than zero or greater than the highest index is specified the y-coordinate of the first
	 * or the last sequence is returned accordingly.
	 *
	 * @param row the row painted at the returned x-position
	 * @return a value >= 0
	 */
	public double paintYByRow(int row) {
		row = Math.max(0, Math.min(getOwner().getAlignmentModel().getSequenceCount() - 1, row));
		SequenceArea area = getSequenceAreaByID(getOwner().getSequenceOrder().idByIndex(row));
		if (isUseSubcomponents()) {
			if (area.hasComponent() && area.getComponent().hasToolkitComponent()) {
				return area.getComponent().getToolkitComponent().getLocationInParent().y;
			}
			else {
				throw new IllegalStateException("No Swing or SWT component of the specified sequence area has yet been created.");
			}
		}
		else {
			throw new InternalError("Not implemented.");
			//TODO Support direct painting without subcomponents.
		}
	}


	/**
	 * Calculates the y coordinate relative to the alignment content area, which contains the specified sequence area.
	 *
	 * @param sequenceArea the sequence area where {@code relativeY} belongs to
	 * @param relativeY the y coordinate relative to {@code sequenceArea}
	 * @return the y coordinate relative to the parent instance of {@link ToolkitSpecificAlignmentArea}
	 * @throws IllegalStateException if neither or Swing or a SWT component has been created for the specified sequence
	 *         area before the call of this method
	 */
	public double alignmentPartY(SequenceArea sequenceArea, double relativeY) {
		if (isUseSubcomponents()) {
			if (sequenceArea.hasComponent() && sequenceArea.getComponent().hasToolkitComponent()) {
				return sequenceArea.getComponent().getToolkitComponent().getLocationInParent().y + relativeY;  // SequenceAreas need to be direct children of the ToolkitSpecificAlignmentPartAreas for this method to work.
			}
			else {
				throw new IllegalStateException("No Swing or SWT component of the specified sequence area has yet been created.");
			}
		}
		else {
			throw new InternalError("Not implemented.");
			//TODO Support direct painting without subcomponents.
		}
	}
}
