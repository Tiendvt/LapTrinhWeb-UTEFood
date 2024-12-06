package vn.iotstar.security.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String name;

    @Column(columnDefinition = "NVARCHAR(15)")
    private String mobileNumber;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String email;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String address;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String city;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String province;

    @Column(columnDefinition = "NVARCHAR(50)")
    private String pincode;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String password;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String profileImage;

    @Column(columnDefinition = "NVARCHAR(50)")
    private String role;

    private Boolean isEnable;

    private Boolean accountNonLocked;

    private Integer failedAttempt;

    private Date lockTime;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String resetToken;
}
