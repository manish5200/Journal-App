package net.manifest.journalapp.dto.journal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.manifest.journalapp.enums.Mood;
import net.manifest.journalapp.utils.journalutils.Location;
import net.manifest.journalapp.utils.journalutils.Weather;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JournalEntryDTO {

    @NotBlank(message = "Title must not be blank")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @Size(max = 50000, message = "Content must not exceed 50000 characters")
    private String content; // Content is optional

    /**
     * Controls the visibility of the entry.
     * Defaults to 'false' (private) to ensure user privacy by default.
     * The user must explicitly choose to make an entry public.
     */
    private Boolean isPublic = false;


    //The user's self-reported mood. This is optional.
    private Mood mood;

    /**
     * NOTE: 'sentiment' has been removed from this DTO.
     * Sentiment is calculated by the backend based on the content;
     * it should never be provided by the user upon creation.
     */


    //Optional location data for the journal entry.
    private Location location;


    // Optional weather data for the journal entry.

    private Weather weather;
}
