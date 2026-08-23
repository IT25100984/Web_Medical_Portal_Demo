package com.webmedicalportaldemo.controller;

import com.webmedicalportaldemo.model.Feedback;
import com.webmedicalportaldemo.service.FeedbackFileService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class GlobalLayoutAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalLayoutAdvice.class);
    private final FeedbackFileService feedbackFileService;
    public GlobalLayoutAdvice(FeedbackFileService feedbackFileService) {
        this.feedbackFileService = feedbackFileService;
    }

    /**
     * Adds public feedback reviews to every MVC model.
     */
    @ModelAttribute("publicReviews")
    public List<Feedback> addPublicReviews() {
        try {
            List<Feedback> reviews = feedbackFileService.getFeedbackFromTextFile();
            return reviews != null ? reviews : Collections.emptyList();
        } catch (Exception exception) {
            LOGGER.error("Unable to load public reviews from the feedback file.", exception);
            return Collections.emptyList();
        }
    }
}