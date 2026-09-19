#!/usr/bin/env python3
"""Generate time-vs-n and depth-vs-n plots from results/results.csv."""

from __future__ import annotations

import csv
from collections import defaultdict
from pathlib import Path

try:
    import matplotlib.pyplot as plt
except ImportError as exc:
    raise SystemExit("Install matplotlib: pip install matplotlib") from exc

ROOT = Path(__file__).resolve().parents[1]
CSV_PATH = ROOT / "results" / "results.csv"
OUT_DIR = ROOT / "docs" / "plots"


def load_rows():
    rows = []
    with CSV_PATH.open(newline="", encoding="utf-8") as f:
        for row in csv.DictReader(f):
            rows.append(
                {
                    "algorithm": row["algorithm"],
                    "input_type": row["input_type"],
                    "n": int(row["n"]),
                    "time_ms": float(row["time_ms"]),
                    "max_depth": int(row["max_depth"]),
                }
            )
    return rows


def series(rows, algorithm: str, input_type: str):
    pts = [(r["n"], r["time_ms"], r["max_depth"]) for r in rows
           if r["algorithm"] == algorithm and r["input_type"] == input_type]
    pts.sort()
    return pts


def main() -> None:
    if not CSV_PATH.exists():
        raise SystemExit(f"Missing {CSV_PATH}. Run: mvn -q exec:java -Dexec.args=bench")

    OUT_DIR.mkdir(parents=True, exist_ok=True)
    rows = load_rows()

    # Time vs n
    fig, ax = plt.subplots(figsize=(9, 5.5))
    specs = [
        ("MergeSort", "random", "MergeSort (random)"),
        ("QuickSort", "random", "QuickSort (random)"),
        ("DeterministicSelect", "random_median", "Select (median)"),
        ("ClosestPair", "uniform_random", "Closest Pair"),
    ]
    for algo, itype, label in specs:
        pts = series(rows, algo, itype)
        if not pts:
            continue
        xs = [p[0] for p in pts]
        ys = [p[1] for p in pts]
        ax.plot(xs, ys, marker="o", label=label)
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_xlabel("n (input size)")
    ax.set_ylabel("time (ms)")
    ax.set_title("Execution time vs n")
    ax.grid(True, which="both", ls="--", alpha=0.4)
    ax.legend()
    fig.tight_layout()
    fig.savefig(OUT_DIR / "time_vs_n.png", dpi=160)
    plt.close(fig)

    # Recursion depth vs n
    fig, ax = plt.subplots(figsize=(9, 5.5))
    for algo, itype, label in specs:
        pts = series(rows, algo, itype)
        if not pts:
            continue
        xs = [p[0] for p in pts]
        ys = [p[2] for p in pts]
        ax.plot(xs, ys, marker="o", label=label)
    ax.set_xscale("log")
    ax.set_xlabel("n (input size)")
    ax.set_ylabel("max recursion depth")
    ax.set_title("Recursion depth vs n")
    ax.grid(True, which="both", ls="--", alpha=0.4)
    ax.legend()
    fig.tight_layout()
    fig.savefig(OUT_DIR / "depth_vs_n.png", dpi=160)
    plt.close(fig)

    # QuickSort input-type sensitivity
    fig, ax = plt.subplots(figsize=(9, 5.5))
    for itype in ("random", "sorted", "reverse", "duplicates"):
        pts = series(rows, "QuickSort", itype)
        if not pts:
            continue
        ax.plot([p[0] for p in pts], [p[1] for p in pts], marker="o", label=itype)
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_xlabel("n")
    ax.set_ylabel("time (ms)")
    ax.set_title("QuickSort: input structure vs time")
    ax.grid(True, which="both", ls="--", alpha=0.4)
    ax.legend()
    fig.tight_layout()
    fig.savefig(OUT_DIR / "quicksort_input_types.png", dpi=160)
    plt.close(fig)

    print(f"Wrote plots to {OUT_DIR}")


if __name__ == "__main__":
    main()
