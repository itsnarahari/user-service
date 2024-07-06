package com.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class GenericRequestDto {

    private String username;
    private CommunicationTypes communicationTypes;
    private String forWhat;

}
