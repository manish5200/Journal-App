package net.manifest.journalapp.dto.auth;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {

    @NonNull
    private  String username;
    @NonNull
    private  String password;
    @NonNull
    private String name;
    @NonNull
    private String email;

    private boolean sentimentAnalysis;

    @Override
    public String toString() {
        return "AdminDTO{" +
                "userName='" + username + '\'' +
                "Name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", sentimentAnalysis=" + sentimentAnalysis +
                '}';
    }
}
