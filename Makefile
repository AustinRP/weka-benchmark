# Makefile for running the benchmarks.

MAIN = Benchmark
WEKA_SRC_DIR = weka-3-6-13-stable/weka


all: run-serial run-parallel-simple

clean:
	rm -f *.class

build-serial: build-weka-serial
	$(eval WEKA_JAR_PATH := 'weka.jar:lib/*:.')
	/usr/bin/javac -cp $(WEKA_JAR_PATH) $(MAIN).java

run-serial: build-serial
	/usr/bin/java -cp $(WEKA_JAR_PATH) $(MAIN) a.csv b.csv 10 2 #> results.csv


build-parallel-simple: build-weka-parallel-simple
	$(eval WEKA_JAR_PATH := 'weka-parallel-simple.jar:lib/*:.')
	/usr/bin/javac -cp $(WEKA_JAR_PATH) $(MAIN).java

run-parallel-simple: build-parallel-simple
	/usr/bin/java -cp $(WEKA_JAR_PATH) $(MAIN) a.csv b.csv 10 2 #> results.csv



# Weka build fun. Assumes Apache ANT is available.
# Generates the JAR files we need for testing.
build-weka-serial:
	cd $(WEKA_SRC_DIR); \
	git checkout master; \
	ant -q clean; ant -q exejar; \
	cp dist/weka.jar ../../weka.jar

build-weka-parallel-simple:
	cd $(WEKA_SRC_DIR); \
	git checkout parallel-simple; \
	ant -q clean; ant -q exejar; \
	cp dist/weka.jar ../../weka-parallel-simple.jar

build-weka-parallel-smart:
	cd $(WEKA_SRC_DIR); \
	git checkout parallel-smart; \
	ant -q clean; ant -q exejar; \
	cp dist/weka.jar ../../weka-parallel-smart.jar
