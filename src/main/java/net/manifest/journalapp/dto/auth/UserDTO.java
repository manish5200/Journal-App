package net.manifest.journalapp.dto.auth;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDTO {

    @NonNull
    private  String username;
    @NonNull
    private  String password;
    @NonNull
    private String name;
    @NonNull
    private String email;

    private boolean sentimentAnalysis;
}
