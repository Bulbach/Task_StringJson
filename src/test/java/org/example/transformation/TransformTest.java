package org.example.transformation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.data.LocomotiveDto;
import org.example.entity.Locomotive;
import org.example.mapper.LocomotiveMapper;
import org.example.mapper.LocomotiveMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class TransformTest {

    private Transform transform;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializeShouldThrowIllegalArgumentException() throws JsonProcessingException {

        // given
        Locomotive locomotive = Locomotive.builder()
                .model("2ТЭ10У")
                .locomotiveNumber("0234")
                .typeLocomotive("Грузовой")
                .documents(null)
                .build();
        LocomotiveMapper locomotiveMapper = new LocomotiveMapperImpl();
        LocomotiveDto locomotiveDto = locomotiveMapper.toDto(locomotive);
        String json = objectMapper.writeValueAsString(locomotiveDto);
        String fakeJson = json.replace("{", "");
        transform = new Transform(fakeJson);
        String expectedMessage = "Invalid JSON";

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> transform.deserialize(LocomotiveDto.class));
        String actualMessage = exception.getMessage();

        // then
        assertTrue(actualMessage.contains(expectedMessage));
    }
}