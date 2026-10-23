package com.jobtracker;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class ApplicationController {
    private final ApplicationTracker tracker;
    public ApplicationController(ApplicationTracker tracker) { this.tracker = tracker; }

    @GetMapping("/applications")
    public List<Application> list(@RequestParam(required=false) String company) {
        return (company == null || company.isBlank()) ? tracker.listApplications() : tracker.search(company);
    }
    @PostMapping("/applications")
    @ResponseStatus(HttpStatus.CREATED)
    public Application add(@RequestBody Map<String,String> body) {
        try { return tracker.addApplication(body.get("companyName"), body.get("position"),
                body.get("status"), body.get("applicationDate"), body.get("notes")); }
        catch (IllegalArgumentException e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage()); }
    }
    @PutMapping("/applications/{id}")
    public Application update(@PathVariable int id, @RequestBody Map<String,String> body) {
        try { return tracker.updateApplication(id, body.get("companyName"), body.get("position"),
                body.get("status"), body.get("applicationDate"), body.get("notes")); }
        catch (NoSuchElementException e) { throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage()); }
        catch (IllegalArgumentException e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage()); }
    }
    @DeleteMapping("/applications/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        try { tracker.deleteApplication(id); }
        catch (NoSuchElementException e) { throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage()); }
    }
    @GetMapping("/logs")
    public List<String> logs() { return tracker.getLogs(); }
}
