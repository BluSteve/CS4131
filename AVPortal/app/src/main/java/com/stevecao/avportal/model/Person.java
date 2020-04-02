package com.stevecao.avportal.model;

public class Person {
    String name, number, email;

    public Person(String name, String number, String email) {
        this.name = name;
        this.number = number;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}