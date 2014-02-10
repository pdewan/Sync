ORIGIN		= Symantec Café
ORIGIN_VER	= Version 7.22
VERSION		= DEBUG

!IFDEF SUB_DEBUG
DEBUG		= $(SUB_DEBUG)
NDEBUG		= !$(SUB_DEBUG)
!ELSE
DEBUG		= 1
NDEBUG		= 0
!ENDIF

PROJ		= Sync
APPTYPE		= JAVA CONSOLE
PROJTYPE	= class

JAVAC		= javac
MAKE		= SMAKE
LNK		= LINK


HEADERS		= 
!IF $(DEBUG)
OUTPUTDIR	= h:\buzzard\dewan\colab\sync\bin
!IF EXIST (h:\buzzard\dewan\colab\sync\bin)
CREATEOUTPUTDIR	=
!ELSE
CREATEOUTPUTDIR	= if not exist $(OUTPUTDIR)\*.* md $(OUTPUTDIR)
!ENDIF
TARGETDIR	= .
CREATETARGETDIR	=

CFLAGS		=  -g 
DEFINES		= 
!ELSE
OUTPUTDIR	= .
CREATEOUTPUTDIR	=
TARGETDIR	= .
CREATETARGETDIR	=

CFLAGS		=  -O 
DEFINES		= 
!ENDIF

MFLAGS		= MASTERPROJ=$(PROJ) 
DEBUGGERFLAGS	=  
PAR		= PROJS BATS OBJS

INCLUDES	= -classpath .;C:\CAFE\BIN\..\Java\Lib\classes.zip;C:\CAFE\BIN\..\Java\Lib\symclass.zip;$(OUTPUTDIR)



OBJS		=  $(OUTPUTDIR)\edu\unc\sync\AbstractMergeAction.class  $(OUTPUTDIR)\edu\unc\sync\Atomic.class  $(OUTPUTDIR)\edu\unc\sync\AtomicChange.class  \
		 $(OUTPUTDIR)\edu\unc\sync\AtomicMergeMatrix.class  $(OUTPUTDIR)\edu\unc\sync\BasicMergeActions.class  $(OUTPUTDIR)\edu\unc\sync\Change.class  $(OUTPUTDIR)\edu\unc\sync\ChangePair.class  \
		 $(OUTPUTDIR)\edu\unc\sync\ChangeSet.class  $(OUTPUTDIR)\edu\unc\sync\Delegated.class  $(OUTPUTDIR)\edu\unc\sync\DelegatedReplicatedDictionary.class  $(OUTPUTDIR)\DelegatedReplicatedObject.class  \
		 $(OUTPUTDIR)\DelegatedReplicatedSequence.class  $(OUTPUTDIR)\DelegatedUtils.class  $(OUTPUTDIR)\DelegationInterface.class  $(OUTPUTDIR)\DelegationObjectInterface.class  \
		 $(OUTPUTDIR)\DelegationVectorInterface.class  $(OUTPUTDIR)\DictionaryChangeSet.class  $(OUTPUTDIR)\DictionaryKeyChange.class  $(OUTPUTDIR)\DictionaryMergeMatrix.class  \
		 $(OUTPUTDIR)\DictionaryModifyChange.class  $(OUTPUTDIR)\DictionaryPutChange.class  $(OUTPUTDIR)\DictionaryRemoveChange.class  $(OUTPUTDIR)\ElementChange.class  \
		 $(OUTPUTDIR)\ElementChangePair.class  $(OUTPUTDIR)\FieldModifyChange.class  $(OUTPUTDIR)\GenericChangeSet.class  $(OUTPUTDIR)\GlobalObject.class  \
		 $(OUTPUTDIR)\MergeAction.class  $(OUTPUTDIR)\MergeMatrix.class  $(OUTPUTDIR)\ModifyChange.class  $(OUTPUTDIR)\NullChange.class  \
		 $(OUTPUTDIR)\NullElementChange.class  $(OUTPUTDIR)\NullSet.class  $(OUTPUTDIR)\ObjectID.class  $(OUTPUTDIR)\ObjectMergeMatrix.class  \
		 $(OUTPUTDIR)\RecordChangeSet.class  $(OUTPUTDIR)\RecordMergeMatrix.class  $(OUTPUTDIR)\Replicated.class  $(OUTPUTDIR)\ReplicatedAtomic.class  \
		 $(OUTPUTDIR)\ReplicatedBeanInfo.class  $(OUTPUTDIR)\ReplicatedCollection.class  $(OUTPUTDIR)\ReplicatedDictionary.class  $(OUTPUTDIR)\ReplicatedFloat.class  \
		 $(OUTPUTDIR)\ReplicatedInteger.class  $(OUTPUTDIR)\ReplicatedObject.class  $(OUTPUTDIR)\ReplicatedRecord.class  $(OUTPUTDIR)\ReplicatedReference.class  \
		 $(OUTPUTDIR)\ReplicatedSequence.class  $(OUTPUTDIR)\ReplicatedString.class  $(OUTPUTDIR)\ReplicatedStringBeanInfo.class  $(OUTPUTDIR)\ReplicationException.class  \
		 $(OUTPUTDIR)\SequenceChangeSet.class  $(OUTPUTDIR)\SequenceDeleteChange.class  $(OUTPUTDIR)\SequenceInsertChange.class  $(OUTPUTDIR)\SequenceMergeMatrix.class  \
		 $(OUTPUTDIR)\SequenceModifyChange.class  $(OUTPUTDIR)\SequenceMoveChange.class  $(OUTPUTDIR)\Sync.class 

