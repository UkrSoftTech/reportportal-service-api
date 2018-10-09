/*
 * Copyright 2018 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.ta.reportportal.core.filter.impl;

import com.epam.ta.reportportal.auth.ReportPortalUser;
import com.epam.ta.reportportal.commons.querygen.Filter;
import com.epam.ta.reportportal.core.filter.IGetUserFilterHandler;
import com.epam.ta.reportportal.dao.UserFilterRepository;
import com.epam.ta.reportportal.entity.filter.UserFilter;
import com.epam.ta.reportportal.exception.ReportPortalException;
import com.epam.ta.reportportal.ws.converter.PagedResourcesAssembler;
import com.epam.ta.reportportal.ws.converter.converters.UserFilterConverter;
import com.epam.ta.reportportal.ws.model.ErrorType;
import com.epam.ta.reportportal.ws.model.SharedEntity;
import com.epam.ta.reportportal.ws.model.filter.UserFilterResource;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Pavel Bortnik
 */
@Service
@Transactional(readOnly = true)
public class GetUserFilterHandlerImpl implements IGetUserFilterHandler {

	private UserFilterRepository filterRepository;

	@Autowired
	public void setFilterRepository(UserFilterRepository filterRepository) {
		this.filterRepository = filterRepository;
	}

	@Override
	public UserFilterResource getFilter(Long filterId, ReportPortalUser.ProjectDetails projectDetails, ReportPortalUser user) {
		UserFilter filter = filterRepository.findById(filterId)
				.orElseThrow(() -> new ReportPortalException(ErrorType.USER_FILTER_NOT_FOUND, filterId, user.getUsername()));
		return UserFilterConverter.TO_FILTER_RESOURCE.apply(filter);
	}

	@Override
	public Iterable<UserFilterResource> getFilters(Filter filter, Pageable pageable, ReportPortalUser.ProjectDetails projectDetails,
			ReportPortalUser user) {
		Page<UserFilter> filters = filterRepository.findByFilter(filter, pageable);
		return PagedResourcesAssembler.pageConverter(UserFilterConverter.TO_FILTER_RESOURCE).apply(filters);
	}

	@Override
	public List<UserFilterResource> getOwnFilters(Filter filter, ReportPortalUser.ProjectDetails projectDetails, ReportPortalUser user) {
		throw new UnsupportedOperationException("Not implemented until acl logic");
	}

	@Override
	public List<UserFilterResource> getSharedFilters(Filter filter, ReportPortalUser.ProjectDetails projectDetails, ReportPortalUser user) {
		throw new UnsupportedOperationException("Not implemented until acl logic");
	}

	@Override
	public Iterable<SharedEntity> getFiltersNames(ReportPortalUser.ProjectDetails projectDetails, ReportPortalUser user, boolean isShared) {
		throw new UnsupportedOperationException("Not implemented until acl logic");
	}

	@Override
	public List<UserFilterResource> getFilters(Long[] ids, ReportPortalUser.ProjectDetails projectDetails, ReportPortalUser user) {
		List<UserFilter> filters = filterRepository.findAllById(Lists.newArrayList(ids));
		return filters.stream().map(UserFilterConverter.TO_FILTER_RESOURCE).collect(Collectors.toList());
	}
}
