package net.manifest.journalapp.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.manifest.journalapp.enums.AccountStatus;

@Data
@NoArgsConstructor
public class UserUpdateDTO {
    private String name;
    private String email;
    private boolean sentimentAnalysis;
}
