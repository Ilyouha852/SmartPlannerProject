from fastapi import FastAPI, HTTPException
from app.models import GenerateRequest, GenerateResponse, DayPlan, Task
from app.prompt_templates import PROMPT_TEMPLATE, extract_json
from app.llm_client import call_local_llm
from pydantic import ValidationError

app = FastAPI(title="SmartPlanner API (Ollama)")

@app.get("/health")
async def health():
    return {"status": "ok"}

@app.post("/generate_plan", response_model=GenerateResponse)
async def generate_plan(req: GenerateRequest):
    print(f"Received request: {req}")
    # Используем replace вместо .format, чтобы не ломать JSON-скобки в шаблоне.
    # Также захардкоживаем количество дней = 7 по текущему требованию.
    prompt = PROMPT_TEMPLATE.replace("{goal}", req.goal).replace("{days}", "7")
    print(f"Generated prompt (days forced to 7): {prompt}")

    try:
        llm_text = await call_local_llm(prompt)
        print(f"LLM response: {llm_text}")
    except Exception as e:
        print(f"LLM error: {e}")
        raise HTTPException(status_code=500, detail=f"Ошибка вызова LLM: {e}")

    parsed = extract_json(llm_text)
    print(f"Parsed JSON: {parsed}")
    
    if not parsed:
        raise HTTPException(status_code=500, detail=f"Не удалось распарсить JSON: {llm_text[:500]}")

    try:
        # Let pydantic validate and construct the response model
        response = GenerateResponse.model_validate(parsed)
        print(f"Final response: {response}")
        return response
    except ValidationError as e:
        print(f"Validation error creating response: {e}")
        raise HTTPException(status_code=500, detail=f"Ошибка валидации JSON: {e}")
    except Exception as e:
        print(f"Error creating response: {e}")
        raise HTTPException(status_code=500, detail=f"Ошибка в структуре JSON: {e}")
