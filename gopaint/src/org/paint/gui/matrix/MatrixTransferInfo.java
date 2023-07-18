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
package org.paint.gui.matrix;

import edu.usc.ksom.pm.panther.paintCommon.Rule;


public class MatrixTransferInfo {
//    private TermAncestor termAncestor;
//    private GeneNode matrixClickedNode;
    private Rule rule;
    private String term;
    
    
    
//    MatrixTransferInfo(TermAncestor termAncestor, GeneNode matrixClickedNode) {
//        this.termAncestor = termAncestor;
//        this.matrixClickedNode = matrixClickedNode;
//    }
    
    MatrixTransferInfo(Rule rule) {
        this.rule = rule;
    }
    
    MatrixTransferInfo(String term) {
        this.term = term;
    }

    public String getTerm() {
        return term;
    }

//    public TermAncestor getTermAncestor() {
//        return termAncestor;
//    }
//
//    public GeneNode getMatrixClickedNode() {
//        return matrixClickedNode;
//    }
    
    public Rule getRule() {
        return rule;
    }
    
}
