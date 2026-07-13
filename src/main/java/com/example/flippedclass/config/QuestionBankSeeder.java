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
                        .category("Java Core")
                        .build(),

                QuestionBank.builder()
                        .content("Which of the following are Spring Boot annotations?")
                        .options("[\"@RestController\", \"@SpringBootApplication\", \"@Component\", \"@ServletService\"]")
                        .correctAnswer("@SpringBootApplication") // In single choice context. If it's MULTIPLE_CHOICE this logic needs to match what UI expects
                        .questionType("MULTIPLE_CHOICE")
                        .points(15)
                        .explanation("@SpringBootApplication and @RestController are native Spring Boot annotations, whereas @Component is also a Spring stereotype.")
                        .category("Spring Boot")
                        .build(),

                QuestionBank.builder()
                        .content("Is React a library or a framework?")
                        .options("[\"A library\", \"A framework\"]")
                        .correctAnswer("A library")
                        .questionType("SINGLE_CHOICE")
                        .points(5)
                        .explanation("React is officially described as a JavaScript library for building user interfaces.")
                        .category("React JS")
                        .build(),

                QuestionBank.builder()
                        .content("In Java, an interface can implement another interface.")
                        .options("[\"True\", \"False\"]")
                        .correctAnswer("False")
                        .questionType("TRUE_FALSE")
                        .points(5)
                        .explanation("An interface cannot IMPLEMENT another interface; it can only EXTEND another interface.")
                        .category("Java Core")
                        .build(),

                QuestionBank.builder()
                        .content("Which hook is used to perform side effects in a React functional component?")
                        .options("[\"useState\", \"useEffect\", \"useContext\", \"useReducer\"]")
                        .correctAnswer("useEffect")
                        .questionType("SINGLE_CHOICE")
                        .points(10)
                        .explanation("useEffect is designed to sync components with external systems and perform side effects.")
                        .category("React JS")
                        .build()
            );

            questionBankRepository.saveAll(initialQuestions);
            logger.info("Successfully seeded {} questions into the Question Bank.", initialQuestions.size());
        } else {
            logger.info("Question Bank already contains data. Skipping seed.");
        }

        // Auto-categorize existing questions that are currently 'General' or null
        List<QuestionBank> allQuestions = questionBankRepository.findAll();
        boolean updated = false;
        for (QuestionBank qb : allQuestions) {
            if (qb.getCategory() == null || qb.getCategory().equals("General")) {
                String content = qb.getContent().toLowerCase();
                String category = "General";
                
                if (content.contains("java") || content.contains("jvm") || content.contains("class") || content.contains("boolean") || content.contains("int variable") || content.contains("path")) {
                    category = "Java Core";
                } else if (content.contains("spring") || content.contains("bean") || content.contains("annotation") || content.contains("restcontroller")) {
                    category = "Spring Boot";
                } else if (content.contains("react") || content.contains("hook") || content.contains("useeffect") || content.contains("component") || content.contains("library")) {
                    category = "React JS";
                } else if (content.contains("tcp") || content.contains("osi") || content.contains("dns") || content.contains("port") || content.contains("https") || content.contains("network")) {
                    category = "Networking";
                } else if (content.contains("deadlock") || content.contains("thread") || content.contains("process")) {
                    category = "Operating System";
                } else if (content.contains("quicksort") || content.contains("algorithm") || content.contains("sort")) {
                    category = "Algorithms";
                } else if (content.contains("database") || content.contains("sql") || content.contains("nosql")) {
                    category = "Database";
                }
                
                if (!category.equals("General")) {
                    qb.setCategory(category);
                    updated = true;
                }
            }
        }
        if (updated) {
            questionBankRepository.saveAll(allQuestions);
            logger.info("Automatically categorized existing questions in the Question Bank.");
        }
    }
}
