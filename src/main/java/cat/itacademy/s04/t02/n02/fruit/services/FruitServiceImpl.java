package cat.itacademy.s04.t02.n02.fruit.services;

import cat.itacademy.s04.t02.n02.fruit.dto.FruitRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.FruitResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.model.Fruit;
import cat.itacademy.s04.t02.n02.fruit.repository.FruitRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FruitServiceImpl implements FruitService{

    private final FruitRepository fruitRepository;

    public FruitServiceImpl(FruitRepository fruitRepository) {
        this.fruitRepository = fruitRepository;
    }

    @Override
    public FruitResponseDTO addFruit(FruitRequestDTO request) {
        Fruit fruit = new Fruit(null, request.name(), request.weightInKilos());
        Fruit saved = fruitRepository.save(fruit);
        return mapToDto(saved);
    }

    @Override
    public List<FruitResponseDTO> getAllFruits() {
        List<Fruit> fruits = fruitRepository.findAll();
        return mapToDtoList(fruits);
    }

    @Override
    public Optional<FruitResponseDTO> getFruitById(Long id) {
        return fruitRepository.findById(id)
                .map(this::mapToDto);
    }

    @Override
    public FruitResponseDTO updateFruit(Long id, FruitRequestDTO request) {
        Fruit fruit = fruitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fruit with ID " + id + " not found"));

        fruit.setName(request.name());
        fruit.setWeightInKilos(request.weightInKilos());

        Fruit updated = fruitRepository.save(fruit);

        return mapToDto(updated);
    }

    @Override
    public void deleteFruit(Long id) {
        Fruit fruit = fruitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fruit with ID " + id + " not found"));

        fruitRepository.delete(fruit);
    }

    private FruitResponseDTO mapToDto(Fruit fruit) {
        return new FruitResponseDTO(
                fruit.getId(),
                fruit.getName(),
                fruit.getWeightInKilos());
    }

    private List<FruitResponseDTO> mapToDtoList(List<Fruit> fruits) {
        return fruits.stream()
                .map(this::mapToDto)
                .toList();
    }

}
