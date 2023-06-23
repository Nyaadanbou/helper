package me.lucko.helper.gson.typeadapters.immutable.common;

import com.google.gson.JsonParseException;

public class ImmutableDeserializerException extends JsonParseException {

    public ImmutableDeserializerException(Exception e) {
        super("Error deserializing immutable collection", e);
    }
}
