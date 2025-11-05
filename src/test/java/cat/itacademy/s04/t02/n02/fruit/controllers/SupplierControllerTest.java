package cat.itacademy.s04.t02.n02.fruit.controllers;

import cat.itacademy.s04.t02.n02.fruit.dto.SupplierRequestDTO;
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

}
