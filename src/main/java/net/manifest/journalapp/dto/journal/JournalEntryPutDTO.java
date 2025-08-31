package net.manifest.journalapp.dto.journal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.manifest.journalapp.enums.Sentiment;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JournalEntryPutDTO {
    private String title;      // optional
    private String content;    // optional
    private Sentiment sentiment; // optional
}
