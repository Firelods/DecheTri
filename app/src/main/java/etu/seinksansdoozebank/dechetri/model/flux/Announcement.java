package etu.seinksansdoozebank.dechetri.model.flux;

import java.util.Date;

public class Announcement {

    private String id;
    private String title;
    private String description;
    private AnnouncementType type;
    private Date publicationDate;
    private Date eventDate;
    public Announcement(String id, String title, String description, AnnouncementType type, Date eventDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.publicationDate = new Date();
        this.eventDate = eventDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AnnouncementType getType() {
        return type;
    }

    public void setType(AnnouncementType type) {
        this.type = type;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }
}