package com.example.assist.api;

import com.example.assist.exception.ChatGPTResponseException;
import com.example.assist.helper.StringConverter;
import com.example.assist.model.JobContent;
import com.google.common.collect.ImmutableList;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ChatGPTApi {

    private final Logger logger = LoggerFactory.getLogger(ChatGPTApi.class);

    @Value("${ChatGPT.secretKey}")
    private String secretKey;

    @Value("${ChatGPT.systemPrompt}")
    private String systemPrompt;

    public void rateJobs(final String resume, final List<JobContent> jobs) {
        if (jobs.size() == 0) {
            return;
        }

        final String ratingString = callChatGPTApi("Resume:\n" + resume +
            "\nJobs:\n" + StringConverter.JobContentsToString(jobs));
        fillRatings(ratingString, jobs);
        jobs.forEach(job -> logger.info(job.toString()));
    }

    private String callChatGPTApi(final String userPrompt) {
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt);
        final ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), userPrompt);
        // logger.info(userMessage.getContent());
        final List<ChatMessage> messages = ImmutableList.of(systemMessage, userMessage);
        final OpenAiService service = new OpenAiService(secretKey);
        final ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                                                            .messages(messages)
                                                            .model("gpt-3.5-turbo")
                                                            .temperature(0.7)
                                                            .stream(false)
                                                            .n(1)
                                                            .build();
        final ChatCompletionResult result = service.createChatCompletion(completionRequest);
        // logger.info(result.toString());
        return result.getChoices().get(0).getMessage().getContent();
    }

    private void fillRatings(final String ratingString, final List<JobContent> jobs) {
        final String[] ratings = ratingString.split("\n\n");
        if (ratings.length > jobs.size()) {
            throw new ChatGPTResponseException("ChatGPT response contains more job postings.");
        }
        if (ratings.length < jobs.size()) {
            throw new ChatGPTResponseException("ChatGPT response contains less job postings.");
        }
        int index = 0;
        for (final String rating : ratings) {
            final String[] lines = rating.split("\n");
            final JobContent job = jobs.get(index);
            job.setOverallRating(Integer.parseInt(lines[1].split(":")[1].trim().split("/")[0]));
            job.setExperienceRating(Integer.parseInt(lines[2].split(":")[1].trim().split("/")[0]));
            job.setSkillsRating(Integer.parseInt(lines[3].split(":")[1].trim().split("/")[0]));
            job.setQualificationsRating(Integer.parseInt(lines[4].split(":")[1].trim().split("/")[0]));
            index += 1;
        }
    }
}
