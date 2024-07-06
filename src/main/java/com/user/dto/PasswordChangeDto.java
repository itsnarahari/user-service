package com.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeDto {

    @NotBlank(message = "Old password should not be blank")
//    @Size(min = 8, max = 16, message = "Old password should be minimum 8 and max 16 chars")
    private String oldPassword;

    @NotBlank(message = "New password should not be blank")
//    @Size(min = 8, max = 16, message = "New password should be minimum 8 and max 16 chars")
    private String newPassword;

}
