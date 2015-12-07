# Makefile for running the benchmarks.

MAIN = Benchmark
WEKA_SRC_DIR = weka-3-6-13-stable/weka


all: run-serial run-parallel-simple

clean:
	rm -f *.class *.jar *.csv

clean-csv:
	rm -f *.csv


# Weka benchmarking fun.
build-serial: weka.jar
	$(eval WEKA_JAR_PATH := 'weka.jar:lib/*:.')
	/usr/bin/javac -cp $(WEKA_JAR_PATH) $(MAIN).java

run-serial: build-serial a.csv b.csv
	/usr/bin/java -cp $(WEKA_JAR_PATH) $(MAIN) serial.csv a.csv b.csv 10


build-parallel-simple: weka-parallel-simple.jar
	$(eval WEKA_JAR_PATH := 'weka-parallel-simple.jar:lib/*:.')
	/usr/bin/javac -cp $(WEKA_JAR_PATH) $(MAIN).java

run-parallel-simple: build-parallel-simple a.csv b.csv
	/usr/bin/java -cp $(WEKA_JAR_PATH) $(MAIN) parallel-simple.csv a.csv b.csv 10


# Weka build fun. Assumes Apache ANT is available.
# Generates the JAR files we need for testing.
weka.jar:
	cd $(WEKA_SRC_DIR); \
	git checkout master; \
	ant -q clean; ant -q exejar; \
	cp dist/weka.jar ../../weka.jar

weka-parallel-simple.jar:
	cd $(WEKA_SRC_DIR); \
	git checkout parallel-simple; \
	ant -q clean; ant -q exejar; \
	cp dist/weka.jar ../../weka-parallel-simple.jar

weka-parallel-smart.jar:
	cd $(WEKA_SRC_DIR); \
	git checkout parallel-smart; \
	ant -q clean; ant -q exejar; \
	cp dist/weka.jar ../../weka-parallel-smart.jar


# Data build fun. Assumes the genmat.py script is available.
a.csv:
	python genmat.py a.csv 1024 1024

b.csv:
	python genmat.py b.csv 1024 1024

