/*
 * Copyright 2017 EPAM Systems
 *
 *
 * This file is part of EPAM Report Portal.
 * https://github.com/reportportal/service-api
 *
 * Report Portal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Report Portal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Report Portal.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.epam.ta.reportportal.core.bts.handler;

import com.epam.ta.reportportal.auth.ReportPortalUser;
import com.epam.ta.reportportal.core.bts.handler.impl.DeleteExternalSystemHandler;
import com.epam.ta.reportportal.ws.model.OperationCompletionRS;

/**
 * Interface for {@link DeleteExternalSystemHandler} external system handler
 *
 * @author Andrei_Ramanchuk
 * @author Pavel Bortnik
 */
public interface IDeleteExternalSystemHandler {

	/**
	 * Delete method for external system entity
	 *
	 * @param projectName      Project Name
	 * @param externalSystemId External System to be deleted
	 * @param user             User
	 * @return Operation result
	 */
	OperationCompletionRS deleteExternalSystem(String projectName, Long externalSystemId, ReportPortalUser user);

	/**
	 * Delete all external system assigned to specified Report Portal project
	 *
	 * @param projectName Project Name
	 * @param user        User
	 * @return Operation Result
	 */
	OperationCompletionRS deleteAllExternalSystems(String projectName, ReportPortalUser user);

}