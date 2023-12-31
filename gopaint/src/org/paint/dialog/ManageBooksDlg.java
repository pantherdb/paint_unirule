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
package org.paint.dialog;

import com.sri.panther.paintCommon.Book;
import com.sri.panther.paintCommon.Constant;
import com.sri.panther.paintCommon.User;
import edu.usc.ksom.pm.panther.paintCommon.DataTransferObj;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.paint.dataadapter.PantherServer;
import org.paint.io.UniruleSoapHelper;
import org.paint.main.PaintManager;


public class ManageBooksDlg extends JDialog {

    Frame frame;
    JTabbedPane mainPanel;
    JPanel SearchLockPanel;
    JPanel myBooksPanel;
    JPanel searchPanel;
    JPanel booksPanel;
    JPanel bookListPanel;
    JPanel unlockedBookListPanel;
    JLabel myBooksStatusLabel;

    public JTextField searchTerm;
    public JRadioButton geneSymbolBtn;
    public JRadioButton geneIdentifierBtn;
    public JRadioButton proteinIdentifierBtn;
    public JRadioButton definitionBtn;
    public JRadioButton getAllBooksBtn;
    public JRadioButton getBookByIdBtn;
//    public JRadioButton getAllUncurtedBtn;
//    public JRadioButton getRequirePaintReviewBtn;
    public JRadioButton getBooksWithStatusApply;
    public JRadioButton getBooksWithStatusUnused;    
    public JRadioButton getBooksByPTNBtn;

    public JTable searchBooksTable;
    public JTable myBooksTable;

    public static final String MSG_PLEASE_ENTER_SEARCH_TERM = "Please enter a search term";
    public static final String MSG_PLEASE_SELECT_LOCK_UNLOCK_BOOKS = "Please select books to lock or unlock";
    public static final String MSG_PLEASE_SELECT_UNLOCK_BOOKS = "Please select books to unlock";
    public static final String MSG_BOOKS_LOCKED = "Selected books have been locked";
    public static final String MSG_BOOKS_UNLOCKED = "Selected books have been unlocked";
    public static final String MSG_BOOKS_CANNOT_BE_LOCKED = "Selected books cannot be locked, the\n  following error has been returned:  ";
    public static final String MSG_NO_LOCKED_BOOKS_FOUND = "No locked books found";
    public static final String MSG_SERVER_ERROR_CANNOT_SEARCH_BOOKS =  "Server returned error, cannot search for books";
    public static final String MSG_SERVER_ERROR_CANNOT_SEARCH_UNIRULE_BOOK_STATUS =  "Server returned error, cannot search unirule book status"; 
    public static final String MSG_SERVER_ERROR_CANNOT_LOCK_BOOKS =  "Server returned error, cannot lock books";
    public static final String MSG_SERVER_ERROR_CANNOT_UNLOCK_BOOKS =  "Server returned error, cannot unlock books";
    public static final String MSG_SERVER_ERROR_CANNOT_LOCK_UNLOCK_BOOKS =  "Server returned error, cannot lock and unlock books";
    public static final String MSG_SERVER_ERROR_CANNOT_ACCESS_LOCKED_BOOKS =  "Server returned error, cannot access locked books";
    public static final String MSG_NO_BOOKS_FOR_SEARCH_CRITERIA = "No books found matching search criteria";
    public static final String MSG_UNABLE_TO_VERIFY_LOGIN = "Unable to verify login";    
    
    
    public static final String MSG_HEADER_LOCK_BOOKS = "Lock Books";
    public static final String MSG_HEADER_UNLOCK_BOOKS = "Unlock Books";
    public static final String MSG_HEADER_LOCK_UNLOCK_BOOKS = "Lock and Unlock Books";
    public static final String MSG_HEADER_MY_BOOKS = "My Books";
    public static final String MSG_HEADER_SEARCH_BOOKS = "Search Books";

    
    public static final String LABEL_SEARCH = "Search Term";
    public static final String LABEL_SUBMIT = "Submit";
    
    public static final String LABEL_SELECT_ALL = "Select all";
    public static final String LABEL_DESELECT_ALL = "Deselect all";
    
    public static final String BUTTON_LABEL_VIEW = "View";
    
    public static final String LABEL_UNLOCK_SELECTED_BOOKS = "Unlock selected books";

    public static final String LABEL_SEARCH_GENE_SYMBOL = "Gene Symbol";
    public static final String LABEL_SEARCH_GENE_IDENTIFIER =
        "Gene Identifier";
    public static final String LABEL_SEARCH_PROTEIN_IDENTIFIER =
        "Protein Identifier";
    public static final String LABEL_SEARCH_DEFINITION = "Definition (partial def supported)";
    public static final String LABEL_GET_FULL_LIST = "Get list of all books";
    public static final String LABEL_GET_BOOK_BY_ID = "Get book by id";
//    public static final String LABEL_GET_UNCURATED_LIST = "Get unlocked and non-manually curated books";
//    public static final String LABEL_GET_REQUIRE_PAINT_REVIEW_LIST = "Get books marked as require PAINT review";
    public static final String LABEL_GET_BOOKS_WITH_STATUS_APPLY = "Get books marked as Apply";
    public static final String LABEL_GET_BOOKS_WITH_STATUS_DISUSED = "Get books marked as Unused";            
//    public static final String LABEL_APPLY = "Get APPLY only books";
//    public static final String LABEL_TEST = "Get TEST only books";    
    public static final String LABEL_GET_BOOK_BY_PTN = "Get book by PTN";    
    public static final String LABEL_TITLE = "Manage Books";

    private static final String COLUMN_NAME_BOOK_ID = "Book Id";
    private static final String COLUMN_NAME_NAME = "Name";
    private static final String COLUMN_NAME_CURATION_STATUS = "Curation status";
    private static final String COLUMN_NAME_EXP_EVDNCE = "Experimental";
    //private static final String COLUMN_NAME_NOTES = "Notes";      // Curator notes
    private static final String COLUMN_NAME_ORG = "Organism";
    private static final String COLUMN_NUM_LEAVES = "# leaves";
    private static final String COLUMN_NAME_LOCKED_BY = "Locked by";
    private static final String COLUMN_NAME_LAST_MODIFIED_BY = "Last modiffied by";
    //private static final String COLUMN_NAME_DATE = "Last Status Change";
    private static final String COLUMN_NAME_OPEN = "Open";
    private static final String COLUMN_NAME_LOCK_UNLOCK = "Lock/UnLock";
    private static final String COLUMN_NAME_UNLOCK = "Unlock";
//    private static final String[] COLUMN_NAMES_SEARCH =
//    { COLUMN_NAME_BOOK_ID, COLUMN_NAME_NAME, COLUMN_NAME_CURATION_STATUS, COLUMN_NAME_DATE, COLUMN_NAME_EXP_EVDNCE, COLUMN_NAME_NOTES, COLUMN_NAME_ORG, COLUMN_NUM_LEAVES, COLUMN_NAME_OPEN,  COLUMN_NAME_LOCK_UNLOCK, COLUMN_NAME_LOCKED_BY, COLUMN_NAME_LAST_MODIFIED_BY};

    private static final String[] COLUMN_NAMES_SEARCH = { COLUMN_NAME_BOOK_ID,
                                                            COLUMN_NAME_NAME,
                                                            COLUMN_NAME_CURATION_STATUS,
                                                            COLUMN_NAME_EXP_EVDNCE,
                                                            COLUMN_NAME_ORG,
                                                            COLUMN_NUM_LEAVES,
                                                            COLUMN_NAME_OPEN,
                                                            COLUMN_NAME_LOCK_UNLOCK,
                                                            COLUMN_NAME_LOCKED_BY,
                                                            COLUMN_NAME_LAST_MODIFIED_BY};

    
    public static final Class[] COLUMN_TYPES_SEARCH = {String.class,
                                                        String.class,
                                                        String.class,
                                                        Boolean.class,
                                                        String.class,
                                                        Integer.class,
                                                        JButton.class,
                                                        Boolean.class,
                                                        String.class,
                                                        String.class};

    public static final String LINE_BREAK = "\\\\n";
    public static final String HTML_LINEBREAK = "<BR>";
    public static final String HTML_START = "<HTML>";
    public static final String HTML_END = "</HTML>";
    
