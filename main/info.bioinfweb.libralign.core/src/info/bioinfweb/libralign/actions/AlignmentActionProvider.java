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
package info.bioinfweb.libralign.actions;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionModel;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.exception.AlignmentSourceNotWritableException;

import java.util.ArrayList;
import java.util.Collection;



/**
 * This class provides the business logic for user actions triggered through the view {@link AlignmentContentArea}
 * to manipulate the associated models {@link AlignmentModel} and {@link SelectionModel}.
 *
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public class AlignmentActionProvider<T> {
	private final AlignmentArea alignmentArea;


//* Otherwise a {@link ClassCastException} will be thrown by all operation
//* methods. If the sequence data provider of {@code view} is changed during runtime to a provider with a different
//* token type a new instance of this class with the correct token type needs to created.
//TODO Stimmt das oder ist das zur Laufzeit egal, da generics dann entfernt sind? Welche Sinn macht es generell hier
//     Generics zu verwenden und bei AlignmentContentArea nicht?
	/**
	 * Creates a new instance of this class that is associated with the specified view and model.
	 * <p>
	 * Note that the token type of the {@link AlignmentModel} associated with {@code view} and the token type
	 * {@code T} if this instance must match.
	 *
	 * @param view - the alignment content area from which the user operations will be triggered (The sequence data
	 *        provider, the selection model and the sequence order this view provides will be used for all operations.)
	 */
	public AlignmentActionProvider(AlignmentArea alignmentArea) {
		super();
		this.alignmentArea = alignmentArea;
	}


	/**
	 * Returns the view this instance is associated with.
	 *
	 * @return the linked view instance
	 */
	public AlignmentArea getAlignmentArea() {
		return alignmentArea;
	}


	public AlignmentModel<T> getModel() {
		return (AlignmentModel<T>)getAlignmentArea().getAlignmentModel();
	}


	/**
	 * Deletes the currently selected tokens.
	 * <p>
	 * This method is called internally by {@link #deleteBackwards()} and {@link #deleteForward()} if the selection
	 * is not empty.
	 */
	public void deleteSelection() {
		SelectionModel selection = getAlignmentArea().getSelection();
		if (!selection.isEmpty()) {
			for (int row = selection.getCursorRow(); row < selection.getCursorRow() + selection.getCursorHeight(); row++) {
				String sequenceID = getAlignmentArea().getSequenceOrder().idByIndex(row);
				int sequenceLength = getModel().getSequenceLength(sequenceID);
				if (selection.getFirstColumn() < sequenceLength) {
					getModel().removeTokensAt(sequenceID,
							selection.getFirstColumn(), Math.min(sequenceLength, selection.getFirstColumn() + selection.getWidth()));
				}
			}
			selection.setNewCursorColumn(selection.getFirstColumn());
		}
	}


	/**
	 * Removes the currently selected tokens from the underlying {@link AlignmentModel}. If no tokens
	 * are selected the token right of the cursor is removed from each sequence the cursor spans to.
	 * <p>
	 * Most users would probably expect this operation to be executed when the delete button is pressed.
	 * <p>
	 * Note that calling this method will have no effect if the underlying {@link AlignmentModel} is
	 * not writable.
	 *
	 * @return {@code true} if the underlying data source was changed as a result of this operation,
	 *         {@code false} otherwise
	 */
	public boolean deleteForward() {
		boolean result = false;
		SelectionModel selection = getAlignmentArea().getSelection();
		try {
			if (selection.isEmpty()) {
				for (int row = selection.getCursorRow(); row < selection.getCursorRow() + selection.getCursorHeight(); row++) {
					String sequenceID = getAlignmentArea().getSequenceOrder().idByIndex(row);
					if (selection.getCursorColumn() < getModel().getSequenceLength(sequenceID)) {
						getModel().removeTokenAt(sequenceID, selection.getCursorColumn());
						result = true;
				  }
				}
			}
			else {
				deleteSelection();
				result = true;
			}
		}
		catch (AlignmentSourceNotWritableException e) {}  // Nothing to do, since result is false by default.
		return result;
	}


	/**
	 * Removes the currently selected tokens from the underlying {@link AlignmentModel}. If no tokens
	 * are selected the token left of the cursor is removed from each sequence the cursor spans to.
	 * <p>
	 * Most users would probably expect this operation to be executed when the backspace button is pressed.
	 * <p>
	 * Note that calling this method will have no effect if the underlying {@link AlignmentModel} is
	 * not writable.
	 *
	 * @return {@code true} if the underlying data source was changed as a result of this operation,
	 *         {@code false} otherwise
	 */
	public boolean deleteBackwards() {
		boolean result = false;
		SelectionModel selection = getAlignmentArea().getSelection();
		try {
			if (selection.isEmpty()) {
				if (selection.getCursorColumn() > 0) {
					for (int row = selection.getCursorRow(); row < selection.getCursorRow() + selection.getCursorHeight(); row++) {
						String sequenceID = getAlignmentArea().getSequenceOrder().idByIndex(row);
						if (selection.getCursorColumn() < getModel().getSequenceLength(sequenceID)) {
							getModel().removeTokenAt(sequenceID, selection.getCursorColumn() - 1);
							result = true;
						}
					}
					selection.setNewCursorColumn(selection.getCursorColumn() - 1);  // Move cursor backwards
				}
			}
			else {
				deleteSelection();
				result = true;
			}
		}
		catch (AlignmentSourceNotWritableException e) {}  // Nothing to do, since result is false by default.
		return result;
	}
	
	
	/**
	 * Inserts trailing gaps into a sequence until it has the specified length.
	 * 
	 * @param sequenceID the ID of the sequence to be elongated
	 * @param newLength the new length the sequence shall have
	 * @return the number of trailing gaps that have been inserted
	 */
	public int elongateSequence(String sequenceID, int newLength) {
		int additionalLength = newLength - getModel().getSequenceLength(sequenceID);
		if (additionalLength > 0) {
			T gapToken = getModel().getTokenSet().getGapToken();
			Collection<T> tokens = new ArrayList<T>(additionalLength);
			for (int i = 0; i < additionalLength; i++) {
				tokens.add(gapToken);
			}
			
			getModel().appendTokens(sequenceID, tokens);
			return additionalLength;
		}
		else {
			return 0;
		}
	}


	/**
	 * Inserts the specified token at the current cursor position specified by {@code selection}. If the cursor
	 * spans over multiple rows the token is inserted into each row. If other tokens are currently selected the
	 * specified token will be inserted as often as the length of the selection.
	 * <p>
	 * Note that no deep copy of the specified token will be used, if it is inserted into several rows. Generally
	 * implementations of {@link AlignmentModel} do not necessarily store one object per position in a sequence,
	 * but equal objects can also be mapped to the token set to save memory.
	 * <p>
	 * Note that calling this method will have no effect if the underlying {@link AlignmentModel} is
	 * not writable.
	 *
	 * @param token - the token to be inserted
	 * @return {@code true} if the underlying data source was changed as a result of this operation,
	 *         {@code false} otherwise
	 */
	public boolean insertToken(T token) {
		SelectionModel selection = getAlignmentArea().getSelection();
		try {
			// Create token list:
			int tokenCount = Math.max(1, selection.getWidth());
			Collection<T> tokens = new ArrayList<T>(tokenCount);
			for (int i = 0; i < tokenCount; i++) {
				tokens.add(token);
			}

			for (int row = selection.getCursorRow(); row < selection.getCursorRow() + selection.getCursorHeight(); row++) {
				String sequenceID = getAlignmentArea().getSequenceOrder().idByIndex(row);
				elongateSequence(sequenceID, selection.getFirstColumn());
				getModel().insertTokensAt(sequenceID,	selection.getFirstColumn(), tokens);
			}
			selection.setNewCursorColumn(selection.getFirstColumn() + tokenCount);  // Move cursor forward
		}
		catch (AlignmentSourceNotWritableException e) {
			return false;
		}
		return true;
	}


	/**
	 * Overwrites the currently selected token(s) in each selected sequence with the specified token.
	 * If no token is selected the token(s) right of the cursor are replaced.
	 * <p>
	 * If more than one token in each sequence is selected, the first one will be replaced by
	 * {@link AlignmentModel#setTokenAt(int, int, Object)} and the other will be deleted using
	 * {@link AlignmentModel#removeTokensAt(int, int, int)}.
	 * <p>
	 * Note that calling this method will have no effect if the underlying {@link AlignmentModel} is
	 * not writable.
	 *
	 * @param token - the new token to replace the other(s)
	 * @return {@code true} if the underlying data source was changed as a result of this operation,
	 *         {@code false} otherwise
	 */
	public boolean overwriteWithToken(T token) {
		SelectionModel selection = getAlignmentArea().getSelection();
		try {
			for (int row = selection.getCursorRow(); row < selection.getCursorRow() + selection.getCursorHeight(); row++) {
				String sequenceID = getAlignmentArea().getSequenceOrder().idByIndex(row);
				
				// Remove possible additional tokens:
				int sequenceLength = getModel().getSequenceLength(sequenceID);
				if ((selection.getWidth() > 1) && (sequenceLength > selection.getFirstColumn())) {
					getModel().removeTokensAt(sequenceID,	selection.getFirstColumn() + 1, 
							Math.min(sequenceLength, selection.getFirstColumn() + selection.getWidth()));
				}

				// Overwrite first token:
				elongateSequence(sequenceID, selection.getFirstColumn() + 1);
				getModel().setTokenAt(sequenceID,	selection.getFirstColumn(), token);
			}
			selection.setNewCursorColumn(selection.getFirstColumn() + 1);  // Move cursor forward
		}
		catch (AlignmentSourceNotWritableException e) {
			return false;
		}
		return true;
	}
}
