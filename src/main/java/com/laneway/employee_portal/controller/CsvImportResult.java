package com.laneway.employee_portal.controller;

import java.util.ArrayList;
import java.util.List;

public class CsvImportResult {

    private int totalRows;
    private int successCount;
    private int failureCount;
    private List<String> successes = new ArrayList<>();
    private List<String> failures = new ArrayList<>();

    public void addSuccess(int rowNumber, String email) {
        successCount++;
        successes.add("Row " + rowNumber + ": Successfully created employee with email " + email);
    }

    public void addFailure(int rowNumber, String reason) {
        failureCount++;
        failures.add("Row " + rowNumber + ": Failed - " + reason);
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public List<String> getSuccesses() {
        return successes;
    }

    public List<String> getFailures() {
        return failures;
    }
}