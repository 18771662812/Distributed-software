-- Redis Lua 脚本：秒杀库存原子扣减
-- 参数说明：
-- KEYS[1]: 库存键 (seckill:stock:productId)
-- KEYS[2]: 已售键 (seckill:sold:productId)
-- ARGV[1]: 扣减数量（通常为1）
-- ARGV[2]: 用户ID（用于防重）
-- ARGV[3]: 用户下单记录键 (seckill:user:productId:userId)

-- 获取当前库存
local stock = redis.call('GET', KEYS[1])
if not stock then
    return {0, "库存不存在"}
end

stock = tonumber(stock)
local quantity = tonumber(ARGV[1])

-- 检查库存是否充足
if stock < quantity then
    return {0, "库存不足"}
end

-- 检查用户是否已经下单过（防重）
local userOrderKey = ARGV[3]
local userOrdered = redis.call('EXISTS', userOrderKey)
if userOrdered == 1 then
    return {0, "用户已下单"}
end

-- 原子扣减库存
redis.call('DECRBY', KEYS[1], quantity)

-- 增加已售数量
redis.call('INCRBY', KEYS[2], quantity)

-- 标记用户已下单（设置过期时间为秒杀活动时长，这里设为1小时）
redis.call('SETEX', userOrderKey, 3600, ARGV[2])

-- 返回成功，包含用户ID
return {1, ARGV[2]}
