# edu-openehr-archetypes

openEHR archetypes in software: examples for CaboLabs' training courses

## Build

```shell
$ gradle build
$ gradle fatJar
```

## Run from Gradle

NOTE: when running with gradle, the current working directory is ./app not ./ so the path to archetype needs an extra ../ if using relative paths.

### Try parse an archetype (returns an error if the ADL is not correct)

```shell
$ gradle run --args="parse ../../openEHR-ADL/lib/src/test/resources/archetypes/entry/observation/openEHR-EHR-OBSERVATION.blood_pressure.v1.adl"
```

### Show internal structure with paths

```shell
$ gradle run --args="traverse src/test/resources/archetypes/openEHR-EHR-OBSERVATION.blood_pressure.v2.adl"
```

### Show constraint at path

```shell
$ gradle run --args="constraint src/test/resources/archetypes/openEHR-EHR-OBSERVATION.blood_pressure.v2.adl /data[at0001]/events[at0006]/data[at0003]/items[at0004]/value"
```

## Run from fat JAR

```shell
$ gradle build
$ gradle fatJar
$ java -jar app/build/libs/edu-openehr-archetypes-0.1-all.jar parse ../openEHR-ADL/lib/src/test/resources/archetypes/entry/observation/openEHR-EHR-OBSERVATION.blood_pressure.v1.adl
```
