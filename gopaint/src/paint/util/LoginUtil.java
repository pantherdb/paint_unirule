/**
 * Copyright 2021 University Of Southern California
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

package org.paint.util;

import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.bbop.framework.GUIManager;
import org.paint.config.PantherDbInfo;
import org.paint.config.Preferences;
import org.paint.dataadapter.PantherServer;
import org.paint.dialog.PantherURLSelectionDlg;

import com.sri.panther.paintCommon.FixedInfo;
import com.sri.panther.paintCommon.TransferInfo;

public class LoginUtil {

	// stored when the settings are written into a property file
	private static boolean       logged_in = false;  // Indicates user login status.  Note:  This information is not stored when the settings are written into a property file
	private static String username = "paint";
	private static String pw = "Pan7h3r";

	public static boolean login() {
		if (!logged_in) {
			login(username, pw);
		}
		return logged_in;
	}

	public static void logout() {
		logged_in = false;
	}
	
	private static Logger log = Logger.getLogger(LoginUtil.class);

	/**
	 * 
	 * @param results The first element in the vector is a String holding the user name to use.  The Second it an character array that contains the password.
	 * 
	 */
	public static void login(String username, String pw) {
		// Determine what databases and upl's are available from the server
		// Get information that does not change.
		logged_in = false;

		if (!InternetChecker.getInstance().isConnectionPresent(true)) {
			return;
		}
		String loginErrorMsg = "";

		String pantherURL = Preferences.inst().getPantherURL();
		log.debug("Logging in to Panther URL: " + pantherURL);
		FixedInfo fi = PantherServer.inst().getFixedInfoFromServer(pantherURL);
		if (fi == null){
			loginErrorMsg = PantherServer.inst().getServerStatus();
		} else {
			loginErrorMsg = PantherDbInfo.setFixedInfo(fi);
			if (loginErrorMsg.length() == 0) {
				String upload_version = PantherDbInfo.getDbAndVersionName();
				// Ensure user property file contains database and upl that is currently available from the server.
				if (!fi.dbUploadValid(upload_version)) {
					PantherURLSelectionDlg seldlg = new PantherURLSelectionDlg(GUIManager.getManager().getFrame());
					seldlg.display();
				}
			}
		}
		if (loginErrorMsg.length() == 0) {
			
			Vector<Object> results = new Vector<Object>();
			results.addElement(username);
			results.addElement(pw.toCharArray());

			Vector objs = new Vector();
			objs.addElement(results);
			objs.addElement(FixedInfo.getDb(PantherDbInfo.getDbAndVersionName()));

			Object  o = PantherServer.inst().sendAndReceive(Preferences.inst().getPantherURL(), "GetUserInfo", objs, null, null);

			if (o == null) {
				loginErrorMsg = "Unable to get user information";
			} else {
				Vector output = (Vector) o;
				TransferInfo  ti = (TransferInfo) output.elementAt(0);
				if (ti.getInfo() == null)
					loginErrorMsg = "Unable to verify user information";
				else
					loginErrorMsg = ti.getInfo();
			}
		}
		logged_in = loginErrorMsg.length() == 0;
		if (!logged_in)
			JOptionPane.showMessageDialog(GUIManager.getManager().getFrame(), loginErrorMsg);
	}

	public static boolean getLoggedIn() {
		return logged_in;
	}

	public static Vector<Object> getUserInfo() {
		Vector<Object> userInfo = new Vector<Object>();
		userInfo.addElement(username);
		userInfo.addElement(pw.toCharArray());
		return userInfo;
	}
}