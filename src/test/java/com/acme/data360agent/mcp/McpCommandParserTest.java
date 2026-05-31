package com.acme.data360agent.mcp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class McpCommandParserTest {
    @Test
    void parsesQuotedArgumentsAndEscapes() {
        var args = McpCommandParser.parse("java -jar \"/tmp/data 360/server.jar\" --label 'Data 360' --flag\\ value");

        assertThat(args).containsExactly(
                "java",
                "-jar",
                "/tmp/data 360/server.jar",
                "--label",
                "Data 360",
                "--flag value"
        );
    }

    @Test
    void rejectsUnclosedQuotes() {
        assertThatThrownBy(() -> McpCommandParser.parse("java -jar \"server.jar"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unclosed quote");
    }
}
