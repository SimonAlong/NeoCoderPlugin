package com.simonalong.neo.coder.plugin;

import java.io.Serializable;

/**
 * @author shizi
 * @since 2020/7/15 3:44 PM
 */
public class CreateTableMetaConfig implements Serializable {

    public CreateTableMetaConfig(){}

    /**
     * 建表语句
     */
    private String createTableStr;
    /**
     * 后端的路径
     */
    private String backendPath;
    /**
     * 项目名字
     */
    private String appName;
    /**
     * 包路径
     */
    private String packagePath;
    /**
     * 包路径
     */
    private String tablePre;

    public String getCreateTableStr() {
        return createTableStr;
    }

    public void setCreateTableStr(String createTableStr) {
        this.createTableStr = createTableStr;
    }

    public String getBackendPath() {
        return backendPath;
    }

    public void setBackendPath(String backendPath) {
        this.backendPath = backendPath;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTablePre() {
        return tablePre;
    }

    public void setTablePre(String tablePre) {
        this.tablePre = tablePre;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }
}
