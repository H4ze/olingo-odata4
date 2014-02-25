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
package org.apache.olingo.odata4.client.api.edm.v3;

import org.apache.olingo.odata4.client.api.edm.CommonProperty;
import org.apache.olingo.odata4.commons.api.edm.constants.EdmContentKind;

public interface Property extends CommonProperty {

  String getFcSourcePath();

  void setFcSourcePath(String fcSourcePath);

  String getFcTargetPath();

  void setFcTargetPath(String fcTargetPath);

  EdmContentKind getFcContentKind();

  void setFcContentKind(EdmContentKind fcContentKind);

  String getFcNSPrefix();

  void setFcNSPrefix(String fcNSPrefix);

  String getFcNSURI();

  void setFcNSURI(String fcNSURI);

  boolean isFcKeepInContent();

  void setFcKeepInContent(boolean fcKeepInContent);

}