    int defaultSortColSearchTbl = 5;
//    int nonSortCol = 6;
//    int COL_INDEX_COMMENT = 5;
    int COL_INDEX_LOCK_UNLOCK  = 7;
    private static final String[] COLUMN_NAMES_MY_BOOKS = { COLUMN_NAME_BOOK_ID,
                                                            COLUMN_NAME_NAME,
                                                            COLUMN_NAME_CURATION_STATUS,
                                                            COLUMN_NAME_OPEN,
                                                            COLUMN_NAME_UNLOCK};
    private static final Class[] COLUMN_TYPES_MY_BOOKS = {String.class,
                                                            String.class,
                                                            String.class,
                                                            JButton.class,
                                                            Boolean.class};
    
    private static final String JLABEL_MY_BOOKS_RETRIEVING_BOOKS = "Retrieving books...";
    private static final String JLABEL_MY_BOOKS_SELECT_BOOKS_TO_UNLOCK = "Select books to unlock";
    
    public static final String[] ORG_LIST_SPECIAL = {"HUMAN", "MOUSE", "DROME", "CAEEL", "YEAST", "ARATH"};
    
    boolean DEBUG = true;
    protected String servletUrl;
    ArrayList userInfo;
    //User user;
    //String dbClsId;
    String openBookId = null;
    User lockedBy = null;
    
    JRadioButton lastValidSearchBtn = null;
    String lastValidSearchStr = null;
    
    public static final Color COLOR_UNALLOWED = new Color(250, 219, 216);       // Pink
    public static final Color COLOR_CAUTION = new Color(249, 231, 159);        // Yellow
    public static final Color COLOR_PERMITTED = new Color(213, 245, 227);      // Green    

    public ManageBooksDlg(Frame frame, String servletUrl, ArrayList userInfo) {
        super(frame, true);
        setTitle(LABEL_TITLE);
        this.frame = frame;
        this.servletUrl = servletUrl;
        this.userInfo = userInfo;
        //this.dbClsId = dbClsId;
        initializePanel();
    }

    protected void initializePanel() {

        initializeSearchLockUnlockPanel();
        initializeMyBooksPanel();

        mainPanel = new JTabbedPane();
        mainPanel.addChangeListener(new PanelChangeListener());
        mainPanel.add("Search And Lock or Unlock Books", SearchLockPanel);
        mainPanel.add("View Locked Books", myBooksPanel);
        setContentPane(mainPanel);
        Rectangle r = frame.getBounds();

        setBounds(r.x + r.width / 2, r.y + r.height / 2, 1000, 800);
        pack();
        setLocationRelativeTo(frame);

    }

    protected void initializeSearchLockUnlockPanel() {
        SearchLockPanel = new JPanel();
        SearchLockPanel.setLayout(new BoxLayout(SearchLockPanel,
                                                BoxLayout.Y_AXIS));
        SearchLockPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        initializeSearchPanel();

        initializeBookListPanel();


        SearchLockPanel.add(searchPanel);
        SearchLockPanel.add(booksPanel);
    }




    protected void initializeSearchPanel() {
        searchPanel = new JPanel();

        // Search Term panel
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.PAGE_AXIS));
        JPanel searchTermPanel = new JPanel();
        searchTermPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        searchTermPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        searchTermPanel.add(new JLabel(LABEL_SEARCH));
        searchTerm = new JTextField(40);
        searchTermPanel.add(searchTerm);


        // Search Type panel
        GridLayout gl = new GridLayout(0, 2, 0, 0);
        gl.setVgap(0);
        gl.setHgap(0);        
        JPanel searchTypePanel = new JPanel(gl);
        searchTypePanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        getBookByIdBtn = new JRadioButton(LABEL_GET_BOOK_BY_ID);
        getBookByIdBtn.setSelected(true);        
        geneSymbolBtn = new JRadioButton(LABEL_SEARCH_GENE_SYMBOL);
        geneIdentifierBtn = new JRadioButton(LABEL_SEARCH_GENE_IDENTIFIER);
        proteinIdentifierBtn = new JRadioButton(LABEL_SEARCH_PROTEIN_IDENTIFIER);
        definitionBtn = new JRadioButton(LABEL_SEARCH_DEFINITION);
        getAllBooksBtn = new JRadioButton(LABEL_GET_FULL_LIST);
        getBooksWithStatusApply = new JRadioButton(LABEL_GET_BOOKS_WITH_STATUS_APPLY);
        getBooksWithStatusUnused = new JRadioButton(LABEL_GET_BOOKS_WITH_STATUS_DISUSED);
//        getAllUncurtedBtn = new JRadioButton(LABEL_GET_UNCURATED_LIST);
//        getRequirePaintReviewBtn = new JRadioButton(LABEL_GET_REQUIRE_PAINT_REVIEW_LIST);        
        getBooksByPTNBtn = new JRadioButton(LABEL_GET_BOOK_BY_PTN);        
        

        ButtonGroup bg = new ButtonGroup();
        bg.add(getBookByIdBtn);
        bg.add(geneSymbolBtn);
        bg.add(geneIdentifierBtn);
        bg.add(proteinIdentifierBtn);
        bg.add(definitionBtn);
        bg.add(getAllBooksBtn);
        bg.add(getBooksWithStatusApply);
        bg.add(getBooksWithStatusUnused);        
//        bg.add(getAllUncurtedBtn);
//        bg.add(getRequirePaintReviewBtn);
        bg.add(getBooksByPTNBtn);
        searchTypePanel.add(getBookByIdBtn);        
        searchTypePanel.add(geneSymbolBtn);
        searchTypePanel.add(geneIdentifierBtn);
        searchTypePanel.add(proteinIdentifierBtn);
        searchTypePanel.add(definitionBtn);
        searchTypePanel.add(getAllBooksBtn);
        searchTypePanel.add(getBooksWithStatusApply);
        searchTypePanel.add(getBooksWithStatusUnused);
//        searchTypePanel.add(getAllUncurtedBtn);
//        searchTypePanel.add(getRequirePaintReviewBtn);
        searchTypePanel.add(getBooksByPTNBtn);        
