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
package org.paint.gui.menu;

import com.sri.panther.paintCommon.User;
import com.sri.panther.paintCommon.familyLibrary.FileNameGenerator;
import com.sri.panther.paintCommon.util.FileUtils;
import com.sri.panther.paintCommon.util.Utils;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.log4j.Logger;
import org.bbop.framework.GUIManager;
import org.paint.config.Preferences;
import org.paint.dataadapter.AnnotationAdapter;
import org.paint.dataadapter.FileAdapter;
import org.paint.datamodel.Family;
import org.paint.dialog.ActiveFamily;
import org.paint.dialog.LoginDlg;
import org.paint.dialog.ManageBooksDlg;
import org.paint.dialog.NewFamily;
import org.paint.gui.DirtyIndicator;
import org.paint.gui.event.AnnotationChangeEvent;
import org.paint.gui.event.AnnotationChangeListener;
import org.paint.gui.event.CommentChangeEvent;
import org.paint.gui.event.EventManager;
import org.paint.gui.event.FamilyChangeEvent;
import org.paint.gui.event.FamilyChangeListener;
import org.paint.gui.event.ServerLocationChangeEvent;
import org.paint.gui.event.ServerLocationChangeListener;
import org.paint.io.UniruleSoapHelper;
import static org.paint.io.UniruleSoapHelper.userHasPrivilegeToUpdateBook;
import org.paint.main.PaintManager;
import org.uniprot.unirule_1.RuleStatusType;

public class FileMenu extends JMenu implements AnnotationChangeListener, FamilyChangeListener, ServerLocationChangeListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    protected static Logger log = Logger.getLogger(FileMenu.class);

    protected JMenuItem loginItem;
    protected JMenuItem logoffItem;
//    protected JMenuItem openDBItem;
    protected JMenuItem manageBooksItem;
    protected JMenuItem updateUniRuleCommentItem;
//    protected JMenuItem updateCommentItem;
//    protected JMenuItem updateFamilyNameItem;
//    protected JMenuItem saveDBItem;
//    protected JMenuItem viewOmittedAnnotInfoItem;
//    protected JMenuItem viewUpdateHistoryItem;
    protected JMenuItem viewUniruleInfoItem;
    protected JMenuItem viewErrorCaseInfoItem;
    protected JMenuItem exportUniRuleAnnotations;
    protected JMenuItem saveUniRuleAnnotations;

    private static final String MENU_ITEM_LOGIN = "Login";
    private static final String MENU_ITEM_LOGOFF = "Logoff";
//    private static final String MENU_ITEM_OPEN_FROM_DB = "Open from database ... ";
    private static final String MENU_ITEM_MANAGE_BOOKS = "Manage and View Books...";
//    private static final String MENU_ITEM_UPDATE_COMMENT = "Update comment...";
    private static final String MENU_ITEM_UPDATE_COMMENT_UNIRULE = "Update UniRule comments";
//    private static final String MENU_ITEM_UPDATE_NAME_FAMILY = "Name Family...";
//    private static final String MENU_ITEM_SAVE_TO_DB = "Save to database...";
    private static final String MENU_ITEM_VIEW_ANNOT_INFO = "View annotation information";
    private static final String MENU_ITEM_VIEW_UNIRULE_INFO = "View UniRule annotation errors";
    private static final String MENU_ITEM_VIEW_CASE_ERROR_INFO = "View UniRule case errors";
    private static final String MENU_ITEM_VIEW_ANNOT_HISTORY_INFO = "View annotation history";
    public static final String MENU_EXPORT_UNIRULE_ANNOTATIONS = "Export UniRule annotations to XML file locally";
    private static final String MENU_SAVE_UNIRULE_ANNOTATIONS = "Save UniRule annotations";

    private static final String LINE_BREAK = "\\\\n";
    private static final String LINE_SEPARATOR_SYSTEM_PROPERY = System.getProperty("line.separator");
    private static final String STR_EMPTY = "";

    private static List<FileMenu> instances = new ArrayList<FileMenu>();

    public FileMenu(){
        super("File");
        this.setMnemonic('f');
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        loginItem = new JMenuItem(MENU_ITEM_LOGIN);
        loginItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, toolkit.getMenuShortcutKeyMask()));
        loginItem.addActionListener(new LoginActionListener(this, loginItem));
        this.add(loginItem);

        logoffItem = new JMenuItem(MENU_ITEM_LOGOFF);
        logoffItem.addActionListener(new LogoffActionListener(this, logoffItem));
        this.add(logoffItem);
