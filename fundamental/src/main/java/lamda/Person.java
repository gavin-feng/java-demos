package lamda;

import java.util.ArrayList;
import java.util.List;

public class Person {

    public enum Sex {
        MALE, FEMALE
    }

    String name;
    Sex gender;
    String emailAddress;
    int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(Sex gender) {
        this.gender = gender;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public Sex getGender() {
        return gender;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void printPerson() {
        System.out.println("name: " + name + " | age: " + age + " | gender: " + gender);
    }

    public static List<Person> createRoster() {
        List<Person> roster = new ArrayList<>();
        Person person = new Person();
        person.setName("Ada");
        person.setAge(10);
        person.setGender(Sex.FEMALE);
        person.setEmailAddress(person.getName()+"@google.com");
        roster.add(person);

        person = new Person();
        person.setName("Ada");
        person.setAge(10);
        person.setGender(Sex.FEMALE);
        person.setEmailAddress(person.getName()+"@google.com");
        roster.add(person);

        person = new Person();
        person.setName("Beta");
        person.setAge(14);
        person.setGender(Sex.MALE);
        person.setEmailAddress(person.getName()+"@google.com");
        roster.add(person);

        person = new Person();
        person.setName("Cris");
        person.setAge(15);
        person.setGender(Sex.FEMALE);
        person.setEmailAddress(person.getName()+"@google.com");
        roster.add(person);

        person = new Person();
        person.setName("Daryle");
        person.setAge(19);
        person.setGender(Sex.MALE);
        person.setEmailAddress(person.getName()+"@google.com");
        roster.add(person);

        person = new Person();
        person.setName("Emy");
        person.setAge(24);
        person.setGender(Sex.FEMALE);
        person.setEmailAddress(person.getName()+"@google.com");
        roster.add(person);

        person = new Person();
        person.setName("Fury");
        person.setAge(26);
        person.setGender(Sex.MALE);
        person.setEmailAddress(person.getName()+"@google.com");
        roster.add(person);

        person = new Person();
        person.setName("Geoge");
        person.setAge(40);
        person.setGender(Sex.MALE);
        person.setEmailAddress(person.getName()+"@google.com");
        roster.add(person);

        return roster;
    }
}
