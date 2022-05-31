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
            String objectStr = objectMapper.writeValueAsString(meta);
            return objectStr;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    @Override
    public SignatureDTO convertToEntityAttribute(String dbData) {
        try {
            return  objectMapper.readValue(dbData, SignatureDTO.class);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }
}