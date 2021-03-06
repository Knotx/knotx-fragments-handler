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
package io.knotx.fragments.task.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doAnswer;

import io.knotx.fragments.api.Fragment;
import io.knotx.fragments.task.engine.FragmentEvent;
import io.knotx.fragments.task.engine.FragmentEvent.Status;
import io.knotx.fragments.task.handler.exception.ConfigurationException;
import io.knotx.fragments.task.handler.utils.RoutingContextStub;
import io.knotx.junit5.util.HoconLoader;
import io.knotx.server.api.context.ClientRequest;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FragmentsHandlerTest {

  private static final String EMPTY_BODY = "";

  @Test
  @DisplayName("Expect no exception when task factories and execution log consumers not configured.")
  void tasksAndConsumersNotFound(Vertx vertx)
      throws Throwable {
    HoconLoader.verify("handler/factoriesNotDefined.conf", config -> {
      //given
      new FragmentsHandler(vertx, config);
      // no exception
    }, vertx);
  }

  @Test
  @DisplayName("Expect exception when task factory name is not defined")
  void taskFactoryNameNotDefined(Vertx vertx)
      throws Throwable {
    HoconLoader.verify("handler/taskFactoryNameNotDefined.conf", config -> {
      //given
      try {
        new FragmentsHandler(vertx, config);
        fail("Should throw an exception!");
      } catch (ConfigurationException e) {
        // expected
      }
    }, vertx);
  }



  @Test
  @DisplayName("Expect processed fragment when factory accepts fragment.")
  void taskFactoryWithTaskEndingWithSuccess(Vertx vertx, VertxTestContext testContext)
      throws Throwable {
    HoconLoader.verifyAsync("handler/taskFactoryWithTaskEndingWithSuccess.conf", config -> {
      //given
      FragmentsHandler underTest = new FragmentsHandler(vertx, config);
      Fragment fragment = new Fragment("type", new JsonObject(), EMPTY_BODY);
      String expectedBody = "_success";

      //when
      Single<List<FragmentEvent>> rxDoHandle = underTest
          .doHandle(underTest
              .createExecutionPlan(Collections.singletonList(fragment), new ClientRequest()));

      rxDoHandle.subscribe(
          result -> testContext.verify(() -> {
            // then
            FragmentEvent fragmentEvent = result.get(0);
            assertEquals(Status.SUCCESS, fragmentEvent.getStatus());
            assertEquals(expectedBody, fragmentEvent.getFragment().getBody());
            testContext.completeNow();
          }),
          testContext::failNow
      );
    }, testContext, vertx);
  }

  @Test
  @DisplayName("Expect HTTP 500 when task ends with _error transition")
  void invalidFragmentsHeaderNotConfigured(Vertx vertx, VertxTestContext testContext)
      throws Throwable {
    HoconLoader.verifyAsync("handler/taskFactoryWithTaskEndingWithError.conf", config -> {
      //given
      RoutingContext routingContext = RoutingContextStub
          .create(emptyFragment(), Collections.emptyMap(), Collections.emptyMap());

      FragmentsHandler underTest = new FragmentsHandler(vertx, config);

      // when
      doAnswer(invocation -> {
        testContext.completeNow();
        return null;
      })
          .when(routingContext)
          .fail(500);

      underTest.handle(routingContext);

    }, testContext, vertx);
  }

  @Test
  @DisplayName("Expect unprocessed fragment when all factories do not accept fragment.")
  void noTaskFactoryAcceptsFragment(Vertx vertx, VertxTestContext testContext)
      throws Throwable {
    HoconLoader.verifyAsync("handler/noTaskFactoryAcceptsFragment.conf", config -> {
      //given
      FragmentsHandler underTest = new FragmentsHandler(vertx, config);
      Fragment fragment = new Fragment("type", new JsonObject(), EMPTY_BODY);

      //when
      Single<List<FragmentEvent>> rxDoHandle = underTest
          .doHandle(underTest
              .createExecutionPlan(Collections.singletonList(fragment), new ClientRequest()));

      rxDoHandle.subscribe(
          result -> testContext.verify(() -> {
            // then
            assertEquals(Status.UNPROCESSED, result.get(0).getStatus());
            testContext.completeNow();
          }),
          testContext::failNow
      );
    }, testContext, vertx);
  }

  @Test
  @DisplayName("Expect exception when consumer factory name is not defined")
  void consumerFactoryNameNotDefined(Vertx vertx)
      throws Throwable {
    HoconLoader.verify("handler/consumerFactoryNameNotDefined.conf", config -> {
      //given
      try {
        new FragmentsHandler(vertx, config);
        fail("Should throw an exception!");
      } catch (ConfigurationException e) {
        // expected
      }
    }, vertx);
  }

  @Test
  @DisplayName("Expect exception when task factory name is not found")
  void taskFactoryNotFound(Vertx vertx)
      throws Throwable {
    HoconLoader.verify("handler/taskFactoryNotFound.conf", config -> {
      //given
      try {
        new FragmentsHandler(vertx, config);
        fail("Should throw an exception!");
      } catch (ConfigurationException e) {
        assertTrue(e.getMessage().contains("invalid"));
      }
    }, vertx);
  }

  @Test
  @DisplayName("Expect exception when consumer factory name is not found")
  void consumerFactoryNotFound(Vertx vertx)
      throws Throwable {
    HoconLoader.verify("handler/consumerFactoryNotFound.conf", config -> {
      //given
      try {
        new FragmentsHandler(vertx, config);
        fail("Should throw an exception!");
      } catch (ConfigurationException e) {
        assertTrue(e.getMessage().contains("Consumer factory"));
      }
    }, vertx);
  }

  @Test
  @DisplayName("Expect changes in body when test consumer configured.")
  void consumerFactoryFound(Vertx vertx, VertxTestContext testContext)
      throws Throwable {
    HoconLoader.verifyAsync("handler/consumerFactoryFound.conf", config -> {
      //given
      Fragment fragment = new Fragment("snippet", new JsonObject(), EMPTY_BODY);
      RoutingContext routingContextMock = RoutingContextStub
          .create(fragment, Collections.emptyMap(), Collections.emptyMap());

      FragmentsHandler underTest = new FragmentsHandler(vertx, config);

      // when
      doAnswer(invocation -> {
        String key = invocation.getArgument(0);
        if ("fragments".equals(key)) {
          // then
          List<Fragment> fragmentList = invocation.getArgument(1);
          assertNotNull(fragmentList);
          assertEquals(1, fragmentList.size());
          String fragmentBody = fragmentList.get(0).getBody();
          assertTrue(fragmentBody.contains("testConsumer"));
          testContext.completeNow();
        }
        return routingContextMock;
      })
          .when(routingContextMock)
          .put(Mockito.any(), Mockito.any());

      // when
      underTest.handle(routingContextMock);
    }, testContext, vertx);
  }


  @Test
  @DisplayName("Expect invalid fragments to pass when the header is configured and provided")
  void invalidFragmentsHeaderProvided(Vertx vertx, VertxTestContext testContext) throws Throwable {
    HoconLoader.verifyAsync("handler/taskFactoryWithHeaderInvalidFragmentsAllowed.conf", config -> {
      //given
      Map<String, String> headers = Collections.singletonMap("Allow-Invalid-Fragments", "true");
      RoutingContext routingContext = RoutingContextStub.create(emptyFragment(), headers,
          Collections.emptyMap());
      doAnswer(invocation -> {
        // then
        testContext.completeNow();
        return routingContext;
      })
          .when(routingContext)
          .next();

      FragmentsHandler underTest = new FragmentsHandler(vertx, config);

      //when
      underTest.handle(routingContext);
    }, testContext, vertx);
  }


  @Test
  @DisplayName("Expect invalid fragments to pass when the parameter is configured and provided")
  void invalidFragmentsParamProvided(Vertx vertx, VertxTestContext testContext) throws Throwable {
    HoconLoader.verifyAsync("handler/taskFactoryWithParamInvalidFragmentsAllowed.conf", config -> {
      //given
      Map<String, String> params = Collections.singletonMap("allowInvalidFragments", "true");
      RoutingContext routingContext = RoutingContextStub
          .create(emptyFragment(), Collections.emptyMap(),
              params);
      doAnswer(invocation -> {
        // then
        testContext.completeNow();
        return routingContext;
      })
          .when(routingContext)
          .next();

      FragmentsHandler underTest = new FragmentsHandler(vertx, config);

      //when
      underTest.handle(routingContext);
    }, testContext, vertx);
  }

  private Fragment emptyFragment() {
    return new Fragment("type", new JsonObject(), "");
  }
}
