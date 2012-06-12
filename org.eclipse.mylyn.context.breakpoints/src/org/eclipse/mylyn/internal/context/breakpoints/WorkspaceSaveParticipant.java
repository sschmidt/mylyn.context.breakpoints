/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.breakpoints;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.model.IBreakpoint;

public class WorkspaceSaveParticipant implements ISaveParticipant {

	private final BreakpointsContextContributor breakpointsContextContributor;

	public WorkspaceSaveParticipant(BreakpointsContextContributor breakpointsContextContributor) {
		this.breakpointsContextContributor = breakpointsContextContributor;
	}

	public void saving(ISaveContext context) throws CoreException {
		IPath stateLocation = getStateLocation();
		File stateFile = resetStateFile(stateLocation);

		Set<IBreakpoint> contextBreakpoints = breakpointsContextContributor.getBreakpoints();
		InputStream breakpoints = BreakpointsContextUtil.exportBreakpoints(contextBreakpoints);

		if (breakpoints != null) {
			saveBreakpoints(stateFile, breakpoints);
		}

		context.map(new Path(Activator.WORKSPACE_STATE_FILE), stateLocation);
		context.needSaveNumber();
	}

	public IPath getStateLocation() {
		return Platform.getStateLocation(Activator.getDefault().getBundle());
	}

	private void saveBreakpoints(File stateFile, InputStream breakpoints) throws CoreException {
		try {
			stateFile.createNewFile();
			FileOutputStream fileOutputStream = new FileOutputStream(stateFile);
			IOUtils.copy(breakpoints, fileOutputStream);
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "error saving plugin state", e)); //$NON-NLS-1$
		}
	}

	private File resetStateFile(IPath stateLocation) {
		File stateFile = stateLocation.append(Activator.WORKSPACE_STATE_FILE).toFile();
		stateFile.getParentFile().mkdirs();
		if (stateFile.exists()) {
			stateFile.delete();
		}
		return stateFile;
	}

	public void doneSaving(ISaveContext context) {
		// ignore

	}

	public void prepareToSave(ISaveContext context) throws CoreException {
		// ignore

	}

	public void rollback(ISaveContext context) {
		// ignore

	}
}
