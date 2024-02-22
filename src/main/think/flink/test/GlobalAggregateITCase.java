package test;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.taskexecutor.GlobalAggregateManager;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.operators.StreamingRuntimeContext;

import java.io.IOException;

public class GlobalAggregateITCase {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment streamExecutionEnvironment =
                StreamExecutionEnvironment.getExecutionEnvironment();

        streamExecutionEnvironment
                .addSource(new TestSourceFunction(new IntegerAggregateFunction(), false))
                .addSink(new DiscardingSink<>());

        streamExecutionEnvironment.execute();
    }
    private static class TestSourceFunction extends RichParallelSourceFunction<Integer> {

        private GlobalAggregateManager aggregateManager = null;
        private final AggregateFunction<Integer, Integer, Integer> aggregateFunction;
        private final boolean expectFailures;

        public TestSourceFunction(
                AggregateFunction<Integer, Integer, Integer> aggregateFunction,
                boolean expectFailures) {
            this.aggregateFunction = aggregateFunction;
            this.expectFailures = expectFailures;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            StreamingRuntimeContext runtimeContext = (StreamingRuntimeContext) getRuntimeContext();
            aggregateManager = runtimeContext.getGlobalAggregateManager();
        }

        @Override
        public void run(SourceContext<Integer> ctx) throws Exception {
            Integer expectedAccumulator = 0;
            int exceptionCount = 0;
            for (int i = 0; i < 5; i++) {
                Integer actualAccumlator = 0;
                try {
                    actualAccumlator =
                            aggregateManager.updateGlobalAggregate("testAgg", i, aggregateFunction);
                    expectedAccumulator += i;
                } catch (IOException e) {
                    exceptionCount++;
                }
                System.out.println(actualAccumlator+"------------");
                if (expectFailures) {
                   // assertEquals(i + 1, exceptionCount);
                } else {
                //    assertEquals(expectedAccumulator, actualAccumlator);
                }
            }
        }

        @Override
        public void cancel() {}
    }

    /** Simple integer aggregate function. */
    private static class IntegerAggregateFunction
            implements AggregateFunction<Integer, Integer, Integer> {

        @Override
        public Integer createAccumulator() {
            return 0;
        }

        @Override
        public Integer add(Integer value, Integer accumulator) {
            return value + accumulator;
        }

        @Override
        public Integer getResult(Integer accumulator) {
            return accumulator;
        }

        @Override
        public Integer merge(Integer accumulatorA, Integer accumulatorB) {
            return add(accumulatorA, accumulatorB);
        }
    }

    /** Aggregate function that throws NullPointerException. */
    private static class ExceptionThrowingAggregateFunction
            implements AggregateFunction<Integer, Integer, Integer> {

        @Override
        public Integer createAccumulator() {
            return 0;
        }

        @Override
        public Integer add(Integer value, Integer accumulator) {
            throw new NullPointerException("test");
        }

        @Override
        public Integer getResult(Integer accumulator) {
            return accumulator;
        }

        @Override
        public Integer merge(Integer accumulatorA, Integer accumulatorB) {
            return add(accumulatorA, accumulatorB);
        }
    }
}