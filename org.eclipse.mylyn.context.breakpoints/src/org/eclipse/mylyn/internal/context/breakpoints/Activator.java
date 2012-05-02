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

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.mylyn.context.breakpoints"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private static BreakpointsContextListener breakpointContextListener = null;

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

		breakpointContextListener = new BreakpointsContextListener();
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(breakpointContextListener);
		ContextCore.getContextManager().addListener(breakpointContextListener);
		plugin = this;
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
		DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(breakpointContextListener);
		ContextCore.getContextManager().removeListener(breakpointContextListener);
		breakpointContextListener = null;
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

	public static BreakpointsContextListener getBreakpointContextListener() {
		return breakpointContextListener;
	}

}
