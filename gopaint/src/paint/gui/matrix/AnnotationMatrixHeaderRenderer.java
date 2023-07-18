/**
 *  Copyright 2022 University Of Southern California
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.paint.gui.matrix;

import com.sri.panther.paintCommon.Constant;
import com.sri.panther.paintCommon.util.StringUtils;
import edu.usc.ksom.pm.panther.paint.matrix.UniRuleMatrix;
import edu.usc.ksom.pm.panther.paintCommon.GOTerm;
import edu.usc.ksom.pm.panther.paintCommon.Rule;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import org.paint.config.Preferences;
import static org.paint.gui.matrix.AnnotationMatrixCellRenderer.COLOR_UNIRULE_RULES_WITH_CASES_OVERLAP;


public class AnnotationMatrixHeaderRenderer extends JLabel implements TableCellRenderer{
    
    public AnnotationMatrixHeaderRenderer(JTableHeader header) {
                setOpaque(true);
		this.setText("");
		setHorizontalAlignment(CENTER);
		setVerticalAlignment(CENTER);
		header.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				AnnotationMatrix table = (AnnotationMatrix) ((JTableHeader) e.getSource()).getTable();
				TableColumnModel columnModel = table.getColumnModel();
				int viewColumn = columnModel.getColumnIndexAtX(e.getX());
				AnnotationMatrixModel model = (AnnotationMatrixModel) table.getModel();
                                HeaderAncestor ha = model.getPopup(viewColumn);
                                if (null != ha) {
                                    ha.showMenu(e);
                                }
//				if (viewColumn >= 0 && viewColumn < model.getColumnCount()) {
//                                        AncestorPopup ap = new AncestorPopup();
//					ColumnTermData td = model.getTermData(viewColumn);
//					td.showMenu(e, table);
//				}
			}
		});
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {    
        AnnotationMatrixModel matrix = (AnnotationMatrixModel) table.getModel();
        if (0 == matrix.getRowCount()) {
            return this;
        }
        this.setIcon(null);

        Color bg_color = null;
        if (true == AnnotationMatrixModel.AnnotationType.GO.equals(matrix.getAnnotType())) {
            if (false == matrix.isOdd(column)) {
                bg_color = AnnotationMatrixCellRenderer.COLOR_CONTRAST;
            } else {
                bg_color = AnnotationMatrixCellRenderer.COLOR_BASIC;
            }
        }
        else if (true == AnnotationMatrixModel.AnnotationType.UNI_RULE.equals(matrix.getAnnotType())) {
            int group = matrix.getGroup(column);
            boolean isOdd = matrix.isOdd(column);
            if (group == UniRuleMatrix.GROUP_UNIRULE_ID_GENERATED) {
                if (true == isOdd) {
                    bg_color = AnnotationMatrixCellRenderer.COLOR_UNIRULE_RULES_WITH_IDS;
                } else {
                    bg_color = AnnotationMatrixCellRenderer.COLOR_UNIRULE_RULES_WITH_IDS_CONTRAST;
                }
            } else if (group == UniRuleMatrix.GROUP_UNIRULE_ID_CASES) {
                if (true == isOdd) {
                    bg_color = AnnotationMatrixCellRenderer.COLOR_UNIRULE_RULES_WITH_CASES;
                } else {
                    bg_color = AnnotationMatrixCellRenderer.COLOR_UNIRULE_RULES_WITH_CASES_CONTRAST;
                }
//                Rule r = matrix.getRuleAtCol(column);
//                if (true == matrix.ruleInMultipleCases(r)) {
//                    bg_color = COLOR_UNIRULE_RULES_WITH_CASES_OVERLAP;
//                } 
            } else if (group == UniRuleMatrix.GROUP_UNIRULE_SWISSPROT_NOT_IN_CASES) {
                if (true == isOdd) {
                    bg_color = AnnotationMatrixCellRenderer.COLOR_UNIRULE_RULES_WITH_SWISSPROT;
                } else {
                    bg_color = AnnotationMatrixCellRenderer.COLOR_UNIRULE_RULES_WITH_SWISSPROT_CONTRAST;
                }
            } else {
                if (true == isOdd) {
                    bg_color = AnnotationMatrixCellRenderer.COLOR_UNIRULE_RULES_NEW;
                } else {
                    bg_color = AnnotationMatrixCellRenderer.COLOR_UNIRULE_RULES_NEW_CONTRAST;
                }
            }
            Rule rule = matrix.getRuleAtCol(column);
            if (true == matrix.ruleInMultipleCases(rule)) {
                bg_color = COLOR_UNIRULE_RULES_WITH_CASES_OVERLAP;
            }            
            if (null != rule.getException()) {
                bg_color = AnnotationMatrixCellRenderer.COLOR_EXCEPTION;
            }
            if (true == matrix.ruleInMultipleCases(rule) && null != rule.getException()) {
                bg_color = AnnotationMatrixCellRenderer.COLOR_CASE_OVERLAP_EXCEPTION;
            }

        }
        Border border;
        if (isSelected) {
            border = BorderFactory.createLineBorder(Color.BLACK);
        } else {
            border = BorderFactory.createEtchedBorder(); // default is lowered etched border
        }
        setBorder(border);        
        if (AnnotationMatrixModel.AnnotationType.GO.equals(matrix.getAnnotType())) {
            GOTerm goTerm = matrix.getTermForColumn(column);
            setToolTipText(goTerm.getName());
            Preferences dp = Preferences.inst();
            UIManager.put("ToolTip.foreground", dp.getForegroundColor());
            ToolTipManager.sharedInstance().setDismissDelay(999999999);
            setBackground(bg_color);
            HeaderAncestor ha = matrix.getPopup(column);

            setForeground(dp.getForegroundColor());
            if (null == ha || false == ha.hasAncestors()) {
                return this;
            }
            Icon icon = Preferences.inst().getIconByName("arrowDown");
            setIcon(icon);
            return this; 
        }
        else {
            setBackground(bg_color);
            Rule rule = matrix.getRuleAtCol(column);
            if (null != rule) {
                StringBuffer sb = new StringBuffer(AnnotationMatrixCellRenderer.STR_HTML_START + rule.getId());
                String labelValue = rule.getValue();
                if (null != labelValue) {
                    String formatted[] = StringUtils.formatString(Constant.STR_DASH + labelValue, 80);
                    for (String small : formatted) {
                        sb.append(small);
                        sb.append(AnnotationMatrixCellRenderer.STR_HTML_BREAK);
                    }
                }
                sb.append(AnnotationMatrixCellRenderer.STR_HTML_END);
                setToolTipText(sb.toString());
            }
            else {
                setToolTipText("");
            }
            Preferences dp = Preferences.inst();
            UIManager.put("ToolTip.foreground", dp.getForegroundColor());
            ToolTipManager.sharedInstance().setDismissDelay(999999999);
            }
            Icon icon = Preferences.inst().getIconByName("arrowDown");
            setIcon(icon);
        
        return this;
    }
    
}
