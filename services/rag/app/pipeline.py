from pathlib import Path
from typing import Any

import pymupdf
import re

def extract_pdf_content(file_path:str | Path)->dict[str,Any]:
    
    path = Path(file_path)

    if not path.exists() or not path.is_file():
        raise FileNotFoundError(f"PDF file not found: {path}")

    if path.suffix.lower() != ".pdf":
        raise ValueError("Only PDF files are supported.")

    pages: list[dict[str, Any]] = []
    all_text: list[str] = []

    with pymupdf.open(path) as document:
        if document.is_encrypted:
            raise ValueError("Password-protected PDFs are not supported yet.")

        for page_number, page in enumerate(document, start=1):
            text = page.get_text("text") or ""

            pages.append(
                {
                    "page": page_number,
                    "text": text,
                }
            )
            all_text.append(text)

        
        combined_text = "\n\n".join(all_text)
        cleaned_text = clean_text(combined_text)

        chunks = chunk_text(
            pages,
            chunk_size=800,
            chunk_overlap=150,
        )

        return {
            "filename": path.name,
            "page_count": len(pages),
            "character_count": len(combined_text),
            "text": combined_text,
            "cleaned_text": cleaned_text,
            "pages": pages,
            "chunk_count": len(chunks),
            "chunks": chunks,
        }

def clean_text(text:str)->str:
    text = text.replace("\x00", "")
    text = text.replace("\r\n", "\n").replace("\r", "\n")
    text = re.sub(r"[ \t]+", " ", text)
    text = re.sub(r"\n[ \t]*\n(?:[ \t]*\n)+", "\n\n", text)

    return text.strip()


def chunk_text(
    pages: list[dict[str, Any]],
    chunk_size: int = 800,
    chunk_overlap: int = 150,
) -> list[dict[str, Any]]:
    if chunk_size <= 0:
        raise ValueError("Chunk size must be greater than zero.")

    if chunk_overlap < 0 or chunk_overlap >= chunk_size:
        raise ValueError(
            "Chunk overlap must be non-negative and smaller than chunk size."
        )

    chunks: list[dict[str, Any]] = []
    chunk_id = 1
    step = chunk_size - chunk_overlap

    for page_data in pages:
        page_number = page_data["page"]
        text = clean_text(page_data["text"])

        if not text:
            continue

        start = 0

        while start < len(text):
            end = min(start + chunk_size, len(text))

            # Prefer a nearby word boundary.
            if end < len(text):
                boundary = text.rfind(" ", start, end)

                if boundary > start + chunk_size // 2:
                    end = boundary

            chunk = text[start:end].strip()

            if chunk:
                chunks.append({
                    "chunk_id": chunk_id,
                    "page": page_number,
                    "text": chunk,
                    "start_char": start,
                    "end_char": end,
                })
                chunk_id += 1

            if end >= len(text):
                break

            start += step

    return chunks
