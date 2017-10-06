/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.syr.cyberseed.sage.server.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author DhruvVerma
 */
@Entity
@Table(name = "record_testresult")
public class TestResultRecord {

    @Id
    @Column(name = "id")
    private long id;
    
    @Column(name = "doctor")
    private String doctor;
    
      @Column(name = "lab")
    private String lab;
      
       @Column(name = "notes")
    private String notes;

    @Column(name = "date")
    private Date date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getLab() {
        return lab;
    }

    public void setLab(String lab) {
        this.lab = lab;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    public TestResultRecord(Integer id, String doctor, String lab, String notes, Date date) {
        this.id = id;
        this.doctor = doctor;
        this.lab = lab;
        this.notes = notes;
        this.date=date;
    }
}