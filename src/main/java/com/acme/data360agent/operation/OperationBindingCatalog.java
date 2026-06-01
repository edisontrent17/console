package com.acme.data360agent.operation;

import com.acme.data360agent.plan.PlanSpec;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OperationBindingCatalog {
    private final OperationRegistry registry;

    public OperationBindingCatalog(OperationRegistry registry) {
        this.registry = registry;
    }

    public List<OperationBindingSnapshot> snapshotsFor(PlanSpec plan) {
        return OperationBindingResolver.snapshotsFor(registry, plan);
    }
}
