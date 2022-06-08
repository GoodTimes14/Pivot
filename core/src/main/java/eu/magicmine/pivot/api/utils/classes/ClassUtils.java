package eu.magicmine.pivot.api.utils.classes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ClassUtils  {


    public static File getFileOrDefault(ClassLoader loader,String name,File directory) {
        File file = new File(directory,name);
        if(!file.exists()) {
            InputStream inputStream = loader.getResourceAsStream(name);
            try {
                Files.copy(inputStream, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
