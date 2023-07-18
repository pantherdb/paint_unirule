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
package org.paint.datamodel;

import edu.usc.ksom.pm.panther.paintCommon.Annotation;
import edu.usc.ksom.pm.panther.paintCommon.Node;


public class Association {
    private Annotation annotation;
    private Node node;
//    private boolean nodeAssociatedToAnnotation = false;                 // We create associations for parent terms where child term is annotated.  Use this field
//                                                                        // to indicate association is not 'real'

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
//        if (true == annotation.isAnnotIsToChildTerm()) {
//            nodeAssociatedToAnnotation = false;
//        }
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

//    public boolean isNodeAssociatedToAnnotation() {
//        return nodeAssociatedToAnnotation;
//    }

//    public void setNodeAssociatedToAnnotation(boolean nodeAssociatedToAnnotation) {
//        this.nodeAssociatedToAnnotation = nodeAssociatedToAnnotation;
//    }
    
}
