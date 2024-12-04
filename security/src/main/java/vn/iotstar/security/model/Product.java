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

    @Column(length = 5000, columnDefinition = "NVARCHAR(5000)")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY) // Many products can belong to one category
    @JoinColumn(name = "category_id") // Foreign key column for the relationship
    private Category category;

    private Double price;

    private int stock;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String image;

    private int discount;
    
    private Double discountPrice;
    
    private Boolean isActive;
}
