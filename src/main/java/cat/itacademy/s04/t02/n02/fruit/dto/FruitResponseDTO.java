package cat.itacademy.s04.t02.n02.fruit.dto;

public record FruitResponseDTO(Long id, String name, int weightInKilos, SupplierResponseDTO supplier) {
}
