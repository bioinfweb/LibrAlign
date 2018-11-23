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
		//boolean result = getApplication().getPherogramDataArea().setRightCutPositionBySelection();
		JOptionPane jpane = new JOptionPane();
		if (getApplication().getPherogramDataArea().setRightCutPositionBySelection() == false)
		{
			//does not work, only throws exception, never shows dialog.
			JOptionPane.showMessageDialog(getApplication().getFrame(),"The right cut positon must not be before the left cut postion." );
		}
		
	}

}
