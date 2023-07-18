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
package org.paint.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollBar;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import org.apache.log4j.Logger;
import org.paint.gui.event.AspectChangeEvent;
import org.paint.gui.event.AspectChangeListener;
import org.paint.gui.event.EventManager;
import org.paint.gui.familytree.TreePanel;
import org.paint.gui.matrix.AnnotationMatrix;
import org.paint.gui.matrix.AnnotationTransferHndlr;
import org.paint.gui.msa.MSAPanel;
import org.paint.gui.table.GeneTable;
import org.paint.main.PaintManager;
import org.paint.util.RenderUtil;

public class FamilyViews extends AbstractPaintGUIComponent 
implements AspectChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static FamilyViews singleton;

	private NodeScroller treePanel;
	private NodeScroller tablePanel;
	private NodeScroller  msaPanel;
	private NodeScroller matrixPanel;

	private JTabbedPane gtabbedPane;
	private JTabbedPane ttabbedPane;

	private static Logger log = Logger.getLogger(FamilyViews.class);

	private List<NodeScroller> scrollables;
	public static final int TREE_PANE = 0;
	public static final int MATRIX_PANE = 1;
	public static final int TABLE_PANE = 2;
	public static final int MSA_PANE = 3;

	/**
	 * Constructor declaration
	 *
	 *
	 * @param doc
	 * @param panel
	 *
	 * @see
	 */
	public FamilyViews() {
		super("tree-info:tree-info");
		this.initializeInterface();
	}

	/**
	 * Method declaration
	 *
	 *
	 * @param frameDim
	 *
	 * @see
	 */
	private void initializeInterface() {

		if (treePanel == null) {
			GeneTable gene_table = new GeneTable();
			tablePanel = new NodeScroller(gene_table);

			MSAPanel msa = new MSAPanel();
			msaPanel = new NodeScroller(msa);

			AnnotationMatrix annot_matrix = new AnnotationMatrix();
			matrixPanel = new NodeScroller(annot_matrix);

			gtabbedPane = new JTabbedPane();
			gtabbedPane.addTab("Annotation Matrix", matrixPanel);
			gtabbedPane.addTab("Protein Information", tablePanel);
			gtabbedPane.addTab("MSA", msaPanel);
			gtabbedPane.setOpaque(true);

			TreePanel tree_pane = new TreePanel();
			treePanel = new NodeScroller(tree_pane);

			ttabbedPane = new JTabbedPane();
			ttabbedPane.addTab("Tree", treePanel);
			ttabbedPane.setOpaque(true);

			setBackground();

			PaintManager.inst().setGeneTable(gene_table);
			PaintManager.inst().setTreePane(tree_pane);
			PaintManager.inst().setMSAPane(msa);
			PaintManager.inst().setMatrix(annot_matrix);

			//TODO: move the logic to PaintManager
			//tree_pane.setTransferHandler(new AnnotationTransferHandler());
                        tree_pane.setTransferHandler(new AnnotationTransferHndlr());

			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, ttabbedPane, gtabbedPane);
			splitPane.setResizeWeight(0.75);

			setOpaque(true); // Frame content panes must be opaque
			this.setLayout(new BorderLayout());
			add(new AnnotationTypeSelectorPanel(), BorderLayout.NORTH);
			add(splitPane, BorderLayout.CENTER);

			scrollables = new ArrayList<NodeScroller>();
			scrollables.add(TREE_PANE, treePanel);
			scrollables.add(MATRIX_PANE, matrixPanel);
			scrollables.add(TABLE_PANE, tablePanel);
			scrollables.add(MSA_PANE, msaPanel);

			EventManager.inst().registerAspectChangeListener(this);
		}
	}

	public static FamilyViews inst() {
		if (singleton == null) 
			singleton = new FamilyViews();
		return singleton;
	}

	public int getBottomMargin(int scroller_index) {
		NodeScroller scrolling_pane = scrollables.get(scroller_index);
		JScrollBar scroller = scrolling_pane.getHorizontalScrollBar();
		int scrollHeight = 0;
		if (scroller == null || (scroller != null && !scroller.isVisible())) {
			for (int i = 0; i < scrollables.size() && scrollHeight == 0; i++) {
				NodeScroller alt_pane = scrollables.get(i);
				scroller = alt_pane.getHorizontalScrollBar();
				if (i != scroller_index && scroller.isVisible()) {
					scrollHeight = scroller.getHeight();
				}
			}
		}
		if (scrollHeight > 0)
			scrollHeight += 1;
		return scrollHeight;
	}

	public int getHScrollerHeight(int scroller_index) {
		NodeScroller scrolling_pane = scrollables.get(scroller_index);
		JScrollBar scroller = scrolling_pane.getHorizontalScrollBar();
		int scrollHeight = scroller.getHeight();
		if (scrollHeight > 0)
			scrollHeight += 1;
		return scrollHeight;
	}

	@Override
	public void handleAspectChangeEvent(AspectChangeEvent event) {
		setBackground();
	}

	private void setBackground() {
		Color bg_color = RenderUtil.getAspectColor();
		setBackground(bg_color);
		gtabbedPane.setBackground(bg_color);
		ttabbedPane.setBackground(bg_color);
	}

}

