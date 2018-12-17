package com.dhkim.inflearnspringrest.events;

import com.dhkim.inflearnspringrest.common.RestDocsConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
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
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(status().isCreated()) // status 201
//                .andExpect(jsonPath("_links.self").exists())      // 문서화에서 테스트 실행
//                .andExpect(jsonPath("_links.query-events").exists())
//                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update event"),
                                linkWithRel("profile").description("link to profile")
                                ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contents type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin event of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end event of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment of new event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        relaxedResponseFields(  // 모든 필드에 대해 문서화 할 필요 없다.(_links 는 미리 되어있음)
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin event of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end event of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment of new event"),
                                fieldWithPath("offline").description("it tell if this event is offline or not"),
                                fieldWithPath("free").description("it tell if this event is free or not"),
                                fieldWithPath("eventStatus").description("event's status")
                        )
                ))
        ;
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
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }
}
