package com.valanse.valanse.repository.jpa;

import com.valanse.valanse.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {

}
