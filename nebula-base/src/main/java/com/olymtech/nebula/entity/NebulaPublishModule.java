package com.olymtech.nebula.entity;

public class NebulaPublishModule extends BaseDO {

    private Integer publishEventId;

    private String publishProductName;

    private String publishModuleName;

    private String publishModuleKey;

    private String moduleSrcSvn;

    public Integer getPublishEventId() {
        return publishEventId;
    }

    public void setPublishEventId(Integer publishEventId) {
        this.publishEventId = publishEventId;
    }

    public String getPublishProductName() {
        return publishProductName;
    }

    public void setPublishProductName(String publishProductName) {
        this.publishProductName = publishProductName;
    }

    public String getPublishModuleName() {
        return publishModuleName;
    }

    public void setPublishModuleName(String publishModuleName) {
        this.publishModuleName = publishModuleName;
    }

    public String getPublishModuleKey() {
        return publishModuleKey;
    }

    public void setPublishModuleKey(String publishModuleKey) {
        this.publishModuleKey = publishModuleKey == null ? null : publishModuleKey.trim();
    }

    public String getModuleSrcSvn() {
        return moduleSrcSvn;
    }

    public void setModuleSrcSvn(String moduleSrcSvn) {
        this.moduleSrcSvn = moduleSrcSvn == null ? null : moduleSrcSvn.trim();
    }
}