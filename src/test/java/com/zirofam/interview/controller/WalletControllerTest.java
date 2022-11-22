package com.zirofam.interview.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zirofam.interview.InterviewApplication;
import com.zirofam.interview.controller.dto.FinancialDto;
import com.zirofam.interview.domain.enumeration.AccountingStatus;
import lombok.SneakyThrows;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

@AutoConfigureMockMvc
@SpringBootTest(classes = InterviewApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WalletControllerTest {

    protected final static ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void create() {
        FinancialDto dto = FinancialDto
                .builder()
                .amount(BigDecimal.valueOf(900))
                .status(AccountingStatus.CREDITOR)
                .user("user1")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/wallet/v1/create")
                        .content(mapper.writeValueAsBytes(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/wallet/v1/findBalanceByUser")
                        .param("user", dto.getUser())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        ObjectMapper objectMapper = new ObjectMapper();
        BigDecimal balance = objectMapper.readValue(result.andReturn().getResponse().getContentAsString(),
                new TypeReference<BigDecimal>() {
                });

        //First value added to wallet (balance = 900)
        assert balance.compareTo(dto.getAmount()) == 0;

        FinancialDto dto2 = FinancialDto
                .builder()
                .amount(BigDecimal.valueOf(1200))
                .status(AccountingStatus.CREDITOR)
                .user("user1")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/wallet/v1/create")
                        .content(mapper.writeValueAsBytes(dto2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResultActions result2 = mockMvc.perform(MockMvcRequestBuilders.get("/api/wallet/v1/findBalanceByUser")
                        .param("user", dto.getUser())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        ObjectMapper objectMapper2 = new ObjectMapper();
        BigDecimal balance2 = objectMapper2.readValue(result2.andReturn().getResponse().getContentAsString(),
                new TypeReference<BigDecimal>() {
                });

        //Second value don't add to wallet (balance = 900)
        assert balance2.compareTo(dto.getAmount()) == 0;

        FinancialDto dto3 = FinancialDto
                .builder()
                .amount(BigDecimal.valueOf(950))
                .status(AccountingStatus.DEBTOR)
                .user("user1")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/wallet/v1/create")
                        .content(mapper.writeValueAsBytes(dto3))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResultActions result3 = mockMvc.perform(MockMvcRequestBuilders.get("/api/wallet/v1/findBalanceByUser")
                        .param("user", dto.getUser())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        ObjectMapper objectMapper3 = new ObjectMapper();
        BigDecimal balance3 = objectMapper3.readValue(result3.andReturn().getResponse().getContentAsString(),
                new TypeReference<BigDecimal>() {
                });

        //Third value don't add to wallet (balance = 900)
        assert balance3.compareTo(dto.getAmount()) == 0;

        FinancialDto dto4 = FinancialDto
                .builder()
                .amount(BigDecimal.valueOf(100))
                .status(AccountingStatus.CREDITOR)
                .user("user1")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/wallet/v1/create")
                        .content(mapper.writeValueAsBytes(dto4))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResultActions result4 = mockMvc.perform(MockMvcRequestBuilders.get("/api/wallet/v1/findBalanceByUser")
                        .param("user", dto.getUser())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        ObjectMapper objectMapper4 = new ObjectMapper();
        BigDecimal balance4 = objectMapper4.readValue(result4.andReturn().getResponse().getContentAsString(),
                new TypeReference<BigDecimal>() {
                });

        //Fourth value don't add to wallet (balance = 900 + 100)
        assert balance4.compareTo(new BigDecimal(1000)) == 0;
    }
}
