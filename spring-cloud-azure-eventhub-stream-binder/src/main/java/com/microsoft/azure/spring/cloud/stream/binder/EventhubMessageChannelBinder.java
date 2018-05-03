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

package com.microsoft.azure.spring.cloud.stream.binder;


import com.microsoft.azure.spring.cloud.stream.binder.properties.EventHubConsumerProperties;
import com.microsoft.azure.spring.cloud.stream.binder.properties.EventHubExtendedBindingProperties;
import com.microsoft.azure.spring.cloud.stream.binder.properties.EventHubProducerProperties;
import com.microsoft.azure.spring.cloud.stream.binder.provisioning.EventHubChannelProvisioner;
import org.springframework.cloud.stream.binder.AbstractMessageChannelBinder;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.ExtendedPropertiesBinder;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.integration.core.MessageProducer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import eventhub.core.EventHubTemplate;
import eventhub.integration.inbound.EventHubInboundChannelAdapter;
import eventhub.integration.outbound.EventHubMessageHandler;

/**
 * @author Warren Zhu
 */
public class EventhubMessageChannelBinder
		extends AbstractMessageChannelBinder<ExtendedConsumerProperties<EventHubConsumerProperties>,
                ExtendedProducerProperties<EventHubProducerProperties>,
                EventHubChannelProvisioner>
	implements ExtendedPropertiesBinder<MessageChannel, EventHubConsumerProperties,
            EventHubProducerProperties> {

	private EventHubTemplate pubSubTemplate;

	private EventHubExtendedBindingProperties bindingProperties =
			new EventHubExtendedBindingProperties();

	public EventhubMessageChannelBinder(String[] headersToEmbed,
                                        EventHubChannelProvisioner provisioningProvider, EventHubTemplate pubSubTemplate) {
		super(headersToEmbed, provisioningProvider);
		this.pubSubTemplate = pubSubTemplate;
	}

	@Override
	protected MessageHandler createProducerMessageHandler(ProducerDestination destination,
														  ExtendedProducerProperties<EventHubProducerProperties> producerProperties,
														  MessageChannel errorChannel) {
		return new EventHubMessageHandler(this.pubSubTemplate, destination.getName());
	}

	@Override
	protected MessageProducer createConsumerEndpoint(ConsumerDestination destination, String group,
													 ExtendedConsumerProperties<EventHubConsumerProperties> properties) {
		EventHubInboundChannelAdapter inboundAdapter =
				new EventHubInboundChannelAdapter(this.pubSubTemplate, destination.getName());
		// Lets Stream do the message payload conversion.
		inboundAdapter.setMessageConverter(null);

		return inboundAdapter;
	}

	@Override
	public EventHubConsumerProperties getExtendedConsumerProperties(String channelName) {
		return this.bindingProperties.getExtendedConsumerProperties(channelName);
	}

	@Override
	public EventHubProducerProperties getExtendedProducerProperties(String channelName) {
		return this.bindingProperties.getExtendedProducerProperties(channelName);
	}

}
