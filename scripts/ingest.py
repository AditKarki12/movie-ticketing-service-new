import csv

from elasticsearch import Elasticsearch

es = Elasticsearch("http://localhost:9200")


def ingest_movies():
    with open("data/movies.csv", 'r') as file:
        reader = csv.DictReader(file)
        for row in reader:
            doc = {
                "id": row["id"],
                "title": row["title"],
                "genre": row["genre"],
                "language": row["language"],
                "duration": int(row["duration"]),
                "description": row["description"],
                "director": row["director"],
                "rating": float(row["rating"])
            }

            es.index(index="movies", id=doc["id"], document=doc)