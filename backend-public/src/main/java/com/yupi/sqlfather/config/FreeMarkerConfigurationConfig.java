package com.yupi.sqlfather.config;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.io.IOException;
import org.springframework.context.annotation.Bean;

/**
 * FreeMarker 模板配置
 *
 * @author hanburger
 */
@org.springframework.context.annotation.Configuration
public class FreeMarkerConfigurationConfig {

    @Bean
    public Configuration configuration() throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
        // 模版加载目录
        cfg.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
        // 模版默认编码
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        //设置日志模版异常
        cfg.setLogTemplateExceptions(false);
        //设置包裹未检查异常
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        return cfg;
    }
}
