import java.util.ArrayList;
import java.util.function.Function;
import java.util.concurrent.atomic.AtomicInteger;

//Nie synchornizowac kazdej iteracji!
//Lepiej dac globalne niz lokalne!

public class MultithreadedIntegaration implements ParallelIntegaration{
    private Function<Double, Double> function;
    private int threads_number;
    private double result;
    private AtomicInteger current_index;
    private double subintervals_number;
    private double dx;
    private double thread_min;

    private class IntegralThread implements Runnable {
        public void run(){
            double part_result = 0.0;
            while(true){

                int thread_index = current_index.getAndAdd(10);
                if(thread_index >= subintervals_number) break;
                int index_end = (int)Math.min(thread_index + 10 ,  subintervals_number);

                for(int i = thread_index; i < index_end; i++){
                    double sub = thread_min + i * dx;
                    part_result += function.apply(sub + dx / 2) * dx;
                }
            }
            synchronized (MultithreadedIntegaration.this) {
                result  += part_result;
            }
        }
    }

    @Override
    public void setFunction(Function<Double, Double> function) {this.function = function;}

    @Override
    public void setThreadsNumber(int threads) {this.threads_number = threads;}
    @Override
    public void calc(Range range, int subintervals) {
        if(subintervals <= 0) throw new IllegalArgumentException();
        result = 0.0;
        current_index = new AtomicInteger(0);
        ArrayList<Thread> threads = new ArrayList<>();
        this.subintervals_number = subintervals;
        this.dx = (range.max() - range.min())/subintervals;
        this.thread_min = range.min();

        for(int i = 0; i < this.threads_number; i++){
            threads.add(new Thread(new IntegralThread()));
            threads.getLast().start();
        }

        for(Thread thread : threads){
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public double getResult() {
        return result;
    }

}
