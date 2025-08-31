package net.manifest.journalapp.dto.journal;

import lombok.Data;
import net.manifest.journalapp.enums.Mood;
import net.manifest.journalapp.utils.journalutils.Location;
import net.manifest.journalapp.utils.journalutils.Weather;

/**
 * A DTO for applying partial updates to a JournalEntry using the PATCH method.
 * All fields are optional. A null value for any field means that the field
 * should not be updated.
 */
@Data
public class JournalEntryPatchDTO {
    private String title; // No @NotBlank, can be null
    private String content;
    private Boolean isPublic;
    private Mood mood;
    private Location location;
    private Weather weather;
}
