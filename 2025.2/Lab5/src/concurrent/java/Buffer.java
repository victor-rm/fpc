import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

class Buffer {

    private final List<Integer> data = new ArrayList<>();
    private final int capacity = 50; // Limite imposto pelo enunciado

    // Semáforos para controle de concorrência
    private final Semaphore mutex = new Semaphore(1); // Garante exclusão mútua na lista
    private final Semaphore empty = new Semaphore(50); // Controla espaços livres (máx 50)
    private final Semaphore full = new Semaphore(0); // Controla itens disponíveis

    private final int totalItemsTarget;
    private int totalConsumed = 0;

    public Buffer(int totalItemsTarget) {
        this.totalItemsTarget = totalItemsTarget;
    }

    public void put(int value) {
        try {
            empty.acquire(); // Espera haver espaço vazio no buffer (bloqueia se cheio)
            mutex.acquire(); // Entra na região crítica

            data.add(value);
            System.out.println(
                "Inserted: " + value + " | Buffer size: " + data.size()
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            mutex.release(); // Sai da região crítica
            full.release(); // Incrementa e sinaliza que há um novo item disponível
        }
    }

    public int remove() {
        try {
            full.acquire(); // Espera haver algum item no buffer (bloqueia se vazio)
            mutex.acquire(); // Entra na região crítica

            // Mecanismo de encerramento seguro para os consumidores
            if (totalConsumed >= totalItemsTarget) {
                full.release(); // Repassa o sinal para acordar o próximo consumidor em cascata
                mutex.release();
                return -1; // Sinaliza parada para a thread consumidora
            }

            int value = data.remove(0);
            totalConsumed++;
            System.out.println(
                "Removed: " + value + " | Buffer size: " + data.size()
            );

            // Se este foi o último item de toda a simulação, acorda os demais consumidores
            if (totalConsumed == totalItemsTarget) {
                full.release();
            }

            mutex.release();
            empty.release(); // Libera uma vaga no buffer para os produtores
            return value;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        }
    }
}
