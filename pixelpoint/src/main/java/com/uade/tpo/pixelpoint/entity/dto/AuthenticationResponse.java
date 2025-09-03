package com.uade.tpo.pixelpoint.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthenticationResponse {
    @JsonProperty("access_token")
    private String accessToken;
    private String token;
}
