import java.util.ArrayList;
import java.util.List;

public class ch03 {
    static double totalTime(List<Double> lapTimes) {
        if (lapTimes.isEmpty() || lapTimes.size() < 2)  {
            return 0;
        }
        ArrayList<Double> raceLaps = new ArrayList<>(lapTimes.subList(1, lapTimes.size()));
        double sum = 0;
        for (double x : raceLaps) {
            sum += x;
        }
        return sum;
    }

    static double avgTime(List<Double> lapTimes) {
        double time = totalTime(lapTimes);
        int laps = lapTimes.size() - 1;
        return time / laps;
    }

    public static void main(String[] args) {
        ArrayList<Double> lapTimes = new ArrayList<>();
        lapTimes.add(31.0); // warm-up lap (not taken into calculations)
        lapTimes.add(20.9);
        lapTimes.add(21.1);
        lapTimes.add(21.3);
        System.out.printf("Total: %.1fs\n", totalTime(lapTimes));
        System.out.printf("Avg: %.1fs\n", avgTime(lapTimes));

        lapTimes = new ArrayList<>();
        lapTimes.add(31.0); // warm-up lap (not taken into calculations)
        System.out.printf("Total: %.1fs\n", totalTime(lapTimes));
        System.out.printf("Avg: %.1fs",     avgTime(lapTimes));
    }
}
