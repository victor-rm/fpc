import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println(
                "Use: java Main <num_producers> <max_items_per_producer> <producing_time> <num_consumers> <consuming_time>"
            );
            return;
        }

        int numProducers = Integer.parseInt(args[0]);
        int maxItemsPerProducer = Integer.parseInt(args[1]);
        int producingTime = Integer.parseInt(args[2]);
        int numConsumers = Integer.parseInt(args[3]);
        int consumingTime = Integer.parseInt(args[4]);

        // Calcula o total de itens que passarão pelo sistema para configurar o encerramento
        int totalItemsTarget = numProducers * maxItemsPerProducer;
        Buffer buffer = new Buffer(totalItemsTarget);

        List<Thread> totalThreads = new ArrayList<>();

        // Cria e inicia as threads produtoras de forma concorrente
        for (int i = 1; i <= numProducers; i++) {
            Producer producer = new Producer(
                i,
                buffer,
                maxItemsPerProducer,
                producingTime
            );
            Thread t = new Thread(producer);
            totalThreads.add(t);
            t.start();
        }

        // Cria e inicia as threads consumidoras de forma concorrente
        for (int i = 1; i <= numConsumers; i++) {
            Consumer consumer = new Consumer(i, buffer, consumingTime);
            Thread t = new Thread(consumer);
            totalThreads.add(t);
            t.start();
        }

        // Bloqueia a execução da Main até que todas as threads finalizem suas tarefas
        for (Thread t : totalThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("--- Execution completed successfully ---");
    }
}
