package vn.iotstar.security.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.security.util.AppConstant;

@Service
public class FileStorageService  {
	
    public String storeFile(MultipartFile file) {
        try {
        	 Path uploadPath = Paths.get(AppConstant.uploadDir);
             if (!Files.exists(uploadPath)) {
                 Files.createDirectories(uploadPath); // Tạo thư mục nếu chưa tồn tại
             }

             // Đường dẫn lưu file
             Path filePath = uploadPath.resolve(file.getOriginalFilename());
             Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
             
             // Trả về đường dẫn file
             return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
