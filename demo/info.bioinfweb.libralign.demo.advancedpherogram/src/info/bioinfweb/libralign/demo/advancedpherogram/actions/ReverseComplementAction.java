package info.bioinfweb.libralign.demo.advancedpherogram.actions;

import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;
import info.bioinfweb.libralign.demo.advancedpherogram.Application;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.utils.AlignmentModelUtils;
import info.bioinfweb.libralign.pherogram.model.PherogramAlignmentRelation;
import info.bioinfweb.libralign.pherogram.model.PherogramAreaModel;

import java.awt.event.ActionEvent;

import javax.swing.Action;

public class ReverseComplementAction extends AbstractAdvancedPherogramAction implements Action {

	public ReverseComplementAction(Application app) {
		super(app);
		putValue(Action.NAME, "Reverse complement");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//Does not work
		 AlignmentModel<?> alignmentModel = getApplication().getAlignmentArea().getAlignmentModel();
		 PherogramArea area = getApplication().getPherogramDataArea();
		 PherogramAreaModel pherogramAlignmentModel = area.getModel();
		 String sequenceID = getApplication().getAlignmentArea().getSequenceOrder().idByIndex(0);
		 int sequenceLength =  getApplication().getAlignmentArea().getAlignmentModel().getSequenceLength(sequenceID);
		 PherogramAlignmentRelation leftRelation = pherogramAlignmentModel.editableIndexByBaseCallIndex(
				 pherogramAlignmentModel.getLeftCutPosition());
		         int leftBorder;
		          if (leftRelation.getCorresponding() == PherogramAlignmentRelation.OUT_OF_RANGE) {
		        	  	leftBorder = leftRelation.getBeforeValidIndex() + 1;
		           }
		          else {
		        	  leftBorder = leftRelation.getAfterValidIndex();
		          }
		          System.out.println(leftBorder);
		          
		  
		        //pherogramAlignmentModel.getPherogramProvider().getBaseCallPosition(baseIndex) 
		        // sowas wie : wenn getCharAt (i) von der Pherogram Area nicht getCharAt(i) von dem anderen entspricht, dann
		        // muss das irgendwie zugeordnet werden.
		          
		        //pherogramAlignmentModel.editableIndexByBaseCallIndex(0); 
		        //Returns the index in the editable alignment sequence that corresponds to the specified index in the base call sequence
		        //Das heißt, wenn die nicht gleich sind, dann müssen sie gleich gesetzt werden.
		    
		       //pherogramAlignmentModel.setShiftChange(0, (leftBorder));
		     
		        
		        pherogramAlignmentModel.setFirstSeqPos(0);
		        //pherogramAlignmentModel.deleteCutOffDistortions();
		          
		 	
		
		 pherogramAlignmentModel.reverseComplement();
		 AlignmentModelUtils.reverseComplement(alignmentModel, sequenceID); //idByIndex(0), because there is only one sequence in this demo application.
		 
	}

}
