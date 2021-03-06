/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.fit.tecsvc.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.response.ODataInvokeResponse;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.domain.ODataEntitySet;
import org.apache.olingo.commons.api.domain.ODataProperty;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class ActionImportITCase extends AbstractBaseTestITCase {

  @Test
  public void primitveAction() throws Exception {
    URI actionURI =
        getClient().newURIBuilder(TecSvcConst.BASE_URI).appendActionCallSegment("AIRTString").build();
    ODataInvokeResponse<ODataProperty> response =
        getClient().getInvokeRequestFactory().getActionInvokeRequest(actionURI, ODataProperty.class).execute();
    assertEquals(200, response.getStatusCode());
    assertEquals("UARTString string value", response.getBody().getPrimitiveValue().toValue());
  }

  @Test
  public void primitveActionInvalidParameters() throws Exception {
    Map<String, ODataValue> parameters = new HashMap<String, ODataValue>();
    parameters.put("Invalid", getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(1));
    URI actionURI =
        getClient().newURIBuilder(TecSvcConst.BASE_URI).appendActionCallSegment("AIRTString").build();
    try {
      getClient().getInvokeRequestFactory().getActionInvokeRequest(actionURI, ODataProperty.class, parameters)
          .execute();
      fail("Expected an ODataClientErrorException");
    } catch (ODataClientErrorException e) {
      assertEquals(400, e.getStatusLine().getStatusCode());
    }
  }

  @Test
  public void primitveCollectionAction() throws Exception {
    URI actionURI =
        getClient().newURIBuilder(TecSvcConst.BASE_URI).appendActionCallSegment("AIRTCollStringTwoParam").build();
    Map<String, ODataValue> parameters = new HashMap<String, ODataValue>();
    parameters.put("ParameterInt16", getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt16((short) 3));
    parameters.put("ParameterDuration", getClient().getObjectFactory().newPrimitiveValueBuilder().setType(
        EdmPrimitiveTypeKind.Duration).setValue(new BigDecimal(1)).build());
    ODataInvokeResponse<ODataProperty> response =
        getClient().getInvokeRequestFactory().getActionInvokeRequest(actionURI, ODataProperty.class, parameters)
            .execute();
    assertEquals(200, response.getStatusCode());
    ODataCollectionValue<ODataValue> valueArray = response.getBody().getCollectionValue();
    assertEquals(3, valueArray.size());
    Iterator<ODataValue> iterator = valueArray.iterator();
    assertEquals("PT1S", iterator.next().asPrimitive().toValue());
    assertEquals("PT2S", iterator.next().asPrimitive().toValue());
    assertEquals("PT3S", iterator.next().asPrimitive().toValue());
  }

  @Test
  public void complexAction() throws Exception {
    URI actionURI =
        getClient().newURIBuilder(TecSvcConst.BASE_URI).appendActionCallSegment("AIRTCTTwoPrimParam").build();
    Map<String, ODataValue> parameters = new HashMap<String, ODataValue>();
    parameters.put("ParameterInt16", getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt16((short) 3));
    ODataInvokeResponse<ODataProperty> response =
        getClient().getInvokeRequestFactory().getActionInvokeRequest(actionURI, ODataProperty.class, parameters)
            .execute();
    assertEquals(200, response.getStatusCode());
    ODataComplexValue complexValue = response.getBody().getComplexValue();
    ODataProperty propInt16 = complexValue.get("PropertyInt16");
    assertNotNull(propInt16);
    assertEquals(3, propInt16.getPrimitiveValue().toValue());
    ODataProperty propString = complexValue.get("PropertyString");
    assertNotNull(propString);
    assertEquals("UARTCTTwoPrimParam string value", propString.getPrimitiveValue().toValue());
  }

  @Test
  public void complexCollectionActionNoContent() throws Exception {
    URI actionURI =
        getClient().newURIBuilder(TecSvcConst.BASE_URI).appendActionCallSegment("AIRTCollCTTwoPrimParam").build();
    Map<String, ODataValue> parameters = new HashMap<String, ODataValue>();
    parameters.put("ParameterInt16", getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt16((short) 0));
    ODataInvokeResponse<ODataProperty> response =
        getClient().getInvokeRequestFactory().getActionInvokeRequest(actionURI, ODataProperty.class, parameters)
            .execute();
    assertEquals(200, response.getStatusCode());
    ODataCollectionValue<ODataValue> complexValueCollection = response.getBody().getCollectionValue();
    assertEquals(0, complexValueCollection.size());
  }

  @Test
  public void complexCollectionActionSubContent() throws Exception {
    URI actionURI =
        getClient().newURIBuilder(TecSvcConst.BASE_URI).appendActionCallSegment("AIRTCollCTTwoPrimParam").build();
    Map<String, ODataValue> parameters = new HashMap<String, ODataValue>();
    parameters.put("ParameterInt16", getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt16((short) 1));
    ODataInvokeResponse<ODataProperty> response =
        getClient().getInvokeRequestFactory().getActionInvokeRequest(actionURI, ODataProperty.class, parameters)
            .execute();
    assertEquals(200, response.getStatusCode());
    ODataCollectionValue<ODataValue> complexValueCollection = response.getBody().getCollectionValue();
    assertEquals(1, complexValueCollection.size());
    Iterator<ODataValue> iterator = complexValueCollection.iterator();

    ODataComplexValue next = iterator.next().asComplex();
    assertEquals(16, next.get("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("Test123", next.get("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void complexCollectionActionAllContent() throws Exception {
    URI actionURI =
        getClient().newURIBuilder(TecSvcConst.BASE_URI).appendActionCallSegment("AIRTCollCTTwoPrimParam").build();
    Map<String, ODataValue> parameters = new HashMap<String, ODataValue>();
    parameters.put("ParameterInt16", getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt16((short) 3));
    ODataInvokeResponse<ODataProperty> response =
        getClient().getInvokeRequestFactory().getActionInvokeRequest(actionURI, ODataProperty.class, parameters)
            .execute();
    assertEquals(200, response.getStatusCode());
    ODataCollectionValue<ODataValue> complexValueCollection = response.getBody().getCollectionValue();
    assertEquals(3, complexValueCollection.size());
    Iterator<ODataValue> iterator = complexValueCollection.iterator();

    ODataComplexValue next = iterator.next().asComplex();
    assertEquals(16, next.get("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("Test123", next.get("PropertyString").getPrimitiveValue().toValue());

    next = iterator.next().asComplex();
    assertEquals(17, next.get("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("Test456", next.get("PropertyString").getPrimitiveValue().toValue());

    next = iterator.next().asComplex();
    assertEquals(18, next.get("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("Test678", next.get("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void entityActionETTwoKeyTwoPrim() throws Exception {
    URI actionURI =
        getClient().newURIBuilder(TecSvcConst.BASE_URI).appendActionCallSegment("AIRTETTwoKeyTwoPrimParam").build();
    Map<String, ODataValue> parameters = new HashMap<String, ODataValue>();
    parameters
        .put("ParameterInt16", getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt16((short) -365));
    ODataInvokeResponse<ODataEntity> response =
        getClient().getInvokeRequestFactory().getActionInvokeRequest(actionURI, ODataEntity.class, parameters)
            .execute();
    assertEquals(200, response.getStatusCode());
    ODataEntity entity = response.getBody();
    ODataProperty propInt16 = entity.getProperty("PropertyInt16");
    assertNotNull(propInt16);
    assertEquals(-365, propInt16.getPrimitiveValue().toValue());
    ODataProperty propString = entity.getProperty("PropertyString");
    assertNotNull(propString);
    assertEquals("Test String2", propString.getPrimitiveValue().toValue());
  }

  @Test
  public void entityCollectionActionETKeyNav() throws Exception {
    URI actionURI =
        getClient().newURIBuilder(TecSvcConst.BASE_URI).appendActionCallSegment("AIRTCollETKeyNavParam").build();
    Map<String, ODataValue> parameters = new HashMap<String, ODataValue>();
    parameters
        .put("ParameterInt16", getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt16((short) 3));
    ODataInvokeResponse<ODataEntitySet> response =
        getClient().getInvokeRequestFactory().getActionInvokeRequest(actionURI, ODataEntitySet.class, parameters)
            .execute();
    assertEquals(200, response.getStatusCode());
    ODataEntitySet entitySet = response.getBody();
    assertEquals(3, entitySet.getEntities().size());
    Integer key = 1;
    for (ODataEntity entity : entitySet.getEntities()) {
      assertEquals(key, entity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
      key++;
    }
  }

  @Test
  public void entityCollectionActionETKeyNavEmptyCollection() throws Exception {
    URI actionURI =
        getClient().newURIBuilder(TecSvcConst.BASE_URI).appendActionCallSegment("AIRTCollETKeyNavParam").build();
    Map<String, ODataValue> parameters = new HashMap<String, ODataValue>();
    parameters
        .put("ParameterInt16", getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt16((short) 0));
    ODataInvokeResponse<ODataEntitySet> response =
        getClient().getInvokeRequestFactory().getActionInvokeRequest(actionURI, ODataEntitySet.class, parameters)
            .execute();
    assertEquals(200, response.getStatusCode());
    ODataEntitySet entitySet = response.getBody();
    assertEquals(0, entitySet.getEntities().size());
  }

  @Test
  public void entityCollectionActionETKeyNavNegativeParam() throws Exception {
    URI actionURI =
        getClient().newURIBuilder(TecSvcConst.BASE_URI).appendActionCallSegment("AIRTCollETKeyNavParam").build();
    Map<String, ODataValue> parameters = new HashMap<String, ODataValue>();
    parameters
        .put("ParameterInt16", getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt16((short) -10));
    ODataInvokeResponse<ODataEntitySet> response =
        getClient().getInvokeRequestFactory().getActionInvokeRequest(actionURI, ODataEntitySet.class, parameters)
            .execute();
    assertEquals(200, response.getStatusCode());
    ODataEntitySet entitySet = response.getBody();
    assertEquals(0, entitySet.getEntities().size());
  }

  @Test
  public void entityCollectionActionETAllPrim() throws Exception {
    URI actionURI =
        getClient().newURIBuilder(TecSvcConst.BASE_URI).appendActionCallSegment("AIRTCollESAllPrimParam").build();
    Calendar time = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    time.clear();
    time.set(Calendar.HOUR_OF_DAY, 3);
    time.set(Calendar.MINUTE, 0);
    time.set(Calendar.SECOND, 0);
    Map<String, ODataValue> parameters = new HashMap<String, ODataValue>();
    parameters
        .put("ParameterTimeOfDay", getClient().getObjectFactory().newPrimitiveValueBuilder().setType(
            EdmPrimitiveTypeKind.TimeOfDay).setValue(time).build());
    ODataInvokeResponse<ODataEntitySet> response =
        getClient().getInvokeRequestFactory().getActionInvokeRequest(actionURI, ODataEntitySet.class, parameters)
            .execute();
    assertEquals(200, response.getStatusCode());
    ODataEntitySet entitySet = response.getBody();
    assertEquals(3, entitySet.getEntities().size());
    Integer key = 1;
    for (ODataEntity entity : entitySet.getEntities()) {
      assertEquals(key, entity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
      key++;
    }
  }

  @Test
  public void entityActionETAllPrim() throws Exception {
    URI actionURI =
        getClient().newURIBuilder(TecSvcConst.BASE_URI).appendActionCallSegment("AIRTESAllPrimParam").build();
    Calendar dateTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    dateTime.clear();
    dateTime.set(1012, 2, 0, 0, 0, 0);
    Map<String, ODataValue> parameters = new HashMap<String, ODataValue>();
    parameters
        .put("ParameterDate", getClient().getObjectFactory().newPrimitiveValueBuilder().setType(
            EdmPrimitiveTypeKind.Date).setValue(dateTime).build());
    ODataInvokeResponse<ODataEntity> response =
        getClient().getInvokeRequestFactory().getActionInvokeRequest(actionURI, ODataEntity.class, parameters)
            .execute();
    // Check 201
    assertEquals(201, response.getStatusCode());
  }

  @Override
  protected ODataClient getClient() {
    ODataClient odata = ODataClientFactory.getClient();
    odata.getConfiguration().setDefaultPubFormat(ODataFormat.JSON_NO_METADATA);
    return odata;
  }

}