//        double maxWidth = 0;
//        double maxHeight = 0;
//        maxWidth += geneSymbolBtn.getPreferredSize().getWidth();
//        maxWidth += geneIdentifierBtn.getPreferredSize().getWidth();
//        maxWidth += proteinIdentifierBtn.getPreferredSize().getWidth();
//        maxHeight += geneSymbolBtn.getPreferredSize().getHeight();
//        maxHeight += geneIdentifierBtn.getPreferredSize().getHeight();
//        maxHeight += proteinIdentifierBtn.getPreferredSize().getHeight();
//        maxHeight += definitionBtn.getPreferredSize().getHeight();
//        maxHeight += getAllBooksBtn.getPreferredSize().getHeight();
//        maxHeight += getBookByIdBtn.getPreferredSize().getHeight();
//        maxHeight += getAllUncurtedBtn.getPreferredSize().getHeight();
//        maxHeight += getRequirePaintReviewBtn.getPreferredSize().getHeight();        
//        Dimension maxSize = new Dimension();
//        maxSize.setSize(maxWidth, maxHeight);
        //searchTypePanel.setMaximumSize(maxSize);
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
        centerPanel.add(searchTypePanel);


        // Submit panel
        JButton submitBtn = new JButton(LABEL_SUBMIT);
        submitBtn.addActionListener(new SearchActionListener());
        JPanel submitPanel = new JPanel();
        submitPanel.add(submitBtn);


        searchPanel.add(searchTermPanel);
        searchPanel.add(centerPanel);
        searchPanel.add(submitPanel);
    }
    
    
    protected void initializeMyBooksPanel() {
        myBooksPanel = new JPanel();
        myBooksPanel.setLayout(new BoxLayout(myBooksPanel, BoxLayout.Y_AXIS));
        
        myBooksStatusLabel = new JLabel(JLABEL_MY_BOOKS_RETRIEVING_BOOKS);
        myBooksStatusLabel.setPreferredSize(myBooksStatusLabel.getPreferredSize());
        
        myBooksPanel.add(myBooksStatusLabel);
        
        
        myBooksTable = new JTable(new MyBooksTableModel(new ArrayList<Book>(), COLUMN_NAMES_MY_BOOKS, COLUMN_TYPES_MY_BOOKS));
        myBooksTable.setDefaultRenderer(JButton.class, new ButtonCellRenderer(BUTTON_LABEL_VIEW));
        myBooksTable.addMouseListener(new LaunchBtnMouseAdapter(myBooksTable));
        myBooksTable.setPreferredScrollableViewportSize(new Dimension(850, 90));
        JScrollPane myBooksScrollPane = new JScrollPane(myBooksTable);
        myBooksPanel.add(myBooksScrollPane);
        
        
        // Submit panel
        JButton selectAllBtn = new JButton(LABEL_SELECT_ALL);
        selectAllBtn.addActionListener(new SelectAllListener());
        JButton deselectAllBtn = new JButton(LABEL_DESELECT_ALL);
        deselectAllBtn.addActionListener(new DeselectAllListener());
        JButton unlockSelectedBtn = new JButton(LABEL_UNLOCK_SELECTED_BOOKS);
        unlockSelectedBtn.addActionListener(new UnlockSelectedBooksListener());
        JPanel submitPanel = new JPanel();
        submitPanel.add(selectAllBtn);
        submitPanel.add(deselectAllBtn);
        submitPanel.add(unlockSelectedBtn);
        myBooksPanel.add(submitPanel);
        
//        JPanel unlockMyBooksPanel = new JPanel();
//        JButton unlockBooksBtn = new JButton(LABEL_UNLOCK_SELECTED_BOOKS);
//        unlockBooksBtn.addActionListener(new UnlockSelectedBooksActionListener());
//        unlockMyBooksPanel.add(unlockBooksBtn);
//        myBooksPanel.add(unlockMyBooksPanel);        
        

    }
    
    

    
    protected void populateMyBooksList() {

        // Temporarily disable feature
//        if (0 == 0) {
//            JOptionPane.showMessageDialog(ManageBooksDlg.this.frame, "Functionality to view locked books and unlock is disabled", MSG_HEADER_MY_BOOKS, JOptionPane.INFORMATION_MESSAGE);
//            mainPanel.setSelectedIndex(0);
//            return;            
//        }


        ArrayList<Book> myBooks = getMyBooks();
        if (null == myBooks) {
            JOptionPane.showMessageDialog(ManageBooksDlg.this.frame, MSG_NO_LOCKED_BOOKS_FOUND, MSG_HEADER_MY_BOOKS, JOptionPane.INFORMATION_MESSAGE);
            mainPanel.setSelectedIndex(0);
            return;
        }
        
        displayMyBooks(myBooks);
        
        
    
    }
    
    


    public void displayMyBooks(ArrayList<Book> myBooks) {
        myBooksStatusLabel.setText(JLABEL_MY_BOOKS_SELECT_BOOKS_TO_UNLOCK);
        myBooksStatusLabel.invalidate();

        MyBooksTableModel mbtm =
            new MyBooksTableModel(myBooks, COLUMN_NAMES_MY_BOOKS, COLUMN_TYPES_MY_BOOKS);
        myBooksTable.setModel(mbtm);
        myBooksTable.setAutoCreateRowSorter(true);
        TableModelEvent le = new TableModelEvent(mbtm);
        mbtm.fireTableChanged(le);


    }

    public ArrayList<Book> getMyBooks() {
        Vector sendInfo = new Vector(2);
        sendInfo.add(ManageBooksDlg.this.searchTerm.getText());
        //sendInfo.add(dbClsId);
        DataTransferObj dto = new DataTransferObj();
        dto.setVc(PaintManager.inst().getVersionContainer());
        dto.setObj(sendInfo);
        DataTransferObj serverObj = PantherServer.inst().searchAllBooks(servletUrl, dto, null, null);

        if (null == serverObj) {
            JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                    MSG_SERVER_ERROR_CANNOT_SEARCH_BOOKS,
                    MSG_HEADER_MY_BOOKS,
                    JOptionPane.ERROR_MESSAGE);
            return new ArrayList<Book>();
        }
        StringBuffer sb = serverObj.getMsg();
        if (null != sb && 0 != sb.length()) {
            JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                    sb.toString(),
                    MSG_HEADER_MY_BOOKS,
                    JOptionPane.ERROR_MESSAGE);
            return new ArrayList<Book>();
        }

        Vector serverRtnList = (Vector) serverObj.getObj();
        if (null == serverRtnList || 2 < serverRtnList.size()) {
            JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                    MSG_SERVER_ERROR_CANNOT_SEARCH_BOOKS,
                    MSG_HEADER_SEARCH_BOOKS,
                    JOptionPane.ERROR_MESSAGE);
            return new ArrayList<Book>();
        }
        ArrayList<Book> bookList = (ArrayList<Book>) serverRtnList.get(1);
        addUniprotInfoToBooks(bookList);
        HashSet<Book> removeSet = new HashSet<Book>();
        User currentUser = PaintManager.inst().getUser();
        if (null == currentUser || null == currentUser.getLoginName()) {
            JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                    MSG_SERVER_ERROR_CANNOT_SEARCH_BOOKS,
                    MSG_HEADER_SEARCH_BOOKS,
                    JOptionPane.ERROR_MESSAGE);
            return new ArrayList<Book>();
        }
        String login = currentUser.getLoginName();
        for (Book b : bookList) {
            User u = b.getLockedBy();
            if (null != u && u.getLoginName() != null && u.getLoginName().equals(login)) {
                continue;
            }
            removeSet.add(b);
        }
        bookList.removeAll(removeSet);
        return bookList;
    }  
    
    protected void setData(ArrayList <Book> books) {
        SearchBookTableModel sbtm = new SearchBookTableModel(books, COLUMN_NAMES_SEARCH, COLUMN_TYPES_SEARCH);
        searchBooksTable.setModel(sbtm);
        searchBooksTable.setAutoCreateRowSorter(true);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(searchBooksTable.getModel());
        searchBooksTable.setRowSorter(sorter);
//        sorter.setSortable(nonSortCol, false);
//        sorter.sort();
        TableModelEvent le = new TableModelEvent(sbtm);
        sbtm.fireTableChanged(le);


    }    

    protected void initializeBookListPanel() {

        bookListPanel = new JPanel();
        bookListPanel.setLayout(new BoxLayout(bookListPanel,
                                                    BoxLayout.Y_AXIS));


        searchBooksTable = new JTable(new SearchBookTableModel(new ArrayList<Book>(), COLUMN_NAMES_SEARCH, COLUMN_TYPES_SEARCH)) {
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = ManageBooksDlg.this.searchBooksTable.rowAtPoint(p);
                int colIndex = ManageBooksDlg.this.searchBooksTable.columnAtPoint(p);
                JTable source = (JTable)e.getSource();
            int row = ManageBooksDlg.this.searchBooksTable.convertRowIndexToModel(rowIndex);
                int realColumnIndex = ManageBooksDlg.this.searchBooksTable.convertColumnIndexToModel(colIndex);

//                if (realColumnIndex == COL_INDEX_COMMENT) {
//                    BookTableModel model = (BookTableModel) searchBooksTable.getModel();
//                    Book aBook = model.data.get(row);
//                    tip = aBook.getCommentUser();
//                    if (null != tip) {
//                        tip = tip.replaceAll(LINE_BREAK, HTML_LINEBREAK);
//                        tip = HTML_START + tip + HTML_END;
//                    }
//                }
                if (null == tip) {
                    return Constant.STR_EMPTY;
                }
                return tip;
            }
            
            // Color checkboxes to inform users about their privilege levels
            public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
                Component comp = super.prepareRenderer(renderer, rowIndex, vColIndex);
                int row = ManageBooksDlg.this.searchBooksTable.convertRowIndexToModel(rowIndex);
                int realColumnIndex = ManageBooksDlg.this.searchBooksTable.convertColumnIndexToModel(vColIndex);

                if (realColumnIndex == COL_INDEX_LOCK_UNLOCK) {
                    if (false == isCellEditable(row, realColumnIndex)) {
                        comp.setBackground(COLOR_UNALLOWED);                      
                    }
                    else {
                        comp.setBackground(COLOR_PERMITTED);                      
                        User currentUser = PaintManager.inst().getUser();
                        if (null != currentUser && User.ROLE_ADMIN.equals(currentUser.getRole())) {
                            // If current user has admin role, but is not same as user locking book, warn user.
                            BookTableModel model = (BookTableModel) searchBooksTable.getModel();
                            Book aBook = model.data.get(row);
                            User lockedBy = aBook.getLockedBy();
                            if (null != lockedBy && false == lockedBy.getLoginName().equals(currentUser.getloginName())) {
                                comp.setBackground(COLOR_CAUTION);
                            }
                        }
                    }

//                    User currentUser = PaintManager.inst().getUser();
//                    if (null == currentUser) {
//                        comp.setBackground(COLOR_UNALLOWED);
//                        return comp;
//                    }
//                    String role = currentUser.getRole();
//                    BookTableModel model = (BookTableModel) searchBooksTable.getModel();
//                    Book aBook = model.data.get(row);
//                    int curationStatus = aBook.getCurationStatus();
//                    if (Book.CURATION_STATUS_UNKNOWN == curationStatus) {
//                        comp.setBackground(COLOR_UNALLOWED);
//                        return comp;
//                    }
//                    // Ensure it is one of the valid statuses
//                    if (Book.CURATION_STATUS_TEST != curationStatus && Book.CURATION_STATUS_APPLY != curationStatus && Book.CURATION_STATUS_DISUSED != curationStatus) {
//                        comp.setBackground(COLOR_UNALLOWED);
//                        return comp;
//                    }
//                    
//                    User lockedBy = aBook.getLockedBy();
//                    if (role.equals(User.ROLE_OBSERVER)) {
//                        comp.setBackground(COLOR_UNALLOWED);
//                        return comp;
//                    } else if (role.equals(User.ROLE_ADMIN)) {
//                        // Admin user can lock or unlock any book.  Just warn admin that someone has already locked the book
//                        if (null == lockedBy ||
//                            (null != lockedBy && true == currentUser.getloginName().equals(lockedBy.getloginName()))) {
//                            comp.setBackground(COLOR_PERMITTED);
//                            return comp;
//                        }
//                        comp.setBackground(COLOR_CAUTION);
//                        return comp;
//                    } else {
//                        if (role.equals(User.ROLE_USER)) {
//                            if (null == lockedBy) {
//                                int status = aBook.getCurationStatus();
//                                if (Book.CURATION_STATUS_TEST == status) {
//                                    comp.setBackground(COLOR_PERMITTED);
//                                    return comp;
//                                }
//                                comp.setBackground(COLOR_UNALLOWED);
//                                return comp;
//                            } else {
//                                if (true == currentUser.getloginName().equals(lockedBy.getloginName())) {
//                                    comp.setBackground(COLOR_PERMITTED);
//                                    return comp;
//                                }
//                                comp.setBackground(COLOR_UNALLOWED);
//                                return comp;
//                            }
//                        } else if (role.equals(User.ROLE_CURATOR)) {
//                            if (null == lockedBy) {
//                                comp.setBackground(COLOR_PERMITTED);
//                                return comp;
//                            } else if (true == currentUser.getloginName().equals(lockedBy.getloginName())) {
//                                comp.setBackground(COLOR_PERMITTED);
//                                return comp;
//                            } else {
//                                comp.setBackground(COLOR_UNALLOWED);
//                                return comp;
//                            }
//                        }
//                        else {
//                            comp.setBackground(COLOR_UNALLOWED);
//                            return comp;
//                        }
//                    }
                }
                return comp;
            }

        };
        searchBooksTable.setDefaultRenderer(JButton.class, new ButtonCellRenderer(BUTTON_LABEL_VIEW));    
        searchBooksTable.addMouseListener(new LaunchBtnMouseAdapter(searchBooksTable));        
        searchBooksTable.setPreferredScrollableViewportSize(new Dimension(850, 90));


        JScrollPane lockedScrollPane = new JScrollPane(searchBooksTable);
        //lockedBooksTable.setFillsViewportHeight(true);


        JLabel booksLabel = new JLabel("Books");
        booksLabel.setPreferredSize(booksLabel.getPreferredSize());

        bookListPanel.add(booksLabel);
        bookListPanel.add(lockedScrollPane);


