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
import com.sri.panther.paintCommon.util.Utils;
import edu.usc.ksom.pm.panther.paintCommon.Qualifier;
import edu.usc.ksom.pm.panther.paint.matrix.NodeInfoForMatrix;
import edu.usc.ksom.pm.panther.paint.matrix.NodeInfoForUniRuleMatrix;
import edu.usc.ksom.pm.panther.paint.matrix.UniRuleMatrix;
import edu.usc.ksom.pm.panther.paintCommon.LabelValue;
import edu.usc.ksom.pm.panther.paintCommon.Rule;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.paint.config.Preferences;
import org.paint.datamodel.GeneNode;
import org.uniprot.unirule_1.PositionalAnnotationType;
import org.uniprot.unirule_1.PositionalConditionType;
import org.uniprot.unirule_1.PositionalFeatureType;
import org.uniprot.unirule_1.RangeType;
import org.uniprot.unirule_1.RuleExceptionType;

public class AnnotationMatrixCellRenderer extends JLabel implements TableCellRenderer{
 
    public static final String STR_EMPTY = "";
    public static final String STR_BRACKET_START = "(";
    public static final String STR_BRACKET_END = ")";
    public static final String STR_COMMA = ",";
    public static final String STR_COLON = ":";
    public static final String STR_HYPHEN = "-";
    public static final String STR_ROW = "Row ";
    public static final String STR_COL = " Col ";
    public static final String STR_TYPE = "Type ";
    public static final String STR_LIGAND = "Ligand ";
    public static final String STR_LIGAND_PART = "Ligand part";
    public static final String STR_DESC = "Description ";
    public static final String STR_SPACE = " ";
    public static final String STR_HTML_START = "<HTML>";
    public static final String STR_HTML_END = "</HTML>";
    public static final String STR_HTML_BREAK = "<BR>";
    public static final String STR_NBSP4_BREAK = "&nbsp;&nbsp;&nbsp;&nbsp;" + STR_HTML_BREAK;
    public static final String STR_NBSP4 = "&nbsp;&nbsp;&nbsp;&nbsp;";    
    private static final String STR_RULE_ID = "Rule: ";
    private static final String STR_QUALIFIER = "Qualifier: ";
    private static final String STR_EXCEPTION = "Exception:  ";
    private static final String STR_ACCESSION = "Accession:  ";    
    private static final String STR_CATEGORY = "Category:  ";   
    private static final String STR_POS_FEATURE = "Position Feature:  ";     
    private static final String STR_NOTE = "Note:  ";
    private static final String STR_IN_GROUP = "In Group: ";
    private static final String STR_ENTRIES_1 = " ("; 
    private static final String STR_ENTRIES_2 = " entries)";
    
    private static final Color PAINT_COLOR_EXP = Preferences.inst().getExpPaintColor();//new Color(16, 128, 64);
//    private static final Color curatedPaintColor = new Color(255, 127, 0);
    private static final Color PAINT_COLOR_INFER = Preferences.inst().getInferPaintColor();//new Color(68, 116, 179);
    private static final Color PAINT_NON_EXP_NON_PAINT = Preferences.inst().getNonExpnonPaintColor();
    public static final Color COLOR_BASIC = new Color(155, 205, 255);
    public static final Color COLOR_CONTRAST = new Color(233, 236, 242);
    
    public static final Color DARK_PINK = new Color(223, 78, 78);
    public static final Color LIGHT_MOSS = new Color(185, 179, 17);
    public static final Color LAVENDER = new Color(230, 230, 250);
    public static final Color LIGHT_YELLOW = new Color(228, 228, 193);

    public static final Color GREY = new Color(153, 155, 175);
    public static final Color MAUVE = new Color(196, 108, 170);
    public static final Color PURPLE = new Color(108, 108, 196);
    public static final Color SEA_BLUE = new Color(43, 148, 201);
    public static final Color TEAL = new Color(0, 128, 128);
    public static final Color COLOR_DARK_RED = new Color(201, 22, 22);
    public static final Color COLOR_GREEN = new Color(50, 168, 82);
    public static final Color COLOR_ORANGE = new Color(230, 104, 14);
    
