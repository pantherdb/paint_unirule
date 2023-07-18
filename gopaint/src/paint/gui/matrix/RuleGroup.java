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
package org.paint.gui.matrix;

import edu.usc.ksom.pm.panther.paintCommon.Node;
import edu.usc.ksom.pm.panther.paintCommon.Rule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;



public class RuleGroup {
    private String id;
    private Hashtable<String, Rule> ruleLookup;
    private ArrayList<Node> applicableNodeList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Hashtable<String, Rule> getRuleLookup() {
        return ruleLookup;
    }
    
    public boolean addRule(Rule r) {
        if (null == r || null == r.getId()) {
            return false;
        }
        if (null == ruleLookup) {
            ruleLookup = new Hashtable<String, Rule>();
        }
        ruleLookup.put(r.getId(), r);
        return true;
    }
    
    public int getNumRules() {
        if (null == ruleLookup) {
            return 0;
        }
        return ruleLookup.size();
    }
    
    public List<Rule> getSortedRules() {
        if (null == ruleLookup) {
            return null;
        }
        ArrayList<Rule> ruleList = new ArrayList<Rule>(ruleLookup.values());
        Collections.sort(ruleList, new Comparator<Rule>() {
            public int compare(Rule o1, Rule o2) {
                if (null == o1.getId() && null == o2.getId()) {
                    return 0;
                }
                if (null == o1.getId()) {
                    return 1;
                }
                if (null == o2.getId()) {
                    return -1;
                } else {
                    return (o1.getId().compareTo(o2.getId()));
                }
            }
        });
        return ruleList;
    }

    public ArrayList<Node> getApplicableNodeList() {
        return applicableNodeList;
    }

    public void setApplicableNodeList(ArrayList<Node> applicableNodeList) {
        this.applicableNodeList = applicableNodeList;
    }

}
