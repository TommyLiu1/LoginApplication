package com.inepro.service.login.group;


import com.canon.meap.service.login.group.GroupAttributeContext;

/**
 * 
 * This interface is used for the Group Function. extends GroupAttributeContext.
 */
public interface GroupAttributeContextExt extends GroupAttributeContext {

    /**
     * Sets the GroupDataImpl.
     * 
     * @param groupDataImpl
     * the GroupDataImpl.
     */
    void addGroup(GroupDataImpl groupDataImpl);

}
