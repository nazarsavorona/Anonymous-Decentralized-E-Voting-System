package ua.knu.backend.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ua.knu.backend.dto.SignatureDTO;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Slf4j
@Converter
public class SignatureConverterJson implements AttributeConverter<SignatureDTO, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(SignatureDTO meta) {
        try {
            String objectStr = objectToJson(meta);
            return objectStr;
        } catch (Exception ex) {
            return null;
            // or throw an error
        }
    }

    @Override
    public SignatureDTO convertToEntityAttribute(String dbData) {
        try {
            SignatureDTO signature = jsonToObject(dbData);
            return signature;
        } catch (Exception ex) {
//            logger.error("Unexpected IOEx decoding json from database: " + dbData);
            return null;
        }
    }

    private static String objectToJson(SignatureDTO data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return "";
    }

    private static SignatureDTO jsonToObject(String data) {
        try {
            return objectMapper.readValue(data, SignatureDTO.class);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return new SignatureDTO();
    }
}