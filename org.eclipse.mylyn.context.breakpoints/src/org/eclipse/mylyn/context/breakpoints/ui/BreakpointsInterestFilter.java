/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.breakpoints.ui;

import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.ide.ui.AbstractMarkerInterestFilter;
import org.eclipse.mylyn.internal.context.breakpoints.Activator;

/**
 * @author Mik Kersten
 * @author Sebastian Schmidt
 */
public class BreakpointsInterestFilter extends AbstractMarkerInterestFilter {

	@Override
	public boolean select(Viewer viewer, Object parent, Object element) {
		Set<IBreakpoint> contextBreakpoints = Activator.getDefault().getBreakpointContextContributor().getBreakpoints();
		if (contextBreakpoints.contains(element)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isImplicitlyInteresting(IMarker marker) {
		return false;
	}
}