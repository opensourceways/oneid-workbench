package com.oneid.application.personalapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckTokenDTO {

    /**
     * 需要校验的token
     */
    @NotNull
    private String token;

    /**
     * 请求访问的url
     */
    @NotNull
    private String url;
}
