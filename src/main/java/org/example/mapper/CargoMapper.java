package org.example.mapper;

import org.example.data.CargoDto;
import org.example.entity.Cargo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface CargoMapper {
    CargoDto toDto(Cargo cargo);

    @Mapping(target = "uuid", ignore = true)
    Cargo toCargo(CargoDto cargoDto);

}
