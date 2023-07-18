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
import edu.usc.ksom.pm.panther.paintCommon.LabelValue;
import edu.usc.ksom.pm.panther.paintCommon.Rule;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;


public class SequencePropertiesManager {
    private static SequencePropertiesManager instance;
    public static final String URL_SEQUENCE_PROPERTIES = ConfigFile.getProperty("url_swissprot_stats");

    public static final String DELIM_PARTS = Pattern.quote("\t");
    public static final int NUM_COLS_PROPERTIES_FILE = 3;
    
    public static final int INDEX_SEQ = 0;
    public static final int INDEX_LABEL = 1;
    public static final int INDEX_VALUE = 2;

    public static final String DELIM_LABEL = Pattern.quote("!");
    public static final String DELIM_VALUE = Pattern.quote("!"); 
    
    private static HashMap<String, ArrayList<LabelValue>> proteinIdToPropertiesLookup;
    
    // Need to create new rules based on properties to nodes
    // Each property can be a new rule
    // If a property occues in more than one sequence, then the associated rule should get the same identifier
    private static HashMap<String, Rule> newRuleLookup;
    private static HashMap<String, ArrayList<Rule>> proteinIdToNewRuleLookup;
    
    public static final String DELIM_NEW_RULE_ID = "-";
    
    private SequencePropertiesManager() {
        
    }
    
    public static synchronized SequencePropertiesManager getInstance() {
        if (null != instance) {
            return instance;
        }
        init();
        return instance;
    }
    
    private static void init() {
        try {
            String[] propertiesContents = FileUtils.readFileFromURL((new URL(URL_SEQUENCE_PROPERTIES)));
            if (null == propertiesContents) {
                return;
            }
            proteinIdToPropertiesLookup = new HashMap<String, ArrayList<LabelValue>>();
            newRuleLookup = new HashMap<String, Rule>();
            proteinIdToNewRuleLookup = new HashMap<String, ArrayList<Rule>> ();
            int counter = 0;
            for (String seqInfo: propertiesContents) {
                String[] parts = seqInfo.split(DELIM_PARTS);
                if (parts.length < NUM_COLS_PROPERTIES_FILE) {
                    System.out.println("Skipping sequence information entry " + seqInfo);
                    continue;
                }
                
                String labelStr = parts[INDEX_LABEL];
                String[] labelParts = labelStr.split(DELIM_LABEL);
                String valueStr = parts[INDEX_VALUE];             
                String[] valueParts = valueStr.split(DELIM_VALUE);
                if (labelParts.length != valueParts.length) {
                    System.out.println("Skipping sequence information entry due to mismatch in labels and values " + seqInfo);                    
                    continue;
                }
                ArrayList<LabelValue> pairs = new ArrayList<LabelValue>();
                proteinIdToPropertiesLookup.put(parts[INDEX_SEQ], pairs);
                HashMap<String, Integer> typeCount = new HashMap<String, Integer>();
                for (int i = 0; i < labelParts.length; i++) {
                    LabelValue lv = new LabelValue();
                    lv.setLabel(labelParts[i]);
                    lv.setValue(valueParts[i]);

//                    if ("Zinc finger ZZ-type and EF-hand domain-containing protein 1".equals(lv.getValue())) {
//                        System.out.println("Here");
//                    }
                    pairs.add(lv);
                    
                    String key = Rule.UNIRULE_PREFIX_SWISS + lv.getLabel() + lv.getValue();
                    Rule r = newRuleLookup.get(key);
                    if (null == r) {
                        Integer count = typeCount.get(labelParts[i]);
                        if (null == count) {
                            typeCount.put(labelParts[i], 0);
                        } else {
                            typeCount.put(labelParts[i], count + 1);
                        }
                        r = new Rule();
                        r.setLabelValue(lv);
//                        r.addProperty(lv.getLabel(), lv);
                        r.setId(Rule.UNIRULE_PREFIX_SWISS + counter + DELIM_NEW_RULE_ID  + lv.getLabel() + typeCount.get(lv.getLabel()));
                        newRuleLookup.put(key, r); 
                    }
                    
                    ArrayList<Rule> rules =  proteinIdToNewRuleLookup.get(parts[INDEX_SEQ]);
                    if (null == rules) {
                        rules = new ArrayList<Rule>();
                        proteinIdToNewRuleLookup.put(parts[INDEX_SEQ], rules);
                    }
                    rules.add(r);
                }
                counter++;
            }
        }
        catch(IOException ie) {
            ie.printStackTrace();
            return;
        }
        instance = new SequencePropertiesManager();
    }
    
    public static List<LabelValue> getProertiesForProtein(String proteinId) {
        if (null == proteinId || null == proteinIdToPropertiesLookup) {
            return null;
        }
        ArrayList<LabelValue> rtnList = proteinIdToPropertiesLookup.get(proteinId);
        if (null == rtnList) {
            return null;
        }
        return (List<LabelValue>)rtnList.clone();
    }
    
    public static List<Rule> getNewRulesForProtein(String proteinId) {
        if (null == proteinId || null == proteinIdToNewRuleLookup) {
            return null;
        }
        ArrayList<Rule> rtnList = proteinIdToNewRuleLookup.get(proteinId);
        if (null == rtnList) {
            return null;
        }
        return (List<Rule>)rtnList.clone();
    }    
}
