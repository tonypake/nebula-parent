/**
 * Olymtech.com Inc.
 * Copyright (c) 2002-2015 All Rights Reserved.
 */
package com.olymtech.nebula.service.impl;


import com.olymtech.nebula.core.svn.SvnUtils;
import com.olymtech.nebula.service.ISvnService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * Created by Gavin on 2016-04-06 17:13.
 */
@Service
public class SvnServiceImpl implements ISvnService {

    private SVNClientManager warSvnClientManager = null;

    private ISVNAuthenticationManager authManager = null;

    @Value("${war_svn_url}")
    private String warSvnUrl;
    @Value("${war_svn_username}")
    private String warSvnUsername;
    @Value("${war_svn_password}")
    private String warSvnPassword;

    @Override
    public SVNClientManager getWarSvnClientManager(){
        if(warSvnClientManager == null){
            warSvnClientManager = SvnUtils.createSvnClientManager(warSvnUrl, warSvnUsername, warSvnPassword);
        }
        return warSvnClientManager;
    }

    @Override
    public ISVNAuthenticationManager getWarAuthManager(){
        if(authManager == null){
            authManager = SVNWCUtil.createDefaultAuthenticationManager(warSvnUsername, warSvnPassword);
        }
        return authManager;
    }

}
