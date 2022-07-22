package eu.magicmine.pivot.bungee.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigUtil {

    public static void saveDefaultConfig(File dataFolder,String name,ClassLoader loader) {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();

        }

        File file = new File(dataFolder,name);

        if (!file.exists()) {
            InputStream inputStream = loader.getResourceAsStream(name);

            if(inputStream == null) return;

            try {

                Files.copy(inputStream,file.toPath());
                inputStream.close();

            } catch (IOException e) {

                e.printStackTrace();

            }
        }
    }


}
