-- 使用MySQL重置admin用户密码为 "admin123"
-- 这里使用的是已经通过BCryptPasswordEncoder生成的密码哈希
-- 注意：不同的加密算法和盐值会生成不同的哈希，此哈希仅供参考

-- 更新admin用户密码
UPDATE auth_user SET password='$2a$10$1ZXb9UlVtAgq9s5qgW0zMO4UiY5k72mtHmJ2GZGXVLiLBLZMrDGlK' WHERE username='admin';

-- 此密码哈希对应的明文是 "admin123"
-- 使用BCryptPasswordEncoder.matches("admin123", "$2a$10$1ZXb9UlVtAgq9s5qgW0zMO4UiY5k72mtHmJ2GZGXVLiLBLZMrDGlK") 验证 