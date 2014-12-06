/**
 *
 */
package de.htw.sdf.photoplatform.webservice.dto;

import java.io.Serializable;

import de.htw.sdf.photoplatform.persistence.model.Image;
import de.htw.sdf.photoplatform.persistence.model.UserImage;

/**
 * Data transfer object to get and update image data.
 * Represents the domain object Image.
 *
 * @author Sergej Meister
 */
public class ImageData implements Serializable {

    private String imageId;

    private String name;

    private String description;

    private double price;

    private String compression;

    private double xResolution;

    private double yResolution;

    private String resolutionUnit;

    private Integer xDemension;

    private Integer yDemension;

    private String path;

    private UserData userData;

    /**
     * Default empty constructor.
     */
    public ImageData(){
        //Default empty constructor.
    }

    /**
     * Constructor for UserImage.
     *
     * @param userImage user image.
     */
    public ImageData(UserImage userImage) {
        this(userImage.getImage());
        this.userData = new UserData(userImage.getUser());
    }

    /**
     * Constructor for Image.
     *
     * @param image user image.
     */
    public ImageData(Image image) {
        this.imageId = image.getId().toString();
        this.name = image.getName();
        this.description = image.getDescription();
        this.price = image.getPrice();
        this.compression = image.getCompression();
        this.xDemension = image.getXDemension();
        this.yDemension = image.getYDemension();
        this.xResolution = image.getXResolution();
        this.yResolution = image.getYResolution();
        this.resolutionUnit = image.getResolutionUnit();
        this.path = image.getPath();
    }

    /**
     * Returns image id.
     *
     * @return image id.
     */
    public String getImageId() {
        return imageId;
    }

    /**
     * Sets image id.
     *
     * @param imageId image id.
     */
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    /**
     * Returns image name.
     *
     * @return name of image.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets image name.
     *
     * @param name name of image.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns image description.
     *
     * @return description of image.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description image.
     *
     * @param description image description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns price of image.
     *
     * @return image price.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets image price.
     *
     * @param price image price.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Returns image compression.
     *
     * @return image compression.
     */
    public String getCompression() {
        return compression;
    }

    /**
     * Sets image compression.
     *
     * @param compression image compression.
     */
    public void setCompression(String compression) {
        this.compression = compression;
    }

    /**
     * Returns x resolution.
     *
     * @return x resolution.
     */
    public double getxResolution() {
        return xResolution;
    }

    /**
     * Sets x resolution.
     *
     * @param xResolution x resolution.
     */
    public void setxResolution(double xResolution) {
        this.xResolution = xResolution;
    }

    /**
     * Returns y resolution.
     *
     * @return y resolution.
     */
    public double getyResolution() {
        return yResolution;
    }

    /**
     * Sets y resolution.
     *
     * @param yResolution
     */
    public void setyResolution(double yResolution) {
        this.yResolution = yResolution;
    }

    /**
     * Returns resolution unit.
     *
     * @return resolution unit.
     */
    public String getResolutionUnit() {
        return resolutionUnit;
    }

    /**
     * Sets resolution unit.
     *
     * @param resolutionUnit resolution unit.
     */
    public void setResolutionUnit(String resolutionUnit) {
        this.resolutionUnit = resolutionUnit;
    }

    /**
     * Returns x demension.
     *
     * @return x demension.
     */
    public Integer getxDemension() {
        return xDemension;
    }

    /**
     * Sets x demension.
     *
     * @param xDemension x demension.
     */
    public void setxDemension(Integer xDemension) {
        this.xDemension = xDemension;
    }

    /**
     * Returns y demension.
     *
     * @return y demension.
     */
    public Integer getyDemension() {
        return yDemension;
    }

    /**
     * Sets y demension.
     *
     * @param yDemension y demension.
     */
    public void setyDemension(Integer yDemension) {
        this.yDemension = yDemension;
    }

    /**
     * Returns path.
     *
     * @return path.
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets image path.
     *
     * @param path image path.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Returns user data.
     *
     * @return user data.
     */
    public UserData getUserData() {
        return userData;
    }

    /**
     * Sets user data.
     * 
     * @param userData user data.
     */
    public void setUserData(UserData userData) {
        this.userData = userData;
    }
}