package com.owl;

import lombok.Data;
import lombok.SneakyThrows;
import org.ini4j.Ini;

import java.io.File;

@Data
public class Config {

    private Ini ini;

    @SneakyThrows
    public Config() {
        this.ini = new Ini(new File("./config.ini"));
    }

    public int getInt(String header, String key) {
        return Integer.parseInt(ini.get(header ,key));
    }

    public boolean getBoolean(String header, String key) {
        return Boolean.parseBoolean(ini.get(header ,key));
    }

    public String get(String header, String key) {
        return ini.get(header ,key);
    }

}
