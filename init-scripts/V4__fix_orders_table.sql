-- Drop unnecessary columns
ALTER TABLE orders 
    DROP COLUMN IF EXISTS order_id,
    DROP COLUMN IF EXISTS created_at,
    DROP COLUMN IF EXISTS total_price,
    DROP COLUMN IF EXISTS user_id,
    DROP COLUMN IF EXISTS tracking_number,
    DROP COLUMN IF EXISTS customer_id;

-- Ensure all required columns exist with correct types
ALTER TABLE orders 
    ALTER COLUMN id SET DATA TYPE BIGINT,
    ALTER COLUMN id SET NOT NULL,
    ALTER COLUMN version SET DATA TYPE BIGINT,
    ALTER COLUMN keycloak_user_id SET DATA TYPE VARCHAR(255),
    ALTER COLUMN keycloak_user_id SET NOT NULL,
    ALTER COLUMN order_date SET DATA TYPE TIMESTAMP,
    ALTER COLUMN order_date SET NOT NULL,
    ALTER COLUMN total_amount SET DATA TYPE DOUBLE PRECISION,
    ALTER COLUMN total_amount SET NOT NULL,
    ALTER COLUMN status SET DATA TYPE VARCHAR(50),
    ALTER COLUMN status SET NOT NULL,
    ALTER COLUMN shipping_address SET DATA TYPE VARCHAR(255),
    ALTER COLUMN shipping_address SET NOT NULL,
    ALTER COLUMN delivery_type SET DATA TYPE VARCHAR(50),
    ALTER COLUMN delivery_type SET NOT NULL,
    ALTER COLUMN delivery_method SET DATA TYPE VARCHAR(50),
    ALTER COLUMN delivery_method SET NOT NULL,
    ALTER COLUMN payment_method SET DATA TYPE VARCHAR(50),
    ALTER COLUMN payment_method SET NOT NULL; 