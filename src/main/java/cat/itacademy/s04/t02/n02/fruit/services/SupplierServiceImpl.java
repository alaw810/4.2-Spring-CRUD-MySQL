package cat.itacademy.s04.t02.n02.fruit.services;

import cat.itacademy.s04.t02.n02.fruit.dto.SupplierRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.SupplierResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.model.Supplier;
import cat.itacademy.s04.t02.n02.fruit.repository.FruitRepository;
import cat.itacademy.s04.t02.n02.fruit.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService{

    private final SupplierRepository supplierRepository;
    private final FruitRepository fruitRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository, FruitRepository fruitRepository) {
        this.supplierRepository = supplierRepository;
        this.fruitRepository = fruitRepository;
    }

    @Override
    public SupplierResponseDTO addSupplier(SupplierRequestDTO request) {
        if (supplierRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Supplier name already exists");
        }

        Supplier supplier = new Supplier(null, request.name(), request.country());
        Supplier saved = supplierRepository.save(supplier);

        return new SupplierResponseDTO(saved.getId(), saved.getName(), saved.getCountry());
    }

    @Override
    public List<SupplierResponseDTO> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(s -> new SupplierResponseDTO(s.getId(), s.getName(), s.getCountry()))
                .toList();
    }

    @Override
    public SupplierResponseDTO getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier with id " + id + " not found"));
        return new SupplierResponseDTO(supplier.getId(), supplier.getName(), supplier.getCountry());
    }

    @Override
    public SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO request) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier with id " + id + " not found"));

        if (supplierRepository.existsByName(request.name())
        && !existing.getName().equalsIgnoreCase(request.name())) {
            throw new IllegalArgumentException("Supplier name already exists");
        }

        existing.setName(request.name());
        existing.setCountry(request.country());

        Supplier updated = supplierRepository.save(existing);

        return new SupplierResponseDTO(updated.getId(), updated.getName(), updated.getCountry());
    }

    @Override
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier with id " + id + " not found"));

        if (!fruitRepository.findBySupplierId(id).isEmpty()) {
            throw new IllegalStateException("Cannot delete supplier with associated fruits");
        }

        supplierRepository.delete(supplier);
    }
}