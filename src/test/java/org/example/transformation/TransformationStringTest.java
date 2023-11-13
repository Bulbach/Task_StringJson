package org.example.transformation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class TransformationStringTest {

    @BeforeAll
    static void setUp() {

    }

    @Test
    void deserializeShouldThrowIllegalArgumentException(){

        // given
        String expectedMessage = "Object is null";

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () ->TransformationString.serialize(null));
        String actualMessage = exception.getMessage();

        // then
        assertTrue(actualMessage.contains(expectedMessage));
    }
}