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

		 AlignmentModel<?> alignmentModel = getApplication().getAlignmentArea().getAlignmentModel();
		 PherogramArea area = getApplication().getPherogramDataArea();
		 PherogramAreaModel pherogramAlignmentModel = area.getModel();
		 String sequenceID = getApplication().getAlignmentArea().getSequenceOrder().idByIndex(0);

		 PherogramAlignmentRelation rightRelation = pherogramAlignmentModel.editableIndexByBaseCallIndex(
				 pherogramAlignmentModel.getRightCutPosition());
		         int rightBorder;
		          if (rightRelation.getCorresponding() == PherogramAlignmentRelation.OUT_OF_RANGE) {
		        	  	rightBorder = rightRelation.getBeforeValidIndex() + 1;
		           }
		          else {
		        	  rightBorder = rightRelation.getAfterValidIndex();
		          }
		          
		 AlignmentModelUtils.reverseComplement(alignmentModel, sequenceID, pherogramAlignmentModel.editableIndexByBaseCallIndex(pherogramAlignmentModel.getLeftCutPosition()).getBeforeValidIndex(),rightBorder);
		 pherogramAlignmentModel.reverseComplement();
	}

}