BATS		= 

.SUFFIXES: .JAVA .CLASS 

.JAVA.CLASS:
	$(JAVAC) $(CFLAGS) $(INCLUDES) $*.java



all:	noteout createdir $(PRECOMPILE) $(OBJS) $(POSTCOMPILE) _done


all2:	createdir $(PRECOMPILE) $(OBJS) $(POSTCOMPILE) _done

noteout:
	REM Output to $(OUTPUTDIR)

createdir:
	$(CREATEOUTPUTDIR)
	$(CREATETARGETDIR)
	
_done:
		REM  Project is up to date

buildall:	clean	all


clean:
		-del $(TARGETDIR)\$$SCW$$.$(PROJTYPE)
		-del $(TARGETDIR)\$(PROJ).CLE
		-del Sync.dpd
		-del $(OBJS)



!IF EXIST (Sync.dpd)
!INCLUDE Sync.dpd
!ENDIF



$(OUTPUTDIR)\edu\unc\sync\AbstractMergeAction.class:	AbstractMergeAction.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) AbstractMergeAction.java



$(OUTPUTDIR)\edu\unc\sync\Atomic.class:	Atomic.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) Atomic.java



$(OUTPUTDIR)\edu\unc\sync\AtomicChange.class:	AtomicChange.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) AtomicChange.java



$(OUTPUTDIR)\edu\unc\sync\AtomicMergeMatrix.class:	AtomicMergeMatrix.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) AtomicMergeMatrix.java



$(OUTPUTDIR)\edu\unc\sync\BasicMergeActions.class:	BasicMergeActions.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) BasicMergeActions.java



$(OUTPUTDIR)\edu\unc\sync\Change.class:	Change.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) Change.java



$(OUTPUTDIR)\edu\unc\sync\ChangePair.class:	ChangePair.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) ChangePair.java



$(OUTPUTDIR)\edu\unc\sync\ChangeSet.class:	ChangeSet.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) ChangeSet.java



$(OUTPUTDIR)\edu\unc\sync\Delegated.class:	Delegated.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) Delegated.java



$(OUTPUTDIR)\edu\unc\sync\DelegatedReplicatedDictionary.class:	DelegatedReplicatedDictionary.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) DelegatedReplicatedDictionary.java



$(OUTPUTDIR)\DelegatedReplicatedObject.class:	DelegatedReplicatedObject.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) DelegatedReplicatedObject.java



$(OUTPUTDIR)\DelegatedReplicatedSequence.class:	DelegatedReplicatedSequence.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) DelegatedReplicatedSequence.java



$(OUTPUTDIR)\DelegatedUtils.class:	DelegatedUtils.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) DelegatedUtils.java



$(OUTPUTDIR)\DelegationInterface.class:	DelegationInterface.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) DelegationInterface.java



$(OUTPUTDIR)\DelegationObjectInterface.class:	DelegationObjectInterface.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) DelegationObjectInterface.java



$(OUTPUTDIR)\DelegationVectorInterface.class:	DelegationVectorInterface.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) DelegationVectorInterface.java



$(OUTPUTDIR)\DictionaryChangeSet.class:	DictionaryChangeSet.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) DictionaryChangeSet.java



$(OUTPUTDIR)\DictionaryKeyChange.class:	DictionaryKeyChange.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) DictionaryKeyChange.java



$(OUTPUTDIR)\DictionaryMergeMatrix.class:	DictionaryMergeMatrix.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) DictionaryMergeMatrix.java



$(OUTPUTDIR)\DictionaryModifyChange.class:	DictionaryModifyChange.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) DictionaryModifyChange.java



