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
package io.knotx.fragments.task.factory.api.metadata;

import io.vertx.core.json.JsonObject;

import java.util.Objects;

public class OperationMetadata {

  private final String factory;

  private final JsonObject data;

  public OperationMetadata(String factory) {
    this(factory, new JsonObject());
  }

  public OperationMetadata(String factory, JsonObject data) {
    this.factory = factory;
    this.data = data;
  }

  public String getFactory() {
    return factory;
  }

  public JsonObject getData() {
    return data;
  }

  @Override
  public String toString() {
    return "OperationMetadata{" +
        "factory='" + factory + '\'' +
        ", data=" + data +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OperationMetadata that = (OperationMetadata) o;
    return Objects.equals(factory, that.factory) &&
        Objects.equals(data, that.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(factory, data);
  }
}
