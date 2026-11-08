# BladeChat

Плагин для управления локальным и глобальным чатами на серверах Minecraft (Paper / Purpur / Folia 1.21, Java 21).

## Возможности
- Разделение чата на локальный (в радиусе) и глобальный (с префиксом или через команду).
- Поддержка HEX-цветов (`&#RRGGBB`), градиентов и классических цветовых кодов (`&a-f`).
- Интеграция с PlaceholderAPI и LuckPerms для вывода префиксов и суффиксов.
- Задержка между глобальными сообщениями (cooldown).
- Очистка чата (`/bladechat clear`).
- Блокировка и разблокировка чата (`/bladechat mute`).
- Режим слежки за локальным чатом для администрации (`/bladechat spy`).

## Форматы сообщений
- **Локальный чат**: `&#E0A602Ⓛ &f%luckperms_prefix% %player% %luckperms_suffix% &7⇨ &#D8D8D8&o%message%`
- **Глобальный чат**: `&#FF471AⒼ &f%luckperms_prefix% %player% %luckperms_suffix% &7⇨ &#D8D8D8%message%`

## Команды
- `/bladechat reload` — перезагрузить конфигурацию плагина (`bladechat.admin.reload`).
- `/bladechat clear` — очистить чат (`bladechat.admin.clear`).
- `/bladechat mute` — заблокировать или разблокировать чат (`bladechat.admin.mute`).
- `/bladechat spy` — включить или выключить режим шпиона (`bladechat.admin.spy`).
- `/g <сообщение>` — отправить сообщение в глобальный чат (`bladechat.global`).
- `/l <сообщение>` — отправить сообщение в локальный чат.

## Сборка
```bash
mvn clean package
```
Скомпилированный `.jar` файл будет находиться в директории `target/`.
