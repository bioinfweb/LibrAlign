package info.bioinfweb.libralign.demo.advancedpherogram.actions;

import java.awt.event.ActionEvent;

import info.bioinfweb.libralign.demo.advancedpherogram.Application;
import info.bioinfweb.libralign.pherogram.PherogramFormats;

import javax.swing.Action;

public class SwitchLeftRightInsertionAction extends AbstractAdvancedPherogramAction implements Action{

	public SwitchLeftRightInsertionAction(Application app) {
		super(app);
		putValue(Action.NAME, "Switch Left/Right Insertion"); 
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	getApplication().getAlignmentArea().getEditSettings().toggleInsertLeftInDataArea();
		
		
	}

}
