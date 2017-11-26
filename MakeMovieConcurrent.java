/**
 * Michael Mangrobang
**/

import raytracer.ABunchOfSpheres;
import java.lang.ArrayIndexOutOfBoundsException;
import java.lang.NumberFormatException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MakeMovieConcurrent implements Runnable {

    // Declare
    private int thread_num;
    private int num_frames;
    private int start;
    private int end;

    // Constructor
    public MakeMovieConcurrent(int num_frames, int thread_num) {
        // Define
        this.thread_num = thread_num;
        this.num_frames = num_frames;
        this.start = thread_num * num_frames;
        this.end = (thread_num + 1) * num_frames;
    }

    // Override Runnable's run() function
    @Override
    public void run() {
        // Create a movie object
        ABunchOfSpheres movie = new ABunchOfSpheres();
        // Render the frames
        for (int count = this.start; count < this.end; count++) {
            movie.render_scene("./frame_" + String.format("%05d", count) + ".png", count);
        }
    }

    private static void executeThreads(int num_frames, int num_threads) {

        // Declare
        long startTime;
        long endTime;
        long seconds;
        double elapsedTime;
		ExecutorService executor;

        // Define
        endTime = 0;
        seconds = 0;
        elapsedTime = 0.0f;
        executor = Executors.newFixedThreadPool(num_threads);
        startTime = System.nanoTime();

        for (int i = 0; i < num_threads; i++) {
            MakeMovieConcurrent movie = new MakeMovieConcurrent(num_frames, i);
            executor.execute(movie);
        }

        executor.shutdown();

        while (!executor.isTerminated()) {
            // Wait for executor to finish
        }

        // Calculate current elapsed time
        endTime = System.nanoTime();
        seconds = endTime - startTime;
        elapsedTime = (double) seconds / 1000000000.0f;
        // Print final time
        System.out.println("Number of Threads: " + num_threads + "\nTime: " + elapsedTime + " seconds");
    }

    // Error message
    private static void abort(String message) {
        System.err.println(message);
        System.exit(1);
    }

    // Main
    public static void main(String[] args) {

        // Declare
        int num_frames;
        int num_threads;
        int partition_size;

        // Define
        num_frames = 0;
        num_threads = 1;
        partition_size = 1;

        try {
            // Try setting num_frames to argument
            num_frames = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            // Else error
            abort("Invalid number of frames (should be an integer)");
        } catch (ArrayIndexOutOfBoundsException e) {
            abort("Usage: java MakeMovieConcurrent <num of frames>");
        }

        // Check if num_frames input is valid
        if (num_frames < 0) {
            // Else error
            abort("Invalid number of frames (should be positive)");
        }

        // Chceck if there is second argument
        if (args.length == 2) {

            try {
                // Try set num_threads to second argument
                num_threads = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                // Else error
                abort("Usage: java JavaThreadWarmpup <num of threads>");
            }

            // Check if num_threads input is valid
            if (num_threads < 1) {
                abort("Invalid number of threads (should be positive)");
            }

        }

        // Calculate number of frames each thread does
        partition_size = num_frames / num_threads;
        executeThreads(partition_size, num_threads);
    }
}
