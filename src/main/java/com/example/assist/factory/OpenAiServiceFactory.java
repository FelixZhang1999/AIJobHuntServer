package com.example.assist.factory;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

/**
 * Inject OpenAIService.
 */
@Component
public class OpenAiServiceFactory implements FactoryBean<OpenAiService> {

    @Value("${ChatGPT.secretKey}")
    private String secretKey;

    @Override
    public OpenAiService getObject() {
        return new OpenAiService(secretKey);
    }

    @Override
    public Class<?> getObjectType() {
        return OpenAiService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
