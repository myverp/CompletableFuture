import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CompletableFutureTask2 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final int N = 20;
        long totalStartTime = System.nanoTime();

        // Асинхронна генерація послідовності
        CompletableFuture<List<Double>> generateSequenceFuture = CompletableFuture.supplyAsync(() -> {
            long startTime = System.nanoTime();
            List<Double> sequence = generateRandomSequence(N);
            long endTime = System.nanoTime();
            System.out.printf("Sequence generated in %d ns\n", (endTime - startTime));
            return sequence;
        });

        // Асинхронний вивід початкової послідовності
        generateSequenceFuture.thenAcceptAsync(sequence -> {
            long startTime = System.nanoTime();
            System.out.println("Initial sequence: " + sequence);
            long endTime = System.nanoTime();
            System.out.printf("Initial sequence displayed in %d ns\n", (endTime - startTime));
        });

        // Асинхронне обчислення суми добутків
        CompletableFuture<Double> calculateSumFuture = generateSequenceFuture.thenApplyAsync(sequence -> {
            long startTime = System.nanoTime();
            double sum = calculateSumOfProducts(sequence);
            long endTime = System.nanoTime();
            System.out.printf("Sum calculated in %d ns\n", (endTime - startTime));
            return sum;
        });

        // Асинхронний вивід результату
        calculateSumFuture.thenAcceptAsync(sum -> {
            long startTime = System.nanoTime();
            System.out.println("Result: " + sum);
            long endTime = System.nanoTime();
            System.out.printf("Result displayed in %d ns\n", (endTime - startTime));
        });

        // Очікування завершення всіх задач
        CompletableFuture.allOf(generateSequenceFuture, calculateSumFuture).join();

        long totalEndTime = System.nanoTime();
        System.out.printf("Total execution time: %d ns\n", (totalEndTime - totalStartTime));
    }

    // Метод для генерації послідовності дійсних чисел
    private static List<Double> generateRandomSequence(int n) {
        Random random = new Random();
        List<Double> sequence = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            sequence.add(random.nextDouble() * 100); // Генеруємо числа від 0 до 100
        }
        return sequence;
    }

    // Метод для обчислення суми добутків
    private static double calculateSumOfProducts(List<Double> sequence) {
        double sum = 0;
        for (int i = 0; i < sequence.size() - 1; i++) {
            sum += sequence.get(i) * sequence.get(i + 1);
        }
        return sum;
    }
}