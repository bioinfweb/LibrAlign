/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bioinfweb.libralign.dataarea.implementations.charset;


import java.io.File;

import info.bioinfweb.jphyloio.EventForwarder;
import info.bioinfweb.jphyloio.formats.nexus.NexusCommandReaderFactory;
import info.bioinfweb.jphyloio.formats.nexus.NexusEventReader;

import org.junit.* ;

import static org.junit.Assert.* ;



public class CharSetEventReaderTest {
	public static void assertCharSet(int expectedStart, int expectedEnd, boolean expectedValue, CharSet set) {
		for (int i = expectedStart; i < expectedEnd; i++) {
			assertEquals(expectedValue, set.contains(i));
		}
	}
	
	
	@Test
	public void testReadingCharSets() {
		CharSetDataModel model = new CharSetDataModel();
		CharSetEventReader charSetReader = new CharSetEventReader(model);
		
		NexusCommandReaderFactory factory = new NexusCommandReaderFactory();
		factory.addJPhyloIOReaders();
		try {
			NexusEventReader reader = new NexusEventReader(new File("data/charSet/CharSet.nex"), false, factory);

			EventForwarder forwarder = new EventForwarder();
			forwarder.getListeners().add(charSetReader);
			forwarder.readAll(reader);
			
			CharSet set = model.getByName("set01");
			assertNotNull(set);
			assertCharSet(0, 3, false, set);
			assertCharSet(3, 6, true, set);
			assertCharSet(6, 9, false, set);
			assertCharSet(9, 10, true, set);
			assertCharSet(10, 12, false, set);
			assertCharSet(12, 17, true, set);
			assertCharSet(17, 200, false, set);
			
			set = model.getByName("set02");
			assertNotNull(set);
			assertCharSet(0, 4, false, set);
			assertCharSet(4, 10, true, set);
			assertCharSet(10, 200, false, set);
			
			set = model.getByName("set03");
			assertNotNull(set);
			assertCharSet(0, 4, false, set);
			assertCharSet(4, 7, true, set);
			assertCharSet(7, 12, false, set);
			assertCharSet(12, 13, true, set);
			assertCharSet(13, 200, false, set);
			
			set = model.getByName("set04");
			assertNotNull(set);
			assertCharSet(0, 4, false, set);
			assertCharSet(4, 7, true, set);
			assertCharSet(7, 12, false, set);
			assertCharSet(12, 13, true, set);
			assertCharSet(13, 200, false, set);
			
			set = model.getByName("set05");
			assertNotNull(set);
			assertCharSet(0, 3, false, set);
			assertCharSet(3, 4, true, set);
			assertCharSet(4, 5, false, set);
			assertCharSet(5, 8, true, set);
			assertCharSet(8, 9, false, set);
			assertCharSet(9, 10, true, set);
			assertCharSet(10, 11, false, set);
			assertCharSet(11, 13, true, set);
			assertCharSet(13, 200, false, set);
			
			set = model.getByName("set06");
			assertNotNull(set);
			assertCharSet(0, 3, false, set);
			assertCharSet(3, 4, true, set);
			assertCharSet(4, 200, false, set);
			
			assertEquals(6, model.size());  // Check that no additional sets are present.
		}
		catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		
	}
}
