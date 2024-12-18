import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureTask1 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Створення списку файлів
        List<String> filePaths = Arrays.asList("file1.txt", "file2.txt", "file3.txt");

        // Створення списку CompletableFuture для завантаження файлів
        List<CompletableFuture<String>> loadFutures = new ArrayList<>();
        for (String filePath : filePaths) {
            loadFutures.add(CompletableFuture.supplyAsync(() -> {
                long startTime = System.nanoTime();
                String content = loadFileContent(filePath);
                long endTime = System.nanoTime();
                System.out.printf("File %s loaded in %d ns\n", filePath, (endTime - startTime)); // Змінено
                System.out.println("Original sentence from " + filePath + ": " + content);
                return content;
            }));
        }

        // Об'єднання результатів завантаження файлів
        CompletableFuture<List<String>> allLoaded = CompletableFuture.allOf(loadFutures.toArray(new CompletableFuture[0]))
                .thenApply(v -> loadFutures.stream()
                        .map(CompletableFuture::join)
                        .toList());

        // Асинхронна обробка завантажених речень
        CompletableFuture<List<String>> processedSentences = allLoaded.thenApplyAsync(sentences -> {
            long startTime = System.nanoTime();
            List<String> processed = new ArrayList<>();
            for (String sentence : sentences) {
                processed.add(sentence.replaceAll("[a-zA-Z]", ""));
            }
            long endTime = System.nanoTime();
            System.out.printf("Sentences processed in %d ns\n", (endTime - startTime)); // Змінено
            return processed;
        });

        // Асинхронний вивід результатів
        processedSentences.thenAcceptAsync(result -> {
            long startTime = System.nanoTime();
            System.out.println("Resulting array: " + result);
            long endTime = System.nanoTime();
            System.out.printf("Result displayed in %d ns\n", (endTime - startTime)); // Змінено
        });

        // Очікування завершення всіх задач
        CompletableFuture.allOf(processedSentences).join();
    }

    // Метод для завантаження вмісту файлу
    private static String loadFileContent(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            return "";
        }
    }
}