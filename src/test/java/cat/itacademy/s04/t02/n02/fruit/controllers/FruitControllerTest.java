package cat.itacademy.s04.t02.n02.fruit.controllers;

import cat.itacademy.s04.t02.n02.fruit.dto.FruitRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.FruitResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.model.Fruit;
import cat.itacademy.s04.t02.n02.fruit.model.Supplier;
import cat.itacademy.s04.t02.n02.fruit.repository.FruitRepository;
import cat.itacademy.s04.t02.n02.fruit.repository.SupplierRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FruitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FruitRepository fruitRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    private Supplier supplier;

    @BeforeEach
    void setup() {
        fruitRepository.deleteAll();
        supplierRepository.deleteAll();

        supplier = new Supplier();
        supplier.setName("FreshFarm");
        supplier.setCountry("Spain");
        supplierRepository.save(supplier);
    }

    @Test
    void createFruit_returns201_whenSupplierExistsAndDataIsValid() throws Exception {
        FruitRequestDTO request = new FruitRequestDTO("Banana", 5, supplier.getId());

        mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Banana"))
                .andExpect(jsonPath("$.weightInKilos").value(5))
                .andExpect(jsonPath("$.supplier.id").value(supplier.getId()));
    }

    @Test
    void createFruit_returns404_whenSupplierDoesNotExist() throws Exception {
        FruitRequestDTO request = new FruitRequestDTO("Apple", 3, 999L);

        mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createFruit_returns400_whenNameIsBlank() throws Exception {
        FruitRequestDTO request = new FruitRequestDTO("   ", 4, supplier.getId());

        mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFruit_returns400_whenWeightIsInvalid() throws Exception {
        FruitRequestDTO request = new FruitRequestDTO("Mango", 0, supplier.getId());

        mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFruit_returns400_whenSupplierIdIsNull() throws Exception {
        FruitRequestDTO request = new FruitRequestDTO("Pear", 5, null);

        mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllFruits_returnsListOfFruits() throws Exception {
        fruitRepository.save(new Fruit(null, "Banana", 4, supplier));
        fruitRepository.save(new Fruit(null, "Pineapple", 5, supplier));

        mockMvc.perform(get("/fruits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Banana"))
                .andExpect(jsonPath("$[1].name").value("Pineapple"));
    }

    @Test
    void getAllFruits_returnsEmptyList_whenNoFruitsExist() throws Exception {
        mockMvc.perform(get("/fruits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getFruitById_returnsCorrectFruit() throws Exception {
        Fruit fruit = fruitRepository.save(new Fruit(null, "Pear", 2, supplier));

        mockMvc.perform(get("/fruits/" + fruit.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fruit.getId()))
                .andExpect(jsonPath("$.name").value("Pear"))
                .andExpect(jsonPath("$.weightInKilos").value(2));
    }

    @Test
    void getFruitById_returns404_whenFruitDoesNotExist() throws Exception {
        mockMvc.perform(get("/fruits/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFruitsBySupplier_returns200_andListFruits_whenSupplierExists() throws Exception {
        Supplier s = supplierRepository.save(new Supplier(null, "FruitHouse", "Spain"));
        fruitRepository.save(new Fruit(null, "Apple", 5, s));
        fruitRepository.save(new Fruit(null, "Pear", 7, s));

        mockMvc.perform(get("/fruits").param("supplierId", String.valueOf(s.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Apple"))
                .andExpect(jsonPath("$[1].name").value("Pear"));
    }

    @Test
    void getFruitsBySupplier_returnsEmptyList_whenSupplierExistsButNoFruits() throws Exception {
        Supplier s = supplierRepository.save(new Supplier(null, "NoFruits", "Portugal"));

        mockMvc.perform(get("/fruits").param("supplierId", String.valueOf(s.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getFruitsBySupplier_returns404_whenSupplierDoesNotExist() throws Exception {
        mockMvc.perform(get("/fruits").param("supplierId", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateFruit_returnsUpdatedFruit_whenDataIsValid() throws Exception {
        Fruit fruit = fruitRepository.save(new Fruit(null, "Melon", 3, supplier));
        FruitRequestDTO updated = new FruitRequestDTO("Watermelon", 6, supplier.getId());

        mockMvc.perform(put("/fruits/" + fruit.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fruit.getId()))
                .andExpect(jsonPath("$.name").value("Watermelon"))
                .andExpect(jsonPath("$.weightInKilos").value(6));
    }

    @Test
    void updateFruit_returns404_whenFruitDoesNotExist() throws Exception {
        FruitRequestDTO updated = new FruitRequestDTO("Mango", 2, supplier.getId());

        mockMvc.perform(put("/fruits/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateFruit_returns400_whenNameIsBlank() throws Exception {
        Fruit fruit = fruitRepository.save(new Fruit(null, "Orange", 5, supplier));
        FruitRequestDTO invalid = new FruitRequestDTO(" ", 5, supplier.getId());

        mockMvc.perform(put("/fruits/" + fruit.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFruit_returns400_whenWeightIsNotPositive() throws Exception {
        Fruit fruit = fruitRepository.save(new Fruit(null, "Peach", 8, supplier));
        FruitRequestDTO invalid = new FruitRequestDTO("Peach", -8, supplier.getId());

        mockMvc.perform(put("/fruits/" + fruit.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteFruit_removesFruitFromDatabase() throws Exception {
        Fruit fruit = fruitRepository.save(new Fruit(null, "Pitaya", 7, supplier));

        mockMvc.perform(delete("/fruits/" + fruit.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/fruits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deleteFruit_returns404_whenFruitDoesNotExist() throws Exception {
        mockMvc.perform(delete("/fruits/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFruit_doesNotReturnBody_whenSuccessful() throws Exception {
        Fruit fruit = fruitRepository.save(new Fruit(null, "Cherry", 2, supplier));

        mockMvc.perform(delete("/fruits/" + fruit.getId()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }
}
