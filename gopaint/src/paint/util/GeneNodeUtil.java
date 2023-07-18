/**
 * Copyright 2023 University Of Southern California
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

import com.sri.panther.paintCommon.Constant;
import com.sri.panther.paintCommon.util.Utils;
import edu.usc.ksom.pm.panther.paintCommon.LabelValue;
import edu.usc.ksom.pm.panther.paintCommon.Node;
import edu.usc.ksom.pm.panther.paintCommon.NodeVariableInfo;
import edu.usc.ksom.pm.panther.paintCommon.Qualifier;
import edu.usc.ksom.pm.panther.paintCommon.Rule;
import edu.usc.ksom.pm.panther.paintCommon.UAnnotation;
import edu.usc.ksom.pm.panther.paintCommon.UAnnotationHelper;
import edu.usc.ksom.pm.panther.paintCommon.UniRuleAnnotationGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
import org.paint.datamodel.GeneNode;
import org.paint.go.GOConstants;
import org.paint.gui.AnnotationTypeSelector;
import org.paint.gui.matrix.AnnotationMatrixModel;
import org.paint.main.PaintManager;

public class GeneNodeUtil {

    public static final String DELIM = ",();";

    protected static final String SEMI_COLON = ";";
    protected static final String OPEN_PAREN = "(";
    protected static final String CLOSE_PAREN = ")";
    protected static final String COMMA = ",";
    protected static final String COLON = ":";
    protected static final String OPEN_BRACKET = "[";
    protected static final String CLOSE_BRACKET = "]";
    protected static final String NEWLINE = "\n";
    protected static final String TAB = "\t";
    protected static final String SPACE = " ";
    protected static final String PLUS = "+";

    private static final String NODE_TYPE_ANNOTATION = "ID=";
    private static final int NODE_TYPE_ANNOTATION_LENGTH = NODE_TYPE_ANNOTATION.length();

    public static final String MSG_INVALID_AN_ID = "Invalid annotation id encountered ";

    public static final String ERROR_MSG_DATA_FOR_NON_AN = "Found data for non-existant annotation node ";

    public static final String STR_EMPTY = "";

    public static final Logger logger = Logger.getLogger(GeneNodeUtil.class);

    private static GeneNodeUtil singleton = null;

    public static final String DATABASE_PREFIX_MGI = "MGI:";
    public static final String SPECIAL_CASE_TAIR = "TAIR";
    public static final String SPECIAL_CASE_MGI = "MGI";
    public static final String SPECIAL_CASE_SOURCES[] = {SPECIAL_CASE_TAIR, SPECIAL_CASE_MGI};     // These evidence sources contain extra ":=", etc

    public static final String MSG_ERR_NO_CASES_TO_IMPORT = "ERROR, no cases to import\n";
    public static final String MSG_ERR_NO_NODES_WITH_UNIRULE_EVIDENCE = "ERROR, no UniRules with evidence";
    public static final String MSG_ERROR_UNABLE_TO_FIND_UNIRULE_NODE = "Error, Unable to find Unirule node ";
    public static final String MSG_ERROR_INVALID_ANCESTOR_DESCENDANT_INFORMATION = "Error, invalid ancestor and descendant information";
    public static final String MSG_ERROR_INVALID_UNIRULE_PRIMARY_DESC_INFO_PART_1 = "Error, invalid primary node ";
    public static final String MSG_ERROR_INVALID_UNIRULE_PRIMARY_DESC_INFO_PART_2 = " with node ";
    public static final String MSG_ERROR_INVALID_UNIRULE_NEG_CONDITION_NODE_PART_1 = "Error, invalid primary node ";
    public static final String MSG_ERROR_INVALID_UNIRULE_NEG_CONDITION_NODE_PART_2 = " with node ";
    public static final String MSG_ERROR_INVALID_UNIRULE_NEG_CONDITION_NODE_PART_3 = " with neg node ";
    public static final String MSG_ERROR_MAIN_CONDITION_IS_NEGATIVE = "Main condition is negative";
    public static final String MSG_ERROR_NO_SWISSPROT_ANNOTATIONS = "No Swisssprot annotations";    
    public static final String MSG_ERROR_UNABLE_TO_FIND_RULE_FOR_LABEL_PART_1 = "Error, unable to find rule ";
    public static final String MSG_ERROR_UNABLE_TO_FIND_RULE_FOR_LABEL_PART_2 = " with label ";
    public static final String MSG_ERROR_UNABLE_TO_FIND_RULE_FOR_LABEL_PART_3 = " for rule id ";    
    public static final String MSG_ERROR_UNABLE_TO_ANNOTATE_WITH_PART_1 = "Error, unable to annotate with rule ";
    public static final String MSG_ERROR_UNABLE_TO_ANNOTATE_WITH_PART_2 = " with label ";
    public static final String MSG_ERROR_UNABLE_TO_ANNOTATE_WITH_PART_3 = " for node ";    
    public static final String MSG_ERROR_UNABLE_TO_ANNOTATE_WITH_PART_4 = " due to error - ";
    public static final String MSG_ERROR_UNABLE_TO_ANNOTATE_NODE_WITH_IBD_PART_1 = "Unable to create IBD for node ";
    public static final String MSG_ERROR_UNABLE_TO_ANNOTATE_NODE_WITH_IBD_PART_2 = " with label ";
    public static final String MSG_ERROR_UNABLE_TO_ANNOTATE_NODE_WITH_IBD_PART_3 = " and value ";    
    public static final String MSG_ERROR_UNABLE_TO_ANNOTATE_NODE_WITH_UIC_PART_1 = "Unable to create UIC for node ";
    public static final String MSG_ERROR_UNABLE_TO_ANNOTATE_NODE_WITH_UIC_PART_2 = " with label ";
    public static final String MSG_ERROR_UNABLE_TO_ANNOTATE_NODE_WITH_UIC_PART_3 = " and value ";
    public static final String MSG_ERROR_UNABLE_TO_FIND_IBA_ANNOTATION_WITH_PART_1 = "Error, unable to find iba annotation for rule ";
    public static final String MSG_ERROR_UNABLE_TO_FIND_IBA_ANNOTATION_WITH_PART_2 = " with label ";
    public static final String MSG_ERROR_UNABLE_TO_CREATE_IRD_ANNOTATION_WITH_PART_1 = "Error, unable to create IRD annotation for rule ";
    public static final String MSG_ERROR_UNABLE_TO_CREATE_IRD_ANNOTATION_WITH_PART_2 = " with label ";
    public static final String MSG_ERROR_UNIRULE_UNDEFINED = "Error, rule not defined";    
    public static final String MSG_ERROR_UNIRULE_NOT_FOUND_PART_1 = "Error, unable to find rule ";
    public static final String MSG_ERROR_UNIRULE_NOT_FOUND_PART_2 = " with label ";

    public GeneNodeUtil() {
    }

    public static GeneNodeUtil inst() {
        if (singleton == null) {
            singleton = new GeneNodeUtil();
        }
        return singleton;
    }

    /**
     *
     * @param treeContents
     * @param sfAn
     * @return
     */
    public GeneNode parseTreeData(String[] treeContents, HashMap<String, Node> nodeLookup, String familyId) {
        if (null == treeContents) {
            return null;
        }
        if (0 == treeContents.length) {
            return null;
        }
        // Modify, if there are no line returns
        if (1 == treeContents.length) {
            treeContents = Utils.tokenize(treeContents[0], SEMI_COLON);
        }

        GeneNode root = parseTreeString(treeContents[0]);

//		// Get subfamily to annotation node relationships
//		Hashtable<String, String> AnToSFTbl = null;
//		if (sfAn != null) {
//			AnToSFTbl = SubFamilyUtil.parseSfAnInfo(sfAn, true);
//			if (AnToSFTbl == null) {
//				return null;
//			}
//			String sfName = AnToSFTbl.get(root.getSeqId());
//			if (null != sfName) {
//				root.setSubFamilyName(sfName);
//				root.setIsSubfamily(true);
//			}
//		}
//		addSfInfoToMainTree(root, AnToSFTbl);
        for (int i = 1; i < treeContents.length; i++) {
            String line = treeContents[i];
            int index = line.indexOf(COLON);
            String anId = line.substring(0, index);
            GeneNode node = PaintManager.inst().getGeneByPaintId(anId);
            if (null == node) {
                logger.error(ERROR_MSG_DATA_FOR_NON_AN + anId);
                continue;
            }
//                        if (null != nodeLookup) {
//                            String lookupId = familyId + ":" + anId;
//                            Node n = nodeLookup.get(lookupId);
//                            node.setNode(n);
//                            node.setPersistantNodeID(n.getStaticInfo().getPublicId());
//                        }
            // minus 1 to trim the semi-colon off?
            PantherParseUtil.inst().parseIDstr(node, line.substring(index + 1));
            /*
			 * Both database info and sequence info should now be set in this leaf node
             */
//			PaintManager.inst().indexNode(node);
        }
        initNodeProperties(root, nodeLookup, familyId);

        return root;

    }

    private GeneNode parseTreeString(String s) {
        GeneNode node = null;
        GeneNode root = null;
        StringTokenizer st = new StringTokenizer(s, DELIM, true);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.equals(OPEN_PAREN)) {
                if (null == node) {
                    /*
					 * The real root node, first one set
                     */
                    node = new GeneNode(false);
                    root = node;
                } else {
                    GeneNode newChild = new GeneNode(false);
                    List<GeneNode> children = node.getChildren();
                    if (null == children) {
                        children = new ArrayList<GeneNode>();
                    }
                    children.add(newChild);
                    newChild.setParent(node);
                    node.setChildren(children);
                    /*
					 * Move down
                     */
                    node = newChild;
                    node.setExpanded(true);
                }
            } else if ((token.equals(CLOSE_PAREN))
                    || (token.equals(COMMA))
                    || (token.equals(SEMI_COLON))) {
                // Do nothing
            } else {
                int squareIndexStart = token.indexOf(OPEN_BRACKET);
                int squareIndexEnd = token.indexOf(CLOSE_BRACKET);
                if (0 == squareIndexStart) {
                    String type = token.substring(squareIndexStart, squareIndexEnd + 1);
                    /* 
					 * This is when the AN number is teased out
                     */
                    setTypeAndId(type, node);
                } else {
                    int index = token.indexOf(COLON);
                    if (0 == index) {
                        if (-1 == squareIndexStart) {
                            node.setDistanceFromParent(Float.valueOf(token.substring(index + 1)).floatValue());
                        } else {
                            node.setDistanceFromParent(Float.valueOf(token.substring((index + 1), squareIndexStart)).floatValue());
                            String type = token.substring(squareIndexStart, squareIndexEnd + 1);
                            /* 
							 * This is when the AN number is teased out
                             */
                            setTypeAndId(type, node); // this use to be included in setType itself
                        }
                        /*
						 * Move back up
                         */
                        node = (GeneNode) node.getParent();
                    } else if (index > 0) {
                        GeneNode newChild = new GeneNode(false);
                        if (-1 == squareIndexStart) {
                            newChild.setDistanceFromParent(Float.valueOf(token.substring(index + 1)).floatValue());
                            setTypeAndId(token.substring(0, index), newChild); // this use to be included in setType itself
                        } else {
                            newChild.setDistanceFromParent(Float.valueOf(token.substring((index + 1), squareIndexStart)).floatValue());
                            String type = token.substring(squareIndexStart, squareIndexEnd + 1);
                            /* 
							 * This is when the AN number is teased out
                             */
                            setTypeAndId(type, newChild); // this use to be included in setType itself
                        }
                        List<GeneNode> children = node.getChildren();
                        if (null == children) {
                            children = new ArrayList<GeneNode>();
                        }
                        /*
						 * Add siblings to current node
                         */
                        children.add(newChild);
                        newChild.setParent(node);
                        node.setChildren(children);
                        node.setExpanded(true);
                    }
                }
            }
        }
        return root;
    }

