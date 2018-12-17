package com.dhkim.inflearnspringrest.events;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

// HATEOAS 의 레퍼런스에 나오지 않은 내용으로 해봅니다.
// @JsonUnwrapped 가 적용됩니다.
public class EventResourceNotRef extends Resource<Event> {

    public EventResourceNotRef(Event content, Link... links) {
        super(content, links);
    }
}
