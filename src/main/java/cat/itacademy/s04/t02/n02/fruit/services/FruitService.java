package cat.itacademy.s04.t02.n02.fruit.services;

import cat.itacademy.s04.t02.n02.fruit.dto.FruitRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.FruitResponseDTO;

import java.util.List;

public interface FruitService {
    FruitResponseDTO addFruit(FruitRequestDTO request);
    List<FruitResponseDTO> getFruitsBySupplierId(Long supplierId);
}
