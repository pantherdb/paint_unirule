/**
 * Copyright 2020 University Of Southern California
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package edu.usc.ksom.pm.panther.paintServer.webservices;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class WSUtil {
    public static Element createTextNode(Document doc, String elementTag, String text) {
        if (null == doc || null == elementTag || null == text) {
            return null;
        }
        Element elem = doc.createElement(elementTag);
        Text t = doc.createTextNode(text);
        elem.appendChild(t);
        return elem;
    }
}
