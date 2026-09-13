package com.webmedicalportaldemo.controller;

import com.webmedicalportaldemo.dto.LabResultDTO;
import com.webmedicalportaldemo.model.LabReport;
import com.webmedicalportaldemo.model.User;
import com.webmedicalportaldemo.service.LabReportService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LabController {

    private static final Logger log = LoggerFactory.getLogger(LabController.class);
    private final LabReportService labReportService;

    @Autowired
    public LabController(LabReportService labReportService) {
        this.labReportService = labReportService;
    }

    @GetMapping("/labDashboard")
    public String showDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            user = (User) session.getAttribute("user");
        }

        if (user == null || (!"LAB_TECH".equalsIgnoreCase(user.getRole())
                && !"LAB_TECHNICIAN".equalsIgnoreCase(user.getRole())
                && !"ADMIN".equalsIgnoreCase(user.getRole())
                && !"DOCTOR".equalsIgnoreCase(user.getRole()))) {
            log.warn("Unauthorized or unauthenticated access attempt to Lab Dashboard.");
            return "redirect:/login";
        }

        List<LabReport> labRequests = labReportService.getAllDiagnosticRequests();
        if (labRequests == null) {
            log.warn("labReportService.getAllDiagnosticRequests() returned NULL. Initializing empty list.");
            labRequests = new ArrayList<>();
        }

        log.info("Successfully fetched {} lab requests for display.", labRequests.size());

        model.addAttribute("labRequests", labRequests);
        return "lab/lab_dashboard";
    }

    @PostMapping("/updateStatus")
    public String updateSampleStatus(@RequestParam("requestId") int requestId,
                                     @RequestParam("sampleStatus") String sampleStatus,
                                     @RequestParam(value = "status", defaultValue = "IN_TESTING") String status,
                                     RedirectAttributes redirectAttributes) {

        boolean updated = labReportService.updateSampleStatus(requestId, sampleStatus, status);
        if (updated) {
            // Changed to addAttribute so it appears as ?msg=status_updated in the URL for your JSP <c:if> logic
            redirectAttributes.addAttribute("msg", "status_updated");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to update sample status.");
        }

        return "redirect:/lab/dashboard";
    }

    @PostMapping("/submitResults")
    public String submitResults(@ModelAttribute LabResultDTO resultDTO,
                                RedirectAttributes redirectAttributes) {
        String filePath = null;
        MultipartFile file = resultDTO.getReportFile();

        if (file != null && !file.isEmpty()) {
            try {
                String uploadDir = "uploads/lab_reports/";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path destination = Paths.get(uploadDir, filename);
                Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
                filePath = destination.toString();
            } catch (IOException e) {
                log.error("File upload error: ", e);
                redirectAttributes.addFlashAttribute("error", "Failed to upload attached report file.");
                return "redirect:/lab/dashboard";
            }
        }

        boolean submitted = labReportService.submitLabResults(
                resultDTO.getRequestId(),
                resultDTO.getResultsSummary(),
                filePath
        );

        if (submitted) {
            // Changed to addAttribute so it appears as ?msg=results_submitted in the URL for your JSP <c:if> logic
            redirectAttributes.addAttribute("msg", "results_submitted");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to record lab test results.");
        }

        return "redirect:/lab/dashboard";
    }
}