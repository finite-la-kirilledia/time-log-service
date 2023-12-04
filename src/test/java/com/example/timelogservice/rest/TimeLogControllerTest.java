package com.example.timelogservice.rest;

import com.example.timelogservice.TimeLogServiceApplicationTests;
import com.example.timelogservice.config.WireMockConfig;
import com.example.timelogservice.entity.TimeLog;
import com.example.timelogservice.repository.TimeLogRepository;
import com.example.timelogservice.service.TimeLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {WireMockConfig.class})
@ActiveProfiles(value = "test-gen-disable")
public class TimeLogControllerTest extends TimeLogServiceApplicationTests {

    @Autowired
    public WireMockServer wireMockServer;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private TimeLogRepository timeLogRepository;

    @Autowired
    private TimeLogService timeLogService;

    private MockMvc mvc;
    private final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @BeforeAll
    public void setup() {
        setupMockResponse(wireMockServer);
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    public void setupMockResponse(WireMockServer mockService) {
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("")));
    }

    @Test
    public void getAll() throws Exception {
        var arrSize = 100;
        generateTestData(arrSize);
        var result = mvc.perform(get("/api/v1/time-log?page=0&size=10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        var dtoResult = result.getResponse().getContentAsString();
        var serviceResult = objectMapper.writeValueAsString(
                timeLogService.getAll(PageRequest.of(0, 10))
        );
        assertEquals(dtoResult, serviceResult);
    }

    private void generateTestData(Integer size) {
        var dateToSave = LocalDateTime.now();
        for (int i = 0; i < size; i++) {
            timeLogRepository.save(new TimeLog(dateToSave));
            dateToSave = dateToSave.plusSeconds(1);
        }
    }
}
