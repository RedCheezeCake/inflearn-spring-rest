package com.dhkim.inflearnspringrest.events;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

// AllArgsConstructor 와 NoArgsConstructor 로 기본 생성자 자동 생성
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
@Entity
public class Event {

    @Id @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // optional 없으면 온라인 모임
    private int basePrice; // optional
    private int maxPrice;
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Enumerated(value = EnumType.STRING)    //ORIDNAL : 값 순서에 따라 0, 1, ...
    private EventStatus eventStatus = EventStatus.DRAFT;

    public void update() {
        if(basePrice == 0 && maxPrice == 0) {
            free = true;
        } else {
            free = false;
        }

        if(location == null || location.isEmpty()) {
            offline = false;
        } else {
            offline = true;
        }
    }
}
