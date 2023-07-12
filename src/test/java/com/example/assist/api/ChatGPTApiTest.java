package com.example.assist.api;

import com.example.assist.exception.ChatGPTResponseException;
import com.example.assist.factory.ChatMessageFactory;
import com.example.assist.factory.OpenAiServiceFactory;
import com.example.assist.model.JobContent;
import com.google.common.collect.ImmutableList;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ChatGPTApiTest {

    @Mock
    private ChatMessageFactory messageFactory;

    @Mock
    private OpenAiService openAiService;

    @Mock
    private OpenAiServiceFactory serviceFactory;
    
    private ChatGPTApi chatGPTApi;

    private final ChatCompletionChoice choice = new ChatCompletionChoice();
    private final ChatCompletionResult result = new ChatCompletionResult();
    private final ChatMessage chatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "Message");
    private final String RESUME = "resume";
    private final String MESSAGE = "Job Posting #1\n" + //
            "Overall rating: 4/5\n" + //
            "Experience: 5/5\n" + //
            "Technical skills: 3/5\n" + //
            "Qualifications: 4/5\n" + //
            "\n" + //
            "Job Posting #2\n" + //
            "Overall rating: 3/5\n" + //
            "Experience: 2/5\n" + //
            "Technical skills: 4/5\n" + //
            "Qualifications: 3/5";


    private List<JobContent> jobs;

    @BeforeEach
    void init() {
        choice.setMessage(new ChatMessage(ChatMessageRole.SYSTEM.value(), MESSAGE));
        choice.setIndex(0);;
        result.setChoices(ImmutableList.of(choice));
        when(serviceFactory.getObject()).thenReturn(openAiService);
        when(openAiService.createChatCompletion(any())).thenReturn(result);
        when(messageFactory.getObject()).thenReturn(chatMessage);
        chatGPTApi = new ChatGPTApi(serviceFactory, messageFactory);
        jobs = ImmutableList.of(JobContent.builder().build(),
                        JobContent.builder().build());
    }

    @Test
    void test_rateJobs_noJobs_success() {
        chatGPTApi.rateJobs(RESUME, ImmutableList.of());
        verify(openAiService, times(0)).createChatCompletion(any());
    }

    @Test
    void test_rateJobs_someJobs_success() {
        chatGPTApi.rateJobs(RESUME, jobs);
        verify(openAiService, times(1)).createChatCompletion(any());
        assertEquals(jobs.get(0).getOverallRating(), 4);
        assertEquals(jobs.get(0).getExperienceRating(), 5);
        assertEquals(jobs.get(0).getSkillsRating(), 3);
        assertEquals(jobs.get(0).getQualificationsRating(), 4);

        assertEquals(jobs.get(1).getOverallRating(), 3);
        assertEquals(jobs.get(1).getExperienceRating(), 2);
        assertEquals(jobs.get(1).getSkillsRating(), 4);
        assertEquals(jobs.get(1).getQualificationsRating(), 3);
    }

    @Test
    void test_rateJobs_chatGptReturnsMoreOrLessJobs_throw() {
        jobs = ImmutableList.of(JobContent.builder().build());
        assertThrows(ChatGPTResponseException.class, () -> {
            chatGPTApi.rateJobs(RESUME, jobs);
        });
        
        jobs = ImmutableList.of(JobContent.builder().build(),
                                JobContent.builder().build(),
                                JobContent.builder().build());
        assertThrows(ChatGPTResponseException.class, () -> {
            chatGPTApi.rateJobs(RESUME, jobs);
        });
    }
}
