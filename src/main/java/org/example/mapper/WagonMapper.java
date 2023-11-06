package org.example.mapper;

import org.example.data.WagonDto;
import org.example.entity.Wagon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = CargoMapper.class)
public interface WagonMapper {

    WagonDto toDto(Wagon wagon);

    @Mapping(target = "uuid", ignore = true)
    Wagon toWagon(WagonDto wagonDto);
}
