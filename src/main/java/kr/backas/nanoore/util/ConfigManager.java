package kr.backas.nanoore.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class ConfigManager<T extends ConfigurationSerializable> {

    private final Class<T> classType;
    private final YamlConfiguration yaml;
    private final File file;

    public ConfigManager(File file, Class<T> classType) {
        this.classType = classType;
        this.yaml = YamlConfiguration.loadConfiguration(file);
        this.file = file;
    }

    public T get(String path) {
        return this.yaml.getSerializable(path, classType);
    }

    public void set(String path, T data) {
        this.yaml.set(path, data);
        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getKeys() {
        return yaml.getKeys(false);
    }
}
