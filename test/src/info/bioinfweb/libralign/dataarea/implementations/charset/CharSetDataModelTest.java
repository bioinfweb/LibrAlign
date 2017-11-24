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
package info.bioinfweb.libralign.dataarea.implementations.charset;

import java.awt.Color;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;



public class CharSetDataModelTest {
	public static void main(String[] args) {
		// Test how methods delegate to each other. (Respective outputs must be present in CharSetDataModel.)
		
		CharSetDataModel model = new CharSetDataModel();
		
		System.out.println("Put by index:");
		model.put(0, "id0", new CharSet("A", Color.BLUE));
		model.valueList().remove(0);
		System.out.println();
		
		System.out.println("Put:");
		model.put("id1", new CharSet("B", Color.YELLOW));
		model.remove(0);
		System.out.println();
		
		System.out.println("Put and clear:");
		model.put("id2", new CharSet("C", Color.RED));
		model.clear();
		System.out.println();
		
		System.out.println("PutAll:");
		Map<String, CharSet> map = new HashedMap<>();
		map.put("id3", new CharSet("D", Color.BLACK));
		map.put("id4", new CharSet("E", Color.BLACK));
		model.putAll(map);
		System.out.println();
		
		System.out.println("Replace:");
		model.replace("id3", new CharSet("F", Color.BLACK));
		System.out.println();
		
		System.out.println("Clear value sublist:");
		model.valueList().subList(0, 1).clear();  // Also delegetes to remove().
		System.out.println();
		
		System.out.println("Clear value list:");
		model.valueList().clear();  // Also delegetes to remove().
		
		// => Inserting always delegates to put(). remove(index) delegates to remove(). clear() does not delegate.
	}
}
