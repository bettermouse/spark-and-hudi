# MailboxProcessor
是一种线程安全的模型,任务交给一个主线程执行
mailbox-based execution model
runMailboxLoop() MailboxDefaultAction 在循环中
This model ensures single-threaded execution between the default action

MailboxDefaultAction通过 MailboxController 与这个类交流
The MailboxDefaultAction interacts with this class through the MailboxProcessor.
MailboxController to communicate control flow changes to the mailbox loop, 
e.g. that invocations of the default action are temporarily or permanently exhausted

如果仅使用信息模型是愚蠢的,总不能一直往信箱里面发默认行为

## MailboxDefaultAction
重复执行的行为

this.mailboxProcessor = new MailboxProcessor(this::processInput, mailbox, actionExecutor);
### StreamTask
```
    /**
     * This method implements the default action of the task (e.g. processing one event from the
     * input). Implementations should (in general) be non-blocking.
     *
     * @param controller controller object for collaborative interaction between the action and the
     *     stream task.
     * @throws Exception on any problems in the action.
     */
    protected void processInput(MailboxDefaultAction.Controller controller) throws Exception {
        //处理数据
        InputStatus status = inputProcessor.processInput();
        //有更多数据,可以输出
        if (status == InputStatus.MORE_AVAILABLE && recordWriter.isAvailable()) {
            return;
        }
        //如果没有输出,结束 邮箱
        if (status == InputStatus.END_OF_INPUT) {
            controller.allActionsCompleted();
            return;
        }

        TaskIOMetricGroup ioMetrics = getEnvironment().getMetricGroup().getIOMetricGroup();
        TimerGauge timer;
        CompletableFuture<?> resumeFuture;
        //如果不可写,说明反压
        if (!recordWriter.isAvailable()) {
            timer = ioMetrics.getBackPressuredTimePerSecond();
            resumeFuture = recordWriter.getAvailableFuture();
        } else {
        //或者是没有输入的数据
            timer = ioMetrics.getIdleTimeMsPerSecond();
            resumeFuture = inputProcessor.getAvailableFuture();
        }
        // 创建的时候 timer.markStart();
        //运行的时候
        // @Override
        // public void run() {
        //     timer.markEnd();
        //     suspendedDefaultAction.resume();
        // }
        assertNoException(
                resumeFuture.thenRun(
                        new ResumeWrapper(controller.suspendDefaultAction(timer), timer)));
    }
```

### Suspension
Represents the suspended state of a MailboxDefaultAction, ready to resume.
### Controller

将一个方法放入其它线程执行,在执行的时候,该方法 还要能控制其它线程的变量.


# 总结 
mainMailboxExecutor 具体执行了什么任务
restoreGates 不清楚
triggerCheckpointAsync
dispatchOperatorEvent
mailboxDefaultAction