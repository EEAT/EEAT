package org.eeat.core.plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.osgi.util.ManifestElement;
import org.eclipse.pde.ui.launcher.IPDELauncherConstants;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

public class Launch {
	static final String OSGI_VM_ARGS = "-Declipse.ignoreApp=false -Dosgi.noShutdown=true -Declipse.application=";
	static final String JAVA_VM_ARGS = "-Dosgi.requiredJavaVersion=1.5 -Xms40m -Xmx256m";
	static String PROGRAM_ARGS = "-os ${target.os} -ws ${target.ws} -arch ${target.arch} -nl ${target.nl} ";
	static final String OSGI_LAUNCH_TYPE = "org.eclipse.pde.ui.EquinoxLauncher";
	static final String JAVA_LAUNCH_TYPE = "org.eclipse.pde.ui.RuntimeWorkbench";
	static String appName;
	static String launchName;
	static String arguments;
	static String vmArguments;
	static String bundleName;
	static String launchType;
	static String vmArgs;

	static protected void addBundleAndDependencies(Bundle bundle, Set<String> dependencies,
			boolean includeOptional) throws BundleException {
		if ((bundle != null) && dependencies.add(bundle.getSymbolicName())) {
			ManifestElement[] elements;
			String header = (String) bundle.getHeaders().get(Constants.REQUIRE_BUNDLE);
			if (header != null) {
				elements = ManifestElement.parseHeader(Constants.REQUIRE_BUNDLE, header);
				for (ManifestElement element : elements) {
					boolean optionalEntry = element.toString().indexOf("resolution:=\"optional\"") > 0;
					// String name = (String) elements[i].getValue();
					// // These are always null, even when optional is set
					// String o =
					// elements[i].getAttribute(Constants.RESOLUTION_OPTIONAL);
					// String d =
					// elements[i].getAttribute(Constants.RESOLUTION_DIRECTIVE);
					// String m =
					// elements[i].getAttribute(Constants.RESOLUTION_MANDATORY);
					// String vv = elements[i].toString();
					if (includeOptional || !optionalEntry) {
						addBundleAndDependencies(Platform.getBundle(element.getValue()),
								dependencies, includeOptional);
					}
				}
			}
			header = (String) bundle.getHeaders().get(Constants.IMPORT_PACKAGE);
			if (header != null) {
				elements = ManifestElement.parseHeader(Constants.IMPORT_PACKAGE, header);
				for (ManifestElement element : elements) {
					addBundleAndDependencies(Platform.getBundle(element.getValue()), dependencies,
							includeOptional);
				}
			}
			header = (String) bundle.getHeaders().get(Constants.DYNAMICIMPORT_PACKAGE);
			if (header != null) {
				elements = ManifestElement.parseHeader(Constants.DYNAMICIMPORT_PACKAGE, header);
				for (ManifestElement element : elements) {
					addBundleAndDependencies(Platform.getBundle(element.getValue()), dependencies,
							includeOptional);
				}
			}
			header = (String) bundle.getHeaders().get(Constants.FRAGMENT_HOST);
			if (header != null) {
				elements = ManifestElement.parseHeader(Constants.FRAGMENT_HOST, header);
				for (ManifestElement element : elements) {
					addBundleAndDependencies(Platform.getBundle(element.getValue()), dependencies,
							includeOptional);
				}
			}
		}
	}

