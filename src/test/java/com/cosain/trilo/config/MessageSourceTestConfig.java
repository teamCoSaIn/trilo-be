package com.cosain.trilo.config;

import com.cosain.trilo.config.property.sourcefactory.YamlPropertySourceFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@TestConfiguration
@ComponentScan(basePackages = "com.cosain.trilo.config.message")
@PropertySources({
        @PropertySource(value = "classpath:exceptions/exception.yml", factory = YamlPropertySourceFactory.class),
        @PropertySource(value = "classpath:exceptions/exception_en.yml", factory = YamlPropertySourceFactory.class)
})
public class MessageSourceTestConfig {

}
