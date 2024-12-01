CREATE DATABASE PharmacyDB;
USE PharmacyDB;

-- Users Table
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    username VARCHAR(50) UNIQUE,
    password VARCHAR(50),
    role ENUM('Admin', 'Staff'),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Medicines Table
CREATE TABLE Medicines (
    medicine_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    brand VARCHAR(100),
    type ENUM('Tablet', 'Syrup', 'Injection'),
    quantity INT,
    price DECIMAL(10, 2),
    expiry_date DATE
);

-- Suppliers Table
CREATE TABLE Suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    contact_number VARCHAR(15),
    email VARCHAR(100),
    address TEXT
);

-- Transactions Table
CREATE TABLE Transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    medicine_id INT NOT NULL,
    customer_id INT NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (medicine_id) REFERENCES Medicines(medicine_id),
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id)
);


-- Inventory Table
CREATE TABLE Inventory (
    inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    medicine_id INT,
    supplier_id INT,
    quantity INT,
    purchase_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (medicine_id) REFERENCES Medicines(medicine_id),
    FOREIGN KEY (supplier_id) REFERENCES Suppliers(supplier_id)
);

-- Customer Table
CREATE TABLE Customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    contact_number VARCHAR(15),
    email VARCHAR(100)
);


DELIMITER //

CREATE PROCEDURE update_inventory(IN med_id INT, IN qty_change INT)
BEGIN
    DECLARE med_expiry DATE;

    SELECT expiry_date INTO med_expiry FROM Medicines WHERE medicine_id = med_id;

    IF med_expiry < CURDATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot add or modify expired medicines';
    ELSE
        UPDATE Medicines
        SET quantity = quantity + qty_change
        WHERE medicine_id = med_id;
    END IF;
END //

DELIMITER ;

DELIMITER //
CREATE PROCEDURE record_transaction(
    IN med_id INT, 
    IN cst_id INT, 
    IN qty INT
)
BEGIN
    DECLARE price_per_unit DECIMAL(10, 2);
    DECLARE total_price DECIMAL(10, 2);

    SELECT price INTO price_per_unit FROM Medicines WHERE medicine_id = med_id;
    SET total_price = price_per_unit * qty;

    INSERT INTO Transactions (medicine_id, customer_id, quantity, total_price, transaction_date)
    VALUES (med_id, cst_id, qty, total_price, NOW());

    CALL update_inventory(med_id, -qty);
END //

DELIMITER ;

DELIMITER //

CREATE TRIGGER check_stock_before_sale
BEFORE INSERT ON Transactions
FOR EACH ROW
BEGIN
    DECLARE current_stock INT;

    SELECT quantity INTO current_stock FROM Medicines WHERE medicine_id = NEW.medicine_id;

    IF current_stock < NEW.quantity THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Insufficient stock';
    END IF;
END //

DELIMITER ;

DELIMITER //

CREATE TRIGGER mark_expired_medicines
BEFORE UPDATE ON Medicines
FOR EACH ROW
BEGIN
    IF NEW.expiry_date < CURDATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot update stock for expired medicines';
    END IF;
END //

DELIMITER ;
