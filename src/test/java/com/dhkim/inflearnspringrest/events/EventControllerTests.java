package com.dhkim.inflearnspringrest.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc; // 단위 테스트보단 크지만 웹서버를 구동안해서 빠름

    @Autowired
    ObjectMapper objectMapper;

//    @MockBean
//    EventRepository eventRepository;    // WebMvcTest 테스트이기 때문에 JPA 빈이 호출이 안됨

    @Test
    public void createEvent() throws Exception {
        // 데이터 생성
        EventDto event = EventDto.builder()
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

        // 201 반환 확인
        mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(event))) // contentType = json
                .andDo(print())
                .andExpect(jsonPath("id").value(Matchers.not(100))) // 생성되면 안되는 값
                .andExpect(status().isCreated()); // status 201
    }

    @Test
    public void badRequest() throws Exception {
        // 데이터 생성
        Event event = Event.builder()
                .id(100)        // db에서 알아서 입력해주기를 기대하는 값
                .name("Spring")
                .description("REST API development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,12,12,20,00))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,12,13,20,00))
                .beginEventDateTime(LocalDateTime.of(2018,12,14,20,00))
                .endEventDateTime(LocalDateTime.of(2018,12,15,20,00))
                .basePrice(100)
                .maxPrice(200)
                .free(true)         // price에 따라 계산되는 값
                .limitOfEnrollment(100)
                .location("가능역")
                .offline(false)     // location에 따라 계산되는 값
                .build();

        // 400 반환 확인
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON_UTF8)
                .content(objectMapper.writeValueAsString(event))) // contentType = json
                .andDo(print())
                .andExpect(status().isBadRequest()); // status 400
    }

    @Test
    public void createEvent_badRequest_empty_input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void createEvent_badRequest_wrong_input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,12,12,20,00))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,12,13,20,00))
                .beginEventDateTime(LocalDateTime.of(2018,12,4,20,00))  // 등록 날짜보다 빠름
                .endEventDateTime(LocalDateTime.of(2018,12,5,20,00))
                .basePrice(10000)           // maxPrice 보다 큼
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("가능역")
                .build();

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
        ;
    }
}
