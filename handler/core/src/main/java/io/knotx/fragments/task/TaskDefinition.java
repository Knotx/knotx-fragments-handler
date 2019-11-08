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
package io.knotx.fragments.task;

import io.knotx.fragments.task.options.GraphNodeOptions;

public class TaskDefinition {

  private final String taskName;

  private final GraphNodeOptions graphNodeOptions;

  public TaskDefinition(String taskName, GraphNodeOptions graphNodeOptions) {
    this.taskName = taskName;
    this.graphNodeOptions = graphNodeOptions;
  }

  public String getTaskName() {
    return taskName;
  }

  public GraphNodeOptions getGraphNodeOptions() {
    return graphNodeOptions;
  }

  @Override
  public String toString() {
    return "TaskDefinition{" +
        "taskName='" + taskName + '\'' +
        ", graphNodeOptions=" + graphNodeOptions +
        '}';
  }
}
