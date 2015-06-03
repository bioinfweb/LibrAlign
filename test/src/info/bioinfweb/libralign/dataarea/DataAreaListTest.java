/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben St�ver
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


import static org.junit.Assert.*;


import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.* ;



public class DataAreaListTest {
  private DataAreaList createList(AlignmentArea alignmentArea, final List<DataAreaChangeEvent> eventList, DataAreaListType type) {
  	DataAreaModel owner = new DataAreaModel(alignmentArea);
  	owner.addListener(new DataAreaModelListener() {
					@Override
					public void dataAreaVisibilityChanged(DataAreaChangeEvent e) {
						eventList.add(e);
					}
		
					@Override
					public void dataAreaInsertedRemoved(DataAreaChangeEvent e) {
						eventList.add(e);
					}
				});
  	return new DataAreaList(owner, type);
  }
  
  
  private DataArea createDataArea(AlignmentContentArea alignmentArea) {
  	return new DataArea(alignmentArea, alignmentArea.getOwner()) {
						@Override
						public void paint(TICPaintEvent event) {}
						
						@Override
						public int getLength() {
							return 1;
						}

						@Override
						public int getHeight() {
							return 1;
						}

						@Override
						public Set<DataAreaListType> validLocations() {
							return EnumSet.of(DataAreaListType.TOP, DataAreaListType.BOTTOM, DataAreaListType.SEQUENCE);
						}

						@Override
						public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {}

						@Override
						public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e) {}

						@Override
						public <T> void afterTokenChange(TokenChangeEvent<T> e) {}

						@Override
						public <T, U> void afterProviderChanged(AlignmentModel<T> previous,	AlignmentModel<U> current) {}
					};
		}
  
  
  private void printEventList(List<DataAreaChangeEvent> list) {
  	if (list.isEmpty()) {
  		System.out.println("Event list is empty.");
  	}
  	else {
  		Iterator<DataAreaChangeEvent> iterator = list.iterator();
    	while (iterator.hasNext()) {
    		DataAreaChangeEvent event = iterator.next();
    		System.out.println(event.getType() + " " + event.getAffectedElements());
    	}
  	}
  }
  
  
  @Test
  public void test_events() {
  	AlignmentArea alignmentArea = new AlignmentArea();
  	List<DataAreaChangeEvent> eventList = new ArrayList<DataAreaChangeEvent>();
  	DataAreaList areaList = createList(alignmentArea, eventList, DataAreaListType.TOP);
  	
  	areaList.add(createDataArea(alignmentArea.getContentArea()));
  	assertEquals(1, eventList.size());
  	eventList.clear();
  	
  	Collection<DataArea> severalAreas = new ArrayList<DataArea>(4);
  	for (int i = 0; i < 4; i++) {
    	severalAreas.add(createDataArea(alignmentArea.getContentArea()));
		}
  	areaList.addAll(severalAreas);
  	assertEquals(1, eventList.size());  // Checks if several events have been produced.
  	eventList.clear();
  	
  	areaList.retainAll(severalAreas);
  	//printEventList(eventList);
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
  	assertEquals(3, eventList.get(0).getAffectedElements().size());  // Check if all elements are contained in the list of the event.
  	eventList.clear();
  }
  
  
  @Test
  public void test_events_sublist() {
  	// Produces assertions as long as DataArea.subList() does not have a special implementation.
  	AlignmentArea alignmentArea = new AlignmentArea();
  	List<DataAreaChangeEvent> eventList = new ArrayList<DataAreaChangeEvent>();
  	DataAreaList areaList = createList(alignmentArea, eventList, DataAreaListType.TOP);
  	
  	Collection<DataArea> severalAreas = new ArrayList<DataArea>(4);
  	for (int i = 0; i < 4; i++) {
    	severalAreas.add(createDataArea(alignmentArea.getContentArea()));
		}
  	areaList.addAll(severalAreas);
  	eventList.clear();
  	
  	List<DataArea> subList = areaList.subList(1, 3);
  	subList.clear();
  	assertEquals(2, areaList.size());
  	assertEquals(1, eventList.size());
  	eventList.clear();
  }
}
