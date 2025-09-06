# DailyQuests

Plugin Paper 1.18.2 (Java 17) – dzienne questy z nagrodami i progresją.

## Instalacja
1. Wymagany serwer Paper 1.18.2, Java 17.
2. Umieść plik DailyQuests.jar w folderze `plugins`.
3. (Opcjonalnie) Zainstaluj [Vault](https://dev.bukkit.org/projects/vault) dla ekonomii.
4. (Opcjonalnie) Zainstaluj [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) dla placeholderów.
5. Zrestartuj serwer.

## Konfiguracja
- `config.yml`: ustawienia pluginu (liczba questów, rotacja, ekonomia, storage, autosave).
- `quests.yml`: definicje questów.

## Komendy
- `/quests [reload|info <id>]` – lista questów, info o questach, reload configu (admin).
- `/claim` – rozliczenie aktywnych questów.

## Permisje
- `dailyquests.user` – dostęp do questów i rozliczania.
- `dailyquests.admin` – reload, rotacja, zarządzanie.

## Placeholdery
- `%dailyquests_current_1_name%` – nazwa 1. aktywnego questu
- `%dailyquests_current_1_progress%` – progres gracza w 1. questcie
- `%dailyquests_current_1_required%` – wymagane itemy w 1. questcie
- `%dailyquests_current_1_tier%` – tier gracza w 1. questcie
- `%dailyquests_current_count%` – liczba aktywnych questów

## Storage
- YAML (`players.yml`) lub SQLite (`players.db`).

## Testy
Minimalne testy jednostkowe dla ProgressService (JUnit 5).
