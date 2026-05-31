package com.acme.data360agent.plan;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

class PlanSpecSchemaTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void schemaVersionAndRequiredFieldsMatchPlanSpecContract() throws Exception {
        var schema = readSchema();

        assertThat(schema.path("$schema").asText()).isEqualTo("https://json-schema.org/draft/2020-12/schema");
        assertThat(schema.at("/properties/schemaVersion/const").asText()).isEqualTo(PlanSpec.CURRENT_SCHEMA_VERSION);
        assertThat(textValues(schema.path("required")))
                .containsExactly("schemaVersion", "id", "goal", "context", "steps");
    }

    @Test
    void actionAndPhaseEnumsMatchJavaModel() throws Exception {
        var schema = readSchema();

        var schemaActions = textValues(schema.at("/$defs/data360Action/enum"));
        var javaActions = Arrays.stream(Data360Action.values()).map(Data360Action::value).toList();
        assertThat(schemaActions).containsExactlyElementsOf(javaActions);

        var schemaPhases = textValues(schema.at("/$defs/phase/enum"));
        var javaPhases = Arrays.stream(PlanPhase.values()).map(PlanPhase::value).toList();
        assertThat(schemaPhases).containsExactlyElementsOf(javaPhases);
    }

    private static JsonNode readSchema() throws Exception {
        return OBJECT_MAPPER.readTree(Paths.get("schemas/planspec.schema.json").toFile());
    }

    private static List<String> textValues(JsonNode node) {
        return StreamSupport.stream(node.spliterator(), false)
                .map(JsonNode::asText)
                .toList();
    }
}
