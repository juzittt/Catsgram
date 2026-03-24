# Первый вспомогательный этам с именем builder
FROM amazoncorretto:21-alpine as builder
# Устанавливаем Application в качестве рабочей директории
WORKDIR application
# Копируем артефакты в папку application в контейнере
COPY target/*.jar app.jar
# Используем специальный режим запуска Spring Boot приложени,
# который активируетраспаковку мтогового jar-файла на составляющие
RUN java -Djarmode=layertools -jar app.jar extract

# Заключительный этап, создающий финальный образ
FROM amazoncorretto:21-alpine
# Поочередно копируем необходимые для приложения файлы,
# которые были распакованы из артефакта на предыдущем этапе;
# при этом каждая инструкция COPY создает новый слой
COPY --from=builder /application/dependencies/ ./
COPY --from=builder /application/spring-boot-loader/ ./
COPY --from=builder /application/snapshot-dependencies/ ./
COPY --from=builder /application/application ./

# В качестве команды указываем запуск специального загрузчика
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]