-- ============================================
-- Project P6: Grievance Priority Queue
-- MySQL Schema
-- ============================================

CREATE DATABASE IF NOT EXISTS grievance_db;
USE grievance_db;

-- 1. Category Table
CREATE TABLE IF NOT EXISTS Category (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    weight INT NOT NULL
);

-- 2. Citizen Table
CREATE TABLE IF NOT EXISTS Citizen (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(50),
    email VARCHAR(100)
);

-- 3. Officer Table
CREATE TABLE IF NOT EXISTS Officer (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(50),
    resolved_count INT DEFAULT 0
);

-- 4. Grievance Table
CREATE TABLE IF NOT EXISTS Grievance (
    id INT PRIMARY KEY AUTO_INCREMENT,
    citizen_id INT NOT NULL,
    category_id INT NOT NULL,
    officer_id INT,
    description TEXT,
    status VARCHAR(20) DEFAULT 'Submitted',
    submitted_date DATE NOT NULL,
    priority INT DEFAULT 0,
    FOREIGN KEY (citizen_id) REFERENCES Citizen(id),
    FOREIGN KEY (category_id) REFERENCES Category(id),
    FOREIGN KEY (officer_id) REFERENCES Officer(id)
);

-- 5. StatusHistory Table
CREATE TABLE IF NOT EXISTS StatusHistory (
    id INT PRIMARY KEY AUTO_INCREMENT,
    grievance_id INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarks VARCHAR(255),
    FOREIGN KEY (grievance_id) REFERENCES Grievance(id)
);

-- ============================================
-- Seed Data
-- ============================================

INSERT INTO Category (name, weight) VALUES
('Water', 3),
('Road', 2),
('Garbage', 2),
('Lighting', 1),
('Other', 1);

INSERT INTO Officer (name, department) VALUES
('Rajesh Kumar', 'Water'),
('Priya Sharma', 'Roads'),
('Amit Verma', 'Sanitation'),
('Sunita Patil', 'Electrical');

INSERT INTO Citizen (name, contact, email) VALUES
('Ramesh Gupta', '9876543210', 'ramesh@email.com'),
('Sunita Devi', '9123456789', 'sunita@email.com'),
('Mahesh Rao', '9988776655', 'mahesh@email.com');
