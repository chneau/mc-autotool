.SILENT:
.ONESHELL:
.NOTPARALLEL:
.EXPORT_ALL_VARIABLES:
.PHONY: run exec build clean

name=$(shell basename $(CURDIR))

run: test

test:
	bash gradlew runClient

build:
	bash gradlew build

clean:
	bash gradlew clean

genSources:
	bash gradlew genSources

runServer:
	bash gradlew runServer

getVersion:
	curl -sSL https://meta.fabricmc.net/v1/versions/loader/${MC} | jq -r '. | map(select(.loader.stable==true)) | "yarn_mappings="+.[0].loader.version , "loader_version="+.[0].mappings.version'
