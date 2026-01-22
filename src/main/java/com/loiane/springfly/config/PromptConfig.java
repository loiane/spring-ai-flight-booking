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

    @Value("classpath:prompts/${app.prompt.supervisor-agent:supervisor-agent-v1.md}")
    private Resource supervisorPromptResource;

    @Value("classpath:prompts/${app.prompt.booking-agent:booking-agent-v1.md}")
    private Resource bookingPromptResource;

    @Value("classpath:prompts/${app.prompt.payment-agent:payment-agent-v1.md}")
    private Resource paymentPromptResource;

    @Value("classpath:prompts/${app.prompt.escalation-agent:escalation-agent-v1.md}")
    private Resource escalationPromptResource;

    @Bean
    public AgentPrompt supervisorAgentPrompt() throws IOException {
        return loadPrompt(supervisorPromptResource, "supervisor-agent");
    }

    @Bean
    public AgentPrompt bookingAgentPrompt() throws IOException {
        return loadPrompt(bookingPromptResource, "booking-agent");
    }

    @Bean
    public AgentPrompt paymentAgentPrompt() throws IOException {
        return loadPrompt(paymentPromptResource, "payment-agent");
    }

    @Bean
    public AgentPrompt escalationAgentPrompt() throws IOException {
        return loadPrompt(escalationPromptResource, "escalation-agent");
    }

    private AgentPrompt loadPrompt(Resource resource, String agentName) throws IOException {
        String content = resource.getContentAsString(StandardCharsets.UTF_8);
        
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

        log.info("Loaded {} prompt version: {}", agentName, version);
        return new AgentPrompt(agentName, version, promptContent.trim());
    }

    public record AgentPrompt(String name, String version, String content) {}
}
