from typing import List
from pydantic import BaseModel

class GenerateRequest(BaseModel):
    goal: str
    days: int = 3

class DayPlan(BaseModel):
    day: int
    tasks: List[str]

class GenerateResponse(BaseModel):
    days: List[DayPlan]
