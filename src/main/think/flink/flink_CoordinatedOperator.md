# CoordinatedOperatorFactory 
org.apache.flink.streaming.api.operators.CoordinatedOperatorFactory
```
public interface CoordinatedOperatorFactory<OUT> extends StreamOperatorFactory<OUT> {

    /**
     * The implementation should return an instance of {@link
     * org.apache.flink.runtime.operators.coordination.OperatorEventHandler}.
     */
    @Override
    <T extends StreamOperator<OUT>> T createStreamOperator(
            StreamOperatorParameters<OUT> parameters);

    /**
     * Get the operator coordinator provider for this operator.
     *
     * @param operatorName the name of the operator.
     * @param operatorID the id of the operator.
     * @return the provider of the {@link OperatorCoordinator} for this operator.
     */
    OperatorCoordinator.Provider getCoordinatorProvider(String operatorName, OperatorID operatorID);
}
```

# StreamOperatorFactory
```
/**
 * A factory to create {@link StreamOperator}.
 *
 * @param <OUT> The output type of the operator
 */
@Experimental
public interface StreamOperatorFactory<OUT> extends Serializable {

    /** Create the operator. Sets access to the context and the output. */
    <T extends StreamOperator<OUT>> T createStreamOperator(
            StreamOperatorParameters<OUT> parameters);

    /** Set the chaining strategy for operator factory. */
    void setChainingStrategy(ChainingStrategy strategy);

    /** Get the chaining strategy of operator factory. */
    ChainingStrategy getChainingStrategy();

    /** Is this factory for {@link StreamSource}. */
    default boolean isStreamSource() {
        return false;
    }

    default boolean isLegacySource() {
        return false;
    }

    /**
     * If the stream operator need access to the output type information at {@link StreamGraph}
     * generation. This can be useful for cases where the output type is specified by the returns
     * method and, thus, after the stream operator has been created.
     */
    default boolean isOutputTypeConfigurable() {
        return false;
    }

    /**
     * Is called by the {@link StreamGraph#addOperator} method when the {@link StreamGraph} is
     * generated. The method is called with the output {@link TypeInformation} which is also used
     * for the {@link StreamTask} output serializer.
     *
     * @param type Output type information of the {@link StreamTask}
     * @param executionConfig Execution configuration
     */
    default void setOutputType(TypeInformation<OUT> type, ExecutionConfig executionConfig) {}

    /** If the stream operator need to be configured with the data type they will operate on. */
    default boolean isInputTypeConfigurable() {
        return false;
    }

    /**
     * Is called by the {@link StreamGraph#addOperator} method when the {@link StreamGraph} is
     * generated.
     *
     * @param type The data type of the input.
     * @param executionConfig The execution config for this parallel execution.
     */
    default void setInputType(TypeInformation<?> type, ExecutionConfig executionConfig) {}

    /** Returns the runtime class of the stream operator. */
    Class<? extends StreamOperator> getStreamOperatorClass(ClassLoader classLoader);
}
```
# OneInputStreamOperatorFactory
A factory to create OneInputStreamOperator.
创建OneInputStreamOperator的一个工场.

# SimpleOperatorFactory
只需要传递一个operator
SimpleOperatorFactory.of(operator)
```
    return transform("Flat Map", outputType, new StreamFlatMap<>(clean(flatMapper)));
    public <R> SingleOutputStreamOperator<R> transform(
            String operatorName,
            TypeInformation<R> outTypeInfo,
            OneInputStreamOperator<T, R> operator) {

        return doTransform(operatorName, outTypeInfo, SimpleOperatorFactory.of(operator));
    }
```
# UdfStreamOperatorFactory
包含用户定义的函数

# WriteOperatorFactory (与hudi交互)
  private final AbstractWriteOperator<I> operator;
  private final Configuration conf;
  
```
/**
 * Factory class for {@link StreamWriteOperator}.
 */
public class WriteOperatorFactory<I>
    extends SimpleUdfStreamOperatorFactory<Object>
    implements CoordinatedOperatorFactory<Object>, OneInputStreamOperatorFactory<I, Object> {
  private static final long serialVersionUID = 1L;

  private final AbstractWriteOperator<I> operator;
  private final Configuration conf;

  public WriteOperatorFactory(Configuration conf, AbstractWriteOperator<I> operator) {
    super(operator);
    this.operator = operator;
    this.conf = conf;
  }

  public static <I> WriteOperatorFactory<I> instance(Configuration conf, AbstractWriteOperator<I> operator) {
    return new WriteOperatorFactory<>(conf, operator);
  }

//已有一个operator,创建的时候可以设置一些东西.
  @Override
  @SuppressWarnings("unchecked")
  public <T extends StreamOperator<Object>> T createStreamOperator(StreamOperatorParameters<Object> parameters) {
    final OperatorID operatorID = parameters.getStreamConfig().getOperatorID();
    final OperatorEventDispatcher eventDispatcher = parameters.getOperatorEventDispatcher();

    this.operator.setOperatorEventGateway(eventDispatcher.getOperatorEventGateway(operatorID));
    this.operator.setup(parameters.getContainingTask(), parameters.getStreamConfig(), parameters.getOutput());
    this.operator.setProcessingTimeService(this.processingTimeService);
    eventDispatcher.registerEventHandler(operatorID, operator);
    return (T) operator;
  }

  @Override
  public OperatorCoordinator.Provider getCoordinatorProvider(String s, OperatorID operatorID) {
    return new StreamWriteOperatorCoordinator.Provider(operatorID, this.conf);
  }
}

```

# StreamWriteFunction  -> AbstractStreamWriteFunction


# OperatorCoordinator
# OperatorCoordinatorHolder