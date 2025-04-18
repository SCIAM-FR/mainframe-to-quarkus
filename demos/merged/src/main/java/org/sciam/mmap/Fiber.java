package org.sciam.mmap;


import org.sciam.model.Chunk;
import sun.misc.Unsafe;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;


/**
 * We are widely inspired by the code put to solve the one-billion-row challenge
 * The file used for this class is generated in another project
 */
public class Fiber {

    private static final long size = 1_000_000_000L;
    private static final long cores = 8;
    private static final long RECORD_SIZE = 32;
    private static final long ID_SIZE = 25;
    private static final long MESURE_SIZE = 7;

    private static final Path path = Path.of("store.data");
    private static final Unsafe UNSAFE = initUnsafe();

    private static Unsafe initUnsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(Unsafe.class);
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) throws Exception {


        if (isSpawn(args)) {
            spawn();
            return;
        }

        var start = System.currentTimeMillis();
        var options = List.of(CREATE, READ).toArray(StandardOpenOption[]::new);

        try (var channel = FileChannel.open(path, options)) {

            var segment = channel.map(READ_ONLY, 0, size * RECORD_SIZE, Arena.global());
            var tasks = chunks(size, cores).stream().map(chunk -> Task.of(chunk, segment, UNSAFE)).toList();

            try (var executor = Executors.newCachedThreadPool()) {
                tasks.forEach(executor::execute);
            }

            System.out.println("All chunks processed in " + (System.currentTimeMillis() - start) + " ms");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }


    private static boolean isSpawn(String[] args) {
        for (String arg : args) {
            if ("--worker".equals(arg)) {
                return false;
            }
        }

        return true;
    }

    private static void spawn() throws Exception {
        ProcessHandle.Info info = ProcessHandle.current().info();
        ArrayList<String> commands = new ArrayList<>();
        Optional<String> command = info.command();
        Optional<String[]> arguments = info.arguments();
        command.ifPresent(commands::add);
        arguments.ifPresent(strings -> commands.addAll(Arrays.asList(strings)));
        commands.add("--worker");

        new ProcessBuilder()
                .command(commands)
                .start()
                .getInputStream()
                .transferTo(System.out);
    }


    public static List<Chunk> chunks(long total, long cores) {

        final List<Chunk> result = new ArrayList<>((int) cores);
        long lines = total / cores;
        long remainder = total % cores;

        long start = 0;
        for (int index = 0; index < cores; index++) {
            long end = start + lines + (index < remainder ? 1 : 0);
            result.add(Chunk.of(index, start, end));
            start = end; // Update start for next core
        }

        return result;
    }

    public record Task(Chunk chunk,
                       MemorySegment segment,
                       Unsafe unsafe) implements Runnable {

        public static Task of(Chunk chunk,
                              MemorySegment segment,
                              Unsafe unsafe) {
            return new Task(chunk, segment, unsafe);
        }

        @Override
        public void run() {

            long start = System.currentTimeMillis();
            for (long index = this.chunk.start(); index < this.chunk.end(); index++) {
                var line = this.segment.asSlice(index * RECORD_SIZE);
                var id = this.segment.asSlice(index * RECORD_SIZE, ID_SIZE).address();
                var mesure = this.segment.asSlice(index * RECORD_SIZE, MESURE_SIZE).address();
                assert id != 0;
                assert mesure != 0;
                assert line != null;
            }

            System.out.println("Finished processing chunk " + this.chunk + " in " + (System.currentTimeMillis() - start) + " ms");
        }

    }

}
