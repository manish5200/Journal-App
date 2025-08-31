package net.manifest.journalapp.dto.journal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.manifest.journalapp.enums.Mood;
import net.manifest.journalapp.enums.Sentiment;
import net.manifest.journalapp.utils.journalutils.Location;
import net.manifest.journalapp.utils.journalutils.RatingStats;
import net.manifest.journalapp.utils.journalutils.Weather;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JournalResponseDTO {
    private String id;
    private String userId;
    private String title;
    private String content;
    private String createdBy;
    private Sentiment sentiment;
    private Mood mood;
    private RatingStats ratingStats;
    private Location location;
    private Weather weather;

}
