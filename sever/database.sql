
CREATE DATABASE shopapp_test;
USE shopapp_test;
-- Khách hàng khi muốn mua hàng => phải đăng ký tài khoản => bảng users

CREATE TABLE users(
    id INT PRIMARY KEY auto_increment,
    full_name VARCHAR(100) not null,
    phone_number CHAR(10),
    email VARCHAR (100) not null ,
    address VARCHAR(200),
    password VARCHAR(100) NOT NULL DEFAULT '',
    created_at DATETIME,
    updated_at DATETIME,
    is_active TINYINT(1) DEFAULT 1,
    date_of_birth DATE,
    role VARCHAR(10) DEFAULT 'CUSTOMER',
    refresh_token mediumtext,
    password_reset_token mediumtext
);


-- hỗ trợ đăng nhập từ Facebook và Google
-- CREATE TABLE social_accounts(
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     provider VARCHAR(20) NOT NULL COMMENT 'Tên nhà social network',
--     provider_id CHAR(50) NOT NULL,
--     email VARCHAR(150) NOT NULL COMMENT 'Email tài khoản',
--     name VARCHAR(100) NOT NULL COMMENT 'Tên người dùng',
--     user_id INT,
--     FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
-- );

-- Bảng danh mục sản phẩm(Category)
CREATE TABLE categories(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name varchar(100) NOT NULL DEFAULT '' COMMENT 'Tên danh mục, vd: đồ điện tử'
);

-- Bảng chứa sản phẩm(Product): "laptop macbook air 15 inch 2023", iphone 15 pro,...
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(350) COMMENT 'Tên sản phẩm',
    brand VARCHAR(50) NOT NULL,
    import_price FLOAT NOT NULL CHECK(import_price>=0),
    price FLOAT NOT NULL CHECK (price >= 0),
    thumbnail VARCHAR(300) DEFAULT '',
    description LONGTEXT,
    created_at DATETIME,
    updated_at DATETIME,
    category_id INT,
    available TINYINT(1) DEFAULT 1,
    color VARCHAR(20) DEFAULT '',
    quantity INT NOT NULL CHECK(quantity >= 0),
    rented_quantity INT NOT NULL CHECK(rented_quantity >= 0),
    FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
);
-- Đặt hàng - orders
CREATE TABLE orders(
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    fullname VARCHAR(100) DEFAULT '',
    email VARCHAR(100) DEFAULT '',
    phone_number CHAR(10) NOT NULL,
    note VARCHAR(100) DEFAULT '',
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('pending', 'processing', 'shipped', 'delivered', 'cancelled') COMMENT 'Trạng thái đơn hàng',
    total_money FLOAT CHECK(total_money >= 0),
    shipping_method VARCHAR(100) NOT NULL,
    shipping_address VARCHAR(200) NOT NULL,
    shipping_date DATE NOT NULL,
    tracking_number VARCHAR(100) NOT NULL,
    payment_method VARCHAR(100) NOT NULL,
    active TINYINT(1),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


CREATE TABLE order_details(
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    product_id INT,
    price FLOAT CHECK(price >= 0),
    number_of_products INT CHECK(number_of_products > 0),
    total_money FLOAT CHECK(total_money >= 0),
	return_date DATETIME NOT NULL,
    time_renting INT ,
    return_method ENUM('home', 'store'),
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);

CREATE TABLE carts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNIQUE,
    updated_at DATETIME,
--    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE cart_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cart_id INT,
    product_id INT,
    quantity INT NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
CREATE TABLE product_images(
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT fk_product_images_product_id
        FOREIGN KEY (product_id)
        REFERENCES products (id) ON DELETE CASCADE,
    image_url VARCHAR(300)
);