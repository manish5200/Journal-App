package net.manifest.journalapp.enums;

import lombok.Getter;
    /**
     * Represents the sentiment of a journal entry's text content, as analyzed by the application.
     * This is distinct from the user's self-reported Mood.
     * Each sentiment level has a numerical score to quantify the emotional tone, which is
     * essential for tracking trends and performing analytics.
     */
    @Getter
    public enum Sentiment {
        // Enum constants with their associated analysis score
        VERY_POSITIVE(2),
        POSITIVE(1),
        NEUTRAL(0),
        NEGATIVE(-1),
        VERY_NEGATIVE(-2);

        /**
         * A numerical score representing the sentiment's intensity.
         * A positive score indicates positive sentiment, negative for negative, and zero for neutral.
         * This is used for calculating averages in the WeeklySummary.
         */
        private final int score;

        /**
         * Constructor for the enum constants.
         * @param score The numerical score.
         */
        Sentiment(int score) {
            this.score = score;
        }
}
