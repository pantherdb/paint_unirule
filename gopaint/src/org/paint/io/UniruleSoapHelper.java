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
import com.sri.panther.paintCommon.Book;
import com.sri.panther.paintCommon.Constant;
import com.sri.panther.paintCommon.User;
import com.sri.panther.paintCommon.util.Utils;
import edu.usc.ksom.pm.panther.paintCommon.LabelValue;
import edu.usc.ksom.pm.panther.paintCommon.Node;
import edu.usc.ksom.pm.panther.paintCommon.Rule;
import edu.usc.ksom.pm.panther.paintCommon.UniRuleAnnotationGroup;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import javax.swing.JOptionPane;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLStreamWriter;
import org.bbop.framework.GUIManager;
import org.paint.config.Preferences;
import org.paint.dataadapter.AnnotationAdapter;
import org.paint.datamodel.Family;
import org.paint.datamodel.GeneNode;
import org.paint.gui.DirtyIndicator;
import org.paint.main.PaintManager;
import org.uniprot.security.CredentialsProvider;
import org.uniprot.security.SecurityHeaderAppender;
import org.uniprot.transformations.CatalyticActivityTransformer;
import org.uniprot.transformations.CofactorTransformer;
import org.uniprot.uniprot.CommentType;
import org.uniprot.uniprot.EvidencedStringType;
import org.uniprot.uniprot.KeywordType;
import org.uniprot.unirule.xml.UniRuleXmlWriter;
import org.uniprot.unirule_1.*;
import org.uniprot.unirule_1.MainType.ConditionSets;
import org.uniprot.unirule_import.UniRuleException;
import org.uniprot.unirule_import.UniRuleImporterService;
import org.uniprot.unirule_import.UniRuleImporterService_Service;
import org.uniprot.unirule_import.schema.PantherRuleSummary;
import org.uniprot.transformations.KeywordTransformer;
import org.uniprot.transformations.SubcellularLocationTransformer;
import org.uniprot.uniprot.DbReferenceType;
import org.uniprot.uniprot.GeneNameType;
import org.uniprot.uniprot.GeneType;


public class UniruleSoapHelper {
    //public static final String URL_SOAP_UNIRULE = "https://wwwdev.ebi.ac.uk/uniprot/unirule/import/import?wsdl";
    private static final java.text.SimpleDateFormat DATE_FORMATTER = new java.text.SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
    public static final String ID_UNIRULE = "UR000000000";
    
    //public static final String CONDITION_TYPE_RULE_FOR_BOOK = "PANTHER";
    public static final String CONDITION_TYPE_PANTHER_TREE_NODE = "PANTHER tree node";    
    public static final String LABEL_CONDITION_MAIN_TYPE = "PANTHER id";
    public static final String LABEL_CONDITION_TREE_NODE = "PANTHER tree node";
    public static final String LABEL_COMMENT_FUNCTION = "function";
    public static final int SIZE_USER_INFO = 2;
    
    public static final String STATUS_APPLY = "apply";    
    public static final String STATUS_TEST = "test";
    public static final String STATUS_DISUSED = "disused";
    
    public static final String MSG_ERR_UNABLE_TO_LOCK_BOOK  = "Unable to lock ";
    public static final String MSG_ERR_UNABLE_TO_UNLOCK_BOOK  = "Unable to unlock ";
    public static final String MSG_LOCKING_UNLOCKING_OPERATION_FAILED = "Locking/Unlocking operation failed";
    public static final String MSG_ERR_LOGIN_BOOK_NOT_FOUND = "Login name or book is not defined";
    public static final String MSG_ERR_LOGIN_NOT_FOUND = "Login name is not defined";    
    public static final String MSG_ERR_USER_ROLE_NO_PRIVILEGE_TO_SAVE = "User role does not have privilege to save";    
    public static final String MSG_ERR_USER_NO_PRIVILEGE_TO_SAVE = "User does not have privilege to save";
    public static final String MSG_ERR_BOOK_NOT_LOCKED_FOR_USER = "Book not locked for user"; 
    public static final String MSG_ERR_UNABLE_TO_SAVE_TO_SERVER = "Unable to save to server";
    
    
    public static final String DB_REF_TYPE_GO = "GO";
    
    public static final String GENE_NAME_TYPE_PRIMARY = "primary";
    public static final String GENE_NAME_TYPE_SYNONYM = "synonym";
    
    public static final String COMMENT_TYPE_FUNCTION = "function";
    public static final String COMMENT_TYPE_SIMILARITY = "similarity";
    public static final String COMMENT_TYPE_SUBUNIT = "subunit";
    public static final String COMMENT_TYPE_PATHWAY = "pathway";
    public static final String COMMENT_TYPE_MISCELLANEOUS = "miscellaneous";
    public static final String COMMENT_TYPE_CAUTION = "caution"; 
    public static final String COMMENT_TYPE_DOMAIN = "domain";
    public static final String COMMENT_TYPE_ACTIVITY_REGULATION = "activity regulation";
    public static final String COMMENT_TYPE_INDUCTION = "induction";    
    public static final String COMMENT_TYPE_PTM = "PTM"; 
    public static final String COMMENT_TYPE_CATALYTIC_ACTIVITY = "catalytic activity"; 
    public static final String COMMENT_TYPE_SUBCELLULAR_LOCATION = "subcellular location"; 
    public static final String COMMENT_TYPE_COFACTOR = "cofactor";
    
    public static final HashMap<String, String> COMMENT_RULE_LABEL_LOOKUP = getCommentLabelLookup();
    public static final HashMap<String, String> COMMENT_LABEL_RULE_LOOKUP = getLabelToCommentLookup();
    
    private static HashMap<String, String> getCommentLabelLookup() {
        HashMap<String, String> lookup = new HashMap<String, String>();
        lookup.put(COMMENT_TYPE_FUNCTION, Rule.PROPERTY_CCFU);
        lookup.put(COMMENT_TYPE_SIMILARITY, Rule.PROPERTY_CCSI);
        lookup.put(COMMENT_TYPE_SUBUNIT, Rule.PROPERTY_CCSU);
        lookup.put(COMMENT_TYPE_PATHWAY, Rule.PROPERTY_CCPA);
        lookup.put(COMMENT_TYPE_MISCELLANEOUS, Rule.PROPERTY_CCCC);
        lookup.put(COMMENT_TYPE_CAUTION, Rule.PROPERTY_CCCT);   
        lookup.put(COMMENT_TYPE_DOMAIN, Rule.PROPERTY_CCDO);
        lookup.put(COMMENT_TYPE_ACTIVITY_REGULATION, Rule.PROPERTY_CCER);
        lookup.put(COMMENT_TYPE_INDUCTION, Rule.PROPERTY_CCIN);
        lookup.put(COMMENT_TYPE_PTM, Rule.PROPERTY_CCPT);
        lookup.put(COMMENT_TYPE_CATALYTIC_ACTIVITY, Rule.PROPERTY_CCCA);
        lookup.put(COMMENT_TYPE_SUBCELLULAR_LOCATION, Rule.PROPERTY_CCLO);
        lookup.put(COMMENT_TYPE_COFACTOR, Rule.PROPERTY_CCCO);
        return lookup;
    }
    
    private static HashMap<String, String> getLabelToCommentLookup() {
        HashMap<String, String> lookup = new HashMap<String, String>();
        lookup.put(Rule.PROPERTY_CCFU, COMMENT_TYPE_FUNCTION);
        lookup.put(Rule.PROPERTY_CCSI, COMMENT_TYPE_SIMILARITY);
        lookup.put(Rule.PROPERTY_CCSU, COMMENT_TYPE_SUBUNIT);
        lookup.put(Rule.PROPERTY_CCPA, COMMENT_TYPE_PATHWAY);
        lookup.put(Rule.PROPERTY_CCCC, COMMENT_TYPE_MISCELLANEOUS);
        lookup.put(Rule.PROPERTY_CCCT, COMMENT_TYPE_CAUTION);   
        lookup.put(Rule.PROPERTY_CCDO, COMMENT_TYPE_DOMAIN);
        lookup.put(Rule.PROPERTY_CCER, COMMENT_TYPE_ACTIVITY_REGULATION);
        lookup.put(Rule.PROPERTY_CCIN, COMMENT_TYPE_INDUCTION);
        lookup.put(Rule.PROPERTY_CCPT, COMMENT_TYPE_PTM);
        lookup.put(Rule.PROPERTY_CCCA, COMMENT_TYPE_CATALYTIC_ACTIVITY);
        lookup.put(Rule.PROPERTY_CCLO, COMMENT_TYPE_SUBCELLULAR_LOCATION);
        lookup.put(Rule.PROPERTY_CCCO, COMMENT_TYPE_COFACTOR);
        return lookup;
    }
    
