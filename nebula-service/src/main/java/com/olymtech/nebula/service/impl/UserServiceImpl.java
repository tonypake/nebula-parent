/**
 * Olymtech.com Inc.
 * Copyright (c) 2002-2015 All Rights Reserved
 */
package com.olymtech.nebula.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.olymtech.nebula.dao.IAclRoleDao;
import com.olymtech.nebula.dao.IAclRolePermissionDao;
import com.olymtech.nebula.dao.IAclUserRoleDao;
import com.olymtech.nebula.dao.INebulaUserInfoDao;
import com.olymtech.nebula.entity.*;
import com.olymtech.nebula.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author taoshanchang 15/11/9
 */
@Service
public class UserServiceImpl implements IUserService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private INebulaUserInfoDao nebulaUserInfoDao;

    @Resource
    private IAclUserRoleDao aclUserRoleDao;

    @Resource
    private IAclRoleDao aclRoleDao;

    @Resource
    private IAclRolePermissionDao aclRolePermissionDao;

    @Override
    public NebulaUserInfo findByUsername(String username) {
        NebulaUserInfo nebulaUserInfo = nebulaUserInfoDao.selectByUsername(username);
        return nebulaUserInfo;
    }

    @Override
    public Set<String> findRolesByEmpId(Integer empId) {
        Set<String> roles = new HashSet<String>();
        List<AclUserRole> aclUserRoles = aclUserRoleDao.selectByEmpId(empId);
        for (AclUserRole aclUserRole : aclUserRoles) {
            roles.add(aclUserRole.getAclRole().getRoleName());
        }
        return roles;
    }

    @Override
    @Deprecated
    public Set<String> findPermissionsByEmpId(Integer empId) {
        Set<String> permissions = new HashSet<String>();
        List<AclUserRole> aclUserRoles = aclUserRoleDao.selectByEmpId(empId);
        for (AclUserRole aclUserRole : aclUserRoles) {
            List<AclRolePermission> aclRolePermissions = aclRolePermissionDao.selectByRoleId(aclUserRole.getRoleId());
            for (AclRolePermission aclRolePermission : aclRolePermissions) {
                permissions.add(aclRolePermission.getAclPermission().getPermission());
            }
        }
        return permissions;
    }

    @Override
    public int insertNebulaUserInfo(NebulaUserInfo nebulaUserInfo, Integer[] roleIds) {
        Integer id = nebulaUserInfoDao.insert(nebulaUserInfo);
        for (Integer roleId : roleIds) {
            AclUserRole aclUserRole = new AclUserRole();
            aclUserRole.setEmpId(nebulaUserInfo.getEmpId());
            aclUserRole.setRoleId(roleId);
            aclUserRoleDao.insert(aclUserRole);
        }
        return id;
    }

    @Override
    public void deleteNebulaUserInfo(Integer id) {
        nebulaUserInfoDao.deleteById(id);
        aclUserRoleDao.deleteById(id);
    }

    @Override
    public void updateNebulaUserInfo(NebulaUserInfo nebulaUserInfo, List<Integer> roleIds) {
        nebulaUserInfoDao.update(nebulaUserInfo);
        aclUserRoleDao.deleteByEmpId(nebulaUserInfo.getEmpId());
        for (Integer roleId : roleIds) {
            AclUserRole aclUserRole = new AclUserRole();
            aclUserRole.setEmpId(nebulaUserInfo.getEmpId());
            aclUserRole.setRoleId(roleId);
            aclUserRoleDao.insert(aclUserRole);
        }
    }

    @Override
    public PageInfo getPageInfoAclUser(DataTablePage dataTablePage) {
        PageHelper.startPage(dataTablePage.getPageNum(), dataTablePage.getPageSize());
        List<NebulaUserInfo> users = nebulaUserInfoDao.selectAllPaging(new NebulaUserInfo());
        PageInfo pageInfo = new PageInfo(users);
        return pageInfo;
    }


    @Override
    public NebulaUserInfo getAclUserWithRolesByEmpId(Integer empId) {
        NebulaUserInfo userInfo = nebulaUserInfoDao.selectByEmpId(empId);
        List<Integer> roleIds = new ArrayList<>();
        List<AclUserRole> aclUserRoles = aclUserRoleDao.selectByEmpId(empId);
        for (AclUserRole aclUserRole : aclUserRoles) {
            roleIds.add(aclUserRole.getRoleId());
        }
        if(roleIds.size()>0) {
            List<AclRole> aclRoles = aclRoleDao.selectByIds(roleIds);
            userInfo.setAclRoles(aclRoles);
        }
        return userInfo;
    }
}
