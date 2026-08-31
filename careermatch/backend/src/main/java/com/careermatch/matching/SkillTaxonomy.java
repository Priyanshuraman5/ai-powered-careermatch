package com.careermatch.matching;

import java.util.List;

public final class SkillTaxonomy {

    private SkillTaxonomy() {
    }

    public static List<SkillDefinition> all() {

        return List.of(

            // =========================================================
            // PROGRAMMING LANGUAGES
            // =========================================================

            skill("java", "Programming Language",
                    "java"),

            skill("python", "Programming Language",
                    "python", "python3", "python 3"),

            skill("javascript", "Programming Language",
                    "javascript", "java script", "js", "ecmascript"),

            skill("typescript", "Programming Language",
                    "typescript", "type script", "ts"),

            skill("c", "Programming Language",
                    "c programming", "c language"),

            skill("c++", "Programming Language",
                    "c++", "cpp", "cplusplus"),

            skill("c#", "Programming Language",
                    "c#", "csharp", "c sharp"),

            skill("go", "Programming Language",
                    "golang", "go language"),

            skill("rust", "Programming Language",
                    "rust"),

            skill("kotlin", "Programming Language",
                    "kotlin"),

            skill("swift", "Programming Language",
                    "swift"),

            skill("dart", "Programming Language",
                    "dart"),

            skill("php", "Programming Language",
                    "php"),

            skill("ruby", "Programming Language",
                    "ruby"),

            skill("scala", "Programming Language",
                    "scala"),

            skill("r", "Programming Language",
                    "r programming", "r language"),

            skill("matlab", "Programming Language",
                    "matlab"),

            skill("perl", "Programming Language",
                    "perl"),

            skill("lua", "Programming Language",
                    "lua"),

            skill("groovy", "Programming Language",
                    "groovy"),

            skill("bash", "Programming Language",
                    "bash", "bash scripting"),

            skill("powershell", "Programming Language",
                    "powershell", "power shell"),

            // =========================================================
            // JAVA / SPRING
            // =========================================================

            skill("spring", "Framework",
                    "spring framework"),

            skill("spring boot", "Framework",
                    "spring boot", "springboot", "spring-boot"),

            skill("spring mvc", "Framework",
                    "spring mvc"),

            skill("spring security", "Security",
                    "spring security"),

            skill("spring data jpa", "Framework",
                    "spring data jpa"),

            skill("spring cloud", "Framework",
                    "spring cloud"),

            skill("spring batch", "Framework",
                    "spring batch"),

            skill("hibernate", "ORM",
                    "hibernate"),

            skill("jpa", "ORM",
                    "jpa", "java persistence api"),

            skill("jdbc", "Database",
                    "jdbc", "java database connectivity"),

            skill("servlet", "Java",
                    "servlet", "java servlet"),

            skill("jsp", "Java",
                    "jsp", "java server pages"),

            skill("thymeleaf", "Framework",
                    "thymeleaf"),

            skill("webflux", "Framework",
                    "spring webflux", "webflux"),

            skill("maven", "Build Tool",
                    "maven", "apache maven"),

            skill("gradle", "Build Tool",
                    "gradle"),

            skill("lombok", "Java",
                    "lombok"),

            // =========================================================
            // WEB
            // =========================================================

            skill("html", "Web",
                    "html", "html5"),

            skill("css", "Web",
                    "css", "css3"),

            skill("rest api", "API",
                    "rest api", "restful api", "rest services", "rest"),

            skill("graphql", "API",
                    "graphql"),

            skill("websocket", "Web",
                    "websocket", "web sockets"),

            skill("json", "Data Format",
                    "json"),

            skill("xml", "Data Format",
                    "xml"),

            skill("ajax", "Web",
                    "ajax"),

            skill("cors", "Web Security",
                    "cors", "cross origin resource sharing"),

            // =========================================================
            // FRONTEND
            // =========================================================

            skill("react", "Frontend Framework",
                    "react", "reactjs", "react.js"),

            skill("next.js", "Frontend Framework",
                    "next.js", "nextjs", "next js"),

            skill("angular", "Frontend Framework",
                    "angular", "angularjs"),

            skill("vue", "Frontend Framework",
                    "vue", "vue.js", "vuejs"),

            skill("svelte", "Frontend Framework",
                    "svelte"),

            skill("redux", "Frontend Library",
                    "redux"),

            skill("redux toolkit", "Frontend Library",
                    "redux toolkit", "rtk"),

            skill("react router", "Frontend Library",
                    "react router", "react-router"),

            skill("jquery", "Frontend Library",
                    "jquery"),

            skill("bootstrap", "CSS Framework",
                    "bootstrap"),

            skill("tailwind css", "CSS Framework",
                    "tailwind", "tailwind css"),

            skill("material ui", "UI Library",
                    "material ui", "mui"),

            skill("vite", "Build Tool",
                    "vite"),

            skill("webpack", "Build Tool",
                    "webpack"),

            skill("babel", "Build Tool",
                    "babel"),

            // =========================================================
            // NODE / BACKEND
            // =========================================================

            skill("node.js", "Backend",
                    "node.js", "nodejs", "node js"),

            skill("express.js", "Backend Framework",
                    "express", "express.js", "expressjs"),

            skill("nestjs", "Backend Framework",
                    "nestjs", "nest.js"),

            skill("django", "Backend Framework",
                    "django"),

            skill("flask", "Backend Framework",
                    "flask"),

            skill("fastapi", "Backend Framework",
                    "fastapi", "fast api"),

            skill("asp.net", "Backend Framework",
                    "asp.net", "aspnet"),

            skill("asp.net core", "Backend Framework",
                    "asp.net core"),

            skill("laravel", "Backend Framework",
                    "laravel"),

            skill("ruby on rails", "Backend Framework",
                    "ruby on rails", "rails"),

            // =========================================================
            // DATABASES
            // =========================================================

            skill("sql", "Database",
                    "sql", "structured query language"),

            skill("mysql", "Database",
                    "mysql", "my sql"),

            skill("postgresql", "Database",
                    "postgresql", "postgres", "postgres sql", "postgre sql"),

            skill("oracle", "Database",
                    "oracle database", "oracle db"),

            skill("sql server", "Database",
                    "sql server", "microsoft sql server", "mssql"),

            skill("sqlite", "Database",
                    "sqlite"),

            skill("mongodb", "Database",
                    "mongodb", "mongo db", "mongo"),

            skill("redis", "Database",
                    "redis"),

            skill("cassandra", "Database",
                    "cassandra"),

            skill("mariadb", "Database",
                    "mariadb", "maria db"),

            skill("dynamodb", "Database",
                    "dynamodb", "dynamo db"),

            skill("firebase", "Database",
                    "firebase"),

            skill("neo4j", "Database",
                    "neo4j"),

            skill("elasticsearch", "Database",
                    "elasticsearch", "elastic search"),

            // =========================================================
            // DATABASE TECHNOLOGIES
            // =========================================================

            skill("pl/sql", "Database",
                    "pl/sql", "plsql"),

            skill("t-sql", "Database",
                    "t-sql", "tsql"),

            skill("mybatis", "ORM",
                    "mybatis"),

            skill("entity framework", "ORM",
                    "entity framework"),

            skill("orm", "Database",
                    "orm", "object relational mapping"),

            skill("database design", "Database",
                    "database design"),

            skill("database optimization", "Database",
                    "database optimization", "database performance"),

            skill("database indexing", "Database",
                    "database indexing", "indexing"),

            skill("database transactions", "Database",
                    "database transactions", "transactions"),

            skill("stored procedures", "Database",
                    "stored procedures", "stored procedure"),

            skill("database normalization", "Database",
                    "database normalization", "normalization"),

            // =========================================================
            // DEVOPS
            // =========================================================

            skill("git", "Version Control",
                    "git"),

            skill("github", "Version Control",
                    "github", "git hub"),

            skill("gitlab", "Version Control",
                    "gitlab", "git lab"),

            skill("bitbucket", "Version Control",
                    "bitbucket"),

            skill("docker", "DevOps",
                    "docker", "docker container", "docker containers"),

            skill("kubernetes", "DevOps",
                    "kubernetes", "k8s"),

            skill("jenkins", "CI/CD",
                    "jenkins"),

            skill("github actions", "CI/CD",
                    "github actions"),

            skill("gitlab ci", "CI/CD",
                    "gitlab ci", "gitlab ci/cd"),

            skill("ci/cd", "DevOps",
                    "ci/cd", "cicd", "continuous integration", "continuous deployment"),

            skill("terraform", "Infrastructure",
                    "terraform"),

            skill("ansible", "DevOps",
                    "ansible"),

            skill("nginx", "Web Server",
                    "nginx"),

            skill("apache", "Web Server",
                    "apache", "apache http server"),

            skill("linux", "Operating System",
                    "linux"),

            // =========================================================
            // CLOUD
            // =========================================================

            skill("aws", "Cloud",
                    "aws", "amazon web services"),

            skill("amazon ec2", "Cloud",
                    "ec2", "amazon ec2"),

            skill("amazon s3", "Cloud",
                    "s3", "amazon s3"),

            skill("amazon rds", "Cloud",
                    "rds", "amazon rds"),

            skill("aws lambda", "Cloud",
                    "lambda", "aws lambda"),

            skill("amazon ecs", "Cloud",
                    "ecs", "amazon ecs"),

            skill("amazon eks", "Cloud",
                    "eks", "amazon eks"),

            skill("cloudfront", "Cloud",
                    "cloudfront", "amazon cloudfront"),

            skill("azure", "Cloud",
                    "azure", "microsoft azure"),

            skill("azure devops", "Cloud",
                    "azure devops"),

            skill("google cloud", "Cloud",
                    "google cloud", "gcp", "google cloud platform"),

            skill("google cloud run", "Cloud",
                    "cloud run", "google cloud run"),

            // =========================================================
            // AI / ML
            // =========================================================

            skill("artificial intelligence", "AI",
                    "artificial intelligence", "ai"),

            skill("machine learning", "AI/ML",
                    "machine learning", "machine-learning", "ml"),

            skill("deep learning", "AI/ML",
                    "deep learning", "deep-learning"),

            skill("natural language processing", "NLP",
                    "natural language processing", "nlp"),

            skill("generative ai", "AI",
                    "generative ai", "gen ai", "genai"),

            skill("large language models", "AI",
                    "large language models", "large language model", "llm", "llms"),

            skill("computer vision", "AI/ML",
                    "computer vision", "cv"),

            skill("neural networks", "AI/ML",
                    "neural networks", "neural network"),

            skill("transformers", "AI/ML",
                    "transformers", "transformer architecture"),

            skill("bert", "NLP",
                    "bert"),

            skill("gpt", "AI",
                    "gpt", "gpt models"),

            skill("rag", "AI",
                    "rag", "retrieval augmented generation"),

            skill("embeddings", "AI",
                    "embeddings", "vector embeddings"),

            skill("semantic search", "AI",
                    "semantic search"),

            skill("semantic similarity", "NLP",
                    "semantic similarity"),

            skill("prompt engineering", "AI",
                    "prompt engineering"),

            skill("fine tuning", "AI",
                    "fine tuning", "fine-tuning"),

            skill("scikit-learn", "Machine Learning",
                    "scikit-learn", "sklearn", "scikit learn"),

            skill("tensorflow", "Machine Learning",
                    "tensorflow"),

            skill("pytorch", "Machine Learning",
                    "pytorch", "py torch"),

            skill("keras", "Machine Learning",
                    "keras"),

            skill("pandas", "Data Science",
                    "pandas"),

            skill("numpy", "Data Science",
                    "numpy", "num py"),

            skill("scipy", "Data Science",
                    "scipy"),

            skill("matplotlib", "Data Science",
                    "matplotlib"),

            // =========================================================
            // NLP
            // =========================================================

            skill("text classification", "NLP",
                    "text classification"),

            skill("text extraction", "NLP",
                    "text extraction"),

            skill("named entity recognition", "NLP",
                    "named entity recognition", "ner"),

            skill("tokenization", "NLP",
                    "tokenization", "tokenisation"),

            skill("lemmatization", "NLP",
                    "lemmatization", "lemmatisation"),

            skill("stemming", "NLP",
                    "stemming"),

            skill("tf-idf", "NLP",
                    "tf-idf", "tfidf", "term frequency inverse document frequency"),

            skill("cosine similarity", "NLP",
                    "cosine similarity"),

            skill("word embeddings", "NLP",
                    "word embeddings"),

            skill("sentence embeddings", "NLP",
                    "sentence embeddings"),

            skill("keyword extraction", "NLP",
                    "keyword extraction"),

            skill("entity extraction", "NLP",
                    "entity extraction"),

            skill("information extraction", "NLP",
                    "information extraction"),

            skill("spacy", "NLP",
                    "spacy", "spaCy"),

            skill("nltk", "NLP",
                    "nltk", "natural language toolkit"),

            skill("hugging face", "AI/NLP",
                    "hugging face", "huggingface"),

            // =========================================================
            // TESTING
            // =========================================================

            skill("junit", "Testing",
                    "junit", "junit 4", "junit 5"),

            skill("mockito", "Testing",
                    "mockito"),

            skill("testng", "Testing",
                    "testng", "test ng"),

            skill("selenium", "Testing",
                    "selenium"),

            skill("cypress", "Testing",
                    "cypress"),

            skill("playwright", "Testing",
                    "playwright"),

            skill("postman", "API Testing",
                    "postman"),

            skill("rest assured", "API Testing",
                    "rest assured", "rest-assured"),

            skill("jmeter", "Performance Testing",
                    "jmeter", "apache jmeter"),

            skill("unit testing", "Testing",
                    "unit testing", "unit test"),

            skill("integration testing", "Testing",
                    "integration testing", "integration test"),

            skill("api testing", "Testing",
                    "api testing"),

            skill("end to end testing", "Testing",
                    "end to end testing", "end-to-end testing", "e2e testing"),

            // =========================================================
            // SECURITY
            // =========================================================

            skill("jwt", "Security",
                    "jwt", "json web token"),

            skill("oauth", "Security",
                    "oauth", "oauth 1.0"),

            skill("oauth2", "Security",
                    "oauth2", "oauth 2", "oauth 2.0"),

            skill("openid connect", "Security",
                    "openid connect", "oidc"),

            skill("authentication", "Security",
                    "authentication"),

            skill("authorization", "Security",
                    "authorization"),

            skill("rbac", "Security",
                    "rbac", "role based access control"),

            skill("https", "Security",
                    "https"),

            skill("ssl", "Security",
                    "ssl"),

            skill("tls", "Security",
                    "tls"),

            skill("encryption", "Security",
                    "encryption"),

            skill("bcrypt", "Security",
                    "bcrypt"),

            skill("csrf", "Security",
                    "csrf", "cross site request forgery"),

            skill("xss", "Security",
                    "xss", "cross site scripting"),

            skill("sql injection", "Security",
                    "sql injection"),

            skill("owasp", "Security",
                    "owasp"),

            // =========================================================
            // MESSAGING
            // =========================================================

            skill("apache kafka", "Messaging",
                    "apache kafka", "kafka"),

            skill("rabbitmq", "Messaging",
                    "rabbitmq", "rabbit mq"),

            skill("activemq", "Messaging",
                    "activemq", "active mq"),

            skill("amazon sqs", "Messaging",
                    "amazon sqs", "sqs"),

            skill("amazon sns", "Messaging",
                    "amazon sns", "sns"),

            skill("message queue", "Messaging",
                    "message queue", "message queues"),

            skill("event streaming", "Messaging",
                    "event streaming"),

            // =========================================================
            // ARCHITECTURE
            // =========================================================

            skill("microservices", "Architecture",
                    "microservices", "microservices architecture"),

            skill("monolithic architecture", "Architecture",
                    "monolithic architecture", "monolith"),

            skill("distributed systems", "Architecture",
                    "distributed systems"),

            skill("event driven architecture", "Architecture",
                    "event driven architecture", "event-driven architecture"),

            skill("mvc", "Architecture",
                    "mvc", "model view controller"),

            skill("clean architecture", "Architecture",
                    "clean architecture"),

            skill("hexagonal architecture", "Architecture",
                    "hexagonal architecture"),

            skill("domain driven design", "Architecture",
                    "domain driven design", "ddd"),

            skill("solid", "Software Engineering",
                    "solid", "solid principles"),

            skill("design patterns", "Software Engineering",
                    "design patterns"),

            skill("system design", "Software Engineering",
                    "system design"),

            skill("scalability", "Architecture",
                    "scalability", "scalable systems"),

            skill("high availability", "Architecture",
                    "high availability"),

            skill("fault tolerance", "Architecture",
                    "fault tolerance")
        );
    }

    private static SkillDefinition skill(
            String canonical,
            String category,
            String... aliases
    ) {
        return new SkillDefinition(
                canonical,
                category,
                List.of(aliases)
        );
    }
}