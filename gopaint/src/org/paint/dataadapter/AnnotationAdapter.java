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
package org.paint.dataadapter;

import com.sri.panther.paintCommon.Constant;
import edu.usc.ksom.pm.panther.paintCommon.Evidence;
import edu.usc.ksom.pm.panther.paintCommon.LabelValue;
import edu.usc.ksom.pm.panther.paintCommon.Node;
import edu.usc.ksom.pm.panther.paintCommon.NodeVariableInfo;
import edu.usc.ksom.pm.panther.paintCommon.Qualifier;
import edu.usc.ksom.pm.panther.paintCommon.QualifierDif;
import edu.usc.ksom.pm.panther.paintCommon.Rule;
import edu.usc.ksom.pm.panther.paintCommon.UAnnotation;
import edu.usc.ksom.pm.panther.paintCommon.UAnnotationHelper;
import edu.usc.ksom.pm.panther.paintCommon.UniRuleAnnotationGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.paint.datamodel.Family;
import org.paint.datamodel.GeneNode;
import org.paint.gui.familytree.TreePanel;
import org.paint.io.CaseTypeDetails;
import org.paint.main.PaintManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;


public class AnnotationAdapter {
    
    public static final String ELEMENT_BOOK = "book";
    public static final String ELEMENT_BOOK_ID = "book_id";
    public static final String ELEMENT_NODE_LIST = "node_list";
    public static final String ELEMENT_NODE = "node";
    public static final String ELEMENT_ANNOTATION_NODE_ID = "annotation_node_id";     
    public static final String ELEMENT_PERSISTENT_ID = "persistent_id";    
    public static final String ELEMENT_ANNOTATION_LIST = "annotation_list";
    public static final String ELEMENT_ANNOTATION = "annotation";
    public static final String ELEMENT_RULE = "rule";
    public static final String ELEMENT_RULE_PROPERTY_LIST = "rule_property_list";    
    public static final String ELEMENT_RULE_PROPERTY = "rule_property";
    public static final String ELEMENT_RULE_PROPERTY_LABEL = "rule_property_label";
    public static final String ELEMENT_RULE_PROPERTY_VALUE = "rule_property_value";    
    public static final String ELEMENT_TERM = "term";    
    public static final String ELEMENT_TERM_NAME = "term_name";
    public static final String ELEMENT_TERM_ASPECT = "term_aspect";    
    public static final String ELEMENT_QUALIFIER_LIST = "qualifier_list";
    public static final String ELEMENT_QUALIFIER = "qualifier";
    public static final String ELEMENT_EVIDENCE_LIST = "evidence_list";
    public static final String ELEMENT_EVIDENCE = "evidence";
    public static final String ELEMENT_EVIDENCE_TYPE = "evidence_type";    
    public static final String ELEMENT_EVIDENCE_VALUE = "evidence_value";
    public static final String ELEMENT_EVIDENCE_CODE = "evidence_code";
    
    
    public static  ArrayList<UniRuleAnnotationGroup> groupUAnnotations() {
        PaintManager pm = PaintManager.inst();
        TreePanel tp = pm.getTree();
        if (null == tp) {
            return null;
        }
        List<Node> allNodes = tp.getNonPrunedNodesInTopologicalOrder();
        if (null == allNodes || 0 == allNodes.size()) {
            return null;
        }
        // Create a lookup of nodes to annotations
        HashMap<Node, ArrayList<UAnnotation>> nodeAnnotLookup = new HashMap<Node, ArrayList<UAnnotation>>();
        HashSet<UAnnotation> annotationLookup = new HashSet<UAnnotation>();
        HashMap<UAnnotation, ArrayList<UAnnotation>> annotToDepAnnot = new HashMap<UAnnotation, ArrayList<UAnnotation>>();
        LinkedHashSet<UAnnotation> depAnnotSet = new LinkedHashSet<UAnnotation>();
        ArrayList<UAnnotation> ibdUicAnnotList = new ArrayList<UAnnotation>();
        
        for (Node n : allNodes) {
            if (null == n) {
                continue;
            }
            NodeVariableInfo nvi = n.getVariableInfo();
            if (null == nvi) {
                continue;
            }
            ArrayList<UAnnotation> annotList = nvi.getuAnnotationList();
            if (null == annotList || 0 == annotList.size()) {
                continue;
            }
            for (UAnnotation a: annotList) {
                String code = a.getEvCode();
                if (Evidence.CODE_IBD.equals(code) || Evidence.CODE_UIC.contains(code) || Evidence.CODE_IRD.equals(code)) {
                    ArrayList<UAnnotation> annots = nodeAnnotLookup.get(n);
                    if (null == annots) {
                        annots = new ArrayList<UAnnotation>();
                        nodeAnnotLookup.put(n, annots);
                    }
                    annots.add(a);
                    annotationLookup.add(a);
                    
                    // Save dependencies information
                    if (Evidence.CODE_IRD.equals(code)) {
                        UAnnotation propagator = UAnnotationHelper.getIBDPropagator(a);
                        if (null == propagator) {
                            propagator = UAnnotationHelper.getUICPropagator(a);
                        }
                        if (null != propagator) {
                            ArrayList<UAnnotation> depList = annotToDepAnnot.get(propagator);
                            if (null == depList) {
                                depList = new ArrayList<UAnnotation>();
                                annotToDepAnnot.put(propagator, depList);
                            }
                            depList.add(a);
                            depAnnotSet.add(a);
                        }
                    }
                    else {
                        ibdUicAnnotList.add(a);
                    }
                }
            }
        }
        
        // Go through list of IBD annotations.  Any other IBD's to same node without dependant IRD's can be grouped together.
        // For ones with IRD's, ensure IRD's occur at same nodes.
//        ArrayList<ArrayList<UAnnotation>> groups = new ArrayList<ArrayList<UAnnotation>>();
        ArrayList<UniRuleAnnotationGroup> uagList = new ArrayList<UniRuleAnnotationGroup>();
        
        for (int i = 0; i < ibdUicAnnotList.size(); i++) {
//            System.out.println("i = " + i);
            UAnnotation cur = ibdUicAnnotList.get(i);
            ArrayList<UAnnotation> curDepList = annotToDepAnnot.get(cur);
            ArrayList<UAnnotation> comps = new ArrayList<UAnnotation>();
            for (int j = 0; j < ibdUicAnnotList.size(); j++) {
                if (i == j) {
                    continue;
                }
                if (true == compareAnnots(cur, curDepList, ibdUicAnnotList.get(j), annotToDepAnnot.get(ibdUicAnnotList.get(j)))) {
                    comps.add(ibdUicAnnotList.get(j));
                }
            }
            ArrayList<UAnnotation> aGroup = new ArrayList<UAnnotation>();
            aGroup.add(cur);
            UniRuleAnnotationGroup uag = new UniRuleAnnotationGroup();
            uagList.add(uag);
            uag.setMainConditionNode(cur.getAnnotatedNode());
            uag.setMainCondNegative(QualifierDif.containsNegative(cur.getQualifierSet()));
            uag.addRule(cur.getRule());
            
            if (null != curDepList) {
                aGroup.addAll(annotToDepAnnot.get(cur));
                ArrayList<UAnnotation> depAnnots = annotToDepAnnot.get(cur);
                ArrayList<Node> depNodes = new ArrayList<Node>(depAnnots.size());
                for (UAnnotation depAnnot: depAnnots) {
                    depNodes.add(depAnnot.getAnnotatedNode());
                }
                uag.setDependantConditionNodes(depNodes);
            }
            for (int j = 0; j < comps.size(); j++) {
                UAnnotation comp = comps.get(j);
                uag.addRule(comp.getRule());
                aGroup.add(comp);
                if (null != curDepList) {
                    aGroup.addAll(annotToDepAnnot.get(comp));
                }
            }
            ibdUicAnnotList.remove(cur);
            ibdUicAnnotList.removeAll(comps);
            i =  i - 1;
//            System.out.println("After comparison i = " + i);
//            groups.add(aGroup);
        }
        //return groups;
        return uagList;        
    }
    
