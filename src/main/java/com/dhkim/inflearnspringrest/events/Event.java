package com.dhkim.inflearnspringrest.events;

import lombok.*;

import java.time.LocalDateTime;

// AllArgsConstructor 와 NoArgsConstructor 로 기본 생성자 자동 생성
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Event {

    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // optional 없으면 온라인 모임
    private int basePrice; // optional
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    private EventStatus eventStatus;
}
