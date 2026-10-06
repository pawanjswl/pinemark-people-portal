package com.pinemark.people.web;

import com.pinemark.people.domain.Employee;
import com.pinemark.people.domain.EmployeeRepository;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Controller
@Validated
public class DirectoryController {

    private static final int MAX_RESULTS = 50;

    private final EmployeeRepository employees;

    public DirectoryController(EmployeeRepository employees) {
        this.employees = employees;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * The search term is constrained at the edge as well as bound in the query.
     * Output is rendered through Thymeleaf's escaping text nodes, never th:utext.
     */
    @GetMapping("/directory")
    public String directory(
            @RequestParam(defaultValue = "")
            @Size(max = 64)
            @Pattern(regexp = "[\\p{L}\\p{Nd} .'-]*", message = "unsupported characters in search term")
            String q,
            Model model) {
        List<Employee> results = employees.search(q, MAX_RESULTS);
        model.addAttribute("query", q);
        model.addAttribute("results", results);
        return "directory";
    }

    /**
     * Authorization is on the server. The template never decides what a caller may
     * see, and the id in the path is checked against the role rather than trusted.
     */
    @GetMapping("/admin/employee/{id}")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public String employeeRecord(@PathVariable long id, Model model) {
        Employee employee = employees.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("employee", employee);
        return "employee";
    }
}
