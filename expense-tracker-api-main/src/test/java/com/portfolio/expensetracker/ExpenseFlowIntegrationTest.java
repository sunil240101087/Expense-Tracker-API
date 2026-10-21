package com.portfolio.expensetracker;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * End-to-end tests covering registration, JWT-protected access, expense CRUD,
 * and cross-user data isolation.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExpenseFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String register(String email) throws Exception {
        String payload = """
                {"email":"%s","password":"Password123","displayName":"Test User"}
                """.formatted(email);

        String body = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(body).get("accessToken").asText();
    }

    @Test
    void protectedEndpointRejectsRequestsWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/expenses"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title", is("Unauthorized")));
    }

    @Test
    void registeredUserCanCreateAndReadOwnExpense() throws Exception {
        String token = register("alice@example.com");

        String expense = """
                {"description":"Lunch","amount":15.50,"category":"FOOD","spentOn":"2026-05-01"}
                """;

        mockMvc.perform(post("/api/v1/expenses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expense))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is("Lunch")))
                .andExpect(jsonPath("$.category", is("FOOD")));

        mockMvc.perform(get("/api/v1/expenses")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content[0].description", is("Lunch")));
    }

    @Test
    void usersCannotAccessEachOthersExpenses() throws Exception {
        String aliceToken = register("alice2@example.com");
        String bobToken = register("bob@example.com");

        String expense = """
                {"description":"Private","amount":99.99,"category":"OTHER","spentOn":"2026-05-02"}
                """;

        String created = mockMvc.perform(post("/api/v1/expenses")
                        .header("Authorization", "Bearer " + aliceToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expense))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long expenseId = objectMapper.readTree(created).get("id").asLong();

        // Bob must not be able to read Alice's expense.
        mockMvc.perform(get("/api/v1/expenses/{id}", expenseId)
                        .header("Authorization", "Bearer " + bobToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void validationErrorsReturn400WithFieldDetails() throws Exception {
        String token = register("carol@example.com");

        String invalid = """
                {"description":"","amount":-5,"category":"FOOD","spentOn":"2999-01-01"}
                """;

        mockMvc.perform(post("/api/v1/expenses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Validation Failed")));
    }

    @Test
    void regularUserCannotAccessAdminEndpoints() throws Exception {
        String token = register("dave@example.com");

        mockMvc.perform(get("/api/v1/admin/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void summaryReturnsCategoryTotals() throws Exception {
        String token = register("erin@example.com");

        mockMvc.perform(post("/api/v1/expenses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"Dinner","amount":40.00,"category":"FOOD","spentOn":"%s"}
                                """.formatted(java.time.LocalDate.now())))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/expenses/summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category", is("FOOD")))
                .andExpect(jsonPath("$[0].total", is(40.00)));
    }
}
