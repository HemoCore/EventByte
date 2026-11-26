-- Give stock back for several tickets at once (expired booking, US-14).
-- Runs as one single Redis step, like reserve-stock.lua.
--
-- KEYS[i] = "stock:<ticketId>"
-- ARGV[i] = quantity to give back for KEYS[i]
--
-- A missing key is left missing: it means Redis has no counter for that ticket
-- (for example Redis was emptied), and the next booking will set the stock
-- again from the database quantity. Creating it here would give a wrong stock.

for i = 1, #KEYS do
  if redis.call('EXISTS', KEYS[i]) == 1 then
    redis.call('INCRBY', KEYS[i], ARGV[i])
  end
end
return 1
