package dev.mayaqq.demonyms.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.mayaqq.demonyms.Demonyms;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class DemonymProcessor {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static HashMap<Identifier, Demonym> DEMONYMS = new HashMap<>();

    public static void register() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Demonyms.id("demonyms");
            }

            @Override
            public void reload(ResourceManager manager) {
                manager.findResources("demonyms", path -> path.toString().endsWith(".json")).forEach((id, resource) -> {
                    try {
                        JsonObject json = GSON.fromJson(resource.getReader(), JsonObject.class);
                        DEMONYMS.put(new Identifier(json.get("id").getAsString()), Demonym.fromJson(json));
                    } catch (Exception e) {
                        Demonyms.LOGGER.error("Failed to load block squish amount from " + id.toString() + " as " + e.getMessage());
                    }
                });
            }
        });
    }
}
