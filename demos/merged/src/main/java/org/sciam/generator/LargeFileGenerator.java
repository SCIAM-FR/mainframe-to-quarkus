package org.sciam.generator;

import org.sciam.model.Stations;
import org.sciam.model.WeatherStation;

import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class LargeFileGenerator {

    private static final Path STORE_FILE = Path.of("./store.txt");
    static final Executor EXECUTOR_SERVICE = Executors.newWorkStealingPool();
    static final long ONE_BILLION = 1_000_000_000L;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        int size = (int) ONE_BILLION;

        try {
            Files.deleteIfExists(STORE_FILE);
            Files.createFile(STORE_FILE);
        } catch (Exception e) {
            // ignore
        }

        List<WeatherStation> stations = Stations.stations();

        int chunkSize = 10_000_000;
        int numberOfFutures = size / chunkSize;
        CompletableFuture<?>[] futures = new CompletableFuture[numberOfFutures];

        for (int n = 0; n < numberOfFutures; n++) {
            int finalN = n;
            futures[n] = CompletableFuture.runAsync(() -> {
                StringBuilder builder = new StringBuilder();
                for (int i = finalN * chunkSize; i <= (finalN + 1) * chunkSize - 1; i++) {
                    WeatherStation station = stations.get(ThreadLocalRandom.current().nextInt(stations.size()));
                    builder.append(StringPadding
                                    .padString(station.id(),
                                            23,
                                            StringPadding.Position.LEFT,
                                            ' '))
                            .append(StringPadding
                                    .padString(station.measurement() + "",
                                            6,
                                            StringPadding.Position.LEFT,
                                            ' '))
                            .append('\n');
                }

                try (BufferedWriter bw = Files
                        .newBufferedWriter(STORE_FILE, StandardOpenOption.APPEND)) {
                    bw.write(builder.toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, EXECUTOR_SERVICE);
        }

        CompletableFuture.allOf(futures).join();

        System.out.printf("Created file with %,d measurements in %s ms%n", size, System.currentTimeMillis() - start);
    }
}
