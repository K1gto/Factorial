import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

// Клас для обчислення факторіалу
class FactorialTask implements Callable<BigInteger> {
    private final int number;

    public FactorialTask(int number) {
        this.number = number;
    }

    @Override
    public BigInteger call() {
        return calculateFactorial(number);
    }

    private BigInteger calculateFactorial(int n) {
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }
}

public class Main {
    public static void main(String[] args) {
        ConcurrentHashMap<Integer, BigInteger> factorialMap = new ConcurrentHashMap<>();

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        List<Future<BigInteger>> futures = new ArrayList<>();

        int[] numbers = {5, 10, 15, 20, 25, 30, 35};

        for (int number : numbers) {
            Callable<BigInteger> task = new FactorialTask(number);
            Future<BigInteger> future = executorService.submit(() -> {
                try {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException("Задача була скасована");
                    }
                    BigInteger result = task.call();
                    factorialMap.put(number, result);
                    return result;
                } catch (InterruptedException e) {
                    System.out.println("Задача для числа " + number + " була скасована.");
                    return null;
                }
            });
            futures.add(future);
        }

        for (Future<BigInteger> future : futures) {
            try {
                if (!future.isCancelled()) {
                    BigInteger result = future.get();
                    if (result != null) {
                        System.out.println("Результат обчислення факторіалу: " + result);
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();

        System.out.println("\nФакторіали чисел:");
        for (int number : numbers) {
            if (factorialMap.containsKey(number)) {
                System.out.println(number + "! = " + factorialMap.get(number));
            }
        }
    }
}
