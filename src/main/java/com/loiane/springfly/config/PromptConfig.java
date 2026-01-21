package com.loiane.springfly.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class PromptConfig {

    private static final Logger log = LoggerFactory.getLogger(PromptConfig.class);
    private static final Pattern FRONT_MATTER_PATTERN = Pattern.compile("^---\\s*\\n(.*?)\\n---\\s*\\n", Pattern.DOTALL);
    private static final Pattern VERSION_PATTERN = Pattern.compile("version:\\s*([\\d.]+)");

    @Value("classpath:prompts/${app.prompt.system-prompt:system-prompt-v1.md}")
    private Resource systemPromptResource;

    @Bean
    public SystemPrompt systemPrompt() throws IOException {
        String content = systemPromptResource.getContentAsString(StandardCharsets.UTF_8);
        
        String version = "unknown";
        String promptContent = content;

        // Extract front matter metadata
        Matcher frontMatterMatcher = FRONT_MATTER_PATTERN.matcher(content);
        if (frontMatterMatcher.find()) {
            String frontMatter = frontMatterMatcher.group(1);
            promptContent = content.substring(frontMatterMatcher.end());

            // Extract version from front matter
            Matcher versionMatcher = VERSION_PATTERN.matcher(frontMatter);
            if (versionMatcher.find()) {
                version = versionMatcher.group(1);
            }
        }

        log.info("Loaded system prompt version: {}", version);
        return new SystemPrompt(version, promptContent.trim());
    }

    public record SystemPrompt(String version, String content) {}
}