//		logoffItem.setVisible(false);
        logoffItem.setEnabled(false);

        this.addSeparator();

//        openDBItem = new JMenuItem(MENU_ITEM_OPEN_FROM_DB);
//        openDBItem.addActionListener(new SearchBooksActionListener());
//        this.add(openDBItem);
//        openDBItem.setEnabled(false); // not until the user logs in
        manageBooksItem = new JMenuItem(MENU_ITEM_MANAGE_BOOKS);
        manageBooksItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, toolkit.getMenuShortcutKeyMask()));
        manageBooksItem.addActionListener(new ManageBooksActionListener());
        add(manageBooksItem);
        manageBooksItem.setEnabled(false);

//        updateFamilyNameItem = new JMenuItem(MENU_ITEM_UPDATE_NAME_FAMILY);
//        updateFamilyNameItem.addActionListener(new UpdateFamilyNameActionListener());
//        add(updateFamilyNameItem);
//        updateFamilyNameItem.setEnabled(false);           
//        
//        updateCommentItem = new JMenuItem(MENU_ITEM_UPDATE_COMMENT);
//        updateCommentItem.addActionListener(new UpdateCommentsActionListener());
//        add(updateCommentItem);
//        updateCommentItem.setEnabled(false);
        updateUniRuleCommentItem = new JMenuItem(MENU_ITEM_UPDATE_COMMENT_UNIRULE);
        updateUniRuleCommentItem.addActionListener(new UpdateUniRuleCommentsActionListener());
        add(updateUniRuleCommentItem);
        updateUniRuleCommentItem.setEnabled(false);

//        saveDBItem = new JMenuItem(MENU_ITEM_SAVE_TO_DB);
//        saveDBItem.addActionListener(new SaveBookActionListener());
//        add(saveDBItem);
//        saveDBItem.setEnabled(false);
        this.addSeparator();

//        viewOmittedAnnotInfoItem = new JMenuItem(MENU_ITEM_VIEW_ANNOT_INFO);
//        viewOmittedAnnotInfoItem.addActionListener(new ViewOmittedAnnotActionListener());
//        add(viewOmittedAnnotInfoItem);
//        viewOmittedAnnotInfoItem.setEnabled(false);
        viewUniruleInfoItem = new JMenuItem(MENU_ITEM_VIEW_UNIRULE_INFO);
        viewUniruleInfoItem.addActionListener(new ViewUniruleOmittedAnnotActionListener());
        add(viewUniruleInfoItem);
        viewUniruleInfoItem.setEnabled(false);

        viewErrorCaseInfoItem = new JMenuItem(MENU_ITEM_VIEW_CASE_ERROR_INFO);
        viewErrorCaseInfoItem.addActionListener(new ViewUniRuleErrorCaseActionListener());
        add(viewErrorCaseInfoItem);
        viewErrorCaseInfoItem.setEnabled(false);

//        viewUpdateHistoryItem = new JMenuItem(MENU_ITEM_VIEW_ANNOT_HISTORY_INFO);
//        viewUpdateHistoryItem.addActionListener(new ViewAnnotHistoryActionListener());
//        add(viewUpdateHistoryItem);
//        viewUpdateHistoryItem.setEnabled(false);
        this.addSeparator();
        exportUniRuleAnnotations = new JMenuItem(MENU_EXPORT_UNIRULE_ANNOTATIONS);
        exportUniRuleAnnotations.addActionListener(new ExportUniRuleXMLAnnotationActionListener());
        add(exportUniRuleAnnotations);
        exportUniRuleAnnotations.setEnabled(false);

        saveUniRuleAnnotations = new JMenuItem(MENU_SAVE_UNIRULE_ANNOTATIONS);
        saveUniRuleAnnotations.addActionListener(new SaveUniRuleAnnotationActionListener());
        add(saveUniRuleAnnotations);
        saveUniRuleAnnotations.setEnabled(false);

        // Add save functon later on if required
