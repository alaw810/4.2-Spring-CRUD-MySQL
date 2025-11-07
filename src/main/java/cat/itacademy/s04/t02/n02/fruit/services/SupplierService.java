package cat.itacademy.s04.t02.n02.fruit.services;

import cat.itacademy.s04.t02.n02.fruit.dto.SupplierRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.SupplierResponseDTO;

import java.util.List;

public interface SupplierService {
    SupplierResponseDTO addSupplier(SupplierRequestDTO request);
    List<SupplierResponseDTO> getAllSuppliers();
    SupplierResponseDTO getSupplierById(Long id);
    SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO request);
    void deleteSupplier(Long id);
}