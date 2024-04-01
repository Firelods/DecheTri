package etu.seinksansdoozebank.dechetri.model.statistics;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Statistics {
    private int totalReports;
    private Map<String, Integer> activeUsers;
    private Map<String, Integer> wasteTypes;
    private Map<String, Integer> reportFrequencyByLocation;
    private Map<String, Integer> userDemographics;
    private Map<String, Integer> mostActiveUsers;
    private Map<String, Double> employeePerformance;

    private final String[] periods = {"Daily", "Weekly", "Monthly"};
    private final String[] wasteType = {"Plastic", "Glass", "Paper", "Hazardous"};
    private final String[] locations = {"Location1", "Location2", "Location3", "Location4"};
    private final String[] demographics = {"Age: 18-25", "Age: 26-35", "Age: 36-45", "Age: 46+"};
    private final String[] users = {"User1", "User2", "User3", "User4", "User5"};
    private final String[] employees = {"Employee1", "Employee2", "Employee3", "Employee4", "Employee5"};

    private Random random;

    public Statistics() {
        this.totalReports = 0;
        this.activeUsers = new HashMap<>();
        this.wasteTypes = new HashMap<>();
        this.reportFrequencyByLocation = new HashMap<>();
        this.userDemographics = new HashMap<>();
        this.mostActiveUsers = new HashMap<>();
        this.employeePerformance = new HashMap<>();
    }

    public void init() {
        // Generate dummy data for each statistic
        generateTotalReports();
        generateActiveUsers();
        generateWasteTypes();
        generateReportFrequencyByLocation();
        generateUserDemographics();
        generateMostActiveUsers();
        generateEmployeePerformance();
    }

    private void generateTotalReports() {
        totalReports = random.nextInt(1000) + 500; // Generate a random number between 500 and 1500
    }

    private void generateActiveUsers() {
        for (String period : periods) {
            updateActiveUsers(period, random.nextInt(500) + 100); // Generate a random number between 100 and 600
        }
    }

    private void generateWasteTypes() {
        for (String type : wasteType) {
            updateWasteTypes(type, random.nextInt(200) + 50); // Generate a random number between 50 and 250
        }
    }

    private void generateReportFrequencyByLocation() {
        for (String location : locations) {
            updateReportFrequencyByLocation(location, random.nextInt(200) + 50); // Generate a random number between 50 and 250
        }
    }

    private void generateUserDemographics() {
        for (String demographic : demographics) {
            updateUserDemographics(demographic, random.nextInt(200) + 50); // Generate a random number between 50 and 250
        }
    }

    private void generateMostActiveUsers() {
        for (String user : users) {
            updateMostActiveUsers(user, random.nextInt(100) + 20); // Generate a random number between 20 and 120
        }
    }

    private void generateEmployeePerformance() {
        for (String employee : employees) {
            updateEmployeePerformance(employee, random.nextDouble() * 5); // Generate a random score between 0 and 5
        }
    }


    // Methods to update statistics

    public void incrementTotalReports() {
        totalReports++;
    }

    public void updateActiveUsers(String period, int count) {
        activeUsers.put(period, count);
    }

    public void updateWasteTypes(String type, int count) {
        wasteTypes.put(type, count);
    }

    public void updateReportFrequencyByLocation(String location, int count) {
        reportFrequencyByLocation.put(location, count);
    }

    public void updateUserDemographics(String demographic, int count) {
        userDemographics.put(demographic, count);
    }

    public void updateMostActiveUsers(String user, int count) {
        mostActiveUsers.put(user, count);
    }

    public void updateEmployeePerformance(String employee, double score) {
        employeePerformance.put(employee, score);
    }

    // Getters and setters for statistics

    public int getTotalReports() {
        return totalReports;
    }

    public Map<String, Integer> getActiveUsers() {
        return activeUsers;
    }

    public Map<String, Integer> getWasteTypes() {
        return wasteTypes;
    }

    public Map<String, Integer> getReportFrequencyByLocation() {
        return reportFrequencyByLocation;
    }

    public Map<String, Integer> getUserDemographics() {
        return userDemographics;
    }

    public Map<String, Integer> getMostActiveUsers() {
        return mostActiveUsers;
    }

    public Map<String, Double> getEmployeePerformance() {
        return employeePerformance;
    }
}
