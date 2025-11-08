import os
import httpx
import json

LOCAL_LLM_URL = os.getenv("LOCAL_LLM_URL", "http://192.168.0.26:11434")
MODEL = os.getenv("LOCAL_OLLAMA_MODEL", "qwen2.5:7b-instruct")

async def call_local_llm(prompt: str, max_tokens: int = 1024) -> str:
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

    # Increase timeout because some models may take longer than 60s to generate
    # Set generous timeout (in seconds). Adjust as needed for your hardware/model.
    async with httpx.AsyncClient(timeout=300.0) as client:
        resp = await client.post(url, json=payload)
        resp.raise_for_status()
        data = resp.json()

    # Ollama может вернуть {"response":"..."} или {"text":"..."}
    return data.get("response") or data.get("text") or str(data)
