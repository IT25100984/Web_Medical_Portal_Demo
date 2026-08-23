package com.webmedicalportaldemo.controller;

import com.webmedicalportaldemo.dao.PatientDAO;
import com.webmedicalportaldemo.dao.PrescriptionDAO;
import com.webmedicalportaldemo.dto.PrescriptionRequestDTO;
import com.webmedicalportaldemo.model.Prescription;
import com.webmedicalportaldemo.model.User;
import com.webmedicalportaldemo.service.MedFileService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        List<Prescription> allOrders = medService.getAllOrders();
        List<Prescription> myOrders = prescriptionDAO.getPrescriptionsByPatientUserId(user.getUserID());
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

        /*
         * The session contains users.user_id, but prescriptions
         * references patients.patient_id.
         */
        Integer patientID = patientDAO.getPatientIdByUserId(currentUser.getUserID());
        if (patientID == null || patientID <= 0) {
            return "redirect:/orderPrescription" + "?msg=patient_not_found";
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
                /*
                 * Perform conditional validation because the cart
                 * is submitted as one encoded string rather than as
                 * individual DTO form objects.
                 */
                if (request.getMedicineName() == null || request.getMedicineName().isBlank()) {
                    System.err.println("Medicine name cannot be empty.");
                    failedOrderCount++;
                    continue;
                }
                if (request.getMedicineName().length() > 150) {
                    System.err.println("Medicine name exceeds 150 characters: " + request.getMedicineName());
                    failedOrderCount++;
                    continue;
                }
                if (request.getQuantity() <= 0) {
                    System.err.println("Invalid quantity for medicine: " + request.getMedicineName());
                    failedOrderCount++;
                    continue;
                }
                if (request.getMedicinePrice() < 0) {
                    System.err.println("Invalid price for medicine: " + request.getMedicineName());
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
                /*
                 * MySQL is now the primary storage location.
                 * savePrescription() returns the generated order ID.
                 */
                int generatedOrderID = prescriptionDAO.savePrescription(prescription);

                if (generatedOrderID <= 0) {
                    System.err.println("MySQL could not save medicine: " + request.getMedicineName());
                    failedOrderCount++;
                    continue;
                }
                prescription.setOrderID(generatedOrderID);
                savedOrderCount++;
                /*
                 * Temporary secondary text-file synchronization.
                 * A file error does not reverse the successful
                 * MySQL insertion.
                 */
                try {medService.logToFile(prescription);
                } catch (Exception fileException) {
                    System.err.println("Prescription ID " + generatedOrderID
                                    + " was saved to MySQL, but the " + "medicine order file could not "
                                    + "be updated: " + fileException.getMessage());
                }
            } catch (NumberFormatException exception) {
                System.err.println("Invalid quantity or price in item: " + itemString);
                failedOrderCount++;
            } catch (Exception exception) {
                System.err.println("Unexpected prescription processing error: " + exception.getMessage());
                failedOrderCount++;
            }
        }
        if (savedOrderCount == 0) {
            return "redirect:/orderPrescription" + "?msg=error";
        }
        if (failedOrderCount > 0) {
            return "redirect:/orderPrescription" + "?msg=partial_success"
                    + "&saved=" + savedOrderCount + "&failed=" + failedOrderCount;
        }
        return "redirect:/orderPrescription" + "?msg=success" + "&saved=" + savedOrderCount;
    }

    /**
     * Pharmacist updates order state
     */
    @GetMapping("/updateOrder")
    public String updateOrder(
            @RequestParam("id") int orderId,
            @RequestParam("action") String action) {

        List<Prescription> allOrders = medService.getAllOrders();
        boolean found = false;
        for (Prescription order : allOrders) {
            if (order.getOrderID() == orderId) {
                switch (action.toLowerCase()) {
                    case "complete": order.setStatus("COMPLETED");
                        break;
                    case "cancel": order.setStatus("CANCELLED");
                        break;
                    default:
                        return "redirect:/pharmacistDashboard?msg=invalid";
                }
                found = true;
                break;
            }
        }
        if (found) {
            medService.saveAllOrders(allOrders);
            return "redirect:/pharmacistDashboard?msg=success";
        }
        return "redirect:/pharmacistDashboard?msg=error";
    }
}