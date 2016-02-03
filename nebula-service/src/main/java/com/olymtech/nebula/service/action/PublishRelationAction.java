package com.olymtech.nebula.service.action;

import com.olymtech.nebula.core.action.AbstractAction;
import com.olymtech.nebula.dao.INebulaPublishAppDao;
import com.olymtech.nebula.dao.INebulaPublishHostDao;
import com.olymtech.nebula.dao.INebulaPublishModuleDao;
import com.olymtech.nebula.dao.INebulaPublishSlbDao;
import com.olymtech.nebula.entity.*;
import com.olymtech.nebula.entity.enums.PublishAction;
import com.olymtech.nebula.file.analyze.IFileAnalyzeService;
import com.olymtech.nebula.service.IAnalyzeArsenalApiService;
import com.olymtech.nebula.service.IPublishScheduleService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.olymtech.nebula.common.utils.DateUtils.getKeyDate;

/**
 * Created by liwenji on 2015/11/4.
 */
@Service
public class PublishRelationAction extends AbstractAction {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IAnalyzeArsenalApiService analyzeArsenalApiService;

    @Resource
    IFileAnalyzeService fileAnalyzeService;

    @Resource
    INebulaPublishModuleDao nebulaPublishModuleDao;

    @Resource
    INebulaPublishHostDao nebulaPublishHostDao;

    @Resource
    INebulaPublishAppDao nebulaPublishAppDaoImpl;

    @Autowired
    private IPublishScheduleService publishScheduleService;

    @Resource
    private INebulaPublishSlbDao nebulaPublishSlbDao;

    @Value("${master_deploy_dir}")
    private String MasterDeployDir;

    public PublishRelationAction() {

    }

