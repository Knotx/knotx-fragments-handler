/*
 * Copyright (C) 2019 Knot.x Project
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
package io.knotx.fragments.task.factory.generic.exception;

public class NodeFactoryNotFoundException extends IllegalArgumentException {

  private String factory;

  public NodeFactoryNotFoundException(String factory) {
    super("Node factory not registered for [" + factory + "]");
    this.factory = factory;
  }

  public String getFactory() {
    return factory;
  }
}