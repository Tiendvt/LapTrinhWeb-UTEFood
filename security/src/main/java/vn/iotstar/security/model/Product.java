package vn.iotstar.security.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 500, columnDefinition = "NVARCHAR(500)")
    private String title;

    @Column(length = 4000, columnDefinition = "NVARCHAR(4000)")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "category_id") 
    private Category category;

    private Double price;

    private int stock;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String image;

    private int discount;
    
    private Double discountPrice;
    
    private Boolean isActive;
}
