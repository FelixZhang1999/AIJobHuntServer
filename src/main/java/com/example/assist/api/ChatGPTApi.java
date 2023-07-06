package com.example.assist.api;

import com.example.assist.exception.ChatGPTResponseException;
import com.example.assist.factory.ChatMessageFactory;
import com.example.assist.factory.OpenAiServiceFactory;
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
import org.springframework.stereotype.Component;

/**
 * Call ChatGPT API.
 */
@Component
public class ChatGPTApi {

    private final ChatMessage systemMessage;
    private final Logger logger = LoggerFactory.getLogger(ChatGPTApi.class);
    private final OpenAiService service;
    
    public ChatGPTApi(OpenAiServiceFactory serviceFactory, ChatMessageFactory messageFactory) {
        this.service = serviceFactory.getObject();
        this.systemMessage = messageFactory.getObject();
    }

    /**
     * Rate a list of job postings according to resume.
     * @param resume The resume of the user
     * @param jobs The job postings
     */
    public void rateJobs(final String resume, final List<JobContent> jobs) {
        if (jobs.size() == 0) {
            return;
        }

        final String ratingString = callChatGPTApi("Resume:\n" + resume +
            "\nJobs:\n" + StringConverter.JobContentsToString(jobs));
        fillRatings(ratingString, jobs);
        jobs.forEach(job -> logger.info(job.toString()));
    }

    /**
     * Call ChatGPT API with userPrompt.
     * @param userPrompt The User Prompt in ChatGPT API call.
     */
    private String callChatGPTApi(final String userPrompt) {
        final ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), userPrompt);
        // logger.info(userMessage.getContent());
        final List<ChatMessage> messages = ImmutableList.of(systemMessage, userMessage);
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

    /**
     * Fill in ratings according to ChatGPT output
     * @param ratingString ChatGPT output
     * @param jobs The job postings to rate
     */
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
