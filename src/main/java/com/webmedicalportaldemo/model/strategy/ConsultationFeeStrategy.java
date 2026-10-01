package com.webmedicalportaldemo.model.strategy;

import com.webmedicalportaldemo.model.Appointment;

/**
 * Consultation appointments are always a flat fee.
 * Behavior is identical to the original Consultation.calculateFee().
 */
public class ConsultationFeeStrategy implements FeeCalculationStrategy {

    private static final double CONSULTATION_FEE = 1500.00;

    @Override
    public double calculateFee(Appointment appointment) {
        return CONSULTATION_FEE;
    }
}