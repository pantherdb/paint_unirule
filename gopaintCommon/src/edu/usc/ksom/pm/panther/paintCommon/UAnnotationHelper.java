/**
 *  Copyright 2023 University Of Southern California
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
package edu.usc.ksom.pm.panther.paintCommon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class UAnnotationHelper {
    
    public static boolean possibleToAnnotateWithUICRule(Rule rule, Node node, StringBuffer errorBuffer) {
        if (null == node || null == rule) {
            errorBuffer.append(" Node or rule is null\n.");
            return false;
        }
        
//      // Check if node has already been annotated to Rule
        String id = rule.getId();
        NodeVariableInfo nvi = node.getVariableInfo();
        if (null == nvi) {
            return true;
        }
        ArrayList<UAnnotation> annotList = nvi.getuAnnotationList();
        if (null == annotList) {
            errorBuffer.append(" Node or rule is null\n.");
            return true;
        }
        for (UAnnotation a : annotList) {
            Rule r = a.getRule();
            if (id.equals(r.getId())) {
                errorBuffer.append("Info - Skipping node " + node.getStaticInfo().getPublicId() + " - has already been annotated with id " + r.getId() + " and label " + r.getLabel() + " and value " + r.getValue());
                System.out.println("Info - Skipping node " + node.getStaticInfo().getPublicId() + " - has already been annotated with id " + r.getId() + " and label " + r.getLabel() + " and value " + r.getValue());
                return false;
            }
        }
        return true;
    }
    
    // Check if leaves have been annotated with rule and also ensure node has not already been annotated with rule    
    public static boolean possibleToAnnotateWithIBDRule(Rule rule, Node node, StringBuffer errorBuf) {
        if (null == node || null == rule) {
            errorBuf.append("Node or rule does not exist\n");
            return false;
        }

        
        // Cannot annotate leaf
        ArrayList<Node> children = node.getStaticInfo().getChildren();
        if (null == children || 0 == children.size()) {
            errorBuf.append("No descendant nodes\n");
            return false;
        }
        
        // Check if any leaf descendant has been annotated with rule
        boolean leafAnnot = false;
        ArrayList<Node> leaves = Node.getAllNonPrunedLeaves(node);
        if (null == leaves || 0 == leaves.size()) {
            errorBuf.append("No non-pruned leaf nodes\n");            
            return false;
        }
        String id = rule.getId();
        for (Node leaf : leaves) {
            NodeVariableInfo nvi = leaf.getVariableInfo();
            if (null == nvi) {
                continue;
            }
            ArrayList<UAnnotation> annotList = nvi.getuAnnotationList();
            if (null == annotList) {
                continue;
            }
            for (UAnnotation a : annotList) {
                if (false == Evidence.isExperimental(a.getEvCode())) {
                    continue;
                }
                Rule r = a.getRule();
                if (false == r.isCuratable()) {
                    continue;
                }
                if (id.equals(r.getId())) {
                    leafAnnot = true;
                    break;
                }
            }
            if (true == leafAnnot) {
                break;
            }
        }
        if (false == leafAnnot) {
            errorBuf.append("No non-pruned leaf nodes with experimental annotation\n");             
            return false;
        }
        
        // Check if node or any of its non-leaf descendant has already been annotated to Rule
        // Leaf descendants have already been checked.  Remove these
        ArrayList<Node> descendants = new ArrayList<Node>();
        Node.getNonPrunedDescendants(node, descendants);
        descendants.add(node);      // Check node as well
        descendants.removeAll(leaves);
        for (Node descendant: descendants) {
            NodeVariableInfo nvi = descendant.getVariableInfo();
            if (null == nvi) {
                continue;
            }
            ArrayList<UAnnotation> annotList = nvi.getuAnnotationList();
            if (null == annotList) {
                continue;
            }
            for (UAnnotation a : annotList) {
                Rule r = a.getRule();
                if (id.equals(r.getId())) {
                    errorBuf.append("Node " + descendant.getStaticInfo().getPublicId() + " has already been annotated with id " + r.getId() + " and label " + r.getLabel() + " and value " + r.getValue());
                    return false;
                }
            }
        }
        
        ArrayList<UAnnotation> evidenceList = getEvidenceForIBDRule(rule, node);
        if (null == evidenceList || 0 == evidenceList.size()) {
            errorBuf.append("Unable to get experimental evidence for rule\n");            
            return false;
        }
        return true;
    }

    public static ArrayList<UAnnotation> getEvidenceForIBDRule(Rule rule, Node n) {
        ArrayList<Node> leaves = Node.getAllNonPrunedLeaves(n);
        if (null == leaves) {
            return null;
        }
        String id = rule.getId();
        if (null == id) {
            return null;
        }
        ArrayList<UAnnotation> evidenceList = new ArrayList<UAnnotation>();
        for (Node leaf: leaves) {
            NodeVariableInfo nvi = leaf.getVariableInfo();
            if (null == nvi) {
                continue;
            }
            ArrayList<UAnnotation> annotList = nvi.getuAnnotationList();
            if (null == annotList) {
                continue;
            }
            for (UAnnotation annot : annotList) {
                Rule r = annot.getRule();
                if (false == r.isCuratable()) {
                    continue;
                }
                if (id.equals(r.getId())) {
                    if (false == evidenceList.contains(annot)) {
                        if (true == Evidence.isExperimental(annot.getEvCode()) && false == Evidence.isPaint(annot.getEvCode())) {
                            evidenceList.add(annot);
                            break;
                        }
                    }
                }
            }
        }
        return evidenceList;        
    }
    
    public static UAnnotation getImmediatePropagator(UAnnotation iba) {
        if (null ==  iba) {
            return null;
        }
        ArrayList<UAnnotation> withList = iba.getWithAnnotList();
        if (null == withList || 0 == withList.size()) {
            return null;
        }
        return withList.get(0);
    }
    
    public static UAnnotation findIBAAnnot(Rule r, Node n) {
        if (null == r || null == n) {
            return null;
        }

        NodeVariableInfo nvi = n.getVariableInfo();
        if (null == nvi) {
            return null;
        }
        ArrayList<UAnnotation> annotList = nvi.getuAnnotationList();
        if (null == annotList) {
            return null;
        }
        LabelValue lv = r.getLabelValue();
        for (UAnnotation annot: annotList) {
            if (false == Evidence.CODE_IBA.equals(annot.getEvCode())) {
                continue;
            }
            Rule curRule = annot.getRule();
            LabelValue curLabelValue = curRule.getLabelValue();
            if (true == lv.sameAs(curLabelValue)) {
                return annot;
            }
        }
        return null;
    }

    public static UAnnotation addUICAnnotRuleAndPropagateIBA(Rule rule, Node n, HashSet<Qualifier> qualifierSet) {  
        if (null == n || null == rule) {
            return null;
        }

        UAnnotation uic = new UAnnotation();
        uic.setAnnotatedNode(n);
        uic.setEvCode(Evidence.CODE_UIC);
//        Rule r = new Rule();
//        r.setId(rule.getId());
        uic.setRule(rule);
        uic.setQualifierSet(qualifierSet);

        NodeVariableInfo nvi = n.getVariableInfo();
        if (null == nvi) {
            nvi = new NodeVariableInfo();
            n.setVariableInfo(nvi);
        }
        nvi.addUAnnotation(uic);

        // Determine list of nodes that are going to get IBA's
        ArrayList<Node> ibaList = new ArrayList<Node>();
        Node.getNonPrunedDescendants(n, ibaList);

        
        for (Node ibaNode: ibaList) {
            UAnnotation iba = new UAnnotation();
            iba.setRule(rule);
            iba.setAnnotatedNode(ibaNode);
            iba.setEvCode(Evidence.CODE_IBA);
            iba.addWithAnnotation(uic);
            nvi = ibaNode.getVariableInfo();
            if (null == nvi) {
                nvi = new NodeVariableInfo();
                ibaNode.setVariableInfo(nvi);
            }           
            // Handle qualifiers
            if (null != qualifierSet) {
                HashSet<Qualifier> copySet = new HashSet<Qualifier>();
                for (Qualifier q: qualifierSet) {
                    Qualifier newQ = new Qualifier();
                    newQ.setText(new String(q.getText()));
                    copySet.add(newQ);
                }
                iba.setQualifierSet(copySet);
            }
            nvi.addUAnnotation(iba);          
        }
        return uic;        
    }    
    
    public static UAnnotation addIBDAnnotRuleAndPropagateIBA(Rule rule, Node n, HashSet<Qualifier> qualifierSet) {    

        if (null == n || null == rule) {
            return null;
        }
        ArrayList<UAnnotation> evList = getEvidenceForIBDRule(rule, n);
        if (null == evList || 0 == evList.size()) {
            return null;
        }
        
        UAnnotation ibd = new UAnnotation();
        ibd.setAnnotatedNode(n);
        ibd.setEvCode(Evidence.CODE_IBD);
//        Rule r = new Rule();
//        r.setId(rule.getId());
        ibd.setRule(rule);
        ibd.setWithAnnotList(evList);
        ibd.setQualifierSet(qualifierSet);

        NodeVariableInfo nvi = n.getVariableInfo();
        if (null == nvi) {
            nvi = new NodeVariableInfo();
            n.setVariableInfo(nvi);
        }
        nvi.addUAnnotation(ibd);
        
        
        // Determine list of nodes that are going to get IBA's
        ArrayList<Node> ibaList = new ArrayList<Node>();
        Node.getNonPrunedDescendants(n, ibaList);
        HashSet<Node> nodesProvidingEv  = new HashSet<Node>();
        for (UAnnotation annot: evList) {
            nodesProvidingEv.add(annot.getAnnotatedNode());
        }
        ibaList.removeAll(nodesProvidingEv);
        
        for (Node ibaNode: ibaList) {
            UAnnotation iba = new UAnnotation();
            iba.setRule(rule);
            iba.setAnnotatedNode(ibaNode);
            iba.setEvCode(Evidence.CODE_IBA);
            iba.addWithAnnotation(ibd);
            nvi = ibaNode.getVariableInfo();
            if (null == nvi) {
                nvi = new NodeVariableInfo();
                ibaNode.setVariableInfo(nvi);
            }           
            // Handle qualifiers
            if (null != qualifierSet) {
                HashSet<Qualifier> copySet = new HashSet<Qualifier>();
                for (Qualifier q: qualifierSet) {
                    Qualifier newQ = new Qualifier();
                    newQ.setText(new String(q.getText()));
                    copySet.add(newQ);
                }
                iba.setQualifierSet(copySet);
            }
            nvi.addUAnnotation(iba);          
        }
        return ibd;
    }
    
    public static boolean isDirectAnnotation(UAnnotation a) {
        String code = a.getEvCode();
        if (Evidence.CODE_IBD.equals(code) || Evidence.CODE_IRD.equals(code)) {
            ArrayList<UAnnotation> withAnnot = a.getWithAnnotList();
            if (null != withAnnot && 0 != withAnnot.size()) {
                return true;
            } 
        }
        else if (Evidence.CODE_UIC.equals(code)) {
            return true;
        }
        return false;
    }
    
    public static boolean isPAINTAnnotation(UAnnotation a) {
        if (true == isDirectAnnotation(a) && true == Evidence.CODE_IBA.equals(a.getEvCode())) {
            return true;
        }
        return false;
    }    
    
    public static void deleteAnnotationAndRepropagate(UAnnotation a) {
        if (null == a) {
            return;
        }
        
        String code = a.getEvCode();
        System.out.println("Deleting " + code + " for " + a.getAnnotatedNode().getStaticInfo().getPublicId());
        Node annotNode = a.getAnnotatedNode();
        if (Evidence.CODE_IBD.equals(code) || Evidence.CODE_UIC.equals(code)) {
            // Delete and repropagate dependant annotations
            ArrayList<UAnnotation> depAnnots = getDependantAnnots(a);
            if (null != depAnnots) {
                for (UAnnotation depAnnot: depAnnots) {
                    deleteAnnotationAndRepropagate(depAnnot);
                }
            }
            // Remove dependant IBA's
            ArrayList<Node> descendants = new ArrayList<Node>();
            Node.getNonPrunedDescendants(annotNode, descendants);
            for (Node descendant: descendants) {
                NodeVariableInfo nvi = descendant.getVariableInfo();
                if (null == nvi) {
                    continue;
                }
                ArrayList<UAnnotation> annotList = nvi.getuAnnotationList();
                if (null == annotList) {
                    continue;
                }
                ArrayList<UAnnotation> deleteList = new ArrayList<UAnnotation>();
                for (UAnnotation depAnnot: annotList) {
                    if (Evidence.CODE_IBA.equals(depAnnot.getEvCode()) && depAnnot.getWithAnnotList().contains(a)) {
                       deleteList.add(depAnnot);
                    }
                }
                annotList.removeAll(deleteList);
                if (annotList.isEmpty()) {
                    nvi.setuAnnotationList(null);
                }
            }
            // Delete the IBD/UIC annotation
            NodeVariableInfo nvi = annotNode.getVariableInfo();
            ArrayList<UAnnotation> annotList = nvi.getuAnnotationList();
            if (annotList != null) {
                annotList.remove(a);
                if (annotList.isEmpty()) {
                    nvi.setuAnnotationList(null);
                }
            }
            return;
        }
        
        // Deal with IRD
        if (Evidence.CODE_IRD.equals(code)) {
//            UAnnotation ibd = getIBDPropagator(a);
//            if (null != ibd) {
//
//                // Get list of nodes for adding IBA
//                ArrayList<Node> ibaNodeList = new ArrayList<Node>();
//                Node.getNonPrunedDescendants(a.getAnnotatedNode(), ibaNodeList);
//                ibaNodeList.add(a.getAnnotatedNode());      // Add node with IRD as well
//
//                // Remove nodes providing evidence
//                ArrayList<UAnnotation> withAnnots = ibd.getWithAnnotList();
//                for (UAnnotation with : withAnnots) {
//                    ibaNodeList.remove(with.getAnnotatedNode());
//                }
//
//                // Add IBA annotations to the nodes
//                for (Node ibaNode : ibaNodeList) {
//                    NodeVariableInfo nvi = ibaNode.getVariableInfo();
//                    if (null == nvi) {
//                        nvi = new NodeVariableInfo();
//                        ibaNode.setVariableInfo(nvi);
//                    }
//
//                    ArrayList<UAnnotation> curAnnotList = nvi.getuAnnotationList();
//                    boolean found = false;
//                    if (null != curAnnotList) {
//                        for (UAnnotation curAnnot : curAnnotList) {
//                            if (Evidence.CODE_IBA.equals(curAnnot.getEvCode())) {
//                                ArrayList<UAnnotation> withList = curAnnot.getWithAnnotList();
//                                if (null != withList && withList.contains(ibd)) {
//                                    found = true;
//                                }
//                            }
//                        }
//                    }
//                    if (found == false) {
//                        UAnnotation iba = new UAnnotation();
//                        iba.setRule(ibd.getRule());
//                        iba.setAnnotatedNode(ibaNode);
//                        iba.setEvCode(Evidence.CODE_IBA);
//                        iba.addWithAnnotation(ibd);
//                        nvi.addUAnnotation(iba);
//                    }
//                }
            // Remove the IRD and re-propagate as necessary
            UAnnotation ibd = getIBDPropagator(a);
            if (null != ibd) {
                NodeVariableInfo nvi = annotNode.getVariableInfo();
                nvi.getuAnnotationList().remove(a);
                fixAnnotationsForGraftPruneOperation(ibd.getAnnotatedNode(), a.getAnnotatedNode());
            }
            else {
                UAnnotation uic = getUICPropagator(a);
                NodeVariableInfo nvi = annotNode.getVariableInfo();
                nvi.getuAnnotationList().remove(a);
                fixAnnotationsForGraftPruneOperation(uic.getAnnotatedNode(), a.getAnnotatedNode());                
            }
            return;

        }
        if (Evidence.CODE_IBA.equals(code)) {
            Node n = a.getAnnotatedNode();
            // Remove any annotation that is dependant on the propagator (i.e. IBA, IRD from same propagator)
            UAnnotation propagator = getIBDPropagator(a);
            if (null == propagator) {
                propagator = getUICPropagator(a);
            }
            if (null != propagator) {

                ArrayList<Node> removeAnnotNodeList = new ArrayList<Node>();
                Node.getNonPrunedDescendants(n, removeAnnotNodeList);
                removeAnnotNodeList.add(n);     // Add self since IBA has to be removed from self as well
                for (Node remove : removeAnnotNodeList) {
        
                    UAnnotation removeAnnot = null;
                    NodeVariableInfo removeNvi = remove.getVariableInfo();
                    if (null != removeNvi) {
                        ArrayList<UAnnotation> curAnnotList = removeNvi.getuAnnotationList();
                        if (null != curAnnotList) {
                            for (UAnnotation curAnnot : curAnnotList) {
                                ArrayList<UAnnotation> curWithList = curAnnot.getWithAnnotList();
                                if (null == curWithList) {
                                    continue;
                                }
                                if (curWithList.contains(propagator)) {
                                    removeAnnot = curAnnot;
                                    break;
                                }
                            }
                            if (null != removeAnnot) {
                                curAnnotList.remove(removeAnnot);
                            }
                            if (curAnnotList.isEmpty()) {
                                removeNvi.setuAnnotationList(null);
                            }
                        }
                    }
                }
                fixAnnotationsForGraftPruneOperation(propagator.getAnnotatedNode(), a.getAnnotatedNode());
            }
        }
    }

    public static ArrayList<UAnnotation> getDependantAnnots(UAnnotation a) {
        Node n = a.getAnnotatedNode();
        ArrayList<Node> descendants = new ArrayList<Node>();
        Node.getNonPrunedDescendants(n, descendants);
        ArrayList<UAnnotation> depAnnots = new ArrayList<UAnnotation>();
        for (Node descendant: descendants) {
            NodeVariableInfo nvi = descendant.getVariableInfo();
            if (null ==  nvi) {
                continue;
            }
            ArrayList<UAnnotation> annots = nvi.getuAnnotationList();
            if (null == annots) {
                continue;
            }
            for (UAnnotation annot: annots) {
                ArrayList<UAnnotation> withList = annot.getWithAnnotList();
                if (null != withList && withList.contains(a)) {
                    if (false == depAnnots.contains(annot)) {
                        depAnnots.add(annot);
                    }
                }
            }
        }
        return depAnnots;
    }
    
    public static UAnnotation getUICPropagator(UAnnotation a) {
        if (null == a) {
            return null;
        }
        ArrayList<UAnnotation> withAnnotList = a.getWithAnnotList();
        if (null != withAnnotList) {
            for (UAnnotation with: withAnnotList) {
                if (Evidence.CODE_UIC.equals(with.getEvCode())) {
                    return with;
                }
            }
        }
        return null;
    }
    
    public static UAnnotation getIBDPropagator(UAnnotation a) {
        if (null == a) {
            return null;
        }
        ArrayList<UAnnotation> withAnnotList = a.getWithAnnotList();
        if (null != withAnnotList) {
            for (UAnnotation with: withAnnotList) {
                if (Evidence.CODE_IBD.equals(with.getEvCode())) {
                    return with;
                }
            }
        }
        return null;
    }
    
    public static boolean containsAnnot(Node n, UAnnotation annot) {
        if (null == n) {
            return false;
        }
        NodeVariableInfo nvi = n.getVariableInfo();
        if (null == nvi) {
            return false;
        }
        
        ArrayList<UAnnotation> annotList = nvi.getuAnnotationList();
        if (null != annotList && annotList.contains(annot)) {
            return true;
        }
        return false;
    }
    
    /**
     * Not only do annotations have to be restored after graft or prune operations, but also when annotations are deleted.  IBA's etc have to be propagated to
     * descendant nodes
     * @param n
     * @param graftPruneNode
     */
    public static void fixAnnotationsForGraftPruneOperation(Node n, Node graftPruneNode) {
        NodeVariableInfo nvi = n.getVariableInfo();
        System.out.println("Fix and repropagate for " + n.getStaticInfo().getPublicId());
        if (null != nvi) {
            ArrayList<UAnnotation> uAnnotList = nvi.getuAnnotationList();
            if (null != uAnnotList) {
                ArrayList<UAnnotation> uAnnotListCopy = (ArrayList<UAnnotation>)uAnnotList.clone();     // Use copy to avoid updating list while traversing
                ArrayList<UAnnotation> removeList = new ArrayList<UAnnotation>();
                for (UAnnotation annot: uAnnotListCopy) {
                    if (false == containsAnnot(n, annot)) {
                        continue;
                    }
                    if (Evidence.CODE_IBD.equals(annot.getEvCode()) || Evidence.CODE_UIC.equals(annot.getEvCode())) {
                        ArrayList<UAnnotation> evList = new ArrayList<UAnnotation>();
                        if (Evidence.CODE_IBD.equals(annot.getEvCode())) {
                            evList = UAnnotationHelper.getEvidenceForIBDRule(annot.getRule(), n);
                            if (null == evList || 0 == evList.size()) {
                                removeList.add(annot);
                                continue;
                            } else {
                                annot.setWithAnnotList(evList);
                            }
                        }
                        // Determine list of nodes that are going to get IBA's
                        ArrayList<Node> ibaList = new ArrayList<Node>();
                        Node.getNonPrunedDescendants(n, ibaList);
                        HashSet<Node> nodesProvidingEv  = new HashSet<Node>();
                        for (UAnnotation evAnnot: evList) {
                            nodesProvidingEv.add(evAnnot.getAnnotatedNode());
                        }
                        ibaList.removeAll(nodesProvidingEv);
                        for (Node ibaNode: ibaList) {
                            boolean containsAnnot = false;
                            NodeVariableInfo ibaNvi = ibaNode.getVariableInfo();
                            if (null != ibaNvi) {
                                ArrayList<UAnnotation> curAnnotList = ibaNvi.getuAnnotationList();
                                if (null != curAnnotList) {
                                    for (UAnnotation curAnnot : curAnnotList) {
                                        if (Evidence.CODE_IBA.equals(curAnnot.getEvCode())) {
                                            if (curAnnot.getWithAnnotList().contains(annot)) {
                                                containsAnnot = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                            // Add IBA annotation, if it does not already exist
                            if (false == containsAnnot) {
                                UAnnotation iba = new UAnnotation();
                                iba.setRule(annot.getRule());
                                iba.setAnnotatedNode(ibaNode);
                                iba.setEvCode(Evidence.CODE_IBA);
                                iba.addWithAnnotation(annot);
                                if (null == ibaNvi) {
                                    ibaNvi = new NodeVariableInfo();
                                    ibaNode.setVariableInfo(ibaNvi);
                                }
                                ibaNvi.addUAnnotation(iba);
                            }
                        }
                    }
                    if (Evidence.CODE_IRD.equals(annot.getEvCode())) {
                        // Remove IBA's from propagator if any
                        UAnnotation propagator = UAnnotationHelper.getIBDPropagator(annot);
                        if (null == propagator) {
                            propagator = UAnnotationHelper.getUICPropagator(annot);
                        }
                        ArrayList<Node> removeAnnotNodeList = new ArrayList<Node>();
                        Node.getNonPrunedDescendants(n, removeAnnotNodeList);
                        removeAnnotNodeList.add(n);
                        for (Node remove: removeAnnotNodeList) {
                            UAnnotation removeIBA = null;
                            NodeVariableInfo removeNvi = remove.getVariableInfo();
                            if (null != removeNvi) {
                                ArrayList<UAnnotation> curAnnotList = removeNvi.getuAnnotationList();
                                if (null != curAnnotList ) {
                                    for (UAnnotation curAnnot: curAnnotList) {
                                        if (Evidence.CODE_IBA.equals(curAnnot.getEvCode())) {
                                            if (curAnnot.getWithAnnotList().contains(propagator)) {
                                                removeIBA = curAnnot;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (null != removeIBA) {
                                    curAnnotList.remove(removeIBA);
                                }
                                if (curAnnotList.isEmpty()) {
                                    removeNvi.setuAnnotationList(null);
                                }
                            }
                        }
                    }
                }
                for (UAnnotation remove: removeList) {
                    deleteAnnotationAndRepropagate(remove);
                }
//                uAnnotList.removeAll(removeList);
//                if (uAnnotList.isEmpty()) {
//                    nvi.setuAnnotationList(null);
//                }
            }            
        }
        
        if (n == graftPruneNode) {
            return;
        }
        ArrayList<Node> children = n.getStaticInfo().getChildren();
        if (null != children) {
            for (Node child : children) {
                fixAnnotationsForGraftPruneOperation(child, graftPruneNode);
            }
        }
    }

    public static boolean addIRD(UAnnotation iba) {
        if (false == Evidence.CODE_IBA.equals(iba.getEvCode())) {
            return false;
        }
        Node n = iba.getAnnotatedNode();
        // Remove any annotation that is dependant on the propagator (i.e. IBA, IRD from same propagator)
        UAnnotation propagator = getIBDPropagator(iba);
        if (null == propagator) {
            propagator = getUICPropagator(iba);
        }
        if (null == propagator) {
            return false;
        }
        
        // Figure out the qualifiers
        HashSet<Qualifier> qualifierSet = null;
        if (QualifierDif.containsNegative(propagator.getQualifierSet())) {
            qualifierSet = new HashSet<Qualifier>();
            for (Qualifier q: propagator.getQualifierSet()) {
                if (q.isNot()) {
                    continue;
                }
                Qualifier newQ = new Qualifier();
                newQ.setText(new String(q.getText()));
                qualifierSet.add(newQ);
            }
            if (qualifierSet.isEmpty()) {
                qualifierSet = null;
            }
        }
        else {
            qualifierSet = new HashSet<Qualifier>();
            if (null != propagator.getQualifierSet()) {
                for (Qualifier q: propagator.getQualifierSet()) {
                    Qualifier newQ = new Qualifier();
                    newQ.setText(new String(q.getText()));
                    qualifierSet.add(newQ);
                }                
            }
            Qualifier newQ = new Qualifier();
            newQ.setText(Qualifier.QUALIFIER_NOT);
            qualifierSet.add(newQ);               
        }
        
        
        ArrayList<Node> removeAnnotNodeList = new ArrayList<Node>();
        Node.getNonPrunedDescendants(n, removeAnnotNodeList);
        removeAnnotNodeList.add(n);     // Add self since IBA has to be removed from self as well
        for (Node remove : removeAnnotNodeList) {
            UAnnotation removeAnnot = null;
            NodeVariableInfo removeNvi = remove.getVariableInfo();
            if (null != removeNvi) {
                ArrayList<UAnnotation> curAnnotList = removeNvi.getuAnnotationList();
                if (null != curAnnotList) {
                    for (UAnnotation curAnnot : curAnnotList) {
                        ArrayList<UAnnotation> curWithList = curAnnot.getWithAnnotList();
                        if (null == curWithList) {
                            continue;
                        }
                        if (curWithList.contains(propagator)) {
                            removeAnnot = curAnnot;
                            break;
                        }
                    }              
                    if (null != removeAnnot) {
                        curAnnotList.remove(removeAnnot);
                    }
                    if (curAnnotList.isEmpty()) {
                        removeNvi.setuAnnotationList(null);
                    }
                }
            }
        }
        
        // Add IRD
        UAnnotation ird = new UAnnotation();
        ird.setEvCode(Evidence.CODE_IRD);
        ird.setAnnotatedNode(n);
        ird.setRule(propagator.getRule());
        if (null != qualifierSet) {
            ird.setQualifierSet(qualifierSet);
        }
        ird.addWithAnnotation(propagator);
        ird.addWithAnnotation(ird);     // Add myself as a with annotation as well.  This is what gives the 'NOT'
        ird.setAnnotatedNode(n);
        NodeVariableInfo nvi = n.getVariableInfo();
        if (null == nvi) {
            nvi = new NodeVariableInfo();
            n.setVariableInfo(nvi);
        }
        nvi.addUAnnotation(ird);
        return true;
    }
}
