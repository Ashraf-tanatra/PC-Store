-- ======================================
-- RESET DB
-- ======================================
DROP DATABASE IF EXISTS ia_computer_db;
CREATE DATABASE ia_computer_db;
USE ia_computer_db;

-- ======================================
-- 1) PERSON
-- ======================================
CREATE TABLE Person (
                        PersonID   INT AUTO_INCREMENT PRIMARY KEY,
                        FirstName  VARCHAR(50) NOT NULL,
                        SecondName VARCHAR(50) NOT NULL,
                        Gender     VARCHAR(10),
                        Phone      VARCHAR(20),
                        CONSTRAINT uq_person_phone UNIQUE (Phone)
);

-- ======================================
-- 2) EMPLOYEE / CUSTOMER
-- ======================================
CREATE TABLE Employee (
                          EmpID   INT PRIMARY KEY,
                          Salary  DECIMAL(10,2) NOT NULL,
                          Address VARCHAR(200),
                          CONSTRAINT fk_employee_person
                              FOREIGN KEY (EmpID) REFERENCES Person(PersonID)
                                  ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Customer (
                          CustID           INT PRIMARY KEY,
                          LastPurchaseDate DATE,
                          CONSTRAINT fk_customer_person
                              FOREIGN KEY (CustID) REFERENCES Person(PersonID)
                                  ON DELETE CASCADE ON UPDATE CASCADE
);





-- ======================================
-- 3) USERS
-- ======================================
CREATE TABLE Users (
                       UserID       INT AUTO_INCREMENT PRIMARY KEY,
                       PersonID     INT NOT NULL,
                       UserName     VARCHAR(50) NOT NULL UNIQUE,
                       Password     VARCHAR(100) NOT NULL,
                       Role         ENUM('ADMIN','EMP','CUST') NOT NULL DEFAULT 'CUST',
                       ActiveStatus BOOLEAN NOT NULL DEFAULT TRUE,
                       Email        VARCHAR(100),

                       CONSTRAINT uq_users_email UNIQUE (Email),
                       CONSTRAINT uq_users_person UNIQUE (PersonID),

                       CONSTRAINT fk_users_person
                           FOREIGN KEY (PersonID) REFERENCES Person(PersonID)
                               ON DELETE CASCADE ON UPDATE CASCADE
);

-- ======================================
-- 4) CATEGORY / INVENTORY / PRODUCT
-- ======================================
CREATE TABLE Category (
                          CatgID      INT AUTO_INCREMENT PRIMARY KEY,
                          CatgName    VARCHAR(100) NOT NULL UNIQUE,
                          isActive VARCHAR(10) NOT NULL DEFAULT 'ACTIVE',
                          Description TEXT,
                          ImagePath   VARCHAR(255)
);
SELECT ProdModel, Price, p.Description, p.ImagePath, Rate FROM Product p join Category c WHERE Rate = 5 AND c.CatgId = p.CatgID AND c.isActive='ACTIVE';




CREATE TABLE Inventory (
                           InvID    INT AUTO_INCREMENT PRIMARY KEY,
                           Capacity INT NOT NULL,
                           Location VARCHAR(100) NOT NULL
);

CREATE TABLE Product (
                         ProdID         INT AUTO_INCREMENT PRIMARY KEY,
                         ProdModel      VARCHAR(100) NOT NULL,
                         Rate           INT,
                         Price          DECIMAL(10,2) NOT NULL,
                         Quantity       INT NOT NULL DEFAULT 0,
                         CatgID         INT NOT NULL,
                         InvID          INT NOT NULL,
                         Description    TEXT,
                         ImagePath      VARCHAR(255),

                         CONSTRAINT chk_product_rate
                             CHECK (Rate BETWEEN 0 AND 5),

                         CONSTRAINT fk_product_category
                             FOREIGN KEY (CatgID) REFERENCES Category(CatgID)
                                 ON DELETE RESTRICT ON UPDATE CASCADE,

                         CONSTRAINT fk_product_inventory
                             FOREIGN KEY (InvID) REFERENCES Inventory(InvID)
                                 ON DELETE RESTRICT ON UPDATE CASCADE
);


-- ======================================
-- 6) DELIVERY / BILL / ORDERS
-- ======================================

