package com.olymtech.nebula.controller;

import com.github.pagehelper.PageInfo;
import com.olymtech.nebula.entity.AclPermission;
import com.olymtech.nebula.entity.Callback;
import com.olymtech.nebula.entity.DataTablePage;
import com.olymtech.nebula.entity.zNode;
import com.olymtech.nebula.service.IAclPermissionService;
import com.olymtech.nebula.service.IAclRoleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WYQ on 2015/11/17.
 */
@Controller
@RequestMapping("/permission")
public class PermissionController extends BaseController {

    @Resource
    private IAclPermissionService permissionService;

    @Resource
    private IAclRoleService aclRoleService;

    @RequestMapping(value="/createPermission.htm",method= {RequestMethod.POST,RequestMethod.GET})
    public String createPermission (){
        return "permission/createPermission";
    }

    @RequestMapping(value = "/insertAclPermission.htm", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Callback insertAclPermission(AclPermission permission) throws Exception{
        permissionService.insertAclPermission(permission);

        return returnCallback("Success", "插入权限成功");
    }

    @RequestMapping(value = "/deleteAclPermission.htm", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Callback deleteAclPermission(Integer id) {
        permissionService.deleteAclPermissionById(id);
        return returnCallback("Success", "删除权限成功");
    }

    @RequestMapping(value = "/updateAclPermission.htm", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Callback updateAclPermission(AclPermission permission) {
        permissionService.updateAclPermission(permission);
        return returnCallback("Success", "更新权限成功");
    }

    @RequestMapping(value = "/selectAllPagingPermission", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object selectAllPagingPermission(DataTablePage dataTablePage) {
        PageInfo pageInfo = permissionService.getPageInfoAclPermission(dataTablePage);
        return pageInfo;
    }

    @RequestMapping(value = "/selectPermission", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object selectPermission(Integer id) {
        List<AclPermission> permissionList = new ArrayList<>();
        if (id != null) {
            permissionList = aclRoleService.getAclRoleWithPermissionsByRoleId(id).getAclPermissions();
        }
        List<zNode> zNodes = permissionService.getzNodes(permissionList);
        return zNodes;
    }

}