//        unlockedBookListPanel = new JPanel();
//        unlockedBookListPanel.setLayout(new BoxLayout(unlockedBookListPanel,
//                                                      BoxLayout.Y_AXIS));
//        unlockedBooksTable =
//                new JTable(new UnlockedBookTableModel(new Vector(), COLUMN_NAMES_UNLOCKED, COLUMN_TYPES_UNLOCKED));
//        unlockedBooksTable.setPreferredScrollableViewportSize(new Dimension(700,
//                                                                            70));
//        unlockedBooksTable.setDefaultRenderer(JButton.class, new ButtonCellRenderer(BUTTON_LABEL_VIEW));
//        unlockedBooksTable.addMouseListener(new LaunchBtnMouseAdapter(unlockedBooksTable, 4));


//        JScrollPane unlockedScrollPane = new JScrollPane(unlockedBooksTable);

//        JLabel unlockedBooksLabel = new JLabel("Books Available for Locking");
//        unlockedBooksLabel.setPreferredSize(unlockedBooksLabel.getPreferredSize());
//        unlockedBookListPanel.add(unlockedBooksLabel);
//        unlockedBookListPanel.add(unlockedScrollPane);

        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel,
                                               BoxLayout.Y_AXIS));
        containerPanel.add(bookListPanel);
