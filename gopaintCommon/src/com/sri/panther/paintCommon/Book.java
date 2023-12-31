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
package com.sri.panther.paintCommon;

import edu.usc.ksom.pm.panther.paintCommon.CurationStatus;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map.Entry;

public class Book implements Comparable, Serializable {
    protected String id;
    protected String name;
    protected int curationStatus;
    protected Date curationStatusUpdateDate;        // Does not include check out date
    protected User lockedBy;
    protected User modifiedBy;
    protected int numLeaves;
    protected HashSet<String> orgSet;
    protected String comment;
    protected String commentUser;
    protected String commentOp;
    protected ArrayList<CurationStatus> curationStatusList;
    
    // A book can have multiple status.  Instead of using a hashtable or vector which will use up lots of memory when users retrieve
    // books from searches, use binary bits for each status.  Most importantly, this makes it easier to check for status
    //Note:  To add curation status, convert the number into binary and ensure that
    //       the bit is not already used.  
    public static final int CURATION_STATUS_NOT_CURATED = 0x01;
    public static final int CURATION_STATUS_AUTOMATICALLY_CURATED = 0x02;
    public static final int CURATION_STATUS_MANUALLY_CURATED = 0x04;
    public static final int CURATION_STATUS_CURATION_REVIEWED = 0x08;
    public static final int CURATION_STATUS_QAED = 0x10;                // 16
    public static final int CURATION_STATUS_CHECKED_OUT = 0x20;         // 32
    public static final int CURATION_STATUS_PARTIALLY_CURATED = 0x40;   // 64
    public static final int CURATION_STATUS_UNKNOWN = 0x80;             // 128
    public static final int CURATION_STATUS_REQUIRE_PAINT_REVIEW = 0x100;   // 256
    public static final int CURATION_STATUS_REQUIRE_PAINT_REVIEW_PTN_NOT_MAPPED = 0x200;   // 512
    public static final int CURATION_STATUS_REQUIRE_PAINT_REVIEW_PTN_CHANGE_FAMILIES = 0x400;   // 1024
    public static final int CURATION_STATUS_REQUIRE_PAINT_REVIEW_TRACKED_TO_CHILD_NODE = 0x800;   // 2048    
    public static final int CURATION_STATUS_APPLY = 0x1000;
    public static final int CURATION_STATUS_TEST = 0x2000;
    public static final int CURATION_STATUS_DISUSED = 0x4000;   
    
//    public static final int[] AVAILABLESTATUSES = {CURATION_STATUS_CHECKED_OUT, CURATION_STATUS_APPLY, 
//    CURATION_STATUS_TEST, CURATION_STATUS_DISUSED};
    
    public static final String LABEL_CURATION_STATUS_NOT_CURATED = "Not Curated";
    public static final String LABEL_CURATION_STATUS_AUTOMATICALLY_CURATED = "Automatically Curated";
    public static final String LABEL_CURATION_STATUS_MANUALLY_CURATED = "Manually Curated";
    public static final String LABEL_CURATION_STATUS_CURATION_REVIEWED = "Curation Reviewed";
    public static final String LABEL_CURATION_STATUS_QAED = "Curation QAed";
    public static final String LABEL_CURATION_STATUS_CHECKED_OUT = "Locked";
    public static final String LABEL_CURATION_STATUS_PARTIALLY_CURATED = "Partially Curated";
    public static final String LABEL_CURATION_STATUS_UNKNOWN = "Unknown";
    public static final String LABEL_CURATION_STATUS_REQUIRE_PAINT_REVIEW = "Require PAINT review";
    public static final String LABEL_CURATION_STATUS_REQUIRE_PAINT_REVIEW_PTN_NOT_MAPPED = "Review - PTN not mapped";
    public static final String LABEL_CURATION_STATUS_REQUIRE_PAINT_REVIEW_PTN_CHANGE_FAMILIES = "Review - PTN changed families";
    public static final String LABEL_CURATION_STATUS_REQUIRE_PAINT_REVIEW_PTN_TRACKED_TO_CHILD_NODE = "Review - PTN tracked to child node";    
    public static final String LABEL_CURATION_STATUS_APPLY = "Apply";
    public static final String LABEL_CURATION_STATUS_TEST = "Test";
    public static final String LABEL_CURATION_STATUS_DISUSED = "Unused";
    
