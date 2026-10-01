package com.webmedicalportaldemo.model.strategy;

import com.webmedicalportaldemo.model.Appointment;
import com.webmedicalportaldemo.model.Surgery;

/**
 * Surgery fees are BASE_FEE plus exactly one selected add-on
 * (ANESTHESIA / FACILITY / EQUIPMENT / OTHER / NONE).
 * Behavior is identical to the original Surgery.calculateFee().
 */
public class SurgeryFeeStrategy implements FeeCalculationStrategy {

    private static final double BASE_FEE = 5000.00;
    private static final double ANESTHESIA_FEE = 2500.00;
    private static final double FACILITY_FEE = 1500.00;
    private static final double EQUIPMENT_FEE = 3000.00;
    private static final double OTHER_FEE = 500.00;

    @Override
    public double calculateFee(Appointment appointment) {
        Surgery surgery = (Surgery) appointment;
        double extraCost;

        switch (surgery.getAddCharge()) {
            case "ANESTHESIA":
                extraCost = ANESTHESIA_FEE;
                break;
            case "FACILITY":
                extraCost = FACILITY_FEE;
                break;
            case "EQUIPMENT":
                extraCost = EQUIPMENT_FEE;
                break;
            case "OTHER":
                extraCost = OTHER_FEE;
                break;
            case "NONE":
            default:
                extraCost = 0.00;
                break;
        }

        return BASE_FEE + extraCost;
    }
}