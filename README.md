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

### Data validation

NOTE 1: for data validation the given path should correspond to a data value, usualy a path that constraints an ELEMENT.value attribute in the archetype.

NOTE 2: since a data set should be given, and each data value has different attributes, each data valu has it's own format for providing data:

- DV_QUANTITY: `magnitude|units` (magnitude is a number, units is a string)
- DV_ORDINAL: `value` or `terminology::code` or `value|terminology::code` (code is a string, value is a number, terminology is a string)
- CODE_PHRASE: `terminology::code` (code and terminology are both strings)
- DV_TEXT: `text` (text is a string, should be quoted if it has spaces)

```shell
$ gradle run --args="validate src/test/resources/archetypes/openEHR-EHR-OBSERVATION.blood_pressure.v2.adl /protocol[at0011]/items[at1038]/value 'hello hello'"
```

## Run from fat JAR

```shell
$ gradle build
$ gradle fatJar
$ java -jar app/build/libs/edu-openehr-archetypes-0.1-all.jar parse ../openEHR-ADL/lib/src/test/resources/archetypes/entry/observation/openEHR-EHR-OBSERVATION.blood_pressure.v1.adl
```
