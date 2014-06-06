/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.pherogram;


import java.awt.Dimension;

import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.SequenceColorSchema;
import info.bioinfweb.libralign.dataarea.PherogramArea;



/**
 * GUI component that allows displaying a pherogram (trace file from Sanger sequencing) which is not attached
 * to a sequence in an {@link AlignmentArea}.
 * <p>
 * Concrete GUI components for Swing or SWT can be created using {@link #createSwingComponent()} or 
 * {@link #createSWTWidget(org.eclipse.swt.widgets.Composite, int)} as this class is a TIC component.
 * 
 * @author Ben St&ouml;ver
 * @see PherogramArea
 */
public class PherogramView extends TICComponent implements PherogramComponent {
	private PherogramProvider pherogram; 
	private double verticalScale = 100f;
	private SequenceColorSchema colorSchema = new SequenceColorSchema();
	private PherogramPainter painter = new PherogramPainter(this);
	
	
	@Override
	public PherogramProvider getProvider() {
		return pherogram;
	}

	
	public void setProvider(PherogramProvider pherogram) {
		this.pherogram = pherogram;
	}


	@Override
	public double getVerticalScale() {
		return verticalScale;
	}

	
	@Override
	public void setVerticalScale(double value) {
		verticalScale = value;
	}


	@Override
	public SequenceColorSchema getColorSchema() {
		return colorSchema;
	}


	public void setColorSchema(SequenceColorSchema colorSchema) {
		this.colorSchema = colorSchema;
	}


	@Override
	public Dimension getSize() {
		return new Dimension(300, 100);  //TODO Return size depending on the pherogram
	}


	@Override
	public void paint(TICPaintEvent e) {
		painter.paintUnscaledTraceCurves(0, 300, e.getGraphics(), 0, 0, 1.0);  //TODO Provide correct x bounds depending on the position of the pherogram in the component and the area that needs to be repainted.
	}
}
