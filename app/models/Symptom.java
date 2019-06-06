package models;

public class Symptom {

    private String name;
    private Boolean hasSymptom;

    public Symptom() {
    }

    public Symptom(String name, Boolean hasSymptom) {
        this.name = name;
        this.hasSymptom = hasSymptom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getHasSymptom() {
        return hasSymptom;
    }

    public void setHasSymptom(Boolean hasSymptom) {
        this.hasSymptom = hasSymptom;
    }
}
