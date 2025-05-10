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

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // ID sản phẩm (khóa chính)

    @Column(length = 500, columnDefinition = "NVARCHAR(500)", nullable = false)
    private String title; // Tên sản phẩm

    @Column(length = 4000, columnDefinition = "NVARCHAR(4000)")
    private String description; // Mô tả sản phẩm

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id") // Khóa ngoại đến bảng Category
    private Category category;

    @Column(nullable = false)
    private Double price; // Giá sản phẩm

    private int stock; // Số lượng trong kho

    @Column(columnDefinition = "NVARCHAR(255)")
    private String image; // Đường dẫn ảnh sản phẩm

    @Column(nullable = false)
    private int sold = 0; // Số lượng đã bán

    @Column(nullable = false)
    private int discount = 0; // Mức giảm giá (%)

    @Column(nullable = false)
    private Double discountPrice; // Giá sau khi giảm giá

    @Column(nullable = false)
    private Boolean isActive = true; // Trạng thái sản phẩm (true = còn hoạt động)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id") // Khóa ngoại đến bảng Shop
    private Shop shop;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false, updatable = false)
    private Date createdDate; // Ngày tạo sản phẩm

    @PrePersist
    protected void onCreate() {
        this.createdDate = new Date(); // Gán ngày tạo tự động khi lưu mới
    }
}

