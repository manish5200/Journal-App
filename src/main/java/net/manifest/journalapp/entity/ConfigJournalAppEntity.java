package net.manifest.journalapp.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;


//FOR WEATHER API

@Document(collection = "config_journal_app")
@NoArgsConstructor
@Setter
@Getter
public class ConfigJournalAppEntity {
        private String key;
        private String value;
}
