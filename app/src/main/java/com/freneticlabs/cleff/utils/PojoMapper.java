package com.freneticlabs.cleff.utils;

/**
 * Created by jcmanzo on 3/19/15.
 * Code taken from http://wiki.fasterxml.com/JacksonSampleSimplePojoMapper
 */

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;

public class PojoMapper {

    private static ObjectMapper m = new ObjectMapper();
    private static JsonFactory jf = new JsonFactory();

    public static <T> ArrayList<T> fromJson(InputStream inputStream, TypeReference valueTypeRef)
            throws JsonParseException, IOException {
        return m.readValue(inputStream, valueTypeRef);
    }

    public static void toJson(Object pojo, Writer writer, boolean prettyPrint)
            throws JsonMappingException, JsonGenerationException, IOException {
        JsonGenerator jg = jf.createGenerator(writer);
        if (prettyPrint) {
            jg.useDefaultPrettyPrinter();
        }
        m.writeValue(jg, pojo);
    }
}
