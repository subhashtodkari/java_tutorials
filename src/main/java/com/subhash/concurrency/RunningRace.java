package com.subhash.concurrency;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class RunningRace {

    private static class RaceReferee extends Thread {

        private CountDownLatch raceTrigger = new CountDownLatch(1);

        private CountDownLatch finishTrigger;

        private CountDownLatch readyTrigger;

        public RaceReferee(String name, int runnersCount) {
            super(name);
            finishTrigger = new CountDownLatch(runnersCount);
            readyTrigger = new CountDownLatch(runnersCount);
        }

        public CountDownLatch getRaceTrigger() {
            return raceTrigger;
        }

        public CountDownLatch getFinishTrigger() {
            return finishTrigger;
        }

        public CountDownLatch getReadyTrigger() {
            return readyTrigger;
        }

        @Override
        public void run() {
            try {
                System.out.println(new Date() + " : " + getName() + " : Ready, steady");
                readyTrigger.await(); // wait for all runner to get ready
                System.out.println(new Date() + " : " + getName() + " : Goooooo............");
                raceTrigger.countDown(); // give go signal to all runners
                finishTrigger.await();//wait for all runners to finish
                System.out.println(new Date() + " : " + getName() + " : Race is over !!!!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Runner extends Thread {

        private int maxSpeed; //meters per second
        private int raceDistance; // meters
        private CountDownLatch raceTrigger;
        private CountDownLatch finishTrigger;
        private CountDownLatch readyTrigger;
        private long timeToFinish = -1;

        public Runner(String name, int maxSpeed, int raceDistance, CountDownLatch raceTrigger, CountDownLatch finishTrigger, CountDownLatch readyTrigger) {
            super(name);
            this.maxSpeed = maxSpeed;
            this.raceDistance = raceDistance;
            this.raceTrigger = raceTrigger;
            this.finishTrigger = finishTrigger;
            this.readyTrigger = readyTrigger;
        }

        public long getTimeToFinish() {
            return timeToFinish;
        }

        @Override
        public void run() {
            try {
                System.out.println(new Date() + " : " + getName() + " : ready");
                readyTrigger.countDown(); // tell referee I am ready
                raceTrigger.await(); // waiting for referee's signal to start
                long start = System.currentTimeMillis();
                System.out.println(new Date() + " : " + getName() + " : started");
                int distCovered = 0;
                while (distCovered < raceDistance) {
                    int currentSpeed = ( 50 + new Random().nextInt(50) ) * maxSpeed / 100;
                    distCovered ++;
                    int timeToCoverOneMeter = 1000 / currentSpeed;
                    Thread.sleep(timeToCoverOneMeter);
                }
                long end = System.currentTimeMillis();
                timeToFinish = end - start;
                finishTrigger.countDown(); // tell referee that I have finished race
                System.out.println(new Date() + " : " + getName() + " : finished in " + timeToFinish + " millisec");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return "Runner: " + getName() +", timeToFinish: " + timeToFinish;
        }
    }

    public static void main(String[] args) {
        int runnersCount = 10;
        final RaceReferee raceReferee = new RaceReferee("Referee", runnersCount);
        raceReferee.start();
        List<Runner> runners = new ArrayList<>(runnersCount);
        for (int i = 0; i < runnersCount; i++) {
            final Runner runner = new Runner("Runner-"+i, 10, 100, raceReferee.getRaceTrigger(), raceReferee.getFinishTrigger(), raceReferee.getReadyTrigger());
            runners.add(runner);
            runner.start();
        }
        try {
            raceReferee.join();
            System.out.println("----------- Results -----------");
            runners.sort(Comparator.comparing(Runner::getTimeToFinish));
            runners.forEach(r -> System.out.println(r));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
