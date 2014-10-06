## Ceph Data Migration

1. cd to project home directory and run command `gradle clean build distZip` to generate zip file
2. run follow command to make data migration

```shell
    unzip ceph-data-migration.zip
    cd ceph-data-migration
    java -jar ceph-data-migration.jar
```