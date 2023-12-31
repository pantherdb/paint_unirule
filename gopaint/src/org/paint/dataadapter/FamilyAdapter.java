/* 
 * 
 * Copyright (c) 2010, Regents of the University of California 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Neither the name of the Lawrence Berkeley National Lab nor the names of its contributors may be used to endorse 
 * or promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package org.paint.dataadapter;

import java.io.Serializable;

import org.paint.datamodel.Family;



public abstract class FamilyAdapter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract boolean fetchFamily(Family family);

	public FamilyAdapter() {
	}

	public boolean initFamily(Family family, String [] treeContents, String sfanInfo[], String  attrContents[], String[] msaContents, String[] wtsContents) {
//		if (treeContents == null || sfanInfo == null || attrContents == null) {
//			return false;
//		}
//
//		family.setTreeStrings(treeContents);
//		family.setAttrTable(attrContents);
//		family.setSfAnInfo(sfanInfo);
//		family.setMSAcontent(msaContents);
//		family.setWtsContent(wtsContents);

		return true;
	}
}