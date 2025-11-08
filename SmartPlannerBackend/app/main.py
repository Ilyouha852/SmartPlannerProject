from fastapi import FastAPI, HTTPException
from app.models import GenerateRequest, GenerateResponse, DayPlan
from app.prompt_templates import PROMPT_TEMPLATE, extract_json
from app.llm_client import call_local_llm

app = FastAPI(title="SmartPlanner API (Ollama)")

@app.get("/health")
async def health():
    return {"status": "ok"}

@app.post("/generate_plan", response_model=GenerateResponse)
async def generate_plan(req: GenerateRequest):
    prompt = PROMPT_TEMPLATE.format(goal=req.goal, days=req.days)

    try:
        llm_text = await call_local_llm(prompt)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Ошибка вызова LLM: {e}")

    parsed = extract_json(llm_text)
    if not parsed:
        raise HTTPException(status_code=500, detail=f"Не удалось распарсить JSON: {llm_text[:500]}")

    try:
        days = [
            DayPlan(day=int(d["day"]), tasks=[str(t) for t in d["tasks"]])
            for d in parsed["days"]
        ]
        return GenerateResponse(days=days)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Ошибка в структуре JSON: {e}")
