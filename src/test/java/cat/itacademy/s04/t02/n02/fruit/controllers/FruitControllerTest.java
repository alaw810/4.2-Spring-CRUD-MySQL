package cat.itacademy.s04.t02.n02.fruit.controllers;

import cat.itacademy.s04.t02.n02.fruit.dto.FruitRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.FruitResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.repository.FruitRepository;
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
public class FruitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FruitRepository fruitRepository;

    @BeforeEach
    void cleanDatabase() {
        fruitRepository.deleteAll();
    }

    @Test
    void addFruit_returnsCreatedFruit() throws Exception {
        FruitRequestDTO request = new FruitRequestDTO("Apple", 5);

        mockMvc.perform(post("/fruits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Apple"))
                .andExpect(jsonPath("$.weightInKilos").value(5));
    }

    @Test
    void getAllFruits_returnsListOfFruits() throws Exception {
        FruitRequestDTO fruit1 = new FruitRequestDTO("Banana", 4);
        FruitRequestDTO fruit2 = new FruitRequestDTO("Pineapple", 5);

        mockMvc.perform(post("/fruits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fruit1)));

        mockMvc.perform(post("/fruits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fruit2)));

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
        FruitRequestDTO fruit = new FruitRequestDTO("Pear", 2);

        String response = mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fruit)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readValue(response, FruitResponseDTO.class).id();

        mockMvc.perform(get("/fruits/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Pear"))
                .andExpect(jsonPath("$.weightInKilos").value(2));
    }

    @Test
    void getFruitById_returns404_whenFruitDoesNotExist() throws Exception {
        mockMvc.perform(get("/fruits/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateFruit_returnsUpdatedFruit_whenDataIsValid() throws Exception {
        FruitRequestDTO fruit = new FruitRequestDTO("Melon", 3);

        String response = mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fruit)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readValue(response, FruitResponseDTO.class).id();

        FruitRequestDTO updatedFruit = new FruitRequestDTO("Watermelon", 6);

        mockMvc.perform(put("/fruits/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedFruit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Watermelon"))
                .andExpect(jsonPath("$.weightInKilos").value(6));
    }

    @Test
    void updateFruit_returns404_whenFruitDoesNotExist() throws Exception {
        FruitRequestDTO fruit = new FruitRequestDTO("Mango", 2);

        mockMvc.perform(put("/fruits/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fruit)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateFruit_returns400_whenNameIsBlank() throws Exception {
        FruitRequestDTO fruit = new FruitRequestDTO("Orange", 5);

        String response = mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fruit)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readValue(response, FruitResponseDTO.class).id();

        FruitRequestDTO invalid = new FruitRequestDTO("", 5);

        mockMvc.perform(put("/fruits/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void updateFruit_returns400_whenWeightIsNotPositive() throws Exception {
        FruitRequestDTO fruit = new FruitRequestDTO("Peach", 8);

        String response = mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fruit)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readValue(response, FruitResponseDTO.class).id();

        FruitRequestDTO invalid = new FruitRequestDTO("Peach", -8);

        mockMvc.perform(put("/fruits/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFruit_persistsChangesInDatabase() throws Exception {
        FruitRequestDTO fruit = new FruitRequestDTO("Grapes", 1);

        String response = mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fruit)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readValue(response, FruitResponseDTO.class).id();

        FruitRequestDTO updated = new FruitRequestDTO("Apricot", 2);

        mockMvc.perform(put("/fruits/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/fruits/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Apricot"))
                .andExpect(jsonPath("$.weightInKilos").value(2));

    }

    @Test
    void deleteFruit_removesFruitFromDatabase() throws Exception {
        FruitRequestDTO fruit = new FruitRequestDTO("Pitaya", 7);

        String response = mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fruit)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readValue(response, FruitResponseDTO.class).id();

        mockMvc.perform(delete("/fruits/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/fruits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deleteFruit_returns404_whenFruitDoesNotExist() throws Exception {
        mockMvc.perform(delete("/fruits/999"))
                .andExpect((status().isNotFound()));
    }

    @Test
    void deleteFruit_doesNotReturnBody_whenSuccessful() throws Exception {
        FruitRequestDTO fruit = new FruitRequestDTO("Cherry", 2);

        String response = mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fruit)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readValue(response, FruitResponseDTO.class).id();

        mockMvc.perform(delete("/fruits/" + id))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

    }
}
