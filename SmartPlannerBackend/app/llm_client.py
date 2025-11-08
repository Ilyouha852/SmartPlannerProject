import os
import httpx
import json

LOCAL_LLM_URL = os.getenv("LOCAL_LLM_URL", "http://localhost:11434")
MODEL = os.getenv("LOCAL_OLLAMA_MODEL", "qwen2.5:7b-instruct")

async def call_local_llm(prompt: str, max_tokens: int = 512) -> str:
    """
    Вызов локальной модели Ollama.
    Эндпоинт: POST /api/generate
    """
    url = f"{LOCAL_LLM_URL}/api/generate"
    payload = {
        "model": MODEL,
        "prompt": prompt,
        "stream": False,        # важно: получаем полный ответ, не поток
        "options": {"num_predict": max_tokens}
    }

    async with httpx.AsyncClient(timeout=60.0) as client:
        resp = await client.post(url, json=payload)
        resp.raise_for_status()
        data = resp.json()

    # Ollama может вернуть {"response":"..."} или {"text":"..."}
    return data.get("response") or data.get("text") or str(data)
