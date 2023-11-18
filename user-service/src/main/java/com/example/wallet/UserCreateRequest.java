package com.example.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor

public class UserCreateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private String dob;

    private String country;

    @NotBlank
    private UserIdentifier userIdentifier;

    @NotBlank
    private String identifierValue;

    public User toUser() {
        return User.builder()
                .name(this.name)
                .phoneNumber(this.phoneNumber)
                .email(this.email)
                .country(this.country)
                .dob(this.dob)
                .password(this.password)
                .userIdentifier(this.userIdentifier)
                .identifierValue(this.identifierValue)
                .build();
    }
}
