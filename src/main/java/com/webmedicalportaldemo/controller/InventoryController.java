package com.webmedicalportaldemo.controller;

import com.webmedicalportaldemo.dao.InventoryDAO;
import com.webmedicalportaldemo.model.PharmacyItem;
import com.webmedicalportaldemo.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/pharmacist/inventory")
public class InventoryController {

    private final InventoryDAO inventoryDAO;

    public InventoryController(InventoryDAO inventoryDAO) {
        this.inventoryDAO = inventoryDAO;
    }

    /**
     * View complete inventory log
     */
    @GetMapping
    public String viewInventory(Model model, HttpSession session) {
        if (!isPharmacist(session)) return "redirect:/login";

        List<PharmacyItem> inventory = inventoryDAO.getAllItems();
        model.addAttribute("inventory", inventory);

        return "pharmacy/inventory_list";
    }

    /**
     * Show form to add a new drug
     */
    @GetMapping("/add")
    public String showAddForm(Model model, HttpSession session) {
        if (!isPharmacist(session)) return "redirect:/login";

        model.addAttribute("isEdit", false);
        model.addAttribute("item", new PharmacyItem());

        return "pharmacy/inventory_form";
    }

    /**
     * Show form to edit an existing drug
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") int id, Model model, HttpSession session) {
        if (!isPharmacist(session)) return "redirect:/login";

        PharmacyItem item = inventoryDAO.getItemById(id);
        if (item == null) {
            return "redirect:/pharmacist/inventory?msg=error";
        }

        model.addAttribute("isEdit", true);
        model.addAttribute("item", item);

        return "pharmacy/inventory_form";
    }

    /**
     * Save a drug (handles both Insert and Update operations)
     */
    @PostMapping("/save")
    public String saveInventoryItem(
            @RequestParam(value = "itemId", required = false, defaultValue = "0") int itemId,
            @RequestParam("drugName") String drugName,
            @RequestParam("unitPrice") double unitPrice,
            @RequestParam("stockQuantity") int stockQuantity,
            HttpSession session) {

        if (!isPharmacist(session)) return "redirect:/login";

        PharmacyItem item = new PharmacyItem();
        item.setItemId(itemId);
        item.setDrugName(drugName.trim());
        item.setUnitPrice(unitPrice);
        item.setStockQuantity(stockQuantity);

        boolean success;
        if (itemId > 0) {
            success = inventoryDAO.updateItem(item);
        } else {
            success = inventoryDAO.insertItem(item);
        }

        if (success) {
            return "redirect:/pharmacist/inventory?msg=success";
        }
        return "redirect:/pharmacist/inventory?msg=error";
    }

    /**
     * Delete a drug from inventory
     */
    @PostMapping("/delete")
    public String deleteInventoryItem(@RequestParam("itemId") int itemId, HttpSession session) {
        if (!isPharmacist(session)) return "redirect:/login";

        boolean success = inventoryDAO.deleteItem(itemId);

        if (success) {
            return "redirect:/pharmacist/inventory?msg=success";
        }
        return "redirect:/pharmacist/inventory?msg=error";
    }

    /**
     * Helper method to verify pharmacist role
     */
    private boolean isPharmacist(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && "PHARMACIST".equalsIgnoreCase(user.getRole());
    }
}