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

import edu.usc.ksom.pm.panther.paintCommon.Node;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import org.geneontology.db.model.Term;
import org.paint.datamodel.GeneNode;
import org.paint.dialog.FindPanel.SEARCH_TYPE;
import org.paint.gui.event.EventManager;
import org.paint.gui.event.GeneSelectEvent;
import org.paint.gui.event.TermSelectEvent;
import org.paint.gui.matrix.AnnotationMatrix;
import org.paint.main.PaintManager;

public class FoundResultsTable extends JTable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** populates table with results */

	private String [] columns = {"ID"};
	List<GeneNode> gene_results;
	List<Term> term_results;
	MatchModel model;
	private SEARCH_TYPE search_type;	

	public FoundResultsTable() {
		super();
		model = new MatchModel();
		super.setModel(model);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getSelectionModel().addListSelectionListener(new TableSelector());
	}

    public void setGeneResults(List<GeneNode> geneResults) {
        if (search_type == SEARCH_TYPE.GENE) {
            this.gene_results = geneResults;
            if (geneResults != null && geneResults.size() > 0) {
                getSelectionModel().addSelectionInterval(0, 0);
                GeneNode node = geneResults.get(0);
                ArrayList<GeneNode> selection = new ArrayList<GeneNode>();
                selection.add(node);
                PaintManager.inst().getTree().getDescendentList(node, selection);
                GeneSelectEvent event = new GeneSelectEvent(this, selection, node);
                EventManager.inst().fireGeneEvent(event);
            }
        }
        else if (search_type == SEARCH_TYPE.GENES_SEPARATED_BY_COMMA) {
            this.gene_results = geneResults;
            // Hightlight MRCA for selections.  User can then click on single gene afterwards
            if (geneResults != null && geneResults.size() > 0) {
                GeneNode mrca = AnnotationMatrix.getMRCA(geneResults);
                if (null != mrca) {
                    ArrayList<GeneNode> descendants = new ArrayList<GeneNode>();
                    ArrayList<Node> nodeDescendants = new ArrayList<Node>();
                    mrca.getNode().getDescendants(mrca.getNode(), nodeDescendants);
                    PaintManager pm = PaintManager.inst();
                    for (Node descendant : nodeDescendants) {
                        GeneNode curGn = pm.getGeneByPTNId(descendant.getStaticInfo().getPublicId());
                        if (null != curGn) {
                            descendants.add(curGn);
                        } else {
                            System.out.println("Unable to find gene node by id " + descendant.getStaticInfo().getPublicId() + " " + descendant.getStaticInfo().getNodeAcc());
                        }
                    }

                    ArrayList<GeneNode> selection = new ArrayList<GeneNode>();
                    selection.addAll(descendants);
                    GeneSelectEvent ge = new GeneSelectEvent(this, selection, mrca);
                    EventManager.inst().fireGeneEvent(ge);
                }
            }            
        }
        model.fireTableDataChanged();
    }

	public void setTermResults(List<Term> term_results) {
		if (search_type == SEARCH_TYPE.TERM) {
			this.term_results = term_results;
			if (term_results != null && term_results.size() > 0) {
				setRowSelectionInterval(0, 0);
				Term selected_term = term_results.get(0);
				TermSelectEvent term_event = new TermSelectEvent (this, selected_term);
				List<GeneNode> selection = EventManager.inst().fireTermEvent(term_event);	
				GeneSelectEvent gene_event = new GeneSelectEvent(this, selection, EventManager.inst().getAncestralSelection());
				EventManager.inst().fireGeneEvent(gene_event);
			}
		}
		model.fireTableDataChanged();
	}

	public void setType(SEARCH_TYPE search_type) {
		this.search_type = search_type;
		setGeneResults(gene_results);
		setTermResults(term_results);
	}

	private class TableSelector implements ListSelectionListener {

		public TableSelector() {
			super();
		}

		public void valueChanged(ListSelectionEvent e) {

			int row = getSelectedRow();
			if (row >= 0 && !e.getValueIsAdjusting() ) {
				if (search_type == SEARCH_TYPE.GENE || search_type == SEARCH_TYPE.GENES_SEPARATED_BY_COMMA) {
					GeneNode node = gene_results.get(row);
					ArrayList<GeneNode> selection = new ArrayList<GeneNode> ();
					selection.add(node);
					PaintManager.inst().getTree().getDescendentList(node, selection);
					GeneSelectEvent event = new GeneSelectEvent(this, selection, node);
					EventManager.inst().fireGeneEvent(event);
					// zoom in on new selection (with some padding)
				}
                                else if (search_type == SEARCH_TYPE.TERM) {
					Term selected_term = term_results.get(row);
					TermSelectEvent term_event = new TermSelectEvent (this, selected_term);
					List<GeneNode> selection = EventManager.inst().fireTermEvent(term_event);	
					GeneSelectEvent gene_event = new GeneSelectEvent(this, selection, EventManager.inst().getAncestralSelection());
					EventManager.inst().fireGeneEvent(gene_event);
				}
			}
		}
	}

	/**
	 * This is the TableModel for the table 
	 * Takes a Vector of SequenceMatch in setData.
	 * Each SequenceMatch represents a row
	 */
	protected class MatchModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

            public int getRowCount() {
                if (search_type == SEARCH_TYPE.GENE && gene_results != null) {
                    return gene_results.size();
                } else if (search_type == SEARCH_TYPE.GENES_SEPARATED_BY_COMMA && gene_results != null) {
                    return gene_results.size();
                } else if (search_type == SEARCH_TYPE.TERM && term_results != null) {
                    return term_results.size();
                } else {
                    return 0;
                }
            }

		public int getColumnCount() {
			return columns.length;
		}

		public String getColumnName(int column) {
			return (String) columns[column];
		}

		public Object getValueAt(int row, int column) {
			if (search_type == SEARCH_TYPE.GENE || search_type == SEARCH_TYPE.GENES_SEPARATED_BY_COMMA) {
				GeneNode match = gene_results.get(row);
				return match.getDatabase() + ":" + match.getDatabaseID();
			} else if (search_type == SEARCH_TYPE.TERM) {
				Term match = term_results.get(row);
				return match.getName();
			} else {
				return "";
			}
		}

		public Class getColumnClass(int c) {
			return getValueAt(0, 0).getClass();
		}

	} // end GeneMatchModel inner class



}
