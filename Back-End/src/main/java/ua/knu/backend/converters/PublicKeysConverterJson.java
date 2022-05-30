package ua.knu.backend.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import ua.knu.backend.dto.ECPointDTO;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Converter
public class PublicKeysConverterJson implements AttributeConverter<List<ECPointDTO>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<ECPointDTO> meta) {
        try {
            String objectStr = objectsToJson(meta);
            log.info(objectStr);
            return objectStr;
        } catch (Exception ex) {
            return null;
            // or throw an error
        }
    }

    @Override
    public List<ECPointDTO> convertToEntityAttribute(String dbData) {
        try {
            List<ECPointDTO> list = jsonToObjects(dbData);
            log.info(list.toString());
            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    private static JSONObject objectToJson(ECPointDTO data) {
        try {
            ECPointDTO point = new ECPointDTO(data.getX(), data.getY());
            return new JSONObject(point);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return new JSONObject();
    }

    private static String objectsToJson(List<ECPointDTO> data) {
        try {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            JSONArray array = new JSONArray();
            for (ECPointDTO datum : data) {
                array.put(objectToJson(datum));
            }

            return array.toString();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return "";
    }

    private static List<ECPointDTO> jsonToObjects(String data) {
        try {
            ECPointDTO[] publicKeys = objectMapper.readValue(data, ECPointDTO[].class);

            return new ArrayList<>(Arrays.asList(publicKeys));
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return new ArrayList<>();
    }
}