pombuilder
==========

BETA BETA BETA

Tycho POM Builder for Eclipse 

P2 Repository: http://dblyuiam.home.xs4all.nl/pombuilder/

Requires apache.commons.io which can be installed from orbit

After installations, right click on a plugin or fragment and select Configure/Enable Pom Builder

The Manifest then contains an error, add the field 

<b>Parent-Project: name.of.parent.project</b>


If you change the version of the parent pom, do a complete rebuild to pick up the changes.
