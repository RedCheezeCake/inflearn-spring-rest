package com.dhkim.inflearnspringrest.events;

import com.dhkim.inflearnspringrest.common.ErrorResource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)   // produces : 반환값에 타입 설정
public class EventController {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EventValidator eventValidator;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        // @Valid 체크 후 에러 처리
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        // custom validator
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        // ModelMapper 사용하지 않는다면 일일히 옮겨줘야 됌!
//        Event event = Event.builder()
//                .name(eventDto.getName())
//                .description(eventDto.getDescription())
//                .build();
        // modelMapper 로 맵핑
        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = eventRepository.save(event);

        ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createURI = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event); // EventResourceNotRef 로 적용해도 동일
//        eventResource.add(selfLinkBuilder.withSelfRel());   // EventResource 생성할 때 붙힐 수 있음
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event")); // another method with self rel
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));

        return ResponseEntity.created(createURI).body(eventResource);
    }

    @GetMapping
    public ResponseEntity getEventList(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        Page<Event> eventPage = this.eventRepository.findAll(pageable);
        PagedResources pagedResources = assembler.toResource(eventPage, entity -> new EventResource(entity));
        pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));

        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getEvent(@PathVariable int id) {
        Optional<Event> eventOptional = this.eventRepository.findById(id);
        if (!eventOptional.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        Event event = eventOptional.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }


    private ResponseEntity<ErrorResource> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorResource(errors));
    }
}
