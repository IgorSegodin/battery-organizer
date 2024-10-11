package org.batteryorganizer;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.batteryorganizer.data.BatteryModule;
import org.batteryorganizer.data.BatteryPack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author isegodin
 */
@Slf4j
public class App {

    public static void main(String[] args) {
        int packSize = 2;

        List<BatteryModule> modules = Arrays.asList(
                new BatteryModule(1, 2.11d, 2.19d),
                new BatteryModule(2, 1.70d, 2.00d),
                new BatteryModule(3, 2.19d, 2.25d),
                new BatteryModule(4, 2.20d, 2.33d),
                new BatteryModule(5, 2.47d, 2.56d),
                new BatteryModule(6, 2.24d, 2.35d)
        );

        List<BatteryModuleWrapper> moduleWrappers = modules.stream()
                .map(m -> new BatteryModuleWrapper(m, (m.getR1() + m.getR2()) / 2))
                .sorted(Comparator.comparing(BatteryModuleWrapper::getNiceness).reversed())
                .toList();

        List<BatteryPack> packs = new LinkedList<>();
        BatteryPack currentPack = new BatteryPack();

        for (int left = 0, right = moduleWrappers.size() - 1; left <= right; ) {

            if (currentPack.size() < packSize) {
                currentPack.add(moduleWrappers.get(left).getModule());
                left++;
            }

            if (currentPack.size() < packSize && right > left) {
                currentPack.add(moduleWrappers.get(right).getModule());
                right--;
            }

            if (currentPack.size() >= packSize) {
                packs.add(currentPack);
                currentPack = new BatteryPack();
            }
        }


        log.info(
                "Selected packs: \n" +
                        packs.stream()
                                .map(String::valueOf)
                                .map(s -> "\t" + s)
                                .collect(Collectors.joining("\n"))
        );

        StringBuilder diffBuilder = new StringBuilder();

        for (int i = 0; i < packs.size(); i++) {
            BatteryPack pack = packs.get(i);

            for (int j = 0; j < packs.size(); j++) {
                if (j <= i) {
                    continue;
                }
                BatteryPack otherPack = packs.get(j);

                double dr1 = Math.abs(pack.getR1Sum() - otherPack.getR1Sum());
                double dr2 = Math.abs(pack.getR2Sum() - otherPack.getR2Sum());

                String dr1String = BigDecimal.valueOf(dr1).setScale(3, RoundingMode.HALF_UP).toString();
                String dr2String = BigDecimal.valueOf(dr2).setScale(3, RoundingMode.HALF_UP).toString();

                diffBuilder.append("\t")
                        .append(pack).append(" vs ").append(otherPack)
                        .append(" r1= ").append(dr1String)
                        .append(" r2= ").append(dr2String)
                        .append("\n");
            }
        }

        log.info("Differences: \n" + diffBuilder);

    }

    @Value
    public static class BatteryModuleWrapper {

        BatteryModule module;
        double niceness;

    }

}
