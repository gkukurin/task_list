package co.kukurin.tasklist.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table TASKS.
 */
public class Tasks {

    private Long id;
    /** Not-null value. */
    private String title;
    /** Not-null value. */
    private String description;
    private java.util.Date date_due;
    /** Not-null value. */
    private java.util.Date date_created;
    private boolean completed;
    private Byte priority;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Tasks() {
    }

    public Tasks(Long id) {
        this.id = id;
    }

    public Tasks(Long id, String title, String description, java.util.Date date_due, java.util.Date date_created, boolean completed, Byte priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date_due = date_due;
        this.date_created = date_created;
        this.completed = completed;
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getTitle() {
        return title;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setTitle(String title) {
        this.title = title;
    }

    /** Not-null value. */
    public String getDescription() {
        return description;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDescription(String description) {
        this.description = description;
    }

    public java.util.Date getDate_due() {
        return date_due;
    }

    public void setDate_due(java.util.Date date_due) {
        this.date_due = date_due;
    }

    /** Not-null value. */
    public java.util.Date getDate_created() {
        return date_created;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDate_created(java.util.Date date_created) {
        this.date_created = date_created;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Byte getPriority() {
        return priority;
    }

    public void setPriority(Byte priority) {
        this.priority = priority;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}