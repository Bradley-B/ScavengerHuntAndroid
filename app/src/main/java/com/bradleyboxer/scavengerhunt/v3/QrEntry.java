package com.bradleyboxer.scavengerhunt.v3;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.Serializable;
import java.util.UUID;

public class QrEntry implements Serializable {

    public enum Type {SCAVENGER_HUNT, CLUE}

    private final Type type;
    private final UUID uuid;

    public static final Gson gson = new Gson();

    public QrEntry(Type type, UUID uuid) {
        this.type = type;
        this.uuid = uuid;
    }

    public String serialize() {
        return gson.toJson(this);
    }

    public static QrEntry deserialize(String json) throws JsonSyntaxException {
        return gson.fromJson(json, QrEntry.class);
    }

    public Type getType() {
        return type;
    }

    public UUID getUuid() {
        return uuid;
    }
}
