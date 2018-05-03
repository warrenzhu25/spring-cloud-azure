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

package eventhub.integration.outbound;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import eventhub.support.EventHubHeaders;
import org.springframework.expression.EvaluationContext;
import org.springframework.integration.expression.ExpressionUtils;
import org.springframework.integration.handler.AbstractMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * Outbound channel adapter to publish messages to Google Cloud Pub/Sub.
 *
 * <p>It delegates Google Cloud Pub/Sub interaction to {@link PubSubOperations}. It also converts
 * the {@link Message} payload into a {@link EventData} accepted by the Google Cloud Pub/Sub
 * Client Library. It supports synchronous and asynchronous sending.
 *
 * @author Warren Zhu
 */
public class EventHubMessageHandler extends AbstractMessageHandler {

	private static final long DEFAULT_TIMEOUT = 10000;

	private final EventHubClient eventHubClient;
	private boolean sync;

	private EvaluationContext evaluationContext;

	private ListenableFutureCallback<String> publishCallback;

	public EventHubMessageHandler(EventHubClient eventHubClient) {
		this.eventHubClient = eventHubClient;
	}

	@Override
	protected void handleMessageInternal(Message<?> message) throws Exception {
		Object payload = message.getPayload();
		String topic = message.getHeaders().containsKey(EventHubHeaders.TOPIC)
				? message.getHeaders().get(EventHubHeaders.TOPIC, String.class)
				: this.topicExpression.getValue(this.evaluationContext, message, String.class);
		
		String partitionKey = message.getHeaders().get(EventHubHeaders.PARTITION_KEY, String.class);
		int partitonId = message.getHeaders().get(EventHubHeaders.PARTITION_KEY, Integer.class);

		if (payload instanceof EventData) {
			this.eventHubClient.sendSync((EventData) payload);
			return;
		}

		ByteString pubsubPayload;

		if (payload instanceof byte[]) {
			pubsubPayload = ByteString.copyFrom((byte[]) payload);
		}
		else if (payload instanceof ByteString) {
			pubsubPayload = (ByteString) payload;
		}
		else {
			pubsubPayload =	ByteString.copyFrom(
					(String) this.messageConverter.fromMessage(message, String.class),
					Charset.defaultCharset());
		}

		Map<String, String> headers = new HashMap<>();
		message.getHeaders().forEach(
				(key, value) -> headers.put(key, value.toString()));

		ListenableFuture<String> pubsubFuture =
				this.pubSubTemplate.publish(topic, pubsubPayload, headers);

		if (this.publishCallback != null) {
			pubsubFuture.addCallback(this.publishCallback);
		}

		if (this.sync) {
			Long timeout = this.publishTimeoutExpression.getValue(
					this.evaluationContext, message, Long.class);
			if (timeout == null || timeout < 0) {
				pubsubFuture.get();
			}
			else {
				pubsubFuture.get(timeout, TimeUnit.MILLISECONDS);
			}
		}
	}


	public boolean isSync() {
		return this.sync;
	}

	/**
	 * Set publish method to be synchronous or asynchronous.
	 *
	 * <p>Publish is asynchronous be default.
	 * @param sync true for synchronous, false for asynchronous
	 */
	public void setSync(boolean sync) {
		this.sync = sync;
	}

	@Override
	protected void onInit() throws Exception {
		super.onInit();
		this.evaluationContext = ExpressionUtils.createStandardEvaluationContext(getBeanFactory());
	}
}
