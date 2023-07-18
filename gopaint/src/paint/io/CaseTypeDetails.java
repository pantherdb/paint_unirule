/**
 * Copyright 2022 University Of Southern California
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
package org.paint.io;

import java.util.ArrayList;
import org.uniprot.unirule_1.MainType;


public class CaseTypeDetails {
    private MainType caseType;
    private String caseNum;
    private ArrayList<String> msgList;

    public MainType getCaseType() {
        return caseType;
    }

    public void setCaseType(MainType caseType) {
        this.caseType = caseType;
    }

    public String getCaseNum() {
        return caseNum;
    }

    public void setCaseNum(String caseNum) {
        this.caseNum = caseNum;
    }

    public ArrayList<String> getMsgList() {
        return msgList;
    }

    public void setMsgList(ArrayList<String> msgList) {
        this.msgList = msgList;
    }
    
    public boolean addMsg(String msg) {
        if (null == msg) {
            return false;
        }
        if (null == msgList) {
            msgList = new ArrayList<String>();
        }
        msgList.add(msg);
        return true;
    }
}
