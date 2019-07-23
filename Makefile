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

