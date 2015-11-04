/**
 * Olymtech.com Inc.
 * Copyright (c) 2002-2015 All Rights Reserved
 */
package com.olymtech.nebula.core.action;

import com.olymtech.nebula.core.action.AbstractAction;
import com.olymtech.nebula.entity.NebulaPublishEvent;

/**
 * @author taoshanchang 15/11/4
 */
public class Action1 extends AbstractAction {

    public Action1(String actionName) {
        super.setActionName(actionName);
    }

    @Override
    public boolean doAction(NebulaPublishEvent event) throws Exception {
        System.out.println("创建文件夹");
        return true;
    }

    @Override
    public void doFailure(NebulaPublishEvent event) {
        System.out.println("回滚创建文件夹");
    }
}
