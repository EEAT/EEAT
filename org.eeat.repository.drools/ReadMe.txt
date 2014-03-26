The pom.xml specifies the dependent jars. Maven finds all the dependent jars and places them in target/dependencies.
All dependent jars must be available during runtime, so all (non-Eclipse) jars in target/dependencies are included in the Eclipse Runtime path.

WARNING!
Oddly, if xml-apis.jar is in classpath, then this component will fail silently.	(don't include xml-apis in the classpath)

org.eclipse.jdt.core-3.5.2.v_981_R35x.jar
  this jdt jar must be in the runtime classpath. If it is in the plugin dependencies, it does not work. 