    public static final Color COLOR_UNIRULE_BASIC = GREY;
    public static final Color COLOR_UNIRULE_CONTRAST = SEA_BLUE;
    public static final Color COLOR_UNIRULE_RULE_UIC = LIGHT_MOSS;
    public static final Color COLOR_UNIRULE_RULE_UIC_CONTRAST = LIGHT_YELLOW;


    // 4 sets of columns:
    // 1. Rules with Unirule Ids
    // 2. Rules belonging to cases
    // 3. Swisssprot rules not in case 2
    // 4. Other rules not in 2 or 3.
    // Each of the 4 sets will have two colors to separate groups within a set.
    public static final Color COLOR_UNIRULE_RULES_WITH_IDS = COLOR_BASIC;
    public static final Color COLOR_UNIRULE_RULES_WITH_IDS_CONTRAST = COLOR_CONTRAST;
    public static final Color COLOR_UNIRULE_RULES_WITH_CASES = GREY;
    public static final Color COLOR_UNIRULE_RULES_WITH_CASES_CONTRAST = SEA_BLUE;
    public static final Color COLOR_UNIRULE_RULES_WITH_CASES_OVERLAP = COLOR_GREEN;
    public static final Color COLOR_UNIRULE_RULES_WITH_SWISSPROT = LIGHT_MOSS;
    public static final Color COLOR_UNIRULE_RULES_WITH_SWISSPROT_CONTRAST = LIGHT_YELLOW;
    public static final Color COLOR_UNIRULE_RULES_NEW = MAUVE;
    public static final Color COLOR_UNIRULE_RULES_NEW_CONTRAST = LAVENDER;    
    public static final Color COLOR_EXCEPTION = COLOR_DARK_RED;
    public static final Color COLOR_CASE_OVERLAP_EXCEPTION = COLOR_ORANGE;
    
    
    NodeInfoForMatrix nodeInfo;
    Qualifier qualifier;
    boolean selected;
    Color backgroundColor;
    String label;
    GeneNode node;
    int row;
    int column;
    AnnotationMatrixModel model;
    
    NodeInfoForUniRuleMatrix uRuleNodeInfo;
    
