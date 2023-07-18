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
package org.paint.tools;

import com.sri.panther.paintCommon.Book;
import com.sri.panther.paintCommon.Constant;
import com.sri.panther.paintCommon.util.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.paint.io.UniruleSoapHelper;
import org.uniprot.unirule_1.UniRuleType;
import org.uniprot.unirule_import.schema.PantherRuleSummary;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class GenerateUPLChangeInfo {
    public static final int NUM_FIELDS_NODE_CHANGE_FILE = 5;
    public static final int INDEX_NEW_FAM = 1;
    public static final int INDEX_PREV_FAM = 2;
    public static final int INDEX_PREV_PTN = 3;
    public static final String MSG_NOT_PROCESSING_UNKNOWN_STATUS_FOR_BOOK = " Not processing unknown status\n";
    public static final String MSG_UNABLE_TO_OPEN_FILE = "Unable to open file ";
    public static final String MSG_UNABLE_TO_RETRIEVE_FAMILY_INFORMATION_FOR_FAMILY = "Unable to retrieve famiy information for family ";    
    public static final String MSG_UNABLE_TO_RETRIEVE_UNIRULE_INFORMATION_FOR_FAMILY = "Unable to retrieve unirule information for family ";
    public static final String MSG_UNIRULE_NODES_FOR_FAMILY = "No UniRule nodes found for family ";
    public static final String MSG_PANTHER_NODES_FOR_FAMILY = "No PANTHER nodes found for family ";
    public static final String MSG_UNIRULE_NODE_NOT_FOUND = " does not have node ";
    public static final String MSG_NODE_MOVED_TO_FAMILY_PART_1 = ", node ";    
    public static final String MSG_NODE_MOVED_TO_FAMILY_PART_2 = " moved to family "; 
    public static final String MSG_INVALID_NUMBER_OF_FIELDS_IN_NODE_CHANGE_FILE_FINRST_LINE = " Node change file does not have expected number of fields in first line = " + NUM_FIELDS_NODE_CHANGE_FILE;
    public static final String MSG_INVALID_NUMBER_OF_FIELDS_IN_NODE_CHANGE_FILE = "Node change file does not have expected number of fields in line = ";
    public static final String MSG_NO_UNIRULE_BOOKS_FOR_PROCESSING = "No books found in UniRule for processing\n.";
    public static final String REPLACE_STR = "%REPLACE_STR%";
    public static final String TREE_STRUCT_URL_SUFFIX = "/webservices/annotationNode.jsp?book=" + REPLACE_STR + "&searchType=SEARCH_TYPE_ANNOTATION_NODE_INFO";
    public static final StringBuffer BUFFER_INFO = new StringBuffer();
    
    public static final String ELEMENT_NODE = "node";
    public static final String ELEMENT_PUBLIC_ID = "public_id";    

    private  HashMap<String, ArrayList<String>> previousFamToNewFamLookup = new HashMap<String, ArrayList<String>>();
    private  HashMap<String, ArrayList<String>> previousFamToNodesLookup = new HashMap<String, ArrayList<String>>();    
    
    public GenerateUPLChangeInfo(String userName, String password, String pantherServerUrl, String nodeChangeFile) {
        if (false == setNodeChangeInfo(nodeChangeFile)) {
            return;
        }
        // Commented out temporarily

        List<PantherRuleSummary> result = UniruleSoapHelper.getSummaries(userName, password);
        if (null == result) {
            BUFFER_INFO.append(MSG_NO_UNIRULE_BOOKS_FOR_PROCESSING);
            return;
        }
        
        for (int i = 0; i < result.size(); i++) {
//        for (int i = 0; i < 1; i++) {
            PantherRuleSummary prs = result.get(i);
            String famId = prs.getPantherId();
            System.out.println("Processing book " + famId + " " + (i + 1) + " of " + result.size());
            int status = UniruleSoapHelper.getCurationStatusIdForStatusString(prs.getStatus());
            
//            String famId = "PTHR10000";
            if (status == Book.CURATION_STATUS_UNKNOWN) {
                BUFFER_INFO.append(famId + MSG_NOT_PROCESSING_UNKNOWN_STATUS_FOR_BOOK);
                continue;
            }
            ExecutorService executor = Executors.newFixedThreadPool(2);
            LoadFamilyNodes familyWorker = new LoadFamilyNodes(famId, pantherServerUrl);
            LoadUnirule uniruleWorker = new LoadUnirule(famId, userName, password);
            executor.execute(familyWorker);
            executor.execute(uniruleWorker);
            executor.shutdown();

            // Wait until all threads finish
            while (!executor.isTerminated()) {

            }
            if (false == familyWorker.opSuccess.booleanValue()) {
                BUFFER_INFO.append(MSG_UNABLE_TO_RETRIEVE_FAMILY_INFORMATION_FOR_FAMILY + famId + Constant.STR_NEWLINE);
                continue;
            }
            if (false == uniruleWorker.opSuccess.booleanValue()) {
                BUFFER_INFO.append(MSG_UNABLE_TO_RETRIEVE_UNIRULE_INFORMATION_FOR_FAMILY + famId + Constant.STR_NEWLINE);
                continue;
            }
            if (null == familyWorker.publicIdSet || familyWorker.publicIdSet.isEmpty()) {
                BUFFER_INFO.append(MSG_PANTHER_NODES_FOR_FAMILY + famId + Constant.STR_NEWLINE);
                continue;
            }
            for (String node : uniruleWorker.nodeSet) {
                if (false == familyWorker.publicIdSet.contains(node)) {
                    ArrayList<String> movedNodeList = previousFamToNodesLookup.get(famId);
                    if (null == movedNodeList) {
                        BUFFER_INFO.append(famId + MSG_UNIRULE_NODE_NOT_FOUND + node + Constant.STR_NEWLINE);
                        continue;
                    }
                    if (movedNodeList.contains(node)) {
                        BUFFER_INFO.append(famId + MSG_NODE_MOVED_TO_FAMILY_PART_1 + node + MSG_NODE_MOVED_TO_FAMILY_PART_2 +  String.join(Constant.STR_COMMA, previousFamToNodesLookup.get(famId)) + Constant.STR_NEWLINE);
                        continue;
                    }
                }
            }
            
        }

    }
    
    
    public boolean setNodeChangeInfo(String nodeChangeFile) {
        try {
            Scanner scanner = new Scanner(new File(nodeChangeFile));
            int counter = 0;
            int size = -1;
            while (scanner.hasNextLine()) {

                // process the line
                String line = scanner.nextLine();
                String[] items = line.split(Pattern.quote(Constant.STR_TAB));
                if (-1 == size) {
                    size = items.length;
                    if (size != NUM_FIELDS_NODE_CHANGE_FILE) {
                        BUFFER_INFO.append(nodeChangeFile + MSG_INVALID_NUMBER_OF_FIELDS_IN_NODE_CHANGE_FILE_FINRST_LINE + Constant.STR_NEWLINE);
                        return false;
                    }
                    continue;   // Do not have to process first line
                }

                size = items.length;
                if (size != NUM_FIELDS_NODE_CHANGE_FILE) {
                    BUFFER_INFO.append(nodeChangeFile + MSG_INVALID_NUMBER_OF_FIELDS_IN_NODE_CHANGE_FILE_FINRST_LINE + counter + 1);
                    return false;
                }
                counter++;

                String[] newFamParts = items[INDEX_NEW_FAM].split(Constant.STR_TAB);
                String[] prevFamParts = items[INDEX_PREV_FAM].split(Constant.STR_TAB);
                String[] prevFamNodes = items[INDEX_PREV_PTN].split(Constant.STR_TAB);
                for (String prevFamPart : prevFamParts) {
                    ArrayList<String> prevToNewFamList = previousFamToNewFamLookup.get(prevFamPart);
                    if (null == prevToNewFamList) {
                        prevToNewFamList = new ArrayList<String>();
                        previousFamToNewFamLookup.put(prevFamPart, prevToNewFamList);
                    }
                    else {
                        System.out.println("Found multiple new families for previous Family Part " + prevFamPart);                        
                    }
                    prevToNewFamList.addAll(new ArrayList(Arrays.asList(newFamParts)));
                    
                    ArrayList<String> prevFamToNodesList = previousFamToNodesLookup.get(prevFamPart);
                    if (null == prevFamToNodesList) {
                       prevFamToNodesList  = new ArrayList<String>();
                        previousFamToNodesLookup.put(prevFamPart, prevFamToNodesList);
                    }
                    else {
                        System.out.println("Found nodes in multiple families for " + prevFamPart);      
                    }
                    prevFamToNodesList.addAll(new ArrayList(Arrays.asList(prevFamNodes)));
                }
            }
        } catch (FileNotFoundException fnf) {
            BUFFER_INFO.append(MSG_UNABLE_TO_OPEN_FILE + nodeChangeFile + Constant.STR_NEWLINE);
            return false;
        }
        return true;
    }

    public class LoadFamilyNodes implements Runnable {

        private final String familyId;
        private final String pantherServerUrl;
        public Boolean opSuccess = Boolean.FALSE;
        public HashSet<String> publicIdSet = new HashSet<String>();

        LoadFamilyNodes(String familyId, String pantherServerUrl) {
            this.familyId = familyId;
            this.pantherServerUrl = pantherServerUrl;

        }

        public void run() {
            try {
                String xml = Utils.readFromUrl(pantherServerUrl + TREE_STRUCT_URL_SUFFIX.replace(REPLACE_STR, familyId), -1, -1);
                if (null == xml) {
                    return;
                }
                xml = xml.trim();
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));

                // normalize text representation
                doc.getDocumentElement().normalize();

                NodeList nodes = doc.getElementsByTagName(ELEMENT_NODE);
                if (null != nodes) {

                    for (int i = 0; i < nodes.getLength(); i++) {
                        Node aNode = nodes.item(i);
                        if (Node.ELEMENT_NODE != aNode.getNodeType()) {
                            continue;
                        }

                        NodeList ids = ((Element) aNode).getElementsByTagName(ELEMENT_PUBLIC_ID);
                        if (null != ids && 0 != ids.getLength()) {
                            for (int j = 0; j < ids.getLength(); j++) {
                                Node publicId = ids.item(j);
                                if (Node.ELEMENT_NODE != publicId.getNodeType()) {
                                    continue;
                                }
                                publicIdSet.add(Utils.getTextFromElement((Element) publicId));
                            }
                        }
                    }
                }
                opSuccess = Boolean.TRUE;

            } catch (SAXParseException err) {
                err.printStackTrace();

            } catch (SAXException e) {
                e.printStackTrace();

            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
    
    public class LoadUnirule implements Runnable {

        private final String familyId;
        private final String userId;
        private final String password;
        public UniRuleType uniruleType = null;
        HashSet<String> nodeSet = null;
        public Boolean opSuccess = null;

        LoadUnirule(String familyId, String userId, String password) {
            this.familyId = familyId;
            this.userId = userId;
            this.password = password;
        }

        public void run() {
            ArrayList<Boolean> successList = new ArrayList<Boolean>();
            uniruleType = UniruleSoapHelper.getRuleForFamily(familyId, successList, userId, password);
            if (null == successList || 0 == successList.size()) {
                uniruleType = null;
                opSuccess = Boolean.FALSE;
                return;
            }
            opSuccess = successList.get(0);
            if (false == opSuccess.booleanValue()) {
                uniruleType = null;
            }
            if (null != uniruleType) {
                nodeSet = UniruleSoapHelper.getPosssibleNodes(uniruleType);
            }
        }
    } 
    
    public static final void main(String args[]) {
        System.out.println("Specify 4 parameters:  user name, password and panther server URL, full path to tracked_ptns_changed_fams.tsv file\n");
        if (4 != args.length) {
            System.out.println("Specify 4 parameters:  user name, password and panther server URL, full path to tracked_ptns_changed_fams.tsv file\n");
            return;
        }
        GenerateUPLChangeInfo guci = new GenerateUPLChangeInfo(args[0], args[1], args[2], args[3]);
//        GenerateUPLChangeInfo guci = new GenerateUPLChangeInfo("user", "user password", "http://localhost:8080", "C:/usc/svn/new_panther/curation/paint/uniprot/branches/UPL17/tracked_ptns_changed_fams - Sheet1.tsv");
        System.out.println(guci.BUFFER_INFO.toString());
    }
}
    