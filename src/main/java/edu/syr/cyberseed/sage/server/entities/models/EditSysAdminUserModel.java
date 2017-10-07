package edu.syr.cyberseed.sage.server.entities.models;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
@Data
public class EditSysAdminUserModel {

    @NotNull
    @Size(min = 1, max = 255)
    @Pattern(regexp = "^[A-Za-z0-9]*$")
    private String username;


    @Size(min = 15, max = 255)
    private String password;


    @Size(min = 1, max = 255)
    private String fname;


    @Size(min = 1, max = 255)
    private String lname;

}
