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
package io.knotx.fragments.task;

import static io.knotx.fragments.engine.graph.CompositeNode.COMPOSITE_NODE_ID;
import static io.knotx.fragments.handler.api.domain.FragmentResult.ERROR_TRANSITION;
import static io.knotx.fragments.handler.api.domain.FragmentResult.SUCCESS_TRANSITION;
import static io.knotx.fragments.handler.options.FragmentsHandlerOptions.DEFAULT_TASK_KEY;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import io.knotx.fragments.api.Fragment;
import io.knotx.fragments.engine.Task;
import io.knotx.fragments.engine.graph.ActionNode;
import io.knotx.fragments.engine.graph.CompositeNode;
import io.knotx.fragments.engine.graph.Node;
import io.knotx.fragments.handler.action.ActionProvider;
import io.knotx.fragments.handler.api.Action;
import io.knotx.fragments.handler.exception.GraphConfigurationException;
import io.knotx.fragments.handler.options.NodeOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DefaultTaskBuilderFactoryTest {

//  private static final Map<String, NodeOptions> NO_TRANSITIONS = Collections.emptyMap();
//
//  private static final String TASK_NAME = "task";
//  private static final Fragment FRAGMENT =
//      new Fragment("type",
//          new JsonObject().put(DEFAULT_TASK_KEY, TASK_NAME),
//          "initial body");
//
//  @Mock
//  private ActionProvider actionProvider;
//
//  @Mock
//  private Action actionMock;
//
//  @BeforeEach
//  void setUp() {
//    when(actionProvider.get("A")).thenReturn(Optional.of(actionMock));
//    when(actionProvider.get("B")).thenReturn(Optional.of(actionMock));
//  }
//
//  @Test
//  @DisplayName("Expect empty task when empty tasks.")
//  void expectNoTaskWhenEmptyTasks() {
//    // given
//    TaskBuilder tested = getTested(Collections.emptyMap());
//
//    // when
//    Optional<Task> task = tested.get(FRAGMENT);
//
//    // then
//    assertNotPresent(task);
//  }
//
//  @Test
//  @DisplayName("Expect configuration exception when action not defined.")
//  void expectExceptionWhenActionNotConfigured() {
//    // given
//    when(actionProvider.get(eq("nonExistingAction"))).thenReturn(Optional.empty());
//    TaskBuilder tested = getTested(singletonMap(TASK_NAME,
//        new NodeOptions("nonExistingAction", NO_TRANSITIONS)));
//
//    // when, then
//    assertThrows(GraphConfigurationException.class, () -> tested.get(FRAGMENT));
//  }
//
//  @ParameterizedTest
//  @ValueSource(strings = {DEFAULT_TASK_KEY, "custom-task-key"})
//  @DisplayName("Expect task name when task key.")
//  void expectTaskName(String taskKey) {
//    // given
//    TaskBuilder tested = new DefaultTaskBuilderFactory()
//        .create(taskKey, singletonMap(TASK_NAME, new NodeOptions("A", NO_TRANSITIONS)),
//            actionProvider);
//
//    // when
//    Optional<Task> task = tested.get(new Fragment("type",
//        new JsonObject().put(taskKey, TASK_NAME), "initial body"));
//
//    // then
//    assertTrue(task.isPresent());
//    assertEquals(TASK_NAME, task.get().getName());
//  }
//
//  @Test
//  @DisplayName("Expect Action node with no transitions: A")
//  void expectActionNodeWithNoTransitions() {
//    // given
//    TaskBuilder tested = getTested(
//        singletonMap(TASK_NAME, new NodeOptions("A", NO_TRANSITIONS)));
//
//    // when
//    Optional<Task> task = tested.get(FRAGMENT);
//
//    // then
//    assertTrue(task.isPresent());
//    assertTrue(task.get().getRootNode().isPresent());
//    Node aNode = task.get().getRootNode().get();
//    assertTrue(aNode instanceof ActionNode);
//    assertEquals("A", aNode.getId());
//    assertNotPresent(aNode.next(SUCCESS_TRANSITION));
//    assertNotPresent(aNode.next(ERROR_TRANSITION));
//  }
//
//  @Test
//  @DisplayName("Expect consequent node: A - _success -> B")
//  void expectActionNodeNextActionNode() {
//    // given
//    TaskBuilder tested = getTested(
//        singletonMap(TASK_NAME, new NodeOptions("A", singletonMap(SUCCESS_TRANSITION,
//            new NodeOptions("B", NO_TRANSITIONS)))));
//
//    // when
//    Optional<Task> task = tested.get(FRAGMENT);
//
//    // then
//    assertTrue(task.isPresent());
//    assertTrue(task.get().getRootNode().isPresent());
//    Node aNode = task.get().getRootNode().get();
//    Optional<Node> bNode = aNode.next(SUCCESS_TRANSITION);
//    assertTrue(bNode.isPresent());
//    assertEquals("B", bNode.get().getId());
//  }
//
//  @Test
//  @DisplayName("Expect Composite node with no transitions: [A]")
//  void expectCompositeNodeWithNoTransitions() {
//    // given
//    TaskBuilder tested = getTested(singletonMap(TASK_NAME,
//        new NodeOptions(
//            actions(new NodeOptions("A", NO_TRANSITIONS)),
//            NO_TRANSITIONS
//        ))
//    );
//
//    // when
//    Optional<Task> task = tested.get(FRAGMENT);
//
//    // then
//    assertTrue(task.isPresent());
//    assertTrue(task.get().getRootNode().isPresent());
//    Node compositeNode = task.get().getRootNode().get();
//    assertTrue(compositeNode instanceof CompositeNode);
//    assertEquals(COMPOSITE_NODE_ID, compositeNode.getId());
//    assertNotPresent(compositeNode.next(SUCCESS_TRANSITION));
//    assertNotPresent(compositeNode.next(ERROR_TRANSITION));
//
//    List<Node> compositeNodes = ((CompositeNode) compositeNode).getNodes();
//    assertEquals(1, compositeNodes.size());
//    assertEquals("A", compositeNodes.get(0).getId());
//    assertTrue(compositeNodes.get(0) instanceof ActionNode);
//  }
//
//  @Test
//  @DisplayName("Expect nested Composite node: [[A]]")
//  void expectNestedCompositeNodesGraph() {
//    // given
//    TaskBuilder tested = getTested(
//        singletonMap(TASK_NAME,
//            new NodeOptions(
//                actions(
//                    new NodeOptions(
//                        actions(
//                            new NodeOptions("A", NO_TRANSITIONS)),
//                        NO_TRANSITIONS)),
//                NO_TRANSITIONS
//            )));
//
//    // when
//    Optional<Task> task = tested.get(FRAGMENT);
//
//    // then
//    assertTrue(task.isPresent());
//    assertTrue(task.get().getRootNode().isPresent());
//    Node outerCompositeNode = task.get().getRootNode().get();
//    assertTrue(outerCompositeNode instanceof CompositeNode);
//
//    Node innerCompositeNode = ((CompositeNode) outerCompositeNode).getNodes().get(0);
//    assertTrue(innerCompositeNode instanceof CompositeNode);
//    Node aNode = ((CompositeNode) innerCompositeNode).getNodes().get(0);
//    assertEquals("A", aNode.getId());
//  }
//
//  @Test
//  @DisplayName("Expect consequent node (B): [A] - _success -> B.")
//  void expectCompositeNodeWithSuccessTransition() {
//    // given
//    TaskBuilder tested = getTested(
//        singletonMap(TASK_NAME,
//            new NodeOptions(
//                actions(new NodeOptions("A", NO_TRANSITIONS)),
//                singletonMap(SUCCESS_TRANSITION, new NodeOptions("B", NO_TRANSITIONS))
//            )));
//
//    // when
//    Optional<Task> task = tested.get(FRAGMENT);
//
//    // then
//    assertTrue(task.isPresent());
//    assertTrue(task.get().getRootNode().isPresent());
//    Node compositeNode = task.get().getRootNode().get();
//    Optional<Node> bNode = compositeNode.next(SUCCESS_TRANSITION);
//    assertTrue(bNode.isPresent());
//    assertEquals("B", bNode.get().getId());
//  }
//
//  @Test
//  @DisplayName("Expect _custom transition ignored for Composite node: [A] - _custom -> B.")
//  void expectCompositeNodeAcceptsOnlySuccessAndErrorTransitions() {
//    // given
//    when(actionProvider.get(eq("A"))).thenReturn(Optional.of(actionMock));
//    when(actionProvider.get(eq("B"))).thenReturn(Optional.of(actionMock));
//
//    TaskBuilder tested = getTested(
//        singletonMap(TASK_NAME,
//            new NodeOptions(
//                actions(new NodeOptions("A", NO_TRANSITIONS)),
//                singletonMap("_custom",
//                    new NodeOptions("B", NO_TRANSITIONS))
//            )));
//
//    // when
//    Optional<Task> task = tested.get(FRAGMENT);
//
//    // then
//    assertTrue(task.isPresent());
//    assertTrue(task.get().getRootNode().isPresent());
//    Node compositeNode = task.get().getRootNode().get();
//    assertNotPresent(compositeNode.next("_custom"));
//  }
//
//  @Test
//  @DisplayName("Expect _custom transition (B): [A - _custom -> B].")
//  void expectCompositeNodeWithActionNodeWithCustomTransition() {
//    // given
//    TaskBuilder tested = getTested(
//        singletonMap(TASK_NAME,
//            new NodeOptions(
//                actions(
//                    new NodeOptions("A", singletonMap("_custom",
//                        new NodeOptions("B", NO_TRANSITIONS)))),
//                NO_TRANSITIONS)
//        ));
//
//    // when
//    Optional<Task> task = tested.get(FRAGMENT);
//
//    // then
//    assertTrue(task.isPresent());
//    assertTrue(task.get().getRootNode().isPresent());
//    Node aNode = ((CompositeNode) task.get().getRootNode().get()).getNodes().get(0);
//    Optional<Node> bNode = aNode.next("_custom");
//    assertTrue(bNode.isPresent());
//    assertEquals("B", bNode.get().getId());
//  }
//
//
//  private TaskBuilder getTested(Map<String, NodeOptions> tasks) {
//    return new DefaultTaskBuilderFactory().create(DEFAULT_TASK_KEY, tasks, actionProvider);
//  }
//
//  private List<NodeOptions> actions(NodeOptions... nodes) {
//    return Arrays.asList(nodes);
//  }
//
//  private void assertNotPresent(
//      @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<?> any) {
//    assertFalse(any.isPresent());
//  }
}