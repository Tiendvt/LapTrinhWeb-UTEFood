package vn.iotstar.security.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Review {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private ProductOrder order;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String comment;

    @ElementCollection
    private List<String> fileUrls = new ArrayList<>(); // Lưu danh sách URL file đã tải lên
 // Hàm thêm đường dẫn file vào danh sách
    public void addFile(String fileUrl) {
        this.fileUrls.add(fileUrl);
    }
    @Column(nullable = false)
    private Integer rating; // Rating từ 1 đến 5

}
