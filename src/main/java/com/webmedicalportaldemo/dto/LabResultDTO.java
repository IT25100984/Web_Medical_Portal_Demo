package com.webmedicalportaldemo.dto;

import org.springframework.web.multipart.MultipartFile;

public class LabResultDTO {

    private int requestId;
    private String resultsSummary;
    private MultipartFile reportFile;

    public LabResultDTO() {
    }

    public LabResultDTO(int requestId, String resultsSummary, MultipartFile reportFile) {
        this.requestId = requestId;
        this.resultsSummary = resultsSummary;
        this.reportFile = reportFile;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getResultsSummary() {
        return resultsSummary;
    }

    public void setResultsSummary(String resultsSummary) {
        this.resultsSummary = resultsSummary;
    }

    public MultipartFile getReportFile() {
        return reportFile;
    }

    public void setReportFile(MultipartFile reportFile) {
        this.reportFile = reportFile;
    }
}