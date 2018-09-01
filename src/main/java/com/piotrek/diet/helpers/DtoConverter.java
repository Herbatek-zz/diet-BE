package com.piotrek.diet.helpers;

public interface DtoConverter<E, D> {

    D toDto(E entity);

    E fromDto(D dto);
}
