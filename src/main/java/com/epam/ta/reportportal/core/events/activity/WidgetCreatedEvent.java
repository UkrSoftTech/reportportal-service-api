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

package com.epam.ta.reportportal.core.events.activity;

import com.epam.ta.reportportal.core.events.ActivityEvent;
import com.epam.ta.reportportal.core.events.activity.details.SimpleWidgetActivityDetails;
import com.epam.ta.reportportal.entity.Activity;
import com.epam.ta.reportportal.entity.widget.Widget;

import java.time.LocalDateTime;

/**
 * @author pavel_bortnik
 */
public class WidgetCreatedEvent implements ActivityEvent {

	private final Widget widget;
	private final Long createdBy;

	public WidgetCreatedEvent(Widget widget, Long createdBy, Long projectRef, Long widgetId) {
		this.widget = widget;
		this.createdBy = createdBy;
	}

	@Override
	public Activity toActivity() {
		Activity activity = new Activity();
		activity.setCreatedAt(LocalDateTime.now());
		activity.setAction(ActivityAction.CREATE_WIDGET.getValue());
		activity.setEntity(Activity.Entity.WIDGET);
		activity.setUserId(createdBy);
		activity.setProjectId(widget.getProject().getId());
		activity.setDetails(new SimpleWidgetActivityDetails(widget.getId()));
		return activity;
	}

}
