package net.manifest.journalapp.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class UserUpdateDTO {
    private String name;
    private String email;
    private boolean sentimentAnalysisEnabled;
}
