package cat.itacademy.s04.t02.n02.fruit.dto;

import jakarta.validation.constraints.NotBlank;

public record SupplierRequestDTO(
        @NotBlank(message = "Supplier name cannot be blank")
        String name,

        @NotBlank(message = "Country cannot be blank")
        String country
) { }