package cat.itacademy.s04.t02.n02.fruit.controllers;

import cat.itacademy.s04.t02.n02.fruit.dto.FruitRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.FruitResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.services.FruitService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fruits")
public class FruitController {

    private final FruitService fruitService;

    public FruitController(FruitService fruitService) {
        this.fruitService = fruitService;
    }

    @PostMapping
    public ResponseEntity<?> createFruit(@Valid @RequestBody FruitRequestDTO request) {
        try {
            FruitResponseDTO created = fruitService.addFruit(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getFruitsBySupplier(@RequestParam Long supplierId) {
        try {
            List<FruitResponseDTO> fruits = fruitService.getFruitsBySupplierId(supplierId);
            return ResponseEntity.ok(fruits);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
