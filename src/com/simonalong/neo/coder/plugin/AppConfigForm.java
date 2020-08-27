package com.simonalong.neo.coder.plugin;

import com.intellij.openapi.project.Project;
import com.simonalong.neo.NeoMap;
import com.simonalong.neo.codegen.config.SqlGeneratorConfig;
import com.simonalong.neo.codegen.generator.CreateSqlEntityCodeGenerator;
import com.simonalong.neo.coder.plugin.util.ConfigCacheHelper;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

/**
 * @author shizi
 * @since 2020/7/7 6:23 PM
 */
public class AppConfigForm {

    /**
     * 总页面
     */
    private JPanel panel1;
    /**
     * 包路径：com.xxx.x
     */
    private JTextField packagePath;
    /**
     * 后端项目的路径
     */
    private JTextField backendPath;
    /**
     * domain模块表示
     */
    private JCheckBox domainFlag;
    /**
     * dao模块标示
     */
    private JCheckBox daoFlag;
    /**
     * 生成器按钮
     */
    private JButton generateBtn;
    /**
     * 应用名
     */
    private JTextField appNameField;
    /**
     * 建表语句
     */
    private JTextArea createTablePane;
    /**
     * 生成的日志
     */
    private JTextArea generateLog;
    private JButton parseTableBtn;
    /**
     * 表前缀
     */
    private JTextField tablePre;
    /**
     * 解析标示
     */
    private Boolean parseSuccess = false;
    private String tableName;
    private Project project;

    public JPanel getContent() {
        return panel1;
    }

    public AppConfigForm(Project project) {
        this.project = project;
        init();
    }

    private void init() {
        // 添加数据库的监听器处理
        addTableSqlParser();

        // 添加代码生成器的监听器处理
        addCodeGenListener();

        // 添加输入应用名之后的配置导入
        addAppNameListener();
    }

    public void appendLog(String logStr) {
        generateLog.append(logStr);
    }

    /**
     * 表结构解析
     */
    private void addTableSqlParser() {
        parseTableBtn.addActionListener(e -> parseAndFreshConfig());
    }

    /**
     * 代码生成回调
     */
    private void addCodeGenListener() {
        generateBtn.addActionListener(e -> {
            // 重新解析并刷新数据
            parseAndFreshConfig();

            try {
                createCodeCheck();
            } catch (WallePluginException ignore) {
                return;
            }

            CreateSqlEntityCodeGenerator codeGenerator = new CreateSqlEntityCodeGenerator();
            SqlGeneratorConfig generatorConfig = new SqlGeneratorConfig();
            generatorConfig.setCreateSql(createTablePane.getText());
            generatorConfig.setProjectPath(backendPath.getText());
            generatorConfig.setEntityPath(packagePath.getText() + ".domain");
            generatorConfig.setPreFix(tablePre.getText());
            generatorConfig.setFieldNamingChg(NeoMap.NamingChg.UNDERLINE);
            codeGenerator.setConfig(generatorConfig);

            appendLog("=============== 代码生成【开始】 ===============\n");
            // 生成domain
            if(domainFlag.isSelected()) {
                try {
                    // 生成代码
                    codeGenerator.generate();
                } catch (Throwable t) {
                    appendLog("生成出错\n");
                    appendLog(t.getCause().toString());
                }

                // 保存配置
                CreateTableMetaConfig config = new CreateTableMetaConfig();
                config.setCreateTableStr(generatorConfig.getCreateSql());
                config.setBackendPath(backendPath.getText());
                config.setPackagePath(packagePath.getText());
                config.setAppName(appNameField.getText());
                config.setTablePre(tablePre.getText());
                try {
                    ConfigCacheHelper.save(config);
                } catch (IOException ignore) {}
            }

            // 生成dao
            if (daoFlag.isSelected()) {
                DaoGenerator daoGenerator = new DaoGenerator();
                daoGenerator.configData(backendPath.getText(), packagePath.getText(), tableName, tablePre.getText());
                daoGenerator.generateCode();
            }

            // 刷新文件
            project.getBaseDir().refresh(false,true);
            appendLog("=============== 代码生成【完成】 ===============\n");
        });
    }

    /**
     * 添加应用名的光标移开之后的回调
     */
    public void addAppNameListener() {
        appNameField.addKeyListener(new KeyListener(){

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String appName = appNameField.getText();
                    if (null != appName && !"".equals(appName)) {
                        freshFieldValue(appName);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }

    /**
     * 解析并刷新配置
     */
    private void parseAndFreshConfig() {
        String tableCreateSql = createTablePane.getText();
        CreateTable createTable = null;
        try {
            createTable = (CreateTable) CCJSqlParserUtil.parse(tableCreateSql);
        } catch (JSQLParserException e1) {
            appendLog("表解析失败\n");
            appendLog(e1.getCause().toString());
            appendLog("\n");
            return;
        }

        String tableNameStr = filterColumnDom(createTable.getTable().getName());
        String tablePreStr = filterColumnDom(getTablePreFromTableName(tableNameStr));
        tablePre.setText(tablePreStr);
        tableName = tableNameStr;
        parseSuccess = true;
    }

    /**
     * 失去焦点时候刷新数据
     */
    private void freshFieldValue(String appName) {
        try {
            CreateTableMetaConfig metaConfig = ConfigCacheHelper.getConfig(appName);
            if (null != metaConfig) {
                backendPath.setText(metaConfig.getBackendPath());
                packagePath.setText(metaConfig.getPackagePath());
                tablePre.setText(metaConfig.getTablePre());
                createTablePane.setText(metaConfig.getCreateTableStr());
            }
        } catch (IOException ignore) {
        }
    }

    private void tableSqlParseCheck() {
        String createTablePaneText = createTablePane.getText();
        if (null == createTablePaneText || "".equals(createTablePaneText)) {
            JOptionPane.showMessageDialog(null, "请填写'建表语句'", "警告", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void createCodeCheck() {
        tableSqlParseCheck();

        String backendFilePath = backendPath.getText();
        if (null == backendFilePath || "".equals(backendFilePath)) {
            JOptionPane.showMessageDialog(null, "请填写'后端项目路径'", "警告", JOptionPane.WARNING_MESSAGE);
            throw new WallePluginException();
        }

        String packageFilePath = packagePath.getText();
        if (null == packageFilePath || "".equals(packageFilePath)) {
            JOptionPane.showMessageDialog(null, "请填写'package'", "警告", JOptionPane.WARNING_MESSAGE);
            throw new WallePluginException();
        }

        if (!parseSuccess) {
            JOptionPane.showMessageDialog(null, "尚未解析或者解析失败", "警告", JOptionPane.WARNING_MESSAGE);
            throw new WallePluginException();
        }
    }

    /**
     * 去除符号`和'
     */
    private String filterColumnDom(String name) {
        if (null == name || "".equals(name)) {
            return "";
        }
        name = name.replaceAll("`", "");
        name = name.replaceAll("'", "");
        return name;
    }

    /**
     * 这里采用默认的方式获取表的前缀
     * <p>
     * 如果表中有对应的下划线或者中划线之类的，则将第一个匹配到的字段作为前缀"_"
     */
    private String getTablePreFromTableName(String tableName) {
        if (tableName.contains("_")) {
            return tableName.substring(0, tableName.indexOf("_") + 1);
        }

        if (tableName.contains("-")) {
            return tableName.substring(0, tableName.indexOf("-") + 1);
        }
        return null;
    }
}
