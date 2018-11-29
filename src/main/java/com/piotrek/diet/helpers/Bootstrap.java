package com.piotrek.diet.helpers;

import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.meal.MealDtoConverter;
import com.piotrek.diet.meal.MealService;
import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductDtoConverter;
import com.piotrek.diet.product.ProductService;
import com.piotrek.diet.user.User;
import com.piotrek.diet.user.UserFacade;
import com.piotrek.diet.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private final UserService userService;
    private final ProductService productService;
    private final MealService mealService;
    private final MealDtoConverter mealDtoConverter;
    private final ProductDtoConverter productDtoConverter;

    @Override
    public void run(String... args) {
        productService.deleteAll().block();
        mealService.deleteAll().block();

        User user = userService.findByEmail("fake@mail.com").block();
        if (user == null) {
            user = new User(123L, "fake@mail.com", "Janusz", "Monkey");
            user.setPictureUrl("https://cdn.dribbble.com/users/199982/screenshots/4051304/burhan-avatar-dribbble-grayscale.png");
            user = userService.save(user).block();
        }

        // products

        Product tescoChlebRazowy = new Product();
        tescoChlebRazowy.setName("Tesco Chleb razowy 400 g");
        tescoChlebRazowy.setDescription("Cechy\n\ngwarancja 100% satysfakcji, 100% żytni, wypiekany na naturalnym zakwasie, bez" +
                " substancji konserwujących i polepszaczy, Jestem z Polski\nO marce\n\nGwarantujemy 100% satysfakcji " +
                "lub zwrot Twoich pieniędzy. Sprawdź jak to działa na naszej stronie internetowej www.tesco.pl lub w sklepie.\n" +
                "\nChleb razowy\nChleb żytni razowy, krojony");
        tescoChlebRazowy.setFat(1.6);
        tescoChlebRazowy.setKcal(216);
        tescoChlebRazowy.setProtein(4.9);
        tescoChlebRazowy.setCarbohydrate(43);
        tescoChlebRazowy.setImageUrl("https://secure.ce-tescoassets.com/assets/PL/924/5051007036924/ShotType1_328x328.jpg");
        tescoChlebRazowy.setUserId(user.getId());
        tescoChlebRazowy = productDtoConverter.fromDto(productService.save(tescoChlebRazowy).block());

        Product mleko = new Product();
        mleko.setName("Mleko 1%");
        mleko.setDescription("Mleko – wydzielina gruczołu mlekowego samic ssaków, pojawiająca się w okresie laktacji. " +
                "Jako produkt żywnościowy dla człowieka najczęstsze zastosowanie ma mleko krowie. ");
        mleko.setFat(1);
        mleko.setKcal(42);
        mleko.setProtein(3.4);
        mleko.setCarbohydrate(5);
        mleko.setImageUrl("http://www.niam.pl/rimages/crop/600/450/files/images/PRODUCT/BACKUP/95909308264_yfyxqwqdrkprnszprwau.jpg");
        mleko.setUserId(user.getId());
        mleko = productDtoConverter.fromDto( productService.save(mleko).block());

        Product cornFlakes = new Product();
        cornFlakes.setName("Płatki kukurydziane Corn Flakes");
        cornFlakes.setDescription("Płatki kukurydziane – popularny posiłek śniadaniowy, głównie ze względu na krótki czas" +
                " przygotowywania oraz pożywność. Zaliczany do zdrowej żywności. Niemal zawsze spożywane z mlekiem," +
                " czasem na sucho. Płatki oprócz kukurydzy mogą zawierać inne gatunki zboża.");
        cornFlakes.setFat(0.4);
        cornFlakes.setKcal(357);
        cornFlakes.setProtein(8);
        cornFlakes.setCarbohydrate(84);
        cornFlakes.setImageUrl("https://res.cloudinary.com/dj484tw6k/f_auto,q_auto,c_pad,b_white,w_360,h_360/v1522812018/bb/40280.png");
        cornFlakes.setUserId(user.getId());
        cornFlakes = productDtoConverter.fromDto(productService.save(cornFlakes).block());

        Product jajko = new Product();
        jajko.setName("Jajko");
        jajko.setDescription("Jaja jako bogate źródło substancji odżywczych są podstawą wielu potraw. W Polsce najczęściej" +
                " spożywa się jaja kurze, a także kacze, gęsie i przepiórcze i indycze. Jaja towarzyszą człowiekowi od" +
                " początku jego istnienia. Jaja rybie w postaci kawioru uważane są za wyrafinowany przysmak.");
        jajko.setFat(11);
        jajko.setKcal(155);
        jajko.setProtein(13);
        jajko.setCarbohydrate(1.1);
        jajko.setImageUrl("https://zabawkiprogresywne.pl/1957-large_default/kauczukowe-jajko-kurze.jpg");
        jajko.setUserId(user.getId());
        jajko = productDtoConverter.fromDto(productService.save(jajko).block());

        Product maslo = new Product();
        maslo.setName("Masło");
        maslo.setDescription("Masło – tłuszcz jadalny w postaci zestalonej, otrzymywany ze śmietany z mleka krowiego." +
                " Masło tradycyjnie wyrabia się w urządzeniach zwanych maselnicami.");
        maslo.setFat(81);
        maslo.setKcal(716);
        maslo.setProtein(0.9);
        maslo.setCarbohydrate(0.1);
        maslo.setImageUrl("https://image.ceneostatic.pl/data/products/41203912/i-mlekovita-maslo-polskie-100-g.jpg");
        maslo.setUserId(user.getId());
        maslo = productDtoConverter.fromDto(productService.save(maslo).block());

        Product woda = new Product();
        woda.setName("Woda");
        woda.setDescription("Woda – związek chemiczny o wzorze H₂O, występujący w warunkach standardowych w stanie ciekłym." +
                " W stanie gazowym wodę określa się mianem pary wodnej, a w stałym stanie skupienia – lodem. Słowo woda" +
                " jako nazwa związku chemicznego może się odnosić do każdego stanu skupienia.");
        woda.setImageUrl("https://www.focus.pl/media/cache/default_view/uploads/media/default/0001/26/woda-butelkowana.jpeg");
        woda.setUserId(user.getId());
        woda = productDtoConverter.fromDto(productService.save(woda).block());


        Meal kanapkiZJajkiem = new Meal();
        kanapkiZJajkiem.setName("Kanapki z jajkiem");
        kanapkiZJajkiem.setDescription("Dobre kanapki z jajakiem idealne na śniadanie");
        kanapkiZJajkiem.setRecipe("Aby przygotować dwie kanapki z jajkiem:\nUgotuj 2 jajaka(ok. 120g) - tak jak lubisz\n" +
                "Weź dwie kromki(ok. 100g) - posmaruj je masłem (ok. 15g\nJajka pokój i połóż na chlebie\nGOTOWE!");
//        kanapkiZJajkiem.setImageUrl("https://www.kwestiasmaku.com/sites/kwestiasmaku.com/files/kanapka_jajko_awokado_rzodkiewka_01.jpg");
        kanapkiZJajkiem.setUserId(user.getId());

        jajko.setAmount(120);
        tescoChlebRazowy.setAmount(100);
        maslo.setAmount(15);
        kanapkiZJajkiem = mealService.save(kanapkiZJajkiem).block();

        kanapkiZJajkiem.getProducts().addAll(List.of(jajko, tescoChlebRazowy, maslo));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getId(), null, new ArrayList<>()));
        mealService.updateMeal(kanapkiZJajkiem.getId(), mealDtoConverter.toDto(kanapkiZJajkiem)).block();
    }
}