    public static final String LABEL_COMMA_SPACE = Constant.STR_COMMA + Constant.STR_SPACE;

    public static Hashtable <Integer, String> STATUS_LABEL_LOOKUP = getStatusLabelLookup();
    
    private static Hashtable<Integer, String> getStatusLabelLookup() {
        Hashtable<Integer, String> rtnTbl = new Hashtable<Integer, String>();
        rtnTbl.put(CURATION_STATUS_CHECKED_OUT, LABEL_CURATION_STATUS_CHECKED_OUT);
        rtnTbl.put(CURATION_STATUS_UNKNOWN, LABEL_CURATION_STATUS_UNKNOWN);
        rtnTbl.put(CURATION_STATUS_APPLY, LABEL_CURATION_STATUS_APPLY);
        rtnTbl.put(CURATION_STATUS_TEST, LABEL_CURATION_STATUS_TEST);
        rtnTbl.put(CURATION_STATUS_DISUSED, LABEL_CURATION_STATUS_DISUSED);        
        return rtnTbl;
    }
    
    
    public Book(String id, String name, int curationStatus, User lockedBy) {
        this.id = id;
        this.name = name;
        this.curationStatus = curationStatus;
        this.lockedBy = lockedBy;
    
    }
    
    public Book(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public int getCurationStatus() {
        return curationStatus;
    }
    
    public void setCurationStatus(int curationStatus) {
        this.curationStatus = curationStatus;
    }
    
    public User getLockedBy() {
        return lockedBy;
    }
    
    public void setLockedBy(User lockedBy) {
        this.lockedBy = lockedBy;
    }

    public User getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(User modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
    
    public int compareTo(Object o) {
        Book comp = (Book)o;
        return id.compareTo(comp.id);
    }
    
    public static String getCurationStatusString(int status) {
        StringBuffer sb = new StringBuffer();
        for (Entry<Integer, String> entry : STATUS_LABEL_LOOKUP.entrySet()) {
            if (0 != (entry.getKey() & status)) {
                sb.append(entry.getValue());
                sb.append(LABEL_COMMA_SPACE);
            }
        }
        // Remove last comma space
        int length = sb.length();
        int suffixLength = LABEL_COMMA_SPACE.length();
        if (length > suffixLength) {
            return sb.toString().substring(0, length - suffixLength);
        }
        return sb.toString();
    }
        
    public boolean hasStatus(int compStatus) {
        if (0 != (curationStatus & compStatus)) {
            return true;
        }
        return false;
    }

    public Date getCurationStatusUpdateDate() {
        return curationStatusUpdateDate;
    }

    public void setCurationStatusUpdateDate(Date curationStatusUpdateDate) {
        this.curationStatusUpdateDate = curationStatusUpdateDate;
    }

    public int getNumLeaves() {
        return numLeaves;
    }

    public void setNumLeaves(int numLeaves) {
        this.numLeaves = numLeaves;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashSet<String> getOrgSet() {
        return orgSet;
    }
    
    public boolean addOrg(String org) {
        if (null == org) {
            return false;
        }
        if (null == orgSet) {
            orgSet = new HashSet<String>();
        }
        orgSet.add(org);
        return true;
    }

    public void setOrgSet(HashSet<String> orgSet) {
        this.orgSet = orgSet;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(String commentUser) {
        this.commentUser = commentUser;
    }

    public String getCommentOp() {
        return commentOp;
    }

    public void setCommentOp(String commentOp) {
        this.commentOp = commentOp;
    }

    public ArrayList<CurationStatus> getCurationStatusList() {
        return curationStatusList;
    }

    public void setCurationStatusList(ArrayList<CurationStatus> curationStatusList) {
        this.curationStatusList = curationStatusList;
    }
    
    public boolean addCurationStatus(CurationStatus cs) {
        if(null == cs) {
            return false;
        }
        if (null == curationStatusList) {
            curationStatusList = new ArrayList<CurationStatus>();
        }
        curationStatusList.add(cs);
        return true;
    }

}
