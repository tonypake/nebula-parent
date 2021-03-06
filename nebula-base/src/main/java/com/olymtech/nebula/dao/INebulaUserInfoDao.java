/**
 * Olymtech.com Inc.
 * Copyright (c) 2002-2015 All Rights Reserved.
 */
package com.olymtech.nebula.dao;

import com.olymtech.nebula.entity.NebulaUserInfo;

import java.util.List;

/**
 * Created by Gavin on 2015-10-23 14:27.
 */
public interface INebulaUserInfoDao extends IBaseDao<NebulaUserInfo,Integer> {

    public NebulaUserInfo selectByUsername(String username);

    public NebulaUserInfo selectByEmpId(Integer empId);

    public void updatePassword(NebulaUserInfo user);

    public List<NebulaUserInfo> selectAllPagingLike(NebulaUserInfo userInfo);

}

