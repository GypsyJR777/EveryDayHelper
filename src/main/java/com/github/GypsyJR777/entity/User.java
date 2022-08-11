package com.github.GypsyJR777.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "user")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getRegistered() {
        return registered;
    }

    public void setRegistered(Timestamp registeredAt) {
        this.registered = registeredAt;
    }

    public boolean isCreatTask() {
        return isCreatTask;
    }

    public void setCreatTask(boolean creatTask) {
        isCreatTask = creatTask;
    }

    public boolean isDoneTask() {
        return isDoneTask;
    }

    public void setDoneTask(boolean doneTask) {
        isDoneTask = doneTask;
    }

    public boolean isDelTask() {
        return isDelTask;
    }

    public void setDelTask(boolean delTask) {
        isDelTask = delTask;
    }
}
