package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Symptom;
import org.jetbrains.annotations.NotNull;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;
import org.swrlapi.sqwrl.values.SQWRLResultValue;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class QueryController extends Controller {

    private HashMap<String, Double> grade = new HashMap<>();
    private TreeMap<String, Double> sortedMap = new TreeMap<>();
    private LinkedHashMap<String, Double> sortedEntries = new LinkedHashMap<>();
    private AWSTextController awsHandler = new AWSTextController();
    private ResponseController responseController;
    private SymptomsManagerController symptomsManagerController;
    private List<Symptom> listOfSymptoms;

    public QueryController() {
        symptomsManagerController = SymptomsManagerController.getInstance();

        //Update existing local ontology if there is a new version available
//        try {
//            updateLocalOntology();
//        } catch (OWLOntologyCreationException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (OWLOntologyStorageException e) {
//            e.printStackTrace();
//        }

    }

    /***
     * Update local ontology if there is any update in S3 bucket
     * @throws OWLOntologyCreationException
     * @throws IOException
     * @throws OWLOntologyStorageException
     */
    private void updateLocalOntology() throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
//// FIXME: 2019-05-31 add this method into thread
        //Create OWLOntology instance using the OWLAPI
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        IRI remoteOnto = IRI.create("https://s3.us-east-2.amazonaws.com/dsontology/humandiseasesymptoms_mapped_2.owl?versionId=Z97XEamOwBIp5f6BtMzr3PIiPVaSfl13");

        //Load remote ontology
        OWLOntology remoteOntology = ontologyManager.loadOntology(remoteOnto);

        //Specify the file name and path here
        File localFile = new File("app/data/disease_ontology.owl");

        /* This logic will make sure that the file
         * gets created if it is not present at the
         * specified location*/
        if (!localFile.exists()) {
            localFile.createNewFile();

            //Save to a local file
            ontologyManager.saveOntology(remoteOntology, new FunctionalSyntaxDocumentFormat(),
                    new FileOutputStream(localFile)
            );

        }
        //Remove remote ontology
        ontologyManager.removeOntology(remoteOntology);

    }


    /***
     * This method will return suggested consultant variation for the particular disease
     * @param disease
     * @return Consultant type
     * @throws OWLOntologyCreationException
     * @throws SQWRLException
     * @throws SWRLParseException
     */
    public String getSpecialist(String disease) throws OWLOntologyCreationException, SQWRLException, SWRLParseException {
        String specialist = null;

        //Create OWLOntology instance using OWLAPI
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();

        File localFile = new File("app/data/disease_ontology.owl");

        //Load local ontology
        OWLOntology localOntology = ontologyManager.loadOntology(IRI.create(localFile));

        // Create SQWRL query engine using the SWRLAPI
        SQWRLQueryEngine queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(localOntology);

        // Build Query
        String DOCQUERY = "dsm:disease(dsm:" + disease
                + ") ^ dsm:consultation_by(dsm:" + disease
                + ", ?doc) -> sqwrl:select(?doc) ^ sqwrl:columnNames(\"Doctor\")";

        Logger.debug(DOCQUERY);

        // Create and execute a SQWRL query using the SWRLAPI
        SQWRLResult result = queryEngine.runSQWRLQuery("Q1", DOCQUERY);


        if (result.next()) {
            String docTypeValue = result.getColumn("Doctor").get(0).toString();
            String doctorType = docTypeValue.substring(4);
            specialist = doctorType;
            Logger.debug("Doctor: " + doctorType);

        }
        return specialist;
    }

    /***
     * This method will create and execute query with received symptom list
     * and check whether it needs more symptoms or if there are no more than one disease possible
     * @param sympList
     * @throws SQWRLException
     * @throws SWRLParseException
     * @throws OWLOntologyCreationException
     */
    public ObjectNode executeQuery(List<String> sympList) throws SQWRLException, SWRLParseException, OWLOntologyCreationException {

        listOfSymptoms = new ArrayList<>();
        for (String symptomName : sympList) {
            Symptom symptom = new Symptom();
            symptom.setName(symptomName);
            symptom.setHasSymptom(true);
            listOfSymptoms.add(symptom);
        }
        symptomsManagerController.setSymptomArrayList(listOfSymptoms);


        //Careate OWLOntology instance using OWLAPI
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();

        File localFile = new File("app/data/disease_ontology.owl");

        //Load local ontology
        OWLOntology localOntology = ontologyManager.loadOntology(IRI.create(localFile));

        // Create SQWRL query engine using the SWRLAPI
        SQWRLQueryEngine queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(localOntology);

        List<String> symptomQueryPhrases = new ArrayList<>();

        for (int i = 0; i < symptomsManagerController.getSymptomArrayList().size(); i++) {
            symptomQueryPhrases.add("^ dsm:has_symptom(?d, dsm:" + symptomsManagerController.getSymptomArrayList().get(i) + ") ");
        }

        //Build Query to get disease
        String QUERY = "dsm:disease(?d) "
                + String.join("", symptomQueryPhrases)
                + " -> sqwrl:select(?d) ^ sqwrl:columnNames(\"Disease\")";

        Logger.debug(QUERY);
        // Create and execute a SQWRL query using the SWRLAPI
        SQWRLResult result = queryEngine.runSQWRLQuery("Q1", QUERY);


        if (result.getNumberOfRows() > 1) { //If there are more than one possible diseases
            Logger.debug("DECISION: " + result.getNumberOfRows() + " possible diseases found. Looking for symptoms...");
            List<String> extractedSymp = new ArrayList<String>();
            HashMap<String, Double> sortedSymptoms = new HashMap<>();
            List<String> top5Significance = new ArrayList<>();


            for (int i = 0; i < result.getNumberOfRows(); i++) {
                String nameValue = result.getColumn("Disease").get(i).toString();
                String diseaseName = nameValue.substring(4);

                Logger.debug("Name: " + diseaseName);

                //Get All Symptoms for predicted diseases
                extractedSymp.addAll(getAllSymptoms(diseaseName));
            }

            //Grading all symptoms
            sortedSymptoms = gradeSymptoms(extractedSymp);

//            Iterator<Map.Entry<String, Double>> it = sortedSymptoms.entrySet().iterator();
            Logger.debug("Passed List" + symptomsManagerController.getSymptomArrayList());

            for (Map.Entry entry : sortedSymptoms.entrySet()) {
                if (symptomsManagerController.getSymptomArrayList().contains(entry.getKey())) {
                    continue;
                } else {
                    top5Significance.add(entry.getKey().toString());
                }
            }

            Logger.debug("Top5: " + top5Significance);

            // Remove least significant values
            for (int i = 0; i < top5Significance.size(); i++) {
                if (i > 5) {
                    top5Significance.remove(i);
                }
            }

            Logger.debug("Removed for top 5:" + top5Significance);

            responseController = new ResponseController();
            String test = responseController.generateResponse("symptoms", top5Significance);

            System.out.println(test);

            ObjectMapper mapper = new ObjectMapper();
            return mapper.valueToTree(test);

            // FIXME: 2019-05-30 what after getting those 5 significant diseases?


        } else if (result.getNumberOfRows() == 1) { //If there is only one possible disease
            Logger.debug("DECISION: " + result.getNumberOfRows() + " possible disease found. Looking for DOCTORS...");

            String nameValue = result.getColumn("Disease").get(0).toString();
            String diseaseName = nameValue.substring(4);

            List<String> sendDocDetailsList = new ArrayList<>();
            sendDocDetailsList.add(diseaseName);
            sendDocDetailsList.add(getSpecialist(diseaseName));

            responseController = new ResponseController();
            String test = responseController.generateResponse("doctor", sendDocDetailsList);
            System.out.println(test);

            Logger.debug("Name: " + diseaseName);
            Logger.debug("Doc is " + getSpecialist(diseaseName));


            ObjectMapper mapper = new ObjectMapper();
            return mapper.valueToTree(test);

        } else { //If there aren't any possible disease for the symptom combinations.
            Logger.debug("DECISION: " + result.getNumberOfRows() + " possible diseases found. Invoking Bayesian Network...");

            ObjectMapper mapper = new ObjectMapper();
            return mapper.valueToTree("{}");
            // TODO: 2019-05-27 Invoke Bayesian Network
        }
    }

    //Get all symptoms for a particular disease.
    public List<String> getAllSymptoms(String disease) throws OWLOntologyCreationException,
            SQWRLException, SWRLParseException {

        //List of extracted Symptoms
        List<String> sympList = new ArrayList<>();

        //Create OWLOntology instance using OWLAPI
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();

        File localFile = new File("src/data/disease_onotology.owl");

        //Load local ontology
        OWLOntology localOntology = ontologyManager.loadOntology(IRI.create(localFile));

        // Create SQWRL query engine using the SWRLAPI
        SQWRLQueryEngine queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(localOntology);

        //Query
        String QUERY = "dsm:disease(dsm:" + disease + ") ^ dsm:has_symptom(dsm:" + disease
                + ", ?sym) -> sqwrl:select(dsm:" + disease
                + ", ?sym) ^ sqwrl:columnNames(\"Disease\",\"Symptoms\")";
        Logger.debug(QUERY);
        //Run Query
        SQWRLResult result = queryEngine.runSQWRLQuery("Q5", QUERY);


        for (int i = 0; i < result.getNumberOfRows(); i++) {
            SQWRLResultValue value = result.getColumn("Symptoms").get(i);
            sympList.add(value.toString().substring(4));

        }

//        //Grade all extracted symptoms
//        gradeSymptoms(sympList);


        return sympList;
    }

    /**
     * Grading extracted symptoms to identify significance symptoms for each disease
     *
     * @param sympList
     * @return
     */
    public LinkedHashMap<String, Double> gradeSymptoms(@NotNull List<String> sympList) {

        /***
         * TreeMap blueprint as follows
         * TreeMap <String *symptom*, Double *allocatedValue*>
         */
        for (String value : sympList) {
            if (!grade.containsKey(value)) {
                grade.put(value, 0.1);
            } else {
                grade.put(value, grade.get(value) + 0.1);
            }
        }
        Logger.debug("before: " + grade);


        sortedEntries.putAll(sortHashMapByValues(grade));

        return sortedEntries;
    }

    /***
     * sort graded values in ascending order by values
     * @param passedMap HashMap to be sorted by values
     * @return sorted LinkedHashMap which has been sorted by values
     */
    public LinkedHashMap<String, Double> sortHashMapByValues(HashMap<String, Double> passedMap) {

        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Double> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();

        Iterator<Double> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Double val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                Double comp1 = passedMap.get(key);
                Double comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

}
