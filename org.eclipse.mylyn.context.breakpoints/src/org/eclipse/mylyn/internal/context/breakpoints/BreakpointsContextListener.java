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

import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextChangeEvent.ContextChangeKind;

public class BreakpointsContextListener extends AbstractContextListener {

	private final BreakpointsContextContributor breakpointContextContributor;

	public BreakpointsContextListener(BreakpointsContextContributor breakpointContextContributor) {
		this.breakpointContextContributor = breakpointContextContributor;
	}

	/**
	 * Maybe put this in ContextContributor API
	 */
	@Override
	public void contextChanged(ContextChangeEvent event) {
		if (event.getEventKind().equals(ContextChangeKind.ACTIVATED)) {
			breakpointContextContributor.activateContext(event.getContext());
		} else if (event.getEventKind().equals(ContextChangeKind.DEACTIVATED)) {
			breakpointContextContributor.deactivateContext();
		}
	}
}
