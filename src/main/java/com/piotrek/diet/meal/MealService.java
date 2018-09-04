package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.PageSupport;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRepository mealRepository;
    private final MealDtoConverter mealDtoConverter;

    Mono<Meal> findById(String id) {
        return mealRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Not found meal [id = " + id + "]"))));
    }

    Flux<Meal> findAllByUserId(String userId) {
        return mealRepository.findAllByUserId(userId);
    }

    Mono<PageSupport<MealDto>> findAllPageable(Pageable pageable) {
        return mealRepository
                .findAll()
                .map(mealDtoConverter::toDto)
                .collectList()
                .map(list -> new PageSupport<>(
                        list
                                .stream()
                                .skip(pageable.getPageNumber() * pageable.getPageSize())
                                .limit(pageable.getPageSize())
                                .collect(Collectors.toList()),
                        pageable.getPageNumber(), pageable.getPageSize(), list.size()));
    }
}
