/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
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
import java.util.HashMap;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jdt.internal.debug.core.breakpoints.JavaLineBreakpoint;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.context.sdk.java.WorkspaceSetupHelper;

/**
 * @author Sebastian Schmidt
 */
@SuppressWarnings("restriction")
public abstract class AbstractBreakpointsTest {

	protected IBreakpoint createTestBreakpoint() throws DebugException {
		IResource testClass = ResourcesPlugin.getWorkspace().getRoot().findMember("/test/src/test.java"); //$NON-NLS-1$
		return new JavaLineBreakpoint(testClass, "test", 5, 1, 5, 0, true, new HashMap<String, String>()); //$NON-NLS-1$
	}

	protected IProject createProject() throws Exception {
		IProject project = WorkspaceSetupHelper.createProject("test"); //$NON-NLS-1$
		ZipFile zip = new ZipFile(new File("testdata/projects/project.zip")); //$NON-NLS-1$
		CommonTestUtil.unzip(zip, project.getLocation().toFile());
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
		return project;
	}
}
