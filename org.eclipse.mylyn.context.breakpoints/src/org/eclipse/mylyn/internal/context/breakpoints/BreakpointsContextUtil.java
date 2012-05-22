/*******************************************************************************
 * Copyright (c) 2012 Sebastian Schmidt and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Schmidt - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.context.breakpoints;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.actions.ExportBreakpointsOperation;
import org.eclipse.debug.ui.actions.ImportBreakpointsOperation;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;

/**
 * @author Sebastian Schmidt
 */
public class BreakpointsContextUtil {

	public static InputStream exportBreakpoints(List<IBreakpoint> breakpoints) {
		ExportBreakpointsOperation exportBreakpointOperation = new ExportBreakpointsOperation(
				breakpoints.toArray(new IBreakpoint[0]));

		try {
			exportBreakpointOperation.run(null);
			return new ByteArrayInputStream(exportBreakpointOperation.getBuffer().toString().getBytes("UTF-8")); //$NON-NLS-1$
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID,
					"Could not export context breakpoints", e));//$NON-NLS-1$
		}

		return null;
	}

	public static List<IBreakpoint> importBreakpoints(IInteractionContext context) {
		InputStream stream = ContextCore.getContextManager().getAdditionalContextInformation(context,
				Activator.PLUGIN_ID);

		if (stream == null) {
			return new ArrayList<IBreakpoint>();
		}

		String breakpoints = new Scanner(stream).useDelimiter("\\A").next(); //$NON-NLS-1$
		ImportBreakpointsOperation importBreakpointOperation = new ImportBreakpointsOperation(new StringBuffer(
				breakpoints), true, false);
		try {
			importBreakpointOperation.run(null);
			return new ArrayList<IBreakpoint>(Arrays.asList(importBreakpointOperation.getImportedBreakpoints()));
		} catch (InvocationTargetException e) {
			StatusHandler.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID,
					"Could not import context breakpoints", e));//$NON-NLS-1$

		}
		return new ArrayList<IBreakpoint>();
	}

	public static void removeBreakpoints(List<IBreakpoint> breakpoints) {
		try {
			DebugPlugin.getDefault()
					.getBreakpointManager()
					.removeBreakpoints(breakpoints.toArray(new IBreakpoint[0]), true);
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID,
					"Could not remove obsolete breakpoints from workspace", e)); //$NON-NLS-1$
		}
	}
}
