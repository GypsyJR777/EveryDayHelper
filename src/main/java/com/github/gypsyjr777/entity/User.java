package com.github.gypsyjr777.entity;

import com.github.gypsyjr777.entity.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "userTg")
public class User {
    @Id
    private Long id;

    private String firstName;

    private String lastName;

    private String userName;

    private Timestamp registered;

    private boolean isCreatTask = false;

    private boolean isDoneTask = false;

    private boolean isDelTask = false;

    private boolean isWeather = false;

    private Status position = Status.START;
}
