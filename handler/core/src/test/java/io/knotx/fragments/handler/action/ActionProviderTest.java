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
 *
 * The code comes from https://github.com/tomaszmichalak/vertx-rx-map-reduce.
 */
package io.knotx.fragments.handler.action;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;

import io.knotx.fragments.handler.api.domain.FragmentContext;
import io.knotx.fragments.handler.api.domain.FragmentResult;
import io.knotx.fragments.handler.api.Cacheable;
import io.knotx.fragments.handler.api.Action;
import io.knotx.fragments.handler.api.ActionFactory;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
class ActionProviderTest {

  private static final String PROXY_FACTORY_NAME = "testFactory";
  private static final String PROXY_FACTORY_NAME_SECOND = "testFactorySecond";
  private static final String PROXY_ALIAS = "proxyAlias";
  private static final String PROXY_ALIAS_SECOND = "proxyAliasSecond";

  @Test
  @DisplayName("Expect no action when empty or null action alias defined.")
  void getWithNoAction(Vertx vertx) {
    // given
    ActionProvider tested = new ActionProvider(Collections.emptyMap(),
        Collections::emptyListIterator, "error", vertx);

    // when
    Optional<Action> operation = tested.get(null);

    // then
    assertFalse(operation.isPresent());
  }

  @Test
  @DisplayName("Expect no action when no action alias defined in configuration.")
  void getWithNoEntries(Vertx vertx) {
    // given
    ActionProvider tested = new ActionProvider(Collections.emptyMap(),
        Collections::emptyListIterator, "error", vertx);

    // when
    Optional<Action> operation = tested.get("any");

    // then
    assertFalse(operation.isPresent());
  }

  @Test
  @DisplayName("Expect no action when no factory found.")
  void getWithNoFactory(Vertx vertx) {
    // given
    Map<String, ActionOptions> proxies = Collections
        .singletonMap(PROXY_ALIAS, new ActionOptions("eb", new JsonObject(), null));

    ActionProvider tested = new ActionProvider(proxies,
        Collections::emptyListIterator, "error",
        vertx);

    // when
    Optional<Action> operation = tested.get(PROXY_ALIAS);

    // then
    assertFalse(operation.isPresent());
  }

  @Test
  @DisplayName("Expect action when action alias defined and factory found.")
  void getOperation(Vertx vertx) {
    // given
    Map<String, ActionOptions> proxies = Collections
        .singletonMap(PROXY_ALIAS,
            new ActionOptions(PROXY_FACTORY_NAME, new JsonObject(), null));
    List<ActionFactory> factories = Collections
        .singletonList(new TestCacheableOperationFactory());

    ActionProvider tested = new ActionProvider(proxies,
        factories::iterator, "error", vertx);

    // when
    Optional<Action> operation = tested.get(PROXY_ALIAS);

    // then
    assertTrue(operation.isPresent());
  }

  @Test
  @DisplayName("Expect new action every time we call non cacheable factory.")
  void getNewAction(Vertx vertx) {
    // given
    Map<String, ActionOptions> proxies = Collections
        .singletonMap(PROXY_ALIAS,
            new ActionOptions(PROXY_FACTORY_NAME, new JsonObject(), null));
    List<ActionFactory> factories = Collections
        .singletonList(new TestOperationFactory());

    ActionProvider tested = new ActionProvider(proxies, factories::iterator, "error", vertx);

    // when
    Optional<Action> firstOperation = tested.get(PROXY_ALIAS);
    Optional<Action> secondOperation = tested.get(PROXY_ALIAS);

    // then
    assertTrue(firstOperation.isPresent());
    assertTrue(secondOperation.isPresent());
    assertNotSame(firstOperation.get(), secondOperation.get());
  }

  @Test
  @DisplayName("Expect the same action every time we call cacheable factory.")
  void getCachedOperation(Vertx vertx) {
    // given
    Map<String, ActionOptions> proxies = Collections
        .singletonMap(PROXY_ALIAS,
            new ActionOptions(PROXY_FACTORY_NAME, new JsonObject(), null));
    List<ActionFactory> factories = Collections
        .singletonList(new TestCacheableOperationFactory());

    ActionProvider tested = new ActionProvider(proxies,
        factories::iterator, "error", vertx);

    // when
    Optional<Action> firstOperation = tested.get(PROXY_ALIAS);
    Optional<Action> secondOperation = tested.get(PROXY_ALIAS);

    // then
    assertTrue(firstOperation.isPresent());
    assertTrue(secondOperation.isPresent());
    assertSame(firstOperation.get(), secondOperation.get());
  }

  @Test
  @DisplayName("Expect not null action defined as doAction while creating action.")
  void getComplexOperation(Vertx vertx) {
    // given
    Action expectedOperation = Mockito.mock(Action.class);
    Action expectedOperationSecond = Mockito.mock(Action.class);

    ActionFactory proxyFactory = Mockito.mock(ActionFactory.class);
    when(proxyFactory.getName()).thenReturn(PROXY_FACTORY_NAME);
    when(proxyFactory
        .create(eq(PROXY_ALIAS), any(), eq(vertx), eq(expectedOperationSecond)))
        .thenReturn(expectedOperation);

    ActionFactory proxyFactorySecond = Mockito.mock(ActionFactory.class);
    when(proxyFactorySecond.getName()).thenReturn(PROXY_FACTORY_NAME_SECOND);
    when(proxyFactorySecond.create(eq(PROXY_ALIAS_SECOND), any(), eq(vertx), eq(null)))
        .thenReturn(expectedOperationSecond);

    Map<String, ActionOptions> proxies = ImmutableMap.of(
        PROXY_ALIAS,
        new ActionOptions(PROXY_FACTORY_NAME, new JsonObject(), PROXY_ALIAS_SECOND),
        PROXY_ALIAS_SECOND,
        new ActionOptions(PROXY_FACTORY_NAME_SECOND, new JsonObject())
    );
    List<ActionFactory> factories = Arrays.asList(proxyFactory, proxyFactorySecond);

    ActionProvider tested = new ActionProvider(proxies, factories::iterator, "error", vertx);

    // when
    tested.get(PROXY_ALIAS);

    // then
    Mockito.verify(proxyFactorySecond)
        .create(eq(PROXY_ALIAS_SECOND), any(), eq(vertx), eq(null));
    Mockito.verify(proxyFactory)
        .create(eq(PROXY_ALIAS), any(), eq(vertx), eq(expectedOperationSecond));
  }

  class TestOperationFactory implements ActionFactory {

    @Override
    public String getName() {
      return PROXY_FACTORY_NAME;
    }

    @Override
    public Action create(String alias, JsonObject config, Vertx vertx,
        Action doAction) {
      // do not change to lambda expression as it can be optimised by compiler
      return new Action() {
        @Override
        public void apply(FragmentContext fragmentContext,
            Handler<AsyncResult<FragmentResult>> resultHandler) {
          // empty
        }
      };
    }
  }

  @Cacheable
  class TestCacheableOperationFactory implements ActionFactory {

    @Override
    public String getName() {
      return PROXY_FACTORY_NAME;
    }

    @Override
    public Action create(String alias, JsonObject config, Vertx vertx,
        Action doAction) {
      // do not change to lambda expression as it can be optimised by compiler
      return new Action() {
        @Override
        public void apply(FragmentContext fragmentContext,
            Handler<AsyncResult<FragmentResult>> resultHandler) {
          // empty
        }
      };
    }
  }
}