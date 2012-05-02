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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.IBreakpointsListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextChangeEvent.ContextChangeKind;
import org.eclipse.mylyn.context.core.ContextCore;

/**
 * @author Sebastian Schmidt
 */
public class BreakpointsContextListener extends AbstractContextListener implements IBreakpointsListener {

	private List<IBreakpoint> contextBreakpoints = new ArrayList<IBreakpoint>();

	public void breakpointsAdded(IBreakpoint[] breakpoints) {
		if (ContextCore.getContextManager().isContextActive()) {
			for (IBreakpoint breakpoint : breakpoints) {
				contextBreakpoints.add(breakpoint);
			}
		}
	}

	public void breakpointsRemoved(IBreakpoint[] breakpoints, IMarkerDelta[] deltas) {
		if (ContextCore.getContextManager().isContextActive()) {
			for (IBreakpoint breakpoint : breakpoints) {
				contextBreakpoints.remove(breakpoint);
			}
		}
	}

	public void breakpointsChanged(IBreakpoint[] breakpoints, IMarkerDelta[] deltas) {
		// ignored, pointer to breakpoint won't change
	}

	@Override
	public void contextChanged(ContextChangeEvent event) {
		if (event.getEventKind().equals(ContextChangeKind.ACTIVATED)) {
			contextBreakpoints = BreakpointsContextUtil.importBreakpoints(event.getContext());
		} else if (event.getEventKind().equals(ContextChangeKind.DEACTIVATED)) {
			BreakpointsContextUtil.removeBreakpoints(contextBreakpoints);
		}
	}

	public List<IBreakpoint> getContextBreakpoints() {
		return contextBreakpoints;
	}
}
