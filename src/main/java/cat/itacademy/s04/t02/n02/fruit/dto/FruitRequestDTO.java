package cat.itacademy.s04.t02.n02.fruit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FruitRequestDTO(
    @NotBlank(message = "Fruit name cannot be empty")
    String name,
    @Positive(message = "Weight must be positive")
    int weightInKilos,
    @NotNull(message = "Supplier ID is required")
    @Positive(message = "Supplier ID must be positive")
    Long supplierId
) { }
