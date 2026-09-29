package com.easychat.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * OpenAPI 文档配置。
 * 扫描范围由 springdoc 自动推导（主类所在包及其子包），此处无需指定。
 */
@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

    /** 安全方案名，需与 addSecuritySchemes 的 key 保持一致 */
    private static final String SECURITY_SCHEME = "token";

    @Bean
    public OpenAPI easyChatOpenApi() {
        SecurityScheme tokenScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("token");   // 对应 GlobalOperationAspect 第 56 行的 getHeader("token")

        return new OpenAPI()
                .info(new Info()
                        .title("EasyChat API")
                        .version("1.0.0")
                        .description("EasyChat 前后端接口文档"))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME, tokenScheme))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME));
    }

    /**
     * 本项目关闭了Spring Boot 自动配置默认的静态资源映射（spring.web.resources.add-mappings=false），
     * 而 knife4j 的 doc.html 属于静态资源、其自动配置又不注册资源处理器，
     * 故此处精准放开文档所需路径，避免全局放开影响既有 404 语义。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}