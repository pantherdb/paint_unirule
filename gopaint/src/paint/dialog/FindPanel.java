/**
 *  Copyright 2021 University Of Southern California
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

import com.sri.panther.paintCommon.Constant;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.geneontology.db.model.Term;
import org.paint.datamodel.GeneNode;
import org.paint.gui.table.GeneSearch;
import org.paint.main.PaintManager;

/**
 * A ControlledPanel which displays the find dialog 
 * (for gene and term).
 *
 * 
 * 0) all searches are case insensitive
 * 1) if regular expression is NOT selected:
 * -it is always a contains match
 **/

public class FindPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// -----------------------------------------------------------------------
	// Class/static variables
	// -----------------------------------------------------------------------

	protected final static Logger logger = LogManager.getLogger(FindPanel.class);

	protected static Color color = new Color (255,255,204);

	// -----------------------------------------------------------------------
	// Instance variables
	// -----------------------------------------------------------------------

	JTextField    findField;
	JButton       findButton;
	JRadioButton geneButton, multipleGenesButton;
	JRadioButton termButton;
	JButton       clearButton;
	JButton       closeButton;
	JPanel        controlPanel;
	JScrollPane   resultTableScroller;
	FoundResultsTable resultTable;
	Box resultBox;
	JPanel        closePanel;

	private SEARCH_TYPE search_type;

	private FindStatusPane resultsPane;

	private static String gene_search;
        private static String multiple_gene_search;
	private static List<GeneNode> gene_results;
	private static String term_search;
	private static List<Term> term_results;


	public enum SEARCH_TYPE {
		GENE,
                GENES_SEPARATED_BY_COMMA,
		TERM;
	}

	public FindPanel() {
		componentInit();
	}

	private void componentInit() {
		setBackground (color);

		JPanel radioPanel = new JPanel();
		radioPanel.setBackground(color);
		radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.X_AXIS));
		geneButton = new JRadioButton("Gene");
                multipleGenesButton = new JRadioButton("Multiple genes separated by comma");
		termButton = new JRadioButton("Term");
		geneButton.setSelected(true);

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(geneButton);
                buttonGroup.add(multipleGenesButton);
		buttonGroup.add(termButton);
		radioPanel.add(geneButton);
		radioPanel.add(Box.createHorizontalStrut(20));
		radioPanel.add(termButton);
		radioPanel.add(Box.createHorizontalStrut(20));                
                radioPanel.add(multipleGenesButton);
		radioPanel.setPreferredSize(new Dimension(400, 30));

		findField = new JTextField();

		findButton      = new JButton("Find");
		clearButton    = new JButton("Clear");
		closeButton    = new JButton("Close");
		Dimension buttonSize = new Dimension(90,20);
		findButton.setPreferredSize(buttonSize);
		clearButton.setPreferredSize(buttonSize);
		closeButton.setPreferredSize(buttonSize);

		controlPanel = new JPanel();
		controlPanel.setBackground (color);
		controlPanel.setForeground (Color.black);

		Box findBox = new Box(BoxLayout.X_AXIS);
		findBox.add(findField);
		findBox.add(findButton);
		findBox.add(Box.createHorizontalGlue());
		findBox.setPreferredSize(new Dimension(400, 30));

		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		controlPanel.add(radioPanel);
		controlPanel.add(findBox);
		controlPanel.add(Box.createVerticalGlue());
		controlPanel.setPreferredSize(new Dimension(400, 60));

		resultsPane = new FindStatusPane(" Results ","", color);
		resultsPane.setBackground (color);
		resultsPane.setPreferredSize(new Dimension(400, 24));
		resultsPane.setFont(getFont());

		resultTable = new FoundResultsTable();
		resultTableScroller = new JScrollPane(resultTable);
		resultTableScroller.setPreferredSize(new Dimension(400, 125));
		resultTable.setFillsViewportHeight(true);

		search_type = SEARCH_TYPE.GENE;
		resultTable.setType(search_type);

		resultBox = new Box(BoxLayout.Y_AXIS);
		resultBox.add(resultsPane);
		resultBox.add(Box.createVerticalStrut(10));
		resultBox.add(resultTableScroller);
		resultBox.add(Box.createVerticalGlue());

		geneButton.addActionListener(this);
                multipleGenesButton.addActionListener(this);
		termButton.addActionListener(this);
		findButton.    addActionListener(this);
		clearButton.   addActionListener(this);
		closeButton.  addActionListener(this);
		findField. addActionListener(this);

		closePanel = new JPanel();
		closePanel.setBackground (color);
		closePanel.setForeground (Color.black);
		closePanel.setLayout(new BorderLayout());
		closePanel.setPreferredSize(new Dimension(400, 30));
		closePanel.add(clearButton, BorderLayout.WEST);
		closePanel.add(closeButton, BorderLayout.EAST);

		setLayout(new BorderLayout());
		add(controlPanel, BorderLayout.NORTH);
		add(resultBox, BorderLayout.CENTER);
		resultBox.setVisible(false);
		add(closePanel, BorderLayout.SOUTH);	
	}

	/**
	 * Handles all buttons and fields, does name, position, and sequence searching
	 */
    public void actionPerformed(ActionEvent evt) {

        if (evt.getSource() == geneButton) {
            search_type = SEARCH_TYPE.GENE;
            resultTable.setType(search_type);
            findField.setText(gene_search);
            findField.requestFocus();
            showResults();
        } else if (evt.getSource() == termButton) {
            search_type = SEARCH_TYPE.TERM;
            resultTable.setType(search_type);
            findField.setText(term_search);
            findField.requestFocus();
            showResults();
        } else if (evt.getSource() == multipleGenesButton) {
            search_type = SEARCH_TYPE.GENES_SEPARATED_BY_COMMA;
            resultTable.setType(search_type);
            findField.setText(multiple_gene_search);
            findField.requestFocus();
            showResults();
        }
        // Name search
        if (evt.getSource() == findButton || evt.getSource() == findField) {
            String text = findField.getText();
            if (text != null && text.length() > 0) {
                text = text.substring(0, 1).equals('*') ? text.substring(1) : text;
                text = text.length() > 0 && text.substring(text.length() - 1).equals('*') ? text.substring(0, text.length() - 1) : text;
            }
            if (!text.equals("")) {
                if (search_type == SEARCH_TYPE.GENE) {
                    gene_search = text;
                    gene_results = GeneSearch.inst().search(PaintManager.inst().getTree().getAllNodes(), text);
                    showResults();
                }
                if (search_type == SEARCH_TYPE.GENES_SEPARATED_BY_COMMA) {
                    text = findField.getText();
                    multiple_gene_search = text;
                    String parts[] = text.split(Pattern.quote(Constant.STR_COMMA));
                    HashSet<String> searchSet = new HashSet<String>();
                    for (String part : parts) {
                        int length = part.length();
                        if (length > 0) {
                            part = part.substring(0, 1).equals('*') ? part.substring(1) : part;
                            part = part.length() > 0 && part.substring(part.length() - 1).equals('*') ? part.substring(0, part.length() - 1) : part;
                            searchSet.add(part);
                        }
                    }
                    gene_results = GeneSearch.inst().search(PaintManager.inst().getTree().getAllNodes(), searchSet);
                    showResults();
                } else if (search_type == SEARCH_TYPE.TERM) {
                    term_search = text;
                    term_results = PaintManager.inst().findTerm(text);
                    showResults();
                }
            }
        } else if (evt.getSource() == clearButton) {
            findField.setText("");
            if (search_type == SEARCH_TYPE.GENE) {
                gene_search = Constant.STR_EMPTY;
                gene_results = null;
            } else if (search_type == SEARCH_TYPE.GENES_SEPARATED_BY_COMMA) {
                multiple_gene_search = Constant.STR_EMPTY;
                gene_results = null;
            } else {
                term_search = Constant.STR_EMPTY;
                term_results = null;
            }
            showResults();
        } else if (evt.getSource() == closeButton) {
            Window win = SwingUtilities.windowForComponent(this);
            win.setVisible(false);
            //		      win.dispose();
        }
    }

    private void showResults() {
        if (search_type == SEARCH_TYPE.GENE || search_type == SEARCH_TYPE.GENES_SEPARATED_BY_COMMA) {
            resultTable.setGeneResults(gene_results);
        } else {
            resultTable.setTermResults(term_results);
        }
        resultBox.setVisible(true);
        int count = resultTable.getRowCount();
        if (count == 0) {
            resultsPane.setText("Nothing found that matches.");
            resultTableScroller.setVisible(false);
        } else {
            resultsPane.setText("Found " + count + " matches.");
            resultTableScroller.setVisible(true);
        }
        controlPanel.validate();

        Window win = SwingUtilities.windowForComponent(resultTableScroller);
        win.validate();
        win.pack();
    }
}

