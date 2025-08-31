package net.manifest.journalapp.dto.auth;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO{

    @NonNull
    private  String username;
    @NonNull
    private  String password;
    @NonNull
    private String name;
    @NonNull
    private String email;
    private boolean sentimentAnalysis=true;
}
