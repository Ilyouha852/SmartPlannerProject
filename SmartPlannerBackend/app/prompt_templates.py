import json
import re
from typing import Optional

PROMPT_TEMPLATE = """
Ты — умный планировщик задач. Твоя задача — составить практичный, реалистичный и поэтапный план достижения цели.

Вход: цель пользователя: "{goal}". Количество дней: 7.

Требования к ответу (строго соблюдай):
1) Возвращай только корректный JSON без каких-либо пояснений и без блоков кода (никаких ```markdown```).
2) Формат ответа должен точно соответствовать схемe ниже.
3) В каждой задаче (task) давай краткое поле "title" и при необходимости поле "description" с подробностями.
4) Указывай приблизительное время на задачу в минутах в поле "est_minutes" (целое число).
5) Добавь поле "priority" со значениями "high", "medium" или "low".

Схема (обязательна):
{
    "days": [
        {
            "day": 1,
            "tasks": [
                {
                    "title": "Короткое название задачи",
                    "description": "Краткое подробное описание (по необходимости)",
                    "est_minutes": 60,
                    "priority": "medium"
                }
            ]
        }
    ]
}

Пример для 2 дней:
{
    "days": [
        {"day": 1, "tasks": [{"title": "Изучить синтаксис Python", "description": "Пройти вводный туториал, написать 5 простых скриптов", "est_minutes": 120, "priority": "high"}]},
        {"day": 2, "tasks": [{"title": "Практика функций и циклов", "description": "Решить 10 задач на функции и циклы", "est_minutes": 90, "priority": "medium"}]}
    ]
}

Требования к контенту:
- Не добавляй постороннего текста (только JSON).
- Каждый день должен содержать 1–8 задач, ориентируйся на реалистичную нагрузку.
- Если задача требует нескольких шагов, помести их в поле "description".

Сформируй план под цель пользователя и под указанное количество дней.
"""

def extract_json(text: str) -> Optional[dict]:
    text = text.strip()
    try:
        return json.loads(text)
    except Exception:
        pass
    # Убираем возможные блоки кода ```...``` и лишние пояснения
    text_clean = re.sub(r"```.*?```", "", text, flags=re.DOTALL).strip()

    # Найдём потенциальное место начала JSON — ближайшую фигурную скобку перед "days" или первую '{'
    days_pos = text_clean.find('"days"')
    if days_pos != -1:
        start_idx = text_clean.rfind('{', 0, days_pos)
    else:
        start_idx = text_clean.find('{')

    if start_idx == -1:
        return None

    # Попробуем извлечь сбалансированный JSON начиная с start_idx
    brace_count = 0
    end_idx = -1
    for i in range(start_idx, len(text_clean)):
        ch = text_clean[i]
        if ch == '{':
            brace_count += 1
        elif ch == '}':
            brace_count -= 1
            if brace_count == 0:
                end_idx = i + 1
                break

    candidate = text_clean[start_idx:end_idx] if end_idx != -1 else text_clean[start_idx:]

    # Если JSON усечён (нет закрывающих скобок), попробуем добавить недостающие '}' и распарсить
    for extra in range(0, 8):
        try:
            to_parse = candidate + ('}' * extra)
            return json.loads(to_parse)
        except Exception:
            continue

    return None
