package common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.Reader;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by MacBook on 1/23/19.
 */
public class JsonUtils
{
    private static final JsonParser parser = new JsonParser();
    private static final Gson gson             = new GsonBuilder().create();
    private static final Gson gsonStatic       = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();

    public static <T> T fromFile(String file, Class<T> classOfT, boolean useStatic) throws Exception
    {
        return fromJson(Files.newBufferedReader(Paths.get(file), StandardCharsets.UTF_8), classOfT, useStatic);
    }

    // -------- From Json ---------
    public static <T> T fromJson(String json, Class<T> classOfT)
    {
        return fromJson(json, classOfT, false);
    }

    public static <T> T fromJson(String json, Class<T> classOfT, boolean useStatic)
    {
        return (useStatic ? gsonStatic : gson).fromJson(json, classOfT);
    }

    public static <T> T fromJson(Reader json, Class<T> classOfT, boolean useStatic)
    {
        return (useStatic ? gsonStatic : gson).fromJson(json, classOfT);
    }

    // -------- To Json -----------
    public static String toJson(Object src)
    {
        return toJson(src, false);
    }

    public static String toJson(Object src, boolean useStatic)
    {
        return (useStatic ? gsonStatic : gson).toJson(src);
    }

    public static String toJson(Object src, Type typeOfT)
    {
        return toJson(src, typeOfT, false);
    }

    public static String toJson(Object src, Type typeOfT, boolean useStatic)
    {
        return (useStatic ? gsonStatic : gson).toJson(src, typeOfT);
    }

    // -------------- File ------------
    public static JsonElement parseFile(String file) throws Exception
    {
        return parse(Files.newBufferedReader(Paths.get(file), StandardCharsets.UTF_8));
    }

    public static JsonElement parse(Reader json)
    {
        return parser.parse(json);
    }

    public static JsonElement parse(String json)
    {
        return parser.parse(json);
    }
}
