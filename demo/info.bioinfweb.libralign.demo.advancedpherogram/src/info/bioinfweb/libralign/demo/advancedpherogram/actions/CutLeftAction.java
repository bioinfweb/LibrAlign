package info.bioinfweb.libralign.demo.advancedpherogram.actions;


import java.awt.event.ActionEvent;

import info.bioinfweb.libralign.demo.advancedpherogram.Application;

import javax.swing.Action;
import javax.swing.JOptionPane;



public class CutLeftAction extends AbstractAdvancedPherogramAction implements Action{
	public CutLeftAction(Application app) {
		super(app);
		putValue(Action.NAME, "Set left cut position"); 
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		boolean result = getApplication().getPherogramDataArea().setLeftCutPositionBySelection();
		if (result == false)
		{
			JOptionPane jpane = new JOptionPane("The left cut position must not be behind the right cut position");
		}
	}
}
