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
                .containsExactly("schemaVersion", "id", "goal", "context", "definition");
        assertThat(schema.at("/$defs/stateMachine/properties/Version/const").asText()).isEqualTo(AslStateMachine.VERSION);
        assertThat(schema.at("/$defs/stateMachine/properties/QueryLanguage/const").asText()).isEqualTo(AslStateMachine.QUERY_LANGUAGE);
    }

    @Test
    void capabilityResourceEnumMatchesJavaModel() throws Exception {
        var schema = readSchema();

        var schemaResources = textValues(schema.at("/$defs/capabilityResource/enum"));
        var javaResources = Arrays.stream(Data360Action.values()).map(Data360Action::resource).toList();
        assertThat(schemaResources).containsExactlyElementsOf(javaResources);
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
