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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String WORKSPACE_STATE_FILE = "activeContextBreakpoints.xml"; //$NON-NLS-1$

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.mylyn.context.breakpoints"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private WorkspaceSaveParticipant workspaceSaveParticipant;

	private BreakpointsContextContributor breakpointContextContributor;

	private BreakpointsContextListener breakpointContextListener;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		breakpointContextContributor = new BreakpointsContextContributor();
		ContextCorePlugin.getDefault().addContextContributor(breakpointContextContributor);

		breakpointContextListener = new BreakpointsContextListener(breakpointContextContributor);
		ContextCore.getContextManager().addListener(breakpointContextListener);

		workspaceSaveParticipant = new WorkspaceSaveParticipant(breakpointContextContributor);

		ISavedState previousPluginState = ResourcesPlugin.getWorkspace().addSaveParticipant(PLUGIN_ID,
				workspaceSaveParticipant);
		if (previousPluginState != null) {
			try {
				restorePreviousPluginState(previousPluginState);
			} catch (FileNotFoundException e) {
				// all good. We just didn't have an active context with breakpoints before.
			}
		}
	}

	private void restorePreviousPluginState(ISavedState previousPluginState) throws IOException {
		IPath path = previousPluginState.lookup(new Path(WORKSPACE_STATE_FILE));
		File file = path.append(WORKSPACE_STATE_FILE).toFile();
		FileInputStream inputStream = new FileInputStream(file);
		//List<IBreakpoint> importBreakpoints = BreakpointsContextUtil.importBreakpoints(inputStream);
		//breakpointContextContributor.setBreakpoints(importBreakpoints);
		breakpointContextContributor.initBreakpointListener();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		ContextCore.getContextManager().removeListener(breakpointContextListener);
		ContextCorePlugin.getDefault().removeContextContributor(breakpointContextContributor);

		breakpointContextListener = null;
		breakpointContextContributor = null;

		ResourcesPlugin.getWorkspace().removeSaveParticipant(PLUGIN_ID);
		workspaceSaveParticipant = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public BreakpointsContextContributor getBreakpointContextContributor() {
		return breakpointContextContributor;
	}

}
