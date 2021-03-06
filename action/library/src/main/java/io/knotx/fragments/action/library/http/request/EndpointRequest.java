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
package io.knotx.fragments.action.library.http.request;

import io.vertx.reactivex.core.MultiMap;
import org.apache.commons.lang3.StringUtils;

public class EndpointRequest {

  private final String path;
  private final MultiMap headers;
  private final String body;

  public EndpointRequest(String path, MultiMap headers) {
    this(path, headers, StringUtils.EMPTY);
  }

  public EndpointRequest(String path, MultiMap headers, String body) {
    this.path = path;
    this.headers = headers;
    this.body = body;
  }

  public String getPath() {
    return path;
  }

  public MultiMap getHeaders() {
    return headers;
  }

  public String getBody() {
    return body;
  }

}
