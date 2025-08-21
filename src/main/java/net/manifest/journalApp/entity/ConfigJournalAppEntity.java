package net.manifest.journalApp.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "config_journal_app")

@NoArgsConstructor
@Setter
@Getter
public class ConfigJournalAppEntity {

        private String key;

        private String value;
}