    public static User userValid(ArrayList loginInfo) {
        if (null == loginInfo || SIZE_USER_INFO != loginInfo.size()) {
            return null;
        }
        UniRuleImporterService_Service service = null;
        String userId = (String)loginInfo.get(0);
        String password = String.copyValueOf((char[]) loginInfo.get(1));
        try {
            service = new UniRuleImporterService_Service(new URL(Preferences.inst().getUniprotURL()));
            CredentialsProvider credentialsProvider = new CredentialsProvider(userId, password);
            SecurityHeaderAppender securityHeaderAppender = new SecurityHeaderAppender();
            securityHeaderAppender.addSecurityHeader(service, credentialsProvider);
            UniRuleImporterService uniruleImporter = service.getUniRuleImporter();
            
            // Retrieve role and ensure it is one of the ones we expect, else set to null
            String role = uniruleImporter.retrieveUserRole(userId);
//            role = User.ROLE_ADMIN;
            if (null != role) {
                role = role.toLowerCase();
                if (false == Utils.search(User.VALID_ROLES, role)) {
                    role = User.ROLE_UNKNOWN;
                }
            }
            //  USER_PRIVILEGE_NOT_SET 
            User u = new User(null, null, null, userId, Constant.USER_PRIVILEGE_NOT_SET, null);
            u.setRole(role);
            return u;
        } catch (MalformedURLException me) {
            me.printStackTrace();
        } catch (UniRuleException ure) {
            ure.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;       
    }
    

    public static boolean save(RuleStatusType status, StringBuffer errMsg) {
        PaintManager pm = PaintManager.inst();
        Family family = pm.getFamily();
        String famId = family.getFamilyID();
        ArrayList<UniRuleAnnotationGroup> uagList = AnnotationAdapter.groupUAnnotations();
        
        errMsg.setLength(0);
        UniRuleImporterService_Service service = null;
        //String userId = "paint";
        ArrayList<Object> info = pm.getUserInfo();
        if (null == info || 2 != info.size()) {
            errMsg.append(MSG_ERR_LOGIN_NOT_FOUND);
            return false;
        }

//        if (1 == 1) {
//            return true;
//        }
        
        try {
            service = new UniRuleImporterService_Service(new URL(Preferences.inst().getUniprotURL()));
            CredentialsProvider credentialsProvider = new CredentialsProvider((String)info.get(0), String.copyValueOf((char[]) info.get(1)));
            SecurityHeaderAppender securityHeaderAppender = new SecurityHeaderAppender();
            securityHeaderAppender.addSecurityHeader(service, credentialsProvider);
        } catch (MalformedURLException me) {
            me.printStackTrace();
        }

        try {           
            UniRuleImporterService uniruleImporter = service.getUniRuleImporter();

            UniRuleType rule = getRule(famId, (String)info.get(0), uagList, family.getUniRuleCommentExternal(), family.getUniRuleCommentInternal(), status, errMsg);
            // Testing
//            System.out.println("Testing! attempt to interpret the rule");            
//            getPAINTRulesForRule(rule);
            uniruleImporter.importNewOrOverwritePantherRule(rule, famId);
            JOptionPane.showMessageDialog(GUIManager.getManager().getFrame(), "Book has been saved", "Save Successful", JOptionPane.INFORMATION_MESSAGE);
            DirtyIndicator.inst().setAnnotated(false);
            return true;
        } catch (UniRuleException e) {
            e.printStackTrace();
        } catch (DatatypeConfigurationException dce) {
            dce.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        errMsg.append(MSG_ERR_UNABLE_TO_SAVE_TO_SERVER);
        return false;
    }
    
//    public static ArrayList<UniRuleType> getRules(String famId, String userId, ArrayList<ArrayList<UAnnotation>> groupList) throws Exception, DatatypeConfigurationException {
//        ArrayList<UniRuleType> rules = new ArrayList<UniRuleType>();    
//        for (ArrayList<UAnnotation> group: groupList) {
//            HashSet<UAnnotation> notSet = new HashSet<UAnnotation>();
//            HashSet<String> labels = new HashSet<String>();
//            for (UAnnotation annot: group) {
//                labels.add(annot.getRule().getId());
//                if (QualifierDif.containsNegative(annot.getQualifierSet())) {
//                    notSet.add(annot);
//                }
//            }
//            String id = String.join(Constant.STR_UNDERSCORE, labels);
//            UniRuleType uniRuleType = createUniRuleTypeWithMetaData(id, userId);
//            
//            MainType mainType = new MainType();
//            mainType.setConditionSets(createMainConditionSets(famId));
//            uniRuleType.setMain(mainType);
//            
//            CasesType casesType = new CasesType();
//            CaseType caseType = new CaseType();          
//            caseType.setConditionSets(createCaseConditionSets(group, notSet));
//            caseType.setAnnotations(createAnnotations(group, notSet));
//            casesType.getCase().add(caseType);
//            uniRuleType.setCases(casesType);            
//            rules.add(uniRuleType);
//        }
//        return rules;
//    }
    
    public static UniRuleType getRule(String famId, String userId, ArrayList<UniRuleAnnotationGroup> uagList, String externalComment, String internalComment, RuleStatusType status, StringBuffer errorBuf) throws Exception, DatatypeConfigurationException {

        UniRuleType uniRuleType = getExistingOrCreateUniRuleTypeWithMetaData(famId, ID_UNIRULE, userId, status);        // Attempt to use existing or create a new one if necessary

        // Comments
        // Only create information type, if it does not exist.  Do not want to overwrite other information that maybe in InformationType field
        InformationType informationType = null;
        informationType = uniRuleType.getInformation();
        if (null == informationType) {
            informationType = new InformationType();
            uniRuleType.setInformation(informationType);
        }
        
        uniRuleType.setMain(null);      // Clear previous information
        // overwrite comment
        informationType.setComment(externalComment);
        informationType.setInternal(internalComment);
        
        // Add cases that do not apply to PAINT.  These were read when the book was opened. Just write these back
        PaintManager pm = PaintManager.inst();
        Family family = pm.getFamily();
        ArrayList<CaseTypeDetails> cases = family.getErrorPaintCases();
        CasesType casesType = new CasesType();
        if (null != cases && 0 != cases.size()) {
            System.out.println("Rewriting cases for family " + famId + " that could not be processed when the family was opened.");
            if (1 == cases.size()) {
                System.out.println("There is 1 case that could not be processed when the family was opened. It will be written back");                
            }
            else {
                System.out.println("There are " + cases.size() + " cases that could not be processed when the family was opened. It will be written back");
            }
            for (CaseTypeDetails ct : cases) {
                CaseType ctNew = new CaseType();
                MainType mtCur = ct.getCaseType();
                if (null == mtCur) {
                    System.out.println("No case type information for error case from when family was opened - " + mtCur.toString());
                    continue;
                }
                ctNew.setRuleExceptions(mtCur.getRuleExceptions());
                ctNew.setAnnotations(mtCur.getAnnotations());
                ctNew.setConditionSets(mtCur.getConditionSets());
                casesType.getCase().add(ctNew);
            }
        }

        // If there are case conditions to the root and without dependent condition nodes, add these to the main condition of the tree.
        Node root = null;
        if (null != pm.getTree()) {
            GeneNode gRoot = pm.getTree().getRoot();
            if (null != gRoot) {
                root = gRoot.getNode();
            }
        }
        
        UniRuleAnnotationGroup rootCase = null;
        for (int i = 0; i < uagList.size(); i++) {
            UniRuleAnnotationGroup cur = uagList.get(i);
            ArrayList<RuleExceptionType> exemptions = getExceptions(cur);
            if ((null == cur.getDependantConditionNodes() || 0 == cur.getDependantConditionNodes().size()) &&
                false == cur.getMainCondNegative().booleanValue() &&
                cur.getMainConditionNode() == root &&
                null == exemptions) {
                rootCase = cur;
                uagList.remove(cur);
                break;
            }
        }
        MainType mainType = null;
        if (null != rootCase) {
            mainType = new MainType();
            uniRuleType.setMain(mainType);
            mainType.setConditionSets(createCaseConditionSets(rootCase));
            ArrayList<Rule> unhandledRules = new ArrayList<Rule>();
            AnnotationsType at = createAnnotations(rootCase.getRules(), unhandledRules);
            if (false == unhandledRules.isEmpty()) {
                for (Rule r : unhandledRules) {
                    errorBuf.append("Rule with label" + r.getLabel() + " and value " + r.getValue() + " could not be added to annotation for root node\n");
                }
            }
            mainType.setAnnotations(at);            
        }
        
        if (null != uagList) {
            System.out.println("Creating rule for family id " + famId + " for non-root node");
            System.out.println("There are " + uagList.size() + " patterns");
            for (UniRuleAnnotationGroup uag : uagList) {
               
                CaseType caseType = new CaseType();
                caseType.setConditionSets(createCaseConditionSets(uag));
                ArrayList<RuleExceptionType> exemptions = getExceptions(uag);
                if (null != exemptions) {
                    RuleExceptionsType ret = new RuleExceptionsType();
                    ret.getRuleException().addAll(exemptions);
                    caseType.setRuleExceptions(ret);
                }
                ArrayList<Rule> unhandledRules = new ArrayList<Rule>();
                AnnotationsType at = createAnnotations(uag.getRules(), unhandledRules);
                if (false == unhandledRules.isEmpty()) {
                    for (Rule r: unhandledRules) {
                        errorBuf.append("Rule with label" + r.getLabel() + " and value " + r.getValue() + " could not be added to annotation\n");
                    }
                }
                caseType.setAnnotations(at);
                casesType.getCase().add(caseType);
            }
        }
        if (false == casesType.getCase().isEmpty()) {
            uniRuleType.setCases(casesType);
        }
        
        // If there are no annotations to the root node, create case conditions to the main node
        if (null == mainType) {
            mainType = new MainType();
            uniRuleType.setMain(mainType);
            mainType.setConditionSets(createMainConditionSets(root.getStaticInfo().getPublicId()));
        }
        
        return uniRuleType;
    }

    public static ArrayList<RuleExceptionType> getExceptions(UniRuleAnnotationGroup urag) {
        if (null == urag) {
            return null;
        }
        
        ArrayList<Rule> rules = urag.getRules();
        if (null == rules) {
            return null;
        }
        ArrayList<RuleExceptionType> exceptions = new ArrayList<RuleExceptionType>();
        for (Rule r: rules) {
           RuleExceptionType ret = (RuleExceptionType)r.getException();
           if (null != ret) {
               exceptions.add(ret);
           }
        }
        if (exceptions.isEmpty()) {
            return null;
        }
        return exceptions;
    }
    
//    private static UniRuleType createUniRule(String userId) throws DatatypeConfigurationException {
//        UniRuleType uniRuleType = createUniRuleTypeWithMetaData("UR000000000", userId);
//
//        MainType mainType = new MainType();
//        mainType.setConditionSets(createMainConditionSets("PTHR11845"));
//        uniRuleType.setMain(mainType);
//
//        CasesType casesType = new CasesType();
//        CaseType caseType = new CaseType();
//        caseType.setConditionSets(createCaseConditionSets());
//        caseType.setAnnotations(createAnnotations());
//        casesType.getCase().add(caseType);
//        uniRuleType.setCases(casesType);
//
//        return uniRuleType;
//    }
    
    private static UniRuleType getExistingOrCreateUniRuleTypeWithMetaData(String familyId, String id, String userId, RuleStatusType status) throws DatatypeConfigurationException {
        
        // Check if there is an existing rule
        UniRuleType uniRuleType = getRuleForFamily(familyId, new ArrayList<Boolean>());
        if (null == uniRuleType) {
            return createUniRuleTypeWithMetaData(familyId, id, userId, status);
        }

        if (null != status) {
            uniRuleType.setStatus(status);
        }
        uniRuleType.setId(id);
        uniRuleType.setCreator(userId);

        XMLGregorianCalendar xmlGregorianCalendar = getXmlGregorianCalendarNow();
        uniRuleType.setCreated(xmlGregorianCalendar);
        uniRuleType.setModified(xmlGregorianCalendar);
        uniRuleType.setModifiedBy(userId);
        
//        uniRuleType.setMain(null);        // Do not overwrite main.  Leave existing or newly created information
        uniRuleType.setCases(null);
        return uniRuleType;
    }

    private static UniRuleType createUniRuleTypeWithMetaData(String familyId, String id, String userId, RuleStatusType status) throws DatatypeConfigurationException {
        UniRuleType uniRuleType = new UniRuleType();
        if (null != status) {
            uniRuleType.setStatus(status);
        }
        uniRuleType.setId(id);
        uniRuleType.setCreator(userId);

        XMLGregorianCalendar xmlGregorianCalendar = getXmlGregorianCalendarNow();

        uniRuleType.setCreated(xmlGregorianCalendar);
        uniRuleType.setModified(xmlGregorianCalendar);
        uniRuleType.setModifiedBy(userId);
        
        
//        // Main type
//        MainType mainType = new MainType();
//        mainType.setConditionSets(createMainConditionSets(familyId));
//        uniRuleType.setMain(mainType);

        return uniRuleType;
    }

    private static XMLGregorianCalendar getXmlGregorianCalendarNow() throws DatatypeConfigurationException {
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(Date.valueOf(LocalDate.now()));
        XMLGregorianCalendar xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
        return xmlGregorianCalendar;
    }


    private static MainType.ConditionSets createMainConditionSets(String rootNodeId) {
        MainType.ConditionSets conditionSets = new MainType.ConditionSets();

        ConditionSetType conditionSetType = new ConditionSetType();

        ConditionType pantherMatchCondition = createConditionType(LABEL_CONDITION_TREE_NODE, rootNodeId, false);
        conditionSetType.getCondition().add(pantherMatchCondition);

        conditionSets.getConditionSet().add(conditionSetType);
        return conditionSets;
    }



//    private static MainType.ConditionSets createCaseConditionSets() {
//        MainType.ConditionSets conditionSets = new MainType.ConditionSets();
//
//        ConditionSetType conditionSetType = new ConditionSetType();
//
//        // non-negative condition of type PANTHER tree node is for gain-of-function
//        ConditionType pthrGainOfFunctionCondition = createConditionType("PANTHER tree node", "PTN004312962", false);
//        conditionSetType.getCondition().add(pthrGainOfFunctionCondition);
//        // negative condition of type PANTHER tree node is for loss-of-function
//        ConditionType pthrLossOfFunctionCondition = createConditionType("PANTHER tree node", "PTN000932019", true);
//        conditionSetType.getCondition().add(pthrLossOfFunctionCondition);
//
//        conditionSets.getConditionSet().add(conditionSetType);
//        return conditionSets;
//    }
    
    private static MainType.ConditionSets createCaseConditionSets(UniRuleAnnotationGroup uag) {
        MainType.ConditionSets conditionSets = new MainType.ConditionSets();
        ConditionSetType conditionSetType = new ConditionSetType();
        
        String id = uag.getMainConditionNode().getStaticInfo().getPublicId();
        ConditionType mainCond = createConditionType(LABEL_CONDITION_TREE_NODE, id, uag.getMainCondNegative());
        conditionSetType.getCondition().add(mainCond);
        
        ArrayList<Node> depNodes = uag.getDependantConditionNodes();
        boolean isNeg = !(uag.getMainCondNegative());
        if (null != depNodes) {
            for (Node dep : depNodes) {
                id = dep.getStaticInfo().getPublicId();
                ConditionType depCond = createConditionType(LABEL_CONDITION_TREE_NODE, id, isNeg);
                conditionSetType.getCondition().add(depCond);
            }
        }
        
//        String posAnnotNodeId = null;
//        for (UAnnotation a: group) {
//            if (false == notSet.contains(a)) {
//                posAnnotNodeId = a.getAnnotatedNode().getStaticInfo().getPublicId();
//            }
//        }
//        
//        if (null != posAnnotNodeId) {
//            // non-negative condition of type PANTHER tree node is for gain-of-function
//            ConditionType pthrGainOfFunctionCondition = createConditionType(LABEL_CONDITION_TREE_NODE, posAnnotNodeId, false);
//            conditionSetType.getCondition().add(pthrGainOfFunctionCondition);
//            System.out.println("Condition type - gain of function for " + posAnnotNodeId);
//        }
//        
//        // negative condition of type PANTHER tree node is for loss-of-function
//        HashSet<String> handledNodes = new HashSet<String>();
//        for (UAnnotation a : notSet) {
//            String nodeId = a.getAnnotatedNode().getStaticInfo().getPublicId();
//            if (true == handledNodes.contains(nodeId)) {
//                continue;
//            }           
//            ConditionType pthrLossOfFunctionCondition = createConditionType(LABEL_CONDITION_TREE_NODE, nodeId, true);
//            conditionSetType.getCondition().add(pthrLossOfFunctionCondition);
//            System.out.println("Condition type - loss of function for " + nodeId);
//            handledNodes.add(nodeId);
//        }

        conditionSets.getConditionSet().add(conditionSetType);
        return conditionSets;
    }    

    private static ConditionType createConditionType(String type, String conditionValue, boolean negative) {
        ConditionType condition = new ConditionType();
        condition.setType(type);
        condition.setNegative(negative);
        ConditionValue pthrMatchConditionValue = new ConditionValue();
        pthrMatchConditionValue.setValue(conditionValue);
        condition.getValue().add(pthrMatchConditionValue);
        return condition;

    }
    
    private static AnnotationsType createAnnotations(ArrayList<Rule> origRules, ArrayList<Rule> unhandledRules) throws Exception {
        if (null == origRules) {
            return null;
        }
        ArrayList<Rule> rules = (ArrayList<Rule>)origRules.clone();
        AnnotationsType annotationsType = new AnnotationsType();
        ArrayList<Rule> handled = new ArrayList<Rule>();
        
        // Handle proteins
        List<AnnotationType> proteinAnnotList = getProteinAnnots(rules, handled);
        rules.removeAll(handled);
        annotationsType.getAnnotation().addAll(proteinAnnotList);

        // Handle comments
        handled.clear();
        List<AnnotationType> commentAnnotList = getCommentAnnots(rules, handled);
        rules.removeAll(handled);
        annotationsType.getAnnotation().addAll(commentAnnotList);
        
        // Remaining types
        for (Rule r: rules) {
            LabelValue lv = r.getLabelValue();
            String type = lv.getLabel();
            String value = lv.getValue();
            if (Rule.PROPERTY_SPKW.equals(type)) {
                KeywordTransformer keywordTransformer = new KeywordTransformer();
                KeywordType kt = keywordTransformer.toXml(value);
                AnnotationType at = new AnnotationType();
                at.setKeyword(kt);
                annotationsType.getAnnotation().add(at);
                handled.add(r);
            }
            else if (Rule.PROPERTY_DRGO.equals(type)) {
                DbReferenceType dr = new DbReferenceType();
                dr.setType(DB_REF_TYPE_GO);
                dr.setId(value);
                AnnotationType at = new AnnotationType();
                at.setDbReference(dr);
                annotationsType.getAnnotation().add(at);
                handled.add(r);
            }
            else if (Rule.PROPERTY_GNNM.equals(type)) {
                GeneNameType gnt =  new GeneNameType();
                gnt.setType(GENE_NAME_TYPE_PRIMARY);
                gnt.setValue(value);
                GeneType gt = new GeneType();
                gt.getName().add(gnt);
                AnnotationType at = new AnnotationType();
                at.setGene(gt);
                annotationsType.getAnnotation().add(at);
                handled.add(r);
            }
            else if (Rule.PROPERTY_GNSY.equals(type)) {
                GeneNameType gnt =  new GeneNameType();
                gnt.setType(GENE_NAME_TYPE_SYNONYM);
                gnt.setValue(value);
                GeneType gt = new GeneType();
                gt.getName().add(gnt);
                AnnotationType at = new AnnotationType();
                at.setGene(gt);
                annotationsType.getAnnotation().add(at);
                handled.add(r);
            }            
        }
        rules.removeAll(handled);
        unhandledRules.addAll(rules);
        return annotationsType;
    }

    private static List<AnnotationType> getProteinAnnots(ArrayList<Rule> rules, ArrayList<Rule> handledRules) {
        
        ArrayList<ProteinType.RecommendedName> derfList = new ArrayList<ProteinType.RecommendedName>();
        ArrayList<EvidencedStringType> shortNameList = new  ArrayList<EvidencedStringType>();
        ArrayList<EvidencedStringType> ecNumberList = new  ArrayList<EvidencedStringType>();
        ArrayList<ProteinType.AlternativeName> alternateNameList = new ArrayList<ProteinType.AlternativeName> ();
        ArrayList<EvidencedStringType> alternateEcNumberList = new ArrayList<EvidencedStringType>();
        
        
        for (Rule r: rules) {
            LabelValue lv = r.getLabelValue();
            String label = lv.getLabel();
            String value = lv.getValue();
            
            if (Rule.PROPERTY_DERF.equals(label)) {
                ProteinType.RecommendedName derf = new ProteinType.RecommendedName();
                EvidencedStringType est = new EvidencedStringType();
                est.setValue(value);
                derf.setFullName(est);
                derfList.add(derf);
                handledRules.add(r);
                continue;
            }
            else if (Rule.PROPERTY_DERS.equals(label)) {
                EvidencedStringType est = new EvidencedStringType();
                est.setValue(value);
                shortNameList.add(est);
                handledRules.add(r);
                continue;
            }
            else if (Rule.PROPERTY_DEEC.equals(label)) {
                EvidencedStringType est = new EvidencedStringType();
                est.setValue(value);
                ecNumberList.add(est);
                handledRules.add(r);
                continue;
            }
            else if (Rule.PROPERTY_DEAF.equals(label)) {
                ProteinType.AlternativeName alternativeName = new ProteinType.AlternativeName();
                EvidencedStringType est = new EvidencedStringType();
                est.setValue(value);
                alternativeName.setFullName(est);
                alternateNameList.add(alternativeName);
                handledRules.add(r);
                continue;
            }
            else if (Rule.PROPERTY_DEAE.equals(label)) {
                EvidencedStringType est = new EvidencedStringType();
                est.setValue(value);
                alternateEcNumberList.add(est);
                handledRules.add(r);
                continue;
            }            
        }
        
        
        ArrayList<AnnotationType> atl = new ArrayList<AnnotationType>();
        if (false == derfList.isEmpty()) {
            for (ProteinType.RecommendedName rn: derfList) {
                AnnotationType at = new AnnotationType();
                atl.add(at);
                ProteinType pt = new ProteinType();
                at.setProtein(pt);
                pt.setRecommendedName(rn);
            }
        }


        if (false == shortNameList.isEmpty()) {
            AnnotationType at = new AnnotationType();
            atl.add(at);
            ProteinType pt = at.getProtein();
            if (null == pt) {
                pt = new ProteinType();
                at.setProtein(pt);
            }
            ProteinType.RecommendedName rn = pt.getRecommendedName();
            if (null == rn) {
                rn = new ProteinType.RecommendedName();
                pt.setRecommendedName(rn);
            }
            for (EvidencedStringType est: shortNameList) {
                rn.getShortName().add(est);
            }
        }
        
        if (false == ecNumberList.isEmpty()) {
            AnnotationType at = new AnnotationType();
            atl.add(at);
            ProteinType pt = at.getProtein();
            if (null == pt) {
                pt = new ProteinType();
                at.setProtein(pt);
            }
            ProteinType.RecommendedName rn = pt.getRecommendedName();
            if (null == rn) {
                rn = new ProteinType.RecommendedName();
                pt.setRecommendedName(rn);
            }
            for (EvidencedStringType est : ecNumberList) {
                rn.getEcNumber().add(est);
            }
        }
        
        if (false == alternateNameList.isEmpty()) {
            AnnotationType at = new AnnotationType();
            atl.add(at);
            ProteinType pt = new ProteinType();
            at.setProtein(pt);
            for (ProteinType.AlternativeName altName : alternateNameList) {
                pt.getAlternativeName().add(altName);
            }
        }
        if (false == alternateEcNumberList.isEmpty()) {
            AnnotationType at = new AnnotationType();
            atl.add(at);
            ProteinType pt = new ProteinType();
            at.setProtein(pt);
            List<ProteinType.AlternativeName> alternateEcNumList = pt.getAlternativeName();
            ProteinType.AlternativeName ecAltName = new ProteinType.AlternativeName();
            alternateEcNumList.add(ecAltName);
            for (EvidencedStringType altEcNum: alternateEcNumberList) {
                ecAltName.getEcNumber().add(altEcNum);
            }
        }
        // Return empty list, if no annotations
        if (true == atl.isEmpty()) {
            return new ArrayList<AnnotationType>(0);
        }
        return atl;
    }

    private static List<AnnotationType> getCommentAnnots(ArrayList<Rule> rules, ArrayList<Rule> handledRules) {
        ArrayList<CommentType> ctl = new ArrayList<CommentType>();
        for (Rule r : rules) {
            LabelValue lv = r.getLabelValue();
            String type = lv.getLabel();
            String value = lv.getValue();
            String label = COMMENT_LABEL_RULE_LOOKUP.get(type);
            // Ensure this is a valid comment type
            if (null == label) {
                continue;
            }
            if (COMMENT_TYPE_CATALYTIC_ACTIVITY.equals(label)) {
                CatalyticActivityTransformer catalyticActivityTransformer = new CatalyticActivityTransformer();
                CommentType ct = catalyticActivityTransformer.cccaToXml(value);
                ctl.add(ct);
                handledRules.add(r);
                continue;
            } else if (COMMENT_TYPE_SUBCELLULAR_LOCATION.equals(label)) {
                SubcellularLocationTransformer subcellularLocationTransformer = new SubcellularLocationTransformer();
                CommentType ct = subcellularLocationTransformer.ccloToXml(value);
                ctl.add(ct);
                handledRules.add(r);
                continue;
            } else if (COMMENT_TYPE_COFACTOR.equals(label)) {
                CofactorTransformer cofactorTransformer = new CofactorTransformer();
                CommentType ct = cofactorTransformer.toXml(value);
                ctl.add(ct);
                handledRules.add(r);
                continue;
            } else {                
                CommentType ct = new CommentType();
                ct.setType(label);
                EvidencedStringType est = new EvidencedStringType();
                est.setValue(value);
                ct.getText().add(est);
                ctl.add(ct);
                handledRules.add(r);
                continue;
            }
        }   
        if (false == ctl.isEmpty()) {
            ArrayList<AnnotationType> atl = new ArrayList<AnnotationType>();
            for (CommentType ct : ctl) {
                AnnotationType at = new AnnotationType();
                atl.add(at);
                at.setComment(ct);
            }
            return atl;
        }
        return new ArrayList<AnnotationType>(0);
    }

    private static AnnotationsType createAnnotationsOrig(ArrayList<Rule> rules) throws Exception {
        AnnotationsType annotationsType = new AnnotationsType();

        // Just output information about gain of function
        for (Rule r: rules) {
            LabelValue lv = r.getLabelValue();
            String label = lv.getLabel();
            String value = lv.getValue();
            System.out.println("Creating annotation for " + label + " with value " + value);
            if (Rule.PROPERTY_DERF.equals(label)) {
                AnnotationType annotationType1 = new AnnotationType();
                ProteinType proteinType = new ProteinType();
                ProteinType.RecommendedName recommendedName = new ProteinType.RecommendedName();               
                EvidencedStringType recommendedNameValue = new EvidencedStringType();
                recommendedNameValue.setValue(value);
                recommendedName.setFullName(recommendedNameValue);
                proteinType.setRecommendedName(recommendedName);
                annotationType1.setProtein(proteinType);
                annotationsType.getAnnotation().add(annotationType1);
                System.out.println("DERF - proteinType with recommended name " + value);
            }
            else if (Rule.PROPERTY_DEEC.equals(label)) {
                AnnotationType annotationType1 = new AnnotationType();
                ProteinType proteinType = new ProteinType();
                ProteinType.RecommendedName recommendedName = new ProteinType.RecommendedName();               
                EvidencedStringType recommendedEc = new EvidencedStringType();
                recommendedEc.setValue(value);
                recommendedName.getEcNumber().add(recommendedEc);
                proteinType.setRecommendedName(recommendedName);
                annotationType1.setProtein(proteinType);
                annotationsType.getAnnotation().add(annotationType1);
                System.out.println("DEEC - Evidence string type " + value);                
            }
            else if (Rule.PROPERTY_DEAF.equals(label)) {
                AnnotationType annotationType1 = new AnnotationType();
                ProteinType proteinType = new ProteinType();
                ProteinType.AlternativeName alternativeName = new ProteinType.AlternativeName();
                EvidencedStringType alternativeNameValue = new EvidencedStringType();
                alternativeNameValue.setValue(value);
                alternativeName.setFullName(alternativeNameValue);
                proteinType.getAlternativeName().add(alternativeName);
                annotationType1.setProtein(proteinType);
                annotationsType.getAnnotation().add(annotationType1);
                System.out.println("DEAF - Evidence alternative name " + value);                
            }            
            else if (Rule.PROPERTY_DEAE.equals(label)) {
                AnnotationType annotationType1 = new AnnotationType();
                ProteinType proteinType = new ProteinType();
                ProteinType.AlternativeName alternativeName = new ProteinType.AlternativeName();
                EvidencedStringType alternativeEc = new EvidencedStringType();
                alternativeEc.setValue(value);
                alternativeName.getEcNumber().add(alternativeEc);
                proteinType.getAlternativeName().add(alternativeName);
                annotationType1.setProtein(proteinType);
                annotationsType.getAnnotation().add(annotationType1);
                System.out.println("DEAE - alternative EC " + value);                  
                
            }            
            else if (Rule.PROPERTY_CCFU.equals(label)) {
                AnnotationType annotationType2 = new AnnotationType();
                CommentType commentType = new CommentType();
                commentType.setType(LABEL_COMMENT_FUNCTION);
                EvidencedStringType commentText = new EvidencedStringType();
                commentText.setValue(value);
                commentType.getText().add(commentText);
                annotationType2.setComment(commentType);
                annotationsType.getAnnotation().add(annotationType2);
                System.out.println("CCFU - comment function " + value); 
            }
            else if (Rule.PROPERTY_CCCA.equals(label)) {
                AnnotationType annotationType3 = new AnnotationType();
                CatalyticActivityTransformer catalyticActivityTransformer = new CatalyticActivityTransformer();
                CommentType catalyticActivityComment = catalyticActivityTransformer.cccaToXml(value);
                annotationType3.setComment(catalyticActivityComment);
                annotationsType.getAnnotation().add(annotationType3);
                System.out.println("CCCA - Catalytic Activity Transformer " + value);                 
            }
            else if (Rule.PROPERTY_CCCO.equals(label)) {
                AnnotationType annotationType4 = new AnnotationType();
                CofactorTransformer cofactorTransformer = new CofactorTransformer();
                CommentType cofactorComment = cofactorTransformer.toXml(value);
                annotationType4.setComment(cofactorComment);
                annotationsType.getAnnotation().add(annotationType4);
                System.out.println("CCCA - Catalytic Activity Transformer " + value);
            }
            else {
                throw new Exception("Found unsupported Rule type " + label);
            }
        }        

        return annotationsType;
    }
    
    private static AnnotationsType createAnnotations() {
        AnnotationsType annotationsType = new AnnotationsType();

        AnnotationType annotationType1 = new AnnotationType();

        // Types so far exported to Paint: DERF, DEAF, DEEC, DEAE, CCFU, CCCA
        // ProteinType is for UniProtKB DE-line annotations
        ProteinType proteinType = new ProteinType();

        // RecommendedName is for DERF and DEEC
        ProteinType.RecommendedName recommendedName = new ProteinType.RecommendedName();

        // DERF
        EvidencedStringType recommendedNameValue = new EvidencedStringType();
        recommendedNameValue.setValue("Recommended Name");
        recommendedName.setFullName(recommendedNameValue);

        // DEEC
        EvidencedStringType recommendedEc = new EvidencedStringType();
        recommendedEc.setValue("3.1.2.-");
        recommendedName.getEcNumber().add(recommendedEc);

        proteinType.setRecommendedName(recommendedName);

        // For DEAF and DEAE
        ProteinType.AlternativeName alternativeName = new ProteinType.AlternativeName();

        // DEAF
        EvidencedStringType alternativeNameValue = new EvidencedStringType();
        alternativeNameValue.setValue("Alternative Name");
        alternativeName.setFullName(alternativeNameValue);


        // DEAE
        EvidencedStringType alternativeEc = new EvidencedStringType();
        alternativeEc.setValue("1.2.3.4");
        alternativeName.getEcNumber().add(alternativeEc);

        proteinType.getAlternativeName().add(alternativeName);

        annotationType1.setProtein(proteinType);
        annotationsType.getAnnotation().add(annotationType1);

        // For UniProtKB CC-line annotations
        AnnotationType annotationType2 = new AnnotationType();
        CommentType commentType = new CommentType();
        commentType.setType("function");
        EvidencedStringType commentText = new EvidencedStringType();
        commentText.setValue("A function comment");
        commentType.getText().add(commentText);

        annotationType2.setComment(commentType);

        annotationsType.getAnnotation().add(annotationType2);
        return annotationsType;
    }


    public static UniRuleType getRuleForFamily(String famId, ArrayList<Boolean> opSuccessList, String userId, String pw) {
        UniRuleImporterService_Service service = null;
        try {
            service = new UniRuleImporterService_Service(new URL(Preferences.inst().getUniprotURL()));
            
//            service = new UniRuleImporterService_Service(
//                    new URL("https://wwwdev.ebi.ac.uk/uniprot/unirule/import/import?wsdl"));
//            User user = PaintManager.inst().getUser();
            //String userId = user.getUserId();
            //String pw = String.valueOf((char[])PaintManager.inst().getUserInfo().get(1));            
//                    new URL("http://localhost:8081/uniprot/unirule/import/import?wsdl"));
            CredentialsProvider credentialsProvider = new CredentialsProvider(userId, pw);
            SecurityHeaderAppender securityHeaderAppender = new SecurityHeaderAppender();
            securityHeaderAppender.addSecurityHeader(service, credentialsProvider);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        UniRuleImporterService uniruleImporter = service.getUniRuleImporter();

        try {
            UniRuleType ut = uniruleImporter.retrievePantherRule(famId);
            opSuccessList.clear();
            opSuccessList.add(Boolean.TRUE);
            return ut;

        } catch (UniRuleException e) {
            e.printStackTrace();
            opSuccessList.clear();
            opSuccessList.add(Boolean.FALSE);
        }
        opSuccessList.clear();
        opSuccessList.add(Boolean.FALSE);
        return null;        
    }
    
    public static UniRuleType getRuleForFamily(String famId, ArrayList<Boolean> opSuccessList) {
        ArrayList<Object> info = PaintManager.inst().getUserInfo();
        String userId = (String) info.get(0);
        String pw = String.copyValueOf((char[]) info.get(1));
        return getRuleForFamily(famId, opSuccessList, userId, pw);
    }
    
//    public static ArrayList<UniRuleAnnotationGroup> getPAINTRulesForRuleOrig(UniRuleType uniRule, String familyId, Node root) {
//        if (null == uniRule) {
//            return new ArrayList<UniRuleAnnotationGroup>();
//        }
//        System.out.println("Information for Unirule " + uniRule.getId());
//        System.out.println("Rule was modified by " + uniRule.getCreator());
//        System.out.println("Modified on " + DATE_FORMATTER.format(new Date(uniRule.getModified().toGregorianCalendar().getTimeInMillis())));
//        
//        ArrayList<UniRuleAnnotationGroup> groups = new ArrayList<UniRuleAnnotationGroup>();
//        MainType mt = uniRule.getMain();
//        if (null != mt) {
//            System.out.println("Information for main condition");
//            ConditionSets cs = mt.getConditionSets();
//            if (null != cs) {
//                List<ConditionSetType> cstList = cs.getConditionSet();
//                if (null != cstList) {
//                    for (int i = 0; i < cstList.size(); i++) {
//                        ConditionSetType cst = cstList.get(i);
//                        List<ConditionType> csList = cst.getCondition();
//                        if (null != csList) {
//                            for (int j = 0; j < csList.size(); j++) {
//                                ConditionType ct = csList.get(j);
//                                List<ConditionValue> cvl = ct.getValue();
//                                if (null != ct && null != cvl) {
//                                    for (int k = 0; k < cvl.size(); k++) {
//                                        ConditionValue cv = cvl.get(k);
//                                        System.out.println("Type - " + ct.getType() + " value " + cv.getValue());
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            CasesType cst = uniRule.getCases();
//            if (null != cst) {
//                List<CaseType> ctl = cst.getCase();
//                if (null != ctl) {
//                    System.out.println("There are " + ctl.size() + " cases");
//                    for (int i = 0; i < ctl.size(); i++) {
//                        System.out.println("Processing case " + (i + 1));
//                        CaseType ct = ctl.get(i);
//                        ConditionSets condSets = ct.getConditionSets();
//                        if (null == condSets) {
//                            System.out.println("Skipping null condition set");
//                            continue;
//                        }
//                        List<ConditionSetType> cstl = condSets.getConditionSet();
//                        if (null == cstl || 0 == cstl.size()) {
//                            System.out.println("Skipping null condition set");
//                            continue;
//                        }
//                        UniRuleAnnotationGroup uag = new UniRuleAnnotationGroup();
//                        groups.add(uag);                       
//                        for (int j = 0; j < cstl.size(); j++) {
//                            ConditionSetType condSetType = cstl.get(j);
//                            List<ConditionType> condTypeList = condSetType.getCondition();
//                            if (null == condTypeList || 0 == condTypeList.size()) {
//                                System.out.println("Skipping null condition set");
//                                continue;                                
//                            }
//
//                            for (int k = 0; k < condTypeList.size(); k++) {
//                                ConditionType condType  = condTypeList.get(k);
//                                boolean isNeg = condType.isNegative();
//                                if (false == isNeg) {
//                                    uag.addPositiveCondNode(condType.getValue().get(0).getValue());
//                                }
//                                else {
//                                    uag.addNegativeCondNode(condType.getValue().get(0).getValue());
//                                }
//                                if (false == isNeg) {
//                                    System.out.println("True condition set " + condType.getType() + " value " + condType.getValue().get(0).getValue());
//                                }
//                                else {
//                                    System.out.println("False condition set " + condType.getType() + " value " + condType.getValue().get(0).getValue());
//                                }
//                            }
//                        }
//                        AnnotationsType ats = ct.getAnnotations();
//                        if (null == ats) {
//                            System.out.println("Skipping null annotation set");
//                            groups.remove(uag);
//                            continue;
//                        }
//
//                        List<AnnotationType> atl = ats.getAnnotation();
//                        if (null == atl || 0 == atl.size()) {
//                            System.out.println("Skipping null annotation set");
//                            groups.remove(uag);                            
//                            continue;
//                        }
//                        for (int j = 0; j < atl.size(); j++) {
//                            AnnotationType at = atl.get(j);
//                            ProteinType pt = at.getProtein();
//                            if (null != pt) {
//                                // ProteinType - 4 types of rules for these
//                                ProteinType.RecommendedName rn = pt.getRecommendedName();
//                                if (null == rn ||
//                                   (null != rn && ((null == rn.getFullName() || (null != rn.getFullName()) && null == rn.getFullName().getValue()) && 
//                                                  ((null == rn.getEcNumber() || (null != rn.getEcNumber()) && 0 == rn.getEcNumber().size()))))) {
//                                    // Alternate name - 2 types of rules for these
//                                    List<ProteinType.AlternativeName> anl = pt.getAlternativeName();
//                                    if (null == anl) {
//                                        groups.remove(uag); 
//                                        System.out.println("Skipping null annotation type, no protein type or comment type found");
//                                        continue;
//                                    }
//                                    else if (anl.size() != 1) {
//                                        groups.remove(uag); 
//                                        System.out.println("Skipping annotation type, did not find exactly 1 alternative name, found " + anl.size());
//                                        continue;                                        
//                                    }
//                                    ProteinType.AlternativeName an = anl.get(0);
//                                    EvidencedStringType fullName = an.getFullName();
//                                    if (null == fullName) {
//                                        List<EvidencedStringType> estl = an.getEcNumber();
//                                        if (null == estl) {
//                                            groups.remove(uag); 
//                                            System.out.println("Skipping null alternative name, no EC number found");
//                                            continue;
//                                        }
//                                        else if (estl.size() != 1) {
//                                            groups.remove(uag); 
//                                            System.out.println("Skipping null alternative name, did not find exactly 1 EC number, found " + estl.size());
//                                            continue;
//                                        }
//                                        String ecNum = estl.get(0).getValue();
//                                        System.out.println("Found rule type " + Rule.PROPERTY_DEAE + " with value " + ecNum);
//                                        Rule r = new Rule();
//                                        LabelValue lv = new LabelValue();
//                                        lv.setLabel(Rule.PROPERTY_DEAE);
//                                        lv.setValue(ecNum);
//                                        r.setLabelValue(lv);
//                                        uag.addUnirule(r);
//                                        continue;
//                                    }
//                                    
//                                    // Full name
//                                    String altName = fullName.getValue();
//                                    System.out.println("Found rule type " + Rule.PROPERTY_DEAF + " with value " + altName);
//                                        Rule r = new Rule();
//                                        LabelValue lv = new LabelValue();
//                                        lv.setLabel(Rule.PROPERTY_DEAF);
//                                        lv.setValue(altName);
//                                        r.setLabelValue(lv);
//                                        uag.addUnirule(r);                             
//                                        continue;                                   
//                                }
//                                // Recommended Name
//                                EvidencedStringType fullName = rn.getFullName();
//                                if (null == fullName) {
//                                    List<EvidencedStringType> ecl = rn.getEcNumber();
//                                    if (null == ecl) {
//                                        groups.remove(uag); 
//                                        System.out.println("Skipping null full name, no EC number list found");
//                                        continue;
//                                    }
//                                    else if (ecl.size() != 1) {
//                                        groups.remove(uag); 
//                                        System.out.println("Skipping null full name, did not find exactly 1 EC number, found " + ecl.size());
//                                        continue;
//                                    }
//                                    String recommendedEc = ecl.get(0).getValue();
//                                    System.out.println("Found rule type " + Rule.PROPERTY_DEEC + " with value " + recommendedEc);
//                                    Rule r = new Rule();
//                                    LabelValue lv = new LabelValue();
//                                    lv.setLabel(Rule.PROPERTY_DEEC);
//                                    lv.setValue(recommendedEc);
//                                    r.setLabelValue(lv);
//                                    uag.addUnirule(r);
//                                    continue;
//                                }
//                                String fullNameValue = fullName.getValue();
//                                System.out.println("Found rule type " + Rule.PROPERTY_DERF + " with value " + fullNameValue);
//                                Rule r = new Rule();
//                                LabelValue lv = new LabelValue();
//                                lv.setLabel(Rule.PROPERTY_DERF);
//                                lv.setValue(fullNameValue);
//                                r.setLabelValue(lv);
//                                uag.addUnirule(r);
//                                continue;                               
//                            }
//                            else {
//                                // OR CommentType - 2 types
//                                CommentType commentType = at.getComment();
//                                if (null == commentType) {
//                                    groups.remove(uag); 
//                                    System.out.println("Skipping null annotation type, no protein type or comment type found");
//                                    continue;                                    
//                                }
//                                String type = commentType.getType();
//                                String value = null;
//                                String label = null;
//                                if (LABEL_COMMENT_FUNCTION.equals(type)) {
//                                    label = Rule.PROPERTY_CCFU;
//                                    value = commentType.getText().get(0).getValue();
//                                }
//                                else {
//                                    label = Rule.PROPERTY_CCCA;
//                                    CatalyticActivityTransformer cct = new CatalyticActivityTransformer();
//                                    value = cct.xmlToCcca(commentType);
//                                }
//                                System.out.println("Found rule type " + label + " with value " + value);
//                                value = convertTextFromXml(value);
//                                Rule r = new Rule();
//                                LabelValue lv = new LabelValue();
//                                lv.setLabel(label);
//                                lv.setValue(value);
//                                r.setLabelValue(lv);
//                                uag.addUnirule(r);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return  groups;
//    }
    
    public static boolean getInfoFromMainCondition(UniRuleType uniRule, String rootNodePublicId, StringBuffer errorBuf, Boolean containsInfoAboutRoot) {
        containsInfoAboutRoot = false;
        if (null == uniRule) {
            return true;
        }
            
        MainType mt = uniRule.getMain();
        if (null == mt) {
            return true;            
        }
        
//        RuleExceptionsType ret = mt.getRuleExceptions();
//        if (null != ret) {
//            errorBuf.append("Found rule excepttions for main condition\n");      
//            return false;            
//        }
        
        ConditionSets cs = mt.getConditionSets();
        if (null == cs) {        
            return true;
        }
        
        List<ConditionSetType> cstList = cs.getConditionSet();
        if (null == cstList ) {
            return true;
        }
        
        if (1 != cstList.size()) { 
            errorBuf.append("Found more than 1 condition set for main condition\n");      
            return false;
        }
        
        ConditionSetType cst = cstList.get(0);
        List<ConditionType> ctList = cst.getCondition();
        if (null == ctList || 1 != ctList.size()) {
            errorBuf.append("Condition list is null or != 1 for main condition\n");      
            return false;
        }
        ConditionType ct = ctList.get(0);
        if (false == CONDITION_TYPE_PANTHER_TREE_NODE.equals(ct.getType())) {
            System.out.println("Found main type of " + ct.getType());
            errorBuf.append("Found unexpected condtion type " + ct.getType() + " for main condition. This will be overwritten, when the book is saved.\n");            
            return false;
        }
        List<ConditionValue> cvl = ct.getValue();
        if (null == cvl) {
            errorBuf.append("Found null condition value for main type " + ct.getType() + " for main condition\n");            
            return false;
        }
        
        if (1 != cvl.size()) {
            errorBuf.append("Found " + cvl.size() + " entries for main condition root node.  Only 1 entry expected. \n");            
            return false;            
        }
        
        ConditionValue cv = cvl.get(0);
        if (false == rootNodePublicId.equals(cv.getValue())) {
            errorBuf.append("Found unexpected root node id " + cv.getValue() + " instead of " + rootNodePublicId + " for main condition\n");            
            System.out.println("Found unexpected rootNode id " + cv.getValue() + " instead of " + rootNodePublicId);
            return false;
        }
       
        
        if (null != ct.getRange()) {
            errorBuf.append("Found unexpected range for main condition\n");            
            System.out.println("Found unexpected range");
            return false;
        }
        if (null != ct.getTag()) {
            errorBuf.append("Found unexpected tag for main condition\n");  
            System.out.println("Found unexpected tag");
            return false;
        }        
        return true;        
    }    
    
    public static boolean verifyMainConditionOld(UniRuleType uniRule, String familyId, StringBuffer errorBuf) {
        if (null == uniRule) {
            errorBuf.append("No Unirule to parse\n");
            return false;
        }
            
        MainType mt = uniRule.getMain();
        if (null == mt) {
            errorBuf.append("Main condition is null\n");
            return false;            
        }
        
        ConditionSets cs = mt.getConditionSets();
        if (null == cs) {
            errorBuf.append("No condition sets for main condition\n");            
            return false;
        }
        
        List<ConditionSetType> cstList = cs.getConditionSet();
        if (null == cstList || 1 != cstList.size()) {
            errorBuf.append("No condition set list for main condition\n");            
            return false;
        }
        ConditionSetType cst = cstList.get(0);
        List<ConditionType> ctList = cst.getCondition();
        if (null == ctList || 1 != ctList.size()) {
            errorBuf.append("No condition set list for main condition\n");            
            return false;
        }
        ConditionType ct = ctList.get(0);
        if (false == LABEL_CONDITION_MAIN_TYPE.equals(ct.getType())) {
            System.out.println("Found main type of " + ct.getType());
            errorBuf.append("Found unexpected condtion type " + ct.getType() + " for main condition\n");            
            return false;
        }
        List<ConditionValue> cvl = ct.getValue();
        if (null == cvl || 1 != cvl.size()) {
            errorBuf.append("Found null condition value for main type " + ct.getType() + " for main condition\n");            
            return false;
        }
        ConditionValue cv = cvl.get(0);
        if (false == familyId.equals(cv.getValue())) {
            errorBuf.append("Found unexpected family id " + cv.getValue() + " instead of " + familyId + " for main condition\n");            
            System.out.println("Found unexpected family id " + cv.getValue() + " instead of " + familyId);
            return false;
        }
        if (null != ct.getRange()) {
            errorBuf.append("Found unexpected range for main condition\n");            
            System.out.println("Found unexpected range");
            return false;
        }
        if (null != ct.getTag()) {
            errorBuf.append("Found unexpected tag for main condition\n");  
            System.out.println("Found unexpected tag");
            return false;
        }
        return true;
    }
    
//    public boolean isCaseApplicable(CaseType ct, Node root) {
//        AnnotationsType at = ct.getAnnotations();
//        RuleExceptionsType ret = ct.getRuleExceptions();
//        ConditionSets cs = ct.getConditionSets();
//        // Ensure we have case type and annotation type, else return false
//        if (null == cs ||
//            null == at ||
//            null != ret) {
//            return false;
//        }
//        
//        // Ensure case type is one of the ones supported by PAINT
//        List<ConditionSetType> cstl =  cs.getConditionSet();
//        if (null == cstl) {
//            return false;
//        }
//        for (ConditionSetType cst: cstl) {
//            List<ConditionType> ctl = cst.getCondition();
//            if (null == ctl || 1 != ctl.size()) {
//                return false;
//            }
//            ConditionType condType = ctl.get(0);
//            return getNodes(condType, root);
//        }
//        
//        
//        return true;
//    }
    
    public static UniRuleAnnotationGroup getNodes(MainType ct, Node root, CaseTypeDetails ctd) {
        if (null == ct) {
            ctd.addMsg("Case type is null");
            return null;
        }
//        if (null != ct.getRuleExceptions()) {
//            ctd.addMsg("Case has rule exceptions information");
//            return null;
//        }
        ConditionSets cs = ct.getConditionSets();
        if (null == cs) {
            ctd.addMsg("Condition sets is null");
            return null;
        }
        List<ConditionSetType> cstl = cs.getConditionSet();
        if (null == cstl|| 1 < cstl.size()) {
            ctd.addMsg("Condition set type list is null");
            return null;
        }
        ConditionSetType cst = cstl.get(0);
        List<ConditionType> ctl = cst.getCondition();
        if (null == ctl || 0 == ctl.size()) {
            ctd.addMsg("Condition set type list is null");
            return null;
        }
        
        // There can be 1 or more nodes
        int size = ctl.size();        
        PaintManager pm = PaintManager.inst();        
        UniRuleAnnotationGroup urag = new UniRuleAnnotationGroup();
//        urag.setOverallStatsExempted(ct.isOverallStatsExempted());
        // If there is 1, then create node information and return;
        if (1 == size) {
            ConditionType condType = ctl.get(0);
            if (null != condType.getRange()) {
                ctd.addMsg("Found range");                 
                return null;
            }
            if (false == CONDITION_TYPE_PANTHER_TREE_NODE.equals(condType.getType())) {
                ctd.addMsg("Found unexpected condition type " + condType.getType() + " for main node");                
                return null;
            }
            List<ConditionValue> cvl = condType.getValue();
            if (null == cvl || 1 != cvl.size()) {
                ctd.addMsg("Condition value is null or has more than 1 entry for " + condType.getType());
                return null;
            }
            String nodeId = cvl.get(0).getValue();
            GeneNode gn = pm.getGeneByPTNId(nodeId);
            if (null == gn) {
                ctd.addMsg("Unable to find " + nodeId);
                return null;
            }
            urag.setMainConditionNode(gn.getNode());
            urag.setMainCondNegative(condType.isNegative());
            return urag;
        }
        
        // If there are multiple conditions, need to find main condition and dependant conditions.  Dependant conditions will be descendants of the main condition node.
        // Furthermore, isNegative will be opposite of main condition node for all descendants.
        HashMap<Node, Boolean> nodeToIsNeg = new HashMap<Node, Boolean>();
        for (ConditionType condType: ctl) {
            if (null != condType.getRange()) {
                ctd.addMsg("Found range");                 
                return null;
            }
            if (false == CONDITION_TYPE_PANTHER_TREE_NODE.equals(condType.getType())) {
                ctd.addMsg("Found unexpected condition type " + condType.getType() + " with value " + condType.getValue());   
                return null;
            }
            List<ConditionValue> cvl = condType.getValue();
            if (null == cvl || 1 != cvl.size()) {
                ctd.addMsg("Condition value is null or has more than 1 entry for " + condType.getType());
                return null;
            }
            String nodeId = cvl.get(0).getValue();
            GeneNode gn = pm.getGeneByPTNId(nodeId);
            if (null == gn) {
                ctd.addMsg("Unable to find " + nodeId);
                return null;
            } 
            nodeToIsNeg.put(gn.getNode(), condType.isNegative());
        }
        
        // Find main condition node
        ArrayList<Node> allNodes = new ArrayList(nodeToIsNeg.keySet());
        Node mainConditionNode = getAncestor(allNodes);
        if (null == mainConditionNode) {
            ctd.addMsg("Unable to find common ancestor node from list of nodes");
            return null;
        }
        
        // Ensure dependant condition nodes are actually descendants. Also ensure isNegative is opposite of the main condition
        boolean mainIsNegative = nodeToIsNeg.get(mainConditionNode);
        ArrayList<Node> descNodes = new ArrayList<Node>();
        Node.getDescendants(mainConditionNode, descNodes);
        for (Entry<Node, Boolean> entry: nodeToIsNeg.entrySet()) {
            Node cur = entry.getKey();
            if (cur == mainConditionNode) {
                continue;
            }
            if (false == descNodes.contains(cur)) {
                ctd.addMsg("Node " + cur.getStaticInfo().getPublicId() + " is not a descendant of main condition node " + mainConditionNode.getStaticInfo().getPublicId());
                return null;
            }
            boolean curIsNeg = entry.getValue();
            if (curIsNeg == mainIsNegative) {
                ctd.addMsg("Node " + cur.getStaticInfo().getPublicId() + " is negative is " + Boolean.toString(curIsNeg) + " which is same as main condition node " + mainConditionNode.getStaticInfo().getPublicId());                
                return null;
            }
        }
        urag.setMainConditionNode(mainConditionNode);
        urag.setMainCondNegative(mainIsNegative);
        allNodes.remove(mainConditionNode);
        urag.setDependantConditionNodes(allNodes);
        return urag;
    }
    
    // Find node in nodes that is ancestor to nodes
    public static Node getAncestor(ArrayList<Node> nodes) {
        for (Node n: nodes) {
            ArrayList<Node> descList = new ArrayList<Node>();
            Node.getDescendants(n, descList);
            
            boolean descInList = true;
            for (Node comp: nodes) {
                if (comp == n) {
                    continue;
                }
                if (false == descList.contains(comp)) {
                    descInList = false;
                    break;
                }
            }
            if (true == descInList) {
                return n;
            }
        }
        return null;
    }
    
//    public static boolean removeValidRulesFromAnnotations(AnnotationsType at, ArrayList<Rule> rules) {
//        if (null == at) {
//            return false;
//        }
//        
//        if (null == rules || 0 == rules.size()) {
//            return true;
//        }
//        
//        List<AnnotationType> atl = at.getAnnotation();
//        if (null == atl) {
//            return false;
//        }
//        
//        for (Rule r: rules) {
//            String ruleLabel = r.getLabel();
//            String ruleValue = r.getValue();
//            for (AnnotationType annotType: atl) {
//                // Keyword, go, gene 
//                if (Rule.PROPERTY_SPKW.equals(ruleLabel)) {
//                    KeywordType kt = annotType.getKeyword();
//                    if (null != kt) {
//                        KeywordTransformer keywordTransformer = new KeywordTransformer();
//                        String value = keywordTransformer.fromXml(kt);
//                        if (value != null && value.equals(ruleValue)) {
//                            annotType.setKeyword(null);
//                            break;
//                        }
//                    }
//                }
//                else if (Rule.PROPERTY_DRGO.equals(ruleLabel)) {
//                    DbReferenceType dbRef = annotType.getDbReference();
//                    if (null != dbRef) {
//                        String value = dbRef.getId();
//                        if (value != null && value.equals(ruleValue)) {
//                            annotType.setDbReference(null);
//                            break;
//                        }
//                    }
//                }
//                else if (Rule.PROPERTY_GNNM.equals(ruleLabel)) {
//                    GeneType gt = annotType.getGene();
//                    if (null != gt) {
//                        List<GeneNameType> gntl = gt.getName();
//                        if (null != gntl) {
//                            for (Iterator<GeneNameType> gntIter = gntl.iterator(); gntIter.hasNext(); ) { 
//                                GeneNameType gnt = gntIter.next();
//                                String type = gnt.getType();
//                                String value = gnt.getValue();
//                                if (GENE_NAME_TYPE_PRIMARY.equals(type)) {
//                                    if (null != value && value.equals(ruleValue)) {
//                                        gntIter.remove();
//                                    }
//                                }
//                            }
//                        }
//                        if (gntl.isEmpty()) {
//                            annotType.setGene(null);
//                        }
//                    }
//                }
//                else if (Rule.PROPERTY_GNSY.equals(ruleLabel)) {
//                    GeneType gt = annotType.getGene();
//                    if (null != gt) {
//                        List<GeneNameType> gntl = gt.getName();
//                        if (null != gntl) {
//                            for (Iterator<GeneNameType> gntIter = gntl.iterator(); gntIter.hasNext(); ) { 
//                                GeneNameType gnt = gntIter.next();
//                                String type = gnt.getType();
//                                String value = gnt.getValue();
//                                if (GENE_NAME_TYPE_SYNONYM.equals(type)) {
//                                    if (null != value && value.equals(ruleValue)) {
//                                        gntIter.remove();
//                                    }
//                                }
//                            }
//                        }
//                        if (gntl.isEmpty()) {
//                            annotType.setGene(null);
//                        }
//                    }
//                }
//                // ProteinType
//                else if (Rule.PROPERTY_DERF.equals(ruleLabel)) {
//                    ProteinType pt = annotType.getProtein();
//                    if (null != pt) {
//                        ProteinType.RecommendedName rn = pt.getRecommendedName();
//                        if (null != rn) {
//                            EvidencedStringType fullName = rn.getFullName();
//                            if (null != fullName && null != fullName.getValue()) {
//                                if (fullName.equals(ruleValue)) {
//                                    rn.setFullName(null);
//                                }
//                            }
//                        }
//                    }
//                }
//                else if (Rule.PROPERTY_DERS.equals(ruleLabel)) {
//                    ProteinType pt = annotType.getProtein();
//                    if (null != pt) {
//                        ProteinType.RecommendedName rn = pt.getRecommendedName();
//                        if (null != rn) {
//                            List<EvidencedStringType> shortNameList = rn.getShortName();
//                            if (null != shortNameList) {
//                                for (Iterator<EvidencedStringType> shortNameIter = shortNameList.iterator(); shortNameIter.hasNext();) {
//                                    EvidencedStringType shortName = shortNameIter.next();
//                                    if (null != shortName.getValue() && shortName.getValue().equals(ruleValue)) {
//                                        shortNameIter.remove();
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                else if (Rule.PROPERTY_DEEC.equals(ruleLabel)) {
//                    ProteinType pt = annotType.getProtein();
//                    if (null != pt) {
//                        ProteinType.RecommendedName rn = pt.getRecommendedName();
//                        if (null != rn) {
//                            List<EvidencedStringType> ecNumberList = rn.getEcNumber();
//                            if (null != ecNumberList) {
//                                for (Iterator<EvidencedStringType> ecNumberIter = ecNumberList.iterator(); ecNumberIter.hasNext(); ) {
//                                    EvidencedStringType ecNumber = ecNumberIter.next();
//                                    if (null != ecNumber.getValue() && ecNumber.getValue().equals(ruleValue)) {
//                                        ecNumberIter.remove();
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                else if (Rule.PROPERTY_DEAF.equals(ruleLabel)) {
//                    ProteinType pt = annotType.getProtein();
//                    if (null != pt) {
//                        List<ProteinType.AlternativeName> alternateNameList = pt.getAlternativeName();
//                        if (null != alternateNameList) {
//                            for (Iterator<ProteinType.AlternativeName>  alternateNameIter = alternateNameList.iterator();  alternateNameIter.hasNext();) {
//                                ProteinType.AlternativeName alternateName = alternateNameIter.next();
//                                EvidencedStringType fullName = alternateName.getFullName();
//                                if (null != fullName && null != fullName.getValue() && fullName.getValue().equals(ruleValue)) {
//                                    alternateNameIter.remove();
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                }
//                // Comment type
//                else if (Rule.PROPERTY_CCCA.equals(ruleLabel)) {
//                    CommentType ct = annotType.getComment();
//                    if (null != ct) {
//                        String type = ct.getType();
//                        if (null != type && COMMENT_TYPE_CATALYTIC_ACTIVITY.equals(type)) {
//                            CatalyticActivityTransformer catalyticActivityTransformer = new CatalyticActivityTransformer();
//                            String converted = catalyticActivityTransformer.xmlToCcca(ct);
//                            if (null != converted && converted.equals(ruleValue)) {
//                                annotType.setComment(null);
//                            }
//                        }
//                    }
//                }
//                else if (Rule.PROPERTY_CCLO.equals(ruleLabel)) {
//                    CommentType ct = annotType.getComment();
//                    if (null != ct) {
//                        String type = ct.getType();
//                        if (null != type && COMMENT_TYPE_SUBCELLULAR_LOCATION.equals(type)) {
//                            SubcellularLocationTransformer subcellularLocationTransformer = new SubcellularLocationTransformer();
//                            String converted = subcellularLocationTransformer.xmlToCclo(ct);
//                            if (null != converted && converted.equals(ruleValue)) {
//                                annotType.setComment(null);
//                            }
//                        }
//                    }
//                }
//                else if (Rule.PROPERTY_CCCO.equals(ruleLabel)) {
//                    CommentType ct = annotType.getComment();
//                    if (null != ct) {
//                        String type = ct.getType();
//                        if (null != type && COMMENT_TYPE_COFACTOR.equals(type)) {
//                            CofactorTransformer cofactorTransformer = new CofactorTransformer();
//                            String converted = cofactorTransformer.fromXml(ct);
//                            if (null != converted && converted.equals(ruleValue)) {
//                                annotType.setComment(null);
//                            }
//                        }
//                    }
//                }
//                else {
//                    CommentType ct = annotType.getComment();
//                    if (null != ct) {
//                        List<EvidencedStringType> estl = ct.getText();
//                        if (null == estl) {
//                            continue;
//                        }
//                        String type = ct.getType();
//                        String label = COMMENT_RULE_LABEL_LOOKUP.get(type);
//                        if (ruleLabel != null && ruleLabel.equals(label)) {
//                            for (Iterator<EvidencedStringType> estIter = estl.iterator();  estIter.hasNext();) {
//                                EvidencedStringType est = estIter.next();
//                                String text = est.getValue();;
//                                if (null != text && text.equals(ruleValue)) {
//                                    estIter.remove();
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                }              
//            }
//        }
//        return true;
//    }
    
    public static ArrayList<Rule> getRulesForAnnotationType(AnnotationType annotType, Boolean annotationErrorsFound) {
        ArrayList<Rule> rules = new ArrayList<Rule>();
        ProteinType pt = annotType.getProtein();
        if (null != pt) {
            ArrayList<Rule> protRules = getProteinRules(pt);
            if (null != protRules) {
                rules.addAll(protRules);
            } else {
                annotationErrorsFound = true;
            }
        }

        KeywordType kt = annotType.getKeyword();
        if (null != kt) {
            KeywordTransformer keywordTransformer = new KeywordTransformer();
            String value = keywordTransformer.fromXml(kt);
            Rule r = new Rule();
            LabelValue lv = new LabelValue();
            lv.setLabel(Rule.PROPERTY_SPKW);
            lv.setValue(value);
            r.setLabelValue(lv);
            rules.add(r);
        }

        DbReferenceType dr = annotType.getDbReference();
        if (null != dr) {
            String type = dr.getType();
            if (DB_REF_TYPE_GO.equals(type)) {
                Rule r = new Rule();
                LabelValue lv = new LabelValue();
                lv.setLabel(Rule.PROPERTY_DRGO);
                lv.setValue(dr.getId());
                r.setLabelValue(lv);
                rules.add(r);
            }
        }

        GeneType gt = annotType.getGene();
        if (null != gt) {
            List<GeneNameType> gntl = gt.getName();
            if (null != gntl) {
                for (GeneNameType gnt : gntl) {
                    String type = gnt.getType();
                    String value = gnt.getValue();
                    if (null != value) {
                        if (GENE_NAME_TYPE_PRIMARY.equals(type)) {
                            Rule r = new Rule();
                            LabelValue lv = new LabelValue();
                            lv.setLabel(Rule.PROPERTY_GNNM);
                            lv.setValue(value);
                            r.setLabelValue(lv);
                            rules.add(r);
                        } else if (GENE_NAME_TYPE_SYNONYM.equals(type)) {
                            Rule r = new Rule();
                            LabelValue lv = new LabelValue();
                            lv.setLabel(Rule.PROPERTY_GNSY);
                            lv.setValue(value);
                            r.setLabelValue(lv);
                            rules.add(r);
                        }
                    }
                }
            }
        }

        CommentType ct = annotType.getComment();
        if (null != ct) {
            ArrayList<Rule> commentRules = getCommentRules(ct);
            if (null != commentRules) {
                rules.addAll(commentRules);
            } else {
                annotationErrorsFound = true;
            }
        }
        return rules;
    }
    
    public static ArrayList<Rule> getRulesForAnnotationsType(AnnotationsType at, Boolean annotationErrorsFound) {
        if (null == at) {
            annotationErrorsFound = true;
            return null;
        }
        List<AnnotationType> atl = at.getAnnotation();
        if (null == atl) {
            annotationErrorsFound = true;
            return null;
        }
        ArrayList<Rule> rules = new ArrayList<Rule>();
        for (AnnotationType annotType : atl) {
            ArrayList<Rule> rList= getRulesForAnnotationType(annotType, annotationErrorsFound);
            if (null != rList) {
                rules.addAll(rList);
            }
        }
        if (rules.isEmpty()) {
            annotationErrorsFound = true;
            return null;
        }

        return rules;
    }
    
    
    public static ArrayList<Rule> getProteinRules(ProteinType pt) {
        if (null == pt) {
            return null;
        }
        ArrayList<Rule> rules = new ArrayList<Rule>();
        ProteinType.RecommendedName rn = pt.getRecommendedName();
        if (null != rn) {
            EvidencedStringType fullName = rn.getFullName();
            if (null != fullName && null != fullName.getValue()) {
                Rule r = new Rule();
                LabelValue lv = new LabelValue();
                lv.setLabel(Rule.PROPERTY_DERF);
                lv.setValue(fullName.getValue());
                r.setLabelValue(lv);
                rules.add(r);
            }

            List<EvidencedStringType> shortNameList = rn.getShortName();
            if (null != shortNameList) {
                for (EvidencedStringType shortName : shortNameList) {
                    if (null != shortName.getValue()) {
                        Rule r = new Rule();
                        LabelValue lv = new LabelValue();
                        lv.setLabel(Rule.PROPERTY_DERS);
                        lv.setValue(shortName.getValue());
                        r.setLabelValue(lv);
                        rules.add(r);
                    }
                }
            }

            List<EvidencedStringType> ecNumberList = rn.getEcNumber();
            if (null != ecNumberList) {
                for (EvidencedStringType ecNumber : ecNumberList) {
                    if (null != ecNumber.getValue()) {
                        Rule r = new Rule();
                        LabelValue lv = new LabelValue();
                        lv.setLabel(Rule.PROPERTY_DEEC);
                        lv.setValue(ecNumber.getValue());
                        r.setLabelValue(lv);
                        rules.add(r);
                    }
                }
            }
        }
        
        List<ProteinType.AlternativeName> alternateNameList = pt.getAlternativeName();
        if (null != alternateNameList) {
            for (ProteinType.AlternativeName alternateName : alternateNameList) {
                EvidencedStringType fullName = alternateName.getFullName();
                if (null != fullName) {
                    Rule r = new Rule();
                    LabelValue lv = new LabelValue();
                    lv.setLabel(Rule.PROPERTY_DEAF);
                    lv.setValue(fullName.getValue());
                    r.setLabelValue(lv);
                    rules.add(r);
                }

                List<EvidencedStringType> ecNumberList = alternateName.getEcNumber();
                if (null != ecNumberList) {
                    for (EvidencedStringType ecNumber : ecNumberList) {
                        Rule r = new Rule();
                        LabelValue lv = new LabelValue();
                        lv.setLabel(Rule.PROPERTY_DEAE);
                        lv.setValue(ecNumber.getValue());
                        r.setLabelValue(lv);
                        rules.add(r);
                    }
                }
            }
        }
        return rules;
    }
    
    public static ArrayList<Rule> getCommentRules(CommentType ct) {
        if (null == ct) {
            return null;
        }
        ArrayList<Rule> rules = new ArrayList<Rule>();
        String type = ct.getType();
        if (COMMENT_TYPE_CATALYTIC_ACTIVITY.equals(type)) {
            CatalyticActivityTransformer catalyticActivityTransformer = new CatalyticActivityTransformer();
            Rule r = new Rule();
            LabelValue lv = new LabelValue();
            lv.setLabel(COMMENT_RULE_LABEL_LOOKUP.get(type));
            lv.setValue(catalyticActivityTransformer.xmlToCcca(ct));
            r.setLabelValue(lv);
            rules.add(r);
        }
        if (COMMENT_TYPE_SUBCELLULAR_LOCATION.equals(type)) {
            SubcellularLocationTransformer subcellularLocationTransformer = new SubcellularLocationTransformer();
            Rule r = new Rule();
            LabelValue lv = new LabelValue();
            lv.setLabel(COMMENT_RULE_LABEL_LOOKUP.get(type));
            lv.setValue(subcellularLocationTransformer.xmlToCclo(ct));
            r.setLabelValue(lv);
            rules.add(r);
        }
        if (COMMENT_TYPE_COFACTOR.equals(type)) {
            CofactorTransformer cofactorTransformer = new CofactorTransformer();
            Rule r = new Rule();
            LabelValue lv = new LabelValue();
            lv.setLabel(COMMENT_RULE_LABEL_LOOKUP.get(type));
            lv.setValue(cofactorTransformer.fromXml(ct));
            r.setLabelValue(lv);
            rules.add(r);
        }

        List<EvidencedStringType> estl = ct.getText();
        if (null != estl) {
            for (EvidencedStringType est : estl) {
                Rule r = new Rule();
                LabelValue lv = new LabelValue();
                lv.setLabel(COMMENT_RULE_LABEL_LOOKUP.get(type));
                lv.setValue(est.getValue());
                r.setLabelValue(lv);
                rules.add(r);
            }
        }
        return rules;
    }
    

    
    public static ArrayList<UniRuleAnnotationGroup> getPAINTRulesForUniRule(UniRuleType uniRule, ArrayList<CaseTypeDetails> errorPaintCases, Hashtable<String, Rule> curatableRules, ArrayList<Rule> newPaintRules, String familyId, Node root, StringBuffer errorBuf) {
        if (null == uniRule) {
            errorBuf.append("No Unirule to parse\n");
            return null;
        }
        System.out.println("Information for Unirule " + uniRule.getId());
        System.out.println("Rule was modified by " + uniRule.getCreator());
        System.out.println("Modified on " + DATE_FORMATTER.format(new Date(uniRule.getModified().toGregorianCalendar().getTimeInMillis())));
        PaintManager pm = PaintManager.inst();
        Family family = pm.getFamily();
        if (null == family) {
            errorBuf.append("Family id is null, unable to cross check with unirule\n");
            return null;
        }
        
        // Main Condition now has information that is applicable to the whole tree.  This has to be saved for the root node
        Boolean containsInfoAboutRoot = false;
        boolean mainValid = getInfoFromMainCondition(uniRule, root.getStaticInfo().getPublicId(), errorBuf, containsInfoAboutRoot);
        MainType mt = uniRule.getMain();
        if (true == mainValid) {
            System.out.println("Found valid main condition");
        } else {
            errorBuf.append("Found invalid main condition\n");
            System.out.println("Found invalid main condition");
        }
        
        // As far as the tree is concerned, main just gets processed the same as everything else.
        List<MainType> mtl = new ArrayList<MainType>();
        if (null != mt) {
            mtl.add(mt);
        }


        CasesType cst = uniRule.getCases();
        if (null != cst) {

            List<CaseType> ctl = cst.getCase();
            if (null != ctl) {
                for (CaseType ct: ctl) {
                    mtl.add(ct);
                }
            }
        }
        System.out.println("There are " + mtl.size() + " cases in the UniRule response including annotations to root node");

        // Get list of applicable cases
        LinkedHashMap<MainType, UniRuleAnnotationGroup> caseToNodeLookup = new LinkedHashMap<MainType, UniRuleAnnotationGroup>();
        for (int i = 0; i < mtl.size(); i++) {
            Boolean annotErrorFound = false;
            MainType ct = mtl.get(i);
            CaseTypeDetails ctd = new CaseTypeDetails();
            ctd.setCaseType(ct);
            HashMap<String, ArrayList<Rule>> ruleExceptionLookup = new HashMap<String, ArrayList<Rule>>();
//            HashMap<Rule, RuleExceptionType> ruleToRuleExceptionTypeLookup = new HashMap<Rule, RuleExceptionType>();
            
            // Get exception rules
            RuleExceptionsType ret = ct.getRuleExceptions();
            if (null != ret) {
                List<RuleExceptionType> retl = ret.getRuleException();
                if (null != retl) {
                    for (RuleExceptionType re: retl) {
                        AnnotationType at = re.getAnnotation();
                        ArrayList<Rule> exceptionRules = getRulesForAnnotationType(at, annotErrorFound);
                        if (null != exceptionRules) {
                            for (Rule eRule: exceptionRules) {
                                eRule.setException(re);
                                ArrayList<Rule> rules = ruleExceptionLookup.get(eRule.getLabel() + eRule.getValue());
                                if (null == rules) {
                                    rules = new ArrayList<Rule>();
                                    ruleExceptionLookup.put(eRule.getLabel() + eRule.getValue(), rules);
                                }
                                rules.add(eRule);
//                                ruleToRuleExceptionTypeLookup.put(exception, re);
                            }
                        }
                    }
                }
            }
            ctd.setCaseNum(Integer.toString(i));

            //HashSet<Rule> errorRules = new HashSet<Rule>();
            // If there are any rules that are not currently in the current set of rules, add these to the new set of rules
            ArrayList<Rule> rules = getRulesForAnnotationsType(ct.getAnnotations(), annotErrorFound);
            if (null != rules) {
                for (Rule r : rules) {
                    LabelValue lv = r.getLabelValue();
                    String label = lv.getLabel();
                    String value = lv.getValue();
                    String standardRuleId = label + value;
                    
                    // First check if this is a rule with exception.   If yes, then do not attempt to use existing rules.  Just create a new rule with exception
                    if (true == ruleExceptionLookup.containsKey(standardRuleId)) {
                        ArrayList<Rule> expRules = ruleExceptionLookup.get(standardRuleId);
                        Rule expRule = expRules.remove(0);
                        if (expRules.isEmpty()) {
                            ruleExceptionLookup.remove(standardRuleId);
                        }
                        expRule.setId(Rule.generateCaseId(i + 1, family.getNextRuleId(), label, true));
                        newPaintRules.add(expRule);
                        rules.set((rules.indexOf(r)), expRule);
                        continue;
                    }
                    
                    // Check if rule can be supported by experimental evidence
                    if (Rule.PAINT_RULES_WITH_POSSIBLE_EXP_EVIDENCE.contains(label)) {
                        // If the rule can only be applied with experimental evidence check if it exists, else create a new one
                        Rule existingRule = curatableRules.get(standardRuleId);
                        if (null == existingRule) {
                            // New rule may have already been added. Check first
                            boolean foundMatch = false;
                            for (Rule newRule : newPaintRules) {
                                if (label.equals(newRule.getLabel()) && value.equals(newRule.getValue())) {
                                    rules.set((rules.indexOf(r)), newRule);
                                    foundMatch = true;
                                    break;
                                }
                            }
                            if (false == foundMatch) {
                                r.setId(Rule.generateCaseId(i + 1, family.getNextRuleId(), label, false));
                                newPaintRules.add(r);
                            }                          
                            
                            
//                            ctd.addMsg("Rule " + label + " - '" + value + "' for case " + i + " - Cannot be created since there are no sequences with supporting experimental evidence");
//                            annotErrorFound = true;
//                            errorRules.add(r);
                        }
                        else {
//                            String id = existingRule.getId();
//                            if (null != id && id.startsWith(Rule.UNIRULE_PREFIX_NEW)) {
//                                existingRule.setId(Rule.generateNextCaseId(id, i + 1));
//                            }
//                            else {
//                                existingRule.setId(Rule.generateCaseId(i + 1, family.getNextRuleId(), label));
//                            }
                            rules.set((rules.indexOf(r)), existingRule);
                        }
                        continue;
                    }
                    // New rule may have already been added. Check first
                    boolean foundMatch = false;
                    for (Rule newRule: newPaintRules) {
                        if (label.equals(newRule.getLabel()) && value.equals(newRule.getValue())) {
                            rules.set((rules.indexOf(r)), newRule);
                            foundMatch = true;
                            break;
                        }
                    }
                    if (false == foundMatch) {
                        r.setId(Rule.generateCaseId(i + 1, family.getNextRuleId(), label, false));
                        newPaintRules.add(r);
                    }
                }
                //rules.removeAll(errorRules);
            }
            else {
                // Sometimes, main type can be empty.  Do not output message for this case
                if (ct != mt && mt != null) {
                    ctd.addMsg("No rules found for case " + i);
                }
                else if (ct == mt && mt != null){
                    continue;
                }
            }

            UniRuleAnnotationGroup urag = getNodes(ct, root, ctd);
            if (null == urag || null == rules || 0 == rules.size() || false == ruleExceptionLookup.isEmpty()) {
                errorPaintCases.add(ctd);
                continue;
            }
            
            // There are some errors with this case, add case to list of cases with problems, however continue processing rest of the information for this case
            // Error cases are written back, when the book is saved. This case has both valid and invalid rules.
            // Do not remove the valid rules from this case, since if a new annotation is not created with the valid rules, these will be lost.
            if (true == annotErrorFound) {
                errorPaintCases.add(ctd);
                //removeValidRulesFromAnnotations(ct.getAnnotations(), rules);
            }
            
            urag.setRules(rules);
            caseToNodeLookup.put(ct, urag);
        }
        System.out.println("There are " + caseToNodeLookup.size() + " cases that are applicable to PAINT with " + newPaintRules.size() + " inferred by curator rules");
        return new ArrayList<UniRuleAnnotationGroup>(caseToNodeLookup.values());

    }
    
    public static HashSet<String> getPosssibleNodes(UniRuleType uniRule) {
        HashSet<String> nodeSet = new HashSet<String>();
        List<MainType> mtl = new ArrayList<MainType>();
        MainType mt = uniRule.getMain();
        if (null != mt) {
            mtl.add(mt);
        }

        CasesType casesType = uniRule.getCases();
        if (null != casesType) {

            List<CaseType> caseTypeList = casesType.getCase();
            if (null != caseTypeList) {
                for (CaseType caseType : caseTypeList) {
                    mtl.add(caseType);
                }
            }
        }
        for (int i = 0; i < mtl.size(); i++) {
            MainType ct = mtl.get(i);
            if (null == ct) {
                continue;
            }
//        if (null != ct.getRuleExceptions()) {
//            ctd.addMsg("Case has rule exceptions information");
//            return null;
//        }
            ConditionSets cs = ct.getConditionSets();
            if (null == cs) {
                continue;
            }
            List<ConditionSetType> cstl = cs.getConditionSet();
            if (null == cstl || 1 < cstl.size()) {
                continue;
            }
            ConditionSetType cst = cstl.get(0);
            List<ConditionType> ctl = cst.getCondition();
            if (null == ctl || 0 == ctl.size()) {
                continue;
            }

            for (ConditionType condType : ctl) {
                if (null != condType.getRange()) {
                    continue;
                }
                if (false == CONDITION_TYPE_PANTHER_TREE_NODE.equals(condType.getType())) {
                    continue;
                }
                List<ConditionValue> cvl = condType.getValue();
                if (null == cvl || 1 != cvl.size()) {
                    continue;
                }
                String nodeId = cvl.get(0).getValue();
                if (null != nodeId) {
                    nodeSet.add(nodeId);
                } else {
                    continue;
                }

            }
        }

        return nodeSet;
    }
    
//    public static ArrayList<UniRuleAnnotationGroup> getNonPAINTRulesForRule(UniRuleType uniRule) {
//        if (null == uniRule) {
//            return new ArrayList<UniRuleAnnotationGroup>();
//        }
//        System.out.println("Information for Unirule " + uniRule.getId());
//        System.out.println("Rule was modified by " + uniRule.getCreator());
//        System.out.println("Modified on " + DATE_FORMATTER.format(new Date(uniRule.getModified().toGregorianCalendar().getTimeInMillis())));
//        
//        ArrayList<UniRuleAnnotationGroup> groups = new ArrayList<UniRuleAnnotationGroup>();
//        ArrayList<UniRuleAnnotationGroup> otherGroups = new ArrayList<UniRuleAnnotationGroup>();
//        MainType mt = uniRule.getMain();
//        if (null != mt) {
//            System.out.println("Information for main condition - Parsing other information");
//            ConditionSets cs = mt.getConditionSets();
//            if (null != cs) {
//                List<ConditionSetType> cstList = cs.getConditionSet();
//                if (null != cstList) {
//                    for (int i = 0; i < cstList.size(); i++) {
//                        ConditionSetType cst = cstList.get(i);
//                        List<ConditionType> csList = cst.getCondition();
//                        if (null != csList) {
//                            for (int j = 0; j < csList.size(); j++) {
//                                ConditionType ct = csList.get(j);
//                                List<ConditionValue> cvl = ct.getValue();
//                                if (null != ct && null != cvl) {
//                                    for (int k = 0; k < cvl.size(); k++) {
//                                        ConditionValue cv = cvl.get(k);
//                                        System.out.println("Type - " + ct.getType() + " value " + cv.getValue());
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            CasesType cst = uniRule.getCases();
//            if (null != cst) {
//                List<CaseType> ctl = cst.getCase();
//                if (null != ctl) {
//                    System.out.println("There are " + ctl.size() + " groups");
//                    for (int i = 0; i < ctl.size(); i++) {
//                        CaseType ct = ctl.get(i);
//                        ConditionSets condSets = ct.getConditionSets();
//                        if (null == condSets) {
//                            System.out.println("Skipping null condition set");
//                            continue;
//                        }
//                        List<ConditionSetType> cstl = condSets.getConditionSet();
//                        if (null == cstl || 0 == cstl.size()) {
//                            System.out.println("Skipping null condition set");
//                            continue;
//                        }
//                        UniRuleAnnotationGroup uag = new UniRuleAnnotationGroup();
//                        groups.add(uag);
//                        UniRuleAnnotationGroup otherGroup = new UniRuleAnnotationGroup();
//                        otherGroups.add(otherGroup);
//                        for (int j = 0; j < cstl.size(); j++) {
//                            ConditionSetType condSetType = cstl.get(j);
//                            List<ConditionType> condTypeList = condSetType.getCondition();
//                            if (null == condTypeList || 0 == condTypeList.size()) {
//                                groups.remove(uag);
//                                otherGroups.remove(otherGroup);
//                                System.out.println("Skipping null condition set");
//                                continue;                                
//                            }
//
//                            for (int k = 0; k < condTypeList.size(); k++) {
//                                ConditionType condType  = condTypeList.get(k);
//                                List<ConditionValue> valueList = condType.getValue();
//                                if (null != valueList && 0 != valueList.size() && condType != null && LABEL_CONDITION_TREE_NODE.equals(condType.getType())) {
//                                    boolean isNeg = condType.isNegative();
//                                    if (0 == k) {
//                                        uag.setNode1(condType.getValue().get(0).getValue());
//                                    }
//                                    else {
//                                        uag.setNode2(condType.getValue().get(0).getValue());
//                                    }
//                                    if (false == isNeg) {
//                                        System.out.println("True condition set " + condType.getType() + " value " + condType.getValue().get(0).getValue());
//                                    }
//                                    else {
//                                        System.out.println("False condition set " + condType.getType() + " value " + condType.getValue().get(0).getValue());
//                                        uag.setNegConditionNode(condType.getValue().get(0).getValue());
//                                    }
//                                }
//                                else {
//                                    otherGroup.addOtherConditionType(condType);
//                                }
//                            }
//                            otherGroup.setNode1(uag.getNode1());
//                            otherGroup.setNode2(uag.getNode2());
//                            otherGroup.setNegConditionNode(uag.getNegConditionNode());
//                        }
//                        
//                        
//                        AnnotationsType ats = ct.getAnnotations();
//                        if (null == ats) {
//                            System.out.println("Skipping null annotation type");
//                            groups.remove(uag);
//                            continue;
//                        }
//
//                        List<AnnotationType> atl = ats.getAnnotation();
//                        if (null == atl || 0 == atl.size()) {
//                            System.out.println("Skipping null annotation set");
//                            groups.remove(uag);                            
//                            continue;
//                        }
//                        for (int j = 0; j < atl.size(); j++) {
//                            AnnotationType at = atl.get(j);
//                            ProteinType pt = at.getProtein();
//                            if (null != pt) {
//                                // ProteinType - 4 types of rules for these
//                                ProteinType.RecommendedName rn = pt.getRecommendedName();
//                                if (null == rn ||
//                                   (null != rn && ((null == rn.getFullName() || (null != rn.getFullName()) && null == rn.getFullName().getValue()) && 
//                                                  ((null == rn.getEcNumber() || (null != rn.getEcNumber()) && 0 == rn.getEcNumber().size()))))) {
//                                    // Alternate name - 2 types of rules for these
//                                    List<ProteinType.AlternativeName> anl = pt.getAlternativeName();
//                                    if (null == anl) {
//                                        groups.remove(uag); 
//                                        System.out.println("Skipping null annotation type, no protein type or comment type found");
//                                        continue;
//                                    }
//                                    else if (anl.size() != 1) {
//                                        groups.remove(uag); 
//                                        System.out.println("Skipping annotation type, did not find exactly 1 alternative name, found " + anl.size());
//                                        continue;                                        
//                                    }
//                                    ProteinType.AlternativeName an = anl.get(0);
//                                    EvidencedStringType fullName = an.getFullName();
//                                    if (null == fullName) {
//                                        List<EvidencedStringType> estl = an.getEcNumber();
//                                        if (null == estl) {
//                                            groups.remove(uag); 
//                                            System.out.println("Skipping null alternative name, no EC number found");
//                                            continue;
//                                        }
//                                        else if (estl.size() != 1) {
//                                            groups.remove(uag); 
//                                            System.out.println("Skipping null alternative name, did not find exactly 1 EC number, found " + estl.size());
//                                            continue;
//                                        }
//                                        String ecNum = estl.get(0).getValue();
//                                        System.out.println("Found rule type " + Rule.PROPERTY_DEAE + " with value " + ecNum);
//                                        Rule r = new Rule();
//                                        LabelValue lv = new LabelValue();
//                                        lv.setLabel(Rule.PROPERTY_DEAE);
//                                        lv.setValue(ecNum);
//                                        r.addProperty(Rule.PROPERTY_DEAE, lv);
//                                        uag.addUnirule(r);
//                                        continue;
//                                    }
//                                    
//                                    // Full name
//                                    String altName = fullName.getValue();
//                                    System.out.println("Found rule type " + Rule.PROPERTY_DEAF + " with value " + altName);
//                                        Rule r = new Rule();
//                                        LabelValue lv = new LabelValue();
//                                        lv.setLabel(Rule.PROPERTY_DEAF);
//                                        lv.setValue(altName);
//                                        r.addProperty(Rule.PROPERTY_DEAF, lv);
//                                        uag.addUnirule(r);                             
//                                        continue;                                   
//                                }
//                                // Recommended Name
//                                EvidencedStringType fullName = rn.getFullName();
//                                if (null == fullName) {
//                                    List<EvidencedStringType> ecl = rn.getEcNumber();
//                                    if (null == ecl) {
//                                        groups.remove(uag); 
//                                        System.out.println("Skipping null full name, no EC number list found");
//                                        continue;
//                                    }
//                                    else if (ecl.size() != 1) {
//                                        groups.remove(uag); 
//                                        System.out.println("Skipping null full name, did not find exactly 1 EC number, found " + ecl.size());
//                                        continue;
//                                    }
//                                    String recommendedEc = ecl.get(0).getValue();
//                                    System.out.println("Found rule type " + Rule.PROPERTY_DEEC + " with value " + recommendedEc);
//                                    Rule r = new Rule();
//                                    LabelValue lv = new LabelValue();
//                                    lv.setLabel(Rule.PROPERTY_DEEC);
//                                    lv.setValue(recommendedEc);
//                                    r.addProperty(Rule.PROPERTY_DEEC, lv);
//                                    uag.addUnirule(r);
//                                    continue;
//                                }
//                                String fullNameValue = fullName.getValue();
//                                System.out.println("Found rule type " + Rule.PROPERTY_DERF + " with value " + fullNameValue);
//                                Rule r = new Rule();
//                                LabelValue lv = new LabelValue();
//                                lv.setLabel(Rule.PROPERTY_DERF);
//                                lv.setValue(fullNameValue);
//                                r.addProperty(Rule.PROPERTY_DERF, lv);
//                                uag.addUnirule(r);
//                                continue;                               
//                            }
//                            else {
//                                // OR CommentType - 2 types
//                                CommentType commentType = at.getComment();
//                                if (null == commentType) {
//                                    groups.remove(uag); 
//                                    System.out.println("Skipping null annotation type, no protein type or comment type found");
//                                    continue;                                    
//                                }
//                                String type = commentType.getType();
//                                String value = null;
//                                String label = null;
//                                if (LABEL_COMMENT_FUNCTION.equals(type)) {
//                                    label = Rule.PROPERTY_CCFU;
//                                    value = commentType.getText().get(0).getValue();
//                                }
//                                else {
//                                    label = Rule.PROPERTY_CCCA;
//                                    CatalyticActivityTransformer cct = new CatalyticActivityTransformer();
//                                    value = cct.xmlToCcca(commentType);
//                                }
//                                System.out.println("Found rule type " + label + " with value " + value);
//                                value = convertTextFromXml(value);
//                                Rule r = new Rule();
//                                LabelValue lv = new LabelValue();
//                                lv.setLabel(label);
//                                lv.setValue(value);
//                                r.addProperty(label, lv);
//                                uag.addUnirule(r);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return  groups;
//    }
    
    /*
    // Remove period from end of text comments - Due to UniProt processing
    */
    public static String convertTextFromXml(String xmlText) {
        if (null == xmlText) {
            return xmlText;
        }
        return xmlText.endsWith(".") ? xmlText.substring(0, xmlText.length()- 1) : xmlText;
    }

    
    public static String getUniruleInXMLFormat(RuleStatusType status, StringBuffer errorBuf) throws Exception {
        ArrayList<UniRuleAnnotationGroup> uagList = AnnotationAdapter.groupUAnnotations();

        PaintManager pm = PaintManager.inst();
        String userId = pm.getUser().getUserId();
        Family family = pm.getFamily();
        UniRuleType uniRule = UniruleSoapHelper.getRule(family.getFamilyID(), userId, uagList, family.getUniRuleCommentExternal(), family.getUniRuleCommentInternal(), status, errorBuf);
        return convertUniruleToXML(uniRule);
    }
    
    public static String convertUniruleToXML(UniRuleType uniRule) throws Exception {
        if (null == uniRule) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        UniRuleXmlWriter uniRuleXmlWriter = new UniRuleXmlWriter();
        XMLStreamWriter xmlStreamWriter = uniRuleXmlWriter.writeUniRulesHeaderTag(out);
        uniRuleXmlWriter.writeUniRule(uniRule, xmlStreamWriter);
        uniRuleXmlWriter.writeUniRulesFooterTag(xmlStreamWriter);

        out.flush();
        String uniRuleXml = out.toString(StandardCharsets.UTF_8);
        out.reset();
        return uniRuleXml;
    }
    

    public static String lockUnlockBooks(ArrayList<String> lockBookList, ArrayList<String> unlockBookList) {
        StringBuffer sb = new StringBuffer();
        UniRuleImporterService_Service service = null;
        try {
            service = new UniRuleImporterService_Service(new URL(Preferences.inst().getUniprotURL()));
//            service = new UniRuleImporterService_Service(
//                    new URL("https://wwwdev.ebi.ac.uk/uniprot/unirule/import/import?wsdl"));
////                                    new URL("http://localhost:8081/uniprot/unirule/import/import?wsdl"));
            ArrayList<Object> info = PaintManager.inst().getUserInfo();
            if (null == info || 2 != info.size()) {
                return null;
            }
            
            CredentialsProvider credentialsProvider = new CredentialsProvider((String)info.get(0), String.copyValueOf((char[]) info.get(1)));
            SecurityHeaderAppender securityHeaderAppender = new SecurityHeaderAppender();
            securityHeaderAppender.addSecurityHeader(service, credentialsProvider);
            UniRuleImporterService uniruleImporter = service.getUniRuleImporter();
            if (null != lockBookList) {
                for (String b: lockBookList) {
                    boolean isLocked = uniruleImporter.lockRule(b);
                    if (true != isLocked) {
                        sb.append(MSG_ERR_UNABLE_TO_LOCK_BOOK + b + Constant.STR_HTML_LINEBREAK);
                    }
                }
            }
            if (null != unlockBookList) {
                for (String b: unlockBookList) {
                    boolean isUnlocked = uniruleImporter.unlockRule(b);
                    if (true != isUnlocked) {
                        sb.append(MSG_ERR_UNABLE_TO_UNLOCK_BOOK + b + Constant.STR_HTML_LINEBREAK);
                    }
                }                
            }
            if (0 != sb.length()) {
                return Constant.STR_HTML_START + sb.toString() + Constant.STR_HTML_END;
            }
            return null;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return MSG_LOCKING_UNLOCKING_OPERATION_FAILED;
        } catch(UniRuleException ue) {
            ue.printStackTrace();
            return MSG_LOCKING_UNLOCKING_OPERATION_FAILED;
        } catch(Exception e) {
            e.printStackTrace();
            return MSG_LOCKING_UNLOCKING_OPERATION_FAILED;
        }        
    }
    public static List<PantherRuleSummary> getSummariesTest() {
        ArrayList<PantherRuleSummary> summaryList = new ArrayList<PantherRuleSummary>(5);
        PantherRuleSummary PTHR10000 = new PantherRuleSummary();
        PTHR10000.setPantherId("PTHR10000");
        PTHR10000.setStatus(RuleStatusType.TEST.toString());
        PTHR10000.setModifiedBy("paint");

        PantherRuleSummary PTHR10003 = new PantherRuleSummary();
        PTHR10003.setPantherId("PTHR10003");
        PTHR10003.setStatus(RuleStatusType.TEST.toString());
        PTHR10003.setLockedBy("paint");
        PTHR10003.setModifiedBy("paint");

        PantherRuleSummary PTHR10005 = new PantherRuleSummary();
        PTHR10005.setPantherId("PTHR10005");
        PTHR10005.setStatus(RuleStatusType.APPLY.toString());
        PTHR10005.setModifiedBy("paint");

        PantherRuleSummary PTHR10006 = new PantherRuleSummary();
        PTHR10006.setPantherId("PTHR10006");
        PTHR10006.setStatus(RuleStatusType.DISUSED.toString());
        PTHR10006.setLockedBy("paint");
        PTHR10006.setModifiedBy("paint");

        PantherRuleSummary PTHR10009 = new PantherRuleSummary();
        PTHR10009.setPantherId("PTHR10009");
        PTHR10009.setStatus(RuleStatusType.APPLY.toString());
        PTHR10009.setLockedBy("anotherUser");
        PTHR10009.setModifiedBy("paint");        
        
        summaryList.add(PTHR10000);
        summaryList.add(PTHR10003);
        summaryList.add(PTHR10005);
        summaryList.add(PTHR10006);
        summaryList.add(PTHR10009);        
        return summaryList;
    }
    
    public static List<PantherRuleSummary> getSummaries(String userName, String password) {

        UniRuleImporterService_Service service = null;
        try {
            service = new UniRuleImporterService_Service(new URL(Preferences.inst().getUniprotURL()));
//            service = new UniRuleImporterService_Service(
//                    new URL("https://wwwdev.ebi.ac.uk/uniprot/unirule/import/import?wsdl"));
////                                    new URL("http://localhost:8081/uniprot/unirule/import/import?wsdl"));
//            ArrayList<Object> info = PaintManager.inst().getUserInfo();
//            if (null == info || 2 != info.size()) {
//                return null;
//            }

            //CredentialsProvider credentialsProvider = new CredentialsProvider((String) info.get(0), String.copyValueOf((char[]) info.get(1)));
            CredentialsProvider credentialsProvider = new CredentialsProvider(userName, password);
            SecurityHeaderAppender securityHeaderAppender = new SecurityHeaderAppender();
            securityHeaderAppender.addSecurityHeader(service, credentialsProvider);
            UniRuleImporterService uniruleImporter = service.getUniRuleImporter();
            return uniruleImporter.retrievePantherRuleSummaries(true);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (UniRuleException ue) {
            ue.printStackTrace();
            return null;
        }
    }
    
    public static int getCurationStatusIdForStatusString(String s) {
        if (STATUS_APPLY.equalsIgnoreCase(s)) {
            return Book.CURATION_STATUS_APPLY;
        }
        else if (STATUS_DISUSED.equalsIgnoreCase(s)) {
            return Book.CURATION_STATUS_DISUSED;
        }
        else if (STATUS_TEST.equalsIgnoreCase(s)) {
            return Book.CURATION_STATUS_TEST;
        }
        return Book.CURATION_STATUS_UNKNOWN;
    }
    


    public static boolean userHasPrivilegeToUpdateBook(User user, String book, StringBuffer errBuf) {
        errBuf.setLength(0);
        if (null == book || null == user) {
            errBuf.append(MSG_ERR_LOGIN_NOT_FOUND);
            return false;
        }
        String loginName = user.getLoginName();
        if (null == loginName) {
            errBuf.append(MSG_ERR_LOGIN_NOT_FOUND);            
            return false;
        }
        
        if (true != User.privToLockOrUpdateBook(user)) {
            errBuf.append(MSG_ERR_USER_ROLE_NO_PRIVILEGE_TO_SAVE);
            return false;
        }

        // If no existing rules for book, then user can save book, Since we have already checked that user has privilege to save
        PaintManager pm = PaintManager.inst();
        ArrayList<Object> info = pm.getUserInfo();
        if (null == info || 2 != info.size()) {
            return false;
        }

        List<PantherRuleSummary> summaries = getSummaries((String) info.get(0), String.copyValueOf((char[]) info.get(1)));
        if (null == summaries) {
            return true;
        }
        
        for (PantherRuleSummary summary: summaries) {
            if (false == book.equals(summary.getPantherId())) {
                continue;
            }
            boolean hasPrivilege = User.privToLockOrUpdateBookWithStatus(user, getCurationStatusIdForStatusString(summary.getStatus()), summary.getModifiedBy());
            if (false == hasPrivilege) {
                errBuf.append(MSG_ERR_USER_NO_PRIVILEGE_TO_SAVE); 
                return false;
            }
            // Unless Admin, ensure book is locked by user
            String role = user.getRole();
            if (false == User.ROLE_ADMIN.equals(role)) {
                if (null == summary.getLockedBy() || false == loginName.equals(summary.getLockedBy())) {
                    errBuf.append(MSG_ERR_BOOK_NOT_LOCKED_FOR_USER); 
                    return false;
                }
            }
        }
        return true;
    }
    
    public static ArrayList<Book> getStatusOfAllBooks() {
        ArrayList<Book> rtnList = new ArrayList<Book>();
        PaintManager pm = PaintManager.inst();
        ArrayList<Object> info = pm.getUserInfo();
        if (null == info || 2 != info.size()) {
            return null;
        }

        List<PantherRuleSummary> result = getSummaries((String) info.get(0), String.copyValueOf((char[]) info.get(1)));
        User currentUser = pm.getUser();
        for (PantherRuleSummary summary : result) {
            User u = null;
            int status = getCurationStatusIdForStatusString(summary.getStatus());

            String lockedBy = summary.getLockedBy();
            if (null != lockedBy && 0 != lockedBy.length()) {
                if (null != currentUser && currentUser.getLoginName().equals(lockedBy)) {
                    u = currentUser;
                } else {
                    u = u = new User(null, null, null, lockedBy, Constant.USER_PRIVILEGE_NOT_SET, null);
                    u.setRole(User.ROLE_UNKNOWN);
                }
            }
            Book b = new Book(summary.getPantherId(), null, status, u);
            String modifiedBy = summary.getModifiedBy();
            if (null != modifiedBy && 0 != modifiedBy.length()) {
                User lastModifiedBy = null;
                if (null != currentUser && currentUser.getLoginName().equals(modifiedBy)) {
                    lastModifiedBy = currentUser;
                } else {
                    lastModifiedBy = new User(null, null, null, modifiedBy, Constant.USER_PRIVILEGE_NOT_SET, null);
                    lastModifiedBy.setRole(User.ROLE_UNKNOWN);
                }

                b.setModifiedBy(lastModifiedBy);
            }
//                System.out.println("PantherID: " + summary.getPantherId() + " - Status: " + summary.getStatus() + " -" +
//                        " lockedBy: " + summary.getLockedBy());
            rtnList.add(b);
        }
        return rtnList;
    }
    
    public static ArrayList<Book> getStatusOfAllBooksOld() {
        ArrayList<Book> rtnList = new ArrayList<Book>();
        UniRuleImporterService_Service service = null;
        try {
            service = new UniRuleImporterService_Service(new URL(Preferences.inst().getUniprotURL()));
//            service = new UniRuleImporterService_Service(
//                    new URL("https://wwwdev.ebi.ac.uk/uniprot/unirule/import/import?wsdl"));
////                                    new URL("http://localhost:8081/uniprot/unirule/import/import?wsdl"));
            PaintManager pm = PaintManager.inst();
            ArrayList<Object> info = pm.getUserInfo();
            if (null == info || 2 != info.size()) {
                return null;
            }
            User currentUser = pm.getUser();
            
            CredentialsProvider credentialsProvider = new CredentialsProvider((String)info.get(0), String.copyValueOf((char[]) info.get(1)));
            SecurityHeaderAppender securityHeaderAppender = new SecurityHeaderAppender();
            securityHeaderAppender.addSecurityHeader(service, credentialsProvider);
            UniRuleImporterService uniruleImporter = service.getUniRuleImporter();
            List<PantherRuleSummary> result = uniruleImporter.retrievePantherRuleSummaries(true);

//            System.out.println("Number of summaries: ");
            for (PantherRuleSummary summary : result) {
                User u = null;
                int status = getCurationStatusIdForStatusString(summary.getStatus());
                
                String lockedBy = summary.getLockedBy();
                if (null != lockedBy && 0 != lockedBy.length()) {
                    if (null != currentUser && currentUser.getLoginName().equals(lockedBy)) {
                        u = currentUser;
                    }
                    else {
                        u = u = new User(null, null, null, lockedBy, Constant.USER_PRIVILEGE_NOT_SET, null);
                        u.setRole(User.ROLE_UNKNOWN);
                    }
                }
                Book b = new Book(summary.getPantherId(), null, status, u);
                
                String modifiedBy = summary.getModifiedBy();
                if (null != modifiedBy && 0 != modifiedBy.length()) {
                    User lastModifiedBy = null;
                    if (null != currentUser && currentUser.getLoginName().equals(modifiedBy)) {
                        lastModifiedBy = currentUser;
                    }
                    else {
                        lastModifiedBy = new User(null, null, null, modifiedBy, Constant.USER_PRIVILEGE_NOT_SET, null);
                        lastModifiedBy.setRole(User.ROLE_UNKNOWN);
                    }
                    
                    b.setModifiedBy(lastModifiedBy);
                }
//                System.out.println("PantherID: " + summary.getPantherId() + " - Status: " + summary.getStatus() + " -" +
//                        " lockedBy: " + summary.getLockedBy());
                rtnList.add(b);
            }
            return rtnList;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch(UniRuleException ue) {
            ue.printStackTrace();
            return null;
        }
    
    }

}


//        ConditionType mainCondition = uniRule.getMain().getConditionSets().getConditionSet().get(0).getCondition().get(0);
//        System.out.println(String.format("Condition in main: %s - %s", mainCondition.getType(), mainCondition.getValue().get(0).getValue()));
//
//        ConditionType caseCondition1 = uniRule.getCases().getCase().get(0).getConditionSets().getConditionSet().get(0).getCondition().get(0);
//        AnnotationType caseAnnotation1 = uniRule.getCases().getCase().get(0).getAnnotations().getAnnotation().get(1);
//        System.out.println(String.format("Condition in main: %s - %s", caseCondition1.getType(), caseCondition1.getValue().get(0).getValue()));
//        System.out.println(String.format("Annotation text in case %s", caseAnnotation1.getComment().getReaction().getText()));
//
//        AnnotationType annotationType1 = uniRule.getCases().getCase().get(0).getAnnotations().getAnnotation().get(0);
//        System.out.println();
//        System.out.println(String.format("First Annotation in case1 %s", annotationType1.getProtein().getRecommendedName().getFullName().getValue()));
        
        
        
//                List<CaseType> ctl = cst.getCase();
//                if (null == ctl) {
//                    return null;
//                }
//                    System.out.println("There are " + ctl.size() + " cases");
//                    for (int i = 0; i < ctl.size(); i++) {
//                        System.out.println("Processing case " + (i + 1));
//                        CaseType ct = ctl.get(i);
//                        ConditionSets condSets = ct.getConditionSets();
//                        if (null == condSets) {
//                            System.out.println("Skipping null condition set");
//                            continue;
//                        }
//                        List<ConditionSetType> cstl = condSets.getConditionSet();
//                        if (null == cstl || 0 == cstl.size()) {
//                            System.out.println("Skipping null condition set");
//                            continue;
//                        }
//                        UniRuleAnnotationGroup uag = new UniRuleAnnotationGroup();
//                        groups.add(uag);                       
//                        for (int j = 0; j < cstl.size(); j++) {
//                            ConditionSetType condSetType = cstl.get(j);
//                            List<ConditionType> condTypeList = condSetType.getCondition();
//                            if (null == condTypeList || 0 == condTypeList.size()) {
//                                System.out.println("Skipping null condition set");
//                                continue;                                
//                            }
//
//                            for (int k = 0; k < condTypeList.size(); k++) {
//                                ConditionType condType  = condTypeList.get(k);
//                                boolean isNeg = condType.isNegative();
//                                if (false == isNeg) {
//                                    uag.addPositiveCondNode(condType.getValue().get(0).getValue());
//                                }
//                                else {
//                                    uag.addNegativeCondNode(condType.getValue().get(0).getValue());
//                                }
//                                if (false == isNeg) {
//                                    System.out.println("True condition set " + condType.getType() + " value " + condType.getValue().get(0).getValue());
//                                }
//                                else {
//                                    System.out.println("False condition set " + condType.getType() + " value " + condType.getValue().get(0).getValue());
//                                }
//                            }
//                        }

//        for (Entry<CaseType, AnnotatedNodesForCase> caseAnnotInfo: caseToNodeLookup.entrySet()) {
//            UniRuleAnnotationGroup uag = new UniRuleAnnotationGroup();
//        }
//
//        
//            
//        HashMap<CaseType, ArrayList<Rule>> caseToRulesLookup = new HashMap<CaseType, ArrayList<Rule>>();
//        for (Entry<CaseType, AnnotatedNodesForCase> entry : caseToNodeLookup.entrySet()) {
//            CaseType ct = entry.getKey();
//            AnnotationsType at = ct.getAnnotations();
//            ArrayList<Rule> rules = getRulesForAnnotation(at);
//            if (null != rules) {
//                caseToRulesLookup.put(ct, rules);
//            }
//        }
//        if (null != cst) {
//            if (null != ctl) {
//                System.out.println("There are " + ctl.size() + " cases");
//                for (int i = 0; i < ctl.size(); i++) {
//                    System.out.println("Processing case " + (i + 1));
//                    CaseType ct = ctl.get(i);
//                    ConditionSets condSets = ct.getConditionSets();
//                    if (null == condSets) {
//                        System.out.println("Skipping null condition set");
//                        continue;
//                    }
//                    List<ConditionSetType> cstl = condSets.getConditionSet();
//                    if (null == cstl || 0 == cstl.size()) {
//                        System.out.println("Skipping null condition set");
//                        continue;
//                    }
//                    UniRuleAnnotationGroup uag = new UniRuleAnnotationGroup();
//                    groups.add(uag);
//                    for (int j = 0; j < cstl.size(); j++) {
//                        ConditionSetType condSetType = cstl.get(j);
//                        List<ConditionType> condTypeList = condSetType.getCondition();
//                        if (null == condTypeList || 0 == condTypeList.size()) {
//                            System.out.println("Skipping null condition set");
//                            continue;
//                        }
//
//                        for (int k = 0; k < condTypeList.size(); k++) {
//                            ConditionType condType = condTypeList.get(k);
//                            boolean isNeg = condType.isNegative();
//                            if (false == isNeg) {
//                                uag.addPositiveCondNode(condType.getValue().get(0).getValue());
//                            } else {
//                                uag.addNegativeCondNode(condType.getValue().get(0).getValue());
//                            }
//                            if (false == isNeg) {
//                                System.out.println("True condition set " + condType.getType() + " value " + condType.getValue().get(0).getValue());
//                            } else {
//                                System.out.println("False condition set " + condType.getType() + " value " + condType.getValue().get(0).getValue());
//                            }
//                        }
//                    }
//                    AnnotationsType ats = ct.getAnnotations();
//                    if (null == ats) {
//                        System.out.println("Skipping null annotation set");
//                        groups.remove(uag);
//                        continue;
//                    }
//
//                    List<AnnotationType> atl = ats.getAnnotation();
//                    if (null == atl || 0 == atl.size()) {
//                        System.out.println("Skipping null annotation set");
//                        groups.remove(uag);
//                        continue;
//                    }
//                    for (int j = 0; j < atl.size(); j++) {
//                        AnnotationType at = atl.get(j);
//                        ProteinType pt = at.getProtein();
//                        if (null != pt) {
//                            // ProteinType - 4 types of rules for these
//                            ProteinType.RecommendedName rn = pt.getRecommendedName();
//                            if (null == rn
//                                    || (null != rn && ((null == rn.getFullName() || (null != rn.getFullName()) && null == rn.getFullName().getValue())
//                                    && ((null == rn.getEcNumber() || (null != rn.getEcNumber()) && 0 == rn.getEcNumber().size()))))) {
//                                // Alternate name - 2 types of rules for these
//                                List<ProteinType.AlternativeName> anl = pt.getAlternativeName();
//                                if (null == anl) {
//                                    groups.remove(uag);
//                                    System.out.println("Skipping null annotation type, no protein type or comment type found");
//                                    continue;
//                                } else if (anl.size() != 1) {
//                                    groups.remove(uag);
//                                    System.out.println("Skipping annotation type, did not find exactly 1 alternative name, found " + anl.size());
//                                    continue;
//                                }
//                                ProteinType.AlternativeName an = anl.get(0);
//                                EvidencedStringType fullName = an.getFullName();
//                                if (null == fullName) {
//                                    List<EvidencedStringType> estl = an.getEcNumber();
//                                    if (null == estl) {
//                                        groups.remove(uag);
//                                        System.out.println("Skipping null alternative name, no EC number found");
//                                        continue;
//                                    } else if (estl.size() != 1) {
//                                        groups.remove(uag);
//                                        System.out.println("Skipping null alternative name, did not find exactly 1 EC number, found " + estl.size());
//                                        continue;
//                                    }
//                                    String ecNum = estl.get(0).getValue();
//                                    System.out.println("Found rule type " + Rule.PROPERTY_DEAE + " with value " + ecNum);
//                                    Rule r = new Rule();
//                                    LabelValue lv = new LabelValue();
//                                    lv.setLabel(Rule.PROPERTY_DEAE);
//                                    lv.setValue(ecNum);
//                                    r.setLabelValue(lv);
//                                    uag.addUnirule(r);
//                                    continue;
//                                }
//
//                                // Full name
//                                String altName = fullName.getValue();
//                                System.out.println("Found rule type " + Rule.PROPERTY_DEAF + " with value " + altName);
//                                Rule r = new Rule();
//                                LabelValue lv = new LabelValue();
//                                lv.setLabel(Rule.PROPERTY_DEAF);
//                                lv.setValue(altName);
//                                r.setLabelValue(lv);
//                                uag.addUnirule(r);
//                                continue;
//                            }
//                            // Recommended Name
//                            EvidencedStringType fullName = rn.getFullName();
//                            if (null == fullName) {
//                                List<EvidencedStringType> ecl = rn.getEcNumber();
//                                if (null == ecl) {
//                                    groups.remove(uag);
//                                    System.out.println("Skipping null full name, no EC number list found");
//                                    continue;
//                                } else if (ecl.size() != 1) {
//                                    groups.remove(uag);
//                                    System.out.println("Skipping null full name, did not find exactly 1 EC number, found " + ecl.size());
//                                    continue;
//                                }
//                                String recommendedEc = ecl.get(0).getValue();
//                                System.out.println("Found rule type " + Rule.PROPERTY_DEEC + " with value " + recommendedEc);
//                                Rule r = new Rule();
//                                LabelValue lv = new LabelValue();
//                                lv.setLabel(Rule.PROPERTY_DEEC);
//                                lv.setValue(recommendedEc);
//                                r.setLabelValue(lv);
//                                uag.addUnirule(r);
//                                continue;
//                            }
//                            String fullNameValue = fullName.getValue();
//                            System.out.println("Found rule type " + Rule.PROPERTY_DERF + " with value " + fullNameValue);
//                            Rule r = new Rule();
//                            LabelValue lv = new LabelValue();
//                            lv.setLabel(Rule.PROPERTY_DERF);
//                            lv.setValue(fullNameValue);
//                            r.setLabelValue(lv);
//                            uag.addUnirule(r);
//                            continue;
//                        } else {
//                            // OR CommentType - 2 types
//                            CommentType commentType = at.getComment();
//                            if (null == commentType) {
//                                groups.remove(uag);
//                                System.out.println("Skipping null annotation type, no protein type or comment type found");
//                                continue;
//                            }
//                            String type = commentType.getType();
//                            String value = null;
//                            String label = null;
//                            if (LABEL_COMMENT_FUNCTION.equals(type)) {
//                                label = Rule.PROPERTY_CCFU;
//                                value = commentType.getText().get(0).getValue();
//                            } else {
//                                label = Rule.PROPERTY_CCCA;
//                                CatalyticActivityTransformer cct = new CatalyticActivityTransformer();
//                                value = cct.xmlToCcca(commentType);
//                            }
//                            System.out.println("Found rule type " + label + " with value " + value);
//                            value = convertTextFromXml(value);
//                            Rule r = new Rule();
//                            LabelValue lv = new LabelValue();
//                            lv.setLabel(label);
//                            lv.setValue(value);
//                            r.setLabelValue(lv);
//                            uag.addUnirule(r);
//                        }
//                    }
//                }
//
//            }
//        }
//        return groups;