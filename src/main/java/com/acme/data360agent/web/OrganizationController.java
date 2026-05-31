package com.acme.data360agent.web;

import com.acme.data360agent.identity.IdentityRequests;
import com.acme.data360agent.identity.IdentityService;
import com.acme.data360agent.identity.Organization;
import com.acme.data360agent.identity.PublicUser;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrganizationController {
    private final IdentityService identities;

    public OrganizationController(IdentityService identities) {
        this.identities = identities;
    }

    @GetMapping("/organizations")
    public List<Organization> organizations() {
        return identities.organizations();
    }

    @PostMapping("/organizations")
    public Organization createOrganization(@Valid @RequestBody IdentityRequests.CreateOrganizationRequest request) {
        return identities.createOrganization(request.name());
    }

    @GetMapping("/organizations/{organizationId}/users")
    public List<PublicUser> users(@PathVariable String organizationId) {
        return identities.users(organizationId);
    }

    @PostMapping("/organizations/{organizationId}/users")
    public PublicUser createUser(@PathVariable String organizationId, @Valid @RequestBody IdentityRequests.CreateUserRequest request) {
        return identities.createUser(organizationId, request);
    }
}
