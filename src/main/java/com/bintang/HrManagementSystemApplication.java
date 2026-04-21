package com.bintang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.bintang.repository.AppMenuRepository;
import com.bintang.entity.AppMenu;

@SpringBootApplication
public class HrManagementSystemApplication {

    @Bean
    public CommandLineRunner initData(AppMenuRepository menuRepository) {
        return args -> {
            // Seed Dashboard Menu if not exists
            if (menuRepository.findByUrl("/dashboard") == null) {
                AppMenu dashboard = new AppMenu();
                dashboard.setTitle("Dashboard");
                dashboard.setUrl("/dashboard");
                dashboard.setIcon("fas fa-th-large");
                dashboard.setRoleRequired("ADMIN");
                dashboard.setSortOrder(0);
                dashboard.setIsActive(true);
                menuRepository.save(dashboard);
                System.out.println("SEEDER: Dashboard menu created");
            }
        };
    }

	public static void main(String[] args) {
		SpringApplication.run(HrManagementSystemApplication.class, args);
		System.out.println("SERVER IS RUNNING");
	}
}
