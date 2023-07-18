/**
 *  Copyright 2021 University Of Southern California
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
import java.util.HashSet;


public class UAnnotation implements Serializable, IWith {
    private Rule rule;
//    private ArrayList<Rule> withRuleList;
    private ArrayList<UAnnotation> withAnnotList;
    private ArrayList<DBReference> withReferenceList; 
    private String evCode;
    private HashSet<Qualifier> qualifierSet;
    private Node annotatedNode;
    
    // Differentt types of rules have different prefixes
//    public static final String CURRENT_UNIRULE_PREFIX = "UR";       // These are already existing rules - These cannot be annotated
//    public static final String NEW_UNIRULE_PREFIX = "URES";        // Rules - where annotations are from Experimental evidences from Sequences (ES)
//    public static final String NEW_UNIRULE_PREFIX_STATEMENT_CURATOR = "URSC";        // Rules - where annotations are Statements from curators (SC)
    

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public ArrayList<DBReference> getWithReferenceList() {
        return withReferenceList;
    }

    public void setWithReferenceList(ArrayList<DBReference> withReferenceList) {
        this.withReferenceList = withReferenceList;
    }

//    public ArrayList<Rule> getWithRuleList() {
//        return withRuleList;
//    }
//
//    public void setWithRuleList(ArrayList<Rule> withRuleList) {
//        this.withRuleList = withRuleList;
//    }

//    public boolean addWithRule(Rule rule) {
//        if (null == rule) {
//            return false;
//        }
//        if (null == withRuleList) {
//            withRuleList = new ArrayList<Rule>();
//        }
//        withRuleList.add(rule);
//        return true;
//    }    
    
    public boolean addWithReference(DBReference dbRef) {
        if (null == dbRef) {
            return false;
        }
        if (null == withReferenceList) {
            withReferenceList = new ArrayList<DBReference>();
        }
        withReferenceList.add(dbRef);
        return true;
    }     
    
    
    public ArrayList<UAnnotation> getWithAnnotList() {
        return withAnnotList;
    }

    public void setWithAnnotList(ArrayList<UAnnotation> withAnnotList) {
        this.withAnnotList = withAnnotList;
    }

    public boolean addWithAnnotation(UAnnotation annot) {
        if (null == annot) {
            return false;
        }
        if (null == withAnnotList) {
            withAnnotList = new ArrayList<UAnnotation>();
        }
        withAnnotList.add(annot);
        return true;
    }

    public String getEvCode() {
        return evCode;
    }

    public void setEvCode(String evCode) {
        this.evCode = evCode;
    }
    
    public HashSet<Qualifier> getQualifierSet() {
        return qualifierSet;
    }

    public void setQualifierSet(HashSet<Qualifier> qualifierSet) {
        this.qualifierSet = qualifierSet;
    }
    
    public boolean addQualifier(Qualifier q) {
        if (null == q) {
            return false;
        }
        if (null == qualifierSet) {
            qualifierSet = new HashSet<Qualifier>();
        }
        if (false == QualifierDif.exists(qualifierSet, q)) {
            qualifierSet.add(q);
            return true;
        }
        return false;
    }

    public Node getAnnotatedNode() {
        return annotatedNode;
    }

    public void setAnnotatedNode(Node annotatedNode) {
        this.annotatedNode = annotatedNode;
    }
    
   
    
}