    public AnnotationMatrixCellRenderer() {
		setText("");
                nodeInfo = null;
                qualifier = null;
                selected = false;
                backgroundColor = null;
                label = null;
                node = null;
		//setOpaque(true); //MUST do this for background to show up.        
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        model = (AnnotationMatrixModel) table.getModel();
        setText(STR_EMPTY);
        nodeInfo = null;
        qualifier = null;
        selected = isSelected;
        backgroundColor = null;
        label = null;
        node = null;
        this.row = row;
        this.column = column;

        AnnotationMatrix annot_table = (AnnotationMatrix) table;
        if (true == AnnotationMatrixModel.AnnotationType.GO.equals(model.getAnnotType())) {
            nodeInfo = (NodeInfoForMatrix) value;
            if (null == nodeInfo) {
                System.out.println("Could not find cell Renderer component for row " + row + " column " + column);
                Exception e = new Exception();
                e.printStackTrace();
                return null;
            }
            node = nodeInfo.getgNode();
            if (node.isSelected() || annot_table.getSelectedColumn() == column) {
                selected = true;
            }
  
            backgroundColor = COLOR_BASIC;

            if (false == model.isOdd(column)) {
                backgroundColor = COLOR_CONTRAST;  //    Don't want to use  Color.LIGHT_GRAY else cannot differentiate cells when border is painted using light grey as well 
            }
            // Handle no annotations
            if (false == nodeInfo.isExpBackground() && false == nodeInfo.isNonExpBackground()) {
                String tooltip = nodeInfo.getgTerm().getName() + STR_BRACKET_START + nodeInfo.getgTerm().getAcc() + STR_BRACKET_END;
                if (null == tooltip || tooltip.isEmpty()) {
                    tooltip = nodeInfo.getgTerm().getAcc();
                }
                setToolTipText(STR_HTML_START + STR_ROW + (this.row + 1) + STR_COL + (this.column + 1) + STR_SPACE + tooltip + STR_HTML_END);
                return this;
            } else if (true == nodeInfo.isExpBackground()) {
                // Experimental evidence
                backgroundColor = PAINT_COLOR_EXP;
                String termStr = nodeInfo.getgTerm().getName() + STR_BRACKET_START + nodeInfo.getgTerm().getAcc() + STR_BRACKET_END;
                if (null == termStr || termStr.isEmpty()) {
                    termStr = nodeInfo.getgTerm().getAcc();
                }
                //setToolTipText(STR_ROW + (this.row + 1)  + STR_COL + (this.column + 1) + STR_SPACE + node.getNodeLabel() + STR_BRACKET_START + node.getNode().getStaticInfo().getNodeAcc() + STR_SPACE +  node.getNode().getStaticInfo().getPublicId() + STR_BRACKET_END + STR_SPACE + termStr + STR_SPACE + getQualifierString(nodeInfo.getqSet()));
                setToolTipText(STR_HTML_START + STR_ROW + (this.row + 1) + STR_COL + (this.column + 1) + STR_SPACE + node.getNodeLabel() + STR_BRACKET_START + node.getNode().getStaticInfo().getNodeAcc() + STR_SPACE + node.getNode().getStaticInfo().getPublicId() + STR_BRACKET_END + STR_SPACE + termStr + STR_HTML_BREAK + getAllQualifierInfo(nodeInfo, STR_HTML_BREAK) + STR_HTML_END);
                return this;

            } else if (false == nodeInfo.isExpBackground() && true == nodeInfo.isNonExpBackground()) {
                backgroundColor = PAINT_COLOR_INFER;

                String termStr = nodeInfo.getgTerm().getName() + STR_BRACKET_START + nodeInfo.getgTerm().getAcc() + STR_BRACKET_END;
                if (null == termStr || termStr.isEmpty()) {
                    termStr = nodeInfo.getgTerm().getAcc();
                }
                //setToolTipText(STR_ROW + (this.row + 1) + STR_COL + (this.column + 1) + STR_SPACE + node.getNodeLabel() + STR_BRACKET_START + node.getNode().getStaticInfo().getNodeAcc() + STR_SPACE +  node.getNode().getStaticInfo().getPublicId() + STR_BRACKET_END + STR_SPACE + termStr + STR_SPACE + getQualifierString(nodeInfo.getNonQset()));
                setToolTipText(STR_HTML_START + STR_ROW + (this.row + 1) + STR_COL + (this.column + 1) + STR_SPACE + node.getNodeLabel() + STR_BRACKET_START + node.getNode().getStaticInfo().getNodeAcc() + STR_SPACE + node.getNode().getStaticInfo().getPublicId() + STR_BRACKET_END + STR_SPACE + termStr + STR_HTML_BREAK + getAllQualifierInfo(nodeInfo, STR_HTML_BREAK) + STR_HTML_END);
                return this;
            }
            return this;
        }
        
        // Now handle Unirule coloring
        uRuleNodeInfo = (NodeInfoForUniRuleMatrix)value;
        if (null == uRuleNodeInfo) {
            System.out.println("Could not find cell Renderer component for row " + row + " column " + column);
            Exception e = new Exception();
            e.printStackTrace();
            return null;
        }
        node = uRuleNodeInfo.getGeneNode();
        if (null == node) {
            return null;
        }
        if (node.isSelected() || annot_table.getSelectedColumn() == column) {
            selected = true;
        }
        
        int group = model.getGroup(column);
        boolean isOdd = model.isOdd(column);
        if (group == UniRuleMatrix.GROUP_UNIRULE_ID_GENERATED) {
            if (true == isOdd) {
                backgroundColor = COLOR_UNIRULE_RULES_WITH_IDS;
            }
            else {
                 backgroundColor = COLOR_UNIRULE_RULES_WITH_IDS_CONTRAST;
            }
        }
        else if (group == UniRuleMatrix.GROUP_UNIRULE_ID_CASES) {
            if (true == isOdd) {
                backgroundColor = COLOR_UNIRULE_RULES_WITH_CASES;
            }
            else {
                 backgroundColor = COLOR_UNIRULE_RULES_WITH_CASES_CONTRAST;
            }
//            Rule r = model.getRuleAtCol(column);
//            if (true == model.ruleInMultipleCases(r)) {
//                backgroundColor = COLOR_UNIRULE_RULES_WITH_CASES_OVERLAP;
//            }            
        }
        else if (group == UniRuleMatrix.GROUP_UNIRULE_SWISSPROT_NOT_IN_CASES) {
            if (true == isOdd) {
                backgroundColor = COLOR_UNIRULE_RULES_WITH_SWISSPROT;
            }
            else {
                 backgroundColor = COLOR_UNIRULE_RULES_WITH_SWISSPROT_CONTRAST;
            }
        }
        else {
            if (true == isOdd) {
                backgroundColor = COLOR_UNIRULE_RULES_NEW;
            }
            else {
                 backgroundColor = COLOR_UNIRULE_RULES_NEW_CONTRAST;
            }            
        }

//        if (model.canCurate(column)) {
//            Rule r = model.getRuleAtCol(column);
//            if (r.isRuleSupportedByExpEvidence()) {
//                backgroundColor = COLOR_UNIRULE_BASIC;
//                if (false == model.isOdd(column)) {
//                    backgroundColor = COLOR_UNIRULE_CONTRAST;
//                }
//            } else {
//                backgroundColor = COLOR_UNIRULE_RULE_UIC;
//                if (false == model.isOdd(column)) {
//                    backgroundColor = COLOR_UNIRULE_RULE_UIC_CONTRAST;
//                }
//            }
//        }
//        else {
//            backgroundColor = COLOR_BASIC;
//            if (false == model.isOdd(column)) {
//                backgroundColor = COLOR_CONTRAST;  
//            }            
//        }
        
        if (false == uRuleNodeInfo.isExpAnnot() && false == uRuleNodeInfo.isNonExpNonPAINTAnnot() && false == uRuleNodeInfo.isPAINTAnnot()) {
            // Nothing here
        }
        else if (true == uRuleNodeInfo.isExpAnnot()) {
            // Experimental evidence
            backgroundColor = PAINT_COLOR_EXP;
        }
        else if (true == uRuleNodeInfo.isPAINTAnnot()) {
            backgroundColor = PAINT_COLOR_INFER;
        }
        else if (true == uRuleNodeInfo.isNonExpNonPAINTAnnot()) {
            backgroundColor = PAINT_NON_EXP_NON_PAINT;
        }
        setToolTipText(getTooltip());
        return this;
    }
   