	static public ILaunchConfiguration createLaunch(BundleContext bundleContext, String bundleName,
			String appName, String launchName, String arguments, String vmArguments)
			throws CoreException {
		Launch.bundleName = bundleName;
		Launch.appName = appName;
		Launch.launchName = launchName;
		Launch.arguments = arguments;
		Launch.vmArguments = vmArguments;
		ILaunchConfigurationWorkingCopy wc;
		ILaunchConfiguration config = null;
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType(launchType);
		if (manager.isExistingLaunchConfigurationName(launchName)) {
			ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
			for (ILaunchConfiguration c : configurations) {
				if (c.getName().equals(launchName)) {
					config = c;
				}
			}
			wc = config.copy(launchName + "-tmp");
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, PROGRAM_ARGS
					+ arguments);
			wc.rename(launchName);
		} else {
			wc = type.newInstance(null, launchName);
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, PROGRAM_ARGS
					+ arguments);
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArgs);
			wc.setAttribute(IPDELauncherConstants.INCLUDE_OPTIONAL, false);
			wc.setAttribute(IPDELauncherConstants.AUTOMATIC_ADD, false);
			// wc.setAttribute(IPDELauncherConstants.AUTOMATIC_VALIDATE, true);
			wc.setAttribute(IPDELauncherConstants.APPLICATION, appName);
			wc.setAttribute(IPDELauncherConstants.CONFIG_GENERATE_DEFAULT, true);
			wc.setAttribute(IPDELauncherConstants.CONFIG_USE_DEFAULT_AREA, true);
			wc.setAttribute(IPDELauncherConstants.USE_DEFAULT, true);
			wc.setAttribute(IPDELauncherConstants.LOCATION,
					"${workspace_loc}/../runtime-New_configuration");
			String depends = getOSGiBundleString(bundleContext, bundleName);
			wc.setAttribute(IPDELauncherConstants.TARGET_BUNDLES, depends);
		}
		config = wc.doSave();
		return config;
	}

	static public List<String> getBundleClasspath(Bundle bundle) {
		List<String> classpath = new ArrayList<String>();
		IPath path = null;
		path = new Path(bundle.getEntry("/").getPath());
		String requires = (String) bundle.getHeaders().get(Constants.BUNDLE_CLASSPATH);
		ManifestElement[] elements = null;
		try {
			elements = ManifestElement.parseHeader(Constants.BUNDLE_CLASSPATH, requires);
		} catch (BundleException e1) {
			System.err.println("Cannot parse manifest: " + e1.toString());
			e1.printStackTrace();
			return null;
		}
		if (elements != null) {
			for (ManifestElement e : elements) {
				if (e.getValue().length() > 0) {
					IRuntimeClasspathEntry myPathEntry = JavaRuntime
							.newArchiveRuntimeClasspathEntry(new Path(path.toString()
									+ e.getValue()));
					myPathEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
					try {
						classpath.add(myPathEntry.getMemento());
					} catch (CoreException e1) {
						System.err.println("Cannot add to classpath " + e1.toString());
						e1.printStackTrace();
					}
				}
			}
		}
		return classpath;
	}

	static public List<String> getBundleDependencies(Bundle bundle) {
		List<String> dependencies = new ArrayList<String>();
		String requires = (String) bundle.getHeaders().get(Constants.BUNDLE_CLASSPATH);
		ManifestElement[] elements = null;
		try {
			elements = ManifestElement.parseHeader(Constants.BUNDLE_CLASSPATH, requires);
		} catch (BundleException e1) {
			System.err.println("Cannot parse manifest: " + e1.toString());
			e1.printStackTrace();
			return null;
		}
		if (elements != null) {
			for (ManifestElement e : elements) {
				if (e.getValue().length() > 0) {
					dependencies.add(e.getValue());
				}
			}
		}
		return dependencies;
	}

	static protected List<String> getBundleDependenciesClosure(Bundle bundle)
			throws BundleException {
		Set<String> dependencies = new HashSet<String>();
		addBundleAndDependencies(bundle, dependencies, false);
		return new ArrayList<String>(dependencies);
	}

	static protected String getOSGiBundleString(BundleContext bundleContext, String bundleName) {
		String bundles = "";
		Bundle bundle = Platform.getBundle(bundleName);
		try {
			for (String bn : getBundleDependenciesClosure(bundle)) {
				bundles += bn + "@default:default,";
			}
		} catch (BundleException e) {
			e.printStackTrace();
		}
		return bundles;
	}

	static public void launchJava(BundleContext bundleContext, String bundleName, String appName,
			String launchName, String arguments, String vmArguments) throws CoreException {
		Launch.launchType = JAVA_LAUNCH_TYPE;
		Launch.vmArgs = JAVA_VM_ARGS;
		DebugUITools.launch(createLaunch(bundleContext, bundleName, appName, launchName, arguments,
				vmArguments), ILaunchManager.RUN_MODE);
	}

	static public void launchOsgi(BundleContext bundleContext, String bundleName, String appName,
			String launchName, String arguments, String vmArguments) throws CoreException {
		Launch.launchType = OSGI_LAUNCH_TYPE;
		Launch.vmArgs = OSGI_VM_ARGS + appName;
		DebugUITools.launch(createLaunch(bundleContext, bundleName, appName, launchName, arguments,
				vmArguments), ILaunchManager.RUN_MODE);
	}

}
