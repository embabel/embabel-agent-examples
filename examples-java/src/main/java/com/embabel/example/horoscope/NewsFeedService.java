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

import com.embabel.agent.domain.library.NewsStory;
import com.embabel.agent.api.common.Ai;
import com.embabel.agent.api.tool.Tool;
import com.embabel.agent.api.tool.ToolCallContext;
import com.embabel.agent.spi.ToolGroupResolver;
import com.embabel.common.ai.model.LlmOptions;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class NewsFeedService {

    private final URI feedUrl;
    private final ToolGroupResolver toolGroupResolver;
    private final String defaultLlm;

    public NewsFeedService(@Value("${star-news-finder.feed-url:https://feeds.bbci.co.uk/news/rss.xml}") URI feedUrl,
                           ToolGroupResolver toolGroupResolver,
                           @Value("${embabel.models.default-llm}") String defaultLlm) {
        this.feedUrl = feedUrl;
        this.toolGroupResolver = toolGroupResolver;
        this.defaultLlm = defaultLlm;
    }

    public List<NewsStory> latestStories(Ai ai) {
        var fetch = resolveFetchTool();
        var rawFetch = fetch.getDefinition().getName().equals("fetch");
        var captured = new java.util.concurrent.atomic.AtomicReference<String>();
        var directFetch = new Tool() {
            @Override
            public Definition getDefinition() { return fetch.getDefinition(); }

            @Override
            public Metadata getMetadata() { return Metadata.create(true); }

            @Override
            public Result call(String input) { return call(input, ToolCallContext.EMPTY); }

            @Override
            public Result call(String input, ToolCallContext context) {
                var arguments = JsonMapper.builder().build().readTree(input);
                if (!feedUrl.toString().equals(arguments.path("url").asText())
                        || (rawFetch && !arguments.path("raw").asBoolean())
                        || arguments.path("start_index").asInt(0) != 0) {
                    throw new IllegalArgumentException("Fetch must retrieve the configured raw RSS feed from its beginning");
                }
                var result = fetch.call(input, context);
                if (result instanceof Result.Error) {
                    throw new IllegalStateException("MCP news feed retrieval failed: " + result);
                }
                captured.set(switch (result) {
                    case Result.Text text -> text.getContent();
                    case Result.WithArtifact artifact -> artifact.getContent();
                    default -> throw new IllegalStateException("Unexpected MCP fetch result");
                });
                return result;
            }
        };
        ai.withLlm(LlmOptions.withModel(defaultLlm).withTimeout(Duration.ofMinutes(3)))
                .withId("fetch_news_feed")
                .withTool(directFetch)
                .generateText("Call %s once with url=%s%s. Do not summarize or search."
                        .formatted(fetch.getDefinition().getName(), feedUrl,
                                rawFetch ? ", raw=true, max_length=100000" : ""));
        if (captured.get() == null) {
            throw new IllegalStateException("The model did not invoke the MCP fetch tool");
        }
        return parseToolResult(captured.get());
    }

    private Tool resolveFetchTool() {
        // Docker/uvx fetch and the Linux npx server use different names and schemas.
        // fetch_html returns the raw response body; fetch_txt/markdown discard RSS markup.
        for (var name : List.of("fetch", "fetch_html")) {
            var resolution = toolGroupResolver.findToolGroupForTool(name);
            if (resolution != null && resolution.getResolvedToolGroup() != null) {
                var tool = resolution.getResolvedToolGroup().getTools().stream()
                        .filter(candidate -> candidate.getDefinition().getName().equals(name)).findFirst();
                if (tool.isPresent()) {
                    return tool.get();
                }
            }
        }
        throw new IllegalStateException("MCP RSS retrieval requires fetch or fetch_html; neither tool is available");
    }

    static List<NewsStory> parseToolResult(String result) {
        var content = result;
        if (result.stripLeading().startsWith("[")) {
            var blocks = JsonMapper.builder().build().readTree(result);
            var text = new StringBuilder();
            for (var block : blocks) {
                if (block.has("text")) {
                    text.append(block.get("text").asText());
                }
            }
            content = text.toString();
        }
        var start = content.indexOf("<rss");
        var end = content.indexOf("</rss>", start);
        if (start < 0 || end < 0) {
            throw new IllegalStateException("MCP fetch did not return a complete RSS feed");
        }
        return parse(content.substring(start, end + "</rss>".length()).getBytes(StandardCharsets.UTF_8));
    }

    static List<NewsStory> parse(byte[] feed) {
        try {
            var factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            var document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(feed));
            var items = document.getElementsByTagName("item");
            var stories = new ArrayList<NewsStory>();
            for (int i = 0; i < items.getLength(); i++) {
                var item = (Element) items.item(i);
                var titles = item.getElementsByTagName("title");
                var links = item.getElementsByTagName("link");
                var descriptions = item.getElementsByTagName("description");
                if (titles.getLength() > 0 && links.getLength() > 0 && descriptions.getLength() > 0) {
                    var link = links.item(0).getTextContent().trim();
                    var scheme = URI.create(link).getScheme();
                    if ("https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme)) {
                        stories.add(new NewsStory(link, titles.item(0).getTextContent().trim(),
                                descriptions.item(0).getTextContent().trim()));
                    }
                }
            }
            if (stories.isEmpty()) {
                throw new IllegalStateException("News feed contains no usable stories");
            }
            return List.copyOf(stories);
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot parse news feed", e);
        }
    }
}