    private static boolean compareAnnots(UAnnotation annot1, ArrayList<UAnnotation> annotList1, UAnnotation annot2, ArrayList<UAnnotation> annotList2) {
        // Qualifiers have to agree
        if (false == QualifierDif.allQualifiersSame(annot1.getQualifierSet(), annot2.getQualifierSet())) {
            return false;
        }
        if (annot1.getAnnotatedNode() != annot2.getAnnotatedNode()) {
            return false;
        }

        // Annotated to the same node without any dependant annotation
        if (null == annotList1 && null == annotList2) {
            return true;
        }
        
        // One has dependant annotations whereas the other does not
        if ((null == annotList1 && null != annotList2) || (null != annotList1 && null == annotList2)) {
            return false;
        }
        
        // if size of dependant annotations does not match, return false
        if (annotList1.size() != annotList2.size()) {
            return false;
        }

        // Compare dependant annotations - They have to be annotated to the same node
        for (int i = 0; i < annotList1.size(); i++) {
            Node cur = annotList1.get(i).getAnnotatedNode();
            boolean found = false;
            for (int j = 0; j < annotList2.size(); j++) {
                if (cur == annotList2.get(j).getAnnotatedNode()) {
                    found = true;
                    break;
                }
            }
            if (false == found) {
                return false;
            }
        }        
        return true;
    }
    
    
    
