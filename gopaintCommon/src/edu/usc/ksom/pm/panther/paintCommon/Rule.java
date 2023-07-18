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

import com.sri.panther.paintCommon.Constant;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;




public class Rule implements Serializable {
    private String id;
    
    // Rules always have a label and value.  They can have an exception
    private LabelValue labelValue;
    
    // Used to store org.uniprot.unirule_1.RuleExceptionType data structure.  Specifically not defining its type here.  This object is shared by the server and client and has to be serializable
    // RuleExceptionType is not serializable and the server code does not currently depend on Unirule package.  Only teh client needs to use information in org.uniprot.unirule_1.RuleExceptionType
    private transient Object exception = null;
    
    
    public static final String PROPERTY_EC = "DEEC";
    public static final String PROPERTY_DERF = "DERF";    
    public static final String PROPERTY_DEEC = PROPERTY_EC;
    public static final String PROPERTY_DEAF = "DEAF";
    public static final String PROPERTY_DEAE = "DEAE";
    public static final String PROPERTY_CCFU = "CCFU";    
    public static final String PROPERTY_CCCA = "CCCA";
    public static final String PROPERTY_CCLO = "CCLO";
    
    public static final String PROPERTY_DERS = "DERS";
    public static final String PROPERTY_SPKW = "SPKW";
    public static final String PROPERTY_DRGO = "DRGO";
    public static final String PROPERTY_GNNM = "GNNM";
    public static final String PROPERTY_GNSY = "GNSY";
    public static final String PROPERTY_CCSI = "CCSI";
    public static final String PROPERTY_CCSU = "CCSU";
    public static final String PROPERTY_CCPA = "CCPA";    

    public static final String PROPERTY_CCCC = "CCCC";
    public static final String PROPERTY_CCCT = "CCCT";
    public static final String PROPERTY_CCDO = "CCDO";
    public static final String PROPERTY_CCER = "CCER";
    public static final String PROPERTY_CCIN = "CCIN";
    public static final String PROPERTY_CCPT = "CCPT";
    public static final String PROPERTY_CCCO = "CCCO";
  
    public static final Set<String> PAINT_RULES_WITH_POSSIBLE_EXP_EVIDENCE = initRulesWithPossibleExpEvidence();
    public static final Set<String> PAINT_RULES_SUPPORTED_BY_EXP_EVIDENCE = initRulesSupByExpEvdnceSet();
    
    // Differentt types of rules have different prefixes
    public static final String UNIRULE_PREFIX = "UR";                   // These are already existing rules - These cannot be annotated?
    public static final String UNIRULE_PREFIX_SWISS = "URSWISS";        // Rules from swissprot
//    public static final String UNIRULE_PREFIX_SWISSE = "URSWISSE";        // Rules from swissprot with exceptions    
    public static final String UNIRULE_PREFIX_NEW = "URNEW";            // Newly created rules    
    public static final String UNIRULE_PREFIX_NEWE = "URNEWE";            // Newly created rules with exceptions
    
    public Rule() {
        
    }

    private static Set<String> initRulesWithPossibleExpEvidence() {
        HashSet<String> rtnSet = new HashSet<String>();
        rtnSet.add(PROPERTY_DERF);
        rtnSet.add(PROPERTY_DEEC);
        rtnSet.add(PROPERTY_DEAF);        
        rtnSet.add(PROPERTY_DEAE);
        rtnSet.add(PROPERTY_CCFU);
        rtnSet.add(PROPERTY_CCCA);
        rtnSet.add(PROPERTY_CCLO);        
        return rtnSet;
    }

    
    private static Set<String> initRulesSupByExpEvdnceSet() {
        HashSet<String> rtnSet = new HashSet<String>();       
        return rtnSet;
    }
    
    public boolean isRuleSupportedByExpEvidence() {
        return PAINT_RULES_SUPPORTED_BY_EXP_EVIDENCE.contains(getLabel());
    }
    
    public static String generateCaseId(int caseNum, int famNextId, String label, boolean isExceptionRule) {
        if (true == isExceptionRule) {
            return UNIRULE_PREFIX_NEWE + Constant.STR_UNDERSCORE +  caseNum + Constant.STR_DASH + famNextId + Constant.STR_DASH + label;
        }
        return UNIRULE_PREFIX_NEW + Constant.STR_UNDERSCORE +  caseNum + Constant.STR_DASH + famNextId + Constant.STR_DASH + label;
    }
    
//    public static String generateNextCaseId(String currentId, int caseNum) {
//        int index = currentId.indexOf(Constant.STR_DASH);
//        return currentId.substring(0, index) + Constant.STR_UNDERSCORE + caseNum + currentId.substring(index);
//    }   
    
    
    public Rule (String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public void setLabelValue(LabelValue lv) {
        this.labelValue = lv;
    }
    
    public LabelValue getLabelValue() {
        return this.labelValue;
    }
    
    public String getLabel() {
        if (null != labelValue) {
            return labelValue.getLabel();
        }
        return null;
    }
    
    public String getValue() {
        if (null != labelValue) {
            return labelValue.getValue();
        }
        return null;
    }
    
    public boolean isCuratable() {
        if (id != null && (id.startsWith(Rule.UNIRULE_PREFIX_SWISS) || id.startsWith(Rule.UNIRULE_PREFIX_NEW))) {
            return true;
        }
        return false;
    }
    
    
//    public boolean addProperty(String property, LabelValue lv) {
//        if (null == property || null == lv) {
//            return false;
//        }
//        if (null == propertyLookup) {
//            propertyLookup = new HashMap<String, ArrayList<LabelValue>>();
//        }
//        ArrayList<LabelValue> lvList = propertyLookup.get(property);
//        if (null == lvList) {
//            lvList = new ArrayList<LabelValue>();
//            propertyLookup.put(property, lvList);
//        }
//        lvList.add(lv);
//        return true;
//    }
//
//    public HashMap<String, ArrayList<LabelValue>> getPropertyLookup() {
//        if (null ==  propertyLookup) {
//            return null;
//        }
//        return (HashMap<String, ArrayList<LabelValue>>)propertyLookup.clone();
//    }
//
//    public void setPropertyLookup(HashMap<String, ArrayList<LabelValue>> propertyLookup) {
//        this.propertyLookup = propertyLookup;
//    }
    
    

    // Currently rules only have one property and one label value.  The label is same as property lookup    
//    public String getPropertyLabel() {
//        if (null == propertyLookup) {
//            return null;
//        }
//        for (String label:propertyLookup.keySet()) {
//            return label;
//        }
//        return null;
//    }
    
    // Currently rules only have one property and one label value.  The label is same as property lookup    
//    public String getPropertyValue() {
//        if (null == propertyLookup) {
//            return null;
//        }
//        for (String label:propertyLookup.keySet()) {
//            ArrayList<LabelValue> list = propertyLookup.get(label);
//            if (null != list) {
//                LabelValue lv = list.get(0);
//                return lv.getValue();
//            }
//        }
//        return null;        
//    }
//    
//    public LabelValue getLabelValue() {
//        if (null == propertyLookup) {
//            return null;
//        }
//        for (String label:propertyLookup.keySet()) {
//            ArrayList<LabelValue> list = propertyLookup.get(label);
//            if (null != list && 0 != list.size()) {
//                return list.get(0);
//            }
//        }
//        return null;
//    }

    public Object getException() {
        return exception;
    }

    public void setException(Object exception) {
        this.exception = exception;
    }
    
}
