package org.aub.payzenapi.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"org.aub.payzenapi.model.mapper"})
public class MapperConfig {
}
