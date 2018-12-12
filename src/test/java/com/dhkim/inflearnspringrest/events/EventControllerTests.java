package com.dhkim.inflearnspringrest.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc; // 단위 테스트보단 크지만 웹서버를 구동안해서 빠름

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    EventRepository eventRepository;    // WebMvcTest 테스트이기 때문에 JPA 빈이 호출이 안됨

    @Test
    public void createEvent() throws Exception {
        Event event = Event.builder()
                .name("Spring")
                .description("REST API development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,12,12,20,00))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,12,13,20,00))
                .beginEventDateTime(LocalDateTime.of(2018,12,14,20,00))
                .endEventDateTime(LocalDateTime.of(2018,12,15,20,00))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("가능역")
                .build();

        event.setId(10);
        Mockito.when(eventRepository.save(event)).thenReturn(event); //  실제 EventRepository 가 아니라 MockBean을 가져오기 때문에

        mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(event))) // contentType = json
                .andDo(print())
                .andExpect(status().isCreated()); // status 201
    }


}
