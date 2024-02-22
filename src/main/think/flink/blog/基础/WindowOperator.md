# WindowOperator
implements the logic for windowing based on a WindowAssigner and Trigger.
通过 KeySelector 获取key,通过WindowAssigner 获取window
A pane is the bucket of elements that have the same key and same Window.
一个元素可以到多个