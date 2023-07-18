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
package edu.usc.ksom.pm.panther.paint.matrix;

import edu.usc.ksom.pm.panther.paintCommon.Evidence;
import edu.usc.ksom.pm.panther.paintCommon.LabelValue;
import edu.usc.ksom.pm.panther.paintCommon.Node;
import edu.usc.ksom.pm.panther.paintCommon.NodeVariableInfo;
import edu.usc.ksom.pm.panther.paintCommon.Qualifier;
import edu.usc.ksom.pm.panther.paintCommon.QualifierDif;
import edu.usc.ksom.pm.panther.paintCommon.Rule;
import edu.usc.ksom.pm.panther.paintCommon.UAnnotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.paint.datamodel.GeneNode;
import org.paint.util.GeneNodeUtil;


public class NodeInfoForUniRuleMatrix {
    private boolean nonExpNonPAINTAnnot = false;
    private boolean expAnnot = false;
    private boolean PAINTAnnot = false;
    private HashSet<Qualifier> qSet;
    private boolean standardPositiveQualifier = false;
    private GeneNode geneNode;
    private String ruleId;

    private LabelValue lv;

    
    public NodeInfoForUniRuleMatrix(GeneNode gn, Rule rule) {
        if (null == gn) {
            return;
        }
        this.geneNode = gn;
        LabelValue ruleLabelValue = null;
        if (null != rule) {
            this.ruleId = rule.getId();
            ruleLabelValue = rule.getLabelValue();
        }
        if (true == GeneNodeUtil.inPrunedBranch(gn)) {
            return;
        }
        Node n = gn.getNode();
        NodeVariableInfo nvi = n.getVariableInfo();
        if (null == nvi) {
            return;
        }
        ArrayList<UAnnotation> uAnnotList = nvi.getuAnnotationList();
        if (null == uAnnotList) {
            return;
        }
        for (UAnnotation uAnnot : uAnnotList) {
            Rule r = uAnnot.getRule();
            
            if (null == ruleLabelValue || null == r) {
                continue;
            }
            
            if (false == ruleLabelValue.sameAs(r.getLabelValue())) {
                continue;
            }
            String evCode = uAnnot.getEvCode();
            if (Evidence.isExperimental(evCode)) {
                expAnnot = true;
            }
            if (Evidence.isPaint(evCode)) {
                PAINTAnnot = true;
            }
            if (false == expAnnot && false == PAINTAnnot) {
                nonExpNonPAINTAnnot = true;
            }
            lv = r.getLabelValue();
            
            
            if (null != uAnnot.getQualifierSet()) {
                qSet = new HashSet<Qualifier>();
                QualifierDif.addIfNotPresent(qSet, uAnnot.getQualifierSet());
            } else {
                this.standardPositiveQualifier = true;
            }
        }
    }
    
    public boolean containsNot() {
        return QualifierDif.containsNegative(qSet);
    }
    
    public boolean containsPositiveAndNegativeQualifier() {
        if (true == standardPositiveQualifier && true == containsNot()) {
            return true;
        }
        return false;
    }
    
    public boolean containsMultiplePositiveQualifier() {
        if (true == standardPositiveQualifier && QualifierDif.containsPositive(qSet)) {
            return true;
        }
        int count = 0;
        if (null != qSet) {           
            for (Qualifier q: qSet) {
                if (true == q.isNot()) {
                    continue;
                }
                count++;
                if (count > 1) {
                    return true;
                }
            }
        }
        if (true == standardPositiveQualifier && count >= 1) {
            return true;
        }
        return false;
    }

    public boolean isNonExpNonPAINTAnnot() {
        return nonExpNonPAINTAnnot;
    }

    public void setNonExpNonPAINTAnnot(boolean nonExpNonPAINTAnnot) {
        this.nonExpNonPAINTAnnot = nonExpNonPAINTAnnot;
    }

    public boolean isExpAnnot() {
        return expAnnot;
    }

    public void setExpAnnot(boolean expAnnot) {
        this.expAnnot = expAnnot;
    }

    public boolean isPAINTAnnot() {
        return PAINTAnnot;
    }

    public void setPAINTAnnot(boolean PAINTAnnot) {
        this.PAINTAnnot = PAINTAnnot;
    }

    public boolean isStandardPositiveQualifier() {
        return standardPositiveQualifier;
    }

    public void setStandardPositiveQualifier(boolean standardPositiveQualifier) {
        this.standardPositiveQualifier = standardPositiveQualifier;
    }

    public GeneNode getGeneNode() {
        return geneNode;
    }

    public String getRuleId() {
        return ruleId;
    }

    public Set<Qualifier> getqSet() {
        if (null == qSet) {
            return null;
        }
        return (Set<Qualifier>)qSet.clone();
    }

    public LabelValue getLabelValue() {
        return lv;
    }
    
}
