-- CREATE DATABASE ecommerce_db;

-- Remove tabelas existentes para garantir um ambiente limpo
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;

-- Criação da tabela de produtos
CREATE TABLE products (
                          id UUID PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          price NUMERIC(10, 2) NOT NULL,
                          quantity INT NOT NULL
);

-- Criação da tabela de pedidos
CREATE TABLE orders (
                        id UUID PRIMARY KEY,
                        customer_name VARCHAR(255) NOT NULL,
                        created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Criação da tabela de itens do pedido
CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id UUID NOT NULL REFERENCES orders(id),
                             product_id UUID NOT NULL,
                             product_name VARCHAR(255) NOT NULL,
                             quantity INT NOT NULL,
                             unit_price NUMERIC(10, 2) NOT NULL
);

-- Inserir alguns produtos de exemplo
INSERT INTO products (id, name, price, quantity) VALUES
                                                     ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Laptop Gamer Ultra', 7500.00, 10),
                                                     ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Mouse Sem Fio Fast', 150.50, 50),
                                                     ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Teclado Mecânico RGB', 450.00, 30),
                                                     ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'Monitor 4K 27"', 2200.00, 15);