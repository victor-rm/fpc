class Consumer implements Runnable {

    private final Buffer buffer;
    private final int sleepTime;
    private final int id;

    public Consumer(int id, Buffer buffer, int sleepTime) {
        this.id = id;
        this.buffer = buffer;
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        while (true) {
            int item = buffer.remove();
            // Se o buffer retornar -1, significa que todos os itens do lab já foram consumidos
            if (item == -1) {
                break;
            }
            System.out.println("Consumer " + id + " consumed item " + item);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("Consumer " + id + " finished and exited.");
    }
}