CREATE TABLE Bill (
                      BillID      INT AUTO_INCREMENT PRIMARY KEY,
                      BillDate    DATETIME NOT NULL,
                      CustID      INT NOT NULL,
                      TotalAmount DECIMAL(10,2) NOT NULL DEFAULT 0,

                      CONSTRAINT fk_bill_customer
                          FOREIGN KEY (CustID) REFERENCES Customer(CustID)
                              ON DELETE RESTRICT ON UPDATE CASCADE
);



CREATE TABLE Orders (
                        OrderID     INT AUTO_INCREMENT PRIMARY KEY,
                        BillID      INT NOT NULL,
                        ProdID      INT NOT NULL,
                        OrderDate   DATETIME NOT NULL,
                        Quantity    INT NOT NULL,
                        UnitPrice   DECIMAL(10,2) NOT NULL,
                        Status      BOOLEAN NOT NULL DEFAULT TRUE,

                        CONSTRAINT fk_orders_bill
                            FOREIGN KEY (BillID) REFERENCES Bill(BillID)
                                ON DELETE CASCADE ON UPDATE CASCADE,

                        CONSTRAINT fk_orders_product
                            FOREIGN KEY (ProdID) REFERENCES Product(ProdID)
                                ON DELETE RESTRICT ON UPDATE CASCADE
);



-- ======================================
-- INSERT DATA
-- ======================================
INSERT INTO Person(FirstName, SecondName, Gender, Phone) VALUES
                                                             ('Ashraf','Tanatra','Male','0590000001'), -- PersonID=1
                                                             ('Ihab','Fawaqa','Male','0590000002'),    -- PersonID=2
                                                             ('Ahmad','Saleh','Male','0590000003'),    -- PersonID=3
                                                             ('Sara','Nassar','Female','0590000004');  -- PersonID=4

INSERT INTO Employee(EmpID, Salary, Address) VALUES
                                                 (1,3500.00,'Ramallah'),
                                                 (2,3200.00,'Nablus');

INSERT INTO Customer(CustID, LastPurchaseDate) VALUES
                                                   (3,'2025-12-01'),
                                                   (4,'2025-12-10');

INSERT INTO Users (PersonID, UserName, Password, Role, ActiveStatus, Email) VALUES
                                                                                (1, 'ashraf', 'ashraf123', 'ADMIN', TRUE, 'ashraf@gmail.com'),
                                                                                (2, 'ihab',   'ihab123',   'EMP',   TRUE, 'ihab@gmail.com'),
                                                                                (3, 'ahmad',  'ahmad123',  'CUST',  TRUE, 'ahmad@gmail.com'),
                                                                                (4, 'sara',   'sara123',   'CUST',  TRUE, 'sara@gmail.com');

-- Category / Inventory (صاروا AutoIncrement، فمش لازم IDs)
INSERT INTO Category(CatgName, Description, ImagePath) VALUES
                                                           ('GPU','Graphics Cards','images/gpu.png'),
                                                           ('CPU','Processors','images/cpu.png'),
                                                           ('RAM','Memory modules','images/ram.png'),
                                                           ('SSD','Storage devices','images/storage.jpg'),
                                                           ('Motherboard','Mainboards','images/motherboard.jpg'),
                                                           ('Power','Power supplies','images/power.jpg');

INSERT INTO Inventory(Capacity, Location) VALUES
                                              (500,'Warehouse - Ramallah'),
                                              (300,'Warehouse - Nablus');

-- IMPORTANT:
-- لازم تعرف CatgID و InvID اللي انولدوا (SELECT) قبل ما تحط Products
SELECT * FROM Category;
SELECT * FROM Inventory;

-- مثال: افترض CatgID GPU = 1, CPU = 2, RAM = 3, SSD = 4
-- وافترض InvID Ramallah = 1, Nablus = 2

INSERT INTO Product(ProdModel, Rate, Price, Quantity, CatgID, InvID, Description) VALUES
                                                                                      ('ASUS GTX 1070 Ti Turbo',5,1200.00,40,1,1,'Used GPU - good condition'),
                                                                                      ('Intel i5-12400F',5,850.00,70,2,1,'New CPU'),
                                                                                      ('Kingston 16GB DDR4 3200',4,180.00,120,3,2,'RAM kit'),
                                                                                      ('Samsung 970 EVO 512GB',5,260.00,90,4,2,'NVMe SSD');



-- Bills (AutoIncrement)
INSERT INTO Bill(BillDate, CustID, TotalAmount) VALUES
                                                    ('2025-12-18 12:00:00',3,0.00),
                                                    ('2025-12-18 13:30:00',4,0.00);




