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
package com.epam.ta.reportportal.util.email;

import com.epam.reportportal.commons.template.TemplateEngine;
import com.epam.ta.reportportal.dao.ServerSettingsRepository;
import com.epam.ta.reportportal.entity.ServerSettings;
import com.epam.ta.reportportal.entity.ServerSettingsEnum;
import com.epam.ta.reportportal.entity.enums.ProjectAttributeEnum;
import com.epam.ta.reportportal.entity.project.ProjectAttribute;
import com.epam.ta.reportportal.entity.project.ProjectUtils;
import com.epam.ta.reportportal.exception.ReportPortalException;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.epam.ta.reportportal.ws.model.ErrorType.EMAIL_CONFIGURATION_IS_INCORRECT;
import static java.util.Optional.ofNullable;

/**
 * Factory for {@link EmailService}
 *
 * @author Andrei Varabyeu
 */
@Service
public class MailServiceFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceFactory.class);
	private static final int DEFAULT_CONNECTION_TIMEOUT = 5000;

	private final TemplateEngine templateEngine;
	private final BasicTextEncryptor encryptor;
	private final ServerSettingsRepository settingsRepository;

	@Autowired
	public MailServiceFactory(TemplateEngine templateEngine, BasicTextEncryptor encryptor, ServerSettingsRepository settingsRepository) {
		this.templateEngine = templateEngine;
		this.encryptor = encryptor;
		this.settingsRepository = settingsRepository;
	}

	/**
	 * Build mail service based on provided configs
	 *
	 * @param projectAttributes Project configuration attributes
	 * @param serverSettings    Server-level configuration
	 * @return Built email service
	 */
	private Optional<EmailService> getEmailService(Set<ProjectAttribute> projectAttributes, List<ServerSettings> serverSettings) {

		Map<String, String> configuration = ProjectUtils.getConfigParameters(projectAttributes);

		return getEmailService(serverSettings).flatMap(service -> {
			// if there is server email config, let's check project config
			if (MapUtils.isNotEmpty(configuration)) {
				// if project config is present, check whether sending emails is enabled and replace server properties with project properties
				if (BooleanUtils.toBoolean(configuration.get(ProjectAttributeEnum.EMAIL_ENABLED.getAttribute()))) {
					//update of present on project level
					ofNullable(configuration.get(ProjectAttributeEnum.EMAIL_FROM.getAttribute())).ifPresent(service::setFrom);
				}

			}
			return Optional.of(service);
		});
	}

	/**
	 * Build mail service based on provided configs
	 *
	 * @param serverSettings Server-level configuration
	 * @return Built email service
	 */
	public Optional<EmailService> getEmailService(List<ServerSettings> serverSettings) {

		Map<String, String> config = serverSettings.stream()
				.collect(Collectors.toMap(ServerSettings::getKey, ServerSettings::getValue, (prev, curr) -> prev));

		if (MapUtils.isNotEmpty(config) && BooleanUtils.toBoolean(config.get(ServerSettingsEnum.ENABLED.getAttribute()))) {

			boolean authRequired = BooleanUtils.toBoolean(config.get(ServerSettingsEnum.AUTH_ENABLED.getAttribute()));

			Properties javaMailProperties = new Properties();
			javaMailProperties.put("mail.smtp.connectiontimeout", DEFAULT_CONNECTION_TIMEOUT);
			javaMailProperties.put("mail.smtp.auth", authRequired);
			javaMailProperties.put("mail.smtp.starttls.enable",
					authRequired && BooleanUtils.toBoolean(config.get(ServerSettingsEnum.STAR_TLS_ENABLED.getAttribute()))
			);

			if (BooleanUtils.toBoolean(ServerSettingsEnum.SSL_ENABLED.getAttribute())) {
				javaMailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				javaMailProperties.put("mail.smtp.socketFactory.fallback", "false");
			}

			EmailService service = new EmailService(javaMailProperties);
			service.setTemplateEngine(templateEngine);
			service.setHost(config.get(ServerSettingsEnum.HOST.getAttribute()));
			service.setPort(Integer.parseInt(config.get(ServerSettingsEnum.PORT.getAttribute())));
			service.setProtocol(config.get(ServerSettingsEnum.PROTOCOL.getAttribute()));
			service.setFrom(config.get(ServerSettingsEnum.FROM.getAttribute()));
			if (authRequired) {
				service.setUsername(config.get(ServerSettingsEnum.USERNAME.getAttribute()));
				service.setPassword(encryptor.decrypt(config.get(ServerSettingsEnum.PASSWORD.getAttribute())));
			}
			return Optional.of(service);

		}

		return Optional.empty();
	}

	//TODO refactor docs

	/**
	 * Build mail service based on default server configs and checks connection
	 *
	 * @return Built email service
	 */
	public Optional<EmailService> getDefaultEmailService() {
		return getEmailService(settingsRepository.findAll());
	}

	/**
	 * Build mail service based on default server configs and checks connection
	 *
	 * @return Built email service
	 */
	public Optional<EmailService> getDefaultEmailService(Set<ProjectAttribute> projectAttributes) {
		return getEmailService(projectAttributes, settingsRepository.findAll());
	}

	/**
	 * Build mail service based on default server configs and checks connection
	 *
	 * @return Built email service
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public EmailService getDefaultEmailService(Set<ProjectAttribute> projectAttributes, boolean checkConnection) {
		EmailService emailService = getEmailService(projectAttributes,
				settingsRepository.findAll()
		).orElseThrow(() -> emailConfigurationFail(null));

		if (checkConnection) {
			checkConnection(emailService);
		}
		return emailService;
	}

	/**
	 * Build mail service based on default server configs and checks connection
	 *
	 * @return Built email service
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public EmailService getDefaultEmailService(boolean checkConnection) {
		EmailService emailService = getEmailService(settingsRepository.findAll()).orElseThrow(() -> emailConfigurationFail(null));

		if (checkConnection) {
			checkConnection(emailService);
		}
		return emailService;
	}

	public void checkConnection(@Nullable EmailService service) {
		try {
			if (null == service) {
				throw emailConfigurationFail(null);
			} else {
				service.testConnection();
			}
		} catch (Exception e) {
			throw emailConfigurationFail(e);
		}
	}

	private ReportPortalException emailConfigurationFail(Throwable e) {
		if (null != e) {
			LOGGER.error("Cannot send email to user", e);
		}
		return new ReportPortalException(EMAIL_CONFIGURATION_IS_INCORRECT, "Please configure email server in Report Portal settings.");
	}

}
