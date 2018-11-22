package info.bioinfweb.libralign.demo.advancedpherogram.actions;


import info.bioinfweb.libralign.demo.advancedpherogram.Application;
import info.bioinfweb.libralign.pherogram.PherogramFormats;

import java.awt.event.ActionEvent;

import javax.swing.Action;



public class DisplayProbabilityValuesAction extends AbstractAdvancedPherogramAction implements Action {
	public DisplayProbabilityValuesAction(Application app) {
		super(app);
		putValue(Action.NAME, "Show/hide probability values"); 
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		PherogramFormats f = getApplication().getPherogramDataArea().getFormats(); 
		f.toggleShowProbabilityValues();
	}
}
