CREATE TABLE Role (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    responsibilities TEXT
);

CREATE TABLE Team (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    color VARCHAR(7),
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE Users (
    login VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    team_id INT REFERENCES Team(id) ON DELETE SET NULL,
    role_id INT REFERENCES Role(id) ON DELETE SET NULL
);

CREATE TABLE Status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE Sprint (
    id SERIAL PRIMARY KEY,
    major_version VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    regression_start DATE NOT NULL,
    regression_end DATE NOT NULL,
    team_id INT NOT NULL REFERENCES Team(id) ON DELETE SET NULL
);

CREATE TABLE Task (
    id SERIAL PRIMARY KEY,
    name VARCHAR(2047) NOT NULL,
    story_points INT,
    implementer VARCHAR(255) REFERENCES Users(login) ON DELETE SET NULL,
    sprint_id INT REFERENCES Sprint(id) ON DELETE SET NULL,
    status_id INT REFERENCES Status(id) ON DELETE SET DEFAULT,
    priority_enum TEXT NOT NULL,
    created_by VARCHAR(255) REFERENCES Users(login) ON DELETE SET NULL
);
ALTER TABLE Task ALTER COLUMN status_id SET DEFAULT 1;

CREATE TABLE Idea (
    id SERIAL PRIMARY KEY,
    description VARCHAR(2047) NOT NULL,
    author_login VARCHAR(255) REFERENCES Users(login) ON DELETE SET NULL,
    status_enum_id TEXT NOT NULL,
    task_id INT REFERENCES Task(id) ON DELETE CASCADE
);

CREATE TABLE Releases (
    id SERIAL PRIMARY KEY,
    version VARCHAR(255) NOT NULL,
    release_date DATE NOT NULL,
    description TEXT,
    sprint_id INT REFERENCES Sprint(id) ON DELETE CASCADE
);

CREATE TABLE Risk (
    id SERIAL PRIMARY KEY,
    description TEXT NOT NULL,
    probability NUMERIC(3, 2) NOT NULL,
    estimated_loss DECIMAL NOT NULL
);

CREATE TABLE Tag (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE Task_Tag (
    task_id INT REFERENCES Task(id) ON DELETE CASCADE,
    tag_id INT REFERENCES Tag(id) ON DELETE CASCADE,
    PRIMARY KEY (task_id, tag_id)
);

CREATE TABLE Idea_Risk (
    idea_id INT REFERENCES Idea(id) ON DELETE CASCADE,
    risk_id INT REFERENCES Risk(id) ON DELETE CASCADE,
    PRIMARY KEY (idea_id, risk_id)
);

CREATE TABLE Task_Risk (
    task_id INT REFERENCES Task(id) ON DELETE CASCADE,
    risk_id INT REFERENCES Risk(id) ON DELETE CASCADE,
    PRIMARY KEY (task_id, risk_id)
);

CREATE TABLE Role_Status (
    role_id INT REFERENCES Role(id) ON DELETE CASCADE,
    status_id INT REFERENCES Status(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, status_id)
);













