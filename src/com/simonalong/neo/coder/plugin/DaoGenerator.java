package com.simonalong.neo.coder.plugin;

import com.simonalong.neo.NeoMap;
import com.simonalong.neo.StringConverter;
import com.simonalong.neo.coder.plugin.util.FileUtil;
import com.simonalong.neo.coder.plugin.util.FreeMarkerTemplateUtil;
import freemarker.template.TemplateException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * @author shizi
 * @since 2020/7/15 4:54 PM
 */
public class DaoGenerator {

    private static final String TEMPLATE_NAME = "dao";

    private static final String DAO_TEMPLATE = "" +
        "package ${packagePath}.dao;" +"\n" + "\n" +
        "import com.simonalong.neo.Neo;" +"\n" +
        "import com.simonalong.neo.core.AbstractBizService;\n" +
        "import org.springframework.beans.factory.annotation.Autowired;\n" +
        "import org.springframework.stereotype.Repository;\n" + "\n" +
        "/**\n" +
        " * @author robot\n" +
        " */\n" +
        "@Repository\n" +
        "public class ${tablePathName}Dao extends AbstractBizService {\n" + "\n" +
        "    @Autowired\n" + "    private Neo db;\n" + "\n" +
        "    @Override\n" +
        "    public Neo getDb() {\n" +
        "        return db;\n" +
        "    }\n" + "\n" +
        "    @Override\n" +
        "    public String getTableName() {\n" +
        "        return \"${tableName}\";\n" +
        "    }\n" +
        "}\n";

    private NeoMap paramsMap = NeoMap.of();
    private String backendPath;
    private String packagePath;
    private String tablePathName;
    private String tableName;
    private String tablePre;

    static {
        FreeMarkerTemplateUtil.putTemplate(TEMPLATE_NAME, DAO_TEMPLATE);
    }

    public void configData(String backendPath, String packagePath, String tableName, String tablePre) {
        if (!backendPath.endsWith("/")) {
            backendPath += "/";
        }

        String tableNameAfterPre = excludePreFix(tableName, tablePre);
        String tablePathName = StringConverter.underLineToBigCamel(tableNameAfterPre);

        paramsMap.put("backendPath", backendPath);
        paramsMap.put("packagePath", packagePath);
        paramsMap.put("tablePathName", tablePathName);
        paramsMap.put("tableName", tableName);
        paramsMap.put("tablePre", tablePre);

        this.backendPath = backendPath;
        this.packagePath = packagePath;
        this.tablePathName = tablePathName;
        this.tableName = tableName;
        this.tablePre = tablePre;
    }

    /**
     * 去除前缀：lk_config_group -> config_group
     */
    private String excludePreFix(String tableName, String tablePre) {
        if (null != tablePre && tableName.startsWith(tablePre)) {
            return tableName.substring(tablePre.length());
        }
        return tableName;
    }

    /**
     * 解析
     *
     * @return 生成的代码
     */
    public void generateCode() {
        String tableNameAfterPre = excludePreFix(tableName, tablePre);
        String backendCodePath = backendPath + "src/main/java/";
        backendCodePath += packagePath.replace(".", "/") + "/";

        writeFile(backendCodePath + "dao/" + StringConverter.underLineToBigCamel(tableNameAfterPre) + "Dao.java");
    }

    private void writeFile(String filePath) {
        try {
            if (!FileUtil.exist(filePath)) {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FileUtil.getFile(filePath)));
                Objects.requireNonNull(FreeMarkerTemplateUtil.getTemplate(TEMPLATE_NAME)).process(paramsMap, bufferedWriter);
            }
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
        }
    }
}
