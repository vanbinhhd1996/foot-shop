-- Create Database
CREATE DATABASE IF NOT EXISTS foodstore_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE foodstore_db;

-- Table: users
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    status ENUM('ACTIVE', 'INACTIVE', 'BANNED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: categories
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    parent_id BIGINT,
    image_url VARCHAR(500),
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_parent (parent_id),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: products
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    image_url VARCHAR(500),
    sku VARCHAR(50) UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    views INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    INDEX idx_category (category_id),
    INDEX idx_name (name),
    INDEX idx_price (price),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: product_images
CREATE TABLE product_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    display_order INT DEFAULT 0,
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: carts
CREATE TABLE carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_cart (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: cart_items
CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_cart_product (cart_id, product_id),
    INDEX idx_cart (cart_id),
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: orders
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    total_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED') DEFAULT 'PENDING',
    payment_method ENUM('COD', 'CREDIT_CARD', 'BANK_TRANSFER', 'E_WALLET') NOT NULL,
    payment_status ENUM('PENDING', 'PAID', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    shipping_address TEXT NOT NULL,
    shipping_phone VARCHAR(20) NOT NULL,
    shipping_name VARCHAR(100) NOT NULL,
    shipping_fee DECIMAL(10, 2) DEFAULT 0,
    discount_amount DECIMAL(10, 2) DEFAULT 0,
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_order_number (order_number),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: order_items
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
    INDEX idx_order (order_id),
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: order_status_history
CREATE TABLE order_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    old_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    note TEXT,
    changed_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (changed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: reviews
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_product_review (product_id, user_id),
    INDEX idx_product (product_id),
    INDEX idx_user (user_id),
    INDEX idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- サンプルデータの挿入
-- ユーザー
INSERT INTO users (username, email, password, full_name, phone, address, role) VALUES
('admin', 'admin@foodstore.com', 'admin123', '管理者', '090-1234-5678', '東京都渋谷区道玄坂1-2-3', 'ADMIN'),
('yamada', 'yamada@example.com', '123456', '山田太郎', '080-1111-2222', '東京都新宿区西新宿1-1-1', 'USER'),
('suzuki', 'suzuki@example.com', '123456', '鈴木花子', '090-3333-4444', '大阪府大阪市北区梅田1-2-3', 'USER');

-- カテゴリー（メイン）
INSERT INTO categories (name, description, display_order, is_active) VALUES
('野菜・果物', '新鮮な野菜と果物', 1, TRUE),
('肉・魚・海鮮', '新鮮な肉類と海鮮食品', 2, TRUE),
('卵・乳製品', '卵、牛乳、チーズなど', 3, TRUE),
('米・パン・麺', '主食類', 4, TRUE),
('飲料', '飲み物各種', 5, TRUE),
('お菓子・スイーツ', 'スナック菓子とデザート', 6, TRUE),
('冷凍食品', '冷凍された調理済み食品', 7, TRUE),
('調味料・油', '料理用調味料と油', 8, TRUE);

-- サブカテゴリー - 野菜・果物
INSERT INTO categories (name, description, parent_id, display_order, is_active) VALUES
('葉物野菜', 'ほうれん草、小松菜など', 1, 1, TRUE),
('根菜類', 'じゃがいも、にんじんなど', 1, 2, TRUE),
('果物', 'りんご、みかん、バナナなど', 1, 3, TRUE);

-- サブカテゴリー - 肉・魚・海鮮
INSERT INTO categories (name, description, parent_id, display_order, is_active) VALUES
('豚肉', '国産豚肉各種', 2, 1, TRUE),
('牛肉', '和牛・輸入牛肉', 2, 2, TRUE),
('鶏肉', '国産鶏肉', 2, 3, TRUE),
('鮮魚', '新鮮な魚', 2, 4, TRUE),
('海鮮', 'エビ、カニ、貝類など', 2, 5, TRUE);

-- 商品データ - 野菜・果物
INSERT INTO products (category_id, name, description, price, stock_quantity, image_url, sku, is_active) VALUES
-- 葉物野菜
(9, 'ほうれん草 1束', '国産の新鮮なほうれん草', 198.00, 100, '/images/hourensou.jpg', 'VEGE-HOUREN-001', TRUE),
(9, '小松菜 1束', '栄養満点の小松菜', 158.00, 80, '/images/komatsuna.jpg', 'VEGE-KOMATSU-001', TRUE),
(9, 'レタス 1玉', 'シャキシャキ新鮮レタス', 198.00, 60, '/images/lettuce.jpg', 'VEGE-LETTUCE-001', TRUE),
(9, 'キャベツ 1玉', '甘くて美味しいキャベツ', 258.00, 50, '/images/cabbage.jpg', 'VEGE-CABBAGE-001', TRUE),

-- 根菜類
(10, 'じゃがいも 500g', '北海道産じゃがいも', 298.00, 150, '/images/potato.jpg', 'VEGE-POTATO-500', TRUE),
(10, 'にんじん 3本', '甘くて栄養豊富', 198.00, 120, '/images/carrot.jpg', 'VEGE-CARROT-3PC', TRUE),
(10, '玉ねぎ 3個', '料理に欠かせない玉ねぎ', 198.00, 200, '/images/onion.jpg', 'VEGE-ONION-3PC', TRUE),
(10, '大根 1本', '新鮮な大根', 198.00, 80, '/images/daikon.jpg', 'VEGE-DAIKON-001', TRUE),

-- 果物
(11, 'りんご（富士）4個', '青森県産富士りんご', 598.00, 100, '/images/apple.jpg', 'FRUIT-APPLE-4PC', TRUE),
(11, 'みかん 1kg', '愛媛県産温州みかん', 498.00, 120, '/images/mikan.jpg', 'FRUIT-MIKAN-1KG', TRUE),
(11, 'バナナ 1房', 'フィリピン産バナナ', 298.00, 150, '/images/banana.jpg', 'FRUIT-BANANA-1PC', TRUE),
(11, 'いちご 1パック', '甘くて美味しいいちご', 598.00, 60, '/images/ichigo.jpg', 'FRUIT-ICHIGO-1PK', TRUE),

-- 肉類 - 豚肉
(12, '豚バラスライス 200g', '国産豚バラ肉', 498.00, 80, '/images/pork-bara.jpg', 'MEAT-PORK-BARA-200', TRUE),
(12, '豚ロース 200g', '柔らかい豚ロース', 598.00, 70, '/images/pork-rosu.jpg', 'MEAT-PORK-ROSU-200', TRUE),
(12, '豚ひき肉 300g', '料理に便利な豚ひき肉', 398.00, 100, '/images/pork-hiki.jpg', 'MEAT-PORK-HIKI-300', TRUE),

-- 牛肉
(13, '牛バラスライス 200g', '和牛バラ肉', 1280.00, 40, '/images/beef-bara.jpg', 'MEAT-BEEF-BARA-200', TRUE),
(13, '牛モモスライス 200g', '赤身の牛もも肉', 980.00, 50, '/images/beef-momo.jpg', 'MEAT-BEEF-MOMO-200', TRUE),

-- 鶏肉
(14, '鶏もも肉 300g', '国産鶏もも肉', 498.00, 100, '/images/chicken-momo.jpg', 'MEAT-CHICK-MOMO-300', TRUE),
(14, '鶏むね肉 300g', 'ヘルシーな鶏むね肉', 298.00, 120, '/images/chicken-mune.jpg', 'MEAT-CHICK-MUNE-300', TRUE),
(14, '鶏手羽元 500g', '唐揚げに最適', 498.00, 80, '/images/chicken-teba.jpg', 'MEAT-CHICK-TEBA-500', TRUE),

-- 鮮魚
(15, 'サーモン切身 2切', 'ノルウェー産サーモン', 798.00, 60, '/images/salmon.jpg', 'FISH-SALMON-2PC', TRUE),
(15, 'サバ切身 2切', '新鮮なサバ', 398.00, 80, '/images/saba.jpg', 'FISH-SABA-2PC', TRUE),
(15, 'アジ 3尾', '新鮮なアジ', 498.00, 50, '/images/aji.jpg', 'FISH-AJI-3PC', TRUE),

-- 海鮮
(16, 'エビ（大）10尾', 'ブラックタイガー', 980.00, 40, '/images/ebi.jpg', 'SEAFOOD-EBI-10PC', TRUE),
(16, 'イカ 1杯', '新鮮なイカ', 498.00, 60, '/images/ika.jpg', 'SEAFOOD-IKA-1PC', TRUE),
(16, 'アサリ 300g', '砂抜き済みアサリ', 398.00, 80, '/images/asari.jpg', 'SEAFOOD-ASARI-300', TRUE),

-- 卵・乳製品
(3, '卵 10個パック', '新鮮な国産卵', 298.00, 200, '/images/tamago.jpg', 'DAIRY-EGG-10PC', TRUE),
(3, '牛乳 1L', '明治おいしい牛乳', 228.00, 150, '/images/milk.jpg', 'DAIRY-MILK-1L', TRUE),
(3, 'ヨーグルト 400g', '明治ブルガリアヨーグルト', 198.00, 180, '/images/yogurt.jpg', 'DAIRY-YOGURT-400', TRUE),
(3, 'バター 150g', '雪印北海道バター', 398.00, 100, '/images/butter.jpg', 'DAIRY-BUTTER-150', TRUE),
(3, 'チーズ（スライス）7枚', '雪印メグミルクスライスチーズ', 298.00, 120, '/images/cheese.jpg', 'DAIRY-CHEESE-7PC', TRUE),

-- 米・パン・麺
(4, '白米 5kg', 'コシヒカリ', 2980.00, 80, '/images/kome.jpg', 'RICE-KOSHI-5KG', TRUE),
(4, '食パン 6枚切', 'ヤマザキ超芳醇', 198.00, 100, '/images/shokupan.jpg', 'BREAD-SHOKU-6PC', TRUE),
(4, 'うどん 3玉', '讃岐うどん', 198.00, 150, '/images/udon.jpg', 'NOODLE-UDON-3PC', TRUE),
(4, 'そば 3束', '十割そば', 398.00, 100, '/images/soba.jpg', 'NOODLE-SOBA-3PC', TRUE),
(4, 'スパゲッティ 500g', 'バリラ スパゲッティ', 298.00, 120, '/images/pasta.jpg', 'NOODLE-PASTA-500', TRUE),

-- 飲料
(5, 'コカ・コーラ 500ml', '定番の炭酸飲料', 138.00, 300, '/images/coca.jpg', 'DRINK-COCA-500', TRUE),
(5, 'お～いお茶 500ml', '緑茶飲料', 138.00, 300, '/images/ocha.jpg', 'DRINK-OCHA-500', TRUE),
(5, 'ポカリスエット 500ml', 'スポーツドリンク', 158.00, 200, '/images/pocari.jpg', 'DRINK-POCARI-500', TRUE),
(5, 'カルピス 500ml', '乳酸菌飲料', 198.00, 150, '/images/calpis.jpg', 'DRINK-CALPIS-500', TRUE),
(5, 'オレンジジュース 1L', '果汁100%', 298.00, 100, '/images/orange-juice.jpg', 'DRINK-ORANGE-1L', TRUE),

-- お菓子・スイーツ
(6, 'ポテトチップス うすしお 60g', 'カルビーポテチ', 148.00, 200, '/images/potato-chips.jpg', 'SNACK-POTATO-60', TRUE),
(6, 'きのこの山 74g', 'チョコレート菓子', 198.00, 150, '/images/kinoko.jpg', 'SNACK-KINOKO-74', TRUE),
(6, 'ハイチュウ 12粒', 'グミキャンディ', 128.00, 180, '/images/hi-chew.jpg', 'CANDY-HICHEW-12', TRUE),
(6, 'じゃがりこ サラダ 60g', 'スティックスナック', 158.00, 200, '/images/jagariko.jpg', 'SNACK-JAGARI-60', TRUE),

-- 冷凍食品
(7, '冷凍餃子 12個', '味の素冷凍餃子', 298.00, 100, '/images/gyoza.jpg', 'FROZEN-GYOZA-12', TRUE),
(7, '冷凍チャーハン 450g', 'ニチレイチャーハン', 398.00, 80, '/images/chahan.jpg', 'FROZEN-CHAHAN-450', TRUE),
(7, 'フライドポテト 300g', 'マケイン フライドポテト', 298.00, 120, '/images/fried-potato.jpg', 'FROZEN-POTATO-300', TRUE),
(7, '冷凍うどん 3食', '讃岐冷凍うどん', 298.00, 150, '/images/frozen-udon.jpg', 'FROZEN-UDON-3PC', TRUE),

-- 調味料・油
(8, '醤油 1L', 'キッコーマン特選丸大豆', 398.00, 150, '/images/shoyu.jpg', 'SEASON-SHOYU-1L', TRUE),
(8, '味噌 750g', 'マルコメ料亭の味', 398.00, 120, '/images/miso.jpg', 'SEASON-MISO-750', TRUE),
(8, 'サラダ油 1L', '日清キャノーラ油', 398.00, 100, '/images/oil.jpg', 'SEASON-OIL-1L', TRUE),
(8, '砂糖 1kg', '上白糖', 298.00, 180, '/images/sato.jpg', 'SEASON-SATO-1KG', TRUE),
(8, '塩 1kg', '食塩', 198.00, 200, '/images/shio.jpg', 'SEASON-SHIO-1KG', TRUE),
(8, 'みりん 500ml', '本みりん', 398.00, 150, '/images/mirin.jpg', 'SEASON-MIRIN-500', TRUE);