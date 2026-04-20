package com.bintang.controller;

import com.bintang.entity.Employee;
import com.bintang.repository.EmployeeRepository;
import com.bintang.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam String nik,
            @RequestParam String password,
            HttpServletResponse response,
            Model model) {

        Optional<Employee> empOpt = employeeRepository.findByNik(nik);

        if (empOpt.isPresent() && password.equals(empOpt.get().getPassword())) {
            Employee emp = empOpt.get();
            String token = jwtUtil.generateToken(emp.getId(), emp.getNik(), emp.getRole());

            Cookie cookie = new Cookie("JWT_TOKEN", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 10); // 10 hours
            response.addCookie(cookie);

            return "redirect:/attendance";
        }

        model.addAttribute("error", "NIK atau Password salah");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/login";
    }
}