    private String getTooltip() {
        if (null == uRuleNodeInfo) {
            return null;
        }
        Rule r = model.getRuleAtCol(column);
        if (null == r) {
            return null;
        }
        
        
        StringBuffer sb = new StringBuffer(STR_HTML_START);
        sb.append(STR_ROW + (row + 1) + STR_COL + (column + 1) + Constant.STR_SPACE + node.getNodeLabelWithPTNAndAcc());
        sb.append(STR_HTML_BREAK);        
        sb.append(STR_RULE_ID);
        LabelValue lv = r.getLabelValue();
        if (null != lv) {
            sb.append(lv.getLabel() + STR_BRACKET_START + r.getId() + STR_BRACKET_END);
        }
        sb.append(STR_HTML_BREAK);

        String value = lv.getValue();
        if (null != value) {
            String formatted[] = StringUtils.formatString(value, 80);
            for (String small : formatted) {
                sb.append(small);
                sb.append(STR_HTML_BREAK);
            }
        }

//        if (null != lv) {
//            ArrayList<String> sortedTypes = new ArrayList<String>(lookup.keySet());
//            for (String type: sortedTypes) {
//                ArrayList<LabelValue> values = lookup.get(type);
//                sb.append(type);
//                sb.append(Constant.STR_COLON);
//                sb.append(Constant.STR_SPACE);
//                String[] valuesList = new String[values.size()];
//                for (int i = 0; i < values.size(); i++) {
//                    valuesList[i] = values.get(i).getValue();
//                }
//                String joined = String.join(Constant.STR_COMMA, valuesList);
//                joined = WordUtils.wrap(joined, 80, STR_NBSP4_BREAK, true);
//                sb.append(joined);
//                sb.append(STR_HTML_BREAK);
//            }
//        }
        // Output qualifier information
        Set<Qualifier> qSet = uRuleNodeInfo.getqSet();
        if (null != qSet) {
            sb.append(STR_QUALIFIER);
            sb.append(getQualifierDisplay(qSet));
            sb.append(STR_HTML_BREAK);
        }
        
        // Output exception information
        Object exObject = r.getException();
        if (null != exObject) {
            sb.append(STR_EXCEPTION);
            RuleExceptionType ret = (RuleExceptionType)exObject;
            List<String> accList = ret.getAccession();
            if (null != accList) {
                sb.append(STR_HTML_BREAK);
                sb.append(STR_NBSP4);
                sb.append(STR_ACCESSION + String.join(STR_COMMA, accList) + STR_ENTRIES_1 + accList.size() + STR_ENTRIES_2);
                
            }
            String category = ret.getCategory();
            if (null != category) {
                sb.append(STR_HTML_BREAK);
                sb.append(STR_NBSP4);                
                sb.append(STR_CATEGORY + category);
            }
            String note = ret.getNote();
            if (null != note) {
                sb.append(STR_HTML_BREAK);
                sb.append(STR_NBSP4);                
                sb.append(STR_NOTE + note); 
            }
            String posFeat = null;
            PositionalFeatureType pft = ret.getPositionalFeature();
            if (null != pft) {
                posFeat = STR_EMPTY;
                PositionalAnnotationType pat = pft.getPositionalAnnotation();
                if (null != pat) {
                    posFeat = posFeat + STR_SPACE + STR_TYPE + STR_COLON + pat.getType() + 
                                        STR_SPACE + STR_LIGAND + STR_COLON + pat.getLigand() + 
                                        STR_SPACE + STR_LIGAND_PART + STR_COLON + pat.getLigandPart() +
                                        STR_SPACE + STR_DESC + STR_COLON + pat.getDescription() + STR_HTML_BREAK;
                }
                PositionalConditionType pct = pft.getPositionalCondition();
                if (null != pct) {
                    RangeType rt = pct.getPosition();
                    if (null != rt) {
                        posFeat = posFeat + pct.getPattern() + STR_COLON + rt.getStart() + STR_HYPHEN + rt.getEnd() + STR_HTML_BREAK;
                    }
                }
                Boolean inGroup = pft.isInGroup();
                if (null != inGroup) {
                    posFeat = posFeat +  STR_IN_GROUP + STR_COLON + inGroup.toString() + STR_HTML_BREAK;
                }
                sb.append(STR_HTML_BREAK);
                sb.append(STR_NBSP4);                
                sb.append(STR_POS_FEATURE + posFeat);                
            }
            sb.append(STR_HTML_BREAK);
        }

        // Output group for case groups
        if (UniRuleMatrix.GROUP_UNIRULE_ID_CASES == model.getGroup(column)) {
            String groupId = model.getGroupIdForCol(column);
            if (null != groupId) {
                sb.append(groupId);
            }
            sb.append(STR_HTML_BREAK);
        }
        
        sb.append(STR_HTML_END);
        return sb.toString();
    }
    