//		saveFileLocalItem = new JMenuItem(save_annots);
//		saveFileLocalItem.addActionListener(new SaveToFileActionListener());
//		this.add(saveFileLocalItem);
        updateMenu();

        EventManager.inst().registerGeneAnnotationChangeListener(this);
        EventManager.inst().registerFamilyListener(this);
        EventManager.inst().registerServerLocationChangeListener(this);

//		instances.add(this);
    }

    public void updateMenu() {
//        saveDBItem.setEnabled(DirtyIndicator.inst().bookUpdated());
//        updateCommentItem.setEnabled(DirtyIndicator.inst().bookUpdated());
        updateUniRuleCommentItem.setEnabled(DirtyIndicator.inst().bookUpdated());
//        updateFamilyNameItem.setEnabled(DirtyIndicator.inst().bookUpdated());
//        viewOmittedAnnotInfoItem.setEnabled(DirtyIndicator.inst().bookUpdated());
        viewUniruleInfoItem.setEnabled(DirtyIndicator.inst().bookUpdated());
        viewErrorCaseInfoItem.setEnabled(DirtyIndicator.inst().bookUpdated());
//        viewUpdateHistoryItem.setEnabled(DirtyIndicator.inst().bookUpdated());
        exportUniRuleAnnotations.setEnabled(DirtyIndicator.inst().bookUpdated());
        saveUniRuleAnnotations.setEnabled(DirtyIndicator.inst().bookUpdated());
//		openDBItem.setEnabled(InternetChecker.getInstance().isConnectionPresent(true));
//
//		boolean family_loaded = DirtyIndicator.inst().familyLoaded();
//		saveFileLocalItem.setEnabled(family_loaded);

    }

    public boolean saveCurrent() {
        return PaintManager.inst().saveCurrent();
    }

    @Override
    public void newFamilyData(FamilyChangeEvent e) {
//        this.updateCommentItem.setEnabled(true);
        this.updateUniRuleCommentItem.setEnabled(true);
//        this.updateFamilyNameItem.setEnabled(true);
//        this.saveDBItem.setEnabled(true);
//        this.viewOmittedAnnotInfoItem.setEnabled(true);
        this.viewUniruleInfoItem.setEnabled(true);
        this.viewErrorCaseInfoItem.setEnabled(true);
//        this.viewUpdateHistoryItem.setEnabled(true);
        this.exportUniRuleAnnotations.setEnabled(true);
        this.saveUniRuleAnnotations.setEnabled(true);
    }

    public void familyClosed() {
        updateUniRuleCommentItem.setEnabled(false);
//            updateFamilyNameItem.setEnabled(false);
//            viewOmittedAnnotInfoItem.setEnabled(false);
        viewUniruleInfoItem.setEnabled(false);
        viewErrorCaseInfoItem.setEnabled(false);
//            viewUpdateHistoryItem.setEnabled(false);
        exportUniRuleAnnotations.setEnabled(false);
        saveUniRuleAnnotations.setEnabled(false);
    }

    public static void saveToXML() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setSelectedFile(new File(PaintManager.inst().getFamily().getFamilyID() + FileNameGenerator.DOT + FileNameGenerator.XML_SUFFIX));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Documents (*.xml)", FileNameGenerator.XML_SUFFIX);
        fileChooser.addChoosableFileFilter(filter);
        int result = fileChooser.showSaveDialog(GUIManager.getManager().getFrame());
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File f = fileChooser.getSelectedFile();
        if (f != null) {
            try {
                StringBuffer errorBuf = new StringBuffer();
                String uniRuleXml = UniruleSoapHelper.getUniruleInXMLFormat(null, errorBuf);
                if (0 != errorBuf.length()) {
                    int dialogResult = JOptionPane.showConfirmDialog(GUIManager.getManager().getFrame(), errorBuf.toString(), "Error creating Rule, Continue", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (dialogResult != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                if (null == uniRuleXml) {
                    JOptionPane.showMessageDialog(GUIManager.getManager().getFrame(), "No unirule annotations to export");
                    return;
                }
                String xmlFile = FileNameGenerator.formatXMLFileName(f.getCanonicalPath());
                FileUtils.writeBufferToFile(xmlFile, new StringBuffer(uniRuleXml));
                JOptionPane.showMessageDialog(GUIManager.getManager().getFrame(), "Annotations have been saved in XML format", "Save to XML Successful", JOptionPane.INFORMATION_MESSAGE);
//                    // returns the full filename, including the path, for the PTHR*****.paint file
//                    String xmlFile = FileNameGenerator.formatXMLFileName(f.getCanonicalPath());
//                    StringBuilder sb = AnnotationAdapter.getUniRuleAnnotationsInXML();
//                    FileUtils.writeBufferToFile(xmlFile, new StringBuffer(sb.toString()));
            } catch (IOException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(GUIManager.getManager().getFrame(), "Unable to save file " + f.getName());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(GUIManager.getManager().getFrame(), "Unable to save file " + f.getName());
            }
        }
    }
    
    @Override
    public void handleServerLocationChangeEvent(ServerLocationChangeEvent event) {
        logoff();
    }    

    private class LoginActionListener implements ActionListener {

        FileMenu fileMenu;
        JMenuItem menuItem;

        public LoginActionListener(FileMenu fileMenu, JMenuItem menuItem) {
            this.fileMenu = fileMenu;
            this.menuItem = menuItem;
        }

        /**
         * Method declaration
         *
         *
         * @param e
         *
         * @see
         */
        public void actionPerformed(ActionEvent e) {
            // Save current document, if necessary and close.  Once logged in, user may be in a different operation mode
            if (DirtyIndicator.inst().bookUpdated() && null != PaintManager.inst().getFamily()) {
                int dialogResult = JOptionPane.showConfirmDialog(GUIManager.getManager().getFrame(), "Book has been updated, do you want to save?", "Book Updated", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    saveCurrent();
                }
            }
            DirtyIndicator.inst().setAnnotated(false);

            LoginDlg dlg = new LoginDlg(GUIManager.getManager().getFrame());

            // First get user login information
            ArrayList results = dlg.display();

            if (null == results) {
                return;
            }
            if (true == results.isEmpty()) {
                return;
            }

            User user = UniruleSoapHelper.userValid(results);
            if (null == user) {
                JOptionPane.showMessageDialog(GUIManager.getManager().getFrame(), "Unable to verify user information", "User Information", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Temporary until locking mechanism is available in Uniprot
//            PantherServer pServer = PantherServer.inst();
//            Vector userInfo = new Vector();
//            userInfo.add(new String("gouser"));
//            userInfo.add("welcome".toCharArray());
//            user = pServer.getUserInfo(Preferences.inst().getPantherURL(), userInfo);
//            if (null == user) {
//                JOptionPane.showMessageDialog(GUIManager.getManager().getFrame(), "Unable to verify user information for gouser", "User Information", JOptionPane.ERROR_MESSAGE);               
//                System.exit(-1);                
//            }
//            if (user.getprivilegeLevel() < Constant.USER_PRIVILEGE_SAVE_LOCAL) {
//                JOptionPane.showMessageDialog(GUIManager.getManager().getFrame(), "User does not have privilege to lock and save books", "User privilege warning", JOptionPane.WARNING_MESSAGE);
//            }
            PaintManager pm = PaintManager.inst();
            pm.setUser(user);
            pm.setUserInfo(results);
            loginItem.setEnabled(false);
            logoffItem.setEnabled(true);
//            openDBItem.setEnabled(true);
            manageBooksItem.setEnabled(true);

        }
    }

    /**
     * Class declaration
     *
     *
     * @author
     * @version %I%, %G%
     */
    private class LogoffActionListener implements ActionListener {

        FileMenu fileMenu;
        JMenuItem menuItem;

        public LogoffActionListener(FileMenu fileMenu, JMenuItem menuItem) {
            this.fileMenu = fileMenu;
            this.menuItem = menuItem;
        }

        /**
         * Method declaration
         *
         *
         * @param e
         *
         * @see
         */
        public void actionPerformed(ActionEvent e) {
            logoff();
        }
    
      
    }

    public void logoff() {
        PaintManager pm = PaintManager.inst();
        if (DirtyIndicator.inst().bookUpdated() && null != pm.getFamily()) {
            int dialogResult = JOptionPane.showConfirmDialog(GUIManager.getManager().getFrame(), "Book has been updated, do you want to save?", "Book Updated", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (dialogResult == JOptionPane.YES_OPTION) {
                saveCurrent();
            }
        }
        DirtyIndicator.inst().setAnnotated(false);

        pm.setUser(null);
        pm.setUserInfo(null);
        loginItem.setEnabled(true);
        logoffItem.setEnabled(false);
//            openDBItem.setEnabled(false);
        manageBooksItem.setEnabled(false);
//            saveDBItem.setEnabled(false);
//            updateCommentItem.setEnabled(false);
        updateUniRuleCommentItem.setEnabled(false);
//            updateFamilyNameItem.setEnabled(false);
//            viewOmittedAnnotInfoItem.setEnabled(false);
        viewUniruleInfoItem.setEnabled(false);
        viewErrorCaseInfoItem.setEnabled(false);
//            viewUpdateHistoryItem.setEnabled(false);
        exportUniRuleAnnotations.setEnabled(false);
        saveUniRuleAnnotations.setEnabled(false);
    }      
    
    
    private class UpdateFamilyNameActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            PaintManager pm = PaintManager.inst();
            String curFamilyName = pm.getFamily().getName();
            JTextArea ta = new JTextArea(1, 40);
            if (null != curFamilyName) {
                ta.setText(curFamilyName);
            }

            ta.setWrapStyleWord(true);
            ta.setLineWrap(true);
            ta.setCaretPosition(0);
            ta.setEditable(true);

            JOptionPane.showMessageDialog(GUIManager.getManager().getFrame(), new JScrollPane(ta), "Family Name", JOptionPane.INFORMATION_MESSAGE);
            String newFamilyName = ta.getText();
            if (null != newFamilyName) {
                newFamilyName.trim();
                if (0 == newFamilyName.length()) {
                    newFamilyName = null;
                }
            }
            if (null != curFamilyName && false == curFamilyName.equals(newFamilyName)) {
                pm.getFamily().setName(newFamilyName);
            } else if (null != newFamilyName && false == newFamilyName.equals(curFamilyName)) {
                pm.getFamily().setName(newFamilyName);
            }
            pm.setTitle();
            DirtyIndicator.inst().setAnnotated(true);
            EventManager.inst().fireAnnotationChangeEvent(new AnnotationChangeEvent(pm.getTree().getRoot()));
        }
    }

    private class ViewUniruleOmittedAnnotActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            PaintManager pm = PaintManager.inst();
            Family fam = pm.getFamily();
            if (null == fam) {
                return;
            }
            StringBuffer errorBuf = fam.getUniruleErrorBuf();
            if (null == errorBuf || 0 == errorBuf.length()) {
                JOptionPane.showMessageDialog(GUIManager.getManager().getFrame(), "No UniRule annotation errors");
                return;
            }
            String info = errorBuf.toString().replaceAll(LINE_BREAK, LINE_SEPARATOR_SYSTEM_PROPERY);
            JTextArea ta = new JTextArea(20, 100);
            if (null != info) {
                ta.setText(info);
            }
            ta.setEditable(false);
            ta.setWrapStyleWord(true);
            ta.setLineWrap(true);
            ta.setCaretPosition(0);
            JOptionPane.showMessageDialog(GUIManager.getManager().getFrame(), new JScrollPane(ta), "Additional information about UniRule annotations", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class ViewUniRuleErrorCaseActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            StringBuffer errorBuf = AnnotationAdapter.getErrorCaseInfo();
            if (null == errorBuf) {
                JOptionPane.showMessageDialog(GUIManager.getManager().getFrame(), "No UniRule case errors");
                return;
            }
            String info = errorBuf.toString();
            JTextArea ta = new JTextArea(20, 100);
            if (null != info) {
                ta.setText(info);
            }
            ta.setEditable(false);
            ta.setWrapStyleWord(true);
            ta.setLineWrap(true);
            ta.setCaretPosition(0);
            JOptionPane.showMessageDialog(GUIManager.getManager().getFrame(), new JScrollPane(ta), "Additional information about UniRule cases that are not displayed in paint", JOptionPane.INFORMATION_MESSAGE);

        }
    }

    private class ViewOmittedAnnotActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            PaintManager pm = PaintManager.inst();
            StringBuffer errorBuf = pm.getFamily().getNodeInfoBuf();
            if (null == errorBuf) {
                errorBuf = new StringBuffer(STR_EMPTY);
            }
            String info = errorBuf.toString().replaceAll(LINE_BREAK, LINE_SEPARATOR_SYSTEM_PROPERY);
            JTextArea ta = new JTextArea(20, 100);
            if (null != info) {
                ta.setText(info);
            }
            ta.setEditable(false);
            ta.setWrapStyleWord(true);
            ta.setLineWrap(true);
            ta.setCaretPosition(0);
            JOptionPane.showMessageDialog(GUIManager.getManager().getFrame(), new JScrollPane(ta), "Additional information about annotations", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class ViewAnnotHistoryActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            PaintManager pm = PaintManager.inst();
            String upateHistory = pm.getUpdateHistory();
            if (null == upateHistory) {
                upateHistory = STR_EMPTY;
            }
            String info = upateHistory.toString().replaceAll(LINE_BREAK, LINE_SEPARATOR_SYSTEM_PROPERY);
            JTextArea ta = new JTextArea(20, 100);
            if (null != info) {
                ta.setText(info);
            }
            ta.setEditable(false);
            ta.setWrapStyleWord(true);
            ta.setLineWrap(true);
            ta.setCaretPosition(0);
            JOptionPane.showMessageDialog(GUIManager.getManager().getFrame(), new JScrollPane(ta), "Curation history information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class UpdateCommentsActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            PaintManager pm = PaintManager.inst();
            String curComment = pm.getCuratorNotes();
            if (null != curComment) {
                curComment = curComment.replaceAll(LINE_BREAK, LINE_SEPARATOR_SYSTEM_PROPERY);
            }
            JTextArea ta = new JTextArea(20, 100);
            if (null != curComment) {
                ta.setText(curComment);
            }

            ta.setWrapStyleWord(true);
            ta.setLineWrap(true);
            ta.setCaretPosition(0);
            ta.setEditable(true);

            JOptionPane.showMessageDialog(GUIManager.getManager().getFrame(), new JScrollPane(ta), "Curator Notes", JOptionPane.INFORMATION_MESSAGE);
            String newComment = ta.getText();
            if (null != newComment) {
                newComment = newComment.trim();
                if (0 == newComment.length()) {
                    newComment = null;
                }
            }
            if (null != curComment && false == curComment.equals(newComment)) {
                pm.setCuratorNotes(newComment);
            } else if (null != newComment && false == newComment.equals(curComment)) {
                pm.setCuratorNotes(newComment);
            }
            DirtyIndicator.inst().setAnnotated(true);
            EventManager.inst().fireCommentChangeEvent(new CommentChangeEvent(this));
        }
    }

    private class UpdateUniRuleCommentsActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            PaintManager pm = PaintManager.inst();
            Family f = pm.getFamily();
            String extComment = f.getUniRuleCommentExternal();
            String intComment = f.getUniRuleCommentInternal();

            JTextArea extTextArea = new JTextArea(5, 80);
            if (null != extComment) {
                extTextArea.setText(extComment);
            }
            extTextArea.setWrapStyleWord(true);
            extTextArea.setLineWrap(true);
            extTextArea.setCaretPosition(0);
            extTextArea.setEditable(true);
            JScrollPane extPanel = new JScrollPane(extTextArea);

            JTextArea intTextArea = new JTextArea(5, 80);
            if (null != intComment) {
                intTextArea.setText(intComment);
            }
            intTextArea.setWrapStyleWord(true);
            intTextArea.setLineWrap(true);
            intTextArea.setCaretPosition(0);
            intTextArea.setEditable(true);
            JScrollPane intPanel = new JScrollPane(intTextArea);

            Object[] inputFields = {"Enter External Comment", extPanel, "Enter Intternal Comment", intPanel};

            int option = JOptionPane.showConfirmDialog(GUIManager.getManager().getFrame(), inputFields, "Comments", JOptionPane.INFORMATION_MESSAGE);
            if (option == JOptionPane.OK_OPTION) {
                f.setUniRuleCommentExternal(extTextArea.getText());
                f.setUniRuleCommentInternal(intTextArea.getText());
            }
            DirtyIndicator.inst().setAnnotated(true);
            EventManager.inst().fireCommentChangeEvent(new CommentChangeEvent(this));
        }
    }

    private class ManageBooksActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            if (DirtyIndicator.inst().bookUpdated() && null != PaintManager.inst().getFamily()) {
                int dialogResult = JOptionPane.showConfirmDialog(GUIManager.getManager().getFrame(), "Book has been updated, do you want to save?", "Book Updated", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    saveCurrent();
                }
            }
            DirtyIndicator.inst().setAnnotated(false);

            ManageBooksDlg dlg = new ManageBooksDlg(GUIManager.getManager().getFrame(), Preferences.inst().getPantherURL(), PaintManager.inst().getUserInfo());
            String bookId = dlg.display();
            if (null == bookId) {
                return;
            }
            User user = PaintManager.inst().getUser();

            if (null == user || (null != user && false == Utils.search(User.UPDATE_USER_ROLES, user.getRole()))) {
                int dialogResult = JOptionPane.showConfirmDialog(GUIManager.getManager().getFrame(), "User does not have privilege to lock and save books, continue?", "User privilege warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (dialogResult != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            User lockedBy = dlg.getLockedBy();
            if (null != user && null != lockedBy && false == user.getloginName().equals(lockedBy.getLoginName())) {
                String msg = "Book is locked by another user " + lockedBy.getLoginName() + ", you will not be able to save changes.  Continue?";
                if (User.ROLE_ADMIN.equals(user.getRole())) {
                    msg = "Book is locked by another user " + lockedBy.getLoginName() + ", However, you have prriviledge to save changes.  Continue?";
                }
                int dialogResult = JOptionPane.showConfirmDialog(GUIManager.getManager().getFrame(), msg, "Book is already locked by another user", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (dialogResult != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            PaintManager.inst().closeCurrent();

            // Open book for user
            PaintManager.inst().openNewFamily(bookId);
            updateMenus();

        }
    }

    private class SaveBookActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(GUIManager.getManager().getFrame(), "Save operation not supported");
            return;

            //PaintManager.inst().saveCurrent();
        }
    }

    private class SearchBooksActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (DirtyIndicator.inst().bookUpdated() && null != PaintManager.inst().getFamily()) {
                int dialogResult = JOptionPane.showConfirmDialog(GUIManager.getManager().getFrame(), "Book has been updated, do you want to save?", "Book Updated", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    saveCurrent();
                }
            }
            DirtyIndicator.inst().setAnnotated(false);

            NewFamily dlg = new NewFamily(GUIManager.getManager().getFrame());
            String familyID = dlg.display();
            if (familyID != null) {
                // Open book for user
                //							PaintManager.inst().closeCurrent();
                PaintManager.inst().openNewFamily(familyID);
                updateMenus();
            }

        }
    }

    private static class SaveToFileActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            ActiveFamily dlg = new ActiveFamily(GUIManager.getManager().getFrame());

            File f = dlg.getSelectedFile(true, null);
            if (f != null) {
                try {
                    // returns the full filename, including the path, for the PTHR*****.paint file
                    String paintfile = FileNameGenerator.formatPAINTFileName(f.getCanonicalPath());
                    FileAdapter dt = new FileAdapter(paintfile);
                    dt.saveOutput();
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(GUIManager.getManager().getFrame(), "Unable to save file " + f);
                }
            }
        }
    }

    private static class SaveUniRuleAnnotationActionListener implements ActionListener {

//        public void actionPerformed(ActionEvent e) {
//            JOptionPane.showMessageDialog(GUIManager.getManager().getFrame(), "Operation disabled, local save operation is supported");
//            return;
//        }

        public void actionPerformed(ActionEvent e) {
            // Different roles allow saving of different status for books

            User u = PaintManager.inst().getUser();
            String role = u.getRole();
            if (User.ROLE_UNKNOWN.equals(role) || User.ROLE_OBSERVER.equals(role)) {
                saveLocally("You do not have permission to save back to server. Save locally?");
                return;
            }

            PaintManager pm = PaintManager.inst();
            Family family = pm.getFamily();
            String famId = family.getFamilyID();
            StringBuffer errorBuf = new StringBuffer();
            if (false == userHasPrivilegeToUpdateBook(pm.getUser(), famId, errorBuf)) {
                saveLocally(errorBuf.toString() + ", do you want to save locally?");
                return;
            }
            RuleStatusType statusType = RuleStatusType.TEST;
            if (User.ROLE_CURATOR.equals(role)) {
                int n = 0;
                Object[] options = {"Save with Test Status", "Save with Apply Status", "Cancel"};
                n = JOptionPane.showOptionDialog(GUIManager.getManager().getFrame(), "Save Options", "", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[n]);
                if (n > 1 || n < 0) {
                    saveLocally("Save locally?");
                    return;
                }
                if (1 == n) {
                    statusType = RuleStatusType.APPLY;
                }
            } else if (User.ROLE_ADMIN.equals(role)) {
                int n = 0;
                Object[] options = {"Save with Test Status", "Save with Apply Status", "Save with Disused Status", "Cancel"};
                n = JOptionPane.showOptionDialog(GUIManager.getManager().getFrame(), "Save Options", "", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[n]);
                if (n > 2 || n < 0) {
                    saveLocally("Save locally?");
                    return;
                }
                if (1 == n) {
                    statusType = RuleStatusType.APPLY;
                } else if (2 == n) {
                    statusType = RuleStatusType.DISUSED;
                }
            }

            boolean success = UniruleSoapHelper.save(statusType, errorBuf);
            if (false == success) {
                saveLocally(errorBuf.toString() + ", do you want to save locally?");
            }
        }
    }

    private static class ExportUniRuleXMLAnnotationActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            FileMenu.saveToXML();
        }
    }

    public static void saveLocally(String msg) {
        int dialogResult = JOptionPane.showConfirmDialog(GUIManager.getManager().getFrame(), msg, "Book Updated", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (dialogResult == JOptionPane.YES_OPTION) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(PaintManager.inst().getFamily().getFamilyID() + FileNameGenerator.DOT + FileNameGenerator.XML_SUFFIX));
            fileChooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Documents (*.xml)", FileNameGenerator.XML_SUFFIX);
            fileChooser.addChoosableFileFilter(filter);
            int result = fileChooser.showSaveDialog(GUIManager.getManager().getFrame());
            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }
            FileMenu.saveToXML();
            DirtyIndicator.inst().setAnnotated(false);
        }
    }

    @Override
    public void handleAnnotationChangeEvent(AnnotationChangeEvent event) {
        updateMenu();
    }

    private static void updateMenus() {
        for (FileMenu f : instances) {
            f.updateMenu();
        }
    }
}
