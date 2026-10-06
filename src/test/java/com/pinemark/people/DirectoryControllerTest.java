package com.pinemark.people;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DirectoryControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void directoryRequiresAuthentication() throws Exception {
        mvc.perform(get("/directory")).andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "r.okonkwo", roles = {"EMPLOYEE"})
    void employeeCannotReachAdminRecord() throws Exception {
        mvc.perform(get("/admin/employee/2")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "h.lindqvist", roles = {"EMPLOYEE", "HR_ADMIN"})
    void adminCanReachEmployeeRecord() throws Exception {
        mvc.perform(get("/admin/employee/2")).andExpect(status().isOk());
    }
}
