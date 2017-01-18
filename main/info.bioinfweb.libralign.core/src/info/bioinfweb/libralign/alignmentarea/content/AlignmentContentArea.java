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


import info.bioinfweb.commons.SystemUtils;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.ToolkitSpecificAlignmentArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelArea;
import info.bioinfweb.libralign.alignmentarea.paintsettings.PaintSettings;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionModel;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;
import info.bioinfweb.libralign.dataarea.DataAreaLocation;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;
import info.bioinfweb.tic.TICComponent;
import info.bioinfweb.tic.TICPaintEvent;
import info.bioinfweb.tic.input.TICMouseWheelEvent;
import info.bioinfweb.tic.input.TICMouseWheelListener;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;



/**
 * Toolkit independent component displaying the contents (not the labels) of a single alignment and its attached
 * data areas.
 * <p>
 * This component is used a child component of {@link AlignmentArea} and in contrast to {@link AlignmentArea}
 * does not contain a scroll container, but acts as the scrolled component.
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
	private Map<KeyStroke, Action> actionMap = new HashMap<KeyStroke, Action>();


	/**
	 * Creates a new instance of this class.
	 * <p>
	 * If {@code code} is part of a {@link MultipleAlignmentsContainer} the shared edit settings of this container
	 * will automatically be used by the returned instance.
	 *
	 * @param owner - the alignment area component that will be containing the return instance
	 */
	public AlignmentContentArea(AlignmentArea owner) {
		super();
		this.owner = owner;
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


	public Map<KeyStroke, Action> getActionMap() {
		return actionMap;
	}


	/**
	 * Returns the rectangle in the paint coordinate system of scrolled area displaying the sequences, that contains
	 * all cells currently occupied by the alignment cursor.
	 * <p>
	 * If the last row has associated data areas, the height of these areas is also included in the rectangle.
	 *
	 * @return a rectangle with paint coordinates
	 */
	public Rectangle getCursorRectangle() {
		SelectionModel selection = getOwner().getSelection();
		int y = paintYByRow(selection.getCursorRow());
		int width = 0;
		if (selection.getCursorColumn() < getOwner().getAlignmentModel().getMaxSequenceLength()) {  // There is no more token behind the cursor, if it is located at the right end of the alignment.
			width = (int)Math.round(getOwner().getPaintSettings().getTokenWidth(selection.getCursorColumn()));
		}
		Rectangle result = new Rectangle(paintXByColumn(selection.getCursorColumn()), y, width,
				paintYByRow(selection.getCursorRow() + selection.getCursorHeight()) - y);
		if (selection.getCursorRow() + selection.getCursorHeight() - 1 ==
				getOwner().getAlignmentModel().getSequenceCount() - 1) {

			result.height += getOwner().getPaintSettings().getTokenHeight();  // Add height of the last row, because the return value of paintYByRow(maxIndex + 1) is equal to paintYByRow(maxIndex).
		}
		return result;
	}


	@Override
	public Dimension getSize() {
		Dimension result = new Dimension(getOwner().getGlobalMaxNeededWidth(), getOwner().getDataAreas().getVisibleAreaHeight());
		if (getOwner().hasAlignmentModel()) {
			result.height += getOwner().getAlignmentModel().getSequenceCount() * getOwner().getPaintSettings().getTokenHeight();
		}
		return result;
	}


	@Override
	public void paint(TICPaintEvent event) {}  // Remains empty because toolkit specific components are provided.


	@Override
	protected String getSwingComponentClassName() {
		return "info.bioinfweb.libralign.alignmentarea.content.SwingAlignmentContentArea";
	}


	@Override
	protected String getSWTComponentClassName() {
		return "info.bioinfweb.libralign.alignmentarea.content.SWTAlignmentContentArea";
	}


	@Override
	public ToolkitSpecificAlignmentContentArea getToolkitComponent() {
		return (ToolkitSpecificAlignmentContentArea)super.getToolkitComponent();
	}


	/**
	 * Returns the column containing the specified x coordinate. If the coordinate lies behind the last column,
	 * the number of columns + 1 is returned.
	 *
	 * @param x - the paint coordinate
	 * @return the alignment column
	 */
	public int columnByPaintX(int x) {
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
	 * @param column - the column painted at the returned x-position
	 * @return a value >= 0
	 */
	public int paintXByColumn(int column) {
	  if (getOwner().hasAlignmentModel()) {
  		if (getOwner().getAlignmentModel() instanceof ConcatenatedAlignmentModel) {
  			throw new InternalError("not implemented");  //TODO Implement and consider that different alignment parts may have different token widths here.
  		}
  		else {
  			return (int)Math.round(column *
  					getOwner().getPaintSettings().getTokenPainterList().painterByColumn(0).getPreferredWidth() *
  					getOwner().getPaintSettings().getZoomX()) +  getOwner().getDataAreas().getGlobalMaxLengthBeforeStart();  //TODO Catch IllegalStateException?
  		}
	  }
	  else {
	    throw new IllegalStateException("Column dependent paint positions can only be calculated if an alignment model is defined.");
	  }
	}


	/**
	 * Returns the row index of the sequence displayed at the specified y coordinate considering the current order
	 * of sequences. If a data area is displayed at the specified position, the row of the associated sequence is returned
	 * anyway.
	 *
	 * @param y - the y coordinate relative to the alignment part area containing the sequence areas
	 * @return a valid sequence position.
	 */
	public int rowByPaintY(int y) {
		AlignmentSubArea subArea = getToolkitComponent().getAreaByY(y);
		if (subArea instanceof DataArea) {
			DataAreaLocation location = ((DataArea)subArea).getList().getLocation();
			if (location.getListType().equals(DataAreaListType.SEQUENCE)) {
				subArea = getToolkitComponent().getSequenceAreaByID(location.getSequenceID());
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
	 * @param row - the row painted at the returned x-position
	 * @return a value >= 0
	 */
	public int paintYByRow(int row) {
		row = Math.max(0, Math.min(getOwner().getAlignmentModel().getSequenceCount() - 1, row));
    return getToolkitComponent().getSequenceAreaByID(getOwner().getSequenceOrder().idByIndex(row)).
    		getToolkitComponent().getLocationInParent().y;
	}


	/**
	 * Calculates the y coordinate relative to the alignment content area, which contains the specified sequence area.
	 *
	 * @param sequenceArea - the sequence area where {@code relativeY} belongs to
	 * @param relativeY - the y coordinate relative to {@code sequenceArea}
	 * @return the y coordinate relative to the parent instance of {@link ToolkitSpecificAlignmentArea}
	 * @throws IllegalStateException if neither or Swing or a SWT component has been created for the specified sequence
	 *         area before the call of this method
	 */
	public int alignmentPartY(SequenceArea sequenceArea, int relativeY) {
		if (sequenceArea.hasToolkitComponent()) {
			return sequenceArea.getToolkitComponent().getLocationInParent().y + relativeY;  // SequenceAreas need to be direct children of the ToolkitSpecificAlignmentPartAreas for this method to work.
		}
		else {
			throw new IllegalStateException("No Swing or SWT component of the specified sequence area has yet been created.");
		}
	}
}
