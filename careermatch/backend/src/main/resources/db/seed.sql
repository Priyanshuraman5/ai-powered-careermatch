-- =====================================================================
-- CareerMatch seed data (MySQL Compatible)
-- =====================================================================

-- ---------------------------------------------------------------------
-- Skills taxonomy
-- ---------------------------------------------------------------------
INSERT INTO skills (name, category) VALUES
('Java', 'Programming Language'),
('Python', 'Programming Language'),
('JavaScript', 'Programming Language'),
('TypeScript', 'Programming Language'),
('SQL', 'Programming Language'),
('Go', 'Programming Language'),
('C++', 'Programming Language'),
('Spring Boot', 'Framework'),
('React', 'Framework'),
('Vue', 'Framework'),
('Node.js', 'Framework'),
('Django', 'Framework'),
('Flask', 'Framework'),
('Express', 'Framework'),
('AWS', 'Cloud/DevOps'),
('Azure', 'Cloud/DevOps'),
('GCP', 'Cloud/DevOps'),
('Docker', 'Cloud/DevOps'),
('Kubernetes', 'Cloud/DevOps'),
('CI/CD', 'Cloud/DevOps'),
('Terraform', 'Cloud/DevOps'),
('PostgreSQL', 'Data'),
('MySQL', 'Data'),
('MongoDB', 'Data'),
('Redis', 'Data'),
('Elasticsearch', 'Data'),
('Machine Learning', 'Data'),
('Data Analysis', 'Data'),
('TensorFlow', 'Data'),
('PyTorch', 'Data'),
('REST API', 'Architecture'),
('GraphQL', 'Architecture'),
('Microservices', 'Architecture'),
('System Design', 'Architecture'),
('Agile', 'Process'),
('Scrum', 'Process'),
('Git', 'Tooling'),
('Jenkins', 'Tooling'),
('Figma', 'Design'),
('UI/UX Design', 'Design'),
('Product Management', 'Business'),
('Project Management', 'Business'),
('Communication', 'Soft Skill'),
('Leadership', 'Soft Skill'),
('Problem Solving', 'Soft Skill'),
('Team Collaboration', 'Soft Skill'),
('Sales', 'Business'),
('Marketing', 'Business'),
('SEO', 'Marketing'),
('Content Writing', 'Marketing'),
('Customer Success', 'Business'),
('Salesforce', 'Business Tools'),
('Excel', 'Business Tools'),
('Tableau', 'Data'),
('Power BI', 'Data'),
('Cybersecurity', 'Security'),
('Penetration Testing', 'Security'),
('Networking', 'Infrastructure'),
('Linux', 'Infrastructure'),
('Swift', 'Programming Language'),
('Kotlin', 'Programming Language');

-- ---------------------------------------------------------------------
-- Sample users (password for all seed users: "password123")
-- ---------------------------------------------------------------------
INSERT INTO users (email, password_hash, full_name, headline, location, phone, role, created_at) VALUES
('demo@careermatch.dev', '$2b$10$dnl07hVHLSmnHKL0KPGrP.QS2tPPNuDPdW/fNTk/9ga.XQKjf5UAi', 'Demo Candidate',
 'Full-stack developer passionate about building products', 'Austin, TX', '555-0100', 'CANDIDATE', NOW()),
('alex.rivera@careermatch.dev', '$2b$10$dnl07hVHLSmnHKL0KPGrP.QS2tPPNuDPdW/fNTk/9ga.XQKjf5UAi', 'Alex Rivera',
 'Backend engineer with 5 years in distributed systems', 'Seattle, WA', '555-0101', 'CANDIDATE', NOW()),
('priya.nair@careermatch.dev', '$2b$10$dnl07hVHLSmnHKL0KPGrP.QS2tPPNuDPdW/fNTk/9ga.XQKjf5UAi', 'Priya Nair',
 'Data scientist focused on ML in production', 'New York, NY', '555-0102', 'CANDIDATE', NOW()),
