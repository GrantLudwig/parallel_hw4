CLASSES = RandomArrayGenerator.class BitonicSynchronized.class BitonicThreadLoop.class
JAVAFLAGS = -J-Xmx48m


all: $(CLASSES)

%.class : %.java
	javac $(JAVAFLAGS) $<

clean:
	@rm -f *.class
