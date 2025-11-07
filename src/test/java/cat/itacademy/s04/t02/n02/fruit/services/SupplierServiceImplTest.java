package cat.itacademy.s04.t02.n02.fruit.services;

import cat.itacademy.s04.t02.n02.fruit.dto.SupplierRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.SupplierResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.model.Fruit;
import cat.itacademy.s04.t02.n02.fruit.model.Supplier;
import cat.itacademy.s04.t02.n02.fruit.repository.FruitRepository;
import cat.itacademy.s04.t02.n02.fruit.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private FruitRepository fruitRepository;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    private Supplier supplier;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        supplier = new Supplier(1L, "FreshFarm", "Spain");
    }

    @Test
    void addSupplier_shouldSaveAndReturnDTO_whenNameIsUnique() {
        SupplierRequestDTO request = new SupplierRequestDTO("GreenGrow", "Italy");

        when(supplierRepository.existsByName("GreenGrow")).thenReturn(false);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);

        SupplierResponseDTO result = supplierService.addSupplier(request);

        assertThat(result.name()).isEqualTo("FreshFarm");
        assertThat(result.country()).isEqualTo("Spain");
        verify(supplierRepository, times(1)).save(any(Supplier.class));
    }

    @Test
    void addSupplier_shouldThrow_whenNameAlreadyExists() {
        SupplierRequestDTO request = new SupplierRequestDTO("FreshFarm", "Spain");
        when(supplierRepository.existsByName("FreshFarm")).thenReturn(true);

        assertThatThrownBy(() -> supplierService.addSupplier(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Supplier name already exists");

        verify(supplierRepository, never()).save(any(Supplier.class));
    }

    @Test
    void getAllSuppliers_shouldReturnListOfDTOs_whenSuppliersExist() {
        Supplier supplier1 = new Supplier(1L, "FreshFarm", "Spain");
        Supplier supplier2 = new Supplier(2L, "GreenWorld", "Italy");
        when(supplierRepository.findAll()).thenReturn(List.of(supplier1, supplier2));

        List<SupplierResponseDTO> result = supplierService.getAllSuppliers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("FreshFarm");
        assertThat(result.get(0).country()).isEqualTo("Spain");
        assertThat(result.get(1).name()).isEqualTo("GreenWorld");
        assertThat(result.get(1).country()).isEqualTo("Italy");

        verify(supplierRepository, times(1)).findAll();
    }

    @Test
    void getAllSuppliers_shouldReturnEmptyList_whenNoSuppliersExist() {
        when(supplierRepository.findAll()).thenReturn(List.of());

        List<SupplierResponseDTO> result = supplierService.getAllSuppliers();

        assertThat(result).isEmpty();
        verify(supplierRepository, times(1)).findAll();
    }

    @Test
    void getSupplierById_shouldReturnDTO_whenSupplierExists() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

        SupplierResponseDTO result = supplierService.getSupplierById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("FreshFarm");
        assertThat(result.country()).isEqualTo("Spain");
        verify(supplierRepository, times(1)).findById(1L);
    }

    @Test
    void getSupplierById_shouldThrow_whenSupplierNotFound() {
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.getSupplierById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Supplier with id 99 not found");

        verify(supplierRepository, times(1)).findById(99L);
    }

    @Test
    void updateSupplier_shouldUpdateAndReturnDTO_whenSupplierExistsAndNameUnique() {
        SupplierRequestDTO request = new SupplierRequestDTO("FreshFarmUpdated", "France");

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepository.existsByName("FreshFarmUpdated")).thenReturn(false);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(
                new Supplier(1L, "FreshFarmUpdated", "France")
        );

        SupplierResponseDTO result = supplierService.updateSupplier(1L, request);

        assertThat(result.name()).isEqualTo("FreshFarmUpdated");
        assertThat(result.country()).isEqualTo("France");
        verify(supplierRepository, times(1)).save(any(Supplier.class));
    }

    @Test
    void updateSupplier_shouldThrow_whenSupplierNotFound() {
        SupplierRequestDTO request = new SupplierRequestDTO("NonExistent", "Italy");
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.updateSupplier(99L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Supplier with id 99 not found");

        verify(supplierRepository, never()).save(any(Supplier.class));
    }

    @Test
    void updateSupplier_shouldThrow_whenNameAlreadyExists() {
        SupplierRequestDTO request = new SupplierRequestDTO("ExistingName", "Spain");

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepository.existsByName("ExistingName")).thenReturn(true);

        assertThatThrownBy(() -> supplierService.updateSupplier(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Supplier name already exists");
    }

    @Test
    void deleteSupplier_shouldDelete_whenSupplierExistsAndHasNoFruits() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(fruitRepository.findBySupplierId(1L)).thenReturn(List.of());

        supplierService.deleteSupplier(1L);

        verify(supplierRepository, times(1)).delete(supplier);
    }

    @Test
    void deleteSupplier_shouldThrow_whenSupplierNotFound() {
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.deleteSupplier(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Supplier with id 99 not found");

        verify(supplierRepository, never()).delete(any(Supplier.class));
    }

    @Test
    void deleteSupplier_shouldThrow_whenHasAssociatedFruits() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(fruitRepository.findBySupplierId(1L)).thenReturn(List.of(new Fruit()));

        assertThatThrownBy(() -> supplierService.deleteSupplier(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot delete supplier with associated fruits");

        verify(supplierRepository, never()).delete(any(Supplier.class));
    }
}
