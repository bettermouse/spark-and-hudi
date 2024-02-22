# 多个数据源水位对齐

## Block SourceReader when watermarks are out of alignment
https://issues.apache.org/jira/browse/FLINK-24441
## SourceOperator
add 

WAITING_FOR_ALIGNMENT

```
/** Can be not completed only in {@link OperatingMode#WAITING_FOR_ALIGNMENT} mode. */
private CompletableFuture<Void> waitingForAlignmentFuture =
        CompletableFuture.completedFuture(null);
        
    @Override
    public CompletableFuture<?> getAvailableFuture() {
        switch (operatingMode) {
            case WAITING_FOR_ALIGNMENT:
                return availabilityHelper.update(waitingForAlignmentFuture);
            case OUTPUT_NOT_INITIALIZED:
            case READING:
                return availabilityHelper.update(sourceReader.isAvailable());
            case SOURCE_STOPPED:
            case SOURCE_DRAINED:
            case DATA_FINISHED:
                return AvailabilityProvider.AVAILABLE;
            default:
                throw new IllegalStateException("Unknown operating mode: " + operatingMode);
        }
    }



                if (watermarkAlignmentParams.isEnabled()) {
                    // Only wrap the output when watermark alignment is enabled, as otherwise this
                    // introduces a small performance regression (probably because of an extra
                    // virtual call)
                    processingTimeService.scheduleWithFixedDelay(
                            this::emitLatestWatermark,
                            watermarkAlignmentParams.getUpdateInterval(),
                            watermarkAlignmentParams.getUpdateInterval());
                }
```