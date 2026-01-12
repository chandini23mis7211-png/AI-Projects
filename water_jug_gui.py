import tkinter as tk
from tkinter import messagebox
from collections import deque


jug1 = 0
jug2 = 0
step = 0
running = False
solution = []
prev_state = (0, 0)


def bfs(cap1, cap2, target):
    visited = set()
    q = deque([((0, 0), [])])

    while q:
        (a, b), path = q.popleft()
        if (a, b) in visited:
            continue
        visited.add((a, b))
        path = path + [(a, b)]

        if a == target or b == target:
            return path

        next_states = [
            (cap1, b), (a, cap2),
            (0, b), (a, 0),
            (min(cap1, a+b), max(0, a+b-cap1)),
            (max(0, a+b-cap2), min(cap2, a+b))
        ]

        for s in next_states:
            if s not in visited:
                q.append((s, path))
    return []

root = tk.Tk()
root.title("AI Water Jug Problem – Production System Simulation")
root.geometry("1200x680")
root.configure(bg="#eef2f3")


top = tk.Frame(root, bg="#eef2f3")
top.pack(pady=5)

tk.Label(top, text="Jug1 Capacity").grid(row=0, column=0)
tk.Label(top, text="Jug2 Capacity").grid(row=0, column=2)
tk.Label(top, text="Target").grid(row=0, column=4)

cap1_e = tk.Entry(top, width=5)
cap2_e = tk.Entry(top, width=5)
target_e = tk.Entry(top, width=5)

cap1_e.grid(row=0, column=1)
cap2_e.grid(row=0, column=3)
target_e.grid(row=0, column=5)

cap1_e.insert(0, "4")
cap2_e.insert(0, "3")
target_e.insert(0, "2")


canvas = tk.Canvas(root, width=450, height=450, bg="white")
canvas.pack(side="left", padx=15)

def draw_jugs():
    canvas.delete("all")
    cap1 = int(cap1_e.get())
    cap2 = int(cap2_e.get())

    s1 = 250 / cap1
    s2 = 250 / cap2

 
    for x in [90, 290]:
        canvas.create_oval(x, 40, x+120, 90)
        canvas.create_rectangle(x, 65, x+120, 330)
        canvas.create_oval(x, 300, x+120, 360)

 
    canvas.create_rectangle(100, 330 - jug1*s1, 200, 330, fill="#4fc3f7")
    canvas.create_rectangle(300, 330 - jug2*s2, 400, 330, fill="#4fc3f7")

    canvas.create_text(150, 390, text=f"Jug1: {jug1}L")
    canvas.create_text(350, 390, text=f"Jug2: {jug2}L")


instruction = tk.StringVar()
instruction.set("Click START to begin the simulation.")

tk.Label(
    root, textvariable=instruction,
    bg="#263238", fg="white",
    font=("Arial", 11),
    wraplength=800, justify="left",
    padx=10, pady=8
).pack(fill="x", padx=10, pady=5)


ctrl = tk.Frame(root)
ctrl.pack(pady=5)

def start():
    global solution, step, jug1, jug2, running, prev_state
    jug1 = jug2 = step = 0
    prev_state = (0, 0)
    solution.clear()
    running = True
    solution.extend(bfs(int(cap1_e.get()), int(cap2_e.get()), int(target_e.get())))
    instruction.set("Simulation started. Click NEXT to apply production rules.")
    draw_jugs()
    highlight_rule(11)

def next_step():
    global step, jug1, jug2, prev_state
    if not running or step >= len(solution):
        return

    prev_state = (jug1, jug2)
    jug1, jug2 = solution[step]
    rule = detect_rule(prev_state, (jug1, jug2))
    highlight_rule(rule)

    instruction.set(
        f"Step {step}: State changed from {prev_state} → ({jug1}, {jug2})\n"
        f"Production Rule R{rule} fired."
    )

    draw_jugs()
    step += 1

    if jug1 == int(target_e.get()) or jug2 == int(target_e.get()):
        highlight_rule(9 if jug1 == int(target_e.get()) else 10)
        messagebox.showinfo("GOAL STATE", "🎯 Target Achieved!")

def stop():
    global running
    running = False
    instruction.set("Simulation stopped.")

def reset():
    global jug1, jug2, step, running
    jug1 = jug2 = step = 0
    running = False
    instruction.set("Simulation reset. Click START.")
    draw_jugs()
    highlight_rule(None)

tk.Button(ctrl, text="START", width=12, bg="#4caf50", fg="white", command=start).grid(row=0, column=0, padx=5)
tk.Button(ctrl, text="NEXT", width=12, bg="#2196f3", fg="white", command=next_step).grid(row=0, column=1, padx=5)
tk.Button(ctrl, text="STOP", width=12, bg="#f44336", fg="white", command=stop).grid(row=0, column=2, padx=5)
tk.Button(ctrl, text="RESET", width=12, bg="#9e9e9e", command=reset).grid(row=0, column=3, padx=5)

def detect_rule(prev, curr):
    a, b = prev
    c, d = curr
    cap1 = int(cap1_e.get())
    cap2 = int(cap2_e.get())

    if prev == (0, 0):
        return 11
    if c == cap1 and b == d:
        return 1
    if d == cap2 and a == c:
        return 2
    if c == 0 and b == d:
        return 3
    if d == 0 and a == c:
        return 4
    if a > 0 and d > b:
        return 5
    if b > 0 and c > a:
        return 6
    return 12


rules = [
    "R1  Fill Jug1 completely",
    "R2  Fill Jug2 completely",
    "R3  Empty Jug1",
    "R4  Empty Jug2",
    "R5  Pour Jug1 → Jug2",
    "R6  Pour Jug2 → Jug1",
    "R7  Pour Jug1 → Jug2 until Jug2 full",
    "R8  Pour Jug2 → Jug1 until Jug1 full",
    "R9  Jug1 == Target → Goal",
    "R10 Jug2 == Target → Goal",
    "R11 Initial State (0,0)",
    "R12 Avoid repeated states"
]

rule_labels = []
rule_frame = tk.Frame(root, bg="#263238")
rule_frame.pack(side="right", fill="y", padx=10)

tk.Label(
    rule_frame, text="PRODUCTION RULES",
    bg="#263238", fg="white",
    font=("Arial", 14, "bold")
).pack(pady=10)

for r in rules:
    lbl = tk.Label(
        rule_frame, text=r,
        bg="#37474f", fg="white",
        font=("Courier", 11),
        anchor="w", width=38, padx=5
    )
    lbl.pack(pady=2)
    rule_labels.append(lbl)

def highlight_rule(n):
    for i, lbl in enumerate(rule_labels):
        if n and i == n-1:
            lbl.config(bg="#ffeb3b", fg="black")
        else:
            lbl.config(bg="#37474f", fg="white")

draw_jugs()
root.mainloop()