    private String getQualifierDisplay(Set<Qualifier> qSet) {
        HashSet<String> qualifiers = new HashSet<String>();
        for (Qualifier q: qSet) {
            if (null != q.getText()) {
                qualifiers.add(q.getText());
            }
        }
        if (qualifiers.isEmpty()) {
            return STR_EMPTY;
        }
        return String.join(STR_COMMA, qualifiers);
    }
    
    private String getAllQualifierInfo(NodeInfoForMatrix nodeInfo, String delim) {
        if (null == nodeInfo) {
            return null;
        }
        HashMap<String, HashSet<String>> qualifierLookup = nodeInfo.getAllQualifierToListOfTerms();
        if (null == qualifierLookup) {
            return null;
        }
        Vector<String> allStrs = new Vector(qualifierLookup.size());
        for (Entry<String, HashSet<String>> entry: qualifierLookup.entrySet()) {
            String qualifier = entry.getKey();
            HashSet<String> terms = entry.getValue();
            allStrs.add(qualifier + STR_COLON + Utils.listToString(new Vector(terms), STR_EMPTY, STR_COMMA));
        }
        return Utils.listToString(allStrs, STR_EMPTY, delim);
    }
    
    public void paintComponent(Graphics g) {
	super.paintComponent(g);
         
        int width = this.getWidth();
        int height = this.getHeight();
        //RenderUtil.paintBorder(g, new Rectangle(0, 0, width, height), null, selected);
        if (true == selected) {
            g.setColor(backgroundColor.brighter());
        }
        else {
            g.setColor(backgroundColor);
        }
        if (AnnotationMatrixModel.AnnotationType.GO == model.getAnnotType()) {
            g.fillRect(1, 1, width - 1, height - 1);
            boolean multipleQualifiers = nodeInfo.containsMultipleQualifiers();
            if (false == nodeInfo.isExpBackground() && false == nodeInfo.isNonExpBackground()) {
                return;
            } else if (true == nodeInfo.isExpBackground()) {
                if (true == multipleQualifiers && false == nodeInfo.isExpNot()) {
                    g.setColor(Color.yellow);
                    g.fillOval(1, 1, width - 3, height - 3);
                } else if (true == multipleQualifiers && true == nodeInfo.isExpNot()) {
                    g.setColor(Color.pink);
                    g.fillOval(1, 1, width - 3, height - 3);
                } else if (false == multipleQualifiers && true == nodeInfo.isExpNot()) {
                    g.setColor(Color.red);
                    g.fillOval(1, 1, width - 3, height - 3);
                }
                if (true == nodeInfo.isExpAnnotToTerm()) {
                    g.setColor(Color.BLACK);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fillRect((getWidth() / 2) - 2, (getHeight() / 2) - 2, 4, 4);
                return;
            } else if (false == nodeInfo.isExpBackground() && true == nodeInfo.isNonExpBackground()) {
                if (true == multipleQualifiers && false == nodeInfo.isNonExpNot()) {
                    g.setColor(Color.yellow);
                    g.fillOval(1, 1, width - 3, height - 3);
                } else if (true == multipleQualifiers && true == nodeInfo.isNonExpNot()) {
                    g.setColor(Color.pink);
                    g.fillOval(1, 1, width - 3, height - 3);
                } else if (false == multipleQualifiers && true == nodeInfo.isNonExpNot()) {
                    g.setColor(Color.red);
                    g.fillOval(1, 1, width - 3, height - 3);
                }
                if (true == nodeInfo.isNonExpAnnotToTerm()) {
                    g.setColor(Color.BLACK);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fillRect((getWidth() / 2) - 2, (getHeight() / 2) - 2, 4, 4);
            }
            return;
        }

        // Unirule
        g.fillRect(1, 1, width - 1, height - 1);
        if (false == uRuleNodeInfo.isExpAnnot() && false == uRuleNodeInfo.isNonExpNonPAINTAnnot() && false == uRuleNodeInfo.isPAINTAnnot()) {
            return;
        }
        if (true == uRuleNodeInfo.containsNot() && (true == uRuleNodeInfo.isStandardPositiveQualifier() || true == uRuleNodeInfo.containsMultiplePositiveQualifier())) {
            g.setColor(Color.pink);
            g.fillOval(1, 1, width - 3, height - 3);
        } else if (true == uRuleNodeInfo.containsNot() && false == uRuleNodeInfo.isStandardPositiveQualifier() && false == uRuleNodeInfo.containsMultiplePositiveQualifier()) {

            g.setColor(Color.red);
            g.fillOval(1, 1, width - 3, height - 3);
        } else if (false == uRuleNodeInfo.containsNot() && true == uRuleNodeInfo.containsMultiplePositiveQualifier()) {
            g.setColor(Color.yellow);
            g.fillOval(1, 1, width - 3, height - 3);
        }
        if (true == uRuleNodeInfo.isExpAnnot() || (true == uRuleNodeInfo.isNonExpNonPAINTAnnot() && false == uRuleNodeInfo.isPAINTAnnot())) {
            g.setColor(Color.BLACK);
        }
        if (true == uRuleNodeInfo.isPAINTAnnot()) {
            g.setColor(Color.WHITE);
            // Ensure group is applicable for node or color differently
            if (null != node && false == model.isColumnApplicableToNode(node.getNode(), column)) {
                g.setColor(Color.LIGHT_GRAY);
            }
            
        }
        
        g.fillRect((getWidth() / 2) - 2, (getHeight() / 2) - 2, 4, 4);
        return;

    }   
}
