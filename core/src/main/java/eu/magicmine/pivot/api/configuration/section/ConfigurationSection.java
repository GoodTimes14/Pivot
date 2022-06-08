package eu.magicmine.pivot.api.configuration.section;

import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class ConfigurationSection {

    private String name;
    private Map<String,Object> configMap;
    private LinkedHashMap<String,ConfigurationSection> nestedSections;

    public ConfigurationSection(String name,Map<String,Object> configMap) {
        this.name = name;
        this.configMap = configMap;
        nestedSections = new LinkedHashMap<>();
        load();
    }


    public void load() {
        for(Map.Entry<String,Object> entry : configMap.entrySet()) {
            if(entry.getValue() instanceof LinkedHashMap) {
                nestedSections.put(entry.getKey(),new ConfigurationSection(entry.getKey(),(Map<String, Object>) entry.getValue()));
            }
        }
    }

    public <T> T get(String path,Class<T> clazz) {
        if(path.contains(".")) {
            String[] array = path.split("\\.");
            String[] sectionNames = Arrays.copyOfRange(array,1,array.length - 1);
            String actualObjPath = array[array.length - 1];
            ConfigurationSection section = nestedSections.get(array[0]);
            for(String sec : sectionNames) {
                section = section.getNestedSections().get(sec);
            }
            if(section.getConfigMap().get(actualObjPath).getClass().isInstance(clazz)) {
                throw new ClassCastException("Path is ok,but the object doesn't correspond with the class you want to cast it with");
            }
            return (T) section.getConfigMap().get(actualObjPath);
        } else {
            if(configMap.get(path).getClass().isInstance(clazz)) {
                throw new ClassCastException("the object doesn't correspond with the class you want to cast it with");
            }
            return (T) configMap.get(path);
        }
    }


}
