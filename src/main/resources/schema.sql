CREATE TABLE IF NOT EXISTS tasks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(32) NOT NULL,
    duration BIGINT,
    start_time TIMESTAMP,
    end_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS epics (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(32) NOT NULL,
    duration BIGINT,
    start_time TIMESTAMP,
    end_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS subtasks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(32) NOT NULL,
    duration BIGINT,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    epic_id INT NOT NULL,
    FOREIGN KEY (epic_id) REFERENCES epics(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    task_id INT NOT NULL,
    task_type VARCHAR(16) NOT NULL,
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
); 