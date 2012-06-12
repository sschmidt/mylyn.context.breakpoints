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

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.IBreakpointsListener;
import org.eclipse.debug.core.model.IBreakpoint;

public class BreakpointsListener implements IBreakpointsListener {

	private final BreakpointsContextContributor breakpointContextContributor;

	public BreakpointsListener(BreakpointsContextContributor breakpointContextContributor) {
		this.breakpointContextContributor = breakpointContextContributor;

	}

	public void breakpointsAdded(IBreakpoint[] breakpoints) {
		for (IBreakpoint breakpoint : breakpoints) {
			breakpointContextContributor.addBreakpoint(breakpoint);
		}
	}

	public void breakpointsRemoved(IBreakpoint[] breakpoints, IMarkerDelta[] deltas) {
		for (IBreakpoint breakpoint : breakpoints) {
			breakpointContextContributor.removeBreakpoint(breakpoint);
		}
	}

	public void breakpointsChanged(IBreakpoint[] breakpoints, IMarkerDelta[] deltas) {
		// ignore. Pointer will remain the same
	}
}