SELECT * FROM Bill;

-- Orders (AutoIncrement) - لازم تعرف BillID اللي انولدوا
-- مثال: BillID = 1,2
INSERT INTO Orders(BillID, ProdID,   OrderDate, Quantity, UnitPrice, Status) VALUES
                                                                                 (1,2,'2025-12-18 12:05:00',1,850.00,TRUE),
                                                                                 (1,3,'2025-12-18 12:06:00',2,180.00,TRUE),
                                                                                 (2,4,'2025-12-18 13:35:00',1,260.00,FALSE);



-- ======================================
-- Quick checks
-- ======================================
SELECT * FROM Person;
SELECT * FROM Employee;
SELECT * FROM Customer;
SELECT * FROM Users;
SELECT * FROM Category;
SELECT * FROM Inventory;
SELECT * FROM Product;
SELECT * FROM Bill;
SELECT * FROM Orders;

-- RAM Products (from your images)
INSERT INTO Product
(ProdModel, Rate, Price, Quantity, CatgID, InvID, Description, ImagePath)
VALUES
    ('Thermaltake TOUGHRAM RGB 16GB (2x8GB)', 5, 220.00, 30, 3, 2,
     'DDR4 RGB RAM kit - 16GB (2x8GB)', 'images/ram_toughram_16gb.jpg'),

    ('T-Force Delta RGB 8GB (1x8GB) 3200MHz', 4, 120.00, 40, 3, 2,
     'DDR4 RGB RAM - 8GB single stick 3200MHz', 'images/ram_tforce_delta_8gb_3200.jpg'),

    ('Gigabyte AORUS RGB Memory 16GB (2x8GB)', 5, 230.00, 25, 3, 2,
     'DDR4 RGB RAM kit - 16GB (2x8GB) AORUS', 'images/ram_aorus_16gb.jpg'),

    ('CORSAIR VENGEANCE RGB PRO 16GB (2x8GB)', 5, 240.00, 35, 3, 2,
     'DDR4 RGB RAM kit - 16GB (2x8GB) Corsair', 'images/ram_corsair_rgb_pro_16gb.jpg'),

    ('T-Force Delta 32GB (2x16GB) 6000MHz', 5, 420.00, 20, 3, 2,
     'High performance RAM kit - 32GB (2x16GB) 6000MHz DDR5 ', 'images/ram_tforce_delta_32gb_6000.jpg');

INSERT INTO Product
(ProdModel, Rate, Price, Quantity, CatgID, InvID, Description, ImagePath)
VALUES

    ('MSI PRO H610M-B DDR4', 4, 105.00, 35, 5, 1,
     'Entry-level Intel motherboard for 12th/13th Gen CPUs',
     'images/motherboard/intel_msi_h610m_b.jpg'),

    ('ASUS PRIME B660M-A WIFI D4', 5, 165.00, 25, 5, 1,
     'B660 chipset, DDR4, built-in WiFi, mATX',
     'images/motherboard/intel_asus_b660m_wifi.jpg'),

    ('Gigabyte B760 AORUS ELITE AX DDR5', 5, 220.00, 15, 5, 1,
     'High-end Intel B760 motherboard, DDR5, AORUS series',
     'images/motherboard/intel_gigabyte_b760_aorus.jpg'),

    ('MSI B450M PRO-VDH MAX', 4, 95.00, 30, 5, 1,
     'AMD B450 chipset, AM4 socket, DDR4 support',
     'images/motherboard/amd_msi_b450m_pro_vdh.jpg'),

    ('Gigabyte B550M DS3H', 5, 135.00, 28, 5, 1,
     'AMD B550 chipset, PCIe 4.0, AM4 socket',
     'images/motherboard/amd_gigabyte_b550m_ds3h.jpg'),

    ('ASUS TUF GAMING B550-PLUS WIFI II', 5, 175.00, 20, 5, 1,
     'Gaming-grade B550 motherboard with WiFi',
     'images/motherboard/amd_asus_tuf_b550_plus.jpg'),

    ('MSI MAG B650 TOMAHAWK WIFI', 5, 230.00, 15, 5, 1,
     'AMD B650 chipset, AM5 socket, DDR5, high performance',
     'images/motherboard/amd_msi_b650_tomahawk.jpg');

-- ===============================
-- Power Supply Products (8 items)
-- ===============================

