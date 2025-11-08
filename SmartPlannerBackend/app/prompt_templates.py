import json
import re
from typing import Optional

PROMPT_TEMPLATE = """<|im_start|>system
Ты — умный планировщик задач. Ты составляешь подробные, пошаговые планы достижения целей.
<|im_end|>
<|im_start|>user
Моя цель: "{goal}"
Составь реалистичный план на {days} дней.
Ответ дай строго в формате JSON:
{{
  "days": [
    {{"day": 1, "tasks": ["Задача 1", "Задача 2", "Задача 3"]}},
    {{"day": 2, "tasks": ["..."]}}
  ]
}}
<|im_end|>
<|im_start|>assistant
"""

def extract_json(text: str) -> Optional[dict]:
    text = text.strip()
    try:
        return json.loads(text)
    except Exception:
        pass

    # Найти первый корректный JSON-блок
    match = re.search(r"\{.*\}", text, re.DOTALL)
    if match:
        try:
            return json.loads(match.group(0))
        except Exception:
            return None
    return None
