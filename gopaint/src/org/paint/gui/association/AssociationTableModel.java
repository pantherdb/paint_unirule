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
package org.paint.gui.association;

import com.sri.panther.paintCommon.Constant;
import com.sri.panther.paintCommon.util.StringUtils;
import edu.usc.ksom.pm.panther.paint.matrix.NodeInfoForMatrix;
import edu.usc.ksom.pm.panther.paintCommon.Annotation;
import edu.usc.ksom.pm.panther.paintCommon.AnnotationDetail;
import edu.usc.ksom.pm.panther.paintCommon.AnnotationHelper;
import edu.usc.ksom.pm.panther.paintCommon.DBReference;
import edu.usc.ksom.pm.panther.paintCommon.Evidence;
import edu.usc.ksom.pm.panther.paintCommon.GOTerm;
import edu.usc.ksom.pm.panther.paintCommon.GOTermHelper;
import edu.usc.ksom.pm.panther.paintCommon.LabelValue;
import edu.usc.ksom.pm.panther.paintCommon.Node;
import edu.usc.ksom.pm.panther.paintCommon.NodeVariableInfo;
import edu.usc.ksom.pm.panther.paintCommon.Qualifier;
import edu.usc.ksom.pm.panther.paintCommon.QualifierDif;
import edu.usc.ksom.pm.panther.paintCommon.Rule;
import edu.usc.ksom.pm.panther.paintCommon.UAnnotation;
import edu.usc.ksom.pm.panther.paintCommon.UAnnotationHelper;
import edu.usc.ksom.pm.panther.paintCommon.WithEvidence;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import org.paint.config.Preferences;
import org.paint.datamodel.GeneNode;
import org.paint.go.GOConstants;
import org.paint.go.GO_Util;
import org.paint.gui.AnnotationTypeSelector;
import org.paint.gui.DirtyIndicator;
import org.paint.gui.PaintTable;
import org.paint.gui.event.AnnotationChangeEvent;
import org.paint.gui.event.EventManager;
import org.paint.main.PaintManager;
import org.paint.util.GeneNodeUtil;
import org.paint.util.RenderUtil;

public class AssociationTableModel extends AbstractTableModel implements PaintTable{
    
	protected static final String TERM_COL_NAME = "Term";
	protected static final String CODE_COL_NAME = "ECO";
	protected static final String REFERENCE_COL_NAME= "Reference";
	protected static final String WITH_COL_NAME = "With";
        protected static final String COL_NAME_QUALIFIER_NOT = "NOT";
        protected static final String COL_NAME_QUALIFIER_COLOCALIZES_WITH = "Colocalizes with";
        protected static final String COL_NAME_QUALIFIER_CONTRIBUTES_TO = "Contributes to";
        protected static final String COL_NAME_DELETE = "Delete";
        
        public static final String INFERRED_NOT = "inferred NOT";
        public static final String INFERRED = "inferred";
	//protected static final String TRASH_COL_NAME = "DEL";
        public static final String HEX_COLOR_FORMAT = "#%02X%02X%02X";

	protected static final String[] COLUMN_HEADINGS = {
		CODE_COL_NAME, 
		TERM_COL_NAME, 
		REFERENCE_COL_NAME,
		WITH_COL_NAME,
		COL_NAME_QUALIFIER_NOT,
                COL_NAME_QUALIFIER_COLOCALIZES_WITH,
                COL_NAME_QUALIFIER_CONTRIBUTES_TO,
                COL_NAME_DELETE
	};
        
        protected GeneNode gNode;
        
        // Display GO Annotations followed by UAnnotations
        protected ArrayList<Annotation> annotList;
        protected ArrayList<UAnnotation> uAnnotList;
        protected String aspect;
        protected GOTermHelper gth;
        protected PaintManager pm;
        protected AnnotationComparator ac = new AnnotationComparator();
        protected UniRuleComparator ur = new UniRuleComparator();
        
        
        public AssociationTableModel() {
            
        }
        
        public int getCountGOAnnot() {
            if (null != annotList) {
                return annotList.size();
            }
            return 0;
        }
        
        public int getCountURule() {
            if (null != uAnnotList) {
                return uAnnotList.size();
            }
            return 0;
        }
        
        public Annotation getAnnotation(int row) {
            if (null == annotList) {
                return null;
            }
            if (row >= 0 && row < annotList.size()) {
                return annotList.get(row);
            }
            return null;
        }
        
        public UAnnotation getUAnnotation(int row) {
            int actualRow = row - getCountGOAnnot();
            if (actualRow < 0) {
                return null;
            }
            if (actualRow >= 0 && actualRow < getRowCount()) {
                return uAnnotList.get(actualRow);
            }
            return null;
        }
        
