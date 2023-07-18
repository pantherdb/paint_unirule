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
package edu.usc.ksom.pm.panther.paintCommon;

import java.util.ArrayList;


public class UniRuleAnnotationGroup {
    private Node mainConditionNode;
    private Boolean mainCondNegative;
    private ArrayList<Node> dependantConditionNodes;
//    private boolean overallStatsExempted;
    private ArrayList<Rule> rules;

    public ArrayList<Rule> getRules() {
        return rules;
    }

    public void setRules(ArrayList<Rule> rules) {
        this.rules = rules;
    }
    
    public boolean addRule(Rule r) {
        if (null == r) {
            return false;
        }
        if (null == rules) {
            rules = new ArrayList<Rule>();
        }
        if (rules.contains(r)) {
            return false;
        }
        rules.add(r);
        return true;
    }


    public Node getMainConditionNode() {
        return mainConditionNode;
    }

    public void setMainConditionNode(Node mainConditionNode) {
        this.mainConditionNode = mainConditionNode;
    }

    public Boolean getMainCondNegative() {
        return mainCondNegative;
    }

    public void setMainCondNegative(Boolean mainCondNegative) {
        this.mainCondNegative = mainCondNegative;
    }

    public ArrayList<Node> getDependantConditionNodes() {
        return dependantConditionNodes;
    }

    public void setDependantConditionNodes(ArrayList<Node> dependantConditionNodes) {
        this.dependantConditionNodes = dependantConditionNodes;
    }

//    public boolean isOverallStatsExempted() {
//        return overallStatsExempted;
//    }
//
//    public void setOverallStatsExempted(boolean overallStatsExempted) {
//        this.overallStatsExempted = overallStatsExempted;
//    }
    
    public ArrayList<Node> getApplicableNodes() {
        ArrayList<Node> nodeList = new ArrayList<Node>();
        addApplicableNodes(mainConditionNode, nodeList);
        return nodeList;
    }
    
    private void addApplicableNodes(Node cur, ArrayList<Node> nodeList) {
        if (null == cur) {
            return;
        }
        if (true == nodeList.contains(cur)) {
            return;
        }
        if (dependantConditionNodes != null && dependantConditionNodes.contains(cur)) {
            return;
        }
        nodeList.add(cur);
        ArrayList<Node> children = cur.getStaticInfo().getChildren();
        if (null != children) {
            for (Node child: children) {
                addApplicableNodes(child, nodeList);
            }
        }
    }
        

    
//    private String node1;
//    private ArrayList<String> node2List;
//    private ArrayList<String> positiveCondNodeList;
//    private ArrayList<String> negativeCondNodeList;
//    private String negConditionNode;
//    private ArrayList<Rule> uniruleList;
    
//    // Other UniRule annotations that are not annoated in PAINT.  However, displayed in paint.   These have to be carried over when saving
//    private ArrayList<ConditionType> otherConditions;
//
//    public ArrayList<String> getPositiveCondNodeList() {
//        return positiveCondNodeList;
//    }
//
//    public void setPositiveCondNodeList(ArrayList<String> positiveCondNodeList) {
//        this.positiveCondNodeList = positiveCondNodeList;
//    }
//    
//    public boolean addPositiveCondNode(String posNode) {
//        if (null == posNode) {
//            return false;
//        }
//        if (null == positiveCondNodeList) {
//            positiveCondNodeList = new ArrayList<String>();
//        }
//        if (false == positiveCondNodeList.contains(posNode)) {
//            positiveCondNodeList.add(posNode);
//            return true;
//        }
//        return false;
//    }
//
//    public ArrayList<String> getNegativeCondNodeList() {
//        return negativeCondNodeList;
//    }
//
//    public void setNegativeCondNodeList(ArrayList<String> negativeCondNodeList) {
//        this.negativeCondNodeList = negativeCondNodeList;
//    }
//    
//    public boolean addNegativeCondNode(String negNode) {
//        if (null == negNode) {
//            return false;
//        }
//        if (null == negativeCondNodeList) {
//            negativeCondNodeList = new ArrayList<String>();
//        }
//        if (false == negativeCondNodeList.contains(negNode)) {
//            negativeCondNodeList.add(negNode);
//            return true;
//        }
//        return false;        
//    }
    


    

//    public String getNode1() {
//        return node1;
//    }
//
//    public void setNode1(String node1) {
//        this.node1 = node1;
//    }
//
//    public ArrayList<String> getNode2List() {
//        return node2List;
//    }
//
//    public void setNode2List(ArrayList<String> node2List) {
//        this.node2List = node2List;
//    }
//    
//    public boolean addNode2(String node2) {
//        if (null == node2) {
//            return false;
//        }
//        if (null == node2List) {
//            node2List = new ArrayList<String>();
//        }
//        if (false == node2List.contains(node2)) {
//            node2List.add(node2);
//            return true;
//        }
//        return false;
//    }
//
//
//
//    public String getNegConditionNode() {
//        return negConditionNode;
//    }
//
//    public void setNegConditionNode(String negConditionNode) {
//        this.negConditionNode = negConditionNode;
//    }

//    public ArrayList<Rule> getUniruleList() {
//        return uniruleList;
//    }
//
//    public boolean addUnirule(Rule r) {
//        if (null == r) {
//            return false;
//        }
//        if (null == uniruleList) {
//            uniruleList = new ArrayList<Rule>();
//        }
//        uniruleList.add(r);
//        return true;
//    }

//    public ArrayList<ConditionType> getOtherConditions() {
//        return otherConditions;
//    }
//
//    public void setOtherConditions(ArrayList<ConditionType> otherConditions) {
//        this.otherConditions = otherConditions;
//    }
//    
//    public boolean addOtherConditionType(ConditionType ct) {
//        if (null == otherConditions) {
//            otherConditions = new ArrayList<ConditionType>();
//        }
//        otherConditions.add(ct);
//        return true;
//    }


    
}
