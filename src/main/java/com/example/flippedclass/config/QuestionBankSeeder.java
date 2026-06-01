package com.example.flippedclass.config;

import com.example.flippedclass.entity.QuestionBank;
import com.example.flippedclass.repository.QuestionBankRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
public class QuestionBankSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(QuestionBankSeeder.class);
    private final QuestionBankRepository questionBankRepository;

    public QuestionBankSeeder(QuestionBankRepository questionBankRepository) {
        this.questionBankRepository = questionBankRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (questionBankRepository.count() == 0) {
            logger.info("Question Bank is empty. Seeding default questions into the database...");

            List<QuestionBank> initialQuestions = Arrays.asList(
                QuestionBank.builder()
                        .content("What is the correct way to write a Java entry point method?")
                        .options("[\"public static void main(String[] args)\", \"public void main(String args)\", \"static void main(String[] args)\", \"public static int main(String[] args)\"]")
                        .correctAnswer("public static void main(String[] args)")
                        .questionType("SINGLE_CHOICE")
                        .points(10)
                        .explanation("A Java program requires a public static void main(String[] args) method to start execution.")
                        .build(),

                QuestionBank.builder()
                        .content("Which of the following are Spring Boot annotations?")
                        .options("[\"@RestController\", \"@SpringBootApplication\", \"@Component\", \"@ServletService\"]")
                        .correctAnswer("@SpringBootApplication") // In single choice context. If it's MULTIPLE_CHOICE this logic needs to match what UI expects
                        .questionType("MULTIPLE_CHOICE")
                        .points(15)
                        .explanation("@SpringBootApplication and @RestController are native Spring Boot annotations, whereas @Component is also a Spring stereotype.")
                        .build(),

                QuestionBank.builder()
                        .content("Is React a library or a framework?")
                        .options("[\"A library\", \"A framework\"]")
                        .correctAnswer("A library")
                        .questionType("SINGLE_CHOICE")
                        .points(5)
                        .explanation("React is officially described as a JavaScript library for building user interfaces.")
                        .build(),

                QuestionBank.builder()
                        .content("In Java, an interface can implement another interface.")
                        .options("[\"True\", \"False\"]")
                        .correctAnswer("False")
                        .questionType("TRUE_FALSE")
                        .points(5)
                        .explanation("An interface cannot IMPLEMENT another interface; it can only EXTEND another interface.")
                        .build(),

                QuestionBank.builder()
                        .content("Which hook is used to perform side effects in a React functional component?")
                        .options("[\"useState\", \"useEffect\", \"useContext\", \"useReducer\"]")
                        .correctAnswer("useEffect")
                        .questionType("SINGLE_CHOICE")
                        .points(10)
                        .explanation("useEffect is designed to sync components with external systems and perform side effects.")
                        .build()
            );

            questionBankRepository.saveAll(initialQuestions);
            logger.info("Successfully seeded {} questions into the Question Bank.", initialQuestions.size());
        } else {
            logger.info("Question Bank already contains data. Skipping seed.");
        }
    }
}
