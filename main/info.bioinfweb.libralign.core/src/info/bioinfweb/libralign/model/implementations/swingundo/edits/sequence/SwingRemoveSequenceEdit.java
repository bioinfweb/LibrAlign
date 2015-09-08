/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bioinfweb.libralign.model.implementations.swingundo.edits.sequence;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.swingundo.SwingUndoAlignmentModel;



/**
 * Edit object that removes a sequence from an instance of {@link AlignmentModel}.
 * 
 * @author Ben St&oml;ver
 * @since 0.1.0
 * @see SwingUndoAlignmentModel
 */
public class SwingRemoveSequenceEdit<T> extends SwingSequenceEdit<T> {
	private String name;
	private Collection tokens;  // Raw type is necessary here, because the generic element type of the provider can't be determined during runtime and the class of the first token might be a subtype of the actual type.
	

	public SwingRemoveSequenceEdit(SwingUndoAlignmentModel<T> provider, int sequenceID) {
		super(provider, sequenceID);
		
		name = getProvider().sequenceNameByID(sequenceID);
		int length = getProvider().getSequenceLength(sequenceID);
		tokens = new ArrayList<Object>(length);
		for (int i = 0; i < length; i++) {
			tokens.add(getProvider().getTokenAt(sequenceID, i));
		}
	}


	@Override
	public void redo() throws CannotRedoException {
		getProvider().getUnderlyingModel().removeSequence(sequenceID);
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		getProvider().getUnderlyingModel().addSequence(name);
		getProvider().getUnderlyingModel().insertTokensAt(sequenceID, 0, tokens);		
		super.undo();
	}


	@Override
	public String getPresentationName() {
		return "Remove sequence \"" + name + "\"";
	}
}
