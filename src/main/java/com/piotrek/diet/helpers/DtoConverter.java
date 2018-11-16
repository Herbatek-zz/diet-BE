package com.piotrek.diet.helpers;

public interface DtoConverter<E extends BaseEntity, D extends BaseDto> {

    D toDto(E entity);

    E fromDto(D dto);
}
