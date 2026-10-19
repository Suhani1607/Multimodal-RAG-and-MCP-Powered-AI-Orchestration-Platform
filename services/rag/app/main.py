from datetime import datetime, timezone
from pathlib import Path
from tempfile import NamedTemporaryFile

from fastapi import FastAPI, File, HTTPException, UploadFile

from app.pipeline import extract_pdf_content


app = FastAPI(
    title="Multimodal RAG Service",
    version="0.1.0",
    description="Document ingestion and retrieval service for the RAG + MCP project.",
)

MAX_FILE_SIZE = 25 * 1024 * 1024  # 25 MB


@app.get("/health")
def health() -> dict[str, str]:
    return {
        "status": "ok",
        "service": "rag",
        "timestamp": datetime.now(timezone.utc).isoformat(),
    }


@app.get("/api/v1/capabilities")
def capabilities() -> dict[str, object]:
    return {
        "service": "rag",
        "implemented": [
            "health",
            "pdf_text_extraction",
            "embedded_image_extraction",
        ],
        "planned": [
            "structured_table_extraction",
            "ocr",
            "image_understanding",
            "text_cleaning",
            "chunking",
            "embedding_and_storage",
            "hybrid_retrieval",
            "source_evidence",
        ],
    }


@app.post("/api/v1/extract")
async def extract_pdf(
    file: UploadFile = File(...),
) -> dict[str, object]:
    if not file.filename or Path(file.filename).suffix.lower() != ".pdf":
        raise HTTPException(
            status_code=400,
            detail="Only PDF files are allowed.",
        )

    temp_path = None

    try:
        content = await file.read(MAX_FILE_SIZE + 1)

        if not content:
            raise HTTPException(
                status_code=400,
                detail="The uploaded file is empty.",
            )

        if len(content) > MAX_FILE_SIZE:
            raise HTTPException(
                status_code=413,
                detail="PDF size must not exceed 25 MB.",
            )

        if not content.startswith(b"%PDF-"):
            raise HTTPException(
                status_code=400,
                detail="The uploaded file does not have a valid PDF header.",
            )

        # Save the upload temporarily for the processing pipeline.
        with NamedTemporaryFile(
            delete=False,
            suffix=".pdf",
        ) as temp_file:
            temp_file.write(content)
            temp_path = Path(temp_file.name)

        # Delegate all PDF processing to pipeline.py.
        result = extract_pdf_content(temp_path)

        # Return the original upload name in the API response.
        result["filename"] = Path(file.filename).name

        return result

    except HTTPException:
        raise

    except ValueError as exc:
        raise HTTPException(
            status_code=400,
            detail=str(exc),
        ) from exc

    except Exception as exc:
        raise HTTPException(
            status_code=500,
            detail="An unexpected error occurred while processing the PDF.",
        ) from exc

    finally:
        if temp_path is not None:
            temp_path.unlink(missing_ok=True)

        await file.close()