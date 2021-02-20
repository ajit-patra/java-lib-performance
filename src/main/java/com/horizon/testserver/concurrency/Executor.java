package com.horizon.testserver.concurrency;

import com.horizon.testserver.dto.PerfItrResultArgs;
import com.horizon.testserver.dto.PerfResultArgs;
import com.horizon.testserver.dto.FloatBooleanPairArgs;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class Executor {

    private final ExecutorService executor;

    private final int threads;
    private final Map<String, Integer[]> itrList;
    private final int warmUpThreads;
    private double totalElapsedTime;
    private int passCount;
    private int failCount;
    private final List<Float> respTimeList = new ArrayList<>();
    private PerfResultArgs perfResultParam;
    private static boolean perfProgress = false;


    public Executor(int threads, int warmUpThreads, Map<String, Integer[]> itrList ) {
        this(Runtime.getRuntime().availableProcessors(), threads, warmUpThreads, itrList);
    }

    public Executor(int threadPoolSize, int threads,  int warmUpThreads, Map<String, Integer[]> itrList ) {
        executor = Executors.newFixedThreadPool(threadPoolSize);
        this.threads = threads;
        this.warmUpThreads = warmUpThreads;
        this.itrList = itrList;
    }

    public synchronized void processTasks(List<BaseTask> taskList)  {

        perfResultParam = null;
        perfProgress = true;

        System.out.println("Start Time Stamp: " + LocalDateTime.now().toString());

        if (warmUpThreads > 0) {
            processTasks(taskList, warmUpThreads);
        }

        long startTime = System.nanoTime();
        List<Future<Result>> resultList = processTasks(taskList, threads);

        Result result;
        for (Future<Result> future : resultList) {
            try {
                result = future.get(20, TimeUnit.SECONDS);
                updateStatistics(result);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }

        System.out.println("End Time Stamp: " + LocalDateTime.now().toString());
        Collections.sort(respTimeList);
        summarizeStatistics((System.nanoTime() - startTime)/1000000);

        executor.shutdown();
        perfProgress = false;
    }

    private List<Future<Result>> processTasks(List<BaseTask> taskList, int itr)  {

        Random rand = new Random();
        BaseTask task;

        List<BaseTask> execTaskList = new ArrayList<>();
        for (int i = 0; i < itr; i++) {
            task = taskList.get(rand.nextInt(taskList.size()));
            execTaskList.add(task);
        }

        //Execute all tasks and get reference to Future objects
        List<Future<Result>> resultList = null;

        try {
            resultList = executor.invokeAll(execTaskList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public PerfResultArgs getPerfResult() {
        return this.perfResultParam;
    }

    public static boolean getPerfProgress() {
        return perfProgress;
    }

    private void updateStatistics(Result res) {

        for (FloatBooleanPairArgs pair: res.getResponse()) {
            if (pair.isValue()) {
                this.totalElapsedTime += pair.getType();
                this.respTimeList.add(pair.getType());
                this.passCount++;
            }
            else {
                this.failCount++;
            }
        }
    }

    private void summarizeStatistics(long totalTime) {

        Map<String, String> data = new LinkedHashMap<>();
        List<PerfItrResultArgs> itrResultParamList = new ArrayList<>(itrList.size());
        data.put("Threads", Integer.toString(this.threads));
        data.put("WarmUpThreads", Integer.toString(this.warmUpThreads));
        int itr = 0;
        for (Map.Entry<String, Integer[]> entry : itrList.entrySet()) {
            itr += entry.getValue()[0];
            data.put("Iterations (" + entry.getKey().split("[.]")[entry.getKey().split("[.]").length-1] + ")", Integer.toString(itr));
            data.put("WarmUpIterations (" + entry.getKey().split("[.]")[entry.getKey().split("[.]").length-1] + ")", Integer.toString(entry.getValue()[1]));
            itrResultParamList.add(new PerfItrResultArgs(entry.getKey(), entry.getValue()[0], entry.getValue()[1]));
        }
        data.put("Passed", Integer.toString(this.passCount));
        data.put("Failed", Integer.toString(this.failCount));
        float totalExecTime = (float)totalTime;
        data.put("ExecutionTime (millisecond)", String.format("%.4f", totalExecTime));
        data.put("ElapsedTime (millisecond)", String.format("%.4f", totalElapsedTime));

        double avgTime = respTimeList.stream().mapToDouble(val -> val).average().orElse(0.0);
        double tp = (this.threads*itr)/this.totalElapsedTime;

        float sd = calculateSD();
        float p25 = calculatePercentile(25);
        float p50 = calculatePercentile(50);
        float p75 = calculatePercentile(75);
        float p90 = calculatePercentile(90);
        float p95 = calculatePercentile(95);
        float p99 = calculatePercentile(99);
        float p99_9 = calculatePercentile(99.9f);
        float p99_99 = calculatePercentile(99.99f);
        float min = respTimeList.get(0);
        float max = respTimeList.get(respTimeList.size()-1);

        data.put("Throughput (req/millisecond)",  String.format("%.4f", tp));
        data.put("Mean (millisecond)",  String.format("%.4f", avgTime));
        data.put("StandardDeviation (millisecond)", String.format("%.4f", sd));
        data.put("Min (millisecond)", String.format("%.4f", min));
        data.put("P25 (millisecond)", String.format("%.4f", p25));
        data.put("P50 (millisecond)", String.format("%.4f", p50));
        data.put("P75 (millisecond)", String.format("%.4f", p75));
        data.put("P90 (millisecond)", String.format("%.4f", p90));
        data.put("P95 (millisecond)", String.format("%.4f", p95));
        data.put("P99 (millisecond)", String.format("%.4f", p99));
        data.put("P99.9 (millisecond)", String.format("%.4f", p99_9));
        data.put("P99.99 (millisecond)", String.format("%.4f", p99_99));
        data.put("Max (millisecond)", String.format("%.4f", max));

        printTable(data);

        perfResultParam = new PerfResultArgs(
                threads,
                warmUpThreads,
                itrResultParamList,
                passCount,
                failCount,
                totalExecTime,
                (float)totalElapsedTime,
                (float)tp,
                (float)avgTime,
                sd,
                min,
                p25,
                p50,
                p75,
                p90,
                p95,
                p99,
                p99_9,
                p99_99,
                max);
    }

    private float calculatePercentile(float percentile) {
        int index = (int)Math.ceil((percentile / 100) * respTimeList.size());
        if (index >= respTimeList.size()) {
            index = respTimeList.size() -1;
        }
        return respTimeList.get(index);
    }

    private float calculateSD()
    {
        double sum = 0.0, standardDeviation = 0.0;
        int length = respTimeList.size();

        for(double num : respTimeList) {
            sum += num;
        }

        double mean = sum/length;

        for(double num: respTimeList) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return (float)Math.sqrt(standardDeviation/length);
    }


    private void printTable(Map<String, String> data) {

        String line = new String(new char[72]).replace('\0', '-');
        System.out.println("\n");
        System.out.println(line);
        System.out.printf("| %s |\n", StringUtils.center("Summary", 68));
        System.out.println(line);

        String nameFormat = "| %1$-50s | ";
        String valueFormat = " %2$14s |%n";
        String format = nameFormat.concat(valueFormat);
        for (String ele : data.keySet()) {
            System.out.printf(format, ele, data.get(ele));
        }
        System.out.println(line);
    }
}
