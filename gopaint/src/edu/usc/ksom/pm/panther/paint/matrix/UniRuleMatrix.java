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
package edu.usc.ksom.pm.panther.paint.matrix;

import edu.usc.ksom.pm.panther.paintCommon.Node;
import edu.usc.ksom.pm.panther.paintCommon.NodeVariableInfo;
import edu.usc.ksom.pm.panther.paintCommon.Rule;
import edu.usc.ksom.pm.panther.paintCommon.UAnnotation;
import edu.usc.ksom.pm.panther.paintCommon.UniRuleAnnotationGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import org.paint.dataadapter.AnnotationAdapter;
import org.paint.datamodel.GeneNode;
import org.paint.gui.matrix.RuleGroup;
import org.paint.main.PaintManager;


public class UniRuleMatrix {
    private List<GeneNode> curNodes;
    private ArrayList<String> uRuleList = new ArrayList<String>();
    private HashMap<String, Rule> uruleLookup = new HashMap<String, Rule>();
    
    // Rules ordered in following groups
    private RuleGroup existingUnirules = new RuleGroup();     // Rules for which unirule ids have been generated
    private ArrayList<RuleGroup> rulesInCases = new ArrayList<RuleGroup>();         // Rules that belong to unirule cases.  Each case belongs to a separate RuleGroup
    private RuleGroup swisssprotUnirulesNotInCases = new RuleGroup();    // Swissprot rules that do not belong to unirule cases
    private RuleGroup otherUnirulesNotInCases = new RuleGroup();    // other rules that do not belong to unirule cases
    
    // Use to make accessing easier
    private ArrayList<Rule> allRules = new ArrayList<Rule>();
    
    // These variables only have information about rules in cases
    private HashMap<String, ArrayList<Rule>> multipleLookup = new HashMap<String, ArrayList<Rule>>();
    private HashSet<Rule> rulesInMultipleCases = new HashSet<Rule>();
    
    public static int GROUP_UNIRULE_INVALID = -1;
    public static int GROUP_UNIRULE_ID_GENERATED = 0;
    public static int GROUP_UNIRULE_ID_CASES = 1;    
    public static int GROUP_UNIRULE_SWISSPROT_NOT_IN_CASES = 2;    
    public static int GROUP_UNIRULE_OTHER_NOT_IN_CASES = 3;
    
    public static final String DELIM_PRIMARY_RULE = "-";
    public static final String PREFIX_ANNOT_GROUP = "Annotation Group ";

    private Hashtable<Integer, Integer> colToGroupLookup = new Hashtable<Integer, Integer>();
    private HashSet<Integer> oddCols = new HashSet<Integer>();
    private Hashtable<Integer, RuleGroup> columnToRuleGroupForCasesLookup = new Hashtable<Integer, RuleGroup>();    

    
    public UniRuleMatrix() {
        PaintManager pm = PaintManager.inst();        
        List<GeneNode> nodes = pm.getTree().getTerminusNodes();
        if (null == nodes) {
            return;
        }
        curNodes = nodes;
        for (GeneNode gNode: nodes) {
            Node n = gNode.getNode();
            if (null == n) {
                continue;
            }
            
            NodeVariableInfo nvi = n.getVariableInfo();
            if (null == nvi) {
                continue;
            }
            
            ArrayList<UAnnotation> uAnnotList = nvi.getuAnnotationList();
            if (null == uAnnotList) {
                continue;
            }
            
            for (UAnnotation uAnnot : uAnnotList) {
                Rule rule = uAnnot.getRule();
                String id = rule.getId();
                if (null != id) {
                    if (false == uRuleList.contains(id)) {
                        uRuleList.add(id);
                    }

                    Rule r = uruleLookup.get(id);
                    if (null == r) {
                        uruleLookup.put(id, rule);
                    }
                }
            }           
        }
        
        // Add Rules that are specific for the family.  These are Inferred by Curator cases.  They are not based on experimental evidence
        ArrayList<Rule> familySpecRules = pm.getFamily().getRulesSpecificToFamily();
        if (null != familySpecRules) {
            for (Rule rule: familySpecRules) {
                String id = rule.getId();
                if (null != id) {
                    if (false == uRuleList.contains(id)) {
                        uRuleList.add(id);
                    }

                    Rule r = uruleLookup.get(id);
                    if (null == r) {
                        uruleLookup.put(id, rule);
                    }
                }
            }
        }
        ArrayList<String> ruleListCopy = (ArrayList<String>)uRuleList.clone();
        ArrayList<UniRuleAnnotationGroup> groups = AnnotationAdapter.groupUAnnotations();
        
        if (null != groups) {
            int count = 1;
            for (UniRuleAnnotationGroup urag: groups) {                
                ArrayList<Rule> rules = urag.getRules();
                
                RuleGroup rg = new RuleGroup();
                rg.setId(PREFIX_ANNOT_GROUP + Integer.toString(count));
                for (Rule r: rules) {
                    String labelValue = r.getLabel() + r.getValue();
                    ArrayList<Rule> ruleList = multipleLookup.get(labelValue);
                    if (null == ruleList) {
                        ruleList = new ArrayList<Rule>();
                        multipleLookup.put(labelValue, ruleList);
                    }
                    ruleList.add(r);
                    String id = r.getId();
                    rg.addRule(r);
                    ruleListCopy.remove(id);
                }

                rg.setApplicableNodeList(urag.getApplicableNodes());
                rulesInCases.add(rg);
                count++;
            }
        }
        
        // Save rules that are shared between multiple cases
        for (ArrayList<Rule> rules: multipleLookup.values()) {
            if (rules.size() > 1) {
                rulesInMultipleCases.addAll(rules);
            }
        }
        
        
        for (String ruleId: ruleListCopy) {
            if (ruleId.startsWith(Rule.UNIRULE_PREFIX_SWISS)) {
                swisssprotUnirulesNotInCases.addRule(uruleLookup.get(ruleId));
            }
            else if (ruleId.startsWith(Rule.UNIRULE_PREFIX_NEW)) {
                otherUnirulesNotInCases.addRule(uruleLookup.get(ruleId));
            }
            else {
                existingUnirules.addRule(uruleLookup.get(ruleId));
            }
        }
        
        // Add rules to the view
        addToAllRules(existingUnirules, GROUP_UNIRULE_ID_GENERATED);
        
        for (int i = 0; i < rulesInCases.size(); i++) {
            RuleGroup rg = rulesInCases.get(i);
            List<Rule> sortedRules = rg.getSortedRules();
            if (null == sortedRules) {
                continue;
            }
            boolean odd = (0 != i % 2);
            for (int j = 0; j < sortedRules.size(); j++) {
                int column = allRules.size();
                colToGroupLookup.put(column, GROUP_UNIRULE_ID_CASES);
                columnToRuleGroupForCasesLookup.put(column, rg);
                if (odd == true) {
                    oddCols.add(column);
                }
                allRules.add(sortedRules.get(j));
            }
        }
        addToAllRules(swisssprotUnirulesNotInCases, GROUP_UNIRULE_SWISSPROT_NOT_IN_CASES);
        addToAllRules(otherUnirulesNotInCases, GROUP_UNIRULE_OTHER_NOT_IN_CASES);     
    }
    
