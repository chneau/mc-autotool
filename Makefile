.SILENT:
.ONESHELL:
.NOTPARALLEL:
.EXPORT_ALL_VARIABLES:
.PHONY: run exec build clean

name=$(shell basename $(CURDIR))
gradleParam=--console=plain

run: test

test:
	bash gradlew ${gradleParam} runClient

build:
	bash gradlew ${gradleParam} build

clean:
	bash gradlew ${gradleParam} clean

genSources:
	bash gradlew ${gradleParam} genSources

runServer:
	bash gradlew ${gradleParam} runServer

getVersion:
	curl -sSL https://meta.fabricmc.net/v1/versions/loader/${MC} | jq -r '. | map(select(.loader.stable==true)) | "yarn_mappings="+.[0].loader.version , "loader_version="+.[0].mappings.version'
