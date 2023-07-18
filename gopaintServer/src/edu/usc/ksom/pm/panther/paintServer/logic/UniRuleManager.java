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
package edu.usc.ksom.pm.panther.paintServer.logic;

import com.sri.panther.paintCommon.util.FileUtils;
import com.sri.panther.paintServer.util.ConfigFile;
import edu.usc.ksom.pm.panther.paintCommon.DBReference;
import edu.usc.ksom.pm.panther.paintCommon.LabelValue;
import edu.usc.ksom.pm.panther.paintCommon.Rule;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;





public class UniRuleManager {
    private static UniRuleManager instance;
    
    public static final String URL_UNIRULE = ConfigFile.getProperty("url_unirule");
    
    public static final String DELIM_PARTS = Pattern.quote("\t");
    public static final int NUM_COLS_UNIRULE_FILE = 4;
    public static final int INDEX_ACC = 0;
    public static final int INDEX_RULE_ID = 1;
    public static final int INDEX_LABEL = 2;
    public static final int INDEX_VALUE = 3;
    
    public static final String DELIM_RULE = Pattern.quote("!");
    public static final String DELIM_LABEL = Pattern.quote("!");
    public static final String DELIM_VALUE = Pattern.quote("!");
    public static final String DELIM_RULE_ID_LABEL = "-";
    
    public static final String DB_REFERENCE_DB = "UniProt";
    public static final String DB_REFERENCE_ID = "????";
    
    
    private static HashMap<String, HashSet<Rule>> proteinIdToRuleLookup;

    
    private UniRuleManager() {
        
    }
   
    public static synchronized UniRuleManager getInstance() {
        if (null != instance) {
            return instance;
        }
        init();
        return instance;
    }    
        
    private static void init(){
        try {
            
//            try {
//                FileInputStream fin = new FileInputStream("C:\\usc\\svn\\new_panther\\curation\\paint\\uniprot\\trunk\\gopaintServer\\unirule" + ".ser");
//                ObjectInputStream ois = new ObjectInputStream(fin);
//                proteinIdToRuleLookup = (HashMap<String, HashSet<Rule>>) ois.readObject();
//                instance = new UniRuleManager();
//                return;
//            }
//            catch (Exception e) {            

            String[] uniRuleContents = FileUtils.readFileFromURL(new URL(URL_UNIRULE));
            if (null == uniRuleContents) {
                return;
            }
            proteinIdToRuleLookup = new HashMap<String, HashSet<Rule>>();
            for (String rule: uniRuleContents) {
                if (null == rule) {
                    continue;
                }
                String[] parts = rule.split(DELIM_PARTS);
                if (parts.length < NUM_COLS_UNIRULE_FILE) {
                    System.out.println("Skipping UniRule entry " + rule);
                    continue;
                }
                
                
                HashSet<Rule> ruleSet = proteinIdToRuleLookup.get(parts[INDEX_ACC]);
                if (null == ruleSet) {
                    ruleSet = new HashSet<Rule>();
                    proteinIdToRuleLookup.put(parts[INDEX_ACC], ruleSet);
                }
                
                
                String ruleListStr = parts[INDEX_RULE_ID];
                String[] ruleParts = ruleListStr.split(DELIM_RULE);
                String ruleLabelStr = parts[INDEX_LABEL];
                String[] ruleLabelParts = ruleLabelStr.split(DELIM_LABEL);
                String[] modifiedLabelParts = new String[ruleLabelParts.length];

                // Sometimes we can have the same label appear more than once.  We want to create a new rule whenever the label appears again
                HashMap<String, Integer> labelLookup = new HashMap<String, Integer>();
                int counter = 0;
                for (int i = 0; i < ruleParts.length; i++) {
                    String ruleId = ruleParts[i];
                    String label = ruleLabelParts[i];
                    Integer curValue = labelLookup.get(ruleId+label);
                    if (null == curValue) {
                        curValue = new Integer(0);
                    } else {
                        curValue++;
                    }
                    labelLookup.put(ruleId+label, curValue);
                    modifiedLabelParts[counter] = label + curValue;
                    counter++;
                }

                String ruleValueStr = parts[INDEX_VALUE];
                String[] ruleValueParts = ruleValueStr.split(DELIM_VALUE);
                int length = ruleParts.length;
                if (length != modifiedLabelParts.length || length != ruleValueParts.length) {
                    System.out.println("Skipping UniRule entry because number of entries are inconsistent " + rule);
                    continue;
                }
                for (int i = 0; i < length; i++) {

                    Rule r = new Rule();
                    r.setId(ruleParts[i] + DELIM_RULE_ID_LABEL + modifiedLabelParts[i]);
                    ruleSet.add(r);
                    LabelValue lv = new LabelValue();
                    lv.setLabel(ruleLabelParts[i]);
                    lv.setValue(ruleValueParts[i]);
                    r.setLabelValue(lv);
                    ///r.addProperty(ruleLabelParts[i], lv);

                }
//                for (String rulePart: ruleParts) {
//                    Rule r = new Rule();
//                    r.setId(rulePart);
//                    ruleSet.add(r);
//                    if (null != ruleLabelStr && null != ruleValueStr) {
//                        String[] labelParts = ruleLabelStr.split(DELIM_LABEL);
//                        String[] valueParts = ruleValueStr.split(DELIM_VALUE);
//                        if (null == labelParts || null == valueParts) {
//                            System.out.println("Error processing Unirule " + rulePart + " unable to get labels and values");
//                            continue;
//                        }
//                        if (labelParts.length != valueParts.length) {
//                            System.out.println("Error processing Unirule " + rulePart + " inconsistent number of labels and values");
//                            continue;                            
//                        }
//                        for (int i = 0; i < labelParts.length; i++) {
//                            LabelValue lv = new LabelValue();
//                            lv.setLabel(labelParts[i]);
//                            lv.setValue(valueParts[i]);
//                            r.addProperty(labelParts[i], lv);
//                        }           
//                    }
//                }
//            }
            
//                try {
//                    FileOutputStream fout = new FileOutputStream("C:\\usc\\svn\\new_panther\\curation\\paint\\uniprot\\trunk\\gopaintServer\\unirule" + ".ser");
//                    ObjectOutputStream oos = new ObjectOutputStream(fout);
//                    oos.writeObject(proteinIdToRuleLookup);
//                } catch (Exception ex) {
//
//                }

            }          
            
        }
        catch (IOException ie) {
            ie.printStackTrace();
            return;
        }        
        instance = new UniRuleManager();
    }
    
    public static Set<Rule> getUniRulesForProtein(String proteinId) {
        if (null == proteinId || null == proteinIdToRuleLookup) {
            return null;
        }
        HashSet<Rule> ruleSet = proteinIdToRuleLookup.get(proteinId);
        if (null == ruleSet) {
            return null;
        }
        return (Set<Rule>)ruleSet.clone();
    }
    
    public DBReference getDbRef(Rule r) {
        DBReference dbRef = new DBReference();
        dbRef.setEvidenceType(DB_REFERENCE_DB);
        dbRef.setEvidenceValue(DB_REFERENCE_ID);
        return dbRef;
    }
}
