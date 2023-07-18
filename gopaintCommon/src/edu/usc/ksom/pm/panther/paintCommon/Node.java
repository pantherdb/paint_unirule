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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class Node implements Serializable, IWith {
    private NodeStaticInfo staticInfo;
    private NodeVariableInfo variableInfo;
    
    public static final int NUM_PARTS_LONG_GENE_NAME = 3;
    public static final int INDEX_SPECIES_LONG_GENE_NAME = 0;
    public static final int INDEX_GENE_LONG_GENE_NAME = 1;
    public static final int INDEX_PROTEIN_LONG_GENE_NAME = 2;    
    public static final int NUM_PARTS_LONG_GENE_NAME_GENE_INFO = 2;    
    public static final int NUM_PARTS_LONG_GENE_NAME_PROTEIN_INFO = 2;
    public static final String DELIM_LONG_GENE_NAME = Pattern.quote("|");
    public static final String DELIM_LONG_GENE_NAME_PROTEIN_PART = Pattern.quote("=");
    public static final int INDEX_LONG_GENE_NAME_PROTEIN_SOURCE = 0;    
    public static final int INDEX_LONG_GENE_NAME_PROTEIN_NAME = 1;    

    public NodeStaticInfo getStaticInfo() {
        return staticInfo;
    }

    public void setStaticInfo(NodeStaticInfo staticInfo) {
        this.staticInfo = staticInfo;
    }

    public NodeVariableInfo getVariableInfo() {
        return variableInfo;
    }

    public void setVariableInfo(NodeVariableInfo variableInfo) {
        this.variableInfo = variableInfo;
    }
    
    // List of ancestors ordered such that furtherest ancestor is last
    public static List<Node> getAncestors(Node n) {
        Node copy = n;
        ArrayList<Node> ancestors = new ArrayList<Node>();
        getAncestor(copy, ancestors);
        return ancestors;
    }
    
    // Add furtherest ancestor last
    private static void getAncestor(Node n, List<Node> ancestors) {
        NodeStaticInfo nsi = n.getStaticInfo();
        if (null == nsi) {
            return;
        }
        Node parent = nsi.getParent();
        if (null != parent) {
            ancestors.add(parent);
        }
        else {
            return;
        }
        getAncestor(parent, ancestors);
    }
     
    
    public static void getNonPrunedDescendants(Node n, ArrayList<Node> nodeList) {
        if (null == n || null == nodeList) {
            return;
        }
        NodeStaticInfo nsi = n.getStaticInfo();
        ArrayList<Node> children = nsi.getChildren();
        if (null != children) {
            for (Node child : children) {
                NodeVariableInfo nvi = child.getVariableInfo();
                if (null != nvi && nvi.isPruned()) {
                    return;
                }
                nodeList.add(child);
                getNonPrunedDescendants(child, nodeList);
            }
        }

    }
    
    public static ArrayList<Node> getAllNonPrunedLeaves(Node n) {
        ArrayList<Node> descendants = new ArrayList<Node>();
        Node copy = n;
        getNonPrunedDescendants(copy, descendants);
        ArrayList<Node> leaves = new ArrayList<Node>();
        for (Node desc: descendants) {
            NodeStaticInfo nsi = desc.getStaticInfo();
            ArrayList<Node> children = nsi.getChildren();
            if (null == children || children.isEmpty()) {
                leaves.add(desc);
            }
        }
        return leaves;
    }

    public static Node getRoot(Node n) {
        // Do not modify parameter that is passed in.
        Node copy = n;
        return getTreeRoot(copy);
    }
    
    private static Node getTreeRoot(Node n) {
        Node parent = n.getStaticInfo().getParent();
        if (null == parent) {
            return n;
        }
        return getTreeRoot(parent);        
    }
    
    

    
    public static String getProteinIdFromLongGeneName(Node n) {
        if (null == n) {
            return null;
        }
        
        NodeStaticInfo nsi = n.getStaticInfo();
        if (null == nsi) {
            return null;
        }
        String longGeneName = nsi.getLongGeneName();
        if (null == longGeneName) {
            return null;
        }
        
        String parts[] = longGeneName.split(DELIM_LONG_GENE_NAME);
        if (NUM_PARTS_LONG_GENE_NAME > parts.length) {
            return null;
        }
        
        String proteinPart = parts[INDEX_PROTEIN_LONG_GENE_NAME];
        String proteinParts[] = proteinPart.split(DELIM_LONG_GENE_NAME_PROTEIN_PART);
        if (NUM_PARTS_LONG_GENE_NAME_PROTEIN_INFO > proteinParts.length) {
            return null;
        }
        return proteinParts[INDEX_LONG_GENE_NAME_PROTEIN_NAME];
    }
    // Orders according to tree topology
    public static void getDescendants(Node n, List<Node> nodeList) {
        if (null == n || null == nodeList) {
            return;
        }

        NodeStaticInfo nsi = n.getStaticInfo();
        ArrayList<Node> children = nsi.getChildren();
        if (null != children) {
            for (Node child : children) {
                nodeList.add(child);
                getDescendants(child, nodeList);
            }
        }
    }

    // Has not been tested
    public boolean isSfChangeEvent() {
        Node parent = this.getStaticInfo().getParent();
        if (null != parent) {
            NodeVariableInfo nvip = parent.getVariableInfo();
            NodeVariableInfo nvi = this.getVariableInfo();
            if (nvip != null && nvi != null) {
                SFAnnotation annotP = nvip.getSfAnnotation();
                SFAnnotation annot = nvi.getSfAnnotation();
                if (null != annotP && null != annot) {
                    return !(annotP.getSfAcc().equals(annot.getSfAcc()));
                }
            }
            else if (nvip == null && nvi != null && nvi.getSfAnnotation() != null) {
                return true;
            }
            else if (nvip != null && null == nvip.getSfAnnotation() && nvi != null && nvi.getSfAnnotation() != null) {
                return true;
            }
        }
        else {
            NodeVariableInfo nvi = this.getVariableInfo();
            if (null != nvi && null != nvi.getSfAnnotation()) {
                return true;
            }
        }
        return false;
    }

}
