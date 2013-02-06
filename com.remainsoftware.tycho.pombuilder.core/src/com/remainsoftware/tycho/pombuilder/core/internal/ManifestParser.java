package com.remainsoftware.tycho.pombuilder.core.internal;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.ManifestElement;

import com.remainsoftware.tycho.pombuilder.core.Constants;
import com.remainsoftware.tycho.pombuilder.core.IParser;
import com.remainsoftware.tycho.pombuilder.core.IPom;
import com.remainsoftware.tycho.pombuilder.core.PomBuilderException;

public class ManifestParser implements IParser {

	private static final String PARENT_PROJECT = "Parent-Project";
	private static final String ECLIPSE_PLUGIN = "eclipse-plugin";
	private static final String BUNDLE_VERSION = "Bundle-Version";
	private static final String BUNDLE_SYMBOLIC_NAME = "Bundle-SymbolicName";

	@Override
	public void parse(IResource resource) throws CoreException {

		// remove previous markers
		IMarker[] markers = resource.findMarkers(Constants.POM_PROBLEM_MARKER,
				false, IResource.DEPTH_ZERO);
		for (IMarker marker : markers) {
			marker.delete();
		}

		FileInputStream fisManifest = null;
		boolean fisManifestOpen = false;

		try {
			fisManifest = new FileInputStream(resource.getLocation().toFile());
			HashMap<String, String> map = new HashMap<String, String>();
			ManifestElement.parseBundleManifest(fisManifest, map);

			ManifestElement[] header = ManifestElement.parseHeader(
					BUNDLE_SYMBOLIC_NAME, map.get(BUNDLE_SYMBOLIC_NAME));
			String symbolicName = header[0].getValueComponents()[0];

			fisManifestOpen = true;

			IPom pom = new Pom(resource);
			pom.setModelVersion("4.0.0");
			pom.setVersion(map.get(BUNDLE_VERSION));
			pom.setArtifactId(symbolicName);
			pom.setPackaging(ECLIPSE_PLUGIN);

			if (map.get(PARENT_PROJECT) == null
					|| map.get(PARENT_PROJECT).trim().length() == 0) {
				IMarker marker = resource
						.createMarker(Constants.POM_PROBLEM_MARKER);
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				marker.setAttribute(IMarker.MESSAGE, "Variable '"
						+ Constants.PARENT_PROJECT + "' has not been defined.");
				marker.setAttribute(IMarker.LINE_NUMBER, 1);
			} else {

				try {
					pom.setParentProject(map.get(PARENT_PROJECT));
				} catch (PomBuilderException e) {
					IMarker marker = resource
							.createMarker(Constants.POM_PROBLEM_MARKER);
					marker.setAttribute(IMarker.SEVERITY,
							IMarker.SEVERITY_WARNING);
					marker.setAttribute(IMarker.MESSAGE, e.getMessage());
					marker.setAttribute(
							IMarker.LINE_NUMBER,
							calculateLine(resource.getLocation().toFile(),
									PARENT_PROJECT));
				}
			}

			pom.write();
			pom.getResource().refreshLocal(IResource.DEPTH_ZERO, null);

		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Constants.BUNDLE_ID, e.getMessage(), e));
		}

		finally {
			if (fisManifestOpen) {
				FileUtil.closeInputStream(fisManifest);
			}
		}
	}

	private Object calculateLine(File file, String string) {
		int line = FileUtil.calculateLine(file, string);
		return line == -1 ? 1 : line;
	}
}
