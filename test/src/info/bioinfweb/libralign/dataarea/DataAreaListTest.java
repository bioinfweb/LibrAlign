/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.dataarea;


import static org.junit.Assert.* ;


import info.bioinfweb.libralign.gui.LibrAlignPaintEvent;
import info.webinsel.util.graphics.DoubleDimension;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.* ;



public class DataAreaListTest {
  private DataAreaList createList(final List<DataAreaChangeEvent> eventList, DataAreaListType type) {
  	DataAreaModel owner = new DataAreaModel();
  	owner.addListener(new DataAreaModelListener() {
					@Override
					public void dataAreaVisibilityChanged(DataAreaChangeEvent e) {
						eventList.add(e);
					}
		
					@Override
					public void dataAreaModelInsertedRemoved(DataAreaChangeEvent e) {
						eventList.add(e);
					}
				});
  	return new DataAreaList(owner, type);
  }
  
  
  private DataArea createDataArea() {
  	return new AbstractDataArea() {
						@Override
						public void paint(LibrAlignPaintEvent event) {}
						
						@Override
						public Dimension2D getSize() {
							return new DoubleDimension(1, 1);
						}
					};
		}
  
  
  @Test
  public void test_events() {
  	List<DataAreaChangeEvent> eventList = new ArrayList<DataAreaChangeEvent>();
  	DataAreaList areaList = createList(eventList, DataAreaListType.TOP);
  	
  	areaList.add(createDataArea());
  	assertEquals(1, eventList.size());
  	eventList.clear();
  	
  	Collection<DataArea> severalAreas = new ArrayList<DataArea>(4);
  	for (int i = 0; i < 4; i++) {
    	severalAreas.add(createDataArea());
		}
  	areaList.addAll(severalAreas);
  	assertEquals(1, eventList.size());  // Checks if several events have been produced.
  	eventList.clear();
  	
  	areaList.retainAll(severalAreas);
  	assertEquals(1, eventList.size());
  	eventList.clear();
  	
  	areaList.remove(1);
  	assertEquals(1, eventList.size());
  	eventList.clear();
  	
  	areaList.removeAll(severalAreas);
  	assertEquals(1, eventList.size());
  	eventList.clear();
  	
  	areaList.addAll(severalAreas);
  	eventList.clear();  // remove addAll event
  	Iterator<DataArea> iterator = areaList.iterator();
  	iterator.next();
  	iterator.next();
  	iterator.remove();
  	assertEquals(3, areaList.size());
  	assertEquals(1, eventList.size());
  	eventList.clear();
  	
  	areaList.clear();
  	assertEquals(1, eventList.size());
  	eventList.clear();
  }
}
