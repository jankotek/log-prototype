# log-prototype

Prototype  LSM key-value store.
[Design specification](https://github.com/input-output-hk/iodb/blob/master/doc/design_spec.md)
 is derived from
[IODB project](https://github.com/input-output-hk/iodb).

This project is not full database engine, but design prototype.
It explores branching snapshots, background compaction and disk space allocation
on append-only store.

As prototype it has some limitations:

- keys and values are primitive 8-byte longs (easier to test compaction)
- there is no close method, resource management relies on JVM garbage collector
- store can not be reopened, data are lost after JVM exits

If this prototype is successful (background compaction is stable),
it will be integrated into MapDB4 in form of:

- java.util.SortedMap
- Mutable and Immutable Scala maps
- MapDB Store
- primitive ECJ collections such as LongLongMap