$(OUTPUTDIR)\DictionaryPutChange.class:	DictionaryPutChange.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) DictionaryPutChange.java



$(OUTPUTDIR)\DictionaryRemoveChange.class:	DictionaryRemoveChange.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) DictionaryRemoveChange.java



$(OUTPUTDIR)\ElementChange.class:	ElementChange.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) ElementChange.java



$(OUTPUTDIR)\ElementChangePair.class:	ElementChangePair.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) ElementChangePair.java



$(OUTPUTDIR)\FieldModifyChange.class:	FieldModifyChange.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) FieldModifyChange.java



$(OUTPUTDIR)\GenericChangeSet.class:	GenericChangeSet.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) GenericChangeSet.java



$(OUTPUTDIR)\GlobalObject.class:	GlobalObject.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) GlobalObject.java



$(OUTPUTDIR)\MergeAction.class:	MergeAction.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) MergeAction.java



$(OUTPUTDIR)\MergeMatrix.class:	MergeMatrix.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) MergeMatrix.java



$(OUTPUTDIR)\ModifyChange.class:	ModifyChange.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) ModifyChange.java



$(OUTPUTDIR)\NullChange.class:	NullChange.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) NullChange.java



$(OUTPUTDIR)\NullElementChange.class:	NullElementChange.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) NullElementChange.java



$(OUTPUTDIR)\NullSet.class:	NullSet.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) NullSet.java



$(OUTPUTDIR)\ObjectID.class:	ObjectID.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) ObjectID.java



$(OUTPUTDIR)\ObjectMergeMatrix.class:	ObjectMergeMatrix.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) ObjectMergeMatrix.java



$(OUTPUTDIR)\RecordChangeSet.class:	RecordChangeSet.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) RecordChangeSet.java



$(OUTPUTDIR)\RecordMergeMatrix.class:	RecordMergeMatrix.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) RecordMergeMatrix.java



$(OUTPUTDIR)\Replicated.class:	Replicated.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) Replicated.java



$(OUTPUTDIR)\ReplicatedAtomic.class:	ReplicatedAtomic.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) ReplicatedAtomic.java



$(OUTPUTDIR)\ReplicatedBeanInfo.class:	ReplicatedBeanInfo.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) ReplicatedBeanInfo.java



$(OUTPUTDIR)\ReplicatedCollection.class:	ReplicatedCollection.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) ReplicatedCollection.java



$(OUTPUTDIR)\ReplicatedDictionary.class:	ReplicatedDictionary.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) ReplicatedDictionary.java



$(OUTPUTDIR)\ReplicatedFloat.class:	ReplicatedFloat.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) ReplicatedFloat.java



$(OUTPUTDIR)\ReplicatedInteger.class:	ReplicatedInteger.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) ReplicatedInteger.java



$(OUTPUTDIR)\ReplicatedObject.class:	ReplicatedObject.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) ReplicatedObject.java



$(OUTPUTDIR)\ReplicatedRecord.class:	ReplicatedRecord.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) ReplicatedRecord.java



$(OUTPUTDIR)\ReplicatedReference.class:	ReplicatedReference.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) ReplicatedReference.java



$(OUTPUTDIR)\ReplicatedSequence.class:	ReplicatedSequence.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) ReplicatedSequence.java



$(OUTPUTDIR)\ReplicatedString.class:	ReplicatedString.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) ReplicatedString.java



$(OUTPUTDIR)\ReplicatedStringBeanInfo.class:	ReplicatedStringBeanInfo.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) ReplicatedStringBeanInfo.java



$(OUTPUTDIR)\ReplicationException.class:	ReplicationException.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) ReplicationException.java



$(OUTPUTDIR)\SequenceChangeSet.class:	SequenceChangeSet.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) SequenceChangeSet.java



$(OUTPUTDIR)\SequenceDeleteChange.class:	SequenceDeleteChange.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) SequenceDeleteChange.java



$(OUTPUTDIR)\SequenceInsertChange.class:	SequenceInsertChange.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) SequenceInsertChange.java



$(OUTPUTDIR)\SequenceMergeMatrix.class:	SequenceMergeMatrix.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) SequenceMergeMatrix.java



$(OUTPUTDIR)\SequenceModifyChange.class:	SequenceModifyChange.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) SequenceModifyChange.java



$(OUTPUTDIR)\SequenceMoveChange.class:	SequenceMoveChange.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) SequenceMoveChange.java



$(OUTPUTDIR)\Sync.class:	Sync.java
		$(JAVAC) $(CFLAGS) $(INCLUDES) -d $(OUTPUTDIR) Sync.java



