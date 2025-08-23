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

