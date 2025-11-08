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
package com.embabel.example.crew.bookwriter;

import com.embabel.agent.api.annotation.*;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.core.CoreToolGroups;
import com.embabel.agent.domain.library.ResearchReport;
import com.embabel.agent.prompt.persona.RoleGoalBackstory;
import com.embabel.agent.tools.file.FileTools;
import com.embabel.common.ai.model.LlmOptions;
import com.embabel.common.ai.prompt.PromptContributor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

record ChapterOutline(String title, String content) {
}

record BookOutline(String title,
                   List<ChapterOutline> chapterOutlines) implements PromptContributor {

    @NotNull
    @Override
    public String contribution() {
        return "Book Outline:\nTitle: " + title + "\n" + chapterOutlines.stream()
                .map(chapter -> chapter.title() + "\n" + chapter.content())
                .collect(Collectors.joining("\n\n"));
    }
}

record Chapter(String title, String content) {
}

record Book(BookRequest request,
            String title,
            List<Chapter> chapters) {

    public String text() {
        return "# " + title() + "\n" + request.goal() + "\n\n" +
                chapters().stream()
                        .map(chapter -> "## " + chapter.title() + "\n" + chapter.content())
                        .collect(Collectors.joining("\n\n"));
    }
}

record BookRequest(String topic, String goal, int wordsPerChapter) implements PromptContributor {
    @NotNull
    @Override
    public String contribution() {
        return """
                Book Request:
                Topic: %s
                Goal: %s
                Maximum words per chapter: %d
                """.formatted(topic, goal, wordsPerChapter);
    }
}

/**
 * All properties can be overridden in application.yml
 */
@EnableAsync
@ConfigurationProperties("examples.book-writer")
record BookWriterConfig(
        LlmOptions researcherLlm,
        LlmOptions writerLlm,
        int maxConcurrency,
        RoleGoalBackstory researcher,
        RoleGoalBackstory outliner,
        RoleGoalBackstory writer,
        String outputDirectory
) {

    public Path saveContent(Book book) {
        var dir = outputDirectory != null ? outputDirectory : System.getProperty("user.dir");
        var fileName = book.title()
                .replaceAll("[<>:\"|*?/\\\\]", "_")
                .replace(" ", "_")
                .toLowerCase() + ".md";
        return FileTools.readWrite(dir).createFile("books" + File.separator + fileName, book.text(), true);
    }
}


/**
 * Based on the Crew AI Book Writer example, this agent creates a book by first researching the topic,
 * then creating an outline, and finally writing the chapters.
 * See <a href="https://github.com/crewAIInc/crewAI-examples/tree/main/flows/write_a_book_with_flows">Write a book with flows</a>
 */
@Agent(description = "Write a book, first creating an outline, then writing the chapters and combining them")
public record BookWriter(BookWriterConfig config) {

    static final Logger logger = LoggerFactory.getLogger(BookWriter.class);

    public BookWriter {
        logger.info("Initialized with configuration {}", config);
    }

    @Action(cost = 100.0)
    BookRequest askForBookRequest(OperationContext context) {
        return WaitFor.formSubmission(
                "Please provide the topic, goal, and maximum words per chapter for the book you want to write:",
                BookRequest.class);
    }

    @Action
    ResearchReport researchTopic(
            BookRequest bookRequest,
            OperationContext context) {
        return context.ai()
                .withLlm(config.researcherLlm())
                .withPromptElements(config.researcher(), bookRequest)
                .withToolGroup(CoreToolGroups.WEB)
                .createObject(
                        """
                                Research the topic to gather the most important information that will
                                be useful in creating a book outline. Ensure you focus on high-quality, reliable sources,
                                and create a set of key points and important information that can be used to create a book outline.
                                """,
                        ResearchReport.class);
    }

    @Action
    BookOutline createOutline(
            BookRequest bookRequest,
            ResearchReport researchReport,
            OperationContext context) {
        return context.ai()
                .withLlm(config.writerLlm())
                .withPromptElements(config.outliner(), bookRequest, researchReport)
                .withToolGroup(CoreToolGroups.WEB)
                .createObject(
                        """
                                Create a book outline as requested with chapters in sequential order based on the given research findings.
                                Ensure that each chapter has a title and a brief description that highlights the topics and subtopics to be covered.
                                Ensure that you do not duplicate any chapters or topics in the outline.
                                """,
                        BookOutline.class);
    }

    @Action
    Book writeBook(
            BookRequest bookRequest,
            BookOutline bookOutline,
            ResearchReport researchReport,
            OperationContext context
    ) {
        var chapters = context.parallelMap(
                bookOutline.chapterOutlines(),
                config.maxConcurrency(),
                chapterOutline -> writeChapter(
                        bookRequest,
                        bookOutline,
                        chapterOutline,
                        context
                )
        );
        return new Book(bookRequest, bookOutline.title(), chapters);
    }

    @AchievesGoal(
            description = "Book has been written and published about the requested topic",
            export = @Export(remote = true)
    )
    @Action
    Book publishBook(Book book) {
        var path = config.saveContent(book);
        logger.info("Book {} written and saved to {}", book.title(), path);
        return book;
    }

    private Chapter writeChapter(
            BookRequest bookRequest,
            BookOutline bookOutline,
            ChapterOutline chapterOutline,
            OperationContext context) {
        logger.info("Researching chapter {}...", chapterOutline.title());
        var specificResearch = context.ai()
                .withLlm(config.researcherLlm())
                .withPromptElements(config.researcher(), bookRequest, bookOutline)
                .withToolGroup(CoreToolGroups.WEB)
                .createObject(
                        """
                                Research the topic of the chapter titled "%s" for the given book outline.
                                Consider the following chapter outline:
                                %s
                                
                                Ensure that you focus on high-quality, reliable sources,
                                and create a set of key points and important information
                                that can be used to write the chapter.
                                """.formatted(chapterOutline.title(), chapterOutline.content()),
                        ResearchReport.class);
        logger.info("Writing chapter {}...", chapterOutline.title());
        return context.ai()
                .withLlm(config.writerLlm())
                .withPromptElements(bookRequest, config.writer(), bookOutline, specificResearch)
                .createObject(
                        """
                                Write a well-structured chapter for the book based on the provided chapter title, goal, and outline.
                                The chapter should be written in markdown format.
                                Chapter title: %s
                                Chapter outline: %s
                                """.formatted(chapterOutline.title(), chapterOutline.content()
                        ),
                        Chapter.class
                );
    }
}
