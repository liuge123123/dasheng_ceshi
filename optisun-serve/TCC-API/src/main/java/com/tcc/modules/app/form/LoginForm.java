package com.tcc.modules.app.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class LoginForm implements Serializable {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

}
