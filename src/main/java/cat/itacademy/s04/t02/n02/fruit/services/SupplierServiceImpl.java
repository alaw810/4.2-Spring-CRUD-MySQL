package cat.itacademy.s04.t02.n02.fruit.services;

import cat.itacademy.s04.t02.n02.fruit.dto.SupplierRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.SupplierResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.model.Supplier;
import cat.itacademy.s04.t02.n02.fruit.repository.SupplierRepository;
import org.springframework.stereotype.Service;

@Service
public class SupplierServiceImpl implements SupplierService{
    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
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
}