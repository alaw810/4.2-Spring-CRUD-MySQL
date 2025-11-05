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

import java.util.List;
import java.util.Optional;

@Service
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

        Fruit saved = fruitRepository.save(new Fruit(null, request.name(), request.weightInKilos(), supplier));
        return mapToDto(saved, supplier);
    }

    private FruitResponseDTO mapToDto(Fruit fruit, Supplier supplier) {
        return new FruitResponseDTO(
                fruit.getId(),
                fruit.getName(),
                fruit.getWeightInKilos(),
                new SupplierResponseDTO(
                        supplier.getId(),
                        supplier.getName(),
                        supplier.getCountry()
                )
        );
    }

    /*private List<FruitResponseDTO> mapToDtoList(List<Fruit> fruits) {
        return fruits.stream()
                .map(this::mapToDto)
                .toList();
    }*/

}