    public static StringBuilder getUniRuleAnnotationsInXML() {
        PaintManager pm = PaintManager.inst();
        List<GeneNode> allNodes = pm.getTree().getAllNodes();
        if (null == allNodes || 0 == allNodes.size()) {
            return new StringBuilder();
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement(ELEMENT_BOOK);
            doc.appendChild(root);
            String id = pm.getFamily().getFamilyID();
            if (null != id) {
                Element idElem = doc.createElement(ELEMENT_BOOK_ID);
                root.appendChild(idElem);
                idElem.appendChild(doc.createTextNode(id));
            }
            Element nodeList = doc.createElement(ELEMENT_NODE_LIST);
            root.appendChild(nodeList);
            for (GeneNode gn: allNodes) {
                Node n = gn.getNode();
                if (null == n) {
                    continue;
                }
                NodeVariableInfo nvi = n.getVariableInfo();
                if (null == nvi) {
                    continue;
                }
                ArrayList<UAnnotation> annotList = nvi.getuAnnotationList();
                if (null == annotList || 0 == annotList.size()) {
                    continue;
                }
                Element node = doc.createElement(ELEMENT_NODE);
                nodeList.appendChild(node);
                Element annotId = doc.createElement(ELEMENT_ANNOTATION_NODE_ID);
                node.appendChild(annotId);
                annotId.appendChild(doc.createTextNode(n.getStaticInfo().getNodeAcc()));
                Element nodeId = doc.createElement(ELEMENT_PERSISTENT_ID);
                node.appendChild(nodeId);
                nodeId.appendChild(doc.createTextNode(n.getStaticInfo().getPublicId()));
                
                Element annotListElem = doc.createElement(ELEMENT_ANNOTATION_LIST);
                node.appendChild(annotListElem);
                for (UAnnotation a: annotList) {
                    Element annot = doc.createElement(ELEMENT_ANNOTATION);
                    annotListElem.appendChild(annot);
                    Rule r = a.getRule();
                    Element rule = doc.createElement(ELEMENT_RULE);
                    annot.appendChild(rule);
                    rule.appendChild(doc.createTextNode(r.getId()));
                    LabelValue lv = r.getLabelValue();
//                    HashMap<String, ArrayList<LabelValue>> rulePropertyList = r.getPropertyLookup();
                    if (null != lv) {
//                        Element rpl = doc.createElement(ELEMENT_RULE_PROPERTY_LIST);
//                        rule.appendChild(rpl);
//                        for (ArrayList<LabelValue> lvList : rulePropertyList.values()) {
//                            for (LabelValue lv : lvList) {
                        Element property = doc.createElement(ELEMENT_RULE_PROPERTY);
                        rule.appendChild(property);
                        Element label = doc.createElement(ELEMENT_RULE_PROPERTY_LABEL);
                        property.appendChild(label);
                        label.appendChild(doc.createTextNode(lv.getLabel()));
                        Element value = doc.createElement(ELEMENT_RULE_PROPERTY_VALUE);
                        property.appendChild(value);
                        value.appendChild(doc.createTextNode(lv.getValue()));
//                            }
//                        }
                    }
                    Element evCode = doc.createElement(ELEMENT_EVIDENCE_CODE);
                    annot.appendChild(evCode);
                    evCode.appendChild(doc.createTextNode(a.getEvCode()));
                    
                    HashSet<Qualifier> qSet = a.getQualifierSet();
                    if (null != qSet) {
                        Element qualifierList = doc.createElement(ELEMENT_QUALIFIER_LIST);
                        annot.appendChild(qualifierList);
                        for (Qualifier q: qSet) {
                            Element qualifier = doc.createElement(ELEMENT_QUALIFIER);
                            qualifierList.appendChild(qualifier);
                            qualifier.appendChild(doc.createTextNode(q.getText()));
                        }
                    }
                    // DO NOT OUTPUT WITHS FOR NOW
//                    ArrayList<DBReference> withRefList = a.getWithReferenceList();
//                    if (null != withRefList && 0 != withRefList.size()) {
//                        
//                    }
                }

            }
            // Output information
            DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
            LSSerializer lsSerializer = domImplementation.createLSSerializer();
            return new StringBuilder(lsSerializer.writeToString(doc)); 
            
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    
    public static Hashtable<String, Rule> getAllRules() {
        PaintManager pm = PaintManager.inst();
        List<GeneNode> allNodes = pm.getTree().getAllNodes();
        if (null == allNodes || 0 == allNodes.size()) {
            return null;
        }
        Hashtable<String, Rule> ruleLookup = new Hashtable<String, Rule>();
        for (GeneNode gn : allNodes) {
            Node n = gn.getNode();
            if (null == n) {
                continue;
            }
            NodeVariableInfo nvi = n.getVariableInfo();
            if (null == nvi) {
                continue;
            }
            ArrayList<UAnnotation> annotList = nvi.getuAnnotationList();
            if (null == annotList || 0 == annotList.size()) {
                continue;
            }
            for (UAnnotation a: annotList) {
                Rule r = a.getRule();
                LabelValue lv = r.getLabelValue();
                ruleLookup.put(lv.getLabel() + lv.getValue(), r);
            }
        }
        return ruleLookup;
    }

    public static Hashtable<String, Rule> getExistingCuratableRules() {
        PaintManager pm = PaintManager.inst();
        List<GeneNode> allNodes = pm.getTree().getAllNodes();
        if (null == allNodes || 0 == allNodes.size()) {
            return null;
        }
        Hashtable<String, Rule> ruleLookup = new Hashtable<String, Rule>();
        for (GeneNode gn : allNodes) {
            Node n = gn.getNode();
            if (null == n) {
                continue;
            }
            NodeVariableInfo nvi = n.getVariableInfo();
            if (null == nvi) {
                continue;
            }
            ArrayList<UAnnotation> annotList = nvi.getuAnnotationList();
            if (null == annotList || 0 == annotList.size()) {
                continue;
            }
            for (UAnnotation a: annotList) {
                Rule r = a.getRule();
                if (false == r.isCuratable()) {
                    continue;
                }
                LabelValue lv = r.getLabelValue();
                ruleLookup.put(lv.getLabel() + lv.getValue(), r);
            }
        }
        return ruleLookup;
    }
    
    public static StringBuffer getErrorCaseInfo() {
        StringBuffer errorBuf = null;
        PaintManager pm = PaintManager.inst();
        Family fam = pm.getFamily();
        if (null == fam) {
            return null;
        }
        ArrayList<CaseTypeDetails> errorCaseList = fam.getErrorPaintCases();
        if (null == errorCaseList || 0 == errorCaseList.size()) {
            return null;
        }
        errorBuf = new StringBuffer();
        for (CaseTypeDetails ctd : errorCaseList) {
            ArrayList<String> msgList = ctd.getMsgList();
            if (null == msgList) {
                errorBuf.append("Case:  " + ctd.getCaseNum());
                errorBuf.append(Constant.LINE_SEPARATOR_SYSTEM_PROPERY);
                errorBuf.append(Constant.LINE_SEPARATOR_SYSTEM_PROPERY);
                continue;
            }
            errorBuf.append("Case:  " + ctd.getCaseNum());
            errorBuf.append(Constant.LINE_SEPARATOR_SYSTEM_PROPERY);            
            errorBuf.append(String.join(Constant.LINE_SEPARATOR_SYSTEM_PROPERY, msgList));
            errorBuf.append(Constant.LINE_SEPARATOR_SYSTEM_PROPERY);
            errorBuf.append(Constant.LINE_SEPARATOR_SYSTEM_PROPERY);            
        }
        return errorBuf;
    }
}
