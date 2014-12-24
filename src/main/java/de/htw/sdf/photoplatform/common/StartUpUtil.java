package de.htw.sdf.photoplatform.common;

import de.htw.sdf.photoplatform.persistence.model.Image;
import de.htw.sdf.photoplatform.repository.ImageDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vs on 22.12.14.
 */
@Service
public class StartUpUtil {

    @Autowired
    ServletContext servletContext;

    @Autowired
    ImageDAO imageDAO;

    /**
     * Clean the upload directories, otherwise we will run into errors
     * in case we restart the application cause of images with duplicate path's (have the same id)
     */
    public void cleanUploadDirectories() {
        String thumbPath = servletContext.getRealPath("/") + "/img/upload/";
        String originalPath = "upload/";

        List<String> thumbResults = new ArrayList<>();
        List<String> origResults = new ArrayList<>();

        try {
            File[] thumbFiles = new File(thumbPath).listFiles();
            for (File file : thumbFiles) {
                if (file.isFile()) {
                    thumbResults.add(file.getName());
                }
            }
        } catch (NullPointerException ex) {
            // ignore
        }

        try {
            File[] origFiles = new File(originalPath).listFiles();
            for (File file : origFiles) {
                if (file.isFile()) {
                    origResults.add(file.getName());
                }
            }
        } catch (NullPointerException ex) {
            // ignore
        }

        List<Image> dbImages = imageDAO.findAll();
        List<String> dbThumbs = new ArrayList<>();
        List<String> dbOrig = new ArrayList<>();
        for (Image image : dbImages) {
            dbThumbs.add(image.getSmallThumbPath().replace("/img/upload/", ""));
            dbThumbs.add(image.getMobileThumbPath().replace("/img/upload/", ""));
            dbThumbs.add(image.getThumbPath().replace("/img/upload/", ""));
            dbOrig.add(image.getPath().replace("upload/", ""));
        }

        thumbResults.removeAll(dbThumbs);
        origResults.removeAll(dbOrig);

        for (String name : thumbResults) {
            File file = new File(thumbPath + name);
            file.delete();
        }

        for (String name : origResults) {
            File file = new File(originalPath + name);
            file.delete();
        }
    }
}
