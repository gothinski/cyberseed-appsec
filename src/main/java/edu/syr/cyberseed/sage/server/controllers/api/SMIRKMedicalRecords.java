package edu.syr.cyberseed.sage.server.controllers.api;

import edu.syr.cyberseed.sage.server.entities.*;
import edu.syr.cyberseed.sage.server.entities.models.DoctorExamRecordModel;
import edu.syr.cyberseed.sage.server.repositories.*;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

@RestController
public class SMIRKMedicalRecords {

    @Autowired
    MedicalRecordRepository medicalRecordRepository;
    @Autowired
    MedicalRecordWithoutAutoIdRepository medicalRecordWithoutAutoIdRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    PermissionsRepository permissionListRepository;
    @Autowired
    DoctorExamRecordRepository doctorExamRecordRepository;

    private static final Logger logger = LoggerFactory.getLogger(SMIRKMedicalRecords.class);

    // 5.8 /addDoctorExamRecord
    @Secured({"ROLE_DOCTOR","ROLE_NURSE","ROLE_MEDICAL_ADMIN"})
    @ApiOperation(value = "Add a Doctor Exam MedicalRecord to the database.",
            notes = "When addDoctorExam MedicalRecord is successfully exercised, the result SHALL be a new Doctor Exam MedicalRecord with valid non-null values added to the database.  The addDoctorExamRecord service SHALL only be accessible to users with the Doctor, Nurse, and Medical Administrator roles.")
    @RequestMapping(value = "/addDoctorExamRecord", method = RequestMethod.POST)
    public ResultValue addDoctorExamRecord(@RequestBody @Valid DoctorExamRecordModel submittedData) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Authenticated user " + currentUser + " is starting execution of service /addDoctorExamRecord");
        String resultString = "FAILURE";

        Doctor possibleExistingDoctor = doctorRepository.findByUsername(submittedData.getDoctorUsername());
        Boolean doctorExists = (possibleExistingDoctor != null) ? true : false;
        Patient possibleExistingPatient = patientRepository.findByUsername(submittedData.getPatientUsername());
        Boolean patientExists = (possibleExistingPatient != null) ? true : false;

        if (doctorExists && patientExists) {

            // was a record id specified?
            logger.info("Submitted record id is " + submittedData.getId());
            if (submittedData.getId() != null) {
                MedicalRecord possibleExistingRecord = medicalRecordRepository.findById(submittedData.getId());
                DoctorExamRecord possibleExistingDoctorExamRecord = doctorExamRecordRepository.findById(submittedData.getId());
                Boolean recordExists = (possibleExistingRecord != null) ? true : false;
                Boolean doctorExamRecordExists = (possibleExistingDoctorExamRecord != null) ? true : false;

                if (recordExists || doctorExamRecordExists) {
                    logger.error("Cannot create doctor exam record due to recordExists=" + recordExists + " and doctorExamRecordExists=" + doctorExamRecordExists
                            + ". You cannot create *new* records with a specific id if records already exist with that id.");
                }
                else {
                    logger.info("Creating records with id " + submittedData.getId());
                    MedicalRecordWithoutAutoId savedMedicalRecord = medicalRecordWithoutAutoIdRepository.save(new MedicalRecordWithoutAutoId(submittedData.getId(),
                            "Doctor Exam",
                            new Date(),
                            currentUser,
                            submittedData.getPatientUsername(),
                            "{\"users\":[\"" + currentUser + "\"]}",
                            "{\"users\":[\"" + currentUser + "\"]}"));
                    logger.info("Created  MedicalRecord with id " + savedMedicalRecord.getId());

                    // create the Doctor exam record
                    DoctorExamRecord savedDoctorExamRecord = doctorExamRecordRepository.save(new DoctorExamRecord(submittedData.getId(),
                            submittedData.getDoctorUsername(),
                            submittedData.getExamDate(),
                            submittedData.getNotes()));
                    logger.info("Created  DoctorExamRecord with id " + savedDoctorExamRecord.getId());
                }

            }
            else {
                try {
                    // create the record
                    MedicalRecord savedMedicalRecord = medicalRecordRepository.save(new MedicalRecord("Doctor Exam",
                            new Date(),
                            currentUser,
                            submittedData.getPatientUsername(),
                            "{\"users\":[\"" +currentUser + "\"]}",
                            "{\"users\":[\"" +currentUser + "\"]}"));
                    logger.info("Created  MedicalRecord with id " + savedMedicalRecord.getId());

                    // create the Doctor exam record
                    // Use id auto assigned by db to MedicalRecord for examRecord
                    DoctorExamRecord savedDoctorExamRecord = doctorExamRecordRepository.save(new DoctorExamRecord(savedMedicalRecord.getId(),
                            submittedData.getDoctorUsername(),
                            submittedData.getExamDate(),
                            submittedData.getNotes()));
                    logger.info("Created  DoctorExamRecord with id " + savedDoctorExamRecord.getId());


                    resultString = "SUCCESS";
                    logger.info("Created doctor exam record for doctor " + submittedData.getDoctorUsername());
                } catch (Exception e) {
                    logger.error("Failure creating doctor exam record for doctor " + submittedData.getDoctorUsername());
                    e.printStackTrace();
                }
            }
        }
        else {
            logger.error("Cannot create doctor exam record due to doctorExists=" + doctorExists + " and patientExists=" +patientExists + ". Both need to exist.");
        }
        ResultValue result = new ResultValue();
        result.setResult(resultString);
        logger.info("Authenticated user " + currentUser + " completed execution of service /addDoctorExamRecord");
        return result;
    }

    // 5.15 /listRecords
    @Secured({"ROLE_USER"})
    @ApiOperation(value = "List all records the accessing user has permissions on.",
            notes = "When listRecords service is successfully exercised the server application SHALL return a list containing the Record ID, Record Type, and Record Date for all records that the accessing user is listed as either owner, on edit permissions list or view permissions list. The listRecords service SHALL be accessible to all users. \n")
    @RequestMapping(value = "/listRecords", method = RequestMethod.GET)
    public ArrayList<String> listRecords() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Authenticated user " + currentUser + " is starting execution of service /listRecords");
        String resultString = "FAILURE";

        List<MedicalRecord> recordsAsOwner = medicalRecordRepository.findByOwner(currentUser);
        List<MedicalRecord> recordsAsPatient = medicalRecordRepository.findByPatient(currentUser);
        //todo add view list
        //todo add edit list
        Set<MedicalRecord> myRecords = new HashSet<MedicalRecord>(recordsAsOwner);
        myRecords.addAll(recordsAsPatient);

        ArrayList<String> recordSummaryList = new ArrayList<String>();
        for (MedicalRecord record : myRecords) {
            String summary = record.getId() + "," + record.getRecord_type() + "," + record.getDate();
            recordSummaryList.add(summary);
        }

        logger.info("Authenticated user " + currentUser + " completed execution of service /listRecords");
        return recordSummaryList;
    }

}