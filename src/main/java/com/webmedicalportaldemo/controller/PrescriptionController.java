package com.webmedicalportaldemo.controller;

import com.webmedicalportaldemo.dao.PatientDAO;
import com.webmedicalportaldemo.dao.PrescriptionDAO;
import com.webmedicalportaldemo.dto.PrescriptionRequestDTO;
import com.webmedicalportaldemo.model.Prescription;
import com.webmedicalportaldemo.model.User;
import com.webmedicalportaldemo.service.MedFileService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class PrescriptionController {

    private final PrescriptionDAO prescriptionDAO;
    private final PatientDAO patientDAO;
    private final MedFileService medService;

    public PrescriptionController(
            PrescriptionDAO prescriptionDAO,
            PatientDAO patientDAO,
            MedFileService medService) {

        this.prescriptionDAO = prescriptionDAO;
        this.patientDAO = patientDAO;
        this.medService = medService;
    }

    /**
     * Patient view of prescriptions
     */
    @GetMapping("/orderPrescription")
    public String showPrescriptionPage(
            Model model,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"PATIENT".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }
        List<Prescription> myOrders = prescriptionDAO.getPrescriptionsByPatientuserID(user.getuserID());
        model.addAttribute("myOrders", myOrders);
        return "pharmacy/prescriptions";
    }

    /**
     * Patient submits prescription order
     */
    @PostMapping("/submitPrescription")
    public String submitPrescription(
            @RequestParam("cartData") String cartData,
            @RequestParam(value = "doctorID", required = false, defaultValue = "0") int doctorID,
            HttpSession session) {

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!"PATIENT".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/login?error=unauthorized";
        }
        if (cartData == null || cartData.isBlank()) {
            return "redirect:/orderPrescription?msg=empty";
        }

        Integer patientID = patientDAO.getPatientIDByuserID(currentUser.getuserID());
        if (patientID == null || patientID <= 0) {
            return "redirect:/orderPrescription?msg=patient_not_found";
        }

        String orderDate = LocalDate.now().toString();
        String orderTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        String[] orderedItems = cartData.split(";");

        int savedOrderCount = 0;
        int failedOrderCount = 0;

        for (String itemString : orderedItems) {
            if (itemString == null || itemString.isBlank()) {
                continue;
            }
            String[] itemDetails = itemString.split(",");
            if (itemDetails.length < 3) {
                System.err.println("Invalid prescription item format: " + itemString);
                failedOrderCount++;
                continue;
            }

            try {
                PrescriptionRequestDTO request = new PrescriptionRequestDTO();
                request.setPatientID(patientID);
                if (doctorID > 0) {
                    request.setDoctorID(doctorID);
                }
                request.setMedicineName(itemDetails[0].trim());
                request.setQuantity(Integer.parseInt(itemDetails[1].trim()));
                request.setMedicinePrice(Double.parseDouble(itemDetails[2].trim()));

                if (request.getMedicineName() == null || request.getMedicineName().isBlank()) {
                    failedOrderCount++;
                    continue;
                }
                if (request.getQuantity() <= 0 || request.getMedicinePrice() < 0) {
                    failedOrderCount++;
                    continue;
                }

                Prescription prescription = new Prescription();
                prescription.setPatientID(request.getPatientID());
                prescription.setDoctorID(request.getDoctorID() == null ? 0 : request.getDoctorID());
                prescription.setMedicineName(request.getMedicineName().trim());
                prescription.setQuantity(request.getQuantity());
                prescription.setMedicinePrice(request.getMedicinePrice());
                prescription.setStatus("PENDING");
                prescription.setOrderDate(orderDate);
                prescription.setOrderTime(orderTime);

                int generatedOrderID = prescriptionDAO.savePrescription(prescription);

                if (generatedOrderID <= 0) {
                    failedOrderCount++;
                    continue;
                }
                prescription.setOrderID(generatedOrderID);
                savedOrderCount++;

                try {
                    medService.logToFile(prescription);
                } catch (Exception fileException) {
                    System.err.println("File sync issue for order ID " + generatedOrderID + ": " + fileException.getMessage());
                }
            } catch (Exception exception) {
                System.err.println("Unexpected error: " + exception.getMessage());
                failedOrderCount++;
            }
        }

        if (savedOrderCount == 0) {
            return "redirect:/orderPrescription?msg=error";
        }
        if (failedOrderCount > 0) {
            return "redirect:/orderPrescription?msg=partial_success&saved=" + savedOrderCount + "&failed=" + failedOrderCount;
        }
        return "redirect:/orderPrescription?msg=success&saved=" + savedOrderCount;
    }

    /**
     * Pharmacist updates order status (Handles both /updatePrescription and /updateOrder)
     */
    @RequestMapping(value = {"/updatePrescription", "/updateOrder"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String updatePrescriptionStatus(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "prescriptionId", required = false) Integer prescriptionId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "action", required = false) String action,
            HttpSession session) {

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !"PHARMACIST".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/login";
        }

        // Determine prescription ID from available request parameters
        int targetId = (prescriptionId != null) ? prescriptionId : (id != null ? id : 0);

        // Determine raw action/status parameter
        String rawStatus = (status != null && !status.isBlank()) ? status : action;

        if (targetId <= 0 || rawStatus == null) {
            return "redirect:/pharmacistDashboard?msg=invalid";
        }

        // Standardize status string values
        String newStatus = switch (rawStatus.toLowerCase()) {
            case "complete", "ready", "completed" -> "COMPLETED";
            case "cancel", "cancelled" -> "CANCELLED";
            default -> rawStatus.toUpperCase();
        };

        // 1. Update MySQL database status
        boolean dbUpdated = prescriptionDAO.updateStatus(targetId, newStatus);

        // 2. Sync changes with secondary file storage
        try {
            List<Prescription> allOrders = medService.getAllOrders();
            for (Prescription order : allOrders) {
                if (order.getOrderID() == targetId) {
                    order.setStatus(newStatus);
                    break;
                }
            }
            medService.saveAllOrders(allOrders);
        } catch (Exception e) {
            System.err.println("File synchronization warning: " + e.getMessage());
        }

        if (dbUpdated) {
            return "redirect:/pharmacistDashboard?msg=success";
        }
        return "redirect:/pharmacistDashboard?msg=error";
    }
}