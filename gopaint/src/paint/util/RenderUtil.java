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

package org.paint.util;

import edu.usc.ksom.pm.panther.paintCommon.AnnotationHelper;
import edu.usc.ksom.pm.panther.paintCommon.Evidence;
import edu.usc.ksom.pm.panther.paintCommon.GOTermHelper;
import edu.usc.ksom.pm.panther.paintCommon.Node;
import edu.usc.ksom.pm.panther.paintCommon.NodeVariableInfo;
import edu.usc.ksom.pm.panther.paintCommon.UAnnotation;
import edu.usc.ksom.pm.panther.paintCommon.UAnnotationHelper;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.paint.config.Preferences;
import org.paint.datamodel.GeneNode;
import org.paint.gui.AnnotationTypeSelector;
import org.paint.gui.AnnotationTypeSelector.AnnotationType;
import org.paint.main.PaintManager;

public class RenderUtil {

	private static final Logger LOG = Logger.getLogger(RenderUtil.class);

	private static final String STR_DOT_DOT = "...";
	private static final String STR_EMPTY = "";

	private static HashMap<String, Color> ortho_colors;

	public static void paintBorder(Graphics g, Rectangle r, Color bgColor, boolean selected) {
		if (bgColor != null) {
			g.setColor(bgColor);
			g.fillRect(r.x, r.y, r.width, r.height);
		}
		if (selected) {
			Preferences prefs = Preferences.inst();
			Color color = prefs.getForegroundColor();
			g.setColor(color);
			// line across the top of the cell in the table
			g.drawLine(r.x, r.y, r.x + r.width, r.y);
		} else {
			g.setColor(Color.lightGray);
		}
		// line across the bottom of the cell in the table
		int bottom_y = r.y + r.height - 1;
		g.drawLine(r.x, bottom_y, r.x + r.width, bottom_y);
	}

	/**
	 *  If string does not fit into cell, replace the end of the string with '...' to denote missing text
	 */
	public static String formatText(Graphics g, Insets insets, int boxWidth, String text, Font font) {
		if (text == null) {
			return "";
		}
		FontMetrics fm = g.getFontMetrics(font);

		int neededWidth = RenderUtil.getTextWidth(fm, text);

		if (boxWidth >= neededWidth) {
			return text;
		}

		String finalStr = STR_DOT_DOT;
		neededWidth = RenderUtil.getTextWidth(fm, finalStr);

		// Return empty string if the ellipsis cannot fit into the column.
		if (neededWidth > boxWidth) {
			return STR_EMPTY;
		}

		StringBuffer sb = new StringBuffer(finalStr);
		int i = 0;
		while ((neededWidth < boxWidth) && (i <= text.length() - 1)) {
			sb.insert(i, text.charAt(i));
			try {
				neededWidth = RenderUtil.getTextWidth(fm, sb.toString());
			}
			catch (ArrayIndexOutOfBoundsException e) {

				LOG.error("ArrayIndexOutOfBoundsException " + e.getMessage() + " returned while attempting to calculate Text size.");

			}
			i++;
		}

		// Remove last added character
		if (neededWidth > boxWidth) {
			try {
				sb.setLength(i - 1);
				sb.append(finalStr);
			}
			catch (StringIndexOutOfBoundsException  e) {

				LOG.error("StringIndexOutOfBoundsException " + e.getMessage() + " returned while attempting to delete character from string buffer.");

			}
		}
		return  sb.toString();
	}

	public static int getTextWidth(FontMetrics fm, String s) {
		int width = 0;
		try {
			width = fm.stringWidth(s);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			LOG.error("ArrayIndexOutOfBoundsException " + e.getMessage() + 
			" returned while attempting to calculate Text size.");
		}
		return width;
	}

	public static int getWidth(FontMetrics fm, String text, Insets insets) {
		return getTextWidth(fm, text + insets.left + insets.right);
	}

