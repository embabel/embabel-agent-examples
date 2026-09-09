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
package com.embabel.example.injection;

import com.embabel.example.injection.travel.ActivitySummarizer;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

@Component
public record JokeShellCommands(
        InjectedComponent injectedComponent,
        ActivitySummarizer activitySummarizer
) {

    @Command(name = "string-joke", description = "Tell a joke")
    public String stringJoke(
            @Option(longName = "topic", description = "topic of the joke", defaultValue = "galahs") String topic
    ) {
        return injectedComponent.tellJokeAbout(topic);
    }

    @Command(name = "object-joke", description = "Create a joke object")
    public String objectJoke(
            @Option(longName = "topic1", description = "first topic", defaultValue = "dogs") String topic1,
            @Option(longName = "topic2", description = "second topic", defaultValue = "cats") String topic2,
            @Option(longName = "voice", description = "voice of the joke", defaultValue = "Shakespearean") String voice

    ) {
        var joke = injectedComponent.createJokeObjectAbout(topic1, topic2, voice);
        return joke.toString();
    }

    @Command(name = "travel-report", description = "Analyze travel report")
    public String travelReport(
    ) {
        var summary = activitySummarizer.summarizeActivity(1L);
        if (summary == null) {
            return "No customer found";
        }
        return summary.toString();
    }


}
