package com.smartspend.smartspend.config;

import com.smartspend.smartspend.entity.Category;
import com.smartspend.smartspend.entity.CategoryType;
import com.smartspend.smartspend.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public DataSeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only insert if the table is completely empty
        if (categoryRepository.count() == 0) {
            Category food = new Category(); food.setCategoryName("Food & Dining"); food.setCategoryType(CategoryType.EXPENSE);
            Category transport = new Category(); transport.setCategoryName("Transport"); transport.setCategoryType(CategoryType.EXPENSE);
            Category rent = new Category(); rent.setCategoryName("Rent & Utilities"); rent.setCategoryType(CategoryType.EXPENSE);
            Category salary = new Category(); salary.setCategoryName("Salary"); salary.setCategoryType(CategoryType.INCOME);
            
            categoryRepository.saveAll(List.of(food, transport, rent, salary));
        }
    }
}