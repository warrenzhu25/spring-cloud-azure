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

package com.microsoft.azure.spring.cloud.stream.binder.provisioning;

import com.microsoft.azure.spring.cloud.stream.binder.properties.EventHubConsumerProperties;
import com.microsoft.azure.spring.cloud.stream.binder.properties.EventHubProducerProperties;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.cloud.stream.provisioning.ProvisioningException;
import org.springframework.cloud.stream.provisioning.ProvisioningProvider;

/**
 * @author João André Martins
 */
public class EventHubChannelProvisioner
		implements ProvisioningProvider<ExtendedConsumerProperties<EventHubConsumerProperties>,
		ExtendedProducerProperties<EventHubProducerProperties>> {

	//private final EventHubAdmin EventHubAdmin;

//	public EventHubChannelProvisioner(EventHubAdmin EventHubAdmin) {
//		this.EventHubAdmin = EventHubAdmin;
//	}

	@Override
	public ProducerDestination provisionProducerDestination(String name,
			ExtendedProducerProperties<EventHubProducerProperties> properties)
			throws ProvisioningException {
//		if (this.EventHubAdmin.getTopic(name) == null) {
//			this.EventHubAdmin.createTopic(name);
//		}
//
		return new EventHubProducerDestination(name);
	}

	@Override
	public ConsumerDestination provisionConsumerDestination(String name, String group,
			ExtendedConsumerProperties<EventHubConsumerProperties> properties)
			throws ProvisioningException {
		/*
		String subscription = group == null ? name : (name + '.' + group);
		if (this.EventHubAdmin.getSubscription(subscription) == null) {
			if (properties.getExtension().isAutoCreateResources()) {
				if (this.EventHubAdmin.getTopic(name) == null) {
					this.EventHubAdmin.createTopic(name);
				}

				this.EventHubAdmin.createSubscription(subscription, name);
			}
			else {
				throw new ProvisioningException("Unexisting '" + subscription + "' subscription.");
			}
		}
		*/
		return new EventHubConsumerDestination(subscription);
	}
}
