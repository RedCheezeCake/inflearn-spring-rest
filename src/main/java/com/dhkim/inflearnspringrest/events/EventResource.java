package com.dhkim.inflearnspringrest.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

// ObjectMapper 가 BeanSerializer 로 serialize
public class EventResource extends ResourceSupport {

    @JsonUnwrapped  // event 프로퍼티들을 event 에서 꺼내줌
    private Event event;

    public EventResource(Event event) {
        this.event = event;
//        add(new Link("http://localhost:8080/api/events/"+event.getId()));
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());  // type safe
    }

    public Event getEvent() {
        return event;
    }
}
