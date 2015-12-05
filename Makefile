
PATH = 'weka.jar:.'
MAIN = Benchmark

build:
	/usr/bin/javac -cp $(PATH) $(MAIN).java

run: build
	/usr/bin/java -cp $(PATH) $(MAIN) > results.csv
