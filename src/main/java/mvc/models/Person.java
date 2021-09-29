package mvc.models;

import org.json.JSONObject;
import java.time.LocalDate;
import java.time.Period;

public class Person {
    private static int idCount = 1;

    private int id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private int age;

    public Person(int id, String firstName, String lastName, LocalDate dateOfBirth) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.age = calculateAge(dateOfBirth);
        setIdCount(++idCount);
    }

    public Person(String firstName, String lastName, LocalDate dateOfBirth) {
        this.id = idCount;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        setIdCount(++idCount);
    }

    public static int getIdCount() {
        return idCount;
    }

    public static void setIdCount(int idCount) {
        Person.idCount = idCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        setAge(calculateAge(this.dateOfBirth));
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static Person fromJSONObject(JSONObject json) {
        try {
            Person person = new Person(json.getInt("id"), json.getString("first_name"), json.getString("last_name"), LocalDate.parse(json.getString("birth_date")));
            return person;
        } catch(Exception e) {
            throw new IllegalArgumentException("Unable to parse person from provided json: " + json.toString());
        }
    }

    public static int calculateAge(LocalDate birthDate) {
        if (birthDate != null) {
            return Period.between(birthDate, LocalDate.now()).getYears();
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return firstName + ' ' + lastName;
    }
}
