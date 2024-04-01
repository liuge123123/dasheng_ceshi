package com.tcc.modules.app.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class RegisterForm implements Serializable {

    @NotBlank
    private String password;

    @NotBlank
    private String mobile;

    @NotBlank
    private String inviteCode;

    @NotBlank
    private String mobileArea;

//    @NotBlank
//    private String otp;

}
