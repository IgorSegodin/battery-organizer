package org.batteryorganizer.data;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author isegodin
 */
public class BatteryPack {

    private List<BatteryModule> modules = new LinkedList<>();

    public void add(BatteryModule module) {
        modules.add(module);
    }

    public int size() {
        return modules.size();
    }

    public double getR1Sum() {
        return modules.stream()
                .mapToDouble(BatteryModule::getR1)
                .sum();
    }

    public double getR2Sum() {
        return modules.stream()
                .mapToDouble(BatteryModule::getR2)
                .sum();
    }

    @Override
    public String toString() {
        return modules.stream()
                .map(BatteryModule::getId)
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining("-"));
    }
}
