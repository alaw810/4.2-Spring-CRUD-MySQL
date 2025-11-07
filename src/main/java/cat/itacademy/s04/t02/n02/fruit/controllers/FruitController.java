package cat.itacademy.s04.t02.n02.fruit.controllers;

import cat.itacademy.s04.t02.n02.fruit.dto.FruitRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.FruitResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.services.FruitService;
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
    public ResponseEntity<FruitResponseDTO> createFruit(@Valid @RequestBody FruitRequestDTO request) {
        FruitResponseDTO created = fruitService.addFruit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FruitResponseDTO> getFruitById(@PathVariable Long id) {
        return ResponseEntity.ok(fruitService.getFruitById(id));
    }

    @GetMapping
    public ResponseEntity<List<FruitResponseDTO>> listFruits(@RequestParam(required = false) Long supplierId) {
        if (supplierId != null) {
            return ResponseEntity.ok(fruitService.getFruitsBySupplierId(supplierId));
        }
        return ResponseEntity.ok(fruitService.getAllFruits());
    }

    @PutMapping("/{id}")
    public ResponseEntity<FruitResponseDTO> updateFruit(@PathVariable Long id, @Valid @RequestBody FruitRequestDTO request) {
        return ResponseEntity.ok(fruitService.updateFruit(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFruit(@PathVariable Long id) {
        fruitService.deleteFruit(id);
        return ResponseEntity.noContent().build();
    }
}
