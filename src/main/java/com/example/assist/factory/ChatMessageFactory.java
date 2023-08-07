package com.example.assist.factory;

import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Inject System ChatMessage.
 */
@Component
public class ChatMessageFactory implements FactoryBean<ChatMessage> {

    @Value("${ChatGPT.systemPrompt}")
    private String systemPrompt;

    @Override
    public ChatMessage getObject() {
        return new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt);
    }

    @Override
    public Class<?> getObjectType() {
        return ChatMessage.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
