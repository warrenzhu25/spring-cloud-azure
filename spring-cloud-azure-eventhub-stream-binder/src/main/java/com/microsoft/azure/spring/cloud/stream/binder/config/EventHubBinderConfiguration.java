/*
 *  Copyright 2017 original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.microsoft.azure.spring.cloud.stream.binder.config;

import com.microsoft.azure.spring.cloud.stream.binder.provisioning.EventHubChannelProvisioner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.binder.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author João André Martins
 */
@Configuration
@ConditionalOnMissingBean(Binder.class)
@EnableConfigurationProperties(EventHubExtendedBindingProperties.class)
public class EventHubBinderConfiguration {

	@Bean
	public EventHubChannelProvisioner EventHubChannelProvisioner(EventHubAdmin EventHubAdmin) {
		return new EventHubChannelProvisioner(EventHubAdmin);
	}

	@Bean
	public EventHubMessageChannelBinder EventHubBinder(
			EventHubChannelProvisioner EventHubChannelProvisioner,
			EventHubTemplate EventHubTemplate) {
		return new EventHubMessageChannelBinder(null, EventHubChannelProvisioner, EventHubTemplate);
	}
}
