# time-consuming plug in flink
git-commit-id-plugin
spotless-maven-plugin  -Dspotless.check.skip=true
# flink 本地模式设置 savepoint
## 方法一
```
jobGraph.setSavepointRestoreSettings(SavepointRestoreSettings.forPath("G:\\flinkState\\savepoint-829d9b-06c0b51011ef",true));
```
## 方法二
https://blog.csdn.net/yy8623977/article/details/125659249
CliFrontend

使用PackagedProgram封装Flink程序,然后构建JobGraph,提交Flink集群
https://blog.csdn.net/czladamling/article/details/125204087
```
//{rest.port=9999, execution.target=local, execution.attached=true, parallelism.default=12}
//这个是flink 本地的默认端口
  def main(args: Array[String]): Unit = {
    //设置必要的属性,包括taskManager数量,slot之类
    val flinkConfig = new Configuration()
    //如果绑定端口0,其实就是随便绑定一个端口
    flinkConfig.setInteger(RestOptions.PORT, 9999);
    val miniClusterConfig: MiniClusterConfiguration =
      new MiniClusterConfiguration.Builder()
        .setConfiguration(flinkConfig)
        .setNumTaskManagers(3)
        .setNumSlotsPerTaskManager(1)
        .build()
    val cluster = new MiniCluster(miniClusterConfig)
    cluster.start()
  }
```

MiniCluster
MiniClusterClient
## AMRMClientAsync
通过yarn的 api 向yarn申请资源,具体的同spark 类似.

# rpc 
RpcService
AkkaRpcService

rcp是通过动态代理来实现的 InvocationHandler

# Task JobMaster debug
## TaskExecutor taskManager start taskExecutor

org.apache.flink.runtime.minicluster.MiniCluster#startTaskManager
```
public void startTaskManager() throws Exception {
    synchronized (lock) {
        final Configuration configuration = miniClusterConfiguration.getConfiguration();

        final TaskExecutor taskExecutor =
                TaskManagerRunner.startTaskManager(
                        configuration,
                        new ResourceID(UUID.randomUUID().toString()),
                        taskManagerRpcServiceFactory.createRpcService(),
                        haServices,
                        heartbeatServices,
                        metricRegistry,
                        blobCacheService,
                        useLocalCommunication(),
                        ExternalResourceInfoProvider.NO_EXTERNAL_RESOURCES,
                        taskManagerTerminatingFatalErrorHandlerFactory.create(
                                taskManagers.size()));

        taskExecutor.start();
        taskManagers.add(taskExecutor);
    }
}
```
org.apache.flink.runtime.taskexecutor.TaskManagerRunner
org.apache.flink.runtime.taskexecutor.TaskManagerRunner#createTaskExecutorService
https://blog.csdn.net/wuxintdrh/article/details/127910339

## 启动了taskExecutor
debug 看谁提交了task
org.apache.flink.runtime.taskexecutor.TaskExecutor#submitTask

# other

## StreamTask
org.apache.flink.streaming.runtime.tasks.StreamTask
org.apache.flink.runtime.operators.BatchTask

```
org.apache.flink.streaming.runtime.tasks.StreamTask#executeRestore
这里面有状态恢复的代码
org.apache.flink.streaming.runtime.tasks.StreamTask#restoreGates
```
### streamTask子类
org.apache.flink.streaming.runtime.tasks.StreamTask#inputProcessor
find by button find usage 

### AbstractInvokable 
该类在 Task中被初始化,是streamTask 实现的接口
all of subclass of this class.Must have a constractor with 1 fields
```
    private static AbstractInvokable loadAndInstantiateInvokable(
            ClassLoader classLoader, String className, Environment environment) throws Throwable {

        final Class<? extends AbstractInvokable> invokableClass;
        try {
            invokableClass =
                    Class.forName(className, true, classLoader).asSubclass(AbstractInvokable.class);
        } catch (Throwable t) {
            throw new Exception("Could not load the task's invokable class.", t);
        }

        Constructor<? extends AbstractInvokable> statelessCtor;

        try {
            statelessCtor = invokableClass.getConstructor(Environment.class);
        } catch (NoSuchMethodException ee) {
            throw new FlinkException("Task misses proper constructor", ee);
        }

```
## StreamInputProcessor
被StreamTask处理记录的接口

## org.apache.flink.runtime.taskmanager.Task

# mailbox
https://blog.csdn.net/qq_21383435/article/details/122771535
```
class StreamTask {
    protected void processInput(MailboxDefaultAction.Controller controller) throws Exception {
        InputStatus status = inputProcessor.processInput(); //处理输入
        if (status == InputStatus.MORE_AVAILABLE && recordWriter.isAvailable()) {
            return;
        }
        if (status == InputStatus.END_OF_INPUT) {
            // 没有后续的输入了，告知 MailboxDefaultAction.Controller 
            controller.allActionsCompleted();
            return;
        }

        // 暂时没有输入的情况
        TaskIOMetricGroup ioMetrics = getEnvironment().getMetricGroup().getIOMetricGroup();
        TimerGauge timer;
        CompletableFuture<?> resumeFuture;
        if (!recordWriter.isAvailable()) {
            timer = ioMetrics.getBackPressuredTimePerSecond();
            resumeFuture = recordWriter.getAvailableFuture();
        } else {
            timer = ioMetrics.getIdleTimeMsPerSecond();
            resumeFuture = inputProcessor.getAvailableFuture();
        }
        // 一旦有输入了，就告知 controller 要恢复 MailboxDefaultAction 的处理
        assertNoException(
                resumeFuture.thenRun(
                        // 首先会暂停 MailboxDefaultAction 的处理
                        new ResumeWrapper(controller.suspendDefaultAction(timer), timer)));
    }

    private static class ResumeWrapper implements Runnable {
        private final Suspension suspendedDefaultAction;
        private final TimerGauge timer;

        public ResumeWrapper(Suspension suspendedDefaultAction, TimerGauge timer) {
            this.suspendedDefaultAction = suspendedDefaultAction;
            timer.markStart();
            this.timer = timer;
        }

        @Override
        public void run() {
            timer.markEnd();
            suspendedDefaultAction.resume();
        }
    }
}

```

# maven flink 编译
mvn  install -DsocksProxyHost=127.0.0.1 -DsocksProxyPort=10808  -DskipTests -Drat.skip=true -T 8 -Dskip.npm


# flink type 
// TypeInformation可以通过  TypeInformation.of
TypeInformation<HoodieRecord> of = TypeInformation.of(HoodieRecord.class);
// 也可以通过 types
import org.apache.flink.api.common.typeinfo.Types;
TypeInformation<Fvp> pojo1 = Types.POJO(Fvp.class);
```
//这个是我对flink typeinformat的理解
TypeInformation<ColdWaybillDetail> x =null;
TypeSerializer<ColdWaybillDetail> serializer = x.createSerializer(null);
TypeSerializerSnapshot<ColdWaybillDetail> coldWaybillDetailTypeSerializerSnapshot = serializer.snapshotConfiguration();
coldWaybillDetailTypeSerializerSnapshot.resolveSchemaCompatibility()
```

# flink rocksdb 内存泄露问题

https://github.com/facebook/rocksdb/wiki/Block-Cache
https://github.com/apache/flink/blob/master/flink-state-backends/flink-statebackend-rocksdb/src/main/java/org/apache/flink/contrib/streaming/state/RocksDBMemoryControllerUtils.java#L64