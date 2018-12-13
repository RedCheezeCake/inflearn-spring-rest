package com.dhkim.inflearnspringrest.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors) {
        if(eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() > 0) {
            errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong");
            errors.rejectValue("maxPrice", "wrongValue", "MaxPrice is wrong");
        }

        LocalDateTime beginEnrollmentDateTime = eventDto.getBeginEnrollmentDateTime();
        LocalDateTime closeEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();
        LocalDateTime beginEventDateTime = eventDto.getBeginEventDateTime();
        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if(endEventDateTime.isBefore(beginEnrollmentDateTime) ||
            endEventDateTime.isBefore(closeEnrollmentDateTime) ||
            endEventDateTime.isBefore(beginEventDateTime)) {
            errors.rejectValue("endEventDateTime", "wrongValue","endEventDateTime is wrong");
        }

        // TODO beginEnrollmentDateTime

        // TODO closeEnrollmentDateTime

        // TODO beginEventDateTime
    }
}
