package cat.itacademy.s04.t02.n02.fruit.services;

import cat.itacademy.s04.t02.n02.fruit.dto.FruitRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.FruitResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.SupplierResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.model.Fruit;
import cat.itacademy.s04.t02.n02.fruit.model.Supplier;
import cat.itacademy.s04.t02.n02.fruit.repository.FruitRepository;
import cat.itacademy.s04.t02.n02.fruit.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@Transactional
public class FruitServiceImpl implements FruitService{

    private final FruitRepository fruitRepository;
    private final SupplierRepository supplierRepository;

    public FruitServiceImpl(FruitRepository fruitRepository, SupplierRepository supplierRepository) {
        this.fruitRepository = fruitRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    public FruitResponseDTO addFruit(FruitRequestDTO request) {
        Supplier supplier = supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new EntityNotFoundException("Supplier with ID " + request.supplierId() + " not found"));

        Fruit fruit = new Fruit();
        fruit.setName(request.name().trim());
        fruit.setWeightInKilos(request.weightInKilos());
        fruit.setSupplier(supplier);

        Fruit saved = fruitRepository.save(fruit);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FruitResponseDTO getFruitById(Long id) {
        Fruit fruit = fruitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fruit not found: " + id));
        return mapToDto(fruit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FruitResponseDTO> getAllFruits() {
        return fruitRepository.findAll().stream().map(this::mapToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FruitResponseDTO> getFruitsBySupplierId(Long supplierId) {
        if (!supplierRepository.existsById(supplierId)) {
            throw new EntityNotFoundException("Supplier with id " + supplierId + " not found");
        }
        return fruitRepository.findBySupplierId(supplierId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public FruitResponseDTO updateFruit(Long id, FruitRequestDTO request) {
        Fruit fruit = fruitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fruit with id " + id + " not found"));

        fruit.setName(request.name());
        fruit.setWeightInKilos(request.weightInKilos());

        if (request.supplierId() != null) {
            Supplier supplier = supplierRepository.findById(request.supplierId())
                    .orElseThrow(() -> new EntityNotFoundException("Supplier with id " + request.supplierId() + " not found"));
            fruit.setSupplier(supplier);
        }

        Fruit updated = fruitRepository.save(fruit);
        return mapToDto(updated);
    }

    @Override
    public void deleteFruit(Long id) {
        Fruit fruit = fruitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fruit not found: " + id));
        fruitRepository.delete(fruit);
    }


    private FruitResponseDTO mapToDto(Fruit fruit) {
        Supplier s = fruit.getSupplier();
        SupplierResponseDTO supplierDto = new SupplierResponseDTO(
                s.getId(),
                s.getName(),
                s.getCountry()
        );
        return new FruitResponseDTO(
                fruit.getId(),
                fruit.getName(),
                fruit.getWeightInKilos(),
                supplierDto
        );
    }
}
