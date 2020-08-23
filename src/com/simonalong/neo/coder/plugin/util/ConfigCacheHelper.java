package com.simonalong.neo.coder.plugin.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.simonalong.neo.coder.plugin.CreateTableMetaConfig;

import java.io.IOException;

/**
 * @author shizi
 * @since 2020/7/15 3:43 PM
 */
public class ConfigCacheHelper {

    private static String BASE_CODE_PATH = "root".equals(System.getProperty("user.name")) ? "/var/cache/walle-plugin/" : System.getProperty("user.home") + "/.cache/walle-plugin/";

    public static String getRoot() {
        return BASE_CODE_PATH;
    }

    public static String getProjectPath(String appName) {
        return getRoot() + "/" + appName;
    }

    public static void save(CreateTableMetaConfig tableMetaConfig) throws IOException {
        if(null == tableMetaConfig) {
            return;
        }
        String filePath = getProjectPath(tableMetaConfig.getAppName());
        if (!FileUtil.exist(filePath)) {
            FileUtil.createFile(filePath);
        }
        try {
            FileUtil.writeFile(filePath, JSON.toJSONString(tableMetaConfig));
        } catch (IOException e) {
            throw e;
        }
    }

    public static CreateTableMetaConfig getConfig(String appName) throws IOException {
        try {
            String result = FileUtil.read(getProjectPath(appName));
            return JSONObject.parseObject(result, CreateTableMetaConfig.class);
        } catch (IOException e) {
            throw e;
        }
    }

}
