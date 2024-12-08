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
//    	if (file == null || file.isEmpty()) {
//            throw new RuntimeException("Invalid file: File is empty or null");
//        }
    	try {
            // Tạo thư mục nếu chưa tồn tại
            Path uploadPath = Paths.get(AppConstant.uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Đường dẫn lưu file
            String fileName = file.getOriginalFilename();
            //System.out.print("file name: " + fileName+"\n");
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Trả về đường dẫn tương đối để sử dụng trong giao diện
            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
