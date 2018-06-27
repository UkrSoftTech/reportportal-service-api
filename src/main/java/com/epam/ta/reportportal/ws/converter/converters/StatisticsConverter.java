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

package com.epam.ta.reportportal.ws.converter.converters;

import com.epam.ta.reportportal.store.database.entity.item.ExecutionStatistics;
import com.epam.ta.reportportal.store.database.entity.item.IssueStatistics;
import com.epam.ta.reportportal.ws.model.statistics.Statistics;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Pavel Bortnik
 */
public final class StatisticsConverter {

	private StatisticsConverter() {
		//static only
	}

	public static final BiFunction<Set<IssueStatistics>, Set<ExecutionStatistics>, Statistics> TO_RESOURCE = (issue, execution) -> {
		Statistics statistics = new Statistics();
		statistics.setDefects(issue.stream()
				.collect(Collectors.toMap(it -> it.getIssueType().getIssueGroup().getTestItemIssueGroup().getValue(),
						StatisticsConverter.TO_ISSUE
				)));
		statistics.setExecutions(
				execution.stream().collect(Collectors.toMap(ExecutionStatistics::getStatus, ExecutionStatistics::getCounter)));
		return statistics;
	};

	public static final Function<IssueStatistics, Map<String, Integer>> TO_ISSUE = i -> {
		Map<String, Integer> res = new HashMap<>(1);
		res.put(i.getIssueType().getLocator(), i.getCounter());
		return res;
	};
}
