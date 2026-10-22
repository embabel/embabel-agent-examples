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
package com.embabel.example.horoscope;

import org.junit.jupiter.api.Test;
import com.embabel.agent.api.common.Ai;
import com.embabel.agent.api.common.PromptRunner;
import com.embabel.agent.api.tool.Tool;
import com.embabel.agent.core.ToolGroup;
import com.embabel.agent.core.ToolGroupResolution;
import com.embabel.agent.spi.ToolGroupResolver;
import com.embabel.common.ai.model.LlmOptions;
import tools.jackson.databind.json.JsonMapper;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class NewsFeedServiceTest {

    private static final String RSS = "<rss><channel><item><title>News</title><link>https://www.bbc.co.uk/news/original</link><description>Summary</description></item></channel></rss>";

    @Test
    void invokesResolvedMcpFetchAndReturnsOriginalStory() {
        var resolver = mock(ToolGroupResolver.class);
        var group = mock(ToolGroup.class);
        var fetch = mock(Tool.class);
        var definition = mock(Tool.Definition.class);
        when(definition.getName()).thenReturn("fetch");
        when(fetch.getDefinition()).thenReturn(definition);
        when(group.getTools()).thenReturn(List.of(fetch));
        when(resolver.findToolGroupForTool("fetch")).thenReturn(new ToolGroupResolution(group, null));
        when(fetch.call(anyString(), any())).thenReturn(Tool.Result.text(RSS));
        var ai = mock(Ai.class);
        var runner = mock(PromptRunner.class, RETURNS_SELF);
        when(ai.withLlm(any(LlmOptions.class))).thenReturn(runner);
        var registered = new AtomicReference<Tool>();
        doAnswer(invocation -> {
            registered.set(invocation.getArgument(0));
            return runner;
        }).when(runner).withTool(any(Tool.class));
        when(runner.generateText(anyString())).thenAnswer(invocation -> {
            assertTrue(registered.get().getMetadata().getReturnDirect());
            registered.get().call("{\"url\":\"https://feeds.bbci.co.uk/news/rss.xml\",\"raw\":true,\"max_length\":100000}");
            return "This model text must not replace the tool result";
        });
        var service = new NewsFeedService(URI.create("https://feeds.bbci.co.uk/news/rss.xml"), resolver, "test-model");

        var stories = service.latestStories(ai);

        assertEquals("https://www.bbc.co.uk/news/original", stories.getFirst().getUrl());
        verify(fetch, times(1)).call(anyString(), any());
    }

    @Test
    void refusesToUseModelTextWhenNoToolWasCalled() {
        var resolver = mock(ToolGroupResolver.class);
        var group = mock(ToolGroup.class);
        var fetch = mock(Tool.class);
        var definition = mock(Tool.Definition.class);
        when(definition.getName()).thenReturn("fetch");
        when(fetch.getDefinition()).thenReturn(definition);
        when(group.getTools()).thenReturn(List.of(fetch));
        when(resolver.findToolGroupForTool("fetch")).thenReturn(new ToolGroupResolution(group, null));
        var ai = mock(Ai.class);
        var runner = mock(PromptRunner.class, RETURNS_SELF);
        when(ai.withLlm(any(LlmOptions.class))).thenReturn(runner);
        when(runner.generateText(anyString())).thenReturn(RSS);
        var service = new NewsFeedService(URI.create("https://feeds.bbci.co.uk/news/rss.xml"), resolver, "test-model");
        assertThrows(IllegalStateException.class, () -> service.latestStories(ai));
        verify(fetch, never()).call(anyString(), any());
    }

    @Test
    void unwrapsMcpTextContentWithoutRewritingUrls() {
        var result = JsonMapper.builder().build().writeValueAsString(List.of(Map.of("text", "Raw content:\n" + RSS)));
        assertEquals("https://www.bbc.co.uk/news/original", NewsFeedService.parseToolResult(result).getFirst().getUrl());
    }

    @Test
    void rejectsTruncatedMcpFeed() {
        assertThrows(IllegalStateException.class, () -> NewsFeedService.parseToolResult("<rss><channel>"));
    }

    @Test
    void preservesFeedTitleDescriptionAndOriginalUrl() {
        var stories = NewsFeedService.parse("""
                <rss><channel><item>
                <title>Training &amp; careers</title>
                <link>https://www.bbc.co.uk/news/articles/original?source=rss&amp;x=1</link>
                <description><![CDATA[New training opportunities.]]></description>
                </item></channel></rss>
                """.getBytes(StandardCharsets.UTF_8));
        assertEquals(1, stories.size());
        assertEquals("Training & careers", stories.getFirst().getTitle());
        assertEquals("https://www.bbc.co.uk/news/articles/original?source=rss&x=1", stories.getFirst().getUrl());
        assertEquals("New training opportunities.", stories.getFirst().getSummary());
    }

    @Test
    void rejectsFeedWithoutUsableStories() {
        assertThrows(IllegalStateException.class, () -> NewsFeedService.parse(
                "<rss><channel/></rss>".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void rejectsExternalEntities() {
        assertThrows(IllegalStateException.class, () -> NewsFeedService.parse("""
                <!DOCTYPE rss [<!ENTITY external SYSTEM "file:///nonexistent">]>
                <rss><channel><item><title>&external;</title></item></channel></rss>
                """.getBytes(StandardCharsets.UTF_8)));
    }
}
