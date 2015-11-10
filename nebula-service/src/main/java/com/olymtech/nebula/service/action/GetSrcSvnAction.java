/**
 * Olymtech.com Inc.
 * Copyright (c) 2002-2015 All Rights Reserved.
 */
package com.olymtech.nebula.service.action;

import com.olymtech.nebula.core.action.AbstractAction;
import com.olymtech.nebula.core.svn.SvnUtils;
import com.olymtech.nebula.entity.NebulaPublishEvent;
import com.olymtech.nebula.entity.enums.PublishAction;
import com.olymtech.nebula.entity.enums.PublishActionGroup;
import com.olymtech.nebula.service.IPublishScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.tmatesoft.svn.core.wc.SVNClientManager;

/**
 * Created by Gavin on 2015-11-04 20:12.
 */
@Service
public class GetSrcSvnAction extends AbstractAction {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IPublishScheduleService publishScheduleService;

    @Value("${master_deploy_dir}")
    private String MasterWarDir;
    @Value("${svn_username}")
    private String SvnUsername;
    @Value("${svn_password}")
    private String SvnPassword;

    @Override
    public boolean doAction(NebulaPublishEvent event) throws Exception {
        publishScheduleService.logScheduleByAction(event.getId(), PublishAction.GET_SRC_SVN, PublishActionGroup.PRE_MASTER, null, "");
        String svnUrl = event.getProductSrcSvn()+"/"+event.getPublishEnv();
        SVNClientManager svnClientManager = SvnUtils.createSvnClientManager(svnUrl, SvnUsername, SvnPassword);
        try{
            SvnUtils.checkout(svnClientManager,svnUrl,MasterWarDir + event.getPublishProductKey() + "/src_svn/");
            publishScheduleService.logScheduleByAction(event.getId(), PublishAction.GET_SRC_SVN, PublishActionGroup.PRE_MASTER, true, "");
            return true;
        }catch (Exception e){
            logger.error("GetPublishSvnAction error:",e);
        }
        publishScheduleService.logScheduleByAction(event.getId(), PublishAction.GET_SRC_SVN, PublishActionGroup.PRE_MASTER, false, "error message");
        return false;
    }

    @Override
    public void doFailure(NebulaPublishEvent event) {

    }
}