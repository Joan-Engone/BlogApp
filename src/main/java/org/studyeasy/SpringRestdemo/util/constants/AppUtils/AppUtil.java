package org.studyeasy.SpringRestdemo.util.constants.AppUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;


public class AppUtil {

    private static final String UPLOAD_BASE_DIR = "uploads"; // Outside classpath

    public static String get_photo_upload_path(String fileName, String folder_name, long album_id) throws IOException {
        String basePath = UPLOAD_BASE_DIR + File.separator + album_id + File.separator + folder_name;
        File uploadDir = new File(basePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        return new File(uploadDir, fileName).getAbsolutePath();
    }

    public static BufferedImage getThumbail(MultipartFile originalFile, Integer width) throws IOException {
        BufferedImage img = ImageIO.read(originalFile.getInputStream());
        return Scalr.resize(img, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, width, Scalr.OP_ANTIALIAS);
    }

    public static Resource getFileAsResource(long album_id, String folder_name, String file_name) throws IOException {
        String fullPath = UPLOAD_BASE_DIR + File.separator + album_id + File.separator + folder_name + File.separator + file_name;
        File file = new File(fullPath);
        if (file.exists()) {
            Path path = file.toPath();
            return new UrlResource(path.toUri());
        } else {
            return null;
        }
    }

     public static void deletePhotoFromPath(String folderName, String fileName, long albumId) throws IOException {
        String absolutePath = get_photo_upload_path(fileName, folderName, albumId);
        Path path = Paths.get(absolutePath);
        Files.deleteIfExists(path);
    }
    
}
