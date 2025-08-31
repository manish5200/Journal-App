package net.manifest.journalapp.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    @NotNull
    @NotBlank(message = "Username is required")
    String userName;

    @NotNull
    @NotBlank(message = "Username is required")
    String password;

}
