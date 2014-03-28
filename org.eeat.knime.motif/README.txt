This folder contains the Eclipse/KNIME SDK node extension project for the MaxMotif node/plugin.

org.eeat.knime.motif
--------------------

The project contains the source code for the MaxMotif library, so additional jars are not required.  The MaxMotif source was modified only slightly to output unicode/UTF16 characters.

org.eeat.knime.motif is the KNIME node extension.  It can be imported as a KNIME node extension or plugin project if the KNIME SDK has been installed within Eclipse.

Most of the real code resides in org.eeat.motif.MaxMotifNodeModel.java

org.eeat.knime.motif_1.0.0.jar
------------------------------

This is the jar exported from the MaxMotif node extension project (above).  To use the MaxMotif node in a KNIME workflow, copy this jar to the "<KNIME>/dropins" directory, then restart KNIME.  The MaxMotif node should show up in the node repository window.




