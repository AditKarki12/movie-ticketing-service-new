# Movie Ticketing Service - Scripts

This directory contains utility scripts for the Movie Ticketing Service, primarily focused on data ingestion and Elasticsearch management.

## Project Structure

- `ingest.py`: Main script for ingesting movie data from CSV into Elasticsearch.
- `mappings/`: Contains Elasticsearch index mapping definitions.
  - `movie-mappings.json`: Mapping for the `movies` index.
- `requirements.txt`: Python dependencies.
- `.venv/`: Virtual environment for running scripts.

## Ingestion Workflow

### Setup
1. Create virtual environment: `python -m venv .venv`
2. Activate venv: `.venv\Scripts\activate`
3. Install dependencies: `pip install -r requirements.txt`

### Execution
Run the ingestion script:
```bash
python ingest.py
```

The script expects:
- Elasticsearch at `http://localhost:9200`.
- Data at `data/movies.csv`.
- Mappings in `mappings/movie-mappings.json`.

## Conventions

- **Naming**: Scripts should be named descriptively.
- **Mappings**: Keep Elasticsearch mappings in the `mappings/` directory.
