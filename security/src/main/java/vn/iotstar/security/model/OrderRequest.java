package vn.iotstar.security.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_request")
public class OrderRequest {
    
    @Id
    private Long id; // Assuming a primary key is required, replace or remove if not applicable

    @Column(columnDefinition = "NVARCHAR(255)")
    private String firstName;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String lastName;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String email;

    @Column(columnDefinition = "NVARCHAR(15)")
    private String mobileNo;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String address;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String city;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String province;

    @Column(columnDefinition = "NVARCHAR(15)")
    private String pincode;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String paymentType;
}
