package info.bioinfweb.libralign.demo.advancedpherogram.actions;


import info.bioinfweb.libralign.demo.advancedpherogram.Application;
import info.bioinfweb.libralign.pherogram.PherogramFormats.QualityOutputType;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;



public class DisplayQualityScoresAction extends AbstractAdvancedPherogramAction implements Action {
	public DisplayQualityScoresAction(Application app) {
		super(app);
		putValue(Action.NAME, "Select displayed quality scores..."); 
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		int selection = JOptionPane.showOptionDialog(getApplication().getFrame(), "Select quality scores to be displayed.", "Quality scores", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, 
				QualityOutputType.values(), getApplication().getPherogramDataArea().getFormats().getQualityOutputType());
		
		if (selection != JOptionPane.CLOSED_OPTION) {
			getApplication().getPherogramDataArea().getFormats().setQualityOutputType(QualityOutputType.values()[selection]);
		}
	}
}
