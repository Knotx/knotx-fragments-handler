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
package io.knotx.fragments.task.factory.generic;

import static io.vertx.core.Future.succeededFuture;

import io.knotx.fragments.action.api.Action;
import io.knotx.fragments.action.api.ActionFactory;
import io.knotx.fragments.api.Fragment;
import io.knotx.fragments.api.FragmentResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class TestAction implements ActionFactory {

  @Override
  public String getName() {
    return "test-action";
  }

  @Override
  public Action create(String alias, JsonObject config, Vertx vertx, Action doAction) {
    String transition = config.getString("transition");

    return (fragmentContext, resultHandler) -> {
      Fragment fragment = fragmentContext.getFragment();
      if (config.containsKey("jsonBody")) {
        fragment.setBody(config.getJsonObject("jsonBody", new JsonObject()).toString());
      } else if (config.containsKey("body")) {
        fragment.setBody(config.getString("body", "any"));
      }

      Future<FragmentResult> resultFuture = succeededFuture(
          new FragmentResult(fragment, transition));
      resultFuture.onComplete(resultHandler);
    };
  }
}
