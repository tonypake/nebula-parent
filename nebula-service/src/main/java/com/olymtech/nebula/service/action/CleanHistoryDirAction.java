/**
 * Olymtech.com Inc.
 * Copyright (c) 2002-2015 All Rights Reserved
 */
package com.olymtech.nebula.service.action;

import com.olymtech.nebula.core.action.AbstractAction;
import com.olymtech.nebula.core.salt.ISaltStackService;
import com.olymtech.nebula.entity.NebulaPublishEvent;
import com.olymtech.nebula.entity.NebulaPublishHost;
import com.olymtech.nebula.entity.NebulaPublishModule;
import com.olymtech.nebula.entity.enums.PublishAction;
import com.olymtech.nebula.entity.enums.PublishActionGroup;
import com.olymtech.nebula.service.IPublishBaseService;
import com.olymtech.nebula.service.IPublishHostService;
import com.olymtech.nebula.service.IPublishScheduleService;
import com.suse.saltstack.netapi.results.ResultInfo;
import com.suse.saltstack.netapi.results.ResultInfoSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author taoshanchang 15/11/17
 */
@Service
public class CleanHistoryDirAction extends AbstractAction {
    @Autowired
    private ISaltStackService saltStackService;
    @Autowired
    private IPublishScheduleService publishScheduleService;
    @Autowired
    private IPublishHostService publishHostService;
    @Autowired
    private IPublishBaseService publishBaseService;

    @Value("${base_war_dir}")
    private String BaseWarDir;
    @Value("${base_etc_dir}")
    private String BaseEtcDir;
    @Value("${etc_link}")
    private String EtcLink;
    @Value("${war_link}")
    private String WarLink;

    @Override
    public boolean doAction(NebulaPublishEvent event) throws Exception {
        publishScheduleService.logScheduleByAction(event.getId(), PublishAction.CLEAN_HISTORY_DIR, event.getPublishActionGroup(), null, "");

        List<NebulaPublishModule> publishModules = event.getPublishModules();

        for (NebulaPublishModule publishModule : publishModules) {

            List<NebulaPublishHost> publishHosts = publishModule.getPublishHosts();
            List<String> targets = new ArrayList<String>();
            for (NebulaPublishHost nebulaPublishHost : publishHosts) {
                targets.add(nebulaPublishHost.getPassPublishHostIp());
            }

            List<String> pathList = new ArrayList<String>();
            pathList.add(BaseWarDir + publishBaseService.checkoutHistoryDirKey(event, publishModule));
            pathList.add(BaseEtcDir + publishBaseService.checkoutHistoryDirKey(event, publishModule));

            ResultInfoSet result = saltStackService.deleteFile(targets, pathList, true);

            ResultInfo resultInfo = result.get(0);
            Map<String, Object> results = resultInfo.getResults();
            int i = 0;
            int successCount = 0;
            for (Map.Entry<String, Object> entry : results.entrySet()) {
                NebulaPublishHost nebulaPublishHost = publishHosts.get(i++);
                nebulaPublishHost.setActionGroup(PublishActionGroup.CLEAN_END);
                nebulaPublishHost.setActionName(PublishAction.CLEAN_HISTORY_DIR);
                if (entry.getValue().equals("")) {
                    nebulaPublishHost.setActionResult("success");
                    nebulaPublishHost.setIsSuccessAction(true);
                    publishHostService.updatePublishHost(nebulaPublishHost);
                    successCount++;
                } else {
                    nebulaPublishHost.setActionResult(entry.getValue().toString());
                    nebulaPublishHost.setIsSuccessAction(false);
                    publishHostService.updatePublishHost(nebulaPublishHost);
                }
            }
            if (successCount != targets.size()) {
                publishScheduleService.logScheduleByAction(
                        event.getId(),
                        PublishAction.CLEAN_HISTORY_DIR,
                        event.getPublishActionGroup(),
                        false,
                        "执行'清理历史目录'异常。实际成功'清理历史目录'主机数:" + successCount + ", 目标'清理历史目录'主机数:" + targets.size()
                );
                return false;
            }

        }
        publishScheduleService.logScheduleByAction(event.getId(), PublishAction.CLEAN_HISTORY_DIR, event.getPublishActionGroup(), true, "All models and sub targes 'execute' success");
        return true;
    }

    @Override
    public void doFailure(NebulaPublishEvent event) {

    }

    @Override
    public boolean doCheck(NebulaPublishEvent event) throws Exception {
        return true;
    }
}
