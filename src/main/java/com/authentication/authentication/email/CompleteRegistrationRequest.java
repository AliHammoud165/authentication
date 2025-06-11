package com.authentication.authentication.email;

import com.authentication.authentication.auth.RegisterRequest;

public class CompleteRegistrationRequest extends RegisterRequest {
    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
