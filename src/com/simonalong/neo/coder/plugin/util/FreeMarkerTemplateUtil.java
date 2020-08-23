package com.simonalong.neo.coder.plugin.util;

import freemarker.cache.NullCacheStorage;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

/**
 * @author shizi
 * @since 2019/12/3 11:48 上午
 */
public class FreeMarkerTemplateUtil {

    private static final Configuration TEMPLATE_CONFIGURATION = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

    static{
        //这里比较重要，用来指定加载模板所在的路径
        TEMPLATE_CONFIGURATION.setDefaultEncoding("UTF-8");
        TEMPLATE_CONFIGURATION.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        TEMPLATE_CONFIGURATION.setCacheStorage(NullCacheStorage.INSTANCE);
    }

    /**
     * 添加模板
     * @param templateName 模板名字
     * @param templateContent 模板内容
     */
    public static void putTemplate(String templateName, String templateContent) {
        // 如果模板加载器没有配置
        if (!TEMPLATE_CONFIGURATION.isTemplateLoaderExplicitlySet()) {
            StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
            stringTemplateLoader.putTemplate(templateName, templateContent);

            TEMPLATE_CONFIGURATION.setTemplateLoader(stringTemplateLoader);
        } else {
            StringTemplateLoader stringTemplateLoader = (StringTemplateLoader) TEMPLATE_CONFIGURATION.getTemplateLoader();
            stringTemplateLoader.putTemplate(templateName, templateContent);
        }
    }

    public static Template getTemplate(String templateName) {
        try {
            return TEMPLATE_CONFIGURATION.getTemplate(templateName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
