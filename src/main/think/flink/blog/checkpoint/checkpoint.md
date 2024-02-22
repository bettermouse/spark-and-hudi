## Integrate Operator Coordinators with Checkpoints
The operator coordinators are stateful and hence need to store state in checkpoints.
The initial implementation approach is to trigger coordinator checkpoints first, and when all coordinator checkpoints are done, then the source checkpoint barriers will be injected.

Note: This functionality will eventually replace the checkpoint master hooks.

在了解flip-27 对checkpoint中的 operatorCoordinator 的状态数据如何备份产生了疑问,所以关注一下flink checkpoint的源码
OperatorCoordinatorCheckpoints.triggerAndAcknowledgeAllCoordinatorCheckpointsWithCompletion(
									coordinatorsToCheckpoint, pendingCheckpoint, timer),
							timer);

https://blog.jrwang.me/2019/flink-source-code-checkpoint/				
# CheckpointCoordinator