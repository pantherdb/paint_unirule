/**
 * Copyright 2021 University Of Southern California
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.paint.util;

import java.awt.FontMetrics;
import java.awt.Insets;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.paint.gui.PaintTable;

public class TableUtil {
	/**
	 * 
	 */

	public TableUtil(){
	}
	
	public static void setColumnWidths(PaintTable grid, int col_count, FontMetrics fm, TableColumnModel colModel) {
		Insets insets = new DefaultTableCellRenderer().getInsets();
		for (int i = 0; i < col_count; i++) {
			int optimalColumnWidth = 0;
			/*
			 * Fixed this so that it works generally for any column that is just an icon
			 * e.g. other homology programs, etc.
			 */
			if (grid.isSquare(i)) {
				optimalColumnWidth = fm.getHeight();
			}
//			Set column width to max size required to fit text                        
			else {
				for (int j = 0; j < grid.getRowCount(); j++) {
					String value = grid.getTextAt(j, i);
					if (null == value) {
						value = "";
					}
                                        if (value.length() > 30) {
                                            value = value.substring(0, 29);
                                        }
					int optimalCellWidth = fm.stringWidth(value) + insets.left + insets.right + 2;
					optimalColumnWidth = Math.max(optimalColumnWidth, optimalCellWidth);
				}
			}
			TableColumn col = colModel.getColumn(i);
			//Get the column at index columnIndex, and set its preferred width.
			col.setPreferredWidth(optimalColumnWidth);
			col.setWidth(optimalColumnWidth);
		}
	}
	
}