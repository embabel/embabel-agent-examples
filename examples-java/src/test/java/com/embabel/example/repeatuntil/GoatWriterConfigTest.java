/*
 * Copyright 2024-2026 Embabel Pty Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.embabel.example.repeatuntil;

import com.embabel.agent.core.AgentPlatform;
import com.embabel.example.JavaAgentShellApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = JavaAgentShellApplication.class)
public class GoatWriterConfigTest {

    @Autowired
    private AgentPlatform agentPlatform;

    @Test
    public void testGoatWriterIsRegistered() throws Exception {
        boolean found = false;
        
        // Find getter for agents
        java.lang.reflect.Method getAgentsMethod = null;
        for (java.lang.reflect.Method m : agentPlatform.getClass().getMethods()) {
            if (m.getReturnType().equals(java.util.List.class) || m.getReturnType().equals(java.util.Collection.class)) {
                if (m.getName().toLowerCase().contains("agent")) {
                    getAgentsMethod = m;
                    break;
                }
            }
        }
        
        if (getAgentsMethod != null) {
            Iterable<?> agents = (Iterable<?>) getAgentsMethod.invoke(agentPlatform);
            for (Object agent : agents) {
                java.lang.reflect.Method nameMethod = agent.getClass().getMethod("getName");
                String name = (String) nameMethod.invoke(agent);
                if ("GoatWriter".equals(name)) {
                    found = true;
                    break;
                }
            }
        }
        
        assertTrue(found, "GoatWriter should be registered as an agent in the platform");
    }
}
