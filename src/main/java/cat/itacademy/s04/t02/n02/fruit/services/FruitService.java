package cat.itacademy.s04.t02.n02.fruit.services;

import cat.itacademy.s04.t02.n02.fruit.dto.FruitRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.FruitResponseDTO;

import java.util.List;
import java.util.Optional;

public interface FruitService {
    FruitResponseDTO addFruit(FruitRequestDTO request);
    List<FruitResponseDTO> getAllFruits();
    Optional<FruitResponseDTO> getFruitById(Long id);
    FruitResponseDTO updateFruit(Long id, FruitRequestDTO request);
    void deleteFruit(Long id);
}
