package dev.mayaqq.demonyms.resources;

import com.google.gson.JsonObject;

public record Demonym(Float scale) {

    public static Demonym fromJson(JsonObject json) {
        return new Demonym(json.get("scale").getAsFloat());
    }
}