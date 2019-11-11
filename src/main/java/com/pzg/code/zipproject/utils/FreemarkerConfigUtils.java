package com.pzg.code.zipproject.utils;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

/**
 * Freemarker模板工具类
 */
public class FreemarkerConfigUtils {

    public static final int TYPE_HTTP = 1;
    public static final int TYPE_HTTPS = 2;
    public static final int TYPE_WEBSERVICE = 3;
    public static final int TYPE_DEMO = 4;

    private static Configuration configuration;

    public static synchronized Configuration getInstance() throws FileNotFoundException {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "template/";
        if (null == configuration) {
            configuration = new Configuration(Configuration.VERSION_2_3_0);
            try {
                TemplateLoader loader = new FileTemplateLoader(new File(path));
                configuration.setTemplateLoader(loader);
            } catch (IOException e) {
                e.printStackTrace();
            }
            configuration.setEncoding(Locale.CHINA, "UTF-8");
        }
        return configuration;
    }
}

