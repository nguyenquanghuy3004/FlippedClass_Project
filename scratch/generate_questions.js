const fs = require('fs');

const questions = [
  // Java
  ["What is the default value of a boolean variable in Java?", '["true", "false", "null", "0"]', "false"],
  ["Which concept of Java is achieved by combining methods and attribute into a class?", '["Encapsulation", "Inheritance", "Polymorphism", "Abstraction"]', "Encapsulation"],
  ["What is the size of int variable?", '["8 bit", "16 bit", "32 bit", "64 bit"]', "32 bit"],
  ["Which of these cannot be used for a variable name in Java?", '["identifier & keyword", "identifier", "keyword", "none of the mentioned"]', "keyword"],
  ["What is the extension of java code files?", '[".js", ".txt", ".class", ".java"]', ".java"],
  ["Which environment variable is used to set the java path?", '["MAVEN_PATH", "JavaPATH", "JAVA", "JAVA_HOME"]', "JAVA_HOME"],
  ["Which of these is not a feature of Java?", '["Object-oriented", "Use of pointers", "Portable", "Dynamic and Extensible"]', "Use of pointers"],
  ["What is the return type of the hashCode() method in the Object class?", '["int", "Object", "long", "void"]', "int"],
  ["Which keyword is used for accessing the features of a package?", '["import", "package", "extends", "export"]', "import"],
  ["In java, jar stands for?", '["Java Archive Runner", "Java Application Resource", "Java Application Runner", "Java Archive"]', "Java Archive"],

  // Spring Boot
  ["Which annotation is used to map web requests onto specific handler classes and/or handler methods?", '["@RequestMapping", "@GetMapping", "@PostMapping", "@Controller"]', "@RequestMapping"],
  ["What is the default port for Spring Boot applications?", '["8080", "8081", "3000", "80"]', "8080"],
  ["Which annotation is used to inject dependencies in Spring?", '["@Inject", "@Autowired", "@Resource", "All of the above"]', "All of the above"],
  ["What does IOC stand for in Spring?", '["Inversion of Control", "Injection of Control", "Interaction of Code", "Implementation of Classes"]', "Inversion of Control"],
  ["Which module of Spring Framework provides support for transactions?", '["Spring Core", "Spring MVC", "Spring TX", "Spring ORM"]', "Spring TX"],
  ["What is the use of @SpringBootApplication annotation?", '["It enables auto-configuration", "It enables component scanning", "It defines a configuration class", "All of the above"]', "All of the above"],
  ["Which embedded server does Spring Boot use by default?", '["Jetty", "Undertow", "Tomcat", "GlassFish"]', "Tomcat"],
  ["How can you override the default port in Spring Boot?", '["application.properties", "pom.xml", "application.yml", "Both application.properties and application.yml"]', "Both application.properties and application.yml"],
  ["Which dependency is required for creating RESTful web services in Spring Boot?", '["spring-boot-starter-web", "spring-boot-starter-data-jpa", "spring-boot-starter-test", "spring-boot-starter-security"]', "spring-boot-starter-web"],
  ["What does @RestController combine?", '["@Controller and @ResponseBody", "@Controller and @RequestMapping", "@Service and @ResponseBody", "@Component and @RequestMapping"]', "@Controller and @ResponseBody"],

  // React
  ["What is React mainly used for?", '["Database Management", "Server-side routing", "Building user interfaces", "Styling"]', "Building user interfaces"],
  ["Which of the following is used in React.js to increase performance?", '["Original DOM", "Virtual DOM", "Both", "None of above"]', "Virtual DOM"],
  ["What is a state in React?", '["A permanent storage", "An internal data store (object) of a component", "External data passed to components", "None of the above"]', "An internal data store (object) of a component"],
  ["How do you pass data from a parent component to a child component?", '["Using state", "Using props", "Using Context", "Using Redux"]', "Using props"],
  ["Which method is called after a component is rendered for the first time?", '["componentDidMount", "componentDidUpdate", "componentWillUnmount", "render"]', "componentDidMount"],
  ["What does JSX stand for?", '["JavaScript XML", "Java Syntax Extension", "JSON X", "JavaScript X"]', "JavaScript XML"],
  ["Which hook is used to manage state in a functional component?", '["useContext", "useEffect", "useState", "useReducer"]', "useState"],
  ["How can you prevent a component from rendering in React?", '["Return null", "Return false", "Return undefined", "None of the above"]', "Return null"],
  ["What is the purpose of the key prop in React lists?", '["To style the list items", "To provide a unique identifier for items to optimize rendering", "To bind data to inputs", "To pass data to children"]', "To provide a unique identifier for items to optimize rendering"],
  ["Which tool is commonly used to bundle React applications?", '["Webpack", "Gulp", "Grunt", "NPM"]', "Webpack"],

  // SQL / DB
  ["What does SQL stand for?", '["Structured Question Language", "Strong Question Language", "Structured Query Language", "Simple Query Language"]', "Structured Query Language"],
  ["Which SQL statement is used to update data in a database?", '["SAVE", "MODIFY", "UPDATE", "CHANGE"]', "UPDATE"],
  ["Which SQL statement is used to delete data from a database?", '["REMOVE", "DELETE", "COLLAPSE", "TRUNCATE"]', "DELETE"],
  ["Which SQL clause is used to filter records?", '["ORDER BY", "GROUP BY", "WHERE", "HAVING"]', "WHERE"],
  ["What does the COUNT() function do in SQL?", '["Adds values together", "Finds the average", "Returns the number of rows that matches a specified criterion", "Returns the highest value"]', "Returns the number of rows that matches a specified criterion"],
  ["Which keyword is used to sort the result-set?", '["SORT BY", "ORDER BY", "GROUP BY", "ALIGN BY"]', "ORDER BY"],
  ["Which SQL statement is used to insert new data in a database?", '["ADD RECORD", "INSERT INTO", "ADD NEW", "INSERT NEW"]', "INSERT INTO"],
  ["How do you select all columns from a table named 'Persons'?", '["SELECT [all] FROM Persons", "SELECT * FROM Persons", "SELECT Persons", "EXTRACT * FROM Persons"]', "SELECT * FROM Persons"],
  ["Which operator is used to search for a specified pattern in a column?", '["GET", "LIKE", "MATCH", "SEARCH"]', "LIKE"],
  ["What is the primary key constraint used for?", '["To identify uniquely each record in a database table", "To relate two tables", "To allow null values", "To sort records"]', "To identify uniquely each record in a database table"],

  // General CS
  ["What is the time complexity of binary search?", '["O(n)", "O(n log n)", "O(log n)", "O(1)"]', "O(log n)"],
  ["Which data structure uses LIFO (Last In First Out)?", '["Queue", "Stack", "Tree", "Graph"]', "Stack"],
  ["What does HTTP stand for?", '["Hyper Text Transfer Protocol", "Hyper Transfer Text Protocol", "High Text Transfer Protocol", "Hyperlink Text Transfer Protocol"]', "Hyper Text Transfer Protocol"],
  ["Which layer of the OSI model does TCP operate on?", '["Network Layer", "Transport Layer", "Application Layer", "Data Link Layer"]', "Transport Layer"],
  ["What is a deadlock?", '["A state where two or more processes are waiting indefinitely for an event that can be caused only by one of the waiting processes", "A process that is terminated", "A network error", "A type of database lock"]', "A state where two or more processes are waiting indefinitely for an event that can be caused only by one of the waiting processes"],
  ["Which algorithmic technique is used by the QuickSort algorithm?", '["Dynamic Programming", "Greedy", "Divide and Conquer", "Backtracking"]', "Divide and Conquer"],
  ["What is DNS used for?", '["To encrypt data", "To translate domain names to IP addresses", "To assign IP addresses", "To route packets"]', "To translate domain names to IP addresses"],
  ["Which port is generally used by HTTPS?", '["80", "443", "21", "22"]', "443"],
  ["What does JSON stand for?", '["JavaScript Object Notation", "Java Syntax Object Notation", "JavaScript Oriented Notation", "JavaScript Online Notation"]', "JavaScript Object Notation"],
  ["Which of the following is a NoSQL database?", '["MySQL", "PostgreSQL", "MongoDB", "Oracle"]', "MongoDB"]
];

let sql = "SET QUOTED_IDENTIFIER ON;\n";

questions.forEach((q) => {
  const content = q[0].replace(/'/g, "''");
  const options = q[1].replace(/'/g, "''");
  const correct = q[2].replace(/'/g, "''");
  
  sql += `INSERT INTO question_bank (content, options, correct_answer, points, question_type, explanation) VALUES ('${content}', '${options}', '${correct}', 10, 'SINGLE_CHOICE', 'Detailed explanation for: ${content}');\n`;
});

fs.writeFileSync('insert_questions.sql', sql);
console.log('Generated insert_questions.sql');
