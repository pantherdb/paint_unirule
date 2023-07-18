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
package edu.usc.ksom.pm.panther.paintServer.logic;

import com.sri.panther.paintCommon.util.FileUtils;
import com.sri.panther.paintServer.util.ConfigFile;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.HashSet;

public class ReviewedSequenceManager {

    public static ReviewedSequenceManager instance;
    public static final String URL_REVIEWED_SEQ = ConfigFile.getProperty("url_reviewed");

    public static final String DELIM_PARTS = "\t";

    public static final int NUM_COLS_REVIEWED_SEQ_FILE = 2;
    public static final int INDEX_SEQ = 0;
    public static final int INDEX_INFO = 1;
    public static final String REVIEWED = "sp";

    private static HashSet<String> reviewedSeqSet;

    private ReviewedSequenceManager() {

    }

    public static synchronized ReviewedSequenceManager getInstance() {
        if (null != instance) {
            return instance;
        }
        init();
        return instance;
    }

    private static void init() {
        try {
//
//            try {
//                FileInputStream fin = new FileInputStream("C:\\usc\\svn\\new_panther\\curation\\paint\\uniprot\\trunk\\gopaintServer\\reviewed" + ".ser");
//                ObjectInputStream ois = new ObjectInputStream(fin);
//                reviewedSeqSet = (HashSet<String>) ois.readObject();
//                instance = new ReviewedSequenceManager();
//                return;
//            } catch (Exception e) {

                String[] reviewedSeqContents = FileUtils.readFileFromURL(new URL(URL_REVIEWED_SEQ));
                if (null == reviewedSeqContents) {
                    System.out.println("Did not retrieve any information from reviewed sequences file " + URL_REVIEWED_SEQ);
                    return;
                }
                reviewedSeqSet = new HashSet<String>();
                for (String seqInfo : reviewedSeqContents) {
                    String[] parts = seqInfo.split(DELIM_PARTS);
                    if (parts.length < NUM_COLS_REVIEWED_SEQ_FILE) {
                        System.out.println("Skipping Reeviewed sequences entry " + seqInfo);
                        continue;
                    }
                    if (null == parts[INDEX_SEQ] || null == parts[INDEX_INFO]) {
                        System.out.println("Skipping entry " + seqInfo + " since sequence or info is invalid");
                        continue;
                    }
                    String reviewed = parts[INDEX_INFO].trim();
                    if (REVIEWED.equals(reviewed)) {
                        reviewedSeqSet.add(parts[INDEX_SEQ].trim());
                    }
                }

//                try {
//                    FileOutputStream fout = new FileOutputStream("C:\\usc\\svn\\new_panther\\curation\\paint\\uniprot\\trunk\\gopaintServer\\reviewed" + ".ser");
//                    ObjectOutputStream oos = new ObjectOutputStream(fout);
//                    oos.writeObject(reviewedSeqSet);
//                } catch (Exception ex) {
//
//                }

//            }

        } catch (IOException ie) {
            ie.printStackTrace();
            reviewedSeqSet = null;
            return;
        }
        instance = new ReviewedSequenceManager();
    }

    public static boolean isReviewed(String seq) {
        if (null == seq || null == reviewedSeqSet) {
            return false;
        }
        return reviewedSeqSet.contains(seq);
    }

}
