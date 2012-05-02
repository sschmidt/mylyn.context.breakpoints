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

import java.io.InputStream;
import java.util.List;

import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.mylyn.context.core.IContextContributor;

/**
 * @author Sebastian Schmidt
 */
public class BreakpointsContextContributor implements IContextContributor {

	public InputStream getSerializedContextInformation() {
		List<IBreakpoint> contextBreakpoints = Activator.getBreakpointContextListener().getContextBreakpoints();
		return BreakpointsContextUtil.exportBreakpoints(contextBreakpoints);
	}

	public String getIdentifier() {
		return Activator.PLUGIN_ID;
	}

}