        public boolean isGoAnnot(int row) {
            if (row >= 0 && row < getCountGOAnnot()) {
                return true;
            }
            return false;
        }
        
        public boolean isUniRuleAnnot(int row) {
            if (row >= getCountGOAnnot() && row < getRowCount()) {
                return true;
            }
            return false;
        }
        
    public void setNode(GeneNode gene) {
        gNode = gene;
        if (null == gene) {
            annotList = null;
            uAnnotList = null;
            return;
        }

        if (true == GeneNodeUtil.inPrunedBranch(gNode)) {
            annotList = null;
            uAnnotList = null;
            return;
        }
        pm = PaintManager.inst();
        gth = pm.goTermHelper();

        Node n = gNode.getNode();
        NodeVariableInfo nvi = n.getVariableInfo();
        if (null == nvi) {
            annotList = null;
        } else {
            ArrayList<Annotation> allAnnot = nvi.getGoAnnotationList();
            if (null == allAnnot) {
                annotList = null;
            } else {

                annotList = new ArrayList<Annotation>();
                annotList.addAll(allAnnot);
                Collections.sort(annotList, ac);
            }
        }
        // Unirule
        if (null == nvi) {
            uAnnotList = null;
            return;
        }

        // Create a new list and add items to it
        ArrayList<UAnnotation> allAnnots = nvi.getuAnnotationList();
        if (null == allAnnots) {
            uAnnotList = null;
            return;
        }
        uAnnotList = new ArrayList<UAnnotation>();
        uAnnotList.addAll(allAnnots);
        Collections.sort(uAnnotList, ur);
    }
         
        
    private class AnnotationComparator implements Comparator  {

        @Override
        public int compare(Object o1, Object o2) {
            String term1 = ((Annotation)o1).getGoTerm();
            String term2 = ((Annotation)o2).getGoTerm();
            if (null == term1 && null == term2) {
                return 0;
            }
            else if (null != term1 && null == term2) {
                return 1;
            }
            else if (null == term1 && null != term2) {
                return -1;
            }
            
            GOTerm a1 = AssociationTableModel.this.gth.getTerm(term1);
            GOTerm a2 = AssociationTableModel.this.gth.getTerm(term2);
            if (a1.equals(a2)) {
                return 0;
            }
            int comp = a1.getAspect().compareTo(a2.getAspect());
            if (0 == comp) {
                return a1.getAcc().compareTo(a2.getAcc());
            }
            return comp;
        }
    }
    
    private class UniRuleComparator implements Comparator {
        public int compare (Object o1, Object o2) {
            UAnnotation annot1 = (UAnnotation)o1;
            UAnnotation annot2 = (UAnnotation)o2;
            return annot1.getRule().getId().compareTo(annot2.getRule().getId());
        }
    }

    @Override
    public int getRowCount() {
        return getCountGOAnnot() + getCountURule();
    }
    
