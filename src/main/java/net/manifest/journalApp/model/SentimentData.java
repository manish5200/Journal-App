package net.manifest.journalApp.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class SentimentData {

    private String email;
    private String userName;
    private String sentiment;
}
