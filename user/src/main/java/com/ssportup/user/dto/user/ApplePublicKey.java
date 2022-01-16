package com.ssportup.user.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class ApplePublicKey {

    private final String kty;
    private final String kid;
    private final String use;
    private final String alg;
    private final String n;
    private final String e;

    public ApplePublicKey(@JsonProperty("kty") String kty,
                          @JsonProperty("kid") String kid,
                          @JsonProperty("use") String use,
                          @JsonProperty("alg") String alg,
                          @JsonProperty("n") String n,
                          @JsonProperty("e") String e) {
        this.kty = kty;
        this.kid = kid;
        this.use = use;
        this.alg = alg;
        this.n = n;
        this.e = e;
    }

}