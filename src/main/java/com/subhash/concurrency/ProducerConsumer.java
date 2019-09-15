package com.subhash.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class ProducerConsumer {

    public static class Consumer implements Callable<Void> {

        private final BlockingQueue<Object> queue;
        private final String name;

        Consumer(String name, BlockingQueue<Object> queue) {
            this.queue = queue;
            this.name = name;
        }

        @Override
        public Void call() throws Exception {
            Object obj;
            while(true) {
                //blocking call - consumer waits until object is available in queue
                obj = queue.take();

                //check if producer has stopped production
                if ("done".equals(obj)) {
                    System.out.println(name + ": Done!");
                    break;
                }

                System.out.println(name + ": <<< " + obj);

                //simulate object processing - consumer is busy
                int t = new Random().nextInt(10);
                //System.out.println(name + ": processing object will take time >> " + t);
                Thread.sleep(t * 1000);
            }

            return null;
        }
    }


    public static class Producer implements Callable<Void> {

        private final BlockingQueue<Object> queue;
        private final String name;

        Producer(String name, BlockingQueue<Object> queue) {
            this.queue = queue;
            this.name = name;
        }

        @Override
        public Void call() throws Exception {

            for (int i = 0; i < 10; i++) {
                System.out.println(name + ": >>> " + i);
                //blocking call - producer waits until queue has at least one space to put object
                queue.put(name + " - " + i);

                //simulate object production - producer is busy but generally lesser than consumer
                int t = new Random().nextInt(3);
                //System.out.println(name + ": producing next object will take time >> " + t);
                Thread.sleep(t * 1000);
            }

            System.out.println(name + ": Done!");
            return null;
        }

    }

    public static void main(String[] args) throws Exception {
        //shared queue between producer and consumer of capacity 5
        BlockingQueue<Object> queue = new ArrayBlockingQueue<>(5);

        //producer thread pool
        int producerThreadPoolSize = 2;
        ExecutorService producers = Executors.newFixedThreadPool(producerThreadPoolSize);
        List<Future<Void>> producerFutures = new ArrayList<>(producerThreadPoolSize);
        for (int i = 0; i < producerThreadPoolSize; i++) {
            producerFutures.add(
                    producers.submit(new Producer("Producer-" + i, queue))
            );
        }

        //consumer thread pool
        int consumerThreadPoolSize = 2;
        ExecutorService consumers = Executors.newFixedThreadPool(consumerThreadPoolSize);
        for (int i = 0; i < consumerThreadPoolSize; i++) {
            consumers.submit(new Consumer("Consumer-" + i, queue));
        }

        //wait for producers to stop
        for (Future<Void> f : producerFutures) {
            f.get();
        }

        //indicate consumer threads to stop
        for (int i = 0; i < consumerThreadPoolSize; i++) {
            queue.put("done");
        }

        consumers.shutdown();
        producers.shutdown();

        System.out.println("Main: Done!");
    }
}
