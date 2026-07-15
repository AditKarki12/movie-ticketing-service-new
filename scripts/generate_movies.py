from elasticsearch import Elasticsearch, helpers
from faker import Faker
import random
from datetime import date, timedelta

fake = Faker()

es = Elasticsearch("http://localhost:9200")

GENRES = ["Action", "Comedy", "Drama", "Horror", "Sci-Fi", "Romance", "Thriller"]
LANGUAGES = ["English", "French", "Spanish", "Hindi", "Korean", "Japanese"]
DIRECTORS = ["Christopher Nolan", "Steven Spielberg", "Bong Joon-ho", "Martin Scorsese", "Greta Gerwig"]

START_DATE = date(2000, 1, 1)
DATE_RANGE_DAYS = (date.today() - START_DATE).days

MAPPING = {
    "mappings": {
        "properties": {
            "id":          {"type": "keyword"},
            "title":       {"type": "text"},
            "genres":      {"type": "keyword"},
            "language":    {"type": "keyword"},
            "duration":    {"type": "integer"},
            "description": {"type": "text"},
            "director":    {"type": "keyword"},
            "rating":      {"type": "double"},
            "releaseDate": {"type": "date", "format": "yyyy-MM-dd"},
            "isActive":    {"type": "boolean"},
            "tags":        {"type": "keyword"}
        }
    }
}

if es.indices.exists(index="movies"):
    es.indices.delete(index="movies")
    print("Deleted existing movies index")

es.indices.create(index="movies", body=MAPPING)
print("Created movies index with explicit mapping")

def random_release_date():
    return (START_DATE + timedelta(days=random.randint(0, DATE_RANGE_DAYS))).strftime("%Y-%m-%d")

def generate_movie(i):
    return {
        "_index": "movies",
        "_id": i,
        "_source": {
            "id": str(i),
            "title": fake.sentence(nb_words=3).replace(".", ""),
            "genres": random.sample(GENRES, random.randint(1, 3)),
            "language": [random.choice(LANGUAGES)],
            "duration": random.randint(80, 180),
            "description": fake.paragraph(),
            "director": random.choice(DIRECTORS),
            "rating": round(random.uniform(1.0, 10.0), 1),
            "releaseDate": random_release_date(),
            "isActive": random.choice([True, False]),
            "tags": random.sample(["dream", "space", "family", "crime", "love", "war", "future"], 2)
        }
    }

actions = (generate_movie(i) for i in range(1, 50001))

helpers.bulk(es, actions)

print("Inserted 50,000 movies")