('recruiter@techcorp.dev', '$2b$10$dnl07hVHLSmnHKL0KPGrP.QS2tPPNuDPdW/fNTk/9ga.XQKjf5UAi', 'Jordan Blake',
 'Technical Recruiter at TechCorp', 'Remote', '555-0103', 'EMPLOYER', NOW());

-- demo candidate's skills
INSERT INTO user_skills (user_id, skill_id)
SELECT (SELECT id FROM users WHERE email = 'demo@careermatch.dev'), id FROM skills
WHERE name IN ('Java', 'Spring Boot', 'JavaScript', 'React', 'SQL', 'Git', 'REST API', 'Agile');

INSERT INTO user_skills (user_id, skill_id)
SELECT (SELECT id FROM users WHERE email = 'alex.rivera@careermatch.dev'), id FROM skills
WHERE name IN ('Go', 'Java', 'Kubernetes', 'Docker', 'AWS', 'Microservices', 'PostgreSQL', 'System Design');

INSERT INTO user_skills (user_id, skill_id)
SELECT (SELECT id FROM users WHERE email = 'priya.nair@careermatch.dev'), id FROM skills
WHERE name IN ('Python', 'Machine Learning', 'TensorFlow', 'PyTorch', 'SQL', 'Data Analysis', 'AWS');

-- ---------------------------------------------------------------------
-- Jobs (15+ realistic postings)
-- ---------------------------------------------------------------------
INSERT INTO jobs (title, company, location, employment_type, description, salary_min, salary_max, experience_level, posted_at, active) VALUES
('Senior Backend Engineer', 'NimbusCloud', 'Austin, TX', 'FULL_TIME',
 'Design and build scalable REST APIs and microservices powering our core platform. Work closely with product and infra teams to ship reliable, well-tested Java and Spring Boot services on AWS.',
 130000, 165000, 'SENIOR', DATE_SUB(NOW(), INTERVAL 2 DAY), true),

('Full-Stack Developer', 'Lighthouse Labs', 'Remote', 'FULL_TIME',
 'Build end-to-end features across our React frontend and Node.js/Express backend. You will own features from design through deployment, working with PostgreSQL and Docker-based CI/CD pipelines.',
 100000, 135000, 'MID', DATE_SUB(NOW(), INTERVAL 1 DAY), true),

('Machine Learning Engineer', 'DataForge AI', 'New York, NY', 'FULL_TIME',
 'Develop and productionize ML models for demand forecasting. Requires strong Python, TensorFlow or PyTorch experience, and comfort deploying models with Docker and AWS SageMaker-style infrastructure.',
 140000, 180000, 'SENIOR', DATE_SUB(NOW(), INTERVAL 3 DAY), true),

('Frontend Engineer (React)', 'Bright Interfaces', 'San Francisco, CA', 'FULL_TIME',
 'Own the component architecture for our design system. Deep React and TypeScript experience required, along with an eye for UI/UX detail and collaboration with Figma-driven design workflows.',
 115000, 150000, 'MID', DATE_SUB(NOW(), INTERVAL 5 DAY), true),

('DevOps Engineer', 'ScaleUp Systems', 'Remote', 'FULL_TIME',
 'Own our CI/CD pipelines, Kubernetes clusters, and infrastructure-as-code using Terraform across AWS and GCP. Strong Linux, Docker, and observability tooling background expected.',
 120000, 155000, 'MID', DATE_SUB(NOW(), INTERVAL 4 DAY), true),

('Junior Software Engineer', 'StartWell Inc', 'Chicago, IL', 'FULL_TIME',
 'Great entry-level role for a new grad. Work on our Java/Spring Boot backend and SQL-backed services, with mentorship from senior engineers. Git and Agile fundamentals a plus.',
 75000, 95000, 'ENTRY', DATE_SUB(NOW(), INTERVAL 6 DAY), true),