//	private void addSfInfoToMainTree(GeneNode node, Hashtable<String, String> AnToSFTbl) {
//		if (null == node) {
//			return;
//		}
//		String anId = node.getSeqId();
//		if (null != anId) {
//			String sfId = AnToSFTbl.get(anId);
//			if (null != sfId) {
//				node.setSubFamilyName(sfId);
//				node.setIsSubfamily(true);
//			}
//		}
//		List<GeneNode> v = node.getChildren();
//		if (null == v) {
//			return;
//		}
//		for (int i = 0; i < v.size(); i++) {
//			addSfInfoToMainTree(v.get(i), AnToSFTbl);
//		}
//
//	}
    private void initNodeProperties(GeneNode node, HashMap<String, Node> nodeLookup, String familyId) {
        if (null == node) {
            return;
        }
        String anId = node.getPaintId();
        if (null != nodeLookup) {
            String lookupId = familyId + ":" + anId;
            Node n = nodeLookup.get(lookupId);
            if (null != n) {
                node.setNode(n);
                node.setPersistantNodeID(n.getStaticInfo().getPublicId());
                String longGeneName = n.getStaticInfo().getLongGeneName();
                if (null != longGeneName) {

                    String parts[] = longGeneName.split(Constant.STR_PIPE);
                    int length = parts.length;
                    if (length < 2) {
                        return;
                    }
                    //organism = parts[0];

                    // Gene part
                    String geneParts[] = parts[1].split(Constant.STR_EQUAL);
                    if (geneParts.length < 2) {
                        return;
                    }
                    String geneSource = geneParts[0];
                    String geneId = geneParts[1];

                    /*
                     * Check for special cases
                     * MGI id is of the form MOUSE|MGI=MGI=97788|UniProtKB=Q99LS3
                     */
                    if (true == Utils.search(SPECIAL_CASE_SOURCES, geneSource)) {
                        if (geneSource.equals(SPECIAL_CASE_MGI) && !geneSource.startsWith(DATABASE_PREFIX_MGI)) {
                            if (geneParts.length >= 3) {
                                geneId = DATABASE_PREFIX_MGI + geneParts[2];
                            } else {
                                geneId = DATABASE_PREFIX_MGI + geneId;
                            }
                        }
                        if (geneSource.equals(SPECIAL_CASE_TAIR)) {
                            if (geneParts.length >= 3) {
                                geneId = geneParts[2];
                            }
                        }
                    }
                    node.setGeneSource(geneSource);
                    node.setGeneId(geneId);

                    // Protein part 
                    String proteinParts[] = parts[2].split(Constant.STR_EQUAL);
                    if (proteinParts.length < 2) {
                        return;
                    }
                    node.setProteinSource(proteinParts[0]);
                    node.setProteinId(proteinParts[1]);

                }
                NodeVariableInfo nvi = n.getVariableInfo();
                if (null != nvi && true == nvi.isPruned()) {
                    node.setPrune(true);
                }
            } else {
                System.out.println("Did not find node information for " + lookupId);
            }
        } else {
            System.out.println("Did not find any node information to match with tree");
        }
        node.setOriginalChildrenToCurrentChildren();
        List<GeneNode> children = node.getChildren();
        if (null == children) {
            node.setExpanded(false);
        } else {
            node.setExpanded(true);
            for (int i = 0; i < children.size(); i++) {
                initNodeProperties(children.get(i), nodeLookup, familyId);
            }
        }
    }

    public void setVisibleRows(List<GeneNode> node_list, List<GeneNode> contents) {
        contents.clear();
        contents.addAll(node_list);
    }

    private void setTypeAndId(String nodeType, GeneNode node) {
        if (null == nodeType) {
            return;
        }
        String annot_id;
        if (!nodeType.startsWith("AN")) {
            node.setType(nodeType);
            // collect the species while we're at it
            int index = nodeType.indexOf("S=");
            if (index >= 0) {
                int endIndex = nodeType.indexOf(COLON, index);
                if (-1 == endIndex) {
                    endIndex = nodeType.indexOf(CLOSE_BRACKET);
                }
                String species = nodeType.substring(index + "S=".length(), endIndex);
                node.setSpecies(species);
                node.addSpeciesLabel(species);
            }
            // now pick up the node name/id
            index = nodeType.indexOf(NODE_TYPE_ANNOTATION);
            if (index >= 0) {
                int endIndex = nodeType.indexOf(COLON, index);
                if (-1 == endIndex) {
                    endIndex = nodeType.indexOf(CLOSE_BRACKET);
                }
                annot_id = nodeType.substring(index + NODE_TYPE_ANNOTATION_LENGTH, endIndex);
            } else {
                annot_id = null;
            }
        } else {
            annot_id = nodeType;
        }
        // now pick up the node name/id
        if (annot_id != null) {
            if (!annot_id.startsWith("AN")) {
                logger.debug(annot_id + " isn't an AN number");
            }
            if (node.getPaintId().length() > 0) {
                logger.debug(annot_id + "AN number is already set to " + node.getPaintId());
            }
            node.setPaintId(annot_id);
//			PaintManager.inst().indexNode(node);
        }

    }

    public static void allDescendents(GeneNode gNode, List<GeneNode> nodeList) {
        if (null == gNode || null == nodeList) {
            return;
        }
        List<GeneNode> children = gNode.getChildren();
        if (null == children) {
            return;
        }
        for (GeneNode child : children) {
            nodeList.add(child);
            allDescendents(child, nodeList);
        }
    }

    public static void getAncestors(GeneNode gNode, List<GeneNode> ancestors) {
        if (null == gNode || null == ancestors) {
            return;
        }
        GeneNode parent = gNode.getParent();
        if (null != parent) {
            ancestors.add(parent);
            getAncestors(parent, ancestors);
        }
    }

    public static void allNonPrunedDescendents(GeneNode gNode, List<GeneNode> nodeList) {
        if (null == gNode || null == nodeList) {
            return;
        }
        Node n = gNode.getNode();
        NodeVariableInfo nvi = n.getVariableInfo();
        if (null != nvi && nvi.isPruned()) {
            return;
        }
        List<GeneNode> children = gNode.getChildren();
        if (null == children) {
            return;
        }
        for (GeneNode child : children) {
            Node cn = child.getNode();
            NodeVariableInfo childNvi = cn.getVariableInfo();
            if (null != childNvi && childNvi.isPruned()) {
                continue;
            }
            nodeList.add(child);
            allNonPrunedDescendents(child, nodeList);
        }
    }

    public static boolean inPrunedBranch(GeneNode gNode) {
        if (true == gNode.isPruned()) {
            return true;
        }
        GeneNode copy = gNode;
        return hasPrunedAncestor(copy);
    }

    private static boolean hasPrunedAncestor(GeneNode gNode) {

        GeneNode parent = gNode.getParent();
        if (null == parent) {
            return false;
        }
        if (true == parent.isPruned()) {
            return true;
        }
        return hasPrunedAncestor(parent);
    }

    public static List<GeneNode> getAllLeaves(List<GeneNode> list) {
        if (null == list) {
            return null;
        }
        ArrayList<GeneNode> rtnList = new ArrayList<GeneNode>();
        for (GeneNode gNode : list) {
            if (true == gNode.isLeaf()) {
                rtnList.add(gNode);
            }
        }
        return rtnList;
    }

    public static boolean hasDirectAnnotation(GeneNode gNode) {
        Node n = gNode.getNode();
        NodeVariableInfo nvi = n.getVariableInfo();
        if (null == nvi) {
            return false;
        }
        ArrayList<edu.usc.ksom.pm.panther.paintCommon.Annotation> annotList = nvi.getGoAnnotationList();
        if (null == annotList) {
            return false;
        }
        for (edu.usc.ksom.pm.panther.paintCommon.Annotation annot : annotList) {
            //edu.usc.ksom.pm.panther.paintCommon.Evidence e = annot.getEvidence();
            String evidenceCode = annot.getSingleEvidenceCodeFromSet();
            if (GOConstants.DESCENDANT_SEQUENCES_EC.equals(evidenceCode) || GOConstants.KEY_RESIDUES_EC.equals(evidenceCode) || GOConstants.DIVERGENT_EC.equals(evidenceCode)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasAllPropagatedAnnotation(GeneNode gNode) {
        Node n = gNode.getNode();
        NodeVariableInfo nvi = n.getVariableInfo();
        if (null == nvi) {
            return false;
        }
        ArrayList<edu.usc.ksom.pm.panther.paintCommon.Annotation> annotList = nvi.getGoAnnotationList();
        if (null == annotList || 0 == annotList.size()) {
            return false;
        }
        for (edu.usc.ksom.pm.panther.paintCommon.Annotation annot : annotList) {
            //edu.usc.ksom.pm.panther.paintCommon.Evidence e = annot.getEvidence();
            String evidenceCode = annot.getSingleEvidenceCodeFromSet();
            if (GOConstants.DESCENDANT_SEQUENCES_EC.equals(evidenceCode) || GOConstants.KEY_RESIDUES_EC.equals(evidenceCode) || GOConstants.DIVERGENT_EC.equals(evidenceCode)) {
                return false;
            }
        }
        return true;
    }
    
    private void processNode(Node n, HashSet<UniRuleAnnotationGroup> errorGrpSet, LinkedHashMap<UniRuleAnnotationGroup, ArrayList<String>> caseLookup, AnnotationMatrixModel arm, boolean canUniruleBeCreatedForBook) {
        for (Entry<UniRuleAnnotationGroup, ArrayList<String>> caseEntry: caseLookup.entrySet()) {
            UniRuleAnnotationGroup uag = caseEntry.getKey();
            if (errorGrpSet.contains(uag)) {
                continue;
            }
            ArrayList<String> curMsgList = caseEntry.getValue();
            
            Node mainNode = uag.getMainConditionNode();
            
            // Create IBD or UIC
            if (n == mainNode) {
                // First check if unirule can be created for book
                if (false == canUniruleBeCreatedForBook) {
                   curMsgList.add(MSG_ERROR_NO_SWISSPROT_ANNOTATIONS);
                   errorGrpSet.add(uag);
                   continue;                    
                }
                
                boolean isNeg = uag.getMainCondNegative();
               // Currently main condition cannot be negative.  Reject these for now
               if (true == isNeg) {
                   curMsgList.add(MSG_ERROR_MAIN_CONDITION_IS_NEGATIVE);
                   errorGrpSet.add(uag);
                   continue;
               }
               
                ArrayList<Rule> uRules = uag.getRules();
                // Need to get corresponding rules in matrix.
                // Check if all matching entries can be found
                if (null == uRules || 0 == uRules.size()) {
                    curMsgList.add(MSG_ERROR_UNIRULE_UNDEFINED);
                    errorGrpSet.add(uag);
                    continue;
                }
                
                // Create an annotation for each rule.
                for (Rule uRule : uRules) {
                    LabelValue uLv = uRule.getLabelValue();
                    Rule rule = arm.getRuleById(uRule.getId());
                    if (null == rule) {
                        curMsgList.add(MSG_ERROR_UNABLE_TO_FIND_RULE_FOR_LABEL_PART_1 + uLv.getLabel()
                                + MSG_ERROR_UNABLE_TO_FIND_RULE_FOR_LABEL_PART_2 + uLv.getValue() + MSG_ERROR_UNABLE_TO_FIND_RULE_FOR_LABEL_PART_3 + uRule.getId());
                        errorGrpSet.add(uag);
                        continue;
                    }

                    LabelValue lv = rule.getLabelValue();
                    String label = lv.getLabel();
                    boolean isIbdAnnot = true;
                    if (false == Rule.PAINT_RULES_SUPPORTED_BY_EXP_EVIDENCE.contains(label)) {
                        isIbdAnnot = false;     // i.e. create UIC
                    }

                    // Create IBD and IRD if necessary
                    if (isIbdAnnot) {
                        StringBuffer errorBuf = new StringBuffer();
                        boolean canCurate = UAnnotationHelper.possibleToAnnotateWithIBDRule(rule, mainNode, errorBuf);
                        if (false == canCurate) {
                            curMsgList.add(MSG_ERROR_UNABLE_TO_ANNOTATE_WITH_PART_1 + lv.getLabel()
                                    + MSG_ERROR_UNABLE_TO_ANNOTATE_WITH_PART_2 + lv.getValue()
                                    + MSG_ERROR_UNABLE_TO_ANNOTATE_WITH_PART_3 + mainNode.getStaticInfo().getPublicId()
                                    + MSG_ERROR_UNABLE_TO_ANNOTATE_WITH_PART_4 + errorBuf.toString());
                            errorGrpSet.add(uag);
                            continue;
                        }

                        // For now, main annotation cannot have NOT qualifier
                        HashSet<Qualifier> qualifierSet = null;
//                    if (true == isNeg) {
//                        qualifierSet = new HashSet<Qualifier>();
//                        Qualifier q = new Qualifier();
//                        q.setText(Qualifier.QUALIFIER_NOT);
//                        qualifierSet.add(q);
//                    }
                        UAnnotation ibd = UAnnotationHelper.addIBDAnnotRuleAndPropagateIBA(rule, mainNode, qualifierSet);                  
                        if (null == ibd) {
                            curMsgList.add(MSG_ERROR_UNABLE_TO_ANNOTATE_NODE_WITH_IBD_PART_1 + mainNode.getStaticInfo().getPublicId()
                                    + MSG_ERROR_UNABLE_TO_ANNOTATE_NODE_WITH_IBD_PART_2 + lv.getLabel()                                    
                                    + MSG_ERROR_UNABLE_TO_ANNOTATE_NODE_WITH_IBD_PART_3 + lv.getValue());
                            errorGrpSet.add(uag);
                            continue;                            
                        }
                    }
                    else {
                        // UIC
                        StringBuffer errorBuf = new StringBuffer();
                        boolean canCurate = UAnnotationHelper.possibleToAnnotateWithUICRule(rule, mainNode, errorBuf);
                        if (false == canCurate) {
                            curMsgList.add(MSG_ERROR_UNABLE_TO_ANNOTATE_WITH_PART_1 + lv.getLabel()
                                    + MSG_ERROR_UNABLE_TO_ANNOTATE_WITH_PART_2 + lv.getValue()
                                    + MSG_ERROR_UNABLE_TO_ANNOTATE_WITH_PART_3 + mainNode.getStaticInfo().getPublicId()
                                    + MSG_ERROR_UNABLE_TO_ANNOTATE_WITH_PART_4 + errorBuf.toString());
                            errorGrpSet.add(uag);
                            continue;
                        }

                        // For now, main annotation cannot have NOT qualifier
                        HashSet<Qualifier> qualifierSet = null;
//                    if (true == isNeg) {
//                        qualifierSet = new HashSet<Qualifier>();
//                        Qualifier q = new Qualifier();
//                        q.setText(Qualifier.QUALIFIER_NOT);
//                        qualifierSet.add(q);
//                    }
                        UAnnotation uic = UAnnotationHelper.addUICAnnotRuleAndPropagateIBA(rule, mainNode, qualifierSet);
                        if (null == uic) {
                            curMsgList.add(MSG_ERROR_UNABLE_TO_ANNOTATE_NODE_WITH_UIC_PART_1 + mainNode.getStaticInfo().getPublicId()
                                    + MSG_ERROR_UNABLE_TO_ANNOTATE_NODE_WITH_UIC_PART_2 + lv.getLabel()                                    
                                    + MSG_ERROR_UNABLE_TO_ANNOTATE_NODE_WITH_UIC_PART_2 + lv.getValue());
                            errorGrpSet.add(uag);
                            continue;                            
                        }
                    }
                }
            }
            else {
                // Add IRD for dependant nodes
                ArrayList<Node> irdNodes = uag.getDependantConditionNodes();
                if (null != irdNodes) {
                    ArrayList<Rule> uRules = uag.getRules();
                    // Need to get corresponding rules in matrix.
                    // Check if all matching entries can be found
                    if (null == uRules || 0 == uRules.size()) {
                        curMsgList.add(MSG_ERROR_UNIRULE_UNDEFINED);
                        errorGrpSet.add(uag);
                        continue;
                    }

                    for (Node descendant : irdNodes) {
                        if (n != descendant) {
                            continue;
                        }

                        for (Rule uRule : uRules) {
                            LabelValue uLv = uRule.getLabelValue();
                            String id = uRule.getId();
                            Rule rule = arm.getRuleById(id);
                            if (null == rule) {
                                curMsgList.add(MSG_ERROR_UNABLE_TO_FIND_RULE_FOR_LABEL_PART_1 + uLv.getLabel()
                                        + MSG_ERROR_UNABLE_TO_FIND_RULE_FOR_LABEL_PART_2 + uLv.getValue() + MSG_ERROR_UNABLE_TO_FIND_RULE_FOR_LABEL_PART_3 + id);
                                errorGrpSet.add(uag);
                                continue;
                            }

                            // Find IBA annotation
                            UAnnotation iba = UAnnotationHelper.findIBAAnnot(rule, descendant);
                            if (null == iba) {
                                curMsgList.add(MSG_ERROR_UNABLE_TO_FIND_IBA_ANNOTATION_WITH_PART_1 + uLv.getLabel()
                                        + MSG_ERROR_UNABLE_TO_FIND_IBA_ANNOTATION_WITH_PART_2 + uLv.getValue());
                                errorGrpSet.add(uag);
                                continue;
                            }

                            // Add IRD
                            boolean success = UAnnotationHelper.addIRD(iba);
                            if (false == success) {
                                UAnnotation propagator = UAnnotationHelper.getImmediatePropagator(iba);
                                UAnnotationHelper.deleteAnnotationAndRepropagate(propagator);
                                curMsgList.add(MSG_ERROR_UNABLE_TO_CREATE_IRD_ANNOTATION_WITH_PART_1 + uLv.getLabel()
                                        + MSG_ERROR_UNABLE_TO_CREATE_IRD_ANNOTATION_WITH_PART_2 + uLv.getValue());
                                errorGrpSet.add(uag);
                                continue;
                            }
                        }
                    }
                }
            }
        }
        
        // Process descendants
        ArrayList<Node> children = n.getStaticInfo().getChildren();
        if (null != children) {
            for (Node child: children) {
                processNode(child, errorGrpSet, caseLookup, arm, canUniruleBeCreatedForBook);
            }
        }
    }

    public StringBuffer addUniruleAnnot(ArrayList<UniRuleAnnotationGroup> cases, Node root) {
        if (null == cases || 0 == cases.size()) {
            return new StringBuffer(MSG_ERR_NO_CASES_TO_IMPORT);
        }
        LinkedHashMap<UniRuleAnnotationGroup, ArrayList<String>> caseLookup = new LinkedHashMap<UniRuleAnnotationGroup, ArrayList<String>>();
        for (UniRuleAnnotationGroup uag: cases) {
            caseLookup.put(uag, new ArrayList<String>());
        }
        PaintManager pm = PaintManager.inst();
        //HashSet<UniRuleAnnotationGroup> errorGrpSet = new HashSet<UniRuleAnnotationGroup>();
        processNode(root, new HashSet<UniRuleAnnotationGroup>(), caseLookup, pm.getMatrix().getAnnotationMatrixModel(AnnotationTypeSelector.AnnotationType.UNIRULE.toString()), pm.canUniRulesBeCreatedForBook());
        ArrayList<String> allMsgs = new ArrayList<String>();
        for (ArrayList<String> errorMsgList: caseLookup.values()) {
            if (errorMsgList.isEmpty()) {
                continue;
            }
            allMsgs.addAll(errorMsgList);
        }
        return new StringBuffer(String.join(Constant.STR_NEWLINE, allMsgs));
    }

//    public StringBuffer addUniruleAnnotOrig(ArrayList<UniRuleAnnotationGroup> cases) {
// 
//        if (null == cases || 0 == cases.size()) {
//            return new StringBuffer(MSG_ERR_NO_CASES_TO_IMPORT);
//        }
//
//        //StringBuffer errorMsg = new StringBuffer();
//        ArrayList<String> errorMsgList = new ArrayList<String>();
//        PaintManager pm = PaintManager.inst();
//        AnnotationMatrixModel arm = pm.getMatrix().getAnnotationMatrixModel(AnnotationTypeSelector.AnnotationType.UNIRULE.toString());
//        if (null == arm) {
//            errorMsgList.add(MSG_ERR_NO_NODES_WITH_UNIRULE_EVIDENCE);
//        }
//        
//        // Case Conditions
//        // One with IBD and zero or more with IRD   OR One with UIC and zero or more with IRD
//        for (UniRuleAnnotationGroup uag : cases) {
//            Node mainNode = uag.getMainConditionNode();
//            boolean isNeg = uag.getMainCondNegative();
//            
//            // Currently main condition cannot be negative.  Reject these for now
//            if (true == isNeg) {
//                errorMsgList.add(MSG_ERROR_MAIN_CONDITION_IS_NEGATIVE);
//                continue;
//            }
//            ArrayList<Node> irdNodes = uag.getDependantConditionNodes();
//            ArrayList<Rule> uRules = uag.getRules();
//            // Need to get corresponding rules in matrix.
//            // Check if all matching entries can be found
//            if (null == uRules || 0 == uRules.size()) {
//                errorMsgList.add(MSG_ERROR_UNIRULE_UNDEFINED);
//                continue;
//            }
//            
//            // Create an annotation for each rule.  If there is a descendant node, then create an IRD for the rule at the descendant node
//            for (Rule uRule : uRules) {
//                LabelValue uLv = uRule.getLabelValue();
//                Rule rule = arm.getNewRuleForLabelValue(uLv);
//                if (null == rule) {
//                    errorMsgList.add(MSG_ERROR_UNABLE_TO_FIND_RULE_FOR_LABEL_PART_1 + uLv.getLabel() + 
//                    MSG_ERROR_UNABLE_TO_FIND_RULE_FOR_LABEL_PART_2 + uLv.getValue());
//                    continue;
//                }
//                LabelValue lv = rule.getLabelValue();
//                String label = lv.getLabel();
//                boolean isIbdAnnot = true;
//                if (false == Rule.PAINT_RULES_SUPPORTED_BY_EXP_EVIDENCE.contains(label)) {
//                    isIbdAnnot = false;     // i.e. create UIC
//                }
//
//                // Create IBD and IRD if necessary
//                if (isIbdAnnot) {
//                    StringBuffer errorBuf = new StringBuffer();
//                    boolean canCurate = UAnnotationHelper.possibleToAnnotateWithIBDRule(rule, mainNode, errorBuf);
//                    if (false == canCurate) {
//                        errorMsgList.add(MSG_ERROR_UNABLE_TO_ANNOTATE_WITH_PART_1 + lv.getLabel() +
//                        MSG_ERROR_UNABLE_TO_ANNOTATE_WITH_PART_2 + lv.getValue() +
//                        MSG_ERROR_UNABLE_TO_ANNOTATE_WITH_PART_3 + mainNode.getStaticInfo().getPublicId() +
//                        MSG_ERROR_UNABLE_TO_ANNOTATE_WITH_PART_4 + errorBuf.toString());
//                        continue;
//                    }
//
//                    // For now, main annotation cannot have NOT qualifier
//                    HashSet<Qualifier> qualifierSet = null;
////                    if (true == isNeg) {
////                        qualifierSet = new HashSet<Qualifier>();
////                        Qualifier q = new Qualifier();
////                        q.setText(Qualifier.QUALIFIER_NOT);
////                        qualifierSet.add(q);
////                    }
//                    UAnnotation ibd = UAnnotationHelper.addIBDAnnotRuleAndPropagateIBA(rule, mainNode, qualifierSet);
//
//                    if (null != irdNodes) {
//                        for (Node descendant : irdNodes) {
//                            // Find IBA annotation
//                            UAnnotation iba = UAnnotationHelper.findIBAAnnot(rule, descendant);
//                            if (null == iba) {
//                                UAnnotationHelper.deleteAnnotationAndRepropagate(ibd);
//                                errorMsgList.add(MSG_ERROR_UNABLE_TO_FIND_IBA_ANNOTATION_WITH_PART_1 + lv.getLabel() +
//                                MSG_ERROR_UNABLE_TO_FIND_IBA_ANNOTATION_WITH_PART_2 + lv.getValue());
//                                continue;
//                            }
//                            boolean success = UAnnotationHelper.addIRD(iba);
//                            if (false == success) {
//                                UAnnotationHelper.deleteAnnotationAndRepropagate(ibd);
//                                errorMsgList.add(MSG_ERROR_UNABLE_TO_CREATE_IRD_ANNOTATION_WITH_PART_1 + lv.getLabel() +
//                                MSG_ERROR_UNABLE_TO_CREATE_IRD_ANNOTATION_WITH_PART_2 + lv.getValue());
//                                continue;
//                            }
//                        }
//                    }
//                }
//                else {
//                    // UIC
//                    boolean canCurate = UAnnotationHelper.possibleToAnnotateWithUICRule(rule, mainNode);
//                    if (false == canCurate) {
//                        errorMsgList.add(MSG_ERROR_UNABLE_TO_ANNOTATE_WITH_PART_1 + lv.getLabel() +
//                        MSG_ERROR_UNABLE_TO_ANNOTATE_WITH_PART_2 + lv.getValue() +
//                        MSG_ERROR_UNABLE_TO_ANNOTATE_WITH_PART_3 + mainNode.getStaticInfo().getPublicId());                        
//                        continue;
//                    }
//
//                    // For now, main annotation cannot have NOT qualifier
//                    HashSet<Qualifier> qualifierSet = null;
////                    if (true == isNeg) {
////                        qualifierSet = new HashSet<Qualifier>();
////                        Qualifier q = new Qualifier();
////                        q.setText(Qualifier.QUALIFIER_NOT);
////                        qualifierSet.add(q);
////                    }
//                    UAnnotation uic = UAnnotationHelper.addUICAnnotRuleAndPropagateIBA(rule, mainNode, qualifierSet);
//
//                    if (null != irdNodes) {
//                        for (Node descendant : irdNodes) {
//                            // Find IBA annotation
//                            UAnnotation iba = UAnnotationHelper.findIBAAnnot(rule, descendant);
//                            if (null == iba) {
//                                UAnnotationHelper.deleteAnnotationAndRepropagate(uic);
//                                errorMsgList.add(MSG_ERROR_UNABLE_TO_FIND_IBA_ANNOTATION_WITH_PART_1 + lv.getLabel() +
//                                MSG_ERROR_UNABLE_TO_FIND_IBA_ANNOTATION_WITH_PART_2 + lv.getValue());
//                                continue;
//                            }
//                            boolean success = UAnnotationHelper.addIRD(iba);
//                            if (false == success) {
//                                UAnnotationHelper.deleteAnnotationAndRepropagate(uic);
//                                errorMsgList.add(MSG_ERROR_UNABLE_TO_CREATE_IRD_ANNOTATION_WITH_PART_1 + lv.getLabel() +
//                                MSG_ERROR_UNABLE_TO_CREATE_IRD_ANNOTATION_WITH_PART_2 + lv.getValue());
//                                continue;
//                            }
//                        }
//                    }
//                    
//                }
//            }
//        }
//        return new StringBuffer(String.join(Constant.STR_NEWLINE, errorMsgList));
//    }
}
            
//            ArrayList<String> posList = group.getPositiveCondNodeList();
//            ArrayList<String> negList = group.getNegativeCondNodeList();
//            ArrayList<String> ancestorList = getAncestorList(posList, negList);
//            if (null == ancestorList) {
//                errorMsg.append(MSG_ERROR_INVALID_ANCESTOR_DESCENDANT_INFORMATION);
//                continue;
//            }
//            ArrayList<String> descendantList = null;
//            boolean ancestorNodeNeg = false;
//            if (posList == ancestorList) {
//                descendantList = negList;
//            }
//            else {
//                descendantList = posList;
//                ancestorNodeNeg = true;
//            }
//            GeneNode ancestorGn = pm.getGeneByPTNId(ancestorList.get(0));
//            Node ancestor = ancestorGn.getNode();
//            ArrayList<Node> descendants = new ArrayList<Node>();
//            boolean error = false;
//            if (null != descendantList) {
//                for (String desc: descendantList) {
//                    GeneNode descGn = pm.getGeneByPTNId(desc);
//                    if (null == descGn) {
//                        errorMsg.append(MSG_ERROR_UNABLE_TO_FIND_UNIRULE_NODE + desc + Constant.STR_NEWLINE);
//                        error = true;
//                        break;
//                    }
//                    descendants.add(descGn.getNode());
//                }
//            }
//            if (true == error) {
//                continue;
//            }
            
//            String negNode = group.getNegConditionNode();
//            Node ancestor = null;
//            Node descendant = null;
//            Node notNode = null;
//            GeneNode gn1 = pm.getGeneByPTNId(node1);
//            if (null == gn1) {
//                errorMsg.append(MSG_ERROR_UNABLE_TO_FIND_UNIRULE_NODE + node1 + Constant.STR_NEWLINE);
//                continue;
//            }
//            Node n1 = gn1.getNode();
//
//            // If there are 2 nodes, one of them  has to be negative
//            // Need to determine ancestor node that got annotated and descendant node that is 'NOTTING' the annotation
//            if (node2 != null) {
//                GeneNode gn2 = pm.getGeneByPTNId(node2);
//                if (null == gn2) {
//                    errorMsg.append(MSG_ERROR_UNABLE_TO_FIND_UNIRULE_NODE + node2 + Constant.STR_NEWLINE);
//                    continue;
//                }
//                Node n2 = gn2.getNode();
//                if (null == negNode
//                        || (false == node1.equals(negNode) && false == node2.equals(negNode))) {
//                    errorMsg.append(MSG_ERROR_INVALID_UNIRULE_NEG_CONDITION_NODE_PART_1 + node1 + MSG_ERROR_INVALID_UNIRULE_NEG_CONDITION_NODE_PART_2 + node2 + MSG_ERROR_INVALID_UNIRULE_NEG_CONDITION_NODE_PART_3 + group.getNegConditionNode() + Constant.STR_NEWLINE);
//                    continue;
//                }
//                ArrayList<Node> descendants = new ArrayList<Node>();
//                Node.getDescendants(n1, descendants);
//                if (descendants.contains(n2)) {
//                    ancestor = n1;
//                    descendant = n2;
//                } else {
//                    descendants.clear();
//                    Node.getDescendants(n2, descendants);
//                    if (descendants.contains(n1)) {
//                        ancestor = n2;
//                        descendant = n1;
//                    } else {
//                        errorMsg.append(MSG_ERROR_INVALID_UNIRULE_PRIMARY_DESC_INFO_PART_1 + node1 + MSG_ERROR_INVALID_UNIRULE_PRIMARY_DESC_INFO_PART_1 + node2 + Constant.STR_NEWLINE);
//                        continue;
//                    }
//                }
//                if (negNode.equals(ancestor.getStaticInfo().getPublicId())) {
//                    notNode = ancestor;
//                } else {
//                    notNode = descendant;
//                }
//            } else {
//                ancestor = n1;
//                if (ancestor.getStaticInfo().getPublicId().equals(negNode)) {
//                    notNode = ancestor;
//                }
//            }


//        public static boolean isTermValidForNode(GeneNode gn, String term) {
//            if (null == gn || null == term) {
//                return false;
//            }
//            PaintManager pm = PaintManager.inst();
//            TaxonomyHelper th = pm.getTaxonHelper();
//            if (null == th) {
//                return true;
//            }
//            return isTermValidForNode(gn, term, th);
//        }
//        
//        private static boolean isTermValidForNode(GeneNode gn, String term, TaxonomyHelper th) {
//            String species = gn.getCalculatedSpecies();
//            if (null == species) {
//                return false;
//            }           
//            boolean rtn = th.isTermValidForSpecies(term, species);
////            if (false == rtn) {
////                System.out.println("Taxonomy violation tern " + term + " not valid for species " + species + " for node " + gn.getNode().getStaticInfo().getPublicId());
////            }
//            return rtn;
//        }


    
//    public ArrayList<String> getAncestorList(ArrayList<String> positiveCondNodeList, ArrayList<String> negativeCondNodeList) {
//        // both lists have more than 1 entry
//        if (null != positiveCondNodeList && null != negativeCondNodeList && positiveCondNodeList.size() != 1 && negativeCondNodeList.size() != 1) {
//            return null;
//        }
//        // Positive list has 1 entry and negative list has zero or more entries
//        if (null !=  positiveCondNodeList && 1 == positiveCondNodeList.size() && (null == negativeCondNodeList || negativeCondNodeList.size() != 1)) {
//            return positiveCondNodeList;
//        }
//        // Negative list has 1 entry and positive list has zero or more entries
//        if (null !=  negativeCondNodeList && 1 == negativeCondNodeList.size() && (null == positiveCondNodeList || positiveCondNodeList.size() != 1)) {
//            return negativeCondNodeList;
//        }
//        PaintManager pm = PaintManager.inst();
//        GeneNode gn1 = pm.getGeneByPTNId(positiveCondNodeList.get(0));
//        if (null == gn1) {
//            return null;
//        }
//        GeneNode gn2 = pm.getGeneByPTNId(negativeCondNodeList.get(0));
//        if (null == gn2) {
//            return null;
//        }
//        Node n1 = gn1.getNode();
//        Node n2 = gn2.getNode();
//        ArrayList<Node> descendants = new ArrayList<Node>();
//        Node.getDescendants(n1, descendants);
//        if (descendants.contains(n2)) {
//            return positiveCondNodeList;
//        }
//        descendants.clear();
//        Node.getDescendants(n2, descendants);
//        if (descendants.contains(n1)) {
//            return negativeCondNodeList;
//        }
//        return null;
//    }
//    
//    public ArrayList<String> getDescendants(ArrayList<String> positiveCondNodeList, ArrayList<String> negativeCondNodeList) {
//        // both lists have more than 1 entry
//        if (null != positiveCondNodeList && null != negativeCondNodeList && positiveCondNodeList.size() != 1 && negativeCondNodeList.size() != 1) {
//            return null;
//        }
//        // Positive list has 1 entry and negative list has zero entries
//        if (null !=  positiveCondNodeList && 1 == positiveCondNodeList.size() && (null == negativeCondNodeList)) {
//            return null;
//        }
//        // Negative list has 1 entry and positive list has zero entries
//        if (null !=  negativeCondNodeList && 1 == negativeCondNodeList.size() && (null == positiveCondNodeList)) {
//            return null;
//        }
//        // Both lists have 1 entry - Determine ancestor and return descendant
//        if (null != positiveCondNodeList && null != negativeCondNodeList && positiveCondNodeList.size() == 1 && negativeCondNodeList.size() == 1) {
//            PaintManager pm = PaintManager.inst();
//            GeneNode gn1 = pm.getGeneByPTNId(positiveCondNodeList.get(0));
//            if (null == gn1) {
//                return null;
//            }
//            GeneNode gn2 = pm.getGeneByPTNId(negativeCondNodeList.get(0));
//            if (null == gn2) {
//                return null;
//            }
//            Node n1 = gn1.getNode();
//            Node n2 = gn2.getNode();
//            ArrayList<Node> descendants = new ArrayList<Node>();
//            Node.getDescendants(n1, descendants);
//            if (descendants.contains(n2)) {
//                return negativeCondNodeList;
//            }
//            descendants.clear();
//            Node.getDescendants(n2, descendants);
//            if (descendants.contains(n1)) {
//                return positiveCondNodeList;
//            }
//        }
//        return null;
//    }