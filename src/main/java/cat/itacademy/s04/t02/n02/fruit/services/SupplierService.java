package cat.itacademy.s04.t02.n02.fruit.services;

import cat.itacademy.s04.t02.n02.fruit.dto.SupplierRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.SupplierResponseDTO;

public interface SupplierService {
    SupplierResponseDTO addSupplier(SupplierRequestDTO request);
}