('Data Analyst', 'Insightly', 'Denver, CO', 'FULL_TIME',
 'Turn raw data into actionable business insights using SQL, Excel, and Tableau/Power BI dashboards. Partner with marketing and sales teams to track KPIs and campaign performance.',
 70000, 90000, 'ENTRY', DATE_SUB(NOW(), INTERVAL 7 DAY), true),

('Product Manager', 'Northstar Software', 'Boston, MA', 'FULL_TIME',
 'Lead the roadmap for our B2B SaaS platform. Requires strong product management, communication, and leadership skills, plus comfort working closely with engineering on system design tradeoffs.',
 125000, 160000, 'SENIOR', DATE_SUB(NOW(), INTERVAL 8 DAY), true),

('Cloud Security Engineer', 'Fortify Networks', 'Remote', 'FULL_TIME',
 'Protect cloud workloads across AWS and Azure. Responsibilities include penetration testing, security audits, and building automated cybersecurity tooling with Python and Terraform.',
 135000, 170000, 'SENIOR', DATE_SUB(NOW(), INTERVAL 9 DAY), true),

('Mobile Engineer (iOS)', 'AppSpring', 'Los Angeles, CA', 'FULL_TIME',
 'Build and maintain our flagship iOS app in Swift. Collaborate with backend teams consuming REST APIs and GraphQL, and contribute to a growing Kotlin-based Android codebase.',
 110000, 145000, 'MID', DATE_SUB(NOW(), INTERVAL 10 DAY), true),

('QA / Test Engineer', 'Reliable Software Co', 'Remote', 'CONTRACT',
 'Design and execute test plans for our web platform, build automated test suites, and work with engineering on CI/CD integration. SQL and scripting experience preferred.',
 60, 85, 'MID', DATE_SUB(NOW(), INTERVAL 11 DAY), true),

('Engineering Manager', 'Peak Technologies', 'Seattle, WA', 'FULL_TIME',
 'Lead a team of 6-8 backend engineers building Java/Spring Boot microservices on Kubernetes. Strong leadership, system design, and mentorship experience required.',
 160000, 200000, 'LEAD', DATE_SUB(NOW(), INTERVAL 12 DAY), true),

('Data Engineer', 'PipelineWorks', 'Remote', 'FULL_TIME',
 'Build and maintain ETL pipelines and data warehouses. Strong Python and SQL required, along with experience with distributed data tools and cloud data platforms (AWS/GCP).',
 115000, 150000, 'MID', DATE_SUB(NOW(), INTERVAL 13 DAY), true),

('UX/UI Designer', 'Bright Interfaces', 'San Francisco, CA', 'FULL_TIME',
 'Design intuitive, accessible interfaces for our web and mobile products. Proficiency in Figma required, plus strong collaboration with frontend engineers implementing React components.',
 95000, 125000, 'MID', DATE_SUB(NOW(), INTERVAL 14 DAY), true),

('Sales Development Representative', 'GrowthPath', 'Remote', 'FULL_TIME',
 'Generate and qualify new business leads for our SaaS platform. Strong communication and Salesforce experience preferred; prior SDR or customer success background a plus.',
 55000, 75000, 'ENTRY', DATE_SUB(NOW(), INTERVAL 15 DAY), true),

('Marketing Specialist', 'GrowthPath', 'Remote', 'FULL_TIME',
 'Own SEO strategy and content writing for our marketing site and blog. Collaborate with product marketing to drive organic growth and measure results with analytics tooling.',
 60000, 80000, 'ENTRY', DATE_SUB(NOW(), INTERVAL 16 DAY), true),

('Staff Software Engineer', 'NimbusCloud', 'Austin, TX', 'FULL_TIME',
 'Drive system design decisions across our platform, mentoring senior engineers and setting technical direction for our Java/Spring Boot and React ecosystem at scale.',
 175000, 220000, 'LEAD', DATE_SUB(NOW(), INTERVAL 17 DAY), true),

