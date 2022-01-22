package com.ssport.dto.users;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    public String getKty() {
        return kty;
    }

    public String getKid() {
        return kid;
    }

    public String getUse() {
        return use;
    }

    public String getAlg() {
        return alg;
    }

    public String getN() {
        return n;
    }

    public String getE() {
        return e;
    }

    @Override
    public String toString() {
        return "ApplePublicKey{" +
                "kty='" + kty + '\'' +
                ", kid='" + kid + '\'' +
                ", use='" + use + '\'' +
                ", alg='" + alg + '\'' +
                ", n='" + n + '\'' +
                ", e='" + e + '\'' +
                '}';
    }
}