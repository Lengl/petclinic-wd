# language: ru
@all
Функция: Shopping

  Сценарий: Give correct change
    Дано следующие товары:
      | name  | price |
      | milk  | 9     |
      | bread | 7     |
      | soap  | 5     |
    Когда я плачу 25 рублей
    Тогда сдача должна быть равна 4