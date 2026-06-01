package com.acme.data360agent.plan;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Data360ActionTest {
    @Test
    void acceptsDottedAliasesForLegacyActionNames() {
        assertThat(Data360Action.from("data360.segment.create")).isEqualTo(Data360Action.CREATE_SEGMENT);
        assertThat(Data360Action.from("data360.segment.update")).isEqualTo(Data360Action.UPDATE_SEGMENT);
        assertThat(Data360Action.from("data360.segment.publish")).isEqualTo(Data360Action.PUBLISH_SEGMENT);
        assertThat(Data360Action.from("data360.activation.create")).isEqualTo(Data360Action.CREATE_ACTIVATION);
        assertThat(Data360Action.from("data360.activation.run")).isEqualTo(Data360Action.RUN_ACTIVATION);
    }

    @Test
    void keepsLegacyActionNamesAsCanonicalJsonValues() {
        assertThat(Data360Action.CREATE_SEGMENT.value()).isEqualTo("data360.createSegment");
        assertThat(Data360Action.PUBLISH_SEGMENT.value()).isEqualTo("data360.publishSegment");
        assertThat(Data360Action.CREATE_ACTIVATION.value()).isEqualTo("data360.createActivation");
    }
}
