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
package com.sri.panther.paintCommon;

import com.sri.panther.paintCommon.util.Utils;
import java.io.Serializable;

public class User implements Serializable {
    protected String firstName;
    protected String lastName;
    protected String email;
    protected int privilegeLevel = Constant.USER_PRIVILEGE_NOT_SET;
    protected String loginName;
    protected String groupName;
    protected String userId;
    protected String name;

    protected String role;
    
    public static final String ROLE_OBSERVER = "observer";
    public static final String ROLE_USER = "user";
    public static final String ROLE_CURATOR = "curator";
    public static final String ROLE_ADMIN = "administrator";
    public static final String ROLE_UNKNOWN = "unknown";
    
    public static final String VALID_ROLES[] = {ROLE_OBSERVER, ROLE_USER, ROLE_CURATOR, ROLE_ADMIN};
    public static final String UPDATE_USER_ROLES[] = {ROLE_USER, ROLE_CURATOR, ROLE_ADMIN};    
    
    public User(String firstName, String lastName, String email, String loginName, int privilegeLevel, String groupName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.loginName = loginName;
        this.privilegeLevel = privilegeLevel;
        this.groupName = groupName;
    }
    
    public User() {
        
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    
    
    public int getprivilegeLevel() {
        return privilegeLevel;
    }
    
    
    public void setPrivilegeLevel(int privilegeLevel) {
        this.privilegeLevel = privilegeLevel;
    }
    
    
    public String getloginName() {
        return loginName;
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public static boolean isGOUser(boolean isLogged, String groupName) {
        if (false == isLogged) {
            return false;
        }
        
        if (null == groupName) {
            return false;
        }
        
        if (0 != groupName.compareTo(Constant.GROUP_NAME_GO_USER)){
                return false;
        }
        return true;
        
        
    }
    public String getRole() {
        return role;
    }

    public boolean setRole(String role) {
        if (Utils.getIndex(VALID_ROLES, role) >= 0) {
            this.role = role;
            return true;
        }
        this.role = ROLE_UNKNOWN;
        return false;
    }

    // Only users with role of USER, CURATOR and ADMIN can lock or update books
    public static boolean privToLockOrUpdateBook(User user) {
        if (null == user) {
            return false;
        }
        String role = user.getRole();
        // Only users with role of USER, CURATOR and ADMIN can lock or update books
        if (true == User.ROLE_USER.equals(role) ||  true == User.ROLE_CURATOR.equals(role) || true == User.ROLE_ADMIN.equals(role)) {
            return true;
        }
        return false;
    }
    /**
     * 
     * @param user
     * @param status Book status
     * @param lastModifiedBy
     * @return 
     */
    public static boolean privToLockOrUpdateBookWithStatus(User user, int status, String lastModifiedBy) {
        if (null == user) {
            return false;
        }
        String role = user.getRole();
        if (null == role) {
            return false;
        }
        if (Book.CURATION_STATUS_DISUSED == status) {
            if (User.ROLE_ADMIN.equals(role)) {
                return true;
            }
            return false;
        } else if (Book.CURATION_STATUS_APPLY == status) {
            if (User.ROLE_ADMIN.equals(role) || User.ROLE_CURATOR.equals(role)) {
                return true;
            }
            return false;
        } else if (Book.CURATION_STATUS_TEST == status) {
            if (User.ROLE_ADMIN.equals(role) || User.ROLE_CURATOR.equals(role)) {
                return true;
            } else if (User.ROLE_USER.equals(role) && (user.getloginName() != null && user.getLoginName().equals(lastModifiedBy))) {    
                // For Test status, user can update only if last modified by is same user
                return true;
            }
            return false;
        } else if (Book.CURATION_STATUS_UNKNOWN == status) {
            if (User.ROLE_ADMIN.equals(role) || User.ROLE_CURATOR.equals(role) || User.ROLE_USER.equals(role)) {
                return true;
            }
            return false;
        }
        return false;
    }    
    
    
    public Object clone() {
        return new User(firstName, lastName, email, loginName, privilegeLevel, groupName);

    }


}
