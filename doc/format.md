File format
===========


File format for `long` - `long` dictionary.


Log File
--------

- Sequence of Update Entries.
- There is no file header.

Update Entry format:

- spacer, long filled with zeroes, used to detect offset errors
- number of key-value pairs  in  this entry (int)
- number of tombstones in entry (int)
- key table (longs)
- value table (longs)


Link File
---------

- Sequence of Link Entries
- It links Update Entries from Log File to their previous entries
- There is no file header

Link Entry format:

- spacer, long filled with zeroes, used to detect offset errors
- number of branches (int)
- link of current Update Entry
- table of previous keys - link pairs


Link
----

- position within log files
- 8 byte long
    - first 4 bytes is file number (signed int)
    - second 4 bytes is file offset (signed int)


File naming
-----------

- Log File name has this format: `$fileNumber-$fileVersion.log`
    - replay should only use file with highest $fileVersion

- Link File name has this format: `$filenum.link`

