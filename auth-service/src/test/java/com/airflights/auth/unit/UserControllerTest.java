package com.airflights.auth.unit;

import com.airflights.auth.controller.UserController;
import com.airflights.auth.dto.UserResponse;
import com.airflights.auth.entity.Role;
import com.airflights.auth.entity.User;
import com.airflights.auth.exception.RestExceptionHandler;
import com.airflights.auth.security.JwtAuthenticationFilter;
import com.airflights.auth.security.UserPrincipal;
import com.airflights.auth.service.UserService;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void create_returnsUserResponse() throws Exception {
        when(userService.create(any()))
                .thenReturn(new UserResponse(1L, "user1", "user1@example.com", Set.of(Role.PASSENGER)));

        String json = """
                {
                  "username": "user1",
                  "email": "user1@example.com",
                  "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.email").value("user1@example.com"));
    }

    @Test
    void create_whenInvalid_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void me_returnsCurrentUser() throws Exception {
        when(userService.getByUsername("user1"))
                .thenReturn(new UserResponse(2L, "user1", "user1@example.com", Set.of(Role.PASSENGER)));

        User user = new User();
        user.setId(2L);
        user.setUsername("user1");
        user.setEmail("user1@example.com");
        user.setPasswordHash("hashed");
        user.setRoles(Set.of(Role.PASSENGER));
        UserPrincipal principal = new UserPrincipal(user);
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        try {
            mockMvc.perform(get("/api/users/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("user1"));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
