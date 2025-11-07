package cat.itacademy.s04.t02.n02.fruit.controllers;

import cat.itacademy.s04.t02.n02.fruit.dto.SupplierRequestDTO;
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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SupplierControllerTest {

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
    void createSupplier_returnsCreatedSupplier_whenDataIsValid() throws Exception {
        SupplierRequestDTO request = new SupplierRequestDTO("Tropical Exports", "Brazil");

        mockMvc.perform(post("/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Tropical Exports"))
                .andExpect(jsonPath("$.country").value("Brazil"));
    }

    @Test
    void createSupplier_returns400_whenNameAlreadyExists() throws Exception {
        SupplierRequestDTO supplier1 = new SupplierRequestDTO("Profruits", "Spain");
        SupplierRequestDTO supplier2 = new SupplierRequestDTO("Profruits", "France");

        mockMvc.perform(post("/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplier1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplier2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSupplier_returns400_whenNameIsBlank() throws Exception {
        SupplierRequestDTO supplier = new SupplierRequestDTO("", "Portugal");

        mockMvc.perform(post("/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplier)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSupplier_returns400_whenCountryIsBlank() throws Exception {
        SupplierRequestDTO supplier = new SupplierRequestDTO("Amazon Fruits", "");

        mockMvc.perform(post("/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplier)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllSuppliers_returnsEmptyListInitially() throws Exception {
        mockMvc.perform(get("/suppliers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllSuppliers_returnsListOfSuppliers() throws Exception {
        Supplier supplier1 = new Supplier(null, "FreshFarm", "Spain");
        Supplier supplier2 = new Supplier(null, "GreenWorld", "Italy");
        supplierRepository.saveAll(List.of(supplier1, supplier2));

        mockMvc.perform(get("/suppliers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("FreshFarm"))
                .andExpect(jsonPath("$[1].name").value("GreenWorld"));
    }

    @Test
    void updateSupplier_returns200_whenDataIsValid() throws Exception {
        Supplier supplier = supplierRepository.save(new Supplier(null, "FruitWorld", "China"));

        SupplierRequestDTO update = new SupplierRequestDTO("FruitWorld Japan", "Japan");

        mockMvc.perform(put("/suppliers/" + supplier.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(supplier.getId()))
                .andExpect(jsonPath("$.name").value("FruitWorld Japan"))
                .andExpect(jsonPath("$.country").value("Japan"));
    }

    @Test
    void updateSupplier_returns404_whenSupplierDoesNotExist() throws Exception {
        SupplierRequestDTO update = new SupplierRequestDTO("NonExistent", "Germany");

        mockMvc.perform(put("/suppliers/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateSupplier_returns400_whenNameAlreadyExists() throws Exception {
        Supplier s1 = supplierRepository.save(new Supplier(null, "AlphaFruits", "Spain"));
        Supplier s2 = supplierRepository.save(new Supplier(null, "BetaFruits", "France"));

        SupplierRequestDTO update = new SupplierRequestDTO("AlphaFruits", "France");

        mockMvc.perform(put("/suppliers/" + s2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSupplier_returns400_whenNameIsBlank() throws Exception {
        Supplier supplier = supplierRepository.save(new Supplier(null, "FruitKing", "Spain"));

        SupplierRequestDTO update = new SupplierRequestDTO("  ", "Portugal");

        mockMvc.perform(put("/suppliers/" + supplier.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSupplier_returns400_whenCountryIsBlank() throws Exception {
        Supplier supplier = supplierRepository.save(new Supplier(null, "FruitLand", "Italy"));

        SupplierRequestDTO update = new SupplierRequestDTO("FruitLand", "   ");

        mockMvc.perform(put("/suppliers/" + supplier.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteSupplier_returns204_whenSupplierExistsWithoutFruits() throws Exception {
        Supplier supplier = supplierRepository.save(new Supplier(null, "DeleteMe", "Spain"));

        mockMvc.perform(delete("/suppliers/" + supplier.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteSupplier_returns404_whenSupplierDoesNotExist() throws Exception {
        mockMvc.perform(delete("/suppliers/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSupplier_returns400_whenSupplierHasAssociatedFruits() throws Exception {
        Supplier supplier = supplierRepository.save(new Supplier(null, "Fruitful", "Brazil"));
        fruitRepository.save(new Fruit(null, "Banana", 10, supplier));

        mockMvc.perform(delete("/suppliers/" + supplier.getId()))
                .andExpect(status().isBadRequest());
    }

}
