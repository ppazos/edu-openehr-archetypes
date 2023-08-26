# edu-openehr-archetypes

openEHR archetypes in software: examples for CaboLabs' training courses

## Run from Gradle

NOTE: when running with gradle, the current working directory is ./app not ./ so the path to archetype needs an extra ../ if using relative paths.

```shell
$ gradle build
$ gradle run --args="parse ../../openEHR-ADL/lib/src/test/resources/archetypes/entry/observation/openEHR-EHR-OBSERVATION.blood_pressure.v1.adl"
```

## Run from fat JAR

```shell
$ gradle build
$ gradle fatJar
$ java -jar app/build/libs/edu-openehr-archetypes-0.1-all.jar parse ../openEHR-ADL/lib/src/test/resources/archetypes/entry/observation/openEHR-EHR-OBSERVATION.blood_pressure.v1.adl
```
