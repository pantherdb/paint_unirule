/**
 *  Copyright 2020 University Of Southern California
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

package org.paint.gui;

import java.util.HashMap;

import org.paint.gui.event.AspectChangeEvent;
import org.paint.gui.event.EventManager;

public class AnnotationTypeSelector {

	private static final long serialVersionUID = 1L;
	private static AnnotationTypeSelector selector;

	private AnnotationType annotationType;

	public enum AnnotationType {
		BIOLOGICAL_PROCESS,
		CELLULAR_COMPONENT,
		MOLECULAR_FUNCTION,
                UNIRULE;

		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	public final static HashMap<String, String> aspects = new HashMap<String, String> ();
	static	{
		aspects.put(AnnotationType.BIOLOGICAL_PROCESS.toString(), "P");
		aspects.put(AnnotationType.CELLULAR_COMPONENT.toString(), "C");
		aspects.put(AnnotationType.MOLECULAR_FUNCTION.toString(), "F");
                aspects.put(AnnotationType.UNIRULE.toString(), "U");
	}
        
        public static final HashMap<String, String> LETTER_TO_ASPECT = new HashMap<String, String> ();
        static {
		LETTER_TO_ASPECT.put("P", AnnotationType.BIOLOGICAL_PROCESS.toString());
		LETTER_TO_ASPECT.put("C", AnnotationType.CELLULAR_COMPONENT.toString());
		LETTER_TO_ASPECT.put("F", AnnotationType.MOLECULAR_FUNCTION.toString());
                LETTER_TO_ASPECT.put("U", AnnotationType.UNIRULE.toString());
        }


	private AnnotationTypeSelector() {
		annotationType = AnnotationType.MOLECULAR_FUNCTION;
	}

	public static AnnotationTypeSelector inst() {
		if (selector == null) {
			selector = new AnnotationTypeSelector();
		}
		return selector;
	}

	public AnnotationType getAnnotationType() {
		return annotationType;
	}

	public void setAspect(AnnotationType newAnnotType) {
		if (annotationType != newAnnotType) {
			this.annotationType = newAnnotType;
			EventManager.inst().fireAspectChangeEvent(new AspectChangeEvent(this));
		}
	}

}
