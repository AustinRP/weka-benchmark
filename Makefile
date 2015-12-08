# Makefile for running the benchmarks.

MAIN = Benchmark
WEKA_SRC_DIR = weka-3-6-13-stable/weka
TEST_SIZES = 128x128 256x256 512x512 1024x1024 2048x2048 4096x4096

all: run-serial run-parallel-simple

clean:
	rm -f *.class *.jar *.csv

clean-csv:
	rm -f *.csv


# Weka benchmarking fun.
build-serial: weka.jar
	$(eval WEKA_JAR_PATH := 'weka.jar:lib/*:.')
	/usr/bin/javac -cp $(WEKA_JAR_PATH) $(MAIN).java

run-serial: build-serial a_128x128.csv b_128x128.csv
	$(eval WEKA_JAR_PATH := 'weka.jar:lib/*:.')
	$(foreach sz, $(TEST_SIZES), /usr/bin/java -cp $(WEKA_JAR_PATH) $(MAIN) serial_$(sz).csv a_$(sz).csv b_$(sz).csv 10;)


build-parallel-simple: weka-parallel-simple.jar
	$(eval WEKA_JAR_PATH := 'weka-parallel-simple.jar:lib/*:.')
	/usr/bin/javac -cp $(WEKA_JAR_PATH) $(MAIN).java

run-parallel-simple: build-parallel-simple a_128x128.csv b_128x128.csv
	$(eval WEKA_JAR_PATH := 'weka-parallel-simple.jar:lib/*:.')
	$(foreach sz, $(TEST_SIZES), /usr/bin/java -cp $(WEKA_JAR_PATH) $(MAIN) parallel-simple_$(sz).csv a_$(sz).csv b_$(sz).csv 10;)


build-parallel-smart: weka-parallel-smart.jar
	$(eval WEKA_JAR_PATH := 'weka-parallel-smart.jar:lib/*:.')
	/usr/bin/javac -cp $(WEKA_JAR_PATH) $(MAIN).java

run-parallel-smart: build-parallel-smart a_128x128.csv b_128x128.csv
	$(eval WEKA_JAR_PATH := 'weka-parallel-smart.jar:lib/*:.')
	$(foreach sz, $(TEST_SIZES), /usr/bin/java -cp $(WEKA_JAR_PATH) $(MAIN) parallel-smart_$(sz).csv a_$(sz).csv b_$(sz).csv 10;)



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
a_128x128.csv:
	python genmat.py a_128x128.csv 128 128
	python genmat.py a_256x256.csv 256 256
	python genmat.py a_512x512.csv 512 512
	python genmat.py a_1024x1024.csv 1024 1024
	python genmat.py a_2048x2048.csv 2048 2048
	python genmat.py a_4096x4096.csv 4096 4096

b_128x128.csv:
	python genmat.py b_128x128.csv 128 128
	python genmat.py b_256x256.csv 256 256
	python genmat.py b_512x512.csv 512 512
	python genmat.py b_1024x1024.csv 1024 1024
	python genmat.py b_2048x2048.csv 2048 2048
	python genmat.py b_4096x4096.csv 4096 4096

