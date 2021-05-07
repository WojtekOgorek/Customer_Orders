package ogorek.wojciech.persistence.converter;

import com.google.gson.Gson;
import ogorek.wojciech.persistence.exception.AppException;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Optional;


public abstract class JsonConverter<T> {

    private final String jsonFilename;
    private final Gson gson = new Gson()
            .newBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
            .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
            .setPrettyPrinting()
            .create();
    private final Type type = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    public JsonConverter(String jsonFilename){this.jsonFilename = jsonFilename;}



    public void toJson(final T element ){
        try(FileWriter fileWriter = new FileWriter(jsonFilename)){
            if(element == null){
                throw new NullPointerException("to Json element is null");
            }
            gson.toJson(element, fileWriter);
        }catch (Exception e){
            throw new AppException("to Json exception" + e.getMessage());
        }
    }

    public Optional<T> fromJson(){
        try(FileReader fileReader = new FileReader(jsonFilename)){
            return Optional.of(gson.fromJson(fileReader, type));
        }catch (Exception e){
            throw new AppException("from Json exception" + e.getMessage());
        }
    }
}
