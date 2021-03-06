/**
 * Olymtech.com Inc.
 * Copyright (c) 2002-2015 All Rights Reserved.
 */
package com.olymtech.nebula.dao.impl;

import com.olymtech.nebula.dao.INebulaUserInfoDao;
import com.olymtech.nebula.entity.NebulaUserInfo;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Gavin on 2015-10-23 14:38.
 */
@Repository("nebulaUserInfoDao")
public class NebulaUserInfoDaoImpl extends BaseDaoImpl<NebulaUserInfo,Integer> implements INebulaUserInfoDao {
    @Override
    public NebulaUserInfo selectByUsername(String username) {
        SqlSession sqlSession = getSqlSession();
        NebulaUserInfo user = sqlSession.selectOne(CLASS_NAME + "-Select-By-UserName", username);
        return user;
    }

    @Override
    public NebulaUserInfo selectByEmpId(Integer empId) {
        return getSqlSession().selectOne(CLASS_NAME + "-Select-By-Emp-Id", empId);
    }

    @Override
    public void updatePassword(NebulaUserInfo user) {
        getSqlSession().update(CLASS_NAME + "-UpdatePassWord-By-Id", user);
    }

    @Override
    public List<NebulaUserInfo> selectAllPagingLike(NebulaUserInfo userInfo) {
        return getSqlSession().selectList(CLASS_NAME + "-Select-All-Paging-Where-Like", userInfo);
    }

}
