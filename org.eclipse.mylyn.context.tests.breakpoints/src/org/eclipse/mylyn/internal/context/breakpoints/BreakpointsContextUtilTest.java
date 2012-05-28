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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jdt.internal.debug.core.breakpoints.JavaLineBreakpoint;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextManager;
import org.eclipse.mylyn.context.sdk.java.WorkspaceSetupHelper;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Sebastian Schmidt
 */
@SuppressWarnings("restriction")
public class BreakpointsContextUtilTest extends AbstractBreakpointsTest {

	private final String contextFileName = "contextWithBreakpoints.xml.zip"; //$NON-NLS-1$

	private final File contextFile = new File("testdata/" + contextFileName); //$NON-NLS-1$

	private File tempContextFile = null;

	private final IInteractionContextManager contextManager = ContextCore.getContextManager();

	@Before
	public void tearUp() throws IOException, CoreException {
		File contextStore = ContextCorePlugin.getDefault().getDefaultContextLocation().toFile();
		tempContextFile = new File(contextStore, contextFileName);
		FileUtils.copyFile(contextFile, tempContextFile);
	}

	@After
	public void tearDown() throws Exception {
		if (tempContextFile != null && tempContextFile.exists()) {
			tempContextFile.delete();
		}
		contextManager.deactivateContext("contextWithBreakpoints"); //$NON-NLS-1$
		WorkspaceSetupHelper.clearWorkspace();
	}

	/**
	 * If the project isn't in the workspace, breakpoints from context should be ignored
	 */
	@Test
	public void testImportBreakpointsWithMissingProject() {
		contextManager.activateContext("contextWithBreakpoints"); //$NON-NLS-1$
		IInteractionContext testContext = contextManager.getActiveContext();
		List<IBreakpoint> breakpoints = BreakpointsContextUtil.importBreakpoints(testContext);
		assertTrue(breakpoints.size() == 0);
	}

	@Test
	public void testImportBreakpoints() throws Exception {
		createProject();
		contextManager.activateContext("contextWithBreakpoints"); //$NON-NLS-1$
		IInteractionContext testContext = contextManager.getActiveContext();
		List<IBreakpoint> breakpoints = BreakpointsContextUtil.importBreakpoints(testContext);
		assertTrue(breakpoints.size() == 2);
		assertTrue(breakpoints.get(0) instanceof JavaLineBreakpoint);
		assertTrue(breakpoints.get(1) instanceof JavaLineBreakpoint);
	}

	@Test
	public void testExportBreakpoints() throws Exception {
		createProject();
		InputStream expectedResult = new FileInputStream(new File("testdata/breakpointFile.xml")); //$NON-NLS-1$
		IBreakpoint breakpoint = createTestBreakpoint();
		List<IBreakpoint> breakpoints = new ArrayList<IBreakpoint>();
		breakpoints.add(breakpoint);
		InputStream exportedBreakpoints = BreakpointsContextUtil.exportBreakpoints(breakpoints);
		assertTrue(IOUtils.contentEquals(expectedResult, exportedBreakpoints));
	}

	@Test
	public void testRemoveBreakpoints() throws Exception {
		createProject();
		IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
		IBreakpoint[] breakpoints = breakpointManager.getBreakpoints();
		int currentBreakpoints = breakpoints.length;

		IBreakpoint breakpoint = createTestBreakpoint();
		breakpointManager.addBreakpoint(breakpoint);
		List<IBreakpoint> breakpointsToRemove = new ArrayList<IBreakpoint>();
		breakpointsToRemove.add(breakpoint);

		breakpointManager.addBreakpoint(breakpoint);
		assertEquals(currentBreakpoints + 1, breakpointManager.getBreakpoints().length);

		BreakpointsContextUtil.removeBreakpoints(breakpointsToRemove);
		assertEquals(currentBreakpoints, breakpointManager.getBreakpoints().length);
	}
}
