package com.dhkim.inflearnspringrest.events;

import com.dhkim.inflearnspringrest.common.BaseControllerTest;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class EventControllerTests extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;


//    @MockBean
//    EventRepository eventRepository;    // WebMvcTest 테스트이기 때문에 JPA 빈이 호출이 안됨

    @Test
    public void createEvent() throws Exception {
        // 데이터 생성
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 12, 12, 20, 00))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 12, 13, 20, 00))
                .beginEventDateTime(LocalDateTime.of(2018, 12, 14, 20, 00))
                .endEventDateTime(LocalDateTime.of(2018, 12, 15, 20, 00))
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
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 12, 12, 20, 00))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 12, 13, 20, 00))
                .beginEventDateTime(LocalDateTime.of(2018, 12, 14, 20, 00))
                .endEventDateTime(LocalDateTime.of(2018, 12, 15, 20, 00))
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
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 12, 12, 20, 00))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 12, 13, 20, 00))
                .beginEventDateTime(LocalDateTime.of(2018, 12, 4, 20, 00))  // 등록 날짜보다 빠름
                .endEventDateTime(LocalDateTime.of(2018, 12, 5, 20, 00))
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

    // --------------------------------------------------------
    // 이벤트 조회 부분
    // --------------------------------------------------------

    // 이벤트 10개씩 3페이지 조회해서 2번째 페이지 응답
    @Test
    public void getEventList() throws Exception {
        // Given
        IntStream.range(0, 40).forEach(this::generateEvent);

        // When & Then
        this.mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC"))
                .andDo(print())
                .andExpect(jsonPath("_embedded.eventResourceList[0].id").exists())              // 출력된 이벤트 중 하나의 id
                .andExpect(jsonPath("_embedded.eventResourceList[0]._links.self").exists())     // 출력된 이벤트 중 하나의 self link (HATEOAS)
                .andExpect(jsonPath("_links.next").exists())        // pageable 의 결과로 붙는 link 정보
                .andExpect(jsonPath("page.size").exists())          // pageable 의 결과로 붙는 page 정보
                .andExpect(jsonPath("_links.profile").exists())     // self-description
                .andDo(document("getEventList"))           // 문서화
        // TODO 문서화
        ;
    }

    // 존재하지않는 이벤트 하나 조회
    @Test
    public void getEventNotExist() throws Exception {
        // When & Then
        this.mockMvc.perform(get("/api/events/404"))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    // 존재하는 이벤트 하나 조회
    @Test
    public void getEvent() throws Exception {
        // Given
        Event event = generateEvent(100);

        // When & Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(event.getName()))   // 정확하게 조회되는지 확인
                .andExpect(jsonPath("_links.self").exists())        // HATEOAS
                .andExpect(jsonPath("_links.profile").exists())     // self-description
                .andDo(document("getEvent"))        // 문서화
        // TODO 문서화
        ;
    }


    // --------------------------------------------------------
    // 이벤트 수정 부분
    // --------------------------------------------------------

    // 존재하지 않는 이벤트 수정
    @Test
    public void updateNotExistEvent() throws Exception {
        // Given
        EventDto eventDto = EventDto.builder()
                .name("Test Event")
                .description("Test Event ")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 12, 12, 20, 00))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 12, 13, 20, 00))
                .beginEventDateTime(LocalDateTime.of(2018, 12, 14, 20, 00))
                .endEventDateTime(LocalDateTime.of(2018, 12, 15, 20, 00))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("가능역")
                .build();

        // When & Then
        this.mockMvc.perform(put("/api/events/333")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound())     // 404 응답 확인
        ;
    }

    // 로직에 맞지않는 값을 사용한 이벤트 수정
    @Test
    public void updateNotValidEvent() throws Exception {
        // Given
        Event event = generateEvent(1);
        EventDto eventDto = EventDto.builder()
                .name("Test Event")
                .description("Test Event ")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 12, 12, 20, 00))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 12, 13, 20, 00))
                .beginEventDateTime(LocalDateTime.of(2018, 12, 11, 20, 00))     // Not Valid
                .endEventDateTime(LocalDateTime.of(2018, 12, 10, 20, 00))       // Not Valid
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("가능역")
                .build();

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())     // 400 응답 확인
        ;
    }

    // 필수 값이 비어있는 이벤트 수정
    @Test
    public void updateBlinkEvent() throws Exception {
        // Given
        Event event = generateEvent(1);
        EventDto eventDto = EventDto.builder()
                .name("Test Event")
                .description("Test Event ")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 12, 12, 20, 00))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 12, 13, 20, 00))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("가능역")
                .build();

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())     // 400 응답 확인
        ;
    }

    // 성공적인 이벤트 수정
    @Test
    public void updateEvent() throws Exception {
        // Given
        Event event = generateEvent(1);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setName("Updated Event");

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())     // 정상적인 응답 확인
                .andExpect(jsonPath("name").value(eventDto.getName()))  // 수정된 이벤트 이름 확인
                .andExpect(jsonPath("_links.self").exists())            // HATEOAS
                .andExpect(jsonPath("_links.profile").exists())         // self-description
                .andDo(document("updateEvent"))     // 문서화
        // TODO 문서화
        ;

    }


    private Event generateEvent(int i) {
        Event event = Event.builder()
                .name("Test Event " + i)
                .description("this event is for testing")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 12, 12, 20, 00))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 12, 13, 20, 00))
                .beginEventDateTime(LocalDateTime.of(2018, 12, 14, 20, 00))
                .endEventDateTime(LocalDateTime.of(2018, 12, 15, 20, 00))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("가능역")
                .build();

        return this.eventRepository.save(event);
    }
}
