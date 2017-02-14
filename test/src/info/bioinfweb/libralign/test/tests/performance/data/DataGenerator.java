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
package info.bioinfweb.libralign.test.tests.performance.data;


import info.bioinfweb.commons.SystemUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;



public class DataGenerator {
	public static final String OUTPUT_FOLDER = "D:\\Users\\BenStoever\\ownCloud\\Dokumente\\Projekte\\LibrAlign\\Testdaten\\Performance";
	public static final String FILE_PREFIX = "Alignment_";
	public static final String SEQUENCE_NAME_PREFIX = "Seq";
	public static final String[] BASE_ALIGNMENT = {
			"GAGCTGAGGT-AA---TTGAACACTTAATGA-",
			"GAGCTGAGATGAA---TTGAACACTTAATGA-",
			"GAGCTGAGGTAAA---TTGAACACATAATGAA",
			"GAGCTGAGCTAAA---TTGAACATAATACAA-",
			"G-GCAGTGCTAAA---TTGAACATACAATGAC",
			"GAGCAGTGCTAAA---TTGAACATACAATGAC",
			"GAGCAGTGCTAAAAAATTGAACATAC----AA",
			"GAACGGTGCTAAAAAATTGAACATAC----AA"};
		// 12345678901234567890123456789012
	public static final int MAX_ROWS_DIV_8 = 16384 / 8;
	public static final int MAX_COLUMNS_DIV_32 = 4096; //(2^17) / 32;
	public static final int MANY_TAXA_DIV_8 = 4096 / 8;
	public static final int MAX_COLUMNS_DIV_32_MANY_TAXA = 256;
	
	
	private void writeAlignment(int rowCountDiv8, int colCountDiv32) throws IOException {
		String file = OUTPUT_FOLDER + SystemUtils.FILE_SEPARATOR + FILE_PREFIX + (rowCountDiv8 * 8) + "_" + (colCountDiv32 * 32) + ".fasta"; 
		System.out.print("Writing \"" + file + "\"...");
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		try {
			for (int row1 = 0; row1 < rowCountDiv8; row1++) {
				for (int row2 = 0; row2 < 8; row2++) {
					writer.write(">" + SEQUENCE_NAME_PREFIX + (row1 * 8 + row2) + SystemUtils.LINE_SEPARATOR);
					for (int col = 0; col < colCountDiv32; col++) {
						writer.write(BASE_ALIGNMENT[row2]);
						if ((col % 2 == 1) || (col == colCountDiv32 - 1)) {
							writer.write(SystemUtils.LINE_SEPARATOR);
						}
					}
				}
			}
		}
		finally {
			writer.close();
			System.out.println("done.");
		}
	}
	
	
	public static void main(String[] args) {
		try {
			DataGenerator generator = new DataGenerator();
//			int rowsDiv8 = 1;
//			while (rowsDiv8 <= MAX_ROWS_DIV_8) {
//				int columnsDiv32 = 1;
//				
//				// Max columns depends in the number of rows.
//				int maxColumnsDiv32 = MAX_COLUMNS_DIV_32;
//				if (rowsDiv8 > MANY_TAXA_DIV_8) {
//					maxColumnsDiv32 = MAX_COLUMNS_DIV_32_MANY_TAXA;
//				}
//				
//				while (columnsDiv32 <= maxColumnsDiv32) {
//					generator.writeAlignment(rowsDiv8, columnsDiv32);
//					columnsDiv32 *= 2;
//				}
//				rowsDiv8 *= 2;
//			}
//			System.out.println("Generating files finished.");
			
			
//			generator.writeAlignment(4096, 1);
//			generator.writeAlignment(4096 * 2, 1);
//			generator.writeAlignment(4096 * 4, 1);
//			generator.writeAlignment(4096 * 8, 1);
//			generator.writeAlignment(4096 * 16, 1);
//			generator.writeAlignment(4096 * 32, 1);
//			generator.writeAlignment(4096 * 64, 1);

//			generator.writeAlignment(1, 4096 * 2);
//			generator.writeAlignment(1, 4096 * 4);
//			generator.writeAlignment(1, 4096 * 8);
//			generator.writeAlignment(1, 4096 * 16);
//			generator.writeAlignment(1, 4096 * 32);
//			generator.writeAlignment(1, 4096 * 64);
//			generator.writeAlignment(1, 4096 * 128);
//			generator.writeAlignment(1, 4096 * 256);
//			generator.writeAlignment(1, 4096 * 512);
//			generator.writeAlignment(1, 4096 * 1024);
			generator.writeAlignment(1, 4096 * 2048);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
