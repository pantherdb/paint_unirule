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

import edu.usc.ksom.pm.panther.paintCommon.GOTerm;
import edu.usc.ksom.pm.panther.paint.matrix.MatrixGroup;
import edu.usc.ksom.pm.panther.paint.matrix.NodeInfoForMatrix;
import edu.usc.ksom.pm.panther.paint.matrix.NodeInfoForUniRuleMatrix;
import edu.usc.ksom.pm.panther.paint.matrix.TermAncestor;
import edu.usc.ksom.pm.panther.paint.matrix.UniRuleMatrix;
import edu.usc.ksom.pm.panther.paintCommon.LabelValue;
import edu.usc.ksom.pm.panther.paintCommon.Node;
import edu.usc.ksom.pm.panther.paintCommon.Rule;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.paint.datamodel.GeneNode;


public class AnnotationMatrixModel extends AbstractTableModel {
    private String aspect;
    private List<GeneNode> orderedNodes;
    private ArrayList<MatrixGroup> matrixGroupList;
    private UniRuleMatrix uniRuleMatrix;
    //private HashMap<String, Integer> uRuleToGroupLookup = new HashMap<String, Integer>();       // For coloring purposes
    public static final String DELIM_PRIMARY_RULE = UniRuleMatrix.DELIM_PRIMARY_RULE;
    
    public enum AnnotationType {
        GO,
        UNI_RULE;
    }
    
    private AnnotationType annotType = AnnotationType.GO;
    
    public AnnotationMatrixModel(List<GeneNode> orderedNodes, String aspect, ArrayList<MatrixGroup> matrixGroupList) {
        this.orderedNodes = orderedNodes;
        this.aspect = aspect;
        this.matrixGroupList = matrixGroupList;
        annotType = AnnotationType.GO;
    }
    
    public AnnotationMatrixModel(UniRuleMatrix uniRuleMatrix) {
        this.uniRuleMatrix = uniRuleMatrix;
        annotType = AnnotationType.UNI_RULE;
//        String previous = null;
//        int group = -1;
//        for (int i = 0; i < uniRuleMatrix.getNumCols(); i++) {
//            String ruleId = uniRuleMatrix.getUniRuleIdAtCol(i);
//            int index = ruleId.indexOf(DELIM_PRIMARY_RULE);
//            if (index < 0) {
//                group++;
//                uRuleToGroupLookup.put(ruleId, group);
//                previous = ruleId;
//                continue;
//            }
//            String current = ruleId.substring(0, index);
//            if (current.equals(previous)) {
//                uRuleToGroupLookup.put(ruleId, group);
//            }
//            else {
//                group++;
//                uRuleToGroupLookup.put(ruleId, group);
//                previous = current;
//            }
//        }
    }
	/*
	 * JTable uses this method to determine the default renderer/
	 * editor for each cell.
	 */
	@Override
	public Class<?> getColumnClass(int c) {
            if (AnnotationType.GO.equals(annotType)) {
		return NodeInfoForMatrix.class;
            }
            else {
                return NodeInfoForUniRuleMatrix.class;
            }
	}    

    @Override
    public int getRowCount() {
        if (AnnotationType.GO.equals(annotType)) {
            if (null == orderedNodes) {
                return 0;
            }
            return orderedNodes.size();
        }
        
        if (null != uniRuleMatrix) {
            return uniRuleMatrix.getNumRows();
        }
        return 0;
    }

    @Override
    public int getColumnCount() {
        if (AnnotationType.GO.equals(annotType)) {
            if (null == matrixGroupList) {
                return 0;
            }
            int total = 0;
            for (MatrixGroup group : matrixGroupList) {
                total += group.getCount();
            }
            return total;
        }
        if (null != uniRuleMatrix) {
            return uniRuleMatrix.getNumCols();
        }
        return 0;
    }
    
    public HeaderAncestor getPopup(int columnIndex) {
        if (AnnotationType.GO.equals(annotType)) {
            int current = 0;
            for (MatrixGroup group : matrixGroupList) {
                if (current <= columnIndex && columnIndex < current + group.getCount()) {
                    return new HeaderAncestor(group.getTermAncestorAtIndex(columnIndex - current));
                }

                current += group.getCount();
            }
            return null;
        }
        return null;
    }
    
    public TermAncestor getTermAncestorAtColumn(int columnIndex) {
        if (AnnotationType.GO.equals(annotType)) {
            int current = 0;
            for (MatrixGroup group : matrixGroupList) {
                if (current <= columnIndex && columnIndex < current + group.getCount()) {
                    return group.getTermAncestorAtIndex(columnIndex - current);
                }

                current += group.getCount();
            }
            return null;
        }
        return null;
    }
    
