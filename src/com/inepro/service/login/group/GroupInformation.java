package com.inepro.service.login.group;

import com.canon.meap.csee.service.login.base.LoginContextExt;
import com.inepro.service.login.Activator;
import com.canon.meap.service.login.group.GroupData;
import com.canon.meap.service.login.group.GroupManagementService;
import com.canon.meap.service.login.group.GroupManagerException;

/**
 * This class sets the groupinformation acquired from GroupManager to LoginContext.
 */
public class GroupInformation {

    private static GroupManagementService groupManagementService = null;

    public GroupInformation() {

    }

    /**
     * sets the groupinformation acquired from GroupManager to LoginContext.
     * @param context GroupAttributeContextExt
     * @return LoginContextExt
     */
    public static LoginContextExt getGroupData(GroupAttributeContextExt context) {

        GroupData[] groupDataList = null;

        if (groupManagementService == null) {
            groupManagementService = (GroupManagementService) Activator
                    .getBundleContext().getService(
                            Activator.getBundleContext().getServiceReference(
                                    GroupManagementService.class.getName()));
        }

        if (groupManagementService == null) {
            return (LoginContextExt) context;
        }

        try {
            groupDataList = groupManagementService.getGroupDatas(context);
        } catch (GroupManagerException e) {
            return (LoginContextExt)context;
        }

        for (int i = 0; i < groupDataList.length; i++) {
            if (groupDataList[i] != null) {
                context.addGroup(new GroupDataImpl(groupDataList[i]));
            }
        }
        return (LoginContextExt) context;
    }
}
