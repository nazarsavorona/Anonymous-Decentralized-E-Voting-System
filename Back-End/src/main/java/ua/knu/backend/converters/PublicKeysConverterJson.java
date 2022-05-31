package ua.knu.backend.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
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
            return objectStr;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    @Override
    public List<ECPointDTO> convertToEntityAttribute(String dbData) {
        try {
            List<ECPointDTO> list = jsonToObjects(dbData);
            return list;
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return null;
    }

    private static String objectsToJson(List<ECPointDTO> data) {
        try {
            JSONArray array = new JSONArray();
            for (int i = 0; i < data.size(); i++) {
                ECPointDTO temp = data.get(i);
                Object obj = new JSONObject(temp);
                array.put(obj);
            }
            return array.toString();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return "";
    }

    private static List<ECPointDTO> jsonToObjects(String data) {
        try {
            ECPointDTO[] publicKeys = objectMapper.readValue(data, ECPointDTO[].class);

            return new ArrayList<>(Arrays.asList(publicKeys));
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return new ArrayList<>();
    }
}