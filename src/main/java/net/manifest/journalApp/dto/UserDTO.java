package net.manifest.journalApp.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDTO {

    @NonNull
    private  String userName;
    @NonNull
    private String email;
    private boolean sentimentAnalysis;
    @NonNull
    private  String password;
}
