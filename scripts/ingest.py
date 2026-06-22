import csv
import json
from elasticsearch import Elasticsearch

es = Elasticsearch("http://localhost:9200")

INDEX_NAME = "movies"

def ingest_movies():
    if not es.indices.exists(index=INDEX_NAME):
        print(f"Index '{INDEX_NAME}' does not exist. Creating...")
        mapping_path = "mappings/movie-mappings.json"
        try:
            with open(mapping_path, "r") as f:
                mapping = json.load(f)
            es.indices.create(index=INDEX_NAME, body=mapping)
        except FileNotFoundError:
            print(f"Error: Mapping file not found at {mapping_path}")
            return
        except Exception as e:
            print(f"Error creating index: {e}")
            return

    data_path = "data/movies.csv"
    try:
        with open(data_path, "r", encoding="utf-8") as file:
            reader = csv.DictReader(file)
            for row in reader:
                doc = {
                    "id": row.get("id"),
                    "title": row.get("title"),
                    "genres": [g.strip() for g in row.get("genres", "").split(",") if g.strip()],
                    "language": [l.strip() for l in row.get("language", "").split(",") if l.strip()],
                    "duration": int(row["duration"]) if row.get("duration") else None,
                    "description": row.get("description"),
                    "director": row.get("director"),
                    "rating": float(row["rating"]) if row.get("rating") else None,
                    "releaseDate": row.get("releaseDate") or None,
                    "isActive": row.get("isActive", "true").lower() == "true",
                    "tags": [t.strip() for t in row.get("tags", "").split(",") if t.strip()]
                }
                es.index(index=INDEX_NAME, id=doc["id"], document=doc)
        print("Movies ingested successfully.")
    except FileNotFoundError:
        print(f"Error: Data file not found at {data_path}")
    except Exception as e:
        print(f"An error occurred during ingestion: {e}")

if __name__ == "__main__":
    ingest_movies()
