package de.nutboyz.nutsmoothie.commons;

/**
 * Task class.
 * @author Mats
 */
public class Task {

    private int id;
    private String name;
    private int reminderRange;

    public Task() {

    }

    public Task(String name, int reminderRange) {
        this.name = name;
        this.reminderRange = reminderRange;
    }

    public int getReminderRange() {
        return reminderRange;
    }

    public void setReminderRange(int reminderRange) {
        this.reminderRange = reminderRange;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name: ");
        sb.append(name);
        sb.append(", reminder range: ");
        sb.append(reminderRange);
        sb.append(", taskid: ");
        sb.append(id);
        return sb.toString();
    }
}