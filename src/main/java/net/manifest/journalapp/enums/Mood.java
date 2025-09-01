package net.manifest.journalapp.enums;

import lombok.Getter;

@Getter
public enum Mood {

    // --- High Positive & Spiritual ---
    SPIRITUAL("🙏🏼", 10),
    DEVOTIONAL("🙇🏼", 10),
    BLISSFUL("😇", 10),
    GRATEFUL("🥰", 9),
    INSPIRED("💡", 9),

    // --- Standard Positive ---
    AWESOME("😄", 8),
    EXCITED("🤩", 8),
    HAPPY("😊", 7),
    PROUD("😌", 7),
    MOTIVATED("🚀", 7),

    // --- Calm & Neutral Positive ---
    GOOD("🙂", 6),
    CALM("🧘🏼", 6),
    RELAXED("😌", 6),
    FOCUSED("🎯", 5),

    // --- Neutral ---
    MEH("😐", 4),
    THOUGHTFUL("🤔", 4),
    TIRED("🥱", 4),

    // --- Standard Negative ---
    BAD("😕", 3),
    SAD("😢", 3),
    FRUSTRATED("😤", 3),
    WORRIED("😟", 3),

    // --- High Negative ---
    AWFUL("😭", 2),
    ANXIOUS("😬", 2),
    ANGRY("😡", 2),
    STRESSED("😫", 1),
    OVERWHELMED("🤯", 1);

    /**
     * The emoji character to visually represent the mood in the user interface.
     */
    private final String emoji;

    /**
     * A numerical score (1-10) to quantify the mood, useful for more detailed analytics.
     */
    private final int score;

    Mood(String emoji, int score) {
        this.emoji = emoji;
        this.score = score;
    }
}

