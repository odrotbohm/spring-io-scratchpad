/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.order;

import static org.assertj.core.api.Assertions.*;

import example.customer.Customer.CustomerIdentifier;
import example.order.EventPublicationRegistryTests.FailingAsyncTransactionalEventListener;
import example.order.Order.OrderCompleted;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.ApplicationModuleListener;
import org.springframework.modulith.events.EventPublicationRegistry;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * @author Oliver Drotbohm
 */
@ApplicationModuleTest
@RequiredArgsConstructor
@Import(FailingAsyncTransactionalEventListener.class)
@DirtiesContext
class EventPublicationRegistryTests {

	private final OrderManagement orders;
	private final EventPublicationRegistry registry;

	@Test
	void leavesPublicationIncompleteForFailingListener() throws Exception {

		var order = new Order(new CustomerIdentifier(UUID.randomUUID()));

		orders.complete(order);

		Thread.sleep(40);

		assertThat(registry.findIncompletePublications()).hasSize(1);
	}

	static class FailingAsyncTransactionalEventListener {

		@ApplicationModuleListener
		void foo(OrderCompleted event) {
			throw new IllegalStateException();
		}
	}
}
