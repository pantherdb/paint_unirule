/**
 * Copyright 2020 University Of Southern California
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
package org.paint.gui.table;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import javax.swing.table.TableCellRenderer;
import org.paint.datamodel.GeneNode;


public class BooleanCellRenderer extends JCheckBox implements TableCellRenderer, UIResource {

    	private GeneNode node;      
        private static final long serialVersionUID = 1L;
        private final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

        BooleanCellRenderer() {
            super();
            setHorizontalAlignment(JLabel.CENTER);
            setBorderPainted(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            GeneTableModel genes = (GeneTableModel) table.getModel();
            node = genes.getNode(row);
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
            setSelected(value != null && ((Boolean) value).booleanValue());
            if (hasFocus) {
                setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            } else {
                setBorder(noFocusBorder);
            }
            return this;
        }
    }