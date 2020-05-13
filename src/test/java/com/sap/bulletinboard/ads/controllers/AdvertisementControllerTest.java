package com.sap.bulletinboard.ads.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.bulletinboard.ads.config.WebAppContextConfig;
import com.sap.bulletinboard.ads.models.Advertisement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebAppContextConfig.class})
@WebAppConfiguration
public class AdvertisementControllerTest {

    private static final String LOCATION = "Location";
    private static final String SOME_TITLE = "MyNewAdvertisement";

    @Autowired
    WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void create() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(buildPostRequest(SOME_TITLE))
                .andExpect(status().isCreated())
                .andExpect(header().string(LOCATION, is(not(""))))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.title", is(SOME_TITLE)))
                .andReturn().getResponse();

        mockMvc.perform(get(response.getHeader(LOCATION)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(SOME_TITLE)));
    }

    @Test
    public void readAll() throws Exception {
        mockMvc.perform(buildPostRequest(SOME_TITLE))
                .andExpect(status().isCreated());

        mockMvc.perform(buildGetRequest(""))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.advertisements.length()", is(both(greaterThan(0)).and(lessThan(10)))));
    }

    @Test
    public void readByIdNotFound() throws Exception {
        mockMvc.perform(buildGetRequest("1024"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void readById() throws Exception {
        String id = buildAndGetId();
        mockMvc.perform(buildGetRequest(id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.title", is(SOME_TITLE)));
    }

    private MockHttpServletRequestBuilder buildPostRequest(String title) throws JsonProcessingException {
        Advertisement advertisement = new Advertisement();
        advertisement.setTitle(title);

        return post(AdvertisementController.PATH).content(toJson(advertisement)).contentType(APPLICATION_JSON_UTF8);
    }

    private MockHttpServletRequestBuilder buildGetRequest(String id) {
        return get(AdvertisementController.PATH + "/" + id);
    }

    private String buildAndGetId() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(buildPostRequest(SOME_TITLE))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        String location = response.getHeader(LOCATION);
        return location.substring(location.lastIndexOf("/") + 1);
    }

    private String toJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}
