/*
 * Copyright 2024-2025 Embabel Software, Inc.
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

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public record JokeShellCommands(
        InjectedComponent injectedComponent
) {

    @ShellMethod("Tell a joke")
    public String stringJoke(
            @ShellOption(value = "topic", help = "topic of the joke", defaultValue = "galahs") String topic
    ) {
        return injectedComponent.tellJokeAbout(topic);
    }

    @ShellMethod("Create a joke object")
    public String objectJoke(
            @ShellOption(value = "topic1", help = "first topic", defaultValue = "dogs") String topic1,
            @ShellOption(value = "topic2", help = "second topic", defaultValue = "cats") String topic2,
            @ShellOption(value = "voice", help = "voice of the joke", defaultValue = "Shakespearean") String voice

    ) {
        var joke = injectedComponent.createJokeObjectAbout(topic1, topic2, voice);
        return joke.toString();
    }


}

