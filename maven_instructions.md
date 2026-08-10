# Публикация библиотеки на Maven Central

Инструкция по сборке и публикации артефакта **com.anti-captcha:anticaptcha** на [Maven Central](https://central.sonatype.com).

> **Что делать прямо сейчас, пока читаете остальное:** разделы 2 и 3 — регистрация и подтверждение namespace. Подтверждение домена через DNS может занять до нескольких часов, поэтому его стоит запустить первым.

---

## 1. Как вообще устроены пакеты в Java

В отличие от npm, NuGet, crates.io и RubyGems, где реестр и инструмент — одно и то же, в Java это разные вещи:

- **Maven Central** — единственный значимый **реестр**. Больше ничего публиковать не нужно.
- **Maven** и **Gradle** — **инструменты сборки**. Оба тянут зависимости из Maven Central.

То есть публикуем один артефакт, и его одинаково подключают все:

```xml
<!-- Maven -->
<dependency>
  <groupId>com.anti-captcha</groupId>
  <artifactId>anticaptcha</artifactId>
  <version>1.0.0</version>
</dependency>
```

```groovy
// Gradle
implementation "com.anti-captcha:anticaptcha:1.0.0"
```

Артефакт однозначно определяется тройкой **groupId:artifactId:version** (сокращённо GAV).

> **Про дефис в `com.anti-captcha`.** groupId и имя Java-пакета — разные вещи, и правила у них разные. В groupId дефис допустим (Maven разрешает `[A-Za-z0-9_\-.]+`), а в имени пакета — нет, это идентификатор языка. Поэтому groupId у нас `com.anti-captcha` (по домену `anti-captcha.com`), а пакет в исходниках остаётся `com.anti_captcha` с подчёркиванием. Так и должно быть, совпадать они не обязаны.

---

## 2. Регистрация в Central Portal

1. Зайти на <https://central.sonatype.com> и нажать **Sign In**.
2. Войти через GitHub, Google или создать отдельный аккаунт по email.

> **Важно:** старый портал `oss.sonatype.org` (OSSRH) и заведение тикетов в Jira **больше не работают** — процесс закрыли в 2025 году. Все инструкции в интернете, где фигурирует `oss.sonatype.org`, `nexus-staging-maven-plugin` или «создайте issue в OSSRH» — устаревшие. Нужен именно Central Portal.

---

## 3. Подтверждение namespace (groupId)

groupId должен принадлежать вам, и это нужно доказать. Есть два способа.

### Вариант А (рекомендую): свой домен → `com.anti-captcha`

groupId — это домен наоборот: домен `anti-captcha.com` даёт groupId `com.anti-captcha`.

1. На <https://central.sonatype.com/publishing/namespaces> нажать **Add Namespace**.
2. Ввести `com.anti-captcha`.
3. Портал покажет **verification key** — строку вида `abc123def456`.
4. Добавить в DNS домена `anti-captcha.com` TXT-запись:

   | Тип | Имя | Значение |
   |---|---|---|
   | TXT | `@` (корень домена) | `abc123def456` |

5. Дождаться распространения DNS. Проверить можно так:

   ```bash
   dig +short TXT anti-captcha.com
   ```

6. Вернуться на портал и нажать **Verify Namespace**.

После подтверждения TXT-запись можно удалить, но лучше оставить — при добавлении подпространств (`com.anti-captcha.something`) она пригодится снова.

### Вариант Б: через GitHub → `io.github.anti-captcha`

Если возиться с DNS не хочется:

1. **Add Namespace** → `io.github.anti-captcha` (должно совпадать с именем организации на GitHub).
2. Портал покажет verification key.
3. Создать в организации `anti-captcha` **публичный репозиторий с именем, равным этому ключу** (пустой, без файлов).
4. Нажать **Verify Namespace**.
5. Репозиторий после подтверждения можно удалить.

Минус варианта Б: groupId получается `io.github.anti-captcha`, что выглядит менее солидно, чем `com.anti-captcha`, и потом сменить его нельзя — это будет другой артефакт.

---

## 4. Токен для публикации

1. На портале: иконка профиля → **View Account** → **Generate User Token**.
2. Портал покажет готовый XML-фрагмент с `<username>` и `<password>` — **это не логин с паролем от аккаунта**, а одноразово показываемая пара токенов.
3. Вставить его в `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username>ТОКЕН_USERNAME</username>
      <password>ТОКЕН_PASSWORD</password>
    </server>
  </servers>
</settings>
```

`<id>central</id>` должен совпадать с тем, что указано в `pom.xml` в секции публикации. Файл `settings.xml` в git не коммитить.

---

## 5. GPG-ключ для подписи

Maven Central принимает только подписанные артефакты. Без ключа публикация не пройдёт.

### Установка

```bash
brew install gnupg
```

### Генерация ключа

```bash
gpg --full-generate-key
```

Отвечать так:

- **Kind of key**: `1` (RSA and RSA)
- **Keysize**: `4096`
- **Valid for**: `0` (бессрочно) или `2y`
- **Real name**: `Anti-Captcha.com`
- **Email**: тот же, что в аккаунте Central Portal
- **Passphrase**: обязательно задать, потом понадобится при сборке

### Публикация открытой части на keyserver

Central проверяет подпись по публичному ключу, поэтому его нужно выложить. Сначала узнать отпечаток:

```bash
gpg --list-secret-keys --keyid-format=long
```

Вывод вроде:

```
sec   rsa4096/A1B2C3D4E5F6A7B8 2026-08-10 [SC]
      1234567890ABCDEF1234567890ABCDEFA1B2C3D4
uid                 [ultimate] Anti-Captcha.com <support@anti-captcha.com>
```

`A1B2C3D4E5F6A7B8` — это key ID. Отправить ключ на серверы:

```bash
gpg --keyserver keyserver.ubuntu.com --send-keys A1B2C3D4E5F6A7B8
gpg --keyserver keys.openpgp.org    --send-keys A1B2C3D4E5F6A7B8
```

Проверить, что ключ виден:

```bash
gpg --keyserver keyserver.ubuntu.com --recv-keys A1B2C3D4E5F6A7B8
```

### Резервная копия

**Потеря секретного ключа означает, что подписывать новые версии тем же ключом больше нельзя.** Сохранить в менеджер паролей:

```bash
gpg --export-secret-keys --armor A1B2C3D4E5F6A7B8 > anticaptcha-signing-key.asc
```

Файл в git не коммитить ни при каких обстоятельствах.

---

## 6. Что требует Maven Central от pom.xml

Проверка на портале отклонит артефакт, если чего-то не хватает. Обязательный минимум уже прописан в нашем `pom.xml`:

| Элемент | Зачем |
|---|---|
| `groupId`, `artifactId`, `version` | координаты артефакта |
| `name`, `description`, `url` | карточка на сайте |
| `licenses` | лицензия (у нас MIT) |
| `developers` | кто сопровождает |
| `scm` | ссылки на репозиторий |

Плюс к основному jar обязательно прикладываются:

- `anticaptcha-1.0.0-sources.jar` — исходники
- `anticaptcha-1.0.0-javadoc.jar` — документация
- `.asc`-подпись **к каждому** файлу, включая сам `pom`

Всё это собирают плагины `maven-source-plugin`, `maven-javadoc-plugin` и `maven-gpg-plugin`, они уже подключены в профиле `release`.

### Правила версионирования (SemVer)

Формат `МАЖОРНАЯ.МИНОРНАЯ.ПАТЧ`:

- **ПАТЧ** (`1.0.0` → `1.0.1`) — исправление багов, ничего не сломано
- **МИНОРНАЯ** (`1.0.1` → `1.1.0`) — новый тип капчи или новый метод, старый код собирается
- **МАЖОРНАЯ** (`1.1.0` → `2.0.0`) — удалены или переименованы публичные классы/методы

Версии с суффиксом `-SNAPSHOT` (например `1.1.0-SNAPSHOT`) на Central не публикуются — это версии для локальной разработки.

> **Опубликованную версию нельзя ни удалить, ни перезалить.** Если в `1.0.0` нашлась ошибка — выпускается `1.0.1`. Это жёстче, чем в NuGet (там есть unlist) и в RubyGems (там есть yank).

---

## 7. Сборка и проверка перед публикацией

Из корня репозитория:

```bash
# 1. Собрать и прогнать тесты
mvn clean verify

# 2. Собрать полный набор артефактов с подписью
mvn clean verify -Prelease
```

При втором запуске GPG спросит passphrase. Если сборка идёт в CI и спросить некого — добавить:

```bash
mvn clean verify -Prelease -Dgpg.passphrase="ВАША_PASSPHRASE"
```

Проверить, что в `target/` появилось всё нужное:

```bash
ls -1 target/*.jar target/*.asc
```

Должно быть 4 jar-а (основной, sources, javadoc и, при наличии, tests) и `.asc` к каждому из них.

### Проверить установку локально

Самая полезная проверка — поставить артефакт в локальный репозиторий и подключить из чистого проекта:

```bash
mvn clean install
```

Затем в любом другом проекте прописать зависимость `com.anti-captcha:anticaptcha:1.0.0` и запустить простой класс:

```java
import com.anti_captcha.Api.ImageToText;

public class Test {
    public static void main(String[] args) {
        ImageToText api = new ImageToText();
        api.setClientKey("ВАШ_КЛЮЧ_ANTI_CAPTCHA");
        System.out.println("Balance: " + api.getBalance());
    }
}
```

Должен вывестись баланс аккаунта.

---

## 8. Публикация

```bash
mvn clean deploy -Prelease
```

Плагин `central-publishing-maven-plugin` соберёт артефакты, подпишет их и загрузит в Central Portal.

### Что происходит дальше

1. Загруженный набор попадает в раздел **Deployments** на портале со статусом `PENDING`, затем `VALIDATING`.
2. Портал проверяет подписи, наличие sources/javadoc, заполненность pom. Обычно 1–5 минут.
3. Если проверки прошли — статус `VALIDATED`. Дальше зависит от настройки:
   - при `<autoPublish>true</autoPublish>` (стоит у нас) публикация идёт автоматически;
   - иначе нужно зайти на портал и нажать **Publish**.
4. После публикации артефакт появляется на <https://central.sonatype.com/artifact/com.anti-captcha/anticaptcha> в течение нескольких минут.
5. **Синхронизация с `repo1.maven.org`** (откуда реально качают Maven и Gradle) занимает до 30 минут, а индексация поиска — до 4 часов.

Если статус стал `FAILED` — на портале, в карточке деплоя, будет список конкретных претензий.

---

## 9. Типичные ошибки

| Сообщение | Причина и что делать |
|---|---|
| `401 Unauthorized` | Токен в `~/.m2/settings.xml` не совпадает или протух. Перевыпустить на портале. |
| `403 Forbidden` / `Namespace is not allowed` | groupId не подтверждён, либо подтверждён другой аккаунт. См. раздел 3. |
| `Missing signature for file` | Не запущен профиль `release`, забыт флаг `-Prelease`. |
| `gpg: signing failed: Inappropriate ioctl for device` | GPG не может спросить passphrase в неинтерактивном терминале. Выполнить `export GPG_TTY=$(tty)` или передать `-Dgpg.passphrase=...`. |
| `Missing javadoc/sources` | Не запущен профиль `release`. |
| `Version already exists` | Версию переопубликовать нельзя. Поднять `<version>` в `pom.xml`. |
| Javadoc падает на ошибках | С JDK 8+ javadoc строгий. Либо починить комментарии, либо в `maven-javadoc-plugin` добавить `<doclint>none</doclint>` (уже стоит). |
| Артефакт опубликован, но Gradle его не находит | Синхронизация с `repo1.maven.org` ещё не прошла, подождать до получаса. |

---

## 10. Отзыв и удаление

**Удалить опубликованную версию нельзя.** Совсем. Это фундаментальное правило Maven Central: чужие сборки не должны ломаться.

Что доступно:

- **Выпустить исправленную версию** и указать в README, что предыдущая содержит ошибку. Основной способ.
- **Пометить как deprecated** — на Central Portal, в карточке артефакта.
- **Запросить удаление у поддержки** — только в исключительных случаях (утечка секретов, нарушение лицензии), через <https://central.sonatype.org/faq/>.

Если в опубликованную версию случайно попал секрет — считайте его скомпрометированным и отзывайте сам секрет, а не версию.

---

## 11. Публикация из GitHub Actions (по желанию)

Чтобы не публиковать руками:

1. Добавить секреты репозитория (**Settings → Secrets and variables → Actions**):
   - `CENTRAL_USERNAME` и `CENTRAL_PASSWORD` — токены из раздела 4
   - `GPG_PRIVATE_KEY` — вывод `gpg --export-secret-keys --armor KEY_ID`
   - `GPG_PASSPHRASE` — passphrase от ключа

2. Создать `.github/workflows/publish.yml`:

```yaml
name: Publish to Maven Central

on:
  push:
    tags:
      - 'v*'

jobs:
  publish:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          server-id: central
          server-username: CENTRAL_USERNAME
          server-password: CENTRAL_PASSWORD
          gpg-private-key: ${{ secrets.GPG_PRIVATE_KEY }}
          gpg-passphrase: GPG_PASSPHRASE

      - name: Publish
        run: mvn --batch-mode clean deploy -Prelease
        env:
          CENTRAL_USERNAME: ${{ secrets.CENTRAL_USERNAME }}
          CENTRAL_PASSWORD: ${{ secrets.CENTRAL_PASSWORD }}
          GPG_PASSPHRASE: ${{ secrets.GPG_PASSPHRASE }}
```

3. Релиз выпускается так:

```bash
# поднять <version> в pom.xml, закоммитить
mvn versions:set -DnewVersion=1.0.1 -DgenerateBackupPoms=false
git commit -am "Release 1.0.1"
git tag v1.0.1
git push origin master --tags
```

`actions/setup-java` сам создаёт `settings.xml` из указанных переменных, отдельно его готовить не нужно.

---

## 12. Временный вариант: JitPack

Пока namespace не подтверждён, библиотеку можно раздавать через [JitPack](https://jitpack.io) — он собирает прямо из тега на GitHub, никакой регистрации и подписи не нужно.

Пользователю понадобится добавить репозиторий:

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.github.anti-captcha</groupId>
  <artifactId>anticaptcha-java</artifactId>
  <version>v1.0.0</version>
</dependency>
```

От нас требуется только рабочий `pom.xml` в репозитории и тег `v1.0.0`. Минус: лишняя строка в конфиге у каждого пользователя и зависимость от стороннего сервиса. Как основной канал не годится, как временный — вполне.

---

## 13. Короткая шпаргалка

```bash
# разово: аккаунт, namespace, токен в ~/.m2/settings.xml, GPG-ключ на keyserver

# 1. поднять версию
mvn versions:set -DnewVersion=1.0.1 -DgenerateBackupPoms=false

# 2. собрать и прогнать тесты
mvn clean verify

# 3. проверить полный набор артефактов с подписями
mvn clean verify -Prelease
ls -1 target/*.jar target/*.asc

# 4. опубликовать
export GPG_TTY=$(tty)
mvn clean deploy -Prelease
```
