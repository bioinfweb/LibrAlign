/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
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
package info.bioinfweb.libralign.selection;


import info.bioinfweb.commons.tic.input.TICMouseAdapter;
import info.bioinfweb.commons.tic.input.TICMouseEvent;
import info.bioinfweb.commons.tic.input.TICMouseListener;
import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.alignmentareacomponents.SequenceArea;



public class SelectionInputListener extends TICMouseAdapter implements TICMouseListener {
	private AlignmentArea owner;
	
	
	public SelectionInputListener(AlignmentArea owner) {
		super();
		this.owner = owner;
	}


	public AlignmentArea getOwner() {
		return owner;
	}


	@Override
	public void mousePressed(TICMouseEvent event) {
		if ((event.isMouseButton1Down()) && (event.getSource() instanceof SequenceArea)) {
			SequenceArea source = (SequenceArea)event.getSource();
			SelectionModel selection = getOwner().getSelection();
			selection.getColumnSelection().setNewSelection(getOwner().columnByPaintX(event.getComponentX()));
			selection.getRowSelection().setNewSelection(getOwner().rowByPaintY(
					getOwner().alignmentPartY(source, event.getComponentY())));
		}
		else if (event.isMouseButton3Down()) {
			getOwner().getSelection().clear();
		}
	}
	

	@Override
	public void mouseDragged(TICMouseEvent event) {
		if ((event.isMouseButton1Down()) && (event.getSource() instanceof SequenceArea)) {
			SequenceArea source = (SequenceArea)event.getSource();
			SelectionModel selection = getOwner().getSelection();
			selection.getColumnSelection().extendSelectionTo(getOwner().columnByPaintX(event.getComponentX()));
			selection.getRowSelection().extendSelectionTo(getOwner().rowByPaintY(
					getOwner().alignmentPartY(source, event.getComponentY())));
		}
	}
}
