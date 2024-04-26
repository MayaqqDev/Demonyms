package dev.mayaqq.demonyms.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public record Demonym(Identifier id, ItemConvertible item, @Nullable HashMap<Identifier, Float> attributes, @Nullable TagKey<Item> disallowedItems) {

    public static final Codec<Demonym> CODEC = Codec.PASSTHROUGH.comapFlatMap(Demonym::decodeCodec, Demonym::encodeCodec);

    public static Demonym fromJson(JsonObject json) {
        TagKey<Item> disallowedItems = null;
        HashMap<Identifier, Float> attributes = null;
        if (json.get("disallowedItems") != null) {
            disallowedItems = TagKey.of(Registries.ITEM.getKey(), new Identifier(json.get("disallowedItems").getAsString()));
        }
        if (json.get("attributes") != null) {
            attributes = json.get("attributes").getAsJsonObject().entrySet().stream().collect(HashMap::new, (map, entry) -> map.put(new Identifier(entry.getKey()), entry.getValue().getAsFloat()), HashMap::putAll);
        }
        return new Demonym(
                new Identifier(json.get("id").getAsString()),
                Registries.ITEM.get(new Identifier(json.get("item").getAsString())),
                attributes,
                disallowedItems
        );
    }

    public String name() {
        return id.getPath();
    }

    public static JsonObject toJson(Demonym demonym) {
        JsonObject json = new JsonObject();
        json.addProperty("id", demonym.id().toString());
        json.addProperty("item", Registries.ITEM.getKey(demonym.item().asItem()).toString());
        if (demonym.attributes != null) {
            JsonObject attributes = new JsonObject();
            demonym.attributes().forEach((id, value) -> attributes.addProperty(id.toString(), value));
            json.add("attributes", attributes);
        }
        if (demonym.disallowedItems != null) {
            json.addProperty("disallowedItems", demonym.disallowedItems().id().toString());
        }
        return json;
    }

    private static DataResult<Demonym> decodeCodec(Dynamic<?> dynamic) {
        Object object = dynamic.convert(JsonOps.INSTANCE).getValue();
        if (object instanceof JsonElement jsonElement) {
            return DataResult.success(fromJson(jsonElement.getAsJsonObject()));
        }
        return DataResult.error(() -> "Value was not an instance of JsonElement");
    }

    private static Dynamic<JsonElement> encodeCodec(Demonym demonym) {
        return new Dynamic<>(JsonOps.INSTANCE, toJson(demonym)).convert(JsonOps.COMPRESSED);
    }
}