    @Override
    public boolean doAction(NebulaPublishEvent event) throws Exception {
        publishScheduleService.logScheduleByAction(event.getId(), PublishAction.ANALYZE_PROJECT, event.getPublishActionGroup(), null, "");
        String publicWarDirPath = MasterDeployDir + "/" + event.getPublishProductKey() + "/publish_war/";
        List<String> appNameList = fileAnalyzeService.getFileListByDirPath(publicWarDirPath);
        String appNames = "";
        String regex = ".*\\.war";
        int appNameNum = appNameList.size();
        for (int i = 0; i < appNameNum - 1; i++) {
            if (appNameList.get(i).matches(regex)) {
                String appname = appNameList.get(i).replace(".war", "");
                appNames += appname + ",";
            }
        }
        String appname = appNameList.get(appNameNum - 1).replace(".war", "");
        appNames += appname;
        try {
            List<NebulaPublishModule> modules = new ArrayList<>();
            Map<String, Object> resultMap = analyzeArsenalApiService.getSimpleHostListByProductAndModule(event.getPublishProductName(), appNames, event.getPublishEnv());
            if (resultMap == null) {
                publishScheduleService.logScheduleByAction(event.getId(), PublishAction.ANALYZE_PROJECT, event.getPublishActionGroup(), false, "请求基础数据库异常");
                return false;
            }
            String msg = resultMap.get("msg").toString();
            List<ProductTree> moduleTrees = (List) resultMap.get("result");
            if (StringUtils.isNotEmpty(msg)) {
                publishScheduleService.logScheduleByAction(event.getId(), PublishAction.ANALYZE_PROJECT, event.getPublishActionGroup(), false, msg);
                return false;
            }
            if (moduleTrees == null || moduleTrees.size() == 0) {
                publishScheduleService.logScheduleByAction(event.getId(), PublishAction.ANALYZE_PROJECT, event.getPublishActionGroup(), false, "您提交的SVN地址无法正确解析主机及应用的信息(匹配工程名称出错)");
                return false;
            }
            for (ProductTree moduleTree : moduleTrees) {
                NebulaPublishModule nebulaPublishModule = new NebulaPublishModule();
                nebulaPublishModule.setPublishEventId(event.getId());
                nebulaPublishModule.setModuleSrcSvn(moduleTree.getSrcSvn());
                nebulaPublishModule.setPublishModuleName(moduleTree.getNodeName());
                nebulaPublishModule.setPublishProductName(event.getPublishProductName());
                Date now = event.getSubmitDatetime();
                String date = getKeyDate(now);
                String key = event.getPublishEnv() + "." + event.getPublishProductName() + "." + nebulaPublishModule.getPublishModuleName() + "." + date;
                nebulaPublishModule.setPublishModuleKey(key);
                Integer moduleId = nebulaPublishModuleDao.insert(nebulaPublishModule);
                List<NebulaPublishHost> hosts = new ArrayList<>();
                for (SimpleHost simpleHost : moduleTree.getHosts()) {
                    NebulaPublishHost nebulaPublishHost = new NebulaPublishHost();
                    nebulaPublishHost.setPublishEventId(event.getId());
                    nebulaPublishHost.setPublishModuleId(moduleId);
                    nebulaPublishHost.setPassPublishHostName(simpleHost.getHostName());
                    nebulaPublishHost.setPassPublishHostIp(simpleHost.getHostIp());
                    nebulaPublishHostDao.insert(nebulaPublishHost);
                    hosts.add(nebulaPublishHost);
                }
                List<NebulaPublishApp> apps = new ArrayList<>();
                int n = moduleTree.getApps().size();
                for (int i = 0; i < n; i++) {
                    NebulaPublishApp nebulaPublishApp = new NebulaPublishApp();
                    nebulaPublishApp.setPublishAppName(moduleTree.getApps().get(i));
                    nebulaPublishApp.setPublishEventId(event.getId());
                    nebulaPublishApp.setPublishModuleId(nebulaPublishModule.getId());
                    nebulaPublishAppDaoImpl.insert(nebulaPublishApp);
                    apps.add(nebulaPublishApp);
                }
                List<NebulaPublishSlb> slbs = new ArrayList<>();
                for (SimpleSlb slb : moduleTree.getSlbs()) {
                    NebulaPublishSlb nebulaPublishSlb = new NebulaPublishSlb();
                    nebulaPublishSlb.setPublishEventId(event.getId());
                    nebulaPublishSlb.setPublishModuleId(nebulaPublishModule.getId());
                    nebulaPublishSlb.setLoadBalancerId(slb.getLoadBalancerId());
                    nebulaPublishSlb.setLoadBalancerName(slb.getLoadBalancerName());
                    nebulaPublishSlb.setLoadBalancerAddress(slb.getLoadBalancerAddress());
                    nebulaPublishSlb.setLoadBalancerStatus(slb.getLoadBalancerStatus());
                    nebulaPublishSlb.setRegionId(slb.getRegionId());
                    nebulaPublishSlb.setAliyunAccount(slb.getAliyunAccount());
                    nebulaPublishSlbDao.insert(nebulaPublishSlb);
                    slbs.add(nebulaPublishSlb);
                }
                nebulaPublishModule.setPublishHosts(hosts);
                nebulaPublishModule.setPublishApps(apps);
                nebulaPublishModule.setPublishSlbs(slbs);
                modules.add(nebulaPublishModule);
            }
            event.setPublishModules(modules);
            publishScheduleService.logScheduleByAction(event.getId(), PublishAction.ANALYZE_PROJECT, event.getPublishActionGroup(), true, "");
            return true;
        } catch (Exception e) {
            publishScheduleService.logScheduleByAction(event.getId(), PublishAction.ANALYZE_PROJECT, event.getPublishActionGroup(), false, "error message");
            logger.error("PublishRelationAction error:", e);
        }
        publishScheduleService.logScheduleByAction(event.getId(), PublishAction.ANALYZE_PROJECT, event.getPublishActionGroup(), false, "error message");
        return false;
    }

    @Override
    public void doFailure(NebulaPublishEvent event) {

    }
}
