package com.jobtracker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ApplicationTracker {
    private final List<Application> applications = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Path dataFile = Paths.get("data", "applications.json");
    private final Path logFile = Paths.get("data", "application_logs.txt");
    private int nextId = 1;

    @PostConstruct
    public void loadSavedData() {
        try {
            Files.createDirectories(dataFile.getParent());
            if (Files.exists(dataFile)) {
                List<Map<String,Object>> rows = mapper.readValue(dataFile.toFile(), new TypeReference<>() {});
                for (Map<String,Object> r : rows) {
                    int id = ((Number)r.get("applicationId")).intValue();
                    Application a = new Application(id, (String)r.get("companyName"), (String)r.get("position"),
                        (String)r.get("status"), (String)r.get("applicationDate"), (String)r.get("notes"));
                    applications.add(a); nextId = Math.max(nextId, id + 1);
                }
            }
        } catch (Exception e) { System.err.println("Could not load saved applications: " + e.getMessage()); }
    }
    private synchronized void persist() {
        try {
            Files.createDirectories(dataFile.getParent());
            List<Map<String,Object>> rows = new ArrayList<>();
            for (Application a : applications) {
                Map<String,Object> r = new LinkedHashMap<>();
                r.put("applicationId",a.getApplicationId()); r.put("companyName",a.getCompanyName());
                r.put("position",a.getPosition()); r.put("status",a.getStatus());
                r.put("applicationDate",a.getApplicationDate()); r.put("notes",a.getNotes()); rows.add(r);
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(dataFile.toFile(), rows);
        } catch (IOException e) { throw new RuntimeException("Could not save applications", e); }
    }
    private void logActivity(String action) {
        try {
            Files.createDirectories(logFile.getParent());
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Files.writeString(logFile, timestamp + " - " + action + System.lineSeparator(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) { System.err.println("Could not write activity log: " + e.getMessage()); }
    }
    public synchronized List<Application> listApplications() { return new ArrayList<>(applications); }
    public synchronized Application addApplication(String company, String position, String status, String date, String notes) {
        if (company == null || company.isBlank() || position == null || position.isBlank())
            throw new IllegalArgumentException("Company name and position are required.");
        Application a = new Application(nextId++, company.trim(), position.trim(), status, date, notes);
        applications.add(a); persist(); logActivity("Added application ID " + a.getApplicationId() + " for company " + a.getCompanyName());
        return a;
    }
    public synchronized Application updateApplication(int id, String company, String position, String status, String date, String notes) {
        Application old = applications.stream().filter(a -> a.getApplicationId() == id).findFirst()
                .orElseThrow(() -> new NoSuchElementException("Application ID not found."));
        if (company == null || company.isBlank() || position == null || position.isBlank())
            throw new IllegalArgumentException("Company name and position are required.");
        // Replace record because companyName and position are final in the original OOP design.
        Application updated = new Application(id, company.trim(), position.trim(), status, date, notes);
        int index = applications.indexOf(old); applications.set(index, updated);
        persist(); logActivity("Updated application ID " + id); return updated;
    }
    public synchronized void deleteApplication(int id) {
        Application a = applications.stream().filter(x -> x.getApplicationId() == id).findFirst()
                .orElseThrow(() -> new NoSuchElementException("Application ID not found."));
        applications.remove(a); persist(); logActivity("Removed application ID " + id);
    }
    public synchronized List<Application> search(String company) {
        String q = company == null ? "" : company.toLowerCase(Locale.ROOT);
        return applications.stream().filter(a -> a.getCompanyName().toLowerCase(Locale.ROOT).contains(q)).toList();
    }
    public synchronized List<String> getLogs() {
        try {
            if (!Files.exists(logFile)) return List.of();
            List<String> lines = new ArrayList<>(Files.readAllLines(logFile));
            Collections.reverse(lines);
            return lines.stream().limit(20).toList();
        } catch (IOException e) { return List.of(); }
    }
}