    public static Font getNodeFont(GeneNode node) {
        Font f = Preferences.inst().getFont();
        //if (node.hasExpEvidence())
        if ((node.hasBiologicalProcessEvidence()
                && AnnotationTypeSelector.inst().getAnnotationType() == AnnotationTypeSelector.AnnotationType.BIOLOGICAL_PROCESS)
                || (node.hasCellularComponentEvidence()
                && AnnotationTypeSelector.inst().getAnnotationType() == AnnotationTypeSelector.AnnotationType.CELLULAR_COMPONENT)
                || (node.hasMolecularFunctionEvidence()
                && AnnotationTypeSelector.inst().getAnnotationType() == AnnotationTypeSelector.AnnotationType.MOLECULAR_FUNCTION)
                || node.hasUniRuleEvidence()
                && AnnotationTypeSelector.inst().getAnnotationType() == AnnotationTypeSelector.AnnotationType.UNIRULE) {
            f = new Font(f.getFontName(), Font.BOLD, f.getSize());
        }
        return f;
    }

	public static Color annotationStatusColor(GeneNode node, Color c) {

		Color color = new Color(c.getRGB());
		Preferences prefs = Preferences.inst();
                AnnotationType at = AnnotationTypeSelector.inst().getAnnotationType();                
                Node n = node.getNode();
                NodeVariableInfo nvi = n.getVariableInfo();
                if (null != nvi) {
                    boolean foundExp = false;
                    boolean foundNonExp = false;
                    boolean foundCurated = false;
                    boolean foundNonExpNonPAINT = false;
                    if (at == AnnotationTypeSelector.AnnotationType.BIOLOGICAL_PROCESS ||
                        at == AnnotationTypeSelector.AnnotationType.MOLECULAR_FUNCTION ||
                        at == AnnotationTypeSelector.AnnotationType.CELLULAR_COMPONENT) {
                        String go_aspect = AnnotationTypeSelector.aspects.get(at.toString());
                        GOTermHelper gth = PaintManager.inst().goTermHelper();
                        ArrayList<edu.usc.ksom.pm.panther.paintCommon.Annotation> annotList = nvi.getGoAnnotationList();
                        if (null != annotList) {
                            for (edu.usc.ksom.pm.panther.paintCommon.Annotation annot : annotList) {
                                if (true == go_aspect.equals(gth.getTerm(annot.getGoTerm()).getAspect())) {
                                    if (true == annot.isExperimental()) {
                                        foundExp = true;
                                        break;
                                    } else {
                                        foundNonExp = true;
                                    }
                                    if (true == AnnotationHelper.isDirectAnnotation(annot)) {
                                        foundCurated = true;
                                    }
                                }
                            }
                        }
                        if (true == foundNonExp) {
                            color = prefs.getInferPaintColor();
                        }
                        if (true == foundExp) {
                            color = prefs.getExpPaintColor();
                        }
                        if (true == foundCurated) {
                            color = prefs.getCuratedPaintColor();
                        }
                    }
                    else {
                        ArrayList<UAnnotation> annotList = nvi.getuAnnotationList();
                        if (null != annotList) {
                            for (UAnnotation annot : annotList) {
                                String code = annot.getEvCode();
                                if (true == Evidence.isExperimental(code)) {
                                    foundExp = true;
                                    break;
                                }
                                if (UAnnotationHelper.isDirectAnnotation(annot)) {
                                    foundCurated = true;
                                }
                                else if (true == Evidence.isPaint(code)) {
                                    foundNonExp = true;
                                }
                                else {
                                    foundNonExpNonPAINT = true;
                                }
                            }
                        }
                        if (true == foundNonExpNonPAINT) {
                            color = prefs.getNonExpnonPaintColor();
                        }
                        if (true == foundNonExp) {
                            color = prefs.getInferPaintColor();
                        }
                        if (true == foundExp) {
                            color = prefs.getExpPaintColor();
                        }
                        if (true == foundCurated) {
                            color = prefs.getCuratedPaintColor();
                        }
                    }
                }

//		if ((node.hasBiologicalProcessEvidence() &&
//				AnnotationTypeSelector.inst().getAspect() == AnnotationTypeSelector.AnnotationType.BIOLOGICAL_PROCESS) ||
//				(node.hasCellularComponentEvidence() &&
//						AnnotationTypeSelector.inst().getAspect() == AnnotationTypeSelector.AnnotationType.CELLULAR_COMPONENT) ||
//						(node.hasMolecularFunctionEvidence() &&
//								AnnotationTypeSelector.inst().getAspect() == AnnotationTypeSelector.AnnotationType.MOLECULAR_FUNCTION)) {
//			color = prefs.getExpPaintColor();
//		}
//                else 
//                if (GO_Util.inst().isPainted(node, false) && (false == node.isLeaf())) {
//                    if (true == GeneNodeUtil.hasDirectAnnotation(node)) {
//                        color = prefs.getCuratedPaintColor();
//                    }
//                    if (true == GeneNodeUtil.hasAllPropagatedAnnotation(node)) {
//                        color = prefs.getInferPaintColor();
//                    }
//			Set<Association> associations = node.getGeneProduct().getAssociations();
//			for (Iterator<Association> assoc_it = associations.iterator(); assoc_it.hasNext();) {
//				Association assoc = assoc_it.next();
//				String termCv = assoc.getTerm().getCv();
//				if ((AnnotationTypeSelector.inst().getAspect() ==
//					AnnotationTypeSelector.AnnotationType.BIOLOGICAL_PROCESS &&
//					termCv.equals(AnnotationTypeSelector.AnnotationType.BIOLOGICAL_PROCESS.toString())) ||
//					(AnnotationTypeSelector.inst().getAspect() ==
//						AnnotationTypeSelector.AnnotationType.CELLULAR_COMPONENT &&
//						termCv.equals(AnnotationTypeSelector.AnnotationType.CELLULAR_COMPONENT.toString())) ||
//						AnnotationTypeSelector.inst().getAspect() ==
//							AnnotationTypeSelector.AnnotationType.MOLECULAR_FUNCTION &&
//							termCv.equals(AnnotationTypeSelector.AnnotationType.MOLECULAR_FUNCTION.toString())) {
//					if (assoc.isMRC() && !node.isLeaf()) {
//						color = prefs.getCuratedPaintColor();
//					}
//					else if (!color.equals(prefs.getCuratedPaintColor())) {
//						color = prefs.getInferPaintColor();
//					}
//				}
//			}
		
		color = selectedColor(node.isSelected(), color, c);
		return color;
	}

