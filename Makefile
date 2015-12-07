# Makefile for running the benchmarks.
MAIN = Benchmark

all: run-serial run-parallel-simple

build-serial:
	$(eval WEKA_JAR_PATH := 'weka.jar:lib/*:.')
	/usr/bin/javac -cp $(WEKA_JAR_PATH) $(MAIN).java

run-serial: build-serial
	/usr/bin/java -cp $(WEKA_JAR_PATH) $(MAIN) a.csv b.csv 10 2 #> results.csv


build-parallel-simple:
	$(eval WEKA_JAR_PATH := 'weka-parallel-simple.jar:lib/*:.')
	/usr/bin/javac -cp $(WEKA_JAR_PATH) $(MAIN).java

run-parallel-simple: build-parallel-simple
	/usr/bin/java -cp $(WEKA_JAR_PATH) $(MAIN) a.csv b.csv 10 2 #> results.csv
