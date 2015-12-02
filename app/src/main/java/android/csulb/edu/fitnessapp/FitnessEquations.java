package android.csulb.edu.fitnessapp;

/**
 * Created by metehan on 12/2/2015.
 */

public class FitnessEquations {

    // Basal Metabolic Rate (in calories burned per 24 hours)
    // Metric system
    public static double calcBMR(double weight, int height, int age, boolean gender) {
        // male == 1
        if (gender) {
            return (13.75 * weight) + (5 * height) - (6.76 * age) + 66;
        } else {
            // female == 0
            return (9.56 * weight) + (1.85 * height) - (4.68 * age) + 655;
        }
    }

    // Gross calories burn
    // Duration: in hours
    public static double calcGCB(double BMR, double duration) {
        return ((BMR * 1.1) / 24) * duration;
    }

    // Net Calories spent
    // Weight: pounds, Distance: miles
    public static double calcNetCal(double weight, double distance, boolean type) {
        // Running
        if (type) {
            return weight * 0.63 * distance;
        }
        // Walking
        else {
            return weight * 0.30 * distance;
        }
    }
}
