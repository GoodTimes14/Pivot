package eu.magicmine.pivot.api.configuration;

import eu.magicmine.pivot.api.configuration.section.ConfigurationSection;
import lombok.Getter;

import java.util.*;

@Getter
public class PivotConfiguration {

    private final Map<String,Object> values;
    private final Map<String, ConfigurationSection> sections;

    public PivotConfiguration(Map<String,Object> values) {
        this.values = values;
        sections = new HashMap<>();
        load();
    }

    public void load() {
        for(Map.Entry<String,Object> entry : values.entrySet()) {
            if(entry.getValue() instanceof LinkedHashMap) {
                sections.put(entry.getKey(),new ConfigurationSection(entry.getKey(),(Map<String, Object>) entry.getValue()));
            }
        }
    }


    public ConfigurationSection getSection(String path) {
        if (path.contains(".")) {
            String[] array = path.split("\\.");
            String[] sectionNames = Arrays.copyOfRange(array,1,array.length);
            ConfigurationSection section = sections.get(array[0]);
            if(section == null) {
                throw  new IllegalArgumentException("This is not a configuration section");
            }
            for(String sec : sectionNames) {
                section = section.getNestedSections().get(sec);
            }
            return section;
        } else {
            ConfigurationSection section = sections.get(path);
            if(section == null) {
                throw  new IllegalArgumentException("This is not a configuration section");
            }
            return section;
        }
    }


    public List<ConfigurationSection> getSections(String path) {
        List<ConfigurationSection> toRet = new ArrayList<>();
        if (path.contains(".")) {
            String[] array = path.split("\\.");
            String[] sectionNames = Arrays.copyOfRange(array,1,array.length);
            ConfigurationSection section = sections.get(array[0]);
            if(section == null) {
                throw  new IllegalArgumentException("This is not a configuration section");
            }
            for(String sec : sectionNames) {
                section = section.getNestedSections().get(sec);
            }
            toRet.addAll(section.getNestedSections().values());
            return toRet;
        } else if(path.length() == 0) {
            toRet.addAll(sections.values());
            return toRet;
        } else {
            ConfigurationSection section = sections.get(path);
            if(section == null) {
                throw  new IllegalArgumentException("This is not a configuration section");
            }
            toRet.addAll(section.getNestedSections().values());
            return toRet;
        }
    }

    public void set(String path,Object obj) {
        if(path.contains(".")) {
            String[] array = path.split("\\.");
            String[] sectionNames = Arrays.copyOfRange(array,1,array.length - 1);
            String actualObjPath = array[array.length - 1];
            ConfigurationSection section = sections.get(array[0]);
            for(String sec : sectionNames) {
                section = section.getNestedSections().get(sec);
            }
            if(section.getConfigMap().get(actualObjPath).getClass().isInstance(obj.getClass())) {
                throw new ClassCastException("Path is ok,but the object doesn't correspond with the class you want to cast it with");
            }
            section.getConfigMap().put(actualObjPath,obj);
        } else {
            if(values.get(path).getClass().isInstance(obj.getClass())) {
                throw new ClassCastException("the object doesn't correspond with the class you want to cast it with");
            }
            values.put(path,obj);
        }
    }


    public <T> T get(String path,Class<T> clazz) {
        if(path.contains(".")) {
            String[] array = path.split("\\.");
            String[] sectionNames = Arrays.copyOfRange(array,1,array.length - 1);
            String actualObjPath = array[array.length - 1];
            ConfigurationSection section = sections.get(array[0]);
            for(String sec : sectionNames) {
                section = section.getNestedSections().get(sec);
            }
            if(section == null || !section.getConfigMap().containsKey(actualObjPath)) {
                return null;
            }
            if(section.getConfigMap().get(actualObjPath).getClass().isInstance(clazz)) {
                throw new ClassCastException("Path is ok,but the object doesn't correspond with the class you want to cast it with");
            }
            return (T) section.getConfigMap().get(actualObjPath);
        } else {
            if(!values.containsKey(path)) {
                return null;
            }
            if(values.get(path).getClass().isInstance(clazz)) {
                throw new ClassCastException("the object doesn't correspond with the class you want to cast it with");
            }
            return (T) values.get(path);
        }
    }

    public Object get(String path) {
        if(path.contains(".")) {
            String[] array = path.split("\\.");
            String[] sectionNames = Arrays.copyOfRange(array,1,array.length - 1);
            String actualObjPath = array[array.length - 1];
            ConfigurationSection section = sections.get(array[0]);
            for(String sec : sectionNames) {
                section = section.getNestedSections().get(sec);
            }
            return section.getConfigMap().get(actualObjPath);
        } else {
            return values.get(path);
        }
    }

}
