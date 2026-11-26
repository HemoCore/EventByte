-- Reserve stock for several tickets at once, all or nothing.
-- Redis runs a script as one single step: no other command can run in the middle,
-- so two bookings can never both take the last ticket (US-13).
--
-- KEYS[i]        = "stock:<ticketId>"
-- ARGV[2*i - 1]  = quantity asked for KEYS[i]
-- ARGV[2*i]      = initial stock for KEYS[i] (only used if the key does not exist yet)
-- returns 1 if reserved, 0 if at least one ticket lacks stock (nothing is changed)

-- 1. first time a ticket is booked: set its stock from the database quantity
for i = 1, #KEYS do
  redis.call('SET', KEYS[i], ARGV[i * 2], 'NX')
end

-- 2. check every ticket before touching anything
for i = 1, #KEYS do
  if tonumber(redis.call('GET', KEYS[i])) < tonumber(ARGV[i * 2 - 1]) then
    return 0
  end
end

-- 3. everything is available: take it
for i = 1, #KEYS do
  redis.call('DECRBY', KEYS[i], ARGV[i * 2 - 1])
end
return 1
