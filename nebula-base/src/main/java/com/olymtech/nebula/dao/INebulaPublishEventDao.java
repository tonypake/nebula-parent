/**
 * Olymtech.com Inc.
 * Copyright (c) 2002-2015 All Rights Reserved.
 */
package com.olymtech.nebula.dao;

import com.olymtech.nebula.entity.NebulaPublishEvent;

import java.util.List;

/**
 * Created by Gavin on 2015-10-23 14:19.
 */
public interface INebulaPublishEventDao extends IBaseDao<NebulaPublishEvent,Integer> {

    public List<NebulaPublishEvent> selectAllPagingWithUser(NebulaPublishEvent event);

    public int selectCountWithUser(NebulaPublishEvent event);

    public List<NebulaPublishEvent> selectNoPUBLISHING(NebulaPublishEvent nebulaPublishEvent);
}
