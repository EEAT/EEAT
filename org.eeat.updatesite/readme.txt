Set updateFolder to the place where you want the Eclipse p2 folder copied.
(It is assumed that you set updateFolder on the server build, which also has a web folder.)
For example, -DupdateFolder="c:\tomcat\web\respository" 

WARNING:
An update project, like this, cannot run alone.
It must run as part of the parent project.
So, in org.eeat.parent run maven's package (or deploy) to create the update site.