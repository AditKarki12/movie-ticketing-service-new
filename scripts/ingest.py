import csv
import json

from elasticsearch import Elasticsearch

es = Elasticsearch("http://localhost:9200")

INDEX_NAME = "movies"

def ingest_movies():
# Check if index exists, if not create it
    if not es.indices.exists(index=INDEX_NAME):
        print(f"Index '{INDEX_NAME}' does not exist. Creating...")
        with open("mapping.json", 'r') as f:
            mapping = json.load(f)
        es.indices.create(index=INDEX_NAME, body=mapping)

# Ingestion Logic
    with open("data/movies.csv", 'r') as file:
        reader = csv.DictReader(file)
        for row in reader:
            doc = {
                "id": row["id"],
                "title": row["title"],
                "genres": row.get("genres", ""),
                "language": row["language"],
                "duration": int(row["duration"]),
                "description": row["description"],
                "director": row["director"],
                "rating": float(row["rating"]),
                "releaseDate": row.get("releaseDate"),
                "isActive": row.get("isActive", "True").lower() == "true",
                "tags": row.get("tags", "")
            }
            es.index(index=INDEX_NAME, id=doc["id"], document=doc)

if __name__ == "__main__":    
    ingest_movies()