package com.example.assist.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * The builder of Suggestion Request.
 */
@AllArgsConstructor(onConstructor_ = {@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)})
@Builder
@Data
public class SuggestionRequest {
    private String content;
}
