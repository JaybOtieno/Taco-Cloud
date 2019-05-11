package sia.tacocloud.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import sia.tacocloud.model.Design;
import sia.tacocloud.model.Ingredient;
import sia.tacocloud.model.Ingredient.Type;
import sia.tacocloud.model.Order;
import sia.tacocloud.model.Taco;
import sia.tacocloud.repositories.IngredientRepository;
import sia.tacocloud.repositories.TacoRepository;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {

    private final IngredientRepository ingredientRepo;

    private final TacoRepository designRepo;

    @Autowired
    public DesignTacoController(IngredientRepository ingredientRepository, TacoRepository tacoRepository){
        this.ingredientRepo = ingredientRepository;
        this.designRepo = tacoRepository;
    }

    @ModelAttribute(name = "order")
    public Order order(){
        return new Order();
    }

    @ModelAttribute(name = "taco")
    public Taco taco(){
        return new Taco();
    }

    @GetMapping("/")
    public String showDesignForm(Model model){
        List<Ingredient> ingredients = new ArrayList<>();

        ingredientRepo.findAll().forEach( i -> ingredients.add(i));

       Type[] types = Ingredient.Type.values();

        for(Ingredient.Type type : types){
            model.addAttribute(type.toString().toLowerCase(),filterByType(ingredients, type));
        }

        model.addAttribute("design", new Taco());

        return "design";
    }


    private List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
        return ingredients
                .stream()
                .filter(x -> x.getType().equals(type))
                .collect(Collectors.toList());
    }

    @PostMapping
    public String processDesign(@Valid Taco design, Errors errors, @ModelAttribute Order order) {

        if (errors.hasErrors()) {
            return "design";
        }

        Taco tacoSaved = designRepo.save(design);
        order.addDesign(tacoSaved);

        log.info("Processing design: " + design);
        return "redirect:/orders/current";

    }
}
