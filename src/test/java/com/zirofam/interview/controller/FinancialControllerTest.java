package com.zirofam.interview.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zirofam.interview.InterviewApplication;
import com.zirofam.interview.controller.dto.FinancialDto;
import com.zirofam.interview.domain.FinancialEntity;
import com.zirofam.interview.domain.enumeration.AccountingStatus;
import com.zirofam.interview.repository.FinancialRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureMockMvc
@SpringBootTest(classes = InterviewApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FinancialControllerTest {

    protected final static ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Autowired
    private FinancialRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    //Test for create entity successfully
    void create() {
        int databaseSizeBeforeCreate = repository.findAll().size();
        FinancialDto dto = FinancialDto
                .builder()
                .amount(BigDecimal.valueOf(12.5))
                .status(AccountingStatus.CREDITOR)
                .user("user1")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/create")
                .content(mapper.writeValueAsBytes(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        List<FinancialEntity> entities = repository.findAll();
        assertThat(entities).hasSize(databaseSizeBeforeCreate + 1);
        FinancialEntity entity = entities.get(entities.size() - 1);
        assert entity.getAmount().compareTo(dto.getAmount()) == 0;
        assertThat(entity.getUser()).isEqualTo(dto.getUser());
        assertThat(entity.getStatus()).isEqualTo(dto.getStatus());
    }

    @Test
    @SneakyThrows
    //Test for create entity successfully
    void create2() {
        FinancialDto dto = FinancialDto
                .builder()
                .amount(BigDecimal.valueOf(12.5))
                .status(AccountingStatus.CREDITOR)
                .user("user1")
                .build();

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/create")
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        ObjectMapper objectMapper = new ObjectMapper();
        FinancialEntity financialEntity = objectMapper.readValue(result.andReturn().getResponse().getContentAsString(), FinancialEntity.class);
        assert financialEntity.getAmount().compareTo(dto.getAmount()) == 0;
        assert financialEntity.getUser().compareTo(dto.getUser()) == 0;
        assert financialEntity.getStatus().compareTo(dto.getStatus()) == 0;
    }

    @Test
    @SneakyThrows
    //Test for handle don't create entity with id
    void createWithId() {
        int databaseSizeBeforeCreate = repository.findAll().size();
        FinancialDto dto = FinancialDto
                .builder()
                .id(UUID.randomUUID().toString())
                .amount(BigDecimal.valueOf(12.5))
                .status(AccountingStatus.CREDITOR)
                .user("user1")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/create")
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()));

        List<FinancialEntity> entities = repository.findAll();
        assertThat(entities).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @SneakyThrows
    //Test for update entity successfully
    void update() {
        int databaseSizeBeforeCreate = repository.findAll().size();
        FinancialEntity financialEntity = createAnObject();
        String id = financialEntity.getId();

        FinancialDto dto = FinancialDto
                .builder()
                .id(id)
                .amount(BigDecimal.valueOf(11.5))
                .status(AccountingStatus.DEBTOR)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/update")
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        List<FinancialEntity> entities = repository.findAll();
        assertThat(entities).hasSize(databaseSizeBeforeCreate + 1);
        FinancialEntity entity = entities.get(entities.size() - 1);
        assert entity.getAmount().compareTo(dto.getAmount()) == 0;
        assert entity.getUser() == null;
        assert entity.getStatus().compareTo(dto.getStatus()) == 0;
    }

    @Test
    @SneakyThrows
    //Test for handle don't update entity without id
    void dontUpdateWithoutId() {
        int databaseSizeBeforeCreate = repository.findAll().size();
        createAnObject();

        FinancialDto dto = FinancialDto
                .builder()
                .amount(BigDecimal.valueOf(11.5))
                .status(AccountingStatus.DEBTOR)
                .user("user1")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/update")
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()));

        List<FinancialEntity> entities = repository.findAll();
        assertThat(entities).hasSize(databaseSizeBeforeCreate + 1);
        FinancialEntity entity = entities.get(entities.size() - 1);
        assert entity.getAmount().compareTo(dto.getAmount()) != 0;
    }

    @Test
    @SneakyThrows
    //Test for partial update entity successfully
    void partialUpdate() {
        FinancialDto dto1 = FinancialDto
                .builder()
                .amount(BigDecimal.valueOf(12.5))
                .status(AccountingStatus.CREDITOR)
                .user("user1")
                .build();

        ResultActions result1 = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/create")
                        .content(mapper.writeValueAsBytes(dto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        String id = new ObjectMapper().readValue(result1.andReturn().getResponse().getContentAsString(), FinancialEntity.class).getId();

        FinancialDto dto2 = FinancialDto
                .builder()
                .id(id)
                .amount(BigDecimal.valueOf(11.5))
                .build();

        ResultActions result2 = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/partialUpdate")
                        .content(mapper.writeValueAsBytes(dto2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        FinancialEntity financialEntity = new ObjectMapper().readValue(result2.andReturn().getResponse().getContentAsString(), FinancialEntity.class);

        assert financialEntity.getAmount().compareTo(dto2.getAmount()) == 0;
        assert financialEntity.getStatus().compareTo(dto1.getStatus()) == 0;
        assert financialEntity.getUser().compareTo(dto1.getUser()) == 0;
    }

    @Test
    @SneakyThrows
    //Test for delete entity successfully
    void delete() {
        int databaseSizeBeforeCreate = repository.findAll().size();
        FinancialEntity financialEntity = createAnObject();
        String id = financialEntity.getId();

        List<FinancialEntity> entities = repository.findAll();
        assertThat(entities).hasSize(databaseSizeBeforeCreate + 1);

        FinancialDto dto = FinancialDto
                .builder()
                .id(id)
                .amount(BigDecimal.valueOf(12.5))
                .status(AccountingStatus.CREDITOR)
                .user("user1")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/delete")
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        entities = repository.findAll();
        assertThat(entities).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @SneakyThrows
    //Test for handle don't delete entity without correct id
    void dontDeleteWithoutCorrectId() {
        int databaseSizeBeforeCreate = repository.findAll().size();
        FinancialEntity financialEntity = createAnObject();
        String id = financialEntity.getId();

        List<FinancialEntity> entities = repository.findAll();
        assertThat(entities).hasSize(databaseSizeBeforeCreate + 1);

        FinancialDto dto = FinancialDto
                .builder()
                .id(id.concat("1"))
                .amount(BigDecimal.valueOf(12.5))
                .status(AccountingStatus.CREDITOR)
                .user("user1")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/delete")
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus()));

        entities = repository.findAll();
        assertThat(entities).hasSize(databaseSizeBeforeCreate+1);
    }

    @Test
    @SneakyThrows
    //Test for find by id entity successfully
    void findById() {
        String id = createAnObject().getId();

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/findById")
                        .param("id", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        ObjectMapper objectMapper = new ObjectMapper();
        FinancialEntity financialEntity = objectMapper.readValue(result.andReturn().getResponse().getContentAsString(), FinancialEntity.class);
        assert financialEntity.getAmount().compareTo(BigDecimal.valueOf(12.5)) == 0;
        assert financialEntity.getUser().compareTo("user1") == 0;
        assert financialEntity.getStatus().compareTo(AccountingStatus.CREDITOR) == 0;
    }

    @Test
    @SneakyThrows
    //Test for find by user entity successfully
    void findByUser() {
        String user = "user1";

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/findByUser")
                        .param("user", user)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        ObjectMapper objectMapper = new ObjectMapper();
        List<FinancialEntity> entities = objectMapper.readValue(result.andReturn().getResponse().getContentAsString(),
                new TypeReference<List<FinancialEntity>>() {
                });

        int databaseSizeBeforeCreate = entities.size();

        FinancialDto dto1 = FinancialDto
                .builder()
                .amount(BigDecimal.valueOf(12.5))
                .status(AccountingStatus.CREDITOR)
                .user(user)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/create")
                        .content(mapper.writeValueAsBytes(dto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        FinancialDto dto2 = FinancialDto
                .builder()
                .amount(BigDecimal.valueOf(10.5))
                .status(AccountingStatus.DEBTOR)
                .user(user)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/create")
                        .content(mapper.writeValueAsBytes(dto2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/findByUser")
                        .param("user", user)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        objectMapper = new ObjectMapper();
        entities = objectMapper.readValue(result.andReturn().getResponse().getContentAsString(),
                new TypeReference<List<FinancialEntity>>() {
                });

        assertThat(entities).hasSize(databaseSizeBeforeCreate+2);
    }

    private FinancialEntity createAnObject() throws Exception {
        FinancialDto dto = FinancialDto
                .builder()
                .amount(BigDecimal.valueOf(12.5))
                .status(AccountingStatus.CREDITOR)
                .user("user1")
                .build();

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/create")
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(result.andReturn().getResponse().getContentAsString(), FinancialEntity.class);
    }
}