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

import java.util.Map;

public class TasksMetadata {

  private final Map<String, TaskMetadata> tasksMetadataByFragmentId;

  public TasksMetadata(Map<String, TaskMetadata> tasksMetadataByFragmentId) {
    this.tasksMetadataByFragmentId = tasksMetadataByFragmentId;
  }

  public TaskMetadata get(String fragmentId) {
    return tasksMetadataByFragmentId.get(fragmentId);
  }

  @Override
  public String toString() {
    return "TasksMetadata{" +
        "tasksMetadataByFragmentId=" + tasksMetadataByFragmentId +
        '}';
  }
}
