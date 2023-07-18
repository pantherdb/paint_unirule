# H1 PAINT Unirule


The UniRule annotation system allows curators to add annotations to protein sequences. The PAINT client tool enables curators to view both GO annotations and UniRule annotations in a phylogenetic context. It allows curators to create groupings (cases) of annotations if a set of sequences have been annotated to the same list of annotations. 


**Build Instructions**
1.  Ensure command 'java -version' returns version 1.11
2.  If there has been any changes in UniRule/PANTHER interface, build UniRule dependent jar files and copy into directory gopaint/lib/thirdParty/Unirule
3.  Ensure gopaint/build.xml refers to library files from UniRule
4.  cd into directory  gopaintServer and execute command 'ant cleanall', if there are any previous builds. Now execute command 'ant'  
5.  Execute command 'ant pub' .  This will buid the executables for the server and client and copy into directory for deployment



** Tracking node changes due to UPL change**
GenerateUPLChangeInfo utility can be used to determine which nodes have moved to a new family, been removed.  All families that have been annotated should be reviewed whenever a new UPL is released since new sequences may have been added to the family and or the topology of hte family may have changed     

