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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.mylyn.context.sdk.java.WorkspaceSetupHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Sebastian Schmidt
 */
public class WorkspaceSaveParticipantTest extends AbstractBreakpointsTest {

	private final BreakpointsContextManager breakpointsContextManager = new BreakpointsContextManager();

	private WorkspaceSaveParticipant participant;

	private ISaveContext saveContext;

	@Before
	public void tearUp() {
		participant = new WorkspaceSaveParticipant(breakpointsContextManager);
		saveContext = mock(ISaveContext.class);
	}

	@After
	@SuppressWarnings("restriction")
	public void tearDown() throws CoreException, IOException {
		File stateFile = getStateFile();
		if (stateFile.exists()) {
			stateFile.delete();
		}

		WorkspaceSetupHelper.clearWorkspace();
	}

	@Test
	public void testSaveEmptyState() throws CoreException {
		breakpointsContextManager.setContextBreakpoints(new ArrayList<IBreakpoint>());

		participant.saving(saveContext);

		assertFalse(getStateFile().exists());
		verify(saveContext).needSaveNumber();
	}

	@Test
	public void testSaveStateWithBreakpoints() throws Exception {
		createProject();
		File breakpointFile = new File("testdata/breakpointFile.xml"); //$NON-NLS-1$
		IBreakpoint breakpoint = createTestBreakpoint();
		List<IBreakpoint> breakpoints = new ArrayList<IBreakpoint>();
		breakpoints.add(breakpoint);
		breakpointsContextManager.setContextBreakpoints(breakpoints);

		participant.saving(saveContext);

		assertTrue(getStateFile().exists());
		IOUtils.contentEquals(new FileInputStream(getStateFile()), new FileInputStream(breakpointFile));
		verify(saveContext).needSaveNumber();
	}

	private File getStateFile() {
		return participant.getStateLocation().append(Activator.WORKSPACE_STATE_FILE).toFile();
	}
}