package de.htw.sdf.photoplatform.webservice.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import de.htw.sdf.photoplatform.persistence.model.Collection;
import de.htw.sdf.photoplatform.persistence.model.CollectionImage;

/**
 * Data transfer object to get and update collection data.
 *
 * Represents the domain object Collection.
 *
 * @author Sergej Meister
 */
public class CollectionData implements Serializable {

    private long collectionId;

    @NotEmpty
    private String name;

    private String description;

    private Boolean isPublic;

    private UserData userdata;

    private ImageData thumbnail;

    private List<ImageData> images;

    /**
     * This object can be used to add images to this collection.
     * See: PhotographerController.addImageToCollection
     */
    private List<Long> imageIds;

    /**
     * Default empty constructor.
     */
    public CollectionData(){
    }

    /**
     * Default empty constructor.
     */
    public CollectionData(Collection collection){
        this(collection,Boolean.FALSE);
    }

    /**
     * Default empty constructor.
     */
    public CollectionData(Collection collection, boolean includeImages){
        this.collectionId = collection.getId();
        this.name = collection.getName();
        this.description = collection.getDescription();
        this.images = new ArrayList<>();
        this.isPublic = collection.isPublic();

        if(includeImages && collection.getCollectionImages() != null){
            for(CollectionImage collectionImage : collection.getCollectionImages()){
                ImageData image = new ImageData(collectionImage.getImage());
                images.add(image);
            }
        }

        this.userdata = new UserData(collection.getUser());
    }

    /**
     * Returns collection id.
     *
     * @return collection id.
     */
    public long getCollectionId() {
        return collectionId;
    }

    /**
     * Sets collection id.
     *
     * @param collectionId collection id.
     */
    public void setCollectionId(long collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * Returns collection name.
     *
     * @return collection name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets collection name.
     *
     * @param name collection name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns collection description.
     *
     * @return collection description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets collection description.
     *
     * @param description collections description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Is collection public.
     *
     * @return is public.
     */
    public Boolean getPublic() {
        return isPublic;
    }

    /**
     * Sets public value to collection.
     *
     * @param isPublic public value.
     */
    public void setPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    /**
     * Returns user data.
     *
     * @return user data.
     */
    public UserData getUserdata() {
        return userdata;
    }

    /**
     * Sets user data.
     *
     * @param userdata user data.
     */
    public void setUserdata(UserData userdata) {
        this.userdata = userdata;
    }

    /**
     * Returns thumbnail image of collection.
     *
     * @return image.
     */
    public ImageData getThumbnail() {
        return thumbnail;
    }

    /**
     * Sets thumbnail image of collection.
     *
     * @param thumbnail image.
     */
    public void setThumbnail(ImageData thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * Returns list of images.
     *
     * @return list of images.
     */
    public List<ImageData> getImages() {
        return images;
    }

    /**
     * Sets list of images.
     *
     * @param images list of images.
     */
    public void setImages(List<ImageData> images) {
        this.images = images;
    }

    /**
     * Returns image ids.
     * Will be used to add images to collection or to delete images from collection.
     * @return list of image ids.
     */
    public List<Long> getImageIds() {
        return imageIds;
    }

    /**
     * Set list of image ids.
     * Will be used to add images to collection or to delete images from collection.
     *
     * @param imageIds image ids.
     */
    public void setImageIds(List<Long> imageIds) {
        this.imageIds = imageIds;
    }
}