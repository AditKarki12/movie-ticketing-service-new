import csv
import json
from elasticsearch import Elasticsearch

es = Elasticsearch("http://localhost:9200")


def ensure_index(index_name, mapping_path):
    if es.indices.exists(index=index_name):
        return True
    print(f"Index '{index_name}' does not exist. Creating...")
    try:
        with open(mapping_path, "r") as f:
            mapping = json.load(f)
        es.indices.create(index=index_name, body=mapping)
        return True
    except FileNotFoundError:
        print(f"Error: Mapping file not found at {mapping_path}")
        return False
    except Exception as e:
        print(f"Error creating index: {e}")
        return False


def ingest_movies():
    index_name = "movies"
    if not ensure_index(index_name, "mappings/movie-mappings.json"):
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
                es.index(index=index_name, id=doc["id"], document=doc)
        print("Movies ingested successfully.")
    except FileNotFoundError:
        print(f"Error: Data file not found at {data_path}")
    except Exception as e:
        print(f"An error occurred during ingestion: {e}")


def ingest_theaters():
    index_name = "theaters"
    if not ensure_index(index_name, "mappings/theater-mappings.json"):
        return

    data_path = "data/theaters.csv"
    try:
        with open(data_path, "r", encoding="utf-8") as file:
            reader = csv.DictReader(file)
            for row in reader:
                doc = {
                    "theaterId": row.get("theaterId"),
                    "theaterName": row.get("theaterName"),
                    "theaterAddress": row.get("theaterAddress"),
                    "theaterCity": row.get("theaterCity"),
                    "theaterState": row.get("theaterState"),
                    "screensTotal": int(row["screensTotal"]) if row.get("screensTotal") else None
                }
                es.index(index=index_name, id=doc["theaterId"], document=doc)
        print("Theaters ingested successfully.")
    except FileNotFoundError:
        print(f"Error: Data file not found at {data_path}")
    except Exception as e:
        print(f"An error occurred during ingestion: {e}")


def ingest_users():
    index_name = "users"
    if not ensure_index(index_name, "mappings/user-mappings.json"):
        return

    data_path = "data/users.csv"
    try:
        with open(data_path, "r", encoding="utf-8") as file:
            reader = csv.DictReader(file)
            for row in reader:
                doc = {
                    "userId": row.get("userId"),
                    "userFirstName": row.get("userFirstName"),
                    "userLastName": row.get("userLastName"),
                    "userEmail": row.get("userEmail"),
                    "role": row.get("role")
                }
                es.index(index=index_name, id=doc["userId"], document=doc)
        print("Users ingested successfully.")
    except FileNotFoundError:
        print(f"Error: Data file not found at {data_path}")
    except Exception as e:
        print(f"An error occurred during ingestion: {e}")


def ingest_showtimes():
    index_name = "showtimes"
    if not ensure_index(index_name, "mappings/showtime-mappings.json"):
        return

    data_path = "data/showtimes.csv"
    try:
        with open(data_path, "r", encoding="utf-8") as file:
            reader = csv.DictReader(file)
            for row in reader:
                doc = {
                    "showtimeId": row.get("showtimeId"),
                    "movieTitle": row.get("movieTitle"),
                    "theaterName": row.get("theaterName"),
                    "localDate": row.get("localDate") or None,
                    "localTime": row.get("localTime") or None,
                    "totalSeats": int(row["totalSeats"]) if row.get("totalSeats") else None,
                    "availableSeats": int(row["availableSeats"]) if row.get("availableSeats") else None,
                    "ticketPrice": float(row["ticketPrice"]) if row.get("ticketPrice") else None
                }
                es.index(index=index_name, id=doc["showtimeId"], document=doc)
        print("Showtimes ingested successfully.")
    except FileNotFoundError:
        print(f"Error: Data file not found at {data_path}")
    except Exception as e:
        print(f"An error occurred during ingestion: {e}")


def ingest_bookings():
    index_name = "bookings"
    if not ensure_index(index_name, "mappings/booking-mappings.json"):
        return

    data_path = "data/bookings.csv"
    try:
        with open(data_path, "r", encoding="utf-8") as file:
            reader = csv.DictReader(file)
            for row in reader:
                doc = {
                    "bookingId": row.get("bookingId"),
                    "userId": int(row["userId"]) if row.get("userId") else None,
                    "userEmail": row.get("userEmail"),
                    "showtimeId": int(row["showtimeId"]) if row.get("showtimeId") else None,
                    "movieTitle": row.get("movieTitle"),
                    "theaterName": row.get("theaterName"),
                    "seatCount": int(row["seatCount"]) if row.get("seatCount") else None,
                    "totalPrice": float(row["totalPrice"]) if row.get("totalPrice") else None,
                    "bookingTime": row.get("bookingTime") or None,
                    "status": row.get("status")
                }
                es.index(index=index_name, id=doc["bookingId"], document=doc)
        print("Bookings ingested successfully.")
    except FileNotFoundError:
        print(f"Error: Data file not found at {data_path}")
    except Exception as e:
        print(f"An error occurred during ingestion: {e}")


if __name__ == "__main__":
    ingest_theaters()
    ingest_users()
    ingest_movies()
    ingest_showtimes()
    ingest_bookings()
