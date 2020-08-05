/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben St�ver
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
package info.bioinfweb.libralign.pherogram.model.undo;


import java.util.Collection;
import java.util.TreeMap;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.data.DataModel;
import info.bioinfweb.libralign.model.utils.AlignmentModelUtils;
import info.bioinfweb.libralign.pherogram.model.PherogramAlignmentRelation;
import info.bioinfweb.libralign.pherogram.model.PherogramAreaModel;
import info.bioinfweb.libralign.pherogram.model.PherogramComponentModel;
import info.bioinfweb.libralign.pherogram.model.PherogramModelListener;
import info.bioinfweb.libralign.pherogram.provider.PherogramProvider;



public class PherogramModelProviderChangeEdit <M extends AlignmentModel<T>, T, D extends PherogramComponentModel> extends PherogramModelEdit<M, T, D>{
	private PherogramProvider oldProvider;
	private PherogramProvider newProvider;
	private boolean reverseComplemented = false;
	private Collection<String> sequenceIDs;
	private TreeMap<String, Integer> sequenceLengthStorage = new TreeMap<>(); 
	private int firstColumn;
	private int lastColumn;
	
	
	public PherogramModelProviderChangeEdit(M alignmentModel, D pherogramModel, PherogramProvider oldProvider, PherogramProvider newProvider, boolean reverseComplemented, Collection<String> sequenceIDs) {
		super(alignmentModel, pherogramModel);
		this.newProvider = newProvider;
		this.oldProvider = oldProvider;
		this.reverseComplemented = reverseComplemented;
		this.sequenceIDs = sequenceIDs;
		
		if (reverseComplemented) {
			lastColumn = getAlignmentModel().getMaxSequenceLength() - 1;
			for (String sequenceID2 : sequenceIDs) {
				int diff = lastColumn + 1 - getAlignmentModel().getSequenceLength(sequenceID2);
				sequenceLengthStorage.put(sequenceID2, diff);
			}
		}
	}

	
	@Override
	public void redo() throws CannotRedoException {
		getPherogramModel().setProvider(newProvider);
		if (reverseComplemented) {
			reverseComplement();
		}
		super.redo();
	}

	
	@Override
	public void undo() throws CannotUndoException {
		getPherogramModel().setProvider(oldProvider);
		if (reverseComplemented) {
			reverseComplement();
			for (String sequenceID : sequenceIDs){
				int originalSequenceLength = lastColumn + 1 - sequenceLengthStorage.get(sequenceID);
				getAlignmentModel().removeTokensAt(sequenceID, originalSequenceLength, getAlignmentModel().getSequenceLength(sequenceID));	
			}
		}
		super.undo();
	}
	
	
	@Override
	public String getPresentationName() {
		return "PherogramProvider was changed to " + newProvider;
	}
	
	
	private void reverseComplement() {
	   	//SelectionModel selection = getReadsArea().getSelection();  
    	for (String sequenceID : sequenceIDs) {
    		PherogramAreaModel pherogramReference = getAlignmentModel().getDataModels().getSequenceList(sequenceID).getFirstOfType(PherogramAreaModel.class);
    		
    		int diff = sequenceLengthStorage.get(sequenceID);
    		for (int i = 0; i < diff; i++) {
    			AlignmentModel<Character> model = (AlignmentModel<Character>) getAlignmentModel();
    			model.appendToken(sequenceID, '-', true);
					//TODO Will the pherogram now be distorted, since interaction was recently moved to the models? This would have to be avoided. (Implementation of edits will anyway be refactored, though.)
    		}
    		
    		AlignmentModelUtils.reverseComplement(getAlignmentModel(), sequenceID, firstColumn, lastColumn + 1);
    		
    		if (pherogramReference != null){
    			 PherogramAlignmentRelation rightRelation = pherogramReference.editableIndexByBaseCallIndex(
    					 pherogramReference.getRightCutPosition());
    	            int rightBorder;
    	            if (rightRelation.getCorresponding() == PherogramAlignmentRelation.OUT_OF_RANGE) {
    	                rightBorder = rightRelation.getBeforeValidIndex() + 1;
    	            }
    	            else {
    	                rightBorder = rightRelation.getAfterValidIndex();
    	            }
    	            
    	            int shift = lastColumn-rightBorder;
    	            
    	            pherogramReference.reverseComplement(sequenceIDs);
    	            pherogramReference.setFirstSeqPos(shift+1);
    		}	
    		
		}

	}

}