('Backend Engineer (Python)', 'DataForge AI', 'New York, NY', 'FULL_TIME',
 'Build backend services in Python (Django/Flask) powering our ML platform. Experience with PostgreSQL, REST API design, and Docker-based deployment workflows required.',
 105000, 140000, 'MID', DATE_SUB(NOW(), INTERVAL 18 DAY), true);

-- ---------------------------------------------------------------------
-- Job -> required skills mapping
-- ---------------------------------------------------------------------
INSERT INTO job_skills (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, skills s
WHERE j.title = 'Senior Backend Engineer' AND s.name IN ('Java', 'Spring Boot', 'AWS', 'REST API', 'Microservices', 'SQL');

INSERT INTO job_skills (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, skills s
WHERE j.title = 'Full-Stack Developer' AND s.name IN ('React', 'Node.js', 'JavaScript', 'PostgreSQL', 'Docker', 'CI/CD');

INSERT INTO job_skills (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, skills s
WHERE j.title = 'Machine Learning Engineer' AND s.name IN ('Python', 'Machine Learning', 'TensorFlow', 'PyTorch', 'AWS', 'Docker');

INSERT INTO job_skills (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, skills s
WHERE j.title = 'Frontend Engineer (React)' AND s.name IN ('React', 'TypeScript', 'JavaScript', 'UI/UX Design', 'Figma');

INSERT INTO job_skills (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, skills s
WHERE j.title = 'DevOps Engineer' AND s.name IN ('Kubernetes', 'Docker', 'Terraform', 'AWS', 'GCP', 'Linux', 'CI/CD');

INSERT INTO job_skills (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, skills s
WHERE j.title = 'Junior Software Engineer' AND s.name IN ('Java', 'Spring Boot', 'SQL', 'Git', 'Agile');

INSERT INTO job_skills (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, skills s
WHERE j.title = 'Data Analyst' AND s.name IN ('SQL', 'Excel', 'Tableau', 'Power BI', 'Data Analysis');

INSERT INTO job_skills (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, skills s
WHERE j.title = 'Product Manager' AND s.name IN ('Product Management', 'Communication', 'Leadership', 'System Design', 'Agile');

INSERT INTO job_skills (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, skills s
WHERE j.title = 'Cloud Security Engineer' AND s.name IN ('AWS', 'Azure', 'Cybersecurity', 'Penetration Testing', 'Python', 'Terraform');

INSERT INTO job_skills (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, skills s
WHERE j.title = 'Mobile Engineer (iOS)' AND s.name IN ('Swift', 'Kotlin', 'REST API', 'GraphQL');

INSERT INTO job_skills (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, skills s
WHERE j.title = 'QA / Test Engineer' AND s.name IN ('SQL', 'CI/CD', 'Git', 'Problem Solving');

INSERT INTO job_skills (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, skills s
WHERE j.title = 'Engineering Manager' AND s.name IN ('Java', 'Spring Boot', 'Kubernetes', 'Leadership', 'System Design', 'Agile');

INSERT INTO job_skills (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, skills s
WHERE j.title = 'Data Engineer' AND s.name IN ('Python', 'SQL', 'AWS', 'GCP', 'Data Analysis');

INSERT INTO job_skills (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, skills s
WHERE j.title = 'UX/UI Designer' AND s.name IN ('Figma', 'UI/UX Design', 'React', 'Communication');

INSERT INTO job_skills (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, skills s
WHERE j.title = 'Sales Development Representative' AND s.name IN ('Sales', 'Communication', 'Salesforce', 'Customer Success');

INSERT INTO job_skills (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, skills s
WHERE j.title = 'Marketing Specialist' AND s.name IN ('SEO', 'Content Writing', 'Marketing', 'Communication');

INSERT INTO job_skills (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, skills s
WHERE j.title = 'Staff Software Engineer' AND s.name IN ('Java', 'Spring Boot', 'React', 'System Design', 'Leadership', 'Microservices');

INSERT INTO job_skills (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, skills s
WHERE j.title = 'Backend Engineer (Python)' AND s.name IN ('Python', 'Django', 'Flask', 'PostgreSQL', 'REST API', 'Docker');