    public String getColumnName(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= COLUMN_HEADINGS.length) {
            System.out.println("Invalid column index requested");
            return null;
        }
        return COLUMN_HEADINGS[columnIndex];
    }
    
    public Class getColumnClass(int columnIndex) {
        String tag = COLUMN_HEADINGS[columnIndex];
        if (tag.equals(COL_NAME_QUALIFIER_NOT) || tag.equals(COL_NAME_QUALIFIER_COLOCALIZES_WITH) ||
            tag.equals(COL_NAME_QUALIFIER_CONTRIBUTES_TO)) {
            return Boolean.class;
        }
        if (tag.equals(COL_NAME_DELETE)) {
            return DeleteButtonRenderer.class;
        }
        return String.class;
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        boolean isGoAnnot = isGoAnnot(rowIndex);
        if (isGoAnnot) {            
            return false;
        }

        boolean isUniRuleAnnot = isUniRuleAnnot(rowIndex);
        // Cannot modify existing rules
        if (true == isUniRuleAnnot) {
            UAnnotation annot = getUAnnotation(rowIndex);
            Rule r = annot.getRule();
            if (null != r) {
                if (false == r.isCuratable()) {
                    return false;
                }
            }
        }
        
        if (columnIndex < 0 || columnIndex >= COLUMN_HEADINGS.length) {
            return false;
        }
        String tag = COLUMN_HEADINGS[columnIndex];
        // Non-editable columns
        if ((CODE_COL_NAME.equals(tag)) || (TERM_COL_NAME.equals(tag)) || (REFERENCE_COL_NAME.equals(tag)) || (WITH_COL_NAME.equals(tag))
                || (COL_NAME_QUALIFIER_CONTRIBUTES_TO.equals(tag)) || (COL_NAME_QUALIFIER_COLOCALIZES_WITH.equals(tag))) {
            return false;
        }
        Object cell = getValueAt(rowIndex, columnIndex);
        if (null == cell || false == cell instanceof Boolean) {
            return false;
        }
        if (true == isUniRuleAnnot) {
            UAnnotation annot = getUAnnotation(rowIndex);
            if (null == annot) {
                return false;
            }
            if (true == tag.equals(COL_NAME_QUALIFIER_NOT) && true == Evidence.CODE_IBA.equals(annot.getEvCode())) {
                return true;
            }
            if (true == tag.equals(COL_NAME_DELETE) && (true == Evidence.CODE_IBD.equals(annot.getEvCode()) || true == Evidence.CODE_UIC.equals(annot.getEvCode()) || true == Evidence.CODE_IRD.equals(annot.getEvCode()))) {
                return true;
            }
        }   
        return false;
    }

    @Override
    public int getColumnCount() {
        return COLUMN_HEADINGS.length;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        boolean isGoAnnot = isGoAnnot(rowIndex);
        if (true == isGoAnnot) {
            return getGOAnnotValueAt(rowIndex, columnIndex);
        }
        boolean isUniRuleAnnot = isUniRuleAnnot(rowIndex);
        if (true == isUniRuleAnnot) {
            return getUniRuleAnnotValueAt(rowIndex, columnIndex);
        }
        return null;
    }
        
    public Object getGOAnnotValueAt(int rowIndex, int columnIndex) {    
        if (null == annotList) {
            return null;
        }
        Annotation a = annotList.get(rowIndex);
	String tag = COLUMN_HEADINGS[columnIndex];

        HashSet<String> actualSet = a.getEvidenceCodeSet();
        HashSet<String> displayEvSet = new HashSet<String>();
//        boolean isPaint = false;
        for (String code: actualSet) {

            displayEvSet.add(code);

        }
        String evidenceCode = StringUtils.listToString(displayEvSet, Constant.STR_EMPTY, Constant.STR_COMMA);
        
        if (CODE_COL_NAME.equals(tag)) {
            return evidenceCode;
        }
        if (TERM_COL_NAME.equals(tag)) {
            GOTermHelper gth = PaintManager.inst().goTermHelper();
            GOTerm term = gth.getTerm(a.getGoTerm());
            
            NodeInfoForMatrix nifm = new NodeInfoForMatrix(gNode, term, gth);
            String inferred = null;
            boolean containsNeg = QualifierDif.containsNegative(gth.getValidQualifiersForTerm(term, a.getQualifierSet()));
            if (false == containsNeg && (true == nifm.isExpNot() || true == nifm.isNonExpNot())) {
                inferred = INFERRED_NOT;
            }
            else if (true == containsNeg && (false == nifm.isExpNot() || false == nifm.isNonExpNot())) {
                inferred = INFERRED;
            }
            
            
            Color aspectColor = RenderUtil.getAspectColor(AnnotationTypeSelector.LETTER_TO_ASPECT.get(term.getAspect()));

            String hexColor = String.format(HEX_COLOR_FORMAT, aspectColor.getRed(), aspectColor.getGreen(), aspectColor.getBlue());
//            return term.getName() + "(" + term.getAcc() + ")";
            
            String style = "<head><style> " + 
                            " body { " + 
                            " background-color: " + hexColor + " ; " + 
                            "} " + 
                            "</style> </head>" ;
            
            if (QualifierDif.containsNegative(a.getQualifierSet())) {
                return "<html>" + style + "<body><s>" + term.getName() + "</s> (<a href=\"" + AssociationTable.URL_LINK_PREFIX_AMIGO + term.getAcc() + "\" >" + term.getAcc() + "</a>)</body></html>";
            }
            else if (true == INFERRED_NOT.equals(inferred)) {
                return "<html>" + style + "<body><s>" + term.getName() + "</s> (<a href=\"" + AssociationTable.URL_LINK_PREFIX_AMIGO + term.getAcc() + "\" >" + term.getAcc() + "</a>) " + inferred + " </body></html>";                
            }
            else if (true == INFERRED.equals(inferred)) {
                return "<html>" + style + "<body>" + term.getName() + " (<a href=\"" + AssociationTable.URL_LINK_PREFIX_AMIGO + term.getAcc() + "\" >" + term.getAcc() + "</a>) " + inferred + " </body></html>";
                
            }
            else {
                return "<html>" + style + "<body>" + term.getName() + " (<a href=\"" + AssociationTable.URL_LINK_PREFIX_AMIGO + term.getAcc() + "\" >" + term.getAcc() + "</a>)</body></html>";
            }
//            String rtn =  "<html>" + term.getName() + " (<a href=\"http://amigo.geneontology.org/amigo/term/" + term.getAcc() + "\" >" + term.getAcc() + "</a>)</html>";
//            System.out.println(rtn);
//            return rtn;
        }
        if (REFERENCE_COL_NAME.equals(tag)) {
            if (true == a.isExperimental()) {
                // Experimental and non-paint annotations get "with" information in reference column
                return getTextForWith(a);
            }

            HashSet<WithEvidence> withSet = a.getAnnotationDetail().getWithEvidenceSet();
            if (null == withSet || 0 == withSet.size()) {
                return Constant.STR_EMPTY;
            }
            StringBuffer sb = new StringBuffer();            
            HashMap<String, String> lookup = new HashMap<String, String>();
            for (WithEvidence we: withSet) {
                String type = we.getEvidenceType();
                String value = null;
                if (true == we.isPAINTType()) {
                    type = GOConstants.PAINT_REF;
                    value = GO_Util.inst().getPaintEvidenceAcc();
                }
                else {
                    continue;
                }
                String key = type + value;
                if (true == lookup.containsKey(key)) {
                    continue;
                }
                lookup.put(key, key);
                sb.append(type);
                sb.append(Constant.STR_COLON);
                sb.append(value);
                sb.append(Constant.STR_SPACE);
            }
            return sb.toString().trim();
        }
        if (WITH_COL_NAME.equals(tag)) {
            if (a.isExperimental()) {
                return Constant.STR_EMPTY;
            }
            return getTextForWith(a);
        }
        if (COL_NAME_QUALIFIER_NOT.equals(tag)) {
            // Cannot update TCV
            if (Evidence.CODE_TCV.equals(a.getSingleEvidenceCodeFromSet())) {
                return false;
            }            
            
            // Cannot change not for non sequence annotations
//            Evidence e = a.getEvidence();
//            if (false == GOConstants.DESCENDANT_SEQUENCES_EC.equals(e.getEvidenceCode())) {
//                return null;
//            }
            HashSet<Qualifier> qualifierSet = a.getQualifierSet();
            qualifierSet = gth.getValidQualifiersForTerm(gth.getTerm(a.getGoTerm()), qualifierSet);
            if (null == qualifierSet) {
                return Boolean.FALSE;
            }
            for (Iterator<Qualifier> iter = qualifierSet.iterator(); iter.hasNext();) {
                Qualifier q = iter.next();
                if (true == q.isNot()) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }
        if (COL_NAME_QUALIFIER_COLOCALIZES_WITH.equals(tag)) {
            HashSet<Qualifier> qualifierSet = a.getQualifierSet();
            GOTerm term = gth.getTerm(a.getGoTerm());
            if (false == gth.canTermHaveQualifier(term, Qualifier.QUALIFIER_COLOCALIZES_WITH)) {
                return null;
            }
            qualifierSet = gth.getValidQualifiersForTerm(term, qualifierSet);            
            if (null == qualifierSet) {
                return Boolean.FALSE;
            }
            for (Iterator<Qualifier> iter = qualifierSet.iterator(); iter.hasNext();) {
                Qualifier q = iter.next();
                if (true == q.isColocalizesWith()) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }
        if (COL_NAME_QUALIFIER_CONTRIBUTES_TO.equals(tag)) {
            HashSet<Qualifier> qualifierSet = a.getQualifierSet();
            GOTerm term = gth.getTerm(a.getGoTerm());
            if (false == gth.canTermHaveQualifier(term, Qualifier.QUALIFIER_CONTRIBUTES_TO)) {
                return null;
            }            
            qualifierSet = gth.getValidQualifiersForTerm(gth.getTerm(a.getGoTerm()), qualifierSet);            
            if (null == qualifierSet) {
                return Boolean.FALSE;
            }
            for (Iterator<Qualifier> iter = qualifierSet.iterator(); iter.hasNext();) {
                Qualifier q = iter.next();
                if (true == q.isContributesTo()) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }
        if (COL_NAME_DELETE.equals(tag)) {
            if (true == Evidence.CODE_TCV.equals(a.getSingleEvidenceCodeFromSet())) {
                return null;
            }
            if (true == AnnotationHelper.isDirectAnnotation(a)) {
                return Boolean.TRUE;
            }
            return null;
        }
        
        return Constant.STR_EMPTY;        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Object getUniRuleAnnotValueAt(int rowIndex, int columnIndex) {
        UAnnotation annot = getUAnnotation(rowIndex);
        if (null == annot) {
            return null;
        }
        
        String tag = COLUMN_HEADINGS[columnIndex];
        if (CODE_COL_NAME.equals(tag)) {
            return annot.getEvCode();
        }
        if (TERM_COL_NAME.equals(tag)) {
            Rule r = annot.getRule();
            LabelValue lv = r.getLabelValue();
            String id = r.getId();
            if (null == id) {
                return id;
            }
            else if (true == id.startsWith(Rule.UNIRULE_PREFIX_SWISS) ||
                true == id.startsWith(Rule.UNIRULE_PREFIX_NEW) ||
                true == id.startsWith(Rule.UNIRULE_PREFIX_NEWE)) {
                return id;
            }
            else if (id.startsWith(Rule.UNIRULE_PREFIX)) {
                StringBuilder sb = new StringBuilder();
                sb.append(lv.getValue());

                if (0 != sb.length()) {
                    sb.insert(0, Constant.STR_SPACE + Constant.STR_BRACKET_ROUND_OPEN);
                    sb.append(Constant.STR_BRACKET_ROUND_CLOSE);
                }
                Color aspectColor = RenderUtil.getAspectColor(AnnotationTypeSelector.LETTER_TO_ASPECT.get(AnnotationTypeSelector.aspects.get(AnnotationTypeSelector.AnnotationType.UNIRULE.toString())));
                String hexColor = String.format(HEX_COLOR_FORMAT, aspectColor.getRed(), aspectColor.getGreen(), aspectColor.getBlue());
                String style = "<head><style> "
                        + " body { "
                        + " background-color: " + hexColor + " ; "
                        + "} "
                        + "</style> </head>";
                return "<html>" + style + "<body>" + "<a href=\"" + Preferences.inst().getUriprotURLPrefix() + AssociationTable.URL_SUFFIX_UNIRULE + id + "\" >" + id + "</a>" + sb.toString() + "</body></html>";
            }
        }
        if (REFERENCE_COL_NAME.equals(tag)) {
            return Constant.STR_EMPTY;
        }
        if (WITH_COL_NAME.equals(tag)) {
            ArrayList<UAnnotation> annotList = annot.getWithAnnotList();
            if (null == annotList || 0 == annotList.size()) {
                return Constant.STR_EMPTY;
            }
            HashSet<Node> nodeSet = new HashSet<Node>();
            for (UAnnotation with: annotList) {
                if (annot == with) {
                    continue;
                }
                nodeSet.add(with.getAnnotatedNode());
            }
            StringBuffer sb = new StringBuffer();
            for (Node node: nodeSet) {
                sb.append(getNodeHttpLink(pm.getGeneByPTNId(node.getStaticInfo().getPublicId())));
                sb.append(Constant.STR_SPACE);
            }
            return "<html><body>" + sb.toString().trim() + "</body></html>";
        }
        if (COL_NAME_QUALIFIER_NOT.equals(tag)) {
            return QualifierDif.containsNegative(annot.getQualifierSet());
        }
        if (COL_NAME_QUALIFIER_COLOCALIZES_WITH.equals(tag)) {
            return false;
        }        
        if (COL_NAME_QUALIFIER_CONTRIBUTES_TO.equals(tag)) {
            return false;
        } 
        if (COL_NAME_DELETE.equals(tag)) {
            if (Evidence.CODE_IBD.equals(annot.getEvCode()) || Evidence.CODE_UIC.equals(annot.getEvCode()) || Evidence.CODE_IRD.equals(annot.getEvCode())) {
                return Boolean.TRUE;
            }
            return null;
        }        
        return Constant.STR_EMPTY;
    }
    
    public Vector<WithEvidence> sortWithEvidence(HashSet<WithEvidence> origList) {
        if (null == origList) {
            return null;
        }
        Vector<WithEvidence> rtnList = new Vector<WithEvidence>(origList.size());
        for (WithEvidence withEv: origList) {
            String code = withEv.getEvidenceCode();
            if (false == Evidence.isExperimental(code) && false == Evidence.isPaint(code)) {
                rtnList.add(withEv);
                continue;
            }
            DBReference withDBRef = (DBReference) withEv.getWith();
            if (DBReference.TYPE_PMID.equals(withDBRef.getEvidenceType())) {
                rtnList.insertElementAt(withEv, 0);
            }
        }
        return rtnList;
    }
    
    public String getTextForWith(Annotation a) {
        AnnotationDetail ad = a.getAnnotationDetail();
        HashSet<WithEvidence> withEvSet = ad.getWithEvidenceDBRefSet();
        StringBuffer sb = new StringBuffer();        
        if (null != withEvSet) {
            Vector<WithEvidence> withDBRefSorted = sortWithEvidence(withEvSet);
            for (WithEvidence withEv : withDBRefSorted) {
                // Only display experimental and PAINT codes
                String code = withEv.getEvidenceCode();
                if (false == Evidence.isExperimental(code) && false == Evidence.isPaint(code)) {
                    continue;
                }
                DBReference withDBRef = (DBReference) withEv.getWith();
                sb.append(getDBValueFormatPMID(withDBRef).toString());
            }
        }
        LinkedHashSet<Node> addedList = new LinkedHashSet<Node>();
        withEvSet = ad.getWithEvidenceAnnotSet();
        if (null != withEvSet) {
            for (WithEvidence withEv : withEvSet) {
                Annotation withAnnot = (Annotation) withEv.getWith();
                Node n = withAnnot.getAnnotationDetail().getAnnotatedNode();
//                if (null == n) {
//                    System.out.println("Here");
//                }
                GeneNode gn = PaintManager.inst().getGeneByPTNId(n.getStaticInfo().getPublicId());                
                if (((Evidence.CODE_IKR.equals(withAnnot.getSingleEvidenceCodeFromSet())) || Evidence.CODE_IRD.equals(a.getSingleEvidenceCodeFromSet()) || Evidence.CODE_TCV.equals(a.getSingleEvidenceCodeFromSet())) && withAnnot == a) {
                    continue;
                }
                addedList.add(withAnnot.getAnnotationDetail().getAnnotatedNode());
            }
        }

        HashSet<Node> nodeSet = ad.getWithNodeSet();
        if (null != nodeSet) {
            for (Node n : nodeSet) {
                addedList.add(n);
            }
        }

        for (Node node : addedList) {
                //sb.append(pm.getGeneByPTNId(node.getStaticInfo().getPublicId()).getNodeLabel() + node.getStaticInfo().getPublicId());

            sb.append(getNodeHttpLink(pm.getGeneByPTNId(node.getStaticInfo().getPublicId())));
            sb.append(Constant.STR_SPACE);
        }

        return "<html><body>" + sb.toString().trim() + "</body></html>";
    }

    public ArrayList<String> getLinksForReferenceCol(Annotation a) {
        HashSet<String> actualSet = a.getEvidenceCodeSet();
        HashSet<String> displayEvSet = new HashSet<String>();
        boolean isExperimental = false;
        boolean isPaint = false;
        for (String code : actualSet) {
            if (Evidence.isExperimental(code)) {
                displayEvSet.add(code);
                isExperimental = true;
                continue;
            } else if (Evidence.isPaint(code)) {
                displayEvSet.add(code);
                isPaint = true;
            }
        }
        if (true == isExperimental && false == isPaint) {
            // Experimental and non-paint annotations get "with" information in reference column
            return getLinksForAnnot(a);
        }
        return null;
    }

    public ArrayList<String> getLinksForWithCol(UAnnotation a) {
        ArrayList<UAnnotation> withs = a.getWithAnnotList();
        if (null == withs) {
            return null;
        }
        ArrayList<String> rtnList = new ArrayList<String>(withs.size());
        for (UAnnotation with : withs) {

            rtnList.add(AssociationTable.URL_LINK_PREFIX_PANTREE_NODE + with.getAnnotatedNode().getStaticInfo().getPublicId());
        }
        return rtnList;
    }
    
    public ArrayList<String> getLinksForWithCol(Annotation a) {
        return getLinksForAnnot(a);
    }
    
    public ArrayList<String> getLinksForAnnot(Annotation a) {
        ArrayList<String> rtnList = new ArrayList<String>();
        AnnotationDetail ad = a.getAnnotationDetail();

        LinkedHashSet<Node> addedList = new LinkedHashSet<Node>();
        HashSet<WithEvidence> withEvSet = ad.getWithEvidenceAnnotSet();
        if (null != withEvSet) {
            for (WithEvidence withEv : withEvSet) {
                Annotation withAnnot = (Annotation) withEv.getWith();
                Node n = withAnnot.getAnnotationDetail().getAnnotatedNode();
                GeneNode gn = PaintManager.inst().getGeneByPTNId(n.getStaticInfo().getPublicId());
                if (((Evidence.CODE_IKR.equals(withAnnot.getSingleEvidenceCodeFromSet()) && false == gn.isLeaf()) || Evidence.CODE_IRD.equals(a.getSingleEvidenceCodeFromSet()) || Evidence.CODE_TCV.equals(a.getSingleEvidenceCodeFromSet())) && withAnnot == a) {
                    continue;
                }
                addedList.add(withAnnot.getAnnotationDetail().getAnnotatedNode());
            }
        }

        HashSet<Node> nodeSet = ad.getWithNodeSet();
        if (null != nodeSet) {
            for (Node n : nodeSet) {
                addedList.add(n);
            }
        }

        for (Node node : addedList) {
            rtnList.add(AssociationTable.URL_LINK_PREFIX_PANTREE_NODE + node.getStaticInfo().getPublicId());
        }
        HashSet<DBReference> dbSet = ad.getWithOtherSet();
        if (null != dbSet) {
            for (DBReference dbRef : dbSet) {
                if (DBReference.TYPE_PMID.equals(dbRef.getEvidenceType())) {
                    rtnList.add(AssociationTable.URL_LINK_PREFIX_PMID + dbRef.getEvidenceValue());
                }
            }
        }
        if (rtnList.isEmpty()) {
            return null;
        }
        return rtnList;
    }
    

    @Override
    public String getTextAt(int row, int column) {
        if (null == annotList) {
            return null;
        }
        Annotation a = annotList.get(row);
	String tag = COLUMN_HEADINGS[column];
        if (CODE_COL_NAME.equals(tag)) {
            HashSet<String> evidenceCodeSet = a.getEvidenceCodeSet();
            return StringUtils.listToString(evidenceCodeSet, Constant.STR_EMPTY, Constant.STR_COMMA);
        }
        if (TERM_COL_NAME.equals(tag)) {
            GOTerm term = PaintManager.inst().goTermHelper().getTerm(a.getGoTerm());
            return term.getName() + "(" + term.getAcc() + ")";
        }
        if (REFERENCE_COL_NAME.equals(tag)) {
            HashSet<WithEvidence> withSet = a.getAnnotationDetail().getWithEvidenceSet();
            if (null == withSet || 0 == withSet.size()) {
                return Constant.STR_EMPTY;
            }
            StringBuffer sb = new StringBuffer();            
            HashMap<String, String> lookup = new HashMap<String, String>();
            for (WithEvidence we: withSet) {
                String type = we.getEvidenceType();
                String value = we.getEvidenceId();
                if (true == we.isPAINTType()) {
                    type = GOConstants.PAINT_REF;
                    value = GO_Util.inst().getPaintEvidenceAcc();
                }
                String key = type + value;
                if (true == lookup.containsKey(key)) {
                    continue;
                }
                lookup.put(key, key);
                sb.append(type);
                sb.append(Constant.STR_COLON);
                sb.append(value);
                sb.append(Constant.STR_SPACE);
            }
            return sb.toString().trim();
        }
        if (WITH_COL_NAME.equals(tag)) {
            AnnotationDetail ad = a.getAnnotationDetail();
            LinkedHashSet<Node> addedList = new LinkedHashSet<Node>();
            HashSet<Annotation> withs = ad.getWithAnnotSet();
            if (null != withs) {
                for (Annotation with: withs) {
                    Node n = with.getAnnotationDetail().getAnnotatedNode();
                    GeneNode gn = PaintManager.inst().getGeneByPTNId(n.getStaticInfo().getPublicId());
                    if (((Evidence.CODE_IKR.equals(a.getSingleEvidenceCodeFromSet()) && false == gn.isLeaf()) || Evidence.CODE_IRD.equals(a.getSingleEvidenceCodeFromSet())) && with == a) {
                        continue;
                    }
                    addedList.add(with.getAnnotationDetail().getAnnotatedNode());
                }
            }
            
            HashSet<Node> nodeSet = ad.getWithNodeSet();
            if (null != nodeSet) {
                for (Node n: nodeSet) {
                    addedList.add(n);
                }
            }
            StringBuffer sb = new StringBuffer();
            
            for (Node node: addedList) {
                sb.append(pm.getGeneByPTNId(node.getStaticInfo().getPublicId()).getNodeLabel()+ node.getStaticInfo().getPublicId());
                sb.append(Constant.STR_SPACE);
            }
            HashSet<DBReference> dbSet = ad.getWithOtherSet();
            if (null != dbSet) {
                for (DBReference dbref: dbSet) {
                    sb.append(getDBValue(dbref).toString());
                }
            }
            return sb.toString().trim();
        }
        return Constant.STR_EMPTY;

        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private StringBuffer getDBValue(DBReference dbRef) {
        StringBuffer sb = new StringBuffer();
        if (GOConstants.PANTHER_DB.equals(dbRef.getEvidenceType())) {
            String value = dbRef.getEvidenceValue();
            GeneNode gn = PaintManager.inst().getGeneByPTNId(value);
            if (null != gn && gn.isLeaf()) {
                sb.append(gn.getNodeLabel());
                sb.append(Constant.STR_SPACE);                
                return sb;
            }
        }

        sb.append(dbRef.getEvidenceType());
        sb.append(Constant.STR_COLON);
        sb.append(dbRef.getEvidenceValue());
        sb.append(Constant.STR_SPACE);

        return sb;
    }
    
 
    private StringBuffer getNodeHttpLink(GeneNode gNode) {
        if (null == gNode) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        String nodeLabel = gNode.getNodeLabelWithPTN();
        if (null == nodeLabel || 0 == nodeLabel.trim().length()) {
            nodeLabel = gNode.getNode().getStaticInfo().getPublicId();
        }
        sb.append("<a href=\"" + AssociationTable.URL_LINK_PREFIX_PANTREE_NODE + gNode.getNode().getStaticInfo().getPublicId() + "\">" + nodeLabel + "</a>");
        sb.append(Constant.STR_SPACE);
        return sb;
    }
    
    private StringBuffer getDBValueFormatPMID(DBReference dbRef) {
        StringBuffer sb = new StringBuffer();
        String evidenceType = dbRef.getEvidenceType();
        if (GOConstants.PANTHER_DB.equals(evidenceType)) {
            System.out.println("Dont expect to get here anymore");
            String value = dbRef.getEvidenceValue();
            GeneNode gn = PaintManager.inst().getGeneByPTNId(value);
            if (null != gn && gn.isLeaf()) {
                sb.append(gn.getNodeLabel());
                sb.append(Constant.STR_SPACE);                
                return sb;
            }
        }
        if (DBReference.TYPE_PMID.equals(evidenceType)) {
            sb.append("<a href=\"" + AssociationTable.URL_LINK_PREFIX_PMID + dbRef.getEvidenceValue() + "\"> PMID:" + dbRef.getEvidenceValue() + "</a>");
            sb.append(Constant.STR_SPACE);
            return sb;
        }

        sb.append(evidenceType);
        sb.append(Constant.STR_COLON);
        sb.append(dbRef.getEvidenceValue());
        sb.append(Constant.STR_SPACE);

        return sb;
    }    

    @Override
    public boolean isSquare(int column) {
        String tag = getColumnName(column);
        if (tag.equals(COL_NAME_QUALIFIER_NOT) || tag.equals(COL_NAME_QUALIFIER_COLOCALIZES_WITH) || 
            tag.equals(COL_NAME_QUALIFIER_CONTRIBUTES_TO) || tag.equals(COL_NAME_DELETE)) {
            return true;
        }
        return false;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void deleteRow(int rowIndex) {
        if (true == isGoAnnot(rowIndex)) {
            if (null == annotList || rowIndex > annotList.size() || rowIndex < 0) {
                return;
            }
            Annotation a = annotList.get(rowIndex);
            AnnotationHelper.deleteAnnotationAndRepropagate(a, pm.getTaxonHelper(), pm.goTermHelper());
            DirtyIndicator.inst().setAnnotated(true);
            EventManager.inst().fireAnnotationChangeEvent(new AnnotationChangeEvent(gNode));
            return;
        }
        UAnnotation a = getUAnnotation(rowIndex);
        UAnnotationHelper.deleteAnnotationAndRepropagate(a);
        DirtyIndicator.inst().setAnnotated(true);
        EventManager.inst().fireAnnotationChangeEvent(new AnnotationChangeEvent(gNode));      
    }
    
    public void notAnnotation(int rowIndex) {
        if (true == isGoAnnot(rowIndex)) {
            System.out.println("Not permitted to annotate GO terms");
            return;
//            if (null == annotList || rowIndex > annotList.size() || rowIndex < 0) {
//                return;
//            }
//            Annotation a = annotList.get(rowIndex);
//            PaintAction.inst().notAnnotation(gNode, a);
//            return;
        }
        // UniRule.  Remove propagated IBA's and other dependant annotations, then add IRD
        UAnnotation a = getUAnnotation(rowIndex);
        boolean success = UAnnotationHelper.addIRD(a);
        if (false == success) {
            return;
        }
        DirtyIndicator.inst().setAnnotated(true);
        EventManager.inst().fireAnnotationChangeEvent(new AnnotationChangeEvent(gNode));
    }
    
}
