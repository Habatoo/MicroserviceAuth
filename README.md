
#### Перед стартом поднимаем две БД для модулей user и event
БД для модуля user
<code>
sudo docker run --rm --name postgres_u -e POSTGRES_PASSWORD=1234567890 -e POSTGRES_USER=sportuser -e POSTGRES_DB=postgres_u -d -p 5433:5432 -v postgres_u:/var/lib/postgresl/postgres_u  postgres
</code>
<br>
<br>
БД для модуля event
<code>
sudo docker run --rm --name postgres_e -e POSTGRES_PASSWORD=1234567890 -e POSTGRES_USER=sportuser -e POSTGRES_DB=postgres_e -d -p 5434:5432 -v postgres_e:/var/lib/postgresl/postgres_e  postgres
</code>
<br>
### zipkin поднимаем из котейнера - в коде не пишем
<code>
docker run -d -p 9411:9411 openzipkin/zipkin
</code>
<br>
<br>

### Последовательность запуска
- стартуем модуль eureka - проверяем http://localhost:3001/
- модуль zipkin проверяем - http://localhost:9411/zipkin/
- стартуем модуль gate - http://localhost:9000/ проверяем после старта следующих двух модулей
- стартуем модуль user - проверяем - http://localhost:8900/info - должно отбражаться что то типа - Event Sat Dec 25 20:05:24 NOVT 2021
- стартуем модуль event - проверяем - http://localhost:8901/info - должно отбражаться что то типа - User Sat Dec 25 20:08:58 NOVT 2021
- в eureka в разделе Application должеы появиться три модуля - user, event и gate
- проверяем что модуль gate перенаправлет через себя сообщения на модуль user - http://localhost:9000/users/info - должно работать как при обращении к модулю user напрямую
- проверяем что модуль gate перенаправлет через себя сообщения на модуль event - http://localhost:9000/events/info - должно работать как при обращении к модулю user напрямую