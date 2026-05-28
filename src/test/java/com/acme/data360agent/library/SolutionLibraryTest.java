package com.acme.data360agent.library;

import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanValidator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SolutionLibraryTest {
    private final SolutionLibrary library = new SolutionLibrary();
    private final PlanValidator validator = new PlanValidator(new OperationRegistry());

    @Test
    void includesComplexCrossCloudTemplates() {
        assertThat(library.all()).hasSizeGreaterThanOrEqualTo(8);
        assertThat(library.all())
                .anyMatch(template -> template.clouds().contains("Marketing Cloud"))
                .anyMatch(template -> template.clouds().contains("Service Cloud"))
                .anyMatch(template -> template.clouds().contains("Sales Cloud"));
    }

    @Test
    void everyTemplateInstantiatesAValidPlan() {
        var context = new PlanContext("sandbox-org", "default", "sandbox");

        for (var template : library.all()) {
            var plan = library.instantiate(template.id(), context);
            var validation = validator.validate(plan);

            assertThat(validation.ok())
                    .as(template.id() + " should validate: " + validation.issues())
                    .isTrue();
        }
    }
}
