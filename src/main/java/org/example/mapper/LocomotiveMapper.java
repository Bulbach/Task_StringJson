package org.example.mapper;

import org.example.data.LocomotiveDto;
import org.example.entity.Locomotive;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface LocomotiveMapper {


    LocomotiveDto toDto(Locomotive locomotive);

    @Mapping(target = "uuid", ignore = true)
    Locomotive toLocomotive(LocomotiveDto locomotiveDto);

}
