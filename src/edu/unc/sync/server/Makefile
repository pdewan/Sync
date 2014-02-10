JAVAC=javac
SYNC_BIN=../../../../bin/
SYNC_SERVER=$(SYNC_BIN)/edu/unc/sync/server

all: *.class

.SUFFIXES:.java .class

SyncServer.class: SyncServer.java
	javac SyncServer.java; cp SyncServer*.class $(SYNC_SERVER)/; rmic -d $(SYNC_BIN) edu.unc.sync.server.SyncServer

SyncClient.class: SyncClient.java
	javac SyncClient.java; cp SyncClient*.class $(SYNC_SERVER)/;rmic -d $(SYNC_BIN) edu.unc.sync.server.SyncClient

TreeModelProxy.class: TreeModelProxy.java
	javac TreeModelProxy.java; cp TreeModelProxy*.class $(SYNC_SERVER)/;rmic -d $(SYNC_BIN) edu.unc.sync.server.TreeModelProxy

TreeModelServer.class: TreeModelServer.java
	javac TreeModelServer.java; cp TreeModelServer*.class $(SYNC_SERVER)/;rmic -d $(SYNC_BIN) edu.unc.sync.server.TreeModelServer

.java.class:;$(JAVAC) $*.java;cp $**.class $(SYNC_SERVER)/ 
