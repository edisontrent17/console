package com.acme.data360agent.web;

import com.acme.data360agent.chat.ChatRequest;
import com.acme.data360agent.chat.ChatResponse;
import com.acme.data360agent.chat.ChatTraceEvent;
import com.acme.data360agent.chat.McpExplorationService;
import com.acme.data360agent.llm.LlmGateway;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final LlmGateway llm;
    private final McpExplorationService exploration;

    public ChatController(LlmGateway llm, McpExplorationService exploration) {
        this.llm = llm;
        this.exploration = exploration;
    }

    @PostMapping
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        var mode = request.mode() == null || request.mode().isBlank() ? "auto" : request.mode();
        var trace = new ArrayList<ChatTraceEvent>();
        trace.add(ChatTraceEvent.step("request", "Received chat request", "completed", "Mode: " + mode));
        var explored = exploration.answer(request.message());
        if (explored.handled()) {
            trace.addAll(explored.trace());
            return new ChatResponse(explored.text(), "mcp", "data360", trace);
        }
        trace.add(ChatTraceEvent.step("router", "No MCP exploration route matched", "completed", "Falling back to the configured LLM."));
        var system = """
                You are the Data 360 Agent Console assistant.
                Answer conversational and read-only exploration questions briefly and directly.
                If live read-only data is needed, say what you would inspect rather than inventing facts.
                If the user asks to set up, change, deploy, create, delete, activate, run, or monitor Salesforce Data 360, explain that you can draft a governed PlanSpec for approval.
                Do not claim that you have executed setup unless an execution run is actually started by the product.
                """;
        var started = Instant.now();
        var completion = llm.completeText(system, "Mode: " + mode + "\nUser: " + request.message());
        trace.add(ChatTraceEvent.timed("model", "Called configured LLM", "completed", "Generated conversational response.", Duration.between(started, Instant.now()).toMillis(), Map.of(
                "provider", completion.provider(),
                "model", completion.model()
        )));
        return new ChatResponse(completion.text(), completion.provider(), completion.model(), trace);
    }
}
