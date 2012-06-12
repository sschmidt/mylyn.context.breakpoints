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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.mylyn.context.core.IContextContributor;
import org.eclipse.mylyn.context.core.IContributedInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.SimpleContributedElement;

/**
 * @author Sebastian Schmidt
 */
public class BreakpointsContextContributor implements IContextContributor {

	private static final String BREAKPOINT_CATEGORY = "Breakpoints"; //$NON-NLS-1$

	private static final String CONTENT_TYPE = "Breakpoint"; //$NON-NLS-1$

	private Map<IBreakpoint, IContributedInteractionElement> contextBreakpoints = null;

	private IContributedInteractionElement breakpointElementsRoot = null;

	private BreakpointsListener breakpointListener = null;

	public void activateContext(IInteractionContext context) {
		this.breakpointElementsRoot = new SimpleContributedElement(this, this, BREAKPOINT_CATEGORY, CONTENT_TYPE);
		setBreakpoints(BreakpointsContextUtil.importBreakpoints(context));
		initBreakpointListener();
	}

	public void initBreakpointListener() {
		if (breakpointListener == null) {
			breakpointListener = new BreakpointsListener(this);
			DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(breakpointListener);
		}
	}

	public void deactivateContext() {
		DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(breakpointListener);
		BreakpointsContextUtil.removeBreakpoints(contextBreakpoints.keySet());
		this.breakpointElementsRoot = null;
		this.contextBreakpoints = null;
		this.breakpointListener = null;
	}

	public InputStream getSerializedContextInformation() {
		return BreakpointsContextUtil.exportBreakpoints(contextBreakpoints.keySet());
	}

	public String getIdentifier() {
		return Activator.PLUGIN_ID;
	}

	public boolean isInteresting(IInteractionElement contributorElement, double threshold) {
		return threshold <= 20; // FIXME: some fancy stuff
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IWorkspaceRoot) {
			return new Object[] { breakpointElementsRoot };
		}
		return null;
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement == breakpointElementsRoot) {
			return contextBreakpoints.keySet().toArray();
		}
		return null;
	}

	public Collection<IContributedInteractionElement> getAllElements() {
		return contextBreakpoints.values();
	}

	public void setBreakpoints(List<IBreakpoint> breakpoints) {
		contextBreakpoints = new HashMap<IBreakpoint, IContributedInteractionElement>();
		for (IBreakpoint breakpoint : breakpoints) {
			contextBreakpoints.put(breakpoint, createBreakpointElement(breakpoint));
		}
	}

	private IContributedInteractionElement createBreakpointElement(IBreakpoint breakpoint) {
		return new SimpleContributedElement(this, breakpoint, breakpoint.getModelIdentifier(), CONTENT_TYPE);
	}

	public void removeElements(List<IInteractionElement> removedElements) {
		for (IInteractionElement element : removedElements) {
			if (element instanceof IContributedInteractionElement) {
				IContributedInteractionElement contributedElement = (IContributedInteractionElement) element;
				if (contributedElement.getReference() instanceof IBreakpoint) {
					contextBreakpoints.remove(contributedElement.getReference());
				}
			}
		}
	}

	public IContributedInteractionElement getInteractionElement(Object visibleObject) {
		return contextBreakpoints.get(visibleObject);
	}

	public boolean isLandmark(IContributedInteractionElement interactionElement) {
		return interactionElement != breakpointElementsRoot; // FIXME: some fancy stuff
	}

	public void addBreakpoint(IBreakpoint breakpoint) {
		contextBreakpoints.put(breakpoint, createBreakpointElement(breakpoint));
	}

	public void removeBreakpoint(IBreakpoint breakpoint) {
		contextBreakpoints.remove(breakpoint);
	}

	public Set<IBreakpoint> getBreakpoints() {
		return contextBreakpoints.keySet();
	}

}