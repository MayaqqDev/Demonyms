package dev.mayaqq.demonyms.resources;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public record Demonym(Identifier id, ItemConvertible item, HashMap<Identifier, Float> attributes, TagKey<Item> disallowedItems, TagKey<Item> allowedItems) {

    public static Demonym fromJson(JsonObject json) {
        return new Demonym(
                new Identifier(json.get("id").getAsString()),
                Registries.ITEM.get(new Identifier(json.get("item").getAsString())),
                json.get("attributes").getAsJsonObject().entrySet().stream().collect(HashMap::new, (map, entry) -> map.put(new Identifier(entry.getKey()), entry.getValue().getAsFloat()), HashMap::putAll),
                TagKey.of(Registries.ITEM.getKey(), new Identifier(json.get("disallowedItems").getAsString())),
                TagKey.of(Registries.ITEM.getKey(), new Identifier(json.get("allowedItems").getAsString()))
        );
    }

    public static JsonObject toJson(Demonym demonym) {
        JsonObject json = new JsonObject();
        json.addProperty("id", demonym.id().toString());
        json.addProperty("item", Registries.ITEM.getKey(demonym.item().asItem()).toString());
        JsonObject attributes = new JsonObject();
        demonym.attributes().forEach((id, value) -> attributes.addProperty(id.toString(), value));
        json.add("attributes", attributes);
        json.addProperty("disallowedItems", demonym.disallowedItems().id().toString());
        json.addProperty("allowedItems", demonym.allowedItems().id().toString());
        return json;
    }
}