package de.nutboyz.nutsmoothie.commons;

/**
 * Task class.
 * @author Mats
 */
public class Task {

    private int id;
    private String name;
    private int reminderRange;
//    private ArrayList<Location> locations = new ArrayList<>();

    public Task() {

    }

    public Task(String name, int reminderRange) {
        this.name = name;
        this.reminderRange = reminderRange;
    }

//    public boolean addLocation (Location location) {
//        if (!locations.contains(location)) {
//            locations.add(location);
//            return true;
//        }
//        else {
//            return false;
//        }
//    }
//
//    public boolean removeLocation(Location location) {
//        if (locations.contains(location)) {
//            locations.remove(location);
//            return true;
//        }
//        else {
//            return false;
//        }
//    }

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

//    public ArrayList<Location> getLocations() {
//        return locations;
//    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name: ");
        sb.append(name);
        sb.append(", reminder range: ");
        sb.append(reminderRange);

//        for (Location loc : locations) {
//            sb.append(loc);
//        }

        sb.append(", taskid: ");
        sb.append(id);
        return sb.toString();
    }
}
