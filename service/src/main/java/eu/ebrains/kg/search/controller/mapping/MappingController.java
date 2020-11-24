package eu.ebrains.kg.search.controller.mapping;

import eu.ebrains.kg.search.controller.Constants;
import eu.ebrains.kg.search.model.target.elasticsearch.ElasticSearchInfo;
import eu.ebrains.kg.search.model.target.elasticsearch.instances.commons.Children;
import eu.ebrains.kg.search.utils.MetaModelUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@Component
public class MappingController {

    private static final String SEARCH_UI_NAMESPACE = "https://schema.hbp.eu/searchUi/";
    private static final String GRAPHQUERY_NAMESPACE = "https://schema.hbp.eu/graphQuery/";


    private final MetaModelUtils utils;

    public MappingController(MetaModelUtils utils) {
        this.utils = utils;
    }

    public Map<String, Object> generateMapping() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> mappings = new HashMap<>();
        result.put("mappings", mappings);
        Constants.TARGET_MODELS_ORDER.forEach(targetModel -> {
            mappings.put(utils.getNameForClass(targetModel), generateMapping(targetModel));
        });
        return result;
    }

    public Map<String, Object> generateMapping(Class<?> clazz) {
        Map<String, Object> mapping = new LinkedHashMap<>();
        Map<String, Object> all = new HashMap<>();
        all.put("enabled", true);
        mapping.put("_all", all);
        Map<String, Object> properties = new LinkedHashMap<>();
        Map<String, Object> timestamp = new LinkedHashMap<>();
        mapping.put("properties", properties);
        timestamp.put("type", "date");
        properties.put("@timestamp", timestamp);
        properties.putAll(handleType(clazz));
        return mapping;
    }

    private Map<String, Object> handleType(Type type) {
        Map<String, Object> properties = new LinkedHashMap<>();
        List<MetaModelUtils.FieldWithGenericTypeInfo> allFields = utils.getAllFields(type);
        allFields.sort(Comparator.comparing(f -> utils.getPropertyName(f.getField())));
        allFields.forEach(field -> {
            Map<String, Object> fieldDefinition = handleField(field);
            if (fieldDefinition != null) {
                properties.put(utils.getPropertyName(field.getField()), fieldDefinition);
            }
        });
        return properties;
    }

    private Map<String, Object> handleField(MetaModelUtils.FieldWithGenericTypeInfo field) {
        try {
            ElasticSearchInfo esInfo = field.getField().getAnnotation(ElasticSearchInfo.class);
            if (esInfo == null || esInfo.mapping()) {
                Type topTypeToHandle = field.getGenericType() != null ? field.getGenericType() : getTopTypeToHandle(field.getField().getGenericType());
                Map<String, Object> fieldDefinition = new HashMap<>();

                if(topTypeToHandle instanceof ParameterizedType && ((ParameterizedType)topTypeToHandle).getRawType() == Children.class){
                    Map<String, Object> otherType = handleType(topTypeToHandle);
                    //TODO check if nested shouldn't be defined one level further up
                    ((Map<String, Object>)otherType.get("children")).put("type", "nested");
                    fieldDefinition.put("properties", otherType);

                    //TODO check why we need this "artificial" value mapping
                    Map<String,  Object> value= new LinkedHashMap<>();
                    otherType.put("value", value);
                    Map<String,  Object> fields = new LinkedHashMap<>();
                    value.put("fields", fields);
                    value.put("type", "text");
                    Map<String,  Object> keyword= new LinkedHashMap<>();
                    fields.put("keyword", keyword);
                    keyword.put("type","keyword");

                }
                else if (topTypeToHandle == String.class) {
                    fieldDefinition.put("type", "text");
                    Map<String, Object> fields = new HashMap<>();
                    fieldDefinition.put("fields", fields);
                    Map<String, Object> keyword = new LinkedHashMap<>();
                    fields.put("keyword", keyword);
                    keyword.put("type", "keyword");
                    if (esInfo != null && esInfo.ignoreAbove() > 0) {
                        keyword.put("ignore_above", esInfo.ignoreAbove());
                    }
                } else if (topTypeToHandle == Date.class) {
                    fieldDefinition.put("type", "date");
                } else if (topTypeToHandle == Boolean.class) {
                    fieldDefinition.put("type", "boolean");
                } else {
                    Map<String, Object> otherType = handleType(topTypeToHandle);
                    fieldDefinition.put("properties", otherType);
                }
                return fieldDefinition;
            } else {
                return null;
            }
        } catch (
                ClassNotFoundException e) {
            throw new RuntimeException(String.format("Was not able to map field %s in type %s", field.getField().getName(), field.getField().getDeclaringClass().getSimpleName()), e);
        }

    }


    private Type getTopTypeToHandle(Type type) throws ClassNotFoundException {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = ((ParameterizedType) type);
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            if (actualTypeArguments.length > 0) {
                Type typeArgument = actualTypeArguments[0];
                if (Collection.class.isAssignableFrom(Class.forName(parameterizedType.getRawType().getTypeName()))) {
                    return getTopTypeToHandle(typeArgument);
                } else {
                    return parameterizedType;
                }
            }
        }
        return type;

    }


}
