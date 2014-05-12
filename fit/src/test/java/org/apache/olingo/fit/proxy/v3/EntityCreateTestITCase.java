/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.fit.proxy.v3;

import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.Customer;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.Employee;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.Message;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.MessageKey;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.Order;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.OrderCollection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;

import org.junit.Test;

/**
 * This is the unit test class to check entity create operations.
 */
public class EntityCreateTestITCase extends AbstractTest {

    @Test
    @Ignore
    public void create() {
        final String sampleName = "sample customer from proxy";
        final Integer id = 100;

        getSampleCustomerProfile(id, sampleName, container);
        container.flush();

        Customer actual = readCustomer(container, id);
        checkSampleCustomerProfile(actual, id, sampleName);

        container.getCustomer().delete(actual.getCustomerId());
        actual = container.getCustomer().get(id);
        assertNull(actual);

        entityContext.detachAll();
        actual = container.getCustomer().get(id);
        assertNotNull(actual);

        container.getCustomer().delete(actual.getCustomerId());
        container.flush();

        actual = container.getCustomer().get(id);
        assertNull(actual);

        entityContext.detachAll();
        actual = container.getCustomer().get(id);
        assertNull(actual);
    }

    @Test
    @Ignore
    public void createEmployee() {
        final Integer id = 101;

        final Employee employee = container.getPerson().newEmployee();
        employee.setPersonId(id);
        employee.setName("sample employee from proxy");
        employee.setManagersPersonId(-9918);
        employee.setSalary(2147483647);
        employee.setTitle("CEO");

        container.flush();

        Employee actual = container.getPerson().get(id, Employee.class);
        assertNotNull(actual);
        assertEquals(id, actual.getPersonId());

        entityContext.detachAll();
        actual = container.getPerson().get(id, Employee.class);
        assertNotNull(actual);

        container.getPerson().delete(actual.getPersonId());
        container.flush();

        actual = container.getPerson().get(id, Employee.class);
        assertNull(actual);

        entityContext.detachAll();
        actual = container.getPerson().get(id, Employee.class);
        assertNull(actual);
    }

    @Test
    @Ignore
    public void createWithNavigation() {
        final String sampleName = "sample customer from proxy with navigation";
        final Integer id = 101;

        final Customer original = getSampleCustomerProfile(id, sampleName, container);
        original.setInfo(container.getCustomerInfo().get(16));
        container.flush();

        Customer actual = readCustomer(container, id);
        checkSampleCustomerProfile(actual, id, sampleName);
        assertEquals(Integer.valueOf(16), actual.getInfo().getCustomerInfoId());

        container.getCustomer().delete(actual.getCustomerId());
        container.flush();

        actual = container.getCustomer().get(id);
        assertNull(actual);
    }

    @Test
    @Ignore
    public void createWithBackNavigation() {
        final String sampleName = "sample customer from proxy with back navigation";
        final Integer id = 102;

        Order order = container.getOrder().newOrder();
        order.setCustomerId(id);
        order.setOrderId(id); // same id ...

        final Customer customer = getSampleCustomerProfile(id, sampleName, container);

        final OrderCollection orders = container.getOrder().newOrderCollection();
        orders.add(order);

        customer.setOrders(orders);
        order.setCustomer(customer);
        container.flush();

        assertEquals(id, order.getOrderId());
        assertEquals(id, order.getCustomerId());

        Customer actual = readCustomer(container, id);
        checkSampleCustomerProfile(actual, id, sampleName);

        assertEquals(1, actual.getOrders().size());
        assertEquals(id, actual.getOrders().iterator().next().getOrderId());
        assertEquals(id, actual.getOrders().iterator().next().getCustomerId());

        order = container.getOrder().get(id);
        assertNotNull(order);
        assertEquals(id, order.getCustomer().getCustomerId());

        container.getOrder().delete(actual.getOrders());
        container.flush();

        order = container.getOrder().get(id);
        assertNull(order);

        actual = readCustomer(container, id);
        assertTrue(actual.getOrders().isEmpty());

        container.getCustomer().delete(actual.getCustomerId());
        container.flush();

        actual = container.getCustomer().get(id);
        assertNull(actual);
    }

    @Test
    @Ignore
    public void multiKey() {
        Message message = container.getMessage().newMessage();
        message.setMessageId(100);
        message.setFromUsername("fromusername");
        message.setToUsername("myusername");
        message.setIsRead(false);
        message.setSubject("test message");
        message.setBody("test");

        container.flush();

        MessageKey key = new MessageKey();
        key.setFromUsername("fromusername");
        key.setMessageId(100);

        message = container.getMessage().get(key);
        assertNotNull(message);
        assertEquals(Integer.valueOf(100), message.getMessageId());
        assertEquals("fromusername", message.getFromUsername());
        assertEquals("myusername", message.getToUsername());
        assertEquals("test message", message.getSubject());
        assertEquals("test", message.getBody());

        container.getMessage().delete(key);
        container.flush();

        assertNull(container.getMessage().get(key));
    }
}