package com.piotrek.diet.user;

import com.piotrek.diet.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDtoConverter userDtoConverter;
    private final CaloriesCalculator caloriesCalculator;
    private final MacronutrientCalculator macroCalculator;

    Mono<UserDto> findDtoById(String id) {
        return findById(id).map(userDtoConverter::toDto);
    }

    public Mono<User> findById(String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Not found user [id = " + id + "]"))));
    }

    public Mono<User> findByFacebookId(Long facebookId) {
        return userRepository.findByFacebookId(facebookId);
    }

    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> save(User user) {
        return userRepository.save(user);
    }

    public Mono<User> save(UserDto userDto) {
        return userRepository.save(userDtoConverter.fromDto(userDto));
    }

    @PreAuthorize("#userId.equals(principal)")
    Mono<UserDto> update(String userId, UserDto userDto) {
        return findById(userId)
                .doOnNext(user -> user.setUsername(userDto.getUsername()))
                .doOnNext(user -> user.setFirstName(userDto.getFirstName()))
                .doOnNext(user -> user.setLastName(userDto.getLastName()))
                .doOnNext(user -> user.setEmail(userDto.getEmail()))
                .doOnNext(user -> user.setPictureUrl(userDto.getPicture_url()))
                .doOnNext(user -> user.setSex(userDto.getSex()))
                .doOnNext(user -> user.setActivity(userDto.getActivity()))
                .doOnNext(user -> user.setAge(userDto.getAge()))
                .doOnNext(user -> user.setWeight(userDto.getWeight()))
                .doOnNext(user -> user.setHeight(userDto.getHeight()))
                .doOnNext(user -> user.setCaloriesPerDay(caloriesCalculator.calculateCaloriesPerDay(userDto)))
                .doOnNext(user -> user.setCarbohydratePerDay(macroCalculator.calculateDailyCarbohydrate(user.getCaloriesPerDay())))
                .doOnNext(user -> user.setProteinPerDay(macroCalculator.calculateDailyProtein(user.getCaloriesPerDay())))
                .doOnNext(user -> user.setFatPerDay(macroCalculator.calculateDailyFat(user.getCaloriesPerDay())))
                .flatMap(userRepository::save)
                .map(userDtoConverter::toDto);
    }

    Mono<Void> deleteById(String userId) {
        return userRepository.deleteById(userId);
    }

    Mono<Void> deleteAll() {
        return userRepository.deleteAll();
    }
}
