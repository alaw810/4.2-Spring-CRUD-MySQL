package cat.itacademy.s04.t02.n02.fruit.controllers;

import cat.itacademy.s04.t02.n02.fruit.dto.FruitRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.SupplierRequestDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @BeforeEach
    void cleanDatabase() {
        fruitRepository.deleteAll();
        supplierRepository.deleteAll();
    }

    @Test
    void createFruit_returns201_whenSupplierExistsAndDataIsValid() throws Exception {
        Supplier supplier = supplierRepository.save(new Supplier(null, "FruitCorp", "Spain"));

        FruitRequestDTO request = new FruitRequestDTO("Banana", 5, supplier.getId());

        mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Banana"))
                .andExpect(jsonPath("$.weightInKilos").value(5))
                .andExpect(jsonPath("$.supplier.name").value("FruitCorp"));
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
        Supplier supplier = supplierRepository.save(new Supplier(null, "Tropics", "Brazil"));

        FruitRequestDTO request = new FruitRequestDTO("  ", 4, supplier.getId());

        mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFruit_returns400_whenWeightIsInvalid() throws Exception {
        Supplier supplier = supplierRepository.save(new Supplier(null, "SweetFruits", "Italy"));

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
}
