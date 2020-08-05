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
package info.bioinfweb.libralign.model.undo;


import java.util.Collection;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.libralign.model.AlignmentModel;



/**
 * An edit object that combines a set of simple {@link AbstractAlignmentModelEdit}s and {@link AbstractDataModelEdit}s to one logical edit step that can be undone
 * or redone by a user of a <i>LibrAlign</i>-based application.
 * 
 * @author Ben St&ouml;ver
 * @since 0.10.0
 *
 * @param <M> the alignment model that is edited by this instance
 */
public class CombinedEdit<M extends AlignmentModel<T>, T> extends AbstractAlignmentModelEdit<M, T> {
	private String presentationName;
	private Collection<AbstractAlignmentModelEdit<M, T>> subedits;
	
	
	public CombinedEdit(M alignmentModel, String presentationName, Collection<AbstractAlignmentModelEdit<M, T>> subedits) {
		super(alignmentModel);
		this.presentationName = presentationName;
		this.subedits = subedits;
		setIsSubedit(false);
	}


	@Override
	public void redo() throws CannotRedoException {
		subedits.forEach(edit -> edit.redo());
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		subedits.forEach(edit -> edit.undo());
		super.undo();
	}


	@Override
	public String getPresentationName() {
		return presentationName;
	}
}
