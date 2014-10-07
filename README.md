## Ceph Data Migration

1. cd to project home directory and run command `gradle clean build distZip` to generate zip file(zip file in the `build/distributions` directory)
2. run follow command to make data migration
3. modify the yml files to correct config your migration info

```shell
    $ unzip ceph-data-migration.zip
    $ cd ceph-data-migration
    $ java -jar ceph-data-migration.jar
```

