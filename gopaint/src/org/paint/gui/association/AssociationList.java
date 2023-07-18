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
package org.paint.gui.association;

import com.sri.panther.paintCommon.Constant;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import org.paint.datamodel.GeneNode;
import org.paint.gui.event.EventManager;
import org.paint.gui.event.FamilyChangeEvent;
import org.paint.gui.event.FamilyChangeListener;
import org.paint.gui.event.GeneSelectEvent;
import org.paint.gui.event.GeneSelectListener;
import org.paint.gui.event.TermSelectEvent;
import org.paint.gui.event.TermSelectionListener;
import org.paint.gui.familytree.TreePanel;
import org.paint.main.PaintManager;

public class AssociationList extends JPanel 
implements GeneSelectListener, FamilyChangeListener, TermSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TitledBorder border;

	private GeneNode node;	
	
	private AssociationTable assoc_table;
	
	final static int HEADER_HEIGHT = 20;

	final static int HEADER_MARGIN = 2;

	protected String dragTitle = "";
        private static final String STR_UNDERSCORE = "_";
        private static final String STR_COPY_NODE_ID_TO_CLIPBOARD = "copy selected node identifier to clipboard";
	
    public AssociationList() {
        super();

        setLayout(new BorderLayout());

        assoc_table = new AssociationTable(); //new AssociationsTable();

        JScrollPane annot_scroll = new JScrollPane(assoc_table);

        border = createBorder("");
        setBorder(border);

        add(annot_scroll, BorderLayout.CENTER);

        EventManager.inst().registerGeneListener(this);
        EventManager.inst().registerFamilyListener(this);
        EventManager.inst().registerTermListener(this);

        // Permit user to copy node id to clipboard.  The title border is not a component and cannot be selected.
        MouseAdapter ma = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int modifiers = e.getModifiers();
                if (InputEvent.BUTTON3_MASK == (modifiers & InputEvent.BUTTON3_MASK)
                        || (((modifiers & InputEvent.BUTTON1_MASK) != 0 && (modifiers & InputEvent.BUTTON3_MASK) == 0) && (true == e.isMetaDown()))) {
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem menuItem = new JMenuItem(STR_COPY_NODE_ID_TO_CLIPBOARD);
                    menuItem.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            String nodeLabel = AssociationList.this.getNodeLabel();
                            if (null != nodeLabel) {
                                StringSelection stringSelection = new StringSelection(nodeLabel);
                                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                                clipboard.setContents(stringSelection, null);
                            }
                        }
                    });
                    popup.add(menuItem);
                    showPopup(popup, e.getComponent(), new Point(e.getX(), e.getY()));
                }
            }
        };
        addMouseListener(ma);
    }

    private void showPopup(JPopupMenu popup, Component comp, Point position) {

        // Get root frame
        Component root = comp;

        while ((root != null) && (false == (root instanceof JFrame))) {
            root = root.getParent();
        }
        if (root != null) {
            SwingUtilities.convertPointToScreen(position, comp);
            Point rootPos = root.getLocationOnScreen();
            Dimension rootSize = root.getSize();
            Dimension popSize = popup.getPreferredSize();
            int x = position.x;
            int y = position.y;
            Insets insets = popup.getInsets();

            if (position.x + popSize.width + (insets.left + insets.right) > rootPos.x + rootSize.width) {
                x = rootPos.x + rootSize.width - popSize.width - insets.left;
            }
            if (position.y + popSize.height + (insets.top + insets.bottom) > rootPos.y + rootSize.height) {
                y = rootPos.y + rootSize.height - popSize.height - insets.top;
            }
            if (x >= rootPos.x + insets.left && y >= rootPos.y + insets.top) {
                position.setLocation(x, y);
            }
            SwingUtilities.convertPointFromScreen(position, comp);
        }

        // Show popup menu.
        popup.show(comp, position.x, position.y);
    }      

	private TitledBorder createBorder(String title) {
		Border raisedbevel = BorderFactory.createRaisedBevelBorder();
		Border loweredbevel = BorderFactory.createLoweredBevelBorder();
		Border border = BorderFactory.createCompoundBorder(
				raisedbevel, loweredbevel);
		return BorderFactory.createTitledBorder(border, title);
	}

	public void handleGeneSelectEvent(GeneSelectEvent event) {
            GeneNode ancestor = event.getAncestor();
            if (null != ancestor) {
                setNode(ancestor);
            }
//		if (event.getGenes().size() > 0)
//			setNode(event.getGenes().get(0));
		else
			setNode(null);
		repaint();
	}

	public void newFamilyData(FamilyChangeEvent e) {
		TreePanel tree = PaintManager.inst().getTree();
		GeneNode root = tree.getRoot();
		setNode(null);
	}
        
        public void familyClosed() {
            
        }
	
	public void handleTermEvent(TermSelectEvent e) {
		GeneNode mrca = EventManager.inst().getAncestralSelection();
		if (!mrca.equals(node) && !e.getSource().equals(assoc_table))
			this.setNode(mrca);
	}
	
    private void setNode(GeneNode node) {
        this.node = node;
        border.setTitle(getNodeLabel());
        revalidate();
    }

    private String getNodeLabel() {
        if (null == node) {
            return Constant.STR_EMPTY;
        }
        String label = node.getNodeLabelProt();
        if (null != label) {
            return label + STR_UNDERSCORE + node.getNode().getStaticInfo().getPublicId();
        }
        return node.getNode().getStaticInfo().getPublicId();
    }

	
}

