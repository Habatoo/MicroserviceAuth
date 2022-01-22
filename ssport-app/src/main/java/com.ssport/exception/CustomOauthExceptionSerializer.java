package com.ssport.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Collections;

public class CustomOauthExceptionSerializer extends StdSerializer<CustomOauthException> {

    public CustomOauthExceptionSerializer() {
        super(CustomOauthException.class);
    }

    @Override
    public void serialize(CustomOauthException value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        SportAppErrorResponse oauthErrorResponse = new SportAppErrorResponse();
        oauthErrorResponse.setCode(value.getCode().getCode());
        oauthErrorResponse.setDescription(value.getMessage());

        jsonGenerator.writeObjectField("errors", Collections.singletonList(oauthErrorResponse));
        jsonGenerator.writeEndObject();
    }
}