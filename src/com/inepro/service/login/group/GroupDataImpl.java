package com.inepro.service.login.group;

import com.canon.meap.service.login.GroupData;

/**
 * 
 * this class implements the com.canon.meap.service.login.group.GroupData.
 *
 */
public class GroupDataImpl implements GroupData {

    private String groupName;

    private String groupUuid;

    /**
     * Constructs a new instance of GroupDataImpl.
     * @param groupData GroupData
     * 
     */
    public GroupDataImpl(com.canon.meap.service.login.group.GroupData groupData) {
        if (groupData != null) {
            this.groupName = groupData.getGroupName();
            this.groupUuid = groupData.getGroupUuid();
        }
    }

    /**
     * 
     * Return the Group Name.
     * @return 
     * the Group Name
     */
    public String getGroupName() {
        return this.groupName;
    }

    /**
     * 
     * Return the Group uuid.
     * @return 
     * Group uuid
     */
    public String getGroupUuid() {
        return this.groupUuid;
    }

    /**
     * Returns the attributes configured in this class.
     * 
     * @param key
     * Keys for getting the attribute
     * 
     * @return
     * Value of the attribute obtained using the key
     */
    public Object getAttribute(String arg0) {
        return null;
    }
}
