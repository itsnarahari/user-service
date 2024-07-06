package com.user.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class ValidationException extends RuntimeException {

    private List<MixedError> errors;

    public ValidationException(List<MixedError> errors){
        this.errors = errors;
    }

    public ValidationException(MixedError... error){
        this.errors = Arrays.asList(error);
    }
    public ValidationException(String... messages){
        this.errors = Arrays.stream(messages).map(s -> new MixedError(s, null))
                .collect(Collectors.toList());
    }
}
