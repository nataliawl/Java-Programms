import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MultithreadedTaskProcessor implements TaskProcessor {
    private int limit;
    private int currentSend;
    private int currentMade;
    private List<Task> allTasks = new ArrayList<>();
    private ResultConsumer taskConsumer;
    private int[] readyToSendId;
    private int[] readyToSendResults;
    private boolean[] ifReady;
    private  BlockingQueue<Integer> tasksIndex = new LinkedBlockingQueue<>();

    private class RunnableWorker implements Runnable {
        @Override
        public void run() {
            while (true) {
                Integer id = tasksIndex.poll();
                if (id == null) break;
                getResult(id);
            }
        }
        private void getResult(int index){
            Task workerTask = allTasks.get(index);
            int taskId = workerTask.id();
            while(index != currentMade){
                try{
                    Thread.sleep(1);
                }
                catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
            }
            synchronized (MultithreadedTaskProcessor.this) {
                currentMade++;}
            int taskResult = workerTask.result();
            synchronized (MultithreadedTaskProcessor.this){
                ifReady[index] = true;
                readyToSendResults[index] = taskResult;
                readyToSendId[index] = taskId;
                sendResult();
            }
        }

        private void sendResult(){
            if(currentSend == allTasks.size()) return;
            synchronized (MultithreadedTaskProcessor.this){
                while(ifReady[currentSend]){
                    taskConsumer.save(readyToSendId[currentSend], readyToSendResults[currentSend]);
                    currentSend++;
                    if(currentSend == allTasks.size()) break;
                }
            }
        }
    }

    @Override
    public void set(List<Task> tasks, ThreadsFactory factory, ResultConsumer consumer) {
        List<Runnable> workers = new ArrayList<>();
        this.allTasks = tasks;
        this.taskConsumer = consumer;
        this.currentSend = 0;
        this.currentMade = 0;
        this.readyToSendResults = new int[allTasks.size()];
        this.readyToSendId = new int[allTasks.size()];
        this.ifReady = new boolean[allTasks.size()];

        for (int i = 0; i < tasks.size(); i++) {
            tasksIndex.offer(i);
        }

        for(int i = 0; i < this.limit; i++) {workers.add(new RunnableWorker());}
        List<Thread> threads = factory.createThreads(workers);
        for(Thread t : threads) t.start();

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void threadsLimit(int limit) { this.limit = limit; }

}
