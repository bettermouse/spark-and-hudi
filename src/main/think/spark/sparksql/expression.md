## BoundReference
A bound reference points to a specific slot in the input tuple, allowing the actual value to be retrieved more efficiently. However, since operations like column pruning can change the layout of intermediate tuples, BindReferences should be run after all such transformations.