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
package io.knotx.fragments.handler.consumer.html.model;

import static io.knotx.fragments.engine.api.EventLogEntry.NodeStatus;
import static io.knotx.fragments.api.FragmentResult.ERROR_TRANSITION;
import static io.knotx.fragments.api.FragmentResult.SUCCESS_TRANSITION;

import io.knotx.fragments.engine.api.EventLogEntry;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public enum LoggedNodeStatus {
  SUCCESS {
    @Override
    protected boolean isEquivalent(NodeStatus status, String transition) {
      return SUCCESS_TRANSITION.equals(transition);
    }
  },

  ERROR {
    @Override
    protected boolean isEquivalent(NodeStatus status, String transition) {
      return ERROR_TRANSITION.equals(transition) || status == NodeStatus.TIMEOUT;
    }
  },

  OTHER {
    @Override
    protected boolean isEquivalent(NodeStatus status, String transition) {
      return StringUtils.isNotEmpty(transition) && !SUCCESS_TRANSITION.equals(transition)
          && !ERROR_TRANSITION.equals(transition) && status != NodeStatus.UNSUPPORTED_TRANSITION;
    }
  },

  MISSING {
    @Override
    protected boolean isEquivalent(NodeStatus status, String transition) {
      return false;
    }
  },

  UNPROCESSED {
    @Override
    protected boolean isEquivalent(NodeStatus status, String transition) {
      return status == NodeStatus.UNPROCESSED;
    }
  };

  public static LoggedNodeStatus from(EventLogEntry logEntry) {
    return Arrays.stream(LoggedNodeStatus.values())
        .filter(status -> status.isEquivalent(logEntry.getStatus(), logEntry.getTransition()))
        .findAny()
        .orElseThrow((() -> new IllegalArgumentException(
            String.format("Cannot find LoggedNodeStatus for NodeStatus=%s and transition=%s",
                logEntry.getStatus(), logEntry.getTransition()))));
  }

  protected abstract boolean isEquivalent(NodeStatus status, String transition);
}