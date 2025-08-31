package net.manifest.journalapp.enums;

import lombok.Getter;

@Getter
public enum Mood {

    SPIRITUAL("🙏🏼",7),
    DEVOTIONAL("🙇🏼",6),
    AWESOME("😄", 5),
    GOOD("😊", 4),
    MEH("😐", 3),
    BAD("😕", 2),
    AWFUL("😢", 1);

    //The emoji character to visually represent the mood in the user interface.

    private final String emoji;


    //A simple numerical score (1-5) to quantify the mood, useful for analytics.
    private final int score;

    /**
     * Constructor for the enum constants.
     * @param emoji The emoji string.
     * @param score The numerical score.
     */
    Mood(String emoji, int score) {
        this.emoji = emoji;
        this.score = score;
    }

}
