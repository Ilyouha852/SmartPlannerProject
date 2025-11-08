from typing import List, Optional
from pydantic import BaseModel

class GenerateRequest(BaseModel):
    goal: str
    days: int = 3

class Task(BaseModel):
    title: str
    description: Optional[str] = None
    est_minutes: int
    priority: str

class DayPlan(BaseModel):
    day: int
    tasks: List[Task]

class GenerateResponse(BaseModel):
    days: List[DayPlan]
