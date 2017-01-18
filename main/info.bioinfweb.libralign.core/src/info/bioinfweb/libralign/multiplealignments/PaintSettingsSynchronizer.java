/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.multiplealignments;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.paintsettings.PaintSettings;
import info.bioinfweb.libralign.alignmentarea.paintsettings.PaintSettingsEvent;
import info.bioinfweb.libralign.alignmentarea.paintsettings.PaintSettingsListener;
import info.bioinfweb.libralign.alignmentarea.paintsettings.TokenPainterReplacedEvent;
import info.bioinfweb.libralign.alignmentarea.paintsettings.ZoomChangeEvent;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.beanutils.BeanUtils;



/**
 * Class for internal use with {@link MultipleAlignmentsContainer} to synchronize single paint setting properties
 * between different alignment areas in a container. Application developers who want to define single paint 
 * settings as global must modify {@link MultipleAlignmentsContainer#getPaintSettingsToSynchronize()} instead of 
 * using this class directly.
 * <p>
 * This class does not synchronize any token painters. 
 * <p>
 * <b>Warning:</b> This class is for internal use in LibrAlign only and should not be referenced in application code
 * directly. API stability is not guaranteed for this class for any release of LibrAlign (no matter if the major version
 * number is increased or not). 
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 * @see PaintSettings
 * @see MultipleAlignmentsContainer#getPaintSettingsToSynchronize()
 */
class PaintSettingsSynchronizer implements PaintSettingsListener {
	private AlignmentAreaList owner;
	private Set<String> propertiesToSynchronizes = new TreeSet<String>();
	private boolean changeIsOngoing = false;
	
	
	public PaintSettingsSynchronizer(AlignmentAreaList owner) {
		super();
		this.owner = owner;
		addAllProperties();
	}


	public AlignmentAreaList getOwner() {
		return owner;
	}


	/**
	 * Application code should never call this method (or use this class) directly, but use 
	 * {@link MultipleAlignmentsContainer#getPaintSettingsToSynchronize()} instead.
	 * 
	 * @return a set of properties that are synchronized by this object.
	 */
	public Set<String> getPropertiesToSynchronizes() {
		return propertiesToSynchronizes;
	}
	
	
	/**
	 * Application code should never call this method (or use this class) directly, but use 
	 * {@link MultipleAlignmentsContainer#setAllPaintSettingPropertiesToSynchronize()} instead.
	 */
	public void addAllProperties() {
		getPropertiesToSynchronizes().add("cursorColor");
		getPropertiesToSynchronizes().add("cursorLineWidth");
		getPropertiesToSynchronizes().add("selectionColor");
		getPropertiesToSynchronizes().add("zoomX");
		getPropertiesToSynchronizes().add("zoomY");
		getPropertiesToSynchronizes().add("changeZoomXOnMouseWheel");
		getPropertiesToSynchronizes().add("changeZoomYOnMouseWheel");
	}
	
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (getPropertiesToSynchronizes().contains(event.getPropertyName()) && !changeIsOngoing) {
			changeIsOngoing = true;
			try {
				for (AlignmentArea area : getOwner()) {
					if (!event.getSource().equals(area.getPaintSettings())) {
						try {
							BeanUtils.setProperty(area.getPaintSettings(), event.getPropertyName(), event.getNewValue());
						} 
						catch (IllegalAccessException e) {
							throw new InternalError(e.getMessage());
						} 
						catch (InvocationTargetException e) {
							throw new InternalError(e.getMessage());
						}
					}
				}
			}
			finally {
				changeIsOngoing = false;
			}
		}
	}
	
	
	@Override
	public void zoomChange(ZoomChangeEvent event) {
		if ((getPropertiesToSynchronizes().contains("zoomX") || getPropertiesToSynchronizes().contains("zoomY"))
				&& !changeIsOngoing) {
			
			changeIsOngoing = true;
			try {
				for (AlignmentArea area : getOwner()) {
					if (!event.getSource().equals(area.getPaintSettings())) {
						PaintSettings settings = area.getPaintSettings();
						
						double zoomX = settings.getZoomX();
						if (getPropertiesToSynchronizes().contains("zoomX")) {
							zoomX = event.getNewZoomX();
						}
						double zoomY = settings.getZoomY();
						if (getPropertiesToSynchronizes().contains("zoomY")) {
							zoomY = event.getNewZoomY();
						}
						
						settings.setZoom(zoomX, zoomY);
					}
				}
				
				if (getPropertiesToSynchronizes().contains("zoomY")) {
					// Set size again, because global value might have increased with the last element.
					for (AlignmentArea area : getOwner()) {
						area.getLabelArea().assignSizeToAll();
					}
					
					getOwner().getOwner().redistributeHeight();
				}
			}
			finally {
				changeIsOngoing = false;
			}
		}
	}


	@Override
	public void tokenPainterReplaced(TokenPainterReplacedEvent event) {}

	
	@Override
	public void tokenPainterListChange(PaintSettingsEvent event) {}
}