INSERT INTO Product
(ProdModel, Rate, Price, Quantity, CatgID, InvID, Description, ImagePath)
VALUES
    ('Corsair RM750x 750W 80+ Gold', 5, 140.00, 25, 6, 1,
     'Fully modular PSU, 80+ Gold certified, very reliable',
     'images/power/psu_corsair_rm750x_box.jpg'),

    ('EVGA 750 GQ 750W 80+ Gold', 4, 125.00, 20, 6, 1,
     'Semi-modular power supply, 80+ Gold efficiency',
     'images/power/psu_evga_750gq_box.jpg'),

    ('Cooler Master MWE 650 Gold', 4, 110.00, 30, 6, 1,
     '650W PSU, 80+ Gold, good value for gaming PCs',
     'images/power/psu_cm_mwe_650_gold_box.jpg'),

    ('Thermaltake Toughpower GF1 750W', 5, 145.00, 18, 6, 1,
     'High quality PSU, fully modular, 80+ Gold',
     'images/power/psu_thermaltake_gf1_750_box.jpg'),

    ('MSI MPG A750GF 750W', 5, 135.00, 22, 6, 1,
     'Gaming PSU, fully modular, 80+ Gold certified',
     'images/power/psu_msi_a750gf_box.jpg'),

    ('Seasonic Focus GX-650 650W', 5, 130.00, 20, 6, 1,
     'Premium Seasonic PSU, fully modular, 80+ Gold',
     'images/power/psu_seasonic_gx650_box.jpg');

INSERT INTO Product
(ProdModel, Rate, Price, Quantity, CatgID, InvID, Description, ImagePath)
VALUES
    ('Samsung 970 EVO Plus 1TB NVMe', 5, 95.00, 30, 4, 1,
     'NVMe M.2 SSD, very fast read/write speeds, ideal for gaming and OS',
     'images/storage/ssd_samsung_970_evo_plus_1tb_box.jpg'),

    ('WD Black SN770 1TB NVMe', 5, 90.00, 28, 4, 1,
     'High-performance NVMe SSD, PCIe Gen4, great value',
     'images/storage/ssd_wd_black_sn770_1tb_box.jpg'),

    ('Kingston NV2 1TB NVMe', 4, 75.00, 35, 4, 1,
     'Budget-friendly NVMe SSD, PCIe Gen4 support',
     'images/storage/ssd_kingston_nv2_1tb_box.jpg'),

    ('Crucial P3 500GB NVMe', 4, 55.00, 40, 4, 1,
     'Entry-level NVMe SSD, good for daily use',
     'images/storage/ssd_crucial_p3_500gb_box.jpg'),

    ('Samsung 870 EVO 1TB SATA', 5, 85.00, 25, 4, 1,
     'SATA III SSD, very reliable, ideal for upgrades',
     'images/storage/ssd_samsung_870_evo_1tb_box.jpg');


INSERT INTO Product
(ProdModel, Rate, Price, Quantity, CatgID, InvID, Description, ImagePath)
VALUES
    ('Intel Core i3-12100F', 4, 95.00, 35, 2, 1,
     'Intel 12th Gen budget CPU, great for entry-level gaming',
     'images/cpu/cpu_intel_i3_12100f_box.jpg'),

    ('Intel Core i7-12700K', 5, 290.00, 15, 2, 1,
     'High-performance Intel CPU for gaming and productivity',
     'images/cpu/cpu_intel_i7_12700k_box.jpg'),

    ('Intel Core i5-13400F', 5, 185.00, 20, 2, 1,
     'Intel 13th Gen CPU, excellent gaming and multitasking',
     'images/cpu/cpu_intel_i5_13400f_box.jpg'),

    ('AMD Ryzen 5 5600X', 5, 155.00, 25, 2, 1,
     '6-core AMD CPU, very strong gaming performance',
     'images/cpu/cpu_amd_ryzen_5_5600x_box.jpg'),

    ('AMD Ryzen 7 5800X', 5, 220.00, 18, 2, 1,
     '8-core AMD CPU for heavy workloads and gaming',
     'images/cpu/cpu_amd_ryzen_7_5800x_box.jpg'),

    ('AMD Ryzen 5 7600X', 5, 245.00, 15, 2, 1,
     'Next-gen AMD AM5 CPU, DDR5 support, excellent performance',
     'images/cpu/cpu_amd_ryzen_5_7600x_box.jpg');