//        containerPanel.add(unlockedBookListPanel);

        booksPanel = new JPanel();
        booksPanel.setLayout(new BorderLayout());
        booksPanel.add(containerPanel, BorderLayout.CENTER);

        JButton lockUnlockBooks = new JButton("Lock or Unlock selected Books");
        lockUnlockBooks.addActionListener(new LockUnlockSelectedBooksActionListener());

        JPanel lockBooksPanel = new JPanel();
        lockBooksPanel.add(lockUnlockBooks);
        booksPanel.add(lockBooksPanel, BorderLayout.SOUTH);


    }


    abstract class BookTableModel extends AbstractTableModel {
        protected ArrayList <Book> data = null;
        protected String[] columnNames = null;
        protected Class[] columnTypes;
        protected boolean locked[] = null;

        BookTableModel(ArrayList<Book> data, String columnNames[], Class columnTypes[]) {
            this.data = data;
            this.columnNames = columnNames;
            this.columnTypes = columnTypes;
            
            // Initialize the initial locked status of the books
            if (null == data) {
                return;
            }
            int size = data.size();
            locked = new boolean[size];
            for (int i = 0; i < size; i++) {
                Book aBook = data.get(i);
                if (null == aBook) {
                    continue;
                }
                User aUser = aBook.getLockedBy();
                if (null == aUser) {
                    locked[i] = false;
                    continue;
                }
                locked[i] = true;
                
            }


        }
        
        
        public Book getBookAtRow(int row) {
            return data.get(row);
        }
        
        ArrayList<String> booksForLocking() {
            if (null == data) {
                return null;
            }
            ArrayList<String> lockBooksList = new ArrayList<String>();
            for (int i = 0; i < locked.length; i++) {
                if (false == locked[i]) {
                    continue;
                }
                Book aBook = data.get(i);
                User u = aBook.getLockedBy();
                if (null == u) {
                    lockBooksList.add(aBook.getId());
                }
                
                
            }
            return lockBooksList;
        }
        
        ArrayList<String> booksForUnlocking(String loginName) {
            if (null == data || null == loginName) {
                return null;
            }
            ArrayList<String> unlockBooksList = new ArrayList<String>();
            for (int i = 0; i < locked.length; i++) {
                if (true == locked[i]) {
                    continue;
                }
                Book aBook = data.get(i);
                User u = aBook.getLockedBy();
                if (null == u) {
                    continue;
                }

                if (true == u.getloginName().equals(loginName)) {
                    unlockBooksList.add(aBook.getId());
                }
            }
            return unlockBooksList;
        }
        
        
        public int getColumnCount() {
            return columnNames.length;
        }


        public String getColumnName(int col) {
            return columnNames[col];
        }
        
        
        // If this method is not implemented, the check box entry will appear as true or false text
        public Class getColumnClass(int c) {
            return columnTypes[c];
        }


        public int getRowCount() {
            return data.size();
        }






        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */

        public void setValueAt(Object value, int row, int col) {
            if (DEBUG) {
//                System.out.println("Setting value at " + row + "," + col +
//                                   " to " + value + " (an instance of " +
//                                   value.getClass() + ")");
            }
            Object oldValue = getValueAt(row, col);
            if (false == oldValue instanceof Boolean) {
                return;
            }
            

            locked[row] = ((Boolean)value).booleanValue();
            
            // Lock book for user

            //            data[row][col] = value;
            fireTableCellUpdated(row, col);

            //            if (DEBUG) {
            //                System.out.println("New value of data:");
            //                printDebugData();
            //            }
        }


        private void printDebugData() {
            //            int numRows = getRowCount();
            //            int numCols = getColumnCount();
            //
            //            for (int i=0; i < numRows; i++) {
            //                System.out.print("    row " + i + ":");
            //                for (int j=0; j < numCols; j++) {
            //                    System.out.print("  " + data[i][j]);
            //                }
            //                System.out.println();
            //            }
            //            System.out.println("--------------------------");
        }


    }


    class SearchBookTableModel extends BookTableModel {
        HashSet<String> curatableBookSet = null;
        
        public SearchBookTableModel(ArrayList<Book> data, String columnNames[], Class columnTypes[]) {
            super(data, columnNames, columnTypes);
            curatableBookSet = PaintManager.inst().getCuratableBookSet();
            if (null == curatableBookSet) {
                curatableBookSet = new HashSet<String>();
            }
        }
        
        
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            Object aValue = getValueAt(rowIndex, columnIndex);
            if (false == aValue instanceof Boolean) {
                return false;
            }
            String header = COLUMN_NAMES_SEARCH[columnIndex];
            if (COLUMN_NAME_EXP_EVDNCE.equals(header)) {
                return false;
            }
            
//            if (COLUMN_NAME_NOTES.equals(header)) {
//                return false;
//            }
            
            if (COLUMN_NAME_ORG.equals(header)) {
                return false;
            }
            
            if (COLUMN_NUM_LEAVES.equals(header)) {
                return false;
            }
            
            if (COLUMN_NAME_LOCK_UNLOCK.equals(header)) {
                // Cannot lock books with no status
                Book aBook = data.get(rowIndex);
                int curationStatus = aBook.getCurationStatus();
                if (Book.CURATION_STATUS_UNKNOWN == curationStatus) {
                    return false;
                }
                
                // Ensure it is one of the valid statuses
                if (Book.CURATION_STATUS_TEST != curationStatus && Book.CURATION_STATUS_APPLY != curationStatus && Book.CURATION_STATUS_DISUSED != curationStatus) {
                    return false;
                }
                User currentUser = PaintManager.inst().getUser();
                if (null == currentUser) {
                    return false;
                }
                String loginName = currentUser.getLoginName();
                if (null == loginName) {
                    return false;
                }
                String role = currentUser.getRole();
                if (role.equals(User.ROLE_OBSERVER)) {
                    return false;
                }
                else if (role.equals(User.ROLE_ADMIN)) {
                    return true;
                }
                else {                   
                    User lockedBy = aBook.getLockedBy();
                    if (role.equals(User.ROLE_USER)) {
                        if (null == lockedBy) {
                            int status = aBook.getCurationStatus();
                            // Test status with last modifier same as current user
                            User modifiedBy = aBook.getModifiedBy();
                            if ((Book.CURATION_STATUS_TEST == status && (null != modifiedBy && loginName.equals(modifiedBy.getLoginName()))) ) {
                                return true;
                            }
                            return false;
                        }
                        else {
                            if (true == loginName.equals(lockedBy.getloginName())) {
                                return true;
                            }
                            return false;
                        }
                    }
                    else if (role.equals(User.ROLE_CURATOR)) {
                        if (null == lockedBy) {
                            return true;
                        }
                        else if (true == loginName.equals(lockedBy.getloginName())) {
                            return true;
                        }
                        return false;
                    }
                    else {
                        return false;
                    }
                }
            }
            return false;
            
        }

        public Object getValueAt(int row, int col) {
            Book aBook = data.get(row);
            String header = COLUMN_NAMES_SEARCH[col];
            if (COLUMN_NAME_BOOK_ID.equals(header)) {
                return aBook.getId();
            }
            else if (COLUMN_NAME_NAME.equals(header)) {
                return aBook.getName();
            }
            else if (COLUMN_NAME_EXP_EVDNCE.equals(header)) {
                String id = aBook.getId();
                if (null != id && curatableBookSet.contains(id)) {
                    return Boolean.valueOf(true);
                }
                return Boolean.valueOf(false);   
            }
//            else if (COLUMN_NAME_NOTES.equals(header)) {
//                String comment = aBook.getCommentUser();
//                if (null != comment && 0 != comment.trim().length()) {
//                    return Boolean.valueOf(true);
//                }
//                else {
//                    return Boolean.valueOf(false);
//                }
//            }
            else if (COLUMN_NAME_ORG.equals(header)) {
                HashSet<String> orgSet = aBook.getOrgSet();
                if (null == orgSet || 0 == orgSet.size()) {
                    return Constant.STR_EMPTY;
                } 
                StringBuffer sb = new StringBuffer();
                for (String org: ORG_LIST_SPECIAL) {
                    if (true == orgSet.contains(org)) {
                        if (0 != sb.length()) {
                            sb.append(Constant.STR_COMMA);
                            sb.append(Constant.STR_SPACE);
                        }
                        sb.append(org);
                    }
                }
                return sb.toString();
            }
            else if (COLUMN_NUM_LEAVES.equals(header)) {
                return aBook.getNumLeaves();
            }
            else if (COLUMN_NAME_CURATION_STATUS.equals(header)) {
                return aBook.getCurationStatusString(aBook.getCurationStatus());              
            }
            else if (COLUMN_NAME_LOCKED_BY.equals(header)) {
                User u = aBook.getLockedBy();
                if (null == u) {
                    return Constant.STR_EMPTY;
                }
                if (null != u.getFirstName() && 0 != u.getFirstName().length()) {
                    return u.getFirstName();
                }
                if (null != u.getLastName() && 0 != u.getLastName().length()) {
                    return u.getLastName();
                }
                return u.getLoginName();
            }
            else if (COLUMN_NAME_LAST_MODIFIED_BY.equals(header)) {
                User u = aBook.getModifiedBy();
                if (null == u) {
                    return Constant.STR_EMPTY;
                }
                if (null != u.getFirstName() && 0 != u.getFirstName().length()) {
                    return u.getFirstName();
                }
                if (null != u.getLastName() && 0 != u.getLastName().length()) {
                    return u.getLastName();
                }
                return u.getLoginName();                
            }
//            else if (COLUMN_NAME_DATE.equals(header)) {
//                return aBook.getCurationStatusUpdateDate();
//            }
            else if (COLUMN_NAME_OPEN.equals(header)) {
                return Constant.STR_EMPTY;
            }
            else if (COLUMN_NAME_LOCK_UNLOCK.equals(header)) {
                
                return Boolean.valueOf(locked[row]);
                
            }
            else {
                System.out.println("Search books model requesting data for unhandled column " + header);
                return null;
            }
        }
        

    }
//
//    class UnlockedBookTableModel extends BookTableModel {
//
//        public UnlockedBookTableModel(Vector data, String columnNames[], Class columnTypes[]) {
//            super(data, columnNames, columnTypes);
//        }
//        /*
//         * Don't need to implement this method unless your table's
//         * editable.
//         */
//
//        public boolean isCellEditable(int row, int col) {
//            //Note that the data/cell address is constant,
//            //no matter where the cell appears on screen.
//
//            if (UNLOCKED_COLUMN_LOCK == col) {
//                return true;
//            }
//
//            return false;
//
//        }
//        
//
//
//
//
//        public Vector getSelectedBooks() {
//            Vector selectedBooks = new Vector();
//            for (int i = 0; i < data.size(); i++) {
//                Vector row = (Vector)data.elementAt(i);
//                Boolean selected = (Boolean)row.get(UNLOCKED_COLUMN_LOCK);
//                if (true == selected.booleanValue()) {
//                    selectedBooks.add(row.elementAt(UNLOCKED_COLUMN_BOOK_ID));
//                }
//            }
//            if (selectedBooks.isEmpty()) {
//                return null;
//            }
//            return selectedBooks;
//
//        }
//    }


    class MyBooksTableModel extends BookTableModel {

        public MyBooksTableModel(ArrayList<Book> data, String columnNames[], Class columnTypes[]) {
            super(data, columnNames, columnTypes);
        }
        /*
         * Don't need to implement this method unless your table's
         * editable.
         */

        public boolean isCellEditable(int row, int col) {
            Object o = getValueAt(row, col);
            if (false == o instanceof Boolean) {
                return false;
            }

            return true;

        }
        
        public Object getValueAt(int row, int col) {
            Book aBook = data.get(row);
            String header = COLUMN_NAMES_MY_BOOKS[col];
            if (COLUMN_NAME_BOOK_ID.equals(header)) {
                return aBook.getId();
            }
            else if (COLUMN_NAME_NAME.equals(header)) {
                return aBook.getName();
            }

            else if (COLUMN_NAME_CURATION_STATUS.equals(header)) {
                return aBook.getCurationStatusString(aBook.getCurationStatus());
            }

            else if (COLUMN_NAME_OPEN.equals(header)) {
                return Constant.STR_EMPTY;
            }
            else if (COLUMN_NAME_UNLOCK.equals(header)) {
                
                return Boolean.valueOf(!locked[row]);
                
            }
            else {
                
                System.out.println("MyBooksTableModel requesting data for unhandled column " + header);
                return null;
            }
        }
        
        public void setValueAt(Object value, int row, int col) {
            Object oldValue = getValueAt(row, col);
            if (false == oldValue instanceof Boolean) {
                return;
            }
            

            locked[row] = !((Boolean)value).booleanValue();
            
            // Lock book for user

            //            data[row][col] = value;
            fireTableCellUpdated(row, col);

            //            if (DEBUG) {
            //                System.out.println("New value of data:");
            //                printDebugData();
            //            }
        }
        
        
        public int getUnLockCol() {
            if(null == columnNames) {
                return -1;
            }
            for (int i = 0; i < columnNames.length; i++) {
                if (columnNames[i].equals(COLUMN_NAME_UNLOCK)) {
                    return i;
                }
            }
            return -1;
            
        }

        
        



        public Vector getSelectedBooks() {
            Vector selectedBooks = new Vector();
//            for (int i = 0; i < data.size(); i++) {
//                Vector row = (Vector)data.elementAt(i);
//                Boolean selected = (Boolean)row.get(MYBOOKS_COLUMN_LOCK);
//                if (true == selected.booleanValue()) {
//                    selectedBooks.add(row.elementAt(MYBOOKS_COLUMN_BOOK_ID));
//                }
//            }
//            if (selectedBooks.isEmpty()) {
//                return null;
//            }
            return selectedBooks;

        }
    }


    public class SearchActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String searchStr = ManageBooksDlg.this.searchTerm.getText();
            if ((null == searchStr || 0 == searchStr.length()) &&
                 false == ManageBooksDlg.this.getAllBooksBtn.isSelected() &&
                 false == ManageBooksDlg.this.getBooksWithStatusApply.isSelected() &&
                 false == ManageBooksDlg.this.getBooksWithStatusUnused.isSelected()                    
//                    &&
//                 false == ManageBooksDlg.this.getAllUncurtedBtn.isSelected() &&
//                 false == ManageBooksDlg.this.getRequirePaintReviewBtn.isSelected()
               ) {
                JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                                              MSG_PLEASE_ENTER_SEARCH_TERM,
                                              MSG_HEADER_SEARCH_BOOKS,
                                              JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Need to get information from 2 sources: paint server and uniprot. Afterwards, combine.
            // Get the information simultaenously.  Once, both processses have completed, combine.
            ExecutorService executor = Executors.newFixedThreadPool(2);

            SearchFromPaintServer searchPaintServerWorker = new SearchFromPaintServer(searchStr);
            executor.execute(searchPaintServerWorker);

            LoadBookStatus loadBookStatusWorker = new LoadBookStatus();
            executor.execute(loadBookStatusWorker);
            executor.shutdown();

            // Wait until all threads finish
            while (!executor.isTerminated()) {

            }

            if (false == searchPaintServerWorker.opSuccess.booleanValue()) {
                JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                                              MSG_SERVER_ERROR_CANNOT_SEARCH_BOOKS,
                                              MSG_HEADER_SEARCH_BOOKS,
                                              JOptionPane.ERROR_MESSAGE);
                ManageBooksDlg.this.setData(new ArrayList<Book>());
                return;                
            }
            else if (false == loadBookStatusWorker.opSuccess.booleanValue()) {
                JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                                              MSG_SERVER_ERROR_CANNOT_SEARCH_UNIRULE_BOOK_STATUS,
                                              MSG_HEADER_SEARCH_BOOKS,
                                              JOptionPane.ERROR_MESSAGE);
                ManageBooksDlg.this.setData(new ArrayList<Book>());
                return;                
            }
            DataTransferObj infoFromServer = searchPaintServerWorker.infoFromServer;
            if (null == infoFromServer){
                JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                                              MSG_SERVER_ERROR_CANNOT_SEARCH_BOOKS,
                                              MSG_HEADER_SEARCH_BOOKS,
                                              JOptionPane.ERROR_MESSAGE);
                ManageBooksDlg.this.setData(new ArrayList<Book>());
                return;
            }
            StringBuffer sb = infoFromServer.getMsg();
            if (null != sb && 0 != sb.length()) {
                JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                        sb.toString(),
                        MSG_HEADER_MY_BOOKS,
                        JOptionPane.ERROR_MESSAGE);
                ManageBooksDlg.this.setData(new ArrayList<Book>());
                return;
            }


            Vector serverRtnList = (Vector)infoFromServer.getObj();
            if (null == serverRtnList || 2 < serverRtnList.size()) {
                        JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                        MSG_SERVER_ERROR_CANNOT_SEARCH_BOOKS,
                        MSG_HEADER_SEARCH_BOOKS,
                        JOptionPane.ERROR_MESSAGE);
                ManageBooksDlg.this.setData(new ArrayList<Book>());                
                return;
            }

            ArrayList<Book> bookList = (ArrayList<Book>) serverRtnList.get(1);
            if (null == bookList || 0 == bookList.size()) {
                JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                                              MSG_NO_BOOKS_FOR_SEARCH_CRITERIA,
                                              MSG_HEADER_SEARCH_BOOKS,
                                              JOptionPane.ERROR_MESSAGE);
                ManageBooksDlg.this.setData(new ArrayList<Book>());
                return;
                
            }

            // Update status and locked by, modified by information
            ArrayList<Book> books = null;
            if (false == ManageBooksDlg.this.getBooksWithStatusApply.isSelected() && false == ManageBooksDlg.this.getBooksWithStatusUnused.isSelected()) {
                ArrayList<Book> statusList = loadBookStatusWorker.books;
                for (Book b : bookList) {
                    String id = b.getId();
                    for (Book statusInfo : statusList) {
                        String compId = statusInfo.getId();
                        if (id.equals(compId)) {
                            b.setCurationStatus(statusInfo.getCurationStatus());
                            b.setLockedBy(statusInfo.getLockedBy());
                            b.setModifiedBy(statusInfo.getModifiedBy());
                            break;
                        }
                    }
                }
            }
            else {
                ArrayList<Book> statusList = loadBookStatusWorker.books;
                ArrayList<Book> removeList = new ArrayList<Book>();
                for (Book b : bookList) {
                    boolean found = false;
                    String id = b.getId();
                    for (Book statusInfo : statusList) {
                        String compId = statusInfo.getId();
                        if (id.equals(compId)) {
                            b.setCurationStatus(statusInfo.getCurationStatus());
                            b.setLockedBy(statusInfo.getLockedBy());
                            b.setModifiedBy(statusInfo.getModifiedBy());
                            found = true;
                            break;
                        }
                    }
                    if (false == found) {
                        removeList.add(b);
                    }
                }
                bookList.removeAll(removeList);
            }
                    
            // Save the last valid search parameters.  When user locks or unlocks books, this data is needed to refresh the book list
            lastValidSearchStr = searchStr;
            if (ManageBooksDlg.this.geneSymbolBtn.isSelected()) {
                lastValidSearchBtn = ManageBooksDlg.this.geneSymbolBtn;
            } else if (ManageBooksDlg.this.geneIdentifierBtn.isSelected()) {
                lastValidSearchBtn = ManageBooksDlg.this.geneIdentifierBtn;
            } else if (ManageBooksDlg.this.proteinIdentifierBtn.isSelected()) {
                lastValidSearchBtn = ManageBooksDlg.this.proteinIdentifierBtn;
            }
            else if(ManageBooksDlg.this.definitionBtn.isSelected()) {
                lastValidSearchBtn = ManageBooksDlg.this.definitionBtn;
            }
            else if (ManageBooksDlg.this.getBookByIdBtn.isSelected()) {
                lastValidSearchBtn = ManageBooksDlg.this.getBookByIdBtn;
            }
            else if (ManageBooksDlg.this.getAllBooksBtn.isSelected()) {
                lastValidSearchBtn = ManageBooksDlg.this.getAllBooksBtn;
            }
            else if (ManageBooksDlg.this.getBooksWithStatusApply.isSelected()) {
                lastValidSearchBtn = ManageBooksDlg.this.getBooksWithStatusApply;
            }
            else if (ManageBooksDlg.this.getBooksWithStatusUnused.isSelected()) {
                lastValidSearchBtn = ManageBooksDlg.this.getBooksWithStatusUnused;
            }            
            else {
                lastValidSearchBtn = ManageBooksDlg.this.getBooksByPTNBtn;
            }            
            
            // Update table
            books = bookList;
            ManageBooksDlg.this.setData(books);

        }
    }

    public class LockUnlockSelectedBooksActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            User u = PaintManager.inst().getUser();
            if (null == u) {
                return;
            }
            String loginName = u.getloginName();
            if (null == loginName) {
                return;
            }

            // Get list of books to lock and unlock
            BookTableModel model = (BookTableModel) searchBooksTable.getModel();
            ArrayList<String> booksForLocking = model.booksForLocking();

            ArrayList<String> booksForUnlocking = model.booksForUnlocking(loginName);

            if (0 == booksForLocking.size() && 0 == booksForUnlocking.size()) {
                JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                        MSG_PLEASE_SELECT_LOCK_UNLOCK_BOOKS,
                        MSG_HEADER_LOCK_BOOKS,
                        JOptionPane.ERROR_MESSAGE);

                return;
            }

            // Lock and unlock selected books
            String msg = UniruleSoapHelper.lockUnlockBooks(booksForLocking, booksForUnlocking);
            if (null != msg) {
                JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                        msg,
                        MSG_HEADER_LOCK_UNLOCK_BOOKS,
                        JOptionPane.ERROR_MESSAGE);               
            }
            searchAgain();
        }
    }
        
    public class PanelChangeListener implements ChangeListener {
        public void stateChanged(ChangeEvent evt) {
            JTabbedPane pane = (JTabbedPane)evt.getSource();
        
            // Get current tab
            int sel = pane.getSelectedIndex();
            

            if (1 == sel) {
                // If my books list has been selected, populate my books list
                populateMyBooksList();
                
            }
            else if (0 == sel) {
                searchAgain();
            }

        }
        
    }
    
    public class ButtonCellRenderer implements TableCellRenderer {
        JButton launchButton;
        String label;
        
        public ButtonCellRenderer(String label) {
            this.label = label;
        }
        public Component getTableCellRendererComponent(JTable table, Object value, 
                                                       boolean isSelected, 
                                                       boolean hasFocus, int row, 
                                                       int column) {
                                                       
            if (null == launchButton) {
                launchButton = new JButton(label);
            }
            return launchButton;

        }
        
        
    }
    
    public class LaunchBtnMouseAdapter extends MouseAdapter {
        JTable table;

        
        public LaunchBtnMouseAdapter(JTable table) {
          this.table = table;
        }
         
        public void mouseClicked(MouseEvent e) {
            TableModel tm = table.getModel();
            Point p = e.getPoint();
            int column = table.columnAtPoint(p);
            int row = table.rowAtPoint(p);
            int convertRow = table.convertRowIndexToModel(row);  
            if (column < 0 || convertRow < 0) {
                return;
            }
            if (tm instanceof MyBooksTableModel) {
                String header = COLUMN_NAMES_MY_BOOKS[column];
                if (true == header.equals(COLUMN_NAME_OPEN)) {
                    
                    Book aBook = ((BookTableModel)tm).getBookAtRow(convertRow);
                    openBook(aBook);
                }
            }
            else if (tm instanceof SearchBookTableModel) {
                String header = COLUMN_NAMES_SEARCH[column];
                if (true == header.equals(COLUMN_NAME_OPEN)) {
                    
                    Book aBook = ((BookTableModel)tm).getBookAtRow(convertRow);
                    openBook(aBook);
                }
            }

        }
    }
    
    public String display() {
        setVisible(true);
        return openBookId;
    }
    
    public void openBook(Book aBook) {
        System.out.println("Going to open book " + aBook.getId());
        openBookId = aBook.getId();
        lockedBy = aBook.getLockedBy();
        this.setVisible(false);
    }
    
    public String getOpenBookId() {
        return openBookId;
    }
    
    public User getLockedBy() {
        return lockedBy;
    }
    
    public class SelectAllListener implements ActionListener{

        public void actionPerformed(ActionEvent e) {
            MyBooksTableModel model = (MyBooksTableModel)myBooksTable.getModel();
            int col = model.getUnLockCol();
            if (-1 == col) {
                return;
            }
            int numRows = model.getRowCount();
            for (int i = 0; i < numRows; i++) {
                model.setValueAt(Boolean.TRUE, i, col);
            }
        }
    }
    
    public class DeselectAllListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            MyBooksTableModel model = (MyBooksTableModel)myBooksTable.getModel();
            int col = model.getUnLockCol();
            if (-1 == col) {
                return;
            }
            int numRows = model.getRowCount();
            for (int i = 0; i < numRows; i++) {
                model.setValueAt(Boolean.FALSE, i, col);
            }
        }
    }
    
    public class UnlockSelectedBooksListener implements ActionListener{

         public void actionPerformed(ActionEvent e) {
            MyBooksTableModel model = (MyBooksTableModel) myBooksTable.getModel();
            ArrayList<String> booksForUnlocking = model.booksForUnlocking(PaintManager.inst().getUser().getloginName());        // Note, retrieving unlocked books since these are the ones that are selected
            if (0 == booksForUnlocking.size()) {
                JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                        MSG_PLEASE_SELECT_UNLOCK_BOOKS,
                        MSG_HEADER_UNLOCK_BOOKS,
                        JOptionPane.ERROR_MESSAGE);
                populateMyBooksList();
                return;

            }

            String msg = UniruleSoapHelper.lockUnlockBooks(new ArrayList<String>(), booksForUnlocking);
            if (null != msg) {

                JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                        msg,
                        MSG_HEADER_UNLOCK_BOOKS,
                        JOptionPane.ERROR_MESSAGE);
            }
            populateMyBooksList();
        }
    }
    
    public void searchAgain() {
        if (null == lastValidSearchBtn) {
            return;
        }
        ExecutorService executor = Executors.newFixedThreadPool(2);

        SearchFromPaintServer searchPaintServerWorker = new SearchFromPaintServer(lastValidSearchStr);
        executor.execute(searchPaintServerWorker);

        LoadBookStatus loadBookStatusWorker = new LoadBookStatus();
        executor.execute(loadBookStatusWorker);
        executor.shutdown();

        // Wait until all threads finish
        while (!executor.isTerminated()) {

        }
//            }
        if (false == searchPaintServerWorker.opSuccess.booleanValue()) {
            JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                    MSG_SERVER_ERROR_CANNOT_SEARCH_BOOKS,
                    MSG_HEADER_SEARCH_BOOKS,
                    JOptionPane.ERROR_MESSAGE);
            ManageBooksDlg.this.setData(new ArrayList<Book>());
            return;
        } else if (false == loadBookStatusWorker.opSuccess.booleanValue()) {
            JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                    MSG_SERVER_ERROR_CANNOT_SEARCH_UNIRULE_BOOK_STATUS,
                    MSG_HEADER_SEARCH_BOOKS,
                    JOptionPane.ERROR_MESSAGE);
            ManageBooksDlg.this.setData(new ArrayList<Book>());
            return;
        }
        DataTransferObj infoFromServer = searchPaintServerWorker.infoFromServer;
        if (null == infoFromServer) {
            JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                    MSG_SERVER_ERROR_CANNOT_SEARCH_BOOKS,
                    MSG_HEADER_SEARCH_BOOKS,
                    JOptionPane.ERROR_MESSAGE);
            ManageBooksDlg.this.setData(new ArrayList<Book>());
            return;
        }
        StringBuffer sb = infoFromServer.getMsg();
        if (null != sb && 0 != sb.length()) {
            JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                    sb.toString(),
                    MSG_HEADER_MY_BOOKS,
                    JOptionPane.ERROR_MESSAGE);
            ManageBooksDlg.this.setData(new ArrayList<Book>());
            return;
        }

        Vector serverRtnList = (Vector) infoFromServer.getObj();
        if (null == serverRtnList || 2 < serverRtnList.size()) {
            JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                    MSG_SERVER_ERROR_CANNOT_SEARCH_BOOKS,
                    MSG_HEADER_SEARCH_BOOKS,
                    JOptionPane.ERROR_MESSAGE);
            ManageBooksDlg.this.setData(new ArrayList<Book>());
            return;
        }

        ArrayList<Book> bookList = (ArrayList<Book>) serverRtnList.get(1);
        if (null == bookList || 0 == bookList.size()) {
            JOptionPane.showMessageDialog(ManageBooksDlg.this.frame,
                    MSG_NO_BOOKS_FOR_SEARCH_CRITERIA,
                    MSG_HEADER_SEARCH_BOOKS,
                    JOptionPane.ERROR_MESSAGE);
            ManageBooksDlg.this.setData(new ArrayList<Book>());
            return;

        }

        // Update status and locked by information
        ArrayList<Book> books = null;
        if (false == ManageBooksDlg.this.getBooksWithStatusApply.isSelected() && false == ManageBooksDlg.this.getBooksWithStatusUnused.isSelected()) {
            ArrayList<Book> statusList = loadBookStatusWorker.books;
            for (Book b : bookList) {
                String id = b.getId();
                for (Book statusInfo : statusList) {
                    String compId = statusInfo.getId();
                    if (id.equals(compId)) {
                        b.setCurationStatus(statusInfo.getCurationStatus());
                        b.setLockedBy(statusInfo.getLockedBy());
                        break;
                    }
                }
            }
        } else {
            ArrayList<Book> statusList = loadBookStatusWorker.books;
            ArrayList<Book> removeList = new ArrayList<Book>();
            for (Book b : bookList) {
                boolean found = false;
                String id = b.getId();
                for (Book statusInfo : statusList) {
                    String compId = statusInfo.getId();
                    if (id.equals(compId)) {
                        b.setCurationStatus(statusInfo.getCurationStatus());
                        b.setLockedBy(statusInfo.getLockedBy());
                        found = true;
                        break;
                    }
                }
                if (false == found) {
                    removeList.add(b);
                }
            }
            bookList.removeAll(removeList);
        }

        books = bookList;
        if (null != books) {
            setData(books);
        }
    }
    
    public void addUniprotInfoToBooks(ArrayList<Book> books) {
        if (null == books) {
            return;
        }
        ArrayList<Book> statusList = UniruleSoapHelper.getStatusOfAllBooks();
        if (null == statusList) {
            return;
        }
        for (Book b : books) {
            String id = b.getId();
            Book found = null;
            for (Book statusInfo : statusList) {
                String compId = statusInfo.getId();
                if (id.equals(compId)) {
                    b.setCurationStatus(statusInfo.getCurationStatus());
                    b.setLockedBy(statusInfo.getLockedBy());
                    found = b;
                    break;
                }
            }
            if (null != found) {
                statusList.remove(found);
            }
        }
    }
    
    public class LoadBookStatus implements Runnable {

        public Boolean opSuccess = null;
        public ArrayList<Book> books = null;

        LoadBookStatus() {
        }

        public void run() {
            books = UniruleSoapHelper.getStatusOfAllBooks();
            if (null != books) {
                opSuccess = true;
                ArrayList<Book> removeList = new ArrayList<Book>();
                if (ManageBooksDlg.this.getBooksWithStatusApply.isSelected()) {
                    for (Book b : books) {
                        if (Book.CURATION_STATUS_APPLY != b.getCurationStatus()) {
                            removeList.add(b);
                        }
                    }
                    books.removeAll(removeList);
                } else if (ManageBooksDlg.this.getBooksWithStatusUnused.isSelected()) {
                    for (Book b : books) {
                        if (Book.CURATION_STATUS_DISUSED != b.getCurationStatus()) {
                            removeList.add(b);
                        }
                    }
                    books.removeAll(removeList);
                }
                return;
            }
            opSuccess = false;
        }
    }

    public class SearchFromPaintServer implements Runnable {
        public Boolean opSuccess = null;
        public DataTransferObj infoFromServer = null;
        public String searchStr = null;
        
        public SearchFromPaintServer(String searchStr) {
            this.searchStr = searchStr;
        }
        public void run() {
            Vector sendInfo = new Vector(2);
            sendInfo.add(searchStr);
            //sendInfo.add(dbClsId);
            DataTransferObj dto = new DataTransferObj();
            dto.setVc(PaintManager.inst().getVersionContainer());
            dto.setObj(sendInfo);
            if (ManageBooksDlg.this.geneSymbolBtn.isSelected()) {
                infoFromServer = PantherServer.inst().searchGeneName(servletUrl, dto, null, null);
            } else if (ManageBooksDlg.this.geneIdentifierBtn.isSelected()) {
                infoFromServer = PantherServer.inst().searchGeneExtId(servletUrl, dto, null, null);
            } else if (ManageBooksDlg.this.proteinIdentifierBtn.isSelected()) {
                infoFromServer = PantherServer.inst().searchProteinExtId(servletUrl, dto, null, null);
            }
            else if(ManageBooksDlg.this.definitionBtn.isSelected()) {
                infoFromServer = PantherServer.inst().searchDefinition(servletUrl, dto, null, null); 
            }
            else if (ManageBooksDlg.this.getBookByIdBtn.isSelected()) {
                infoFromServer = PantherServer.inst().searchBookId(servletUrl, dto, null, null); 
            }
            else if (ManageBooksDlg.this.getBooksByPTNBtn.isSelected()) {
                infoFromServer = PantherServer.inst().searchBookPTN(servletUrl, dto, null, null); 
            }            
            else if(ManageBooksDlg.this.getAllBooksBtn.isSelected()) {               
                infoFromServer = PantherServer.inst().searchAllBooks(servletUrl, dto, null, null); 
            }
//            else if (ManageBooksDlg.this.getBooksWithStatusApply.isSelected()) {
//                infoFromServer = PantherServer.inst().searchUncuratedBooks(servletUrl, dto, null, null); 
//            }
//            else if (ManageBooksDlg.this.getBooksWithStatusUnused.isSelected()) {
//                infoFromServer = PantherServer.inst().searchRequirePaintReviewUnlocked(servletUrl, dto, null, null); 
//            }
            // For getting status apply or status unused, retrieve list of all books - Remove all that are not status apply or status unused
            else if (ManageBooksDlg.this.getBooksWithStatusApply.isSelected()  ||  ManageBooksDlg.this.getBooksWithStatusUnused.isSelected()) {
                infoFromServer = PantherServer.inst().searchAllBooks(servletUrl, dto, null, null); 
            }
            opSuccess = true;
        }
    }

}

