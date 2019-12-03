JFLAGS = -g
JC = javac
J = java
CLIENT = gui.PlayPoker
CENTRAL = server.CentralServer
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

PACKAGES := \
	game \
	gui \
	server \

CLASSES := $(shell find $(PACKAGES) -type f -name '*.java')

.PHONY: default clean

.PHONY: default central

.PHONY: default play

default: classes

classes: $(CLASSES:.java=.class)

all:  $(CLS)

clean:
		$(RM) $(shell find $(PACKAGES) -type f -name '*.class')

central:
		$(J) $(CENTRAL) 

client:
		$(J) $(CLIENT) 