	public static Color selectedColor(boolean selected, Color color, Color c) {
		if (selected) {
			if (color.equals(c)) {
				color = Color.gray;
			} else {
				color = color.brighter().brighter();
			}
		}
		return color;
	}

	public static Color getAspectColor() {
		return getAspectColor(AnnotationTypeSelector.inst().getAnnotationType().toString());
	}

	public static Color getAspectColor(String cv) {
		if (cv != null) {
//                    System.out.println(AnnotationTypeSelector.AnnotationType.BIOLOGICAL_PROCESS.toString());
			if (cv.equals(AnnotationTypeSelector.AnnotationType.BIOLOGICAL_PROCESS.toString()))
				return Preferences.inst().getAspectColor(Preferences.HIGHLIGHT_BP);
			if (cv.equals(AnnotationTypeSelector.AnnotationType.CELLULAR_COMPONENT.toString()))
				return Preferences.inst().getAspectColor(Preferences.HIGHLIGHT_CC);
			if (cv.equals(AnnotationTypeSelector.AnnotationType.MOLECULAR_FUNCTION.toString()))
				return Preferences.inst().getAspectColor(Preferences.HIGHLIGHT_MF);
                        if (cv.equals(AnnotationTypeSelector.AnnotationType.UNIRULE.toString()))
				return Preferences.inst().getAspectColor(Preferences.HIGHLIGHT_UR);
		}
		return Preferences.inst().getBackgroundColor();
	}

	public static Color getLineColor(GeneNode node) {
		return ((GeneNode) node.getParent()).getSubFamilyColor();
	}

	public static Color getOrthoColor(String ortho_name) {
		if (ortho_colors == null) {
			ortho_colors = new HashMap<String, Color> ();
		}
		Color color = ortho_colors.get(ortho_name);
		if (color == null) {
			int red_val = 0;
			int green_val = 0;
			int blue_val = 0;
			while ((red_val + green_val + blue_val) < 128) {
				red_val = (int)(Math.random() * 255);
				green_val = (int)(Math.random() * 255);
				blue_val = (int)(Math.random() * 255);
			}
			color = new Color(red_val, green_val, blue_val);
			ortho_colors.put(ortho_name, color);
		}
		return color;
	}

}
