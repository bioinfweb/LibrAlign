/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2016  Ben Stöver
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
package info.bioinfweb.libralign.alignmentarea.paintsettings;



public class ZoomChangeEvent extends PaintSettingsEvent {
	private double oldZoomX;
	private double newZoomX;
	private double oldZoomY;
	private double newZoomY;
	
	
	public ZoomChangeEvent(PaintSettings source, double oldZoomX,	double newZoomX, double oldZoomY, double newZoomY) {
		super(source);
		this.oldZoomX = oldZoomX;
		this.newZoomX = newZoomX;
		this.oldZoomY = oldZoomY;
		this.newZoomY = newZoomY;
	}


	public double getOldZoomX() {
		return oldZoomX;
	}


	public double getNewZoomX() {
		return newZoomX;
	}
	
	
	public boolean isZoomXChange() {
		return oldZoomX != newZoomX;
	}


	public double getOldZoomY() {
		return oldZoomY;
	}


	public double getNewZoomY() {
		return newZoomY;
	}
	
	
	public boolean isZoomYChange() {
		return oldZoomY != newZoomY;
	}
}
