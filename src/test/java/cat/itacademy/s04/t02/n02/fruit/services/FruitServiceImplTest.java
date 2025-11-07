package cat.itacademy.s04.t02.n02.fruit.services;

import cat.itacademy.s04.t02.n02.fruit.dto.FruitRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.FruitResponseDTO;
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

class FruitServiceImplTest {

    @Mock
    private FruitRepository fruitRepository;
    @Mock
    private SupplierRepository supplierRepository;
    @InjectMocks
    private FruitServiceImpl fruitService;

    private Supplier supplier;
    private Fruit fruit;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("FreshFarm");
        supplier.setCountry("Spain");

        fruit = new Fruit();
        fruit.setId(1L);
        fruit.setName("Banana");
        fruit.setWeightInKilos(5);
        fruit.setSupplier(supplier);
    }

    @Test
    void addFruit_shouldSaveAndReturnDTO_whenSupplierExists() {
        FruitRequestDTO request = new FruitRequestDTO("Banana", 5, 1L);
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(fruitRepository.save(any(Fruit.class))).thenReturn(fruit);

        FruitResponseDTO result = fruitService.addFruit(request);

        assertThat(result.name()).isEqualTo("Banana");
        assertThat(result.weightInKilos()).isEqualTo(5);
        verify(fruitRepository, times(1)).save(any(Fruit.class));
    }

    @Test
    void addFruit_shouldThrow_whenSupplierNotFound() {
        FruitRequestDTO request = new FruitRequestDTO("Banana", 5, 99L);
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fruitService.addFruit(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Supplier with ID " + request.supplierId() + " not found");
    }

    @Test
    void getFruitById_shouldReturnDTO_whenFruitExists() {
        when(fruitRepository.findById(1L)).thenReturn(Optional.of(fruit));

        FruitResponseDTO result = fruitService.getFruitById(1L);

        assertThat(result.name()).isEqualTo("Banana");
        assertThat(result.supplier().name()).isEqualTo("FreshFarm");
    }

    @Test
    void getFruitById_shouldThrow_whenNotFound() {
        when(fruitRepository.findById(10L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> fruitService.getFruitById(10L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getFruitsBySupplierId_shouldReturnList_whenSupplierExists() {
        when(supplierRepository.existsById(1L)).thenReturn(true);
        when(fruitRepository.findBySupplierId(1L)).thenReturn(List.of(fruit));

        List<FruitResponseDTO> list = fruitService.getFruitsBySupplierId(1L);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).name()).isEqualTo("Banana");
    }

    @Test
    void getFruitsBySupplierId_shouldThrow_whenSupplierNotFound() {
        when(supplierRepository.existsById(2L)).thenReturn(false);

        assertThatThrownBy(() -> fruitService.getFruitsBySupplierId(2L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateFruit_shouldUpdateAndReturnDTO_whenFound() {
        FruitRequestDTO request = new FruitRequestDTO("Apple", 10, 1L);
        when(fruitRepository.findById(1L)).thenReturn(Optional.of(fruit));
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(fruitRepository.save(any(Fruit.class))).thenReturn(fruit);

        FruitResponseDTO result = fruitService.updateFruit(1L, request);

        assertThat(result.name()).isEqualTo("Apple");
        verify(fruitRepository, times(1)).save(any(Fruit.class));
    }

    @Test
    void deleteFruit_shouldRemove_whenExists() {
        when(fruitRepository.findById(1L)).thenReturn(Optional.of(fruit));

        fruitService.deleteFruit(1L);

        verify(fruitRepository, times(1)).delete(fruit);
    }

    @Test
    void deleteFruit_shouldThrow_whenNotFound() {
        when(fruitRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fruitService.deleteFruit(2L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