    private void addToAllRules(RuleGroup rg, int groupNumber) {
        List<Rule> sortedRules = rg.getSortedRules();
        if (null == sortedRules) {
            return;
        }
        String previous = null;
        int group = -1;
        for (int i = 0; i < sortedRules.size(); i++) {
            int column = allRules.size();
            colToGroupLookup.put(column, groupNumber);
            Rule rule = sortedRules.get(i);
            allRules.add(rule);
            String ruleId = rule.getId();
            int index = ruleId.indexOf(DELIM_PRIMARY_RULE);
            if (index < 0) {
                previous = ruleId;
                group++;
                if (0 != group % 2) {
                    oddCols.add(column);
                }
                continue;
            }
            String current = ruleId.substring(0, index);
            if (current.equals(previous)) {
                if (0 != group % 2) {
                    oddCols.add(column);
                }
            } else {
                previous = current;
                group++;
                if (0 != group % 2) {
                    oddCols.add(column);
                }

            }
        }       
    }
    
    public int getNumRulesInCases() {
        int count = 0;
        for (RuleGroup rg: rulesInCases) {
            count+= rg.getNumRules();
        }
        return count;
    }
    
    public int getGroupForCol(Integer column) {
        if (null == colToGroupLookup || null == column) {
            return -1;
        }
        Integer group =  colToGroupLookup.get(column);
        if (null == group) {
            return -1;
        }
        return group;
    }
    
    public boolean isColumnOdd(int column) {
        if (oddCols.contains(column)) {
            return true;
        }
        return false;
    }
    
    public int getNumRows() {
        if (null == curNodes) {
            return 0;
        }
        return curNodes.size();
    }

    public int getNumCols() {
        return allRules.size();
    }
    
    public GeneNode getNodeAtRow(int row) {
        if (null == curNodes || row < 0 || row >=curNodes.size()) {
            return null;
        }
        return curNodes.get(row);
    }
    
    public int getRow(GeneNode gn) {
        if (null == curNodes) {
            return -1;
        }
        return curNodes.indexOf(gn);
    }
    
    public String getUniRuleIdAtCol(int col) {
        if (null == allRules || col < 0 || col > allRules.size()) {
            return null;
        }
        return  allRules.get(col).getId();
    }
       
    public Rule getUniRules(int col) {
        if (null == allRules || col < 0 || col > allRules.size()) {
            return null;
        }
        return  allRules.get(col);
    }
    
    public Rule getRuleById(String id) {
        if (null == uruleLookup || null == id) {
            return null;
        }
        return uruleLookup.get(id);
    }
    
    public String getGroupIdForCol(Integer column) {
        if (null == column) {
            return null;
        }
        RuleGroup rg = columnToRuleGroupForCasesLookup.get(column);
        if (null == rg) {
            return null;
        }
        return rg.getId();
    }
    
    public boolean ruleInMultipleCases(String ruleId) {
        if (null == ruleId) {
            return false;
        }
        ArrayList<Rule> rules = multipleLookup.get(ruleId);
        if (null != rules && rules.size() > 0) {
            return true;
        }
        return false;
    }
    
    public boolean ruleInMultipleCases(Rule r) {
        if (null != r && rulesInMultipleCases.contains(r)) {
            return true;
        }
        return false;
    }
    
    public boolean isColumnApplicableToNode(Node n, Integer column) {
        if (null == column || null == n) {
            return false;
        }
        RuleGroup rg = columnToRuleGroupForCasesLookup.get(column);
        if (null == rg) {
            return false;
        }
        ArrayList<Node> nodeList = rg.getApplicableNodeList();
        if (null != nodeList && nodeList.contains(n)) {
            return true;
        }
        return false;
    }
    
}
