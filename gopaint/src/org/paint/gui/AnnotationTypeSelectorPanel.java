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

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.geneontology.db.model.Term;
import org.obo.datamodel.LinkDatabase;
import org.obo.datamodel.LinkedObject;
import org.paint.config.Preferences;
import org.paint.go.GO_Util;
import org.paint.gui.AnnotationTypeSelector.AnnotationType;
import org.paint.gui.event.AspectChangeEvent;
import org.paint.gui.event.AspectChangeListener;
import org.paint.gui.event.EventManager;
import org.paint.gui.event.TermSelectEvent;
import org.paint.gui.event.TermSelectionListener;
import org.paint.main.PaintManager;

public class AnnotationTypeSelectorPanel extends JPanel implements AspectChangeListener, TermSelectionListener {

	private static final long serialVersionUID = 1L;

	private JRadioButton bpButton;
	private JRadioButton ccButton;
	private JRadioButton mfButton;
        private JRadioButton urButton;

	private Border plainBorder;

	public AnnotationTypeSelectorPanel() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		//		setOpaque(true);
		//		setBackground(Preferences.inst().getBackgroundColor());

		mfButton = new JRadioButton("  Molecular Function  ", true);
		ccButton = new JRadioButton("  Cellular Component  ", false);
		bpButton = new JRadioButton("  Biological Process  ", false);
                urButton = new JRadioButton("  UniRule    ", false);

		AspectSelectorListener aspectSelectionListener = new AspectSelectorListener();
		bpButton.addActionListener(aspectSelectionListener);
		ccButton.addActionListener(aspectSelectionListener);
		mfButton.addActionListener(aspectSelectionListener);
                urButton.addActionListener(aspectSelectionListener);

		add(mfButton);
		add(ccButton);
		add(bpButton);
                add(urButton);

		ButtonGroup group = new ButtonGroup();
		group.add(mfButton);
		group.add(ccButton);
		group.add(bpButton);
                group.add(urButton);

		mfButton.setOpaque(true);
		ccButton.setOpaque(true);
		bpButton.setOpaque(true);
                urButton.setOpaque(true);

		Preferences prefs = Preferences.inst();

		mfButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_MF).darker());
		ccButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_CC));
		bpButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_BP));
		urButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_UR));
                

		plainBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		mfButton.setMargin(new Insets(2, 12, 2, 12));

		mfButton.setBorderPainted(true);
		ccButton.setBorderPainted(true);
		bpButton.setBorderPainted(true);
		urButton.setBorderPainted(true);
                

		mfButton.setBorder(plainBorder);
		ccButton.setBorder(plainBorder);
		bpButton.setBorder(plainBorder);
		urButton.setBorder(plainBorder);
                
		EventManager.inst().registerAspectChangeListener(this);
		EventManager.inst().registerTermListener(this);
	}

	class AspectSelectorListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JRadioButton button = (JRadioButton)e.getSource();
			switchAspect(button);
		}
	}

	@Override
	public void handleAspectChangeEvent(AspectChangeEvent event) {
		if (event.getSource() == this)
			return;

		Preferences prefs = Preferences.inst();
		if (mfButton.isSelected()) {
			mfButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_MF).darker());
			ccButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_CC));
			bpButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_BP));
			urButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_UR));                        
		}
		if (bpButton.isSelected()) {
			mfButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_MF));
			ccButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_CC));
			bpButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_BP).darker());
			urButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_UR));                        
		}
		else if (ccButton.isSelected()) {
			mfButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_MF));
			ccButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_CC).darker());
			bpButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_BP));
			urButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_UR));                        
		}
                else if (urButton.isSelected()) {
			mfButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_MF));
			ccButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_CC));
			bpButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_BP));
			urButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_UR).darker());                     
                }
	}

	@Override
	public void handleTermEvent(TermSelectEvent e) {
		List<Term> terms = e.getTermSelection();
		if (terms != null && !terms.isEmpty()) {
			Term term = terms.get(0);
			LinkDatabase go_root = PaintManager.inst().getGoRoot().getLinkDatabase();
			LinkedObject obo_term = (LinkedObject) GO_Util.inst().getObject(go_root, term.getAcc());
			String aspect_str = obo_term.getNamespace().getID().toLowerCase();
			if (aspect_str.equals(AnnotationType.BIOLOGICAL_PROCESS.toString()) && !bpButton.isSelected()) {
				bpButton.setSelected(true);
				switchAspect(bpButton);
			}
			else if (aspect_str.equals(AnnotationType.MOLECULAR_FUNCTION.toString()) && !mfButton.isSelected()) {
				mfButton.setSelected(true);
				switchAspect(mfButton);
			}
			else if (aspect_str.equals(AnnotationType.CELLULAR_COMPONENT.toString()) && !ccButton.isSelected()) {
				ccButton.setSelected(true);
				switchAspect(ccButton);
			}
                        else if (!urButton.isSelected()){
                                urButton.setSelected(true);
                                switchAspect(urButton);
                        }
		}
	}

	private void switchAspect(JRadioButton button) {
		Preferences prefs = Preferences.inst();
		if (button == bpButton) {
			AnnotationTypeSelector.inst().setAspect(AnnotationTypeSelector.AnnotationType.BIOLOGICAL_PROCESS);
			mfButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_MF));
			ccButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_CC));
			bpButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_BP).darker());
                        urButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_UR));
		}
		else if (button == ccButton) {
			AnnotationTypeSelector.inst().setAspect(AnnotationTypeSelector.AnnotationType.CELLULAR_COMPONENT);
			mfButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_MF));
			ccButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_CC).darker());
			bpButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_BP));
                        urButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_UR));                        
		}
		else if (button == mfButton) {
			AnnotationTypeSelector.inst().setAspect(AnnotationTypeSelector.AnnotationType.MOLECULAR_FUNCTION);
			mfButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_MF).darker());
			ccButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_CC));
			bpButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_BP));
                        urButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_UR));                        
		}
                else if (button == urButton) {
			AnnotationTypeSelector.inst().setAspect(AnnotationTypeSelector.AnnotationType.UNIRULE);
			mfButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_MF));
			ccButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_CC));
			bpButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_BP));
                        urButton.setBackground(prefs.getAspectColor(Preferences.HIGHLIGHT_UR).darker());                        
		}
	}
}
