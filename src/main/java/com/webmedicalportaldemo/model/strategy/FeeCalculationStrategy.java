package com.webmedicalportaldemo.model.strategy;

import com.webmedicalportaldemo.model.Appointment;

/**
 * Strategy interface for the "family of algorithms" used to calculate
 * an appointment's total fee.
 *
 * Each appointment type (Consultation, Surgery, and any future type)
 * supplies its own concrete strategy. The Appointment (context) holds
 * a reference to one of these and delegates to it, instead of each
 * subclass implementing calculateFee() itself.
 */
public interface FeeCalculationStrategy {
    double calculateFee(Appointment appointment);
}