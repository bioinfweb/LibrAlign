package info.bioinfweb.libralign.demo.advancedpherogram.actions;

import java.awt.event.ActionEvent;

import info.bioinfweb.libralign.demo.advancedpherogram.Application;

import javax.swing.Action;
import javax.swing.JOptionPane;

public class CutRightAction extends AbstractAdvancedPherogramAction implements Action {

	public CutRightAction(Application app) {
		super(app);
		putValue(Action.NAME, "Set right cut position"); 
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		try {
			getApplication().getPherogramDataArea().setRightCutPositionBySelection();
		}
		
		catch (IndexOutOfBoundsException e) {
			JOptionPane.showMessageDialog(getApplication().getFrame(), e.getLocalizedMessage());
		}
	}
}