    public GOTerm getTermForColumn(int columnIndex) {
        if (AnnotationType.GO.equals(annotType)) {
            if (null == matrixGroupList) {
                return null;
            }
            int current = 0;
            for (MatrixGroup group : matrixGroupList) {
                if (current <= columnIndex && columnIndex < current + group.getCount()) {
                    return group.getTermAtIndex(columnIndex - current);
                }

                current += group.getCount();
            }
            return null;
        }
        return null;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (AnnotationType.GO.equals(annotType)) {
            if (null == orderedNodes || rowIndex > orderedNodes.size()) {
                return null;
            }

            int current = 0;
            for (int i = 0; i < matrixGroupList.size(); i++) {
                MatrixGroup group = matrixGroupList.get(i);
                if (current <= columnIndex && columnIndex < current + group.getCount()) {
                    return group.getAnnotInfoForNode(orderedNodes.get(rowIndex).getNode(), columnIndex - current);
                }

                current += group.getCount();
            }
            System.out.println("Invalid row or column " + rowIndex + " col index = " + columnIndex);
            return null;
        }
        if (null == uniRuleMatrix) {
            return null;
        }
        String ruleId = uniRuleMatrix.getUniRuleIdAtCol(columnIndex);
        Rule rule = uniRuleMatrix.getRuleById(ruleId);
        return new NodeInfoForUniRuleMatrix(getNode(rowIndex), rule);
    }
    
    public boolean isOdd(int columnIndex) {
        if (AnnotationType.GO.equals(annotType)) {        
            int current = 0;
            if (null == matrixGroupList) {
                return false;
            }
            for (int i = 0; i < matrixGroupList.size(); i++) {
                MatrixGroup group = matrixGroupList.get(i);
                if (current <= columnIndex && columnIndex < current + group.getCount()) {
                    if (i % 2 == 0) {
                        return false;
                    }
                    return true;
                }
                current += group.getCount();
            }
            return false;
        }
        
        // Unirule definition of odd columns
        if (null == this.uniRuleMatrix) {
            return false;
        }
        if (true == this.uniRuleMatrix.isColumnOdd(columnIndex)) {
            return true;
        }
        return false;
    }
    
    public GeneNode getNode(int row) {
        if (AnnotationType.GO.equals(annotType)) {
            if (row >= orderedNodes.size()) {
                System.out.println("Asking for row " + row + " which is > than the number of rows (" + orderedNodes.size() + ")");
                return null;
            } else if (row < 0) {
                System.out.println("Asking for negative row");
                return null;
            } else {
                return orderedNodes.get(row);
            }
        }
        if (null != uniRuleMatrix) {
            return uniRuleMatrix.getNodeAtRow(row);
        }
        return null;
    }
        
    public int getRow(GeneNode dsn) {
        if (AnnotationType.GO.equals(annotType)) {
            try {
                return orderedNodes.indexOf(dsn);
            } catch (NullPointerException e) {
                //log.debug("Could not find gene " + dsn.getSeqName() + " in contents");
                return -1;
            }
        }
        if (null != uniRuleMatrix) {
            return uniRuleMatrix.getRow(dsn);
        }        
        return -1;
    }
    
    public AnnotationType getAnnotType() {
        return this.annotType;
    }
    
    public Rule getRuleAtCol(int column) {
        if (annotType.UNI_RULE != annotType) {
            return null;
        }
        return uniRuleMatrix.getUniRules(column);
    }
    
    public boolean canCurate(int column) {
        if (annotType.UNI_RULE != annotType) {
            return false;
        }
        Rule r = this.getRuleAtCol(column);
        if (null == r) {
            return false;
        }
        // Ensure this is one of the types that can be annotated
        LabelValue lv = r.getLabelValue();
        if (null == lv) {
            return false;
        }
//        if (false == Rule.PAINT_RULES_SUPPORTED_BY_EXP_EVIDENCE.contains(lv.getLabel())) {
//            return false;
//        }
        if (false == r.isCuratable()) {
            return false;
        }
        return true;
    }

    public Rule getRuleById(String  id) {
        if (null == uniRuleMatrix){
            return null;
        }
        return uniRuleMatrix.getRuleById(id);
    }
    
    public int getGroup(int column) {
        if (null == uniRuleMatrix) {
            return UniRuleMatrix.GROUP_UNIRULE_INVALID;
        }
        return uniRuleMatrix.getGroupForCol(column);
    }
    
    public String getGroupIdForCol(int column) {
        if (null == uniRuleMatrix) {
            return null;
        }
        return uniRuleMatrix.getGroupIdForCol(column);        
    }
    
    public boolean ruleInMultipleCases(Rule r) {
        if (null == uniRuleMatrix) {
            return false;
        }
        return uniRuleMatrix.ruleInMultipleCases(r);
    }

    public boolean isColumnApplicableToNode(Node n, Integer column) {
        if (null == uniRuleMatrix) {
            return false;
        }
        return uniRuleMatrix.isColumnApplicableToNode(n, column);
    }    
    
    public void clear() {
        orderedNodes = null;
        matrixGroupList = null;
        uniRuleMatrix = null;
    }
    
}
