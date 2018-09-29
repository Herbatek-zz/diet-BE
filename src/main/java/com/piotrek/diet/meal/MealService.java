package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.PageSupport;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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

    public Mono<Meal> findById(String id) {
        return mealRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Not found meal [id = " + id + "]"))));
    }

    Flux<Meal> findAllByUserId(String userId) {
        return mealRepository.findAllByUserId(userId);
    }

    Mono<PageSupport<MealDto>> findAllPageable(Pageable pageable) {
        return mealRepository
                .findAll()
                .collectList()
                .map(list -> new PageSupport<>(
                        list
                                .stream()
                                .skip(pageable.getPageNumber() * pageable.getPageSize())
                                .limit(pageable.getPageSize())
                                .map(mealDtoConverter::toDto)
                                .collect(Collectors.toList()),
                        pageable.getPageNumber(), pageable.getPageSize(), list.size()));
    }

    public Mono<Meal> save(Meal meal) {
        return mealRepository.save(meal);
    }

    Mono<Void> deleteAll() {
        return mealRepository.deleteAll();
    }

    Mono<Void> deleteById(String id) {
        return mealRepository.deleteById(id);
    }

    Mono<PageSupport<MealDto>> searchByName(PageRequest pageRequest, String query) {
        return mealRepository.findAllByNameIgnoreCaseContaining(query)
                .collectList()
                .map(list -> new PageSupport<>(
                        list
                                .stream()
                                .skip(pageRequest.getPageNumber() * pageRequest.getPageSize())
                                .limit(pageRequest.getPageSize())
                                .map(mealDtoConverter::toDto)
                                .collect(Collectors.toList()),
                        pageRequest.getPageNumber(), pageRequest.getPageSize(), list.size()));
    }
}
