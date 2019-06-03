package controllers;

import models.Symptom;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.List;

public class SymptomsManagerController extends Controller {

    private static List<Symptom> symptomArrayList = new ArrayList<>();

    private static SymptomsManagerController symptomsManagerController;

    private SymptomsManagerController() {
        this.symptomsManagerController = new SymptomsManagerController();
    }

    public static SymptomsManagerController getInstance() {
        if (symptomsManagerController == null) {
            symptomsManagerController = new SymptomsManagerController();
        }
        return symptomsManagerController;
    }

    public List<Symptom> getSymptomArrayList() {
        return symptomArrayList;
    }

    public void setSymptomArrayList(List<Symptom> symptomArrayList) {
        SymptomsManagerController.symptomArrayList.addAll(symptomArrayList);
    }

}
