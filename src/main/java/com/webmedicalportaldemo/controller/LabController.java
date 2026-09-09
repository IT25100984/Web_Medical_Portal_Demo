package com.webmedicalportaldemo.controller;

import com.webmedicalportaldemo.dao.LabTestDAO;
import com.webmedicalportaldemo.model.LabTest;
import com.webmedicalportaldemo.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/lab")
public class LabController {

    private final LabTestDAO labTestDAO;

    public LabController(LabTestDAO labTestDAO) {
        this.labTestDAO = labTestDAO;
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"LAB_TECH".equals(user.getRole())) {
            return "redirect:/login";
        }

        List<LabTest> labRequests = labTestDAO.getAllLabRequests();
        model.addAttribute("labRequests", labRequests);
        return "lab/lab_dashboard";
    }

    @PostMapping("/updateStatus")
    public String updateSampleStatus(@RequestParam("requestId") int requestId,
                                     @RequestParam("sampleStatus") String sampleStatus,
                                     @RequestParam("status") String status) {
        labTestDAO.updateSampleStatus(requestId, sampleStatus, status);
        return "redirect:/lab/dashboard?msg=status_updated";
    }

    @PostMapping("/submitResults")
    public String submitResults(@RequestParam("requestId") int requestId,
                                @RequestParam("resultsSummary") String resultsSummary,
                                @RequestParam(value = "reportFile", required = false) MultipartFile file) {
        String filePath = null;

        if (file != null && !file.isEmpty()) {
            try {
                String uploadDir = "uploads/lab_reports/";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                filePath = uploadDir + filename;
                file.transferTo(new File(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        labTestDAO.submitLabResults(requestId, resultsSummary, filePath);
        return "redirect:/lab/dashboard?msg=results_submitted";
    }
}