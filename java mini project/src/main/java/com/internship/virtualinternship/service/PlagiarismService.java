package com.internship.virtualinternship.service;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;

/**
 * A service dedicated to plagiarism detection.
 * This class provides methods to compare text submissions and calculate similarity scores.
 */
@Service
public class PlagiarismService {

    // Jaro-Winkler is a good choice for comparing short strings of text
    // to find similarities, like in code or written answers.
    private final JaroWinklerSimilarity similarityAlgorithm = new JaroWinklerSimilarity();

    /**
     * Calculates the similarity between two text submissions.
     * The result is a score between 0.0 (completely different) and 1.0 (identical).
     *
     * @param text1 The first submission's content.
     * @param text2 The second submission's content.
     * @return A similarity score as a double.
     */
    public double calculateSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null || text1.isEmpty() || text2.isEmpty()) {
            return 0.0; // No similarity if either text is empty
        }
        return similarityAlgorithm.apply(text1, text